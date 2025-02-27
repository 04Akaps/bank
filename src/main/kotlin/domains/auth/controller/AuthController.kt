package org.example.domains.auth.controller

import jakarta.servlet.http.HttpServletResponse
import org.example.config.GlobalConfig
import org.example.domains.auth.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val globalConfig: GlobalConfig
) {

    @GetMapping("/callback")
    fun callBack(
        @RequestParam("code", required = true) code: String,
        @RequestParam("state", required = true) state: String,
        response : HttpServletResponse
    ) : ResponseEntity<Map<String, String>>{
        val token : String = authService.handleAuth(state, code)

        response.addCookie(
            jakarta.servlet.http.Cookie("authToken", token).apply {
                isHttpOnly = true
                path = "/"
                maxAge = 60 * 60 * 24
            }
        )

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI.create(globalConfig.clientURL))
            .build()
    }


    @GetMapping("/verify-token")
    fun verifyToken(
        @RequestParam("token") token: String
    ){

    }
}