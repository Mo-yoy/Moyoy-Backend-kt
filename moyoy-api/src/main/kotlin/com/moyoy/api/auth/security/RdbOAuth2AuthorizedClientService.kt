package com.moyoy.api.auth.security

import com.moyoy.infra.jasypt.JasyptStringEncryptor
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.SqlParameterValue
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.OAuth2RefreshToken
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils
import java.sql.ResultSet
import java.sql.Timestamp
import java.sql.Types
import java.util.Optional

/**
 *  리소스 남을때 수정할 코드
 */
@Component
class RdbOAuth2AuthorizedClientService(
    jdbcOperations: JdbcOperations,
    clientRegistrationRepository: ClientRegistrationRepository,
    private val jasyptStringEncryptor: JasyptStringEncryptor
) : JdbcOAuth2AuthorizedClientService(jdbcOperations, clientRegistrationRepository) {
    init {
        // 저장/업데이트 시 매퍼 설정 (암호화)
        setAuthorizedClientParametersMapper { holder ->
            val client = holder.authorizedClient
            val principal = holder.principal

            mutableListOf<SqlParameterValue>().apply {
                add(SqlParameterValue(Types.VARCHAR, client.clientRegistration.registrationId))
                add(SqlParameterValue(Types.VARCHAR, principal.name))
                add(SqlParameterValue(Types.VARCHAR, client.accessToken.tokenType.value))

                // 3. 래퍼 클래스의 encrypt 호출
                val encryptedAt = jasyptStringEncryptor.encrypt(client.accessToken.tokenValue)
                add(SqlParameterValue(Types.VARCHAR, encryptedAt))

                add(SqlParameterValue(Types.TIMESTAMP, Timestamp.from(client.accessToken.issuedAt)))
                add(SqlParameterValue(Types.TIMESTAMP, Timestamp.from(client.accessToken.expiresAt)))

                val scopes =
                    if (CollectionUtils.isEmpty(client.accessToken.scopes)) {
                        null
                    } else {
                        StringUtils.collectionToDelimitedString(client.accessToken.scopes, ",")
                    }
                add(SqlParameterValue(Types.VARCHAR, scopes))

                client.refreshToken?.let { rt ->
                    val encryptedRt = jasyptStringEncryptor.encrypt(rt.tokenValue)
                    add(SqlParameterValue(Types.VARCHAR, encryptedRt))
                    add(SqlParameterValue(Types.TIMESTAMP, rt.issuedAt?.let { Timestamp.from(it) }))
                } ?: run {
                    add(SqlParameterValue(Types.VARCHAR, null))
                    add(SqlParameterValue(Types.TIMESTAMP, null))
                }
            }
        }

        setAuthorizedClientRowMapper(DecryptingRowMapper(clientRegistrationRepository))
    }

    private inner class DecryptingRowMapper(
        private val repo: ClientRegistrationRepository
    ) : RowMapper<OAuth2AuthorizedClient> {
        override fun mapRow(
            rs: ResultSet,
            rowNum: Int
        ): OAuth2AuthorizedClient {
            val regId = rs.getString("client_registration_id")
            val cr =
                repo.findByRegistrationId(regId)
                    ?: throw IllegalStateException("ClientRegistration not found: $regId")

            val tokenType =
                if (OAuth2AccessToken.TokenType.BEARER.value
                        .equals(rs.getString("access_token_type"), ignoreCase = true)
                ) {
                    OAuth2AccessToken.TokenType.BEARER
                } else {
                    null
                }

            // 4. 래퍼 클래스의 decrypt 호출
            val encryptedAt = rs.getString("access_token_value")
            val accessTokenValue = jasyptStringEncryptor.decrypt(encryptedAt)

            val access =
                OAuth2AccessToken(
                    tokenType,
                    accessTokenValue,
                    rs.getTimestamp("access_token_issued_at").toInstant(),
                    rs.getTimestamp("access_token_expires_at").toInstant(),
                    Optional
                        .ofNullable(rs.getString("access_token_scopes"))
                        .map { StringUtils.commaDelimitedListToSet(it) }
                        .orElse(emptySet())
                )

            val refreshToken =
                rs.getString("refresh_token_value")?.let { encryptedRt ->
                    val rtValue = jasyptStringEncryptor.decrypt(encryptedRt)
                    OAuth2RefreshToken(rtValue, rs.getTimestamp("refresh_token_issued_at")?.toInstant())
                }

            return OAuth2AuthorizedClient(cr, rs.getString("principal_name"), access, refreshToken)
        }
    }
}
