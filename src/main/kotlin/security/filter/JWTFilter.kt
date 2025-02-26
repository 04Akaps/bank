package org.example.security.jwt

//import jakarta.servlet.FilterChain
//import jakarta.servlet.http.HttpServletRequest
//import jakarta.servlet.http.HttpServletResponse
//import org.example.common.exception.CustomException
//import org.example.common.exception.ErrorCode
//import org.example.common.jwt.JwtProvider
//import org.springframework.stereotype.Component
//import org.springframework.web.filter.OncePerRequestFilter
//import kotlin.jvm.Throws
////
//@Component
//class JWTFilter(
//    private val jwtProvider: JwtProvider,
//) : OncePerRequestFilter() {
//
//    val PUBLIC_ENDPOINT = mapOf(
//        "/api/v1/auth/login" to true,
//        "/api/v1/auth/callback" to true,
//        "/favicon.ico" to true
//    )
//
//    val JWT_AUTH_ENDPOINT = mapOf(
//        "/api/v1/bank/**" to true,
//        "/api/v1/history/**" to true
//    )
//
//    override fun doFilterInternal(
//        request: HttpServletRequest,
//        response: HttpServletResponse,
//        filterChain: FilterChain
//    ) {
//        val url = request.requestURI
//
//        if (!PUBLIC_ENDPOINT.containsKey(url)) {
//            val authHeader = request.getHeader("Authorization")
//
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                val token = authHeader.substring(7) // Bearer 이후의 값
//                isValidToken(token)
//                filterChain.doFilter(request, response)
//            } else {
//                val info = "$authHeader - ${request.requestURI}"
//
//                throw CustomException(ErrorCode.ACCESS_TOKEN_NEED, info)
//            }
//        }
//    }
//
//    @Throws(CustomException::class)
//    private fun isValidToken(token: String)  {
//        jwtProvider.decodeAccessTokenAfterVerifying(token)
//    }
//}