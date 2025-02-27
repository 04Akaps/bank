package org.example.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.common.exception.CustomException
import org.example.common.exception.ErrorCode
import org.example.common.jwt.JwtProvider
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
                isValidToken(token)
            } else {
                val info = "$authHeader - ${request.requestURI}"
                throw CustomException(ErrorCode.ACCESS_TOKEN_NEED, info)
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