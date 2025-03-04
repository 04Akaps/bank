package org.example.domains.auth.service

import com.github.f4b6a3.ulid.UlidCreator
import org.example.common.exception.CustomException
import org.example.common.exception.ErrorCode
import org.example.common.jwt.JwtProvider
import org.example.common.logger.Logging
import org.example.common.transaction.TxAdvice
import org.example.domains.auth.repository.AuthUserRepository
import org.example.domains.auth.repository.model.User
import org.example.interfaces.OAuthService
import org.springframework.stereotype.Service
import org.slf4j.Logger

import org.springframework.transaction.support.TransactionSynchronizationManager

@Service
class AuthService(
    private val jwtProvider: JwtProvider,
    private val oAuth2Services: Map<String, OAuthService>,
    private val authUserRepository: AuthUserRepository,
    private val txAdvice: TxAdvice,
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

        val username = (userInfo.name ?: userInfo.email).toString()

        txAdvice.run {
            val exist = authUserRepository.existsByUsernameAndPlatform(username, provider)

            if (exist) {
                authUserRepository.updateAccessTokenByUsernameAndPlatform(username, provider, token)
            } else {
                val ulid = UlidCreator.getUlid().toString()

                val user = User(
                    ulid = ulid,
                    platform = provider,
                    username = username,
                    accessToken = token,
                )

                authUserRepository.save<User>(user)
            }
        }

        return@loggingStopWatch token
    }

    @Throws(CustomException::class)
    fun verifyToken(authorization : String)  {
        jwtProvider.decodeAccessTokenAfterVerifying(authorization.removePrefix("Bearer "))
    }


    companion object {
        private val logger: Logger = Logging.getLogger(AuthService::class.java)
    }
}