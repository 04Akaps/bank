package org.example.common.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.AlgorithmMismatchException
import com.auth0.jwt.exceptions.InvalidClaimException
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import jakarta.servlet.http.HttpServletRequest
import org.example.common.exception.CustomException
import org.example.common.exception.ErrorCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtProvider {

    @Value("\${jwt.secret-key}")
    lateinit var secretKey: String

    @Value("\${jwt.token-time-for-minute}")
    var tokenTimeForMinute: Long = 0


    fun createToken(userId: String): String {
        return JWT.create()
            .withSubject(userId)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + tokenTimeForMinute * ONE_MINUTE_TO_MILLIS))
            .sign(Algorithm.HMAC256(secretKey))
    }

    fun createToken(email: String, name : String, id : String) : String {
        return JWT.create()
            .withSubject("$email - $name - $id")
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + tokenTimeForMinute * ONE_MINUTE_TO_MILLIS))
            .sign(Algorithm.HMAC256(secretKey))
    }

    fun decodeAccessTokenAfterVerifying(token: String): DecodedJWT {
        return decodeTokenAfterVerifying(token, secretKey)
    }

    fun getToken(request: HttpServletRequest): String {
        val authorization = request.getHeader("Authorization")
        return authorization.removePrefix("Bearer ")
    }

    private fun decodeTokenAfterVerifying(token: String, key: String): DecodedJWT {
        try {
            return JWT.require(Algorithm.HMAC256(key)).build().verify(token)
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

    private fun decodedJWT(token: String): DecodedJWT {
        return JWT.decode(token)
    }


    companion object {
        const val ONE_MINUTE_TO_MILLIS: Long = 60 * 1000
        const val ONE_HOUR_TO_MILLIS: Long = 60 * 60 * 1000
    }

}