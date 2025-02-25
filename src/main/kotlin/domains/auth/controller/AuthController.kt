package org.example.domains.auth.controller

import org.example.domains.auth.service.AuthService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val authService: AuthService) {

    @GetMapping("/")
    fun index () = "redirect:/login"

    @GetMapping("/login")
    fun login () = "login"

    @GetMapping("/login/complelte")
    fun loginComplete() = "redirect:/welcome"

    @GetMapping("/welcome")
    @ResponseBody
    fun welcome() = "Hello! Social Login!!"
}