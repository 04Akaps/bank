package org.example.domains.auth.controller

import org.example.domains.auth.service.GoogleAuthService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val authService: GoogleAuthService) {

    @GetMapping("/callback")
    fun callBack(
        @RequestParam("code") code: String,
    ){
        println("codecodecode" +  code)
        val googleToken = authService.getGoogleToken(code)
        val userInfo = authService.getGoogleUserInfo(googleToken.accessToken)
        println(userInfo)
    }


    @GetMapping("/verify-token")
    fun verifyToken(
        @RequestParam("token") token: String
    ){

    }
}