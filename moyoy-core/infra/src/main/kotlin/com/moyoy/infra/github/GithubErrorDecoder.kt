package com.moyoy.infra.github

import com.moyoy.infra.github.error.GithubClientErrorException
import com.moyoy.infra.github.error.GithubForbiddenException
import com.moyoy.infra.github.error.GithubResourceNotFoundException
import com.moyoy.infra.github.error.GithubServerErrorException
import com.moyoy.infra.github.error.GithubUnauthorizedException
import com.moyoy.infra.github.error.GithubUnknownErrorException
import com.moyoy.infra.github.error.GithubValidationFailedException
import feign.Response
import feign.codec.ErrorDecoder

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
