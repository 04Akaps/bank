package org.example.domains.auth.service

import org.example.common.exception.CustomException
import org.example.common.exception.ErrorCode
import org.example.common.jwt.JwtProvider
import org.example.common.logger.Logging
import org.example.interfaces.OAuthService
import org.springframework.stereotype.Service
import org.slf4j.Logger

@Service
class AuthService(
    private val jwtProvider: JwtProvider,
    private val oAuth2Services: Map<String, OAuthService>
) {

    fun handleAuth(state : String, code : String) : String = Logging.loggingStopWatch(logger) { log ->
        val provider = state.lowercase()

        log["provider"] = provider
        log["state"] = state
        log["code"] = code

        val oAuthService = oAuth2Services[provider]
            ?: throw CustomException(ErrorCode.AUTH_STATE_NOT_SUPPORTED, "Unsupported provider: $provider")

        val accessToken = oAuthService.getToken(code)
        val userInfo = oAuthService.getUserInfo(accessToken.accessToken)


        val token = jwtProvider.createToken(provider, userInfo.email, userInfo.name, userInfo.id)

        return@loggingStopWatch token
    }

    @Throws(CustomException::class)
    fun verifyToken(token : String)  {
        jwtProvider.decodeAccessTokenAfterVerifying(token)
    }


    companion object {
        private val logger: Logger = Logging.getLogger(AuthService::class.java)
    }
}