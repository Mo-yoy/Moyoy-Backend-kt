package com.moyoy.infra.feign.github

import com.moyoy.infra.feign.github.error.GithubClientErrorException
import com.moyoy.infra.feign.github.error.GithubForbiddenException
import com.moyoy.infra.feign.github.error.GithubResourceNotFoundException
import com.moyoy.infra.feign.github.error.GithubServerErrorException
import com.moyoy.infra.feign.github.error.GithubUnauthorizedException
import com.moyoy.infra.feign.github.error.GithubUnknownErrorException
import com.moyoy.infra.feign.github.error.GithubValidationFailedException
import feign.Response
import feign.codec.ErrorDecoder

/**
 * 깃허브에서 주는 ErrorMessage가 의미 있는 메시지인지 의문이라 status 위주로만 만들까, message 까지 활용해 볼까 아직 고민중
 * 3xx 응답이 필요한 경우는 아직 없었는데 필요할 경우 여기서 제어
 */
class GithubErrorDecoder : ErrorDecoder {
    override fun decode(
        methodKey: String,
        response: Response
    ): Exception {
        val status = response.status()

        return when (status) {
            401 -> GithubUnauthorizedException()
            403 -> GithubForbiddenException()
            404 -> GithubResourceNotFoundException()
            422 -> GithubValidationFailedException()
            in 400..499 -> GithubClientErrorException(status)
            in 500..599 -> GithubServerErrorException(status)
            else -> GithubUnknownErrorException(status)
        }
    }
}
