package com.moyoy.api.user.presentation

import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.moyoy.api.auth.error.AuthErrorCode
import com.moyoy.api.auth.jwt.RefreshTokenCookieFactory
import com.moyoy.api.user.application.ReIssueJwtUseCase
import com.moyoy.api.user.presentation.UserController.Companion.REFRESH_TOKEN_COOKIE_NAME
import com.moyoy.common.error.MoyoyException
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.filter.OncePerRequestFilter

@WebMvcTest(
    excludeFilters = [
        ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [OncePerRequestFilter::class])
    ],
    value = [UserController::class]
)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@ActiveProfiles("test")
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var reIssueJwtUseCase: ReIssueJwtUseCase

    @MockitoBean
    private lateinit var refreshTokenCookieFactory: RefreshTokenCookieFactory

    @DisplayName("쿠키에 유효한 리프레시 토큰을 전달하면 토큰 재발급에 성공한다")
    @Test
    fun can_reissue_jwt_token_with_valid_refresh_token_success() {
        // given
        val rawRefreshToken = "valid-refresh-token"
        val reissuedAccessToken = "reissued-access-token"
        val reissuedRefreshToken = "reissued-refresh-token"
        val reissueJwtInput = ReIssueJwtUseCase.Input(rawRefreshToken)
        val reissueJwtOutput = ReIssueJwtUseCase.Output(reissuedAccessToken, reissuedRefreshToken)

        given(reIssueJwtUseCase.execute(reissueJwtInput))
            .willReturn((reissueJwtOutput))

        given(refreshTokenCookieFactory.createRefreshTokenCookie(reissueJwtOutput.refreshToken))
            .willReturn(ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, reissuedRefreshToken).build())

        // When & Then
        mockMvc
            .perform(
                post("/api/v1/auth/reissue/token")
                    .cookie(Cookie(REFRESH_TOKEN_COOKIE_NAME, rawRefreshToken))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk)
            .andExpect(header().exists(SET_COOKIE))
            .andExpect(jsonPath("$.data.accessToken").value(reissuedAccessToken))
            // Docs
            .andDo(
                document(
                    "jwt-reissue-success",
                    resource(
                        ResourceSnippetParameters
                            .builder()
                            .tag("🔄 JWT 재발급")
                            .summary("JWT 토큰 재발급 API")
                            .description(
                                """
                                사용자의 요청 쿠키(refresh)를 이용하여 액세스 토큰과 리프레시 토큰을 재발급합니다.
                                - Access Token: 응답 Body (data.accessToken)
                                - Refresh Token: 응답 Header (Set-Cookie)
                                """.trimIndent()
                            ).responseFields(
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("code").description("성공/에러 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.accessToken").description("새로 발급된 Access Token")
                            ).build()
                    )
                )
            )
    }

    @DisplayName("토큰 재발급 실패 시 에러 응답을 문서화한다")
    @ParameterizedTest(name = "JWT 재발급 API 에러 케이스: {0}")
    @EnumSource(
        value = AuthErrorCode::class,
        names = ["TOKEN_NOT_EXIST", "TOKEN_TYPE_MISMATCH", "INVALID_TOKEN", "EXPIRED_TOKEN", "BLOCKED_TOKEN"]
    )
    fun tokenReissueErrorTest(errorCode: AuthErrorCode) {
        // given
        val invalidRefreshToken = "invalid-refresh-token"
        val invalidReIssueJwtInput = ReIssueJwtUseCase.Input(invalidRefreshToken)

        given(reIssueJwtUseCase.execute(invalidReIssueJwtInput))
            .willThrow(MoyoyException(errorCode))

        // when & then
        mockMvc
            .perform(
                post("/api/v1/auth/reissue/token")
                    .cookie(Cookie(REFRESH_TOKEN_COOKIE_NAME, invalidRefreshToken))
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().is4xxClientError)
            .andExpect(jsonPath("$.code").value(errorCode.code))
            .andExpect(jsonPath("$.message").value(errorCode.message))
            // Docs
            .andDo(
                document(
                    "JWT 재발급 실패-${errorCode.code}",
                    resource(
                        ResourceSnippetParameters
                            .builder()
                            .tag("🔄 JWT 재발급")
                            .description("JWT 재발급 실패 케이스: ${errorCode.message}")
                            .responseFields(
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("code").description("에러 코드 (${errorCode.code})"),
                                fieldWithPath("message").description("에러 메시지"),
                                subsectionWithPath("data").description("데이터 (null)").optional()
                            ).build()
                    )
                )
            )
    }
}
