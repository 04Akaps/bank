package org.example.domains.auth.service

import org.example.common.exception.CustomException
import org.example.common.exception.ErrorCode
import org.example.common.jwt.JwtProvider
import org.example.common.logger.Logging
import org.springframework.stereotype.Service
import org.slf4j.Logger

@Service
class AuthService(
    private val jwtProvider: JwtProvider,
    private val googleAuthService: GoogleAuthService
) {

    fun handleAuth(state : String, code : String) : String = Logging.loggingStopWatch(logger) { log ->
        log["state"] = state
        log["code"] = code

        var token  = ""

        when (state.lowercase()) {
            "google" -> {
                val googleToken = googleAuthService.getGoogleToken(code)
                val userInfo = googleAuthService.getGoogleUserInfo(googleToken.accessToken)

                token = jwtProvider.createToken(userInfo.email, userInfo.name, userInfo.id)
            } else ->  {
                throw CustomException(ErrorCode.AUTH_STATE_NOT_SUPPORTED, "$state, $code")
            }
        }


        return@loggingStopWatch token
    }


    companion object {
        private val logger: Logger = Logging.getLogger(AuthService::class.java)
    }
}