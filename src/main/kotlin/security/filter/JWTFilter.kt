package org.example.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.common.exception.CustomException
import org.example.common.exception.ErrorCode
import org.example.common.jwt.JwtProvider
import org.example.common.types.ResponseProvider
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import kotlin.jvm.Throws
//
@Component
class JWTFilter(
    private val jwtProvider: JwtProvider,
) : OncePerRequestFilter() {

    private val JWT_AUTH_ENDPOINT = arrayOf(
        "/api/v1/bank/**",
        "/api/v1/history/**"
    )

    private val pathMatcher = AntPathMatcher()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestURI = request.requestURI

        if (shouldPerformAuthentication(requestURI)) {
            val authHeader = request.getHeader("Authorization")

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                val token = authHeader.substring(7) // Bearer 이후의 값

                try {
                    isValidToken(token)
                } catch (e: CustomException) {
                    response.status = HttpServletResponse.SC_UNAUTHORIZED
                    response.contentType = "application/json;charset=UTF-8"

                    val msg = e.getCodeInterface()
                    val errorResponse = ResponseProvider.customError(
                        code = msg.code,
                        message = msg.message,
                        null,
                    )

                    response.writer.write(ObjectMapper().writeValueAsString(errorResponse))
                    response.writer.flush()
                    return
                }
            } else {

                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.contentType = "application/json;charset=UTF-8"

                val errorResponse = ResponseProvider.customError(
                    code = ErrorCode.ACCESS_TOKEN_NEED.code,
                    message = ErrorCode.ACCESS_TOKEN_NEED.message,
                    null,
                )

                response.writer.write(ObjectMapper().writeValueAsString(errorResponse))
                response.writer.flush()
                return
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun shouldPerformAuthentication(requestURI: String): Boolean {
        for (endpoint in JWT_AUTH_ENDPOINT) {
            if (pathMatcher.match(endpoint, requestURI)) {
                return true
            }
        }
        return false
    }

    @Throws(CustomException::class)
    private fun isValidToken(token: String)  {
        jwtProvider.decodeAccessTokenAfterVerifying(token)
    }
}