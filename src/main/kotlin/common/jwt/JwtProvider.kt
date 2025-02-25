package org.example.common.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.*
import com.auth0.jwt.interfaces.DecodedJWT
import jakarta.servlet.http.HttpServletRequest
import org.example.common.exception.CustomException
import org.example.common.exception.ErrorCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtProvider {

    @Value("\${jwt.secret-key}")
    lateinit var secretKey: String

    @Value("\${jwt.refresh-secret-key}")
    lateinit var refreshSecretKey: String

    @Value("\${jwt.token-time-for-minute}")
    var tokenTimeForMinute: Long = 0

    @Value("\${jwt.refresh-token-time-for-hour}")
    var refreshTokenTimeForHour: Long = 0


    companion object {
        const val ONE_MINUTE_TO_MILLIS: Long = 60 * 1000
        const val ONE_HOUR_TO_MILLIS: Long = 60 * 60 * 1000

        @JvmStatic
        fun createToken(userId: String, secretKey: String, tokenTimeForMinute: Long): String {
            return JWT.create()
                .withSubject(userId)
                .withIssuedAt(Date())
                .withExpiresAt(Date(System.currentTimeMillis() + tokenTimeForMinute * ONE_MINUTE_TO_MILLIS))
                .sign(Algorithm.HMAC256(secretKey))
        }

        @JvmStatic
        fun createRefreshToken(userId: String, refreshSecretKey: String, refreshTokenTimeForHour: Long): String {
            return JWT.create()
                .withSubject(userId)
                .withIssuedAt(Date())
                .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenTimeForHour * ONE_HOUR_TO_MILLIS))
                .sign(Algorithm.HMAC256(refreshSecretKey))
        }

        @JvmStatic
        fun checkAccessTokenForRefresh(token: String, secretKey: String): DecodedJWT {
            try {
                val decodedJWT = JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token)
                throw CustomException(ErrorCode.ACCESS_TOKEN_IS_NOT_EXPIRED)
            } catch (e: AlgorithmMismatchException) {
                throw CustomException(ErrorCode.TOKEN_IS_INVALID)
            } catch (e: SignatureVerificationException) {
                throw CustomException(ErrorCode.TOKEN_IS_INVALID)
            } catch (e: InvalidClaimException) {
                throw CustomException(ErrorCode.TOKEN_IS_INVALID)
            } catch (e: TokenExpiredException) {
                return JWT.decode(token)
            }
        }

        @JvmStatic
        fun decodeAccessTokenAfterVerifying(token: String, secretKey: String): DecodedJWT {
            return decodeTokenAfterVerifying(token, secretKey)
        }

        @JvmStatic
        fun decodeRefreshTokenAfterVerifying(token: String, refreshSecretKey: String): DecodedJWT {
            return decodeTokenAfterVerifying(token, refreshSecretKey)
        }

        @JvmStatic
        fun decodeTokenAfterVerifying(token: String, secretKey: String): DecodedJWT {
            try {
                return JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token)
            } catch (e: AlgorithmMismatchException) {
                throw CustomException(ErrorCode.TOKEN_IS_INVALID)
            } catch (e: SignatureVerificationException) {
                throw CustomException(ErrorCode.TOKEN_IS_INVALID)
            } catch (e: InvalidClaimException) {
                throw CustomException(ErrorCode.TOKEN_IS_INVALID)
            } catch (e: TokenExpiredException) {
                throw CustomException(ErrorCode.TOKEN_IS_EXPIRED)
            }
        }

        @JvmStatic
        fun decodedJWT(token: String): DecodedJWT {
            return JWT.decode(token)
        }

        @JvmStatic
        fun getToken(request: HttpServletRequest): String {
            val authorization = request.getHeader("Authorization")
            return authorization.removePrefix("Bearer ")
        }
    }

}

