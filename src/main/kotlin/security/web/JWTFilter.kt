package org.example.security.web

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.common.exception.CustomException
import org.example.common.exception.ErrorCode
import org.springframework.web.filter.OncePerRequestFilter

class JWTFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7) // Bearer 이후의 값
            if (isValidToken(token)) {
                // 유효성 유무만 체크
                filterChain.doFilter(request, response)
            } else {
                throw CustomException(ErrorCode.TOKEN_IS_INVALID, token)
            }
        } else {
            throw CustomException(ErrorCode.ACCESS_TOKEN_IS_NOT_REQUIRED, authHeader)
        }
    }

    private fun isValidToken(token: String): Boolean {
        // 여기서 JWT가 유효한지 검증
        return token == "valid-token"
    }
}