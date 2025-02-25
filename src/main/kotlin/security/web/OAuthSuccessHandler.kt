package org.example.security.web

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.security.jwt.JwtProvider
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2SuccessHandler(private val jwtProvider: JwtProvider)  : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {

        val user = authentication.principal as OAuth2User
        val email = user.attributes["email"] as String

        val token = jwtProvider.createToken(email, jwtProvider.secretKey, jwtProvider.tokenTimeForMinute)

        // JWT를 쿠키로 저장하거나 헤더에 포함시킬 수 있음
        response.addHeader("Authorization", "Bearer $token")

        // 클라이언트가 JWT를 사용할 수 있도록 리디렉트
        response.sendRedirect("http://localhost:3000/dashboard?token=$token")
    }
}