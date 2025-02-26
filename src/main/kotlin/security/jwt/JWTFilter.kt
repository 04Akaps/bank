package org.example.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.common.exception.CustomException
import org.example.common.exception.ErrorCode
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import kotlin.jvm.Throws

@Component
class JWTFilter(
    private val jwtProvider: JwtProvider,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7) // Bearer 이후의 값
            isValidToken(token)
            filterChain.doFilter(request, response)
        } else {
            throw CustomException(ErrorCode.ACCESS_TOKEN_IS_NOT_REQUIRED, authHeader)
        }
    }

    @Throws(CustomException::class)
    private fun isValidToken(token: String)  {
        jwtProvider.decodeAccessTokenAfterVerifying(token)
    }
}