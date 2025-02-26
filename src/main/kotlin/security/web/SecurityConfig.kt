package org.example.security.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig {


    // 인증 불필요 router
    private val PUBLIC_ENDPOINT = arrayOf(
        "/api/v1/auth/login"
    )

    // 인증 필요 router
    private val JWT_AUTH_ENDPOINT = arrayOf(
        "/api/v1/auth/refresh-token",
        "/api/v1/histor/**"
    )

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf().disable()
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(*PUBLIC_ENDPOINT).permitAll()
                    .requestMatchers(*JWT_AUTH_ENDPOINT).authenticated()
                    .anyRequest().permitAll() // 나머지는 일단 허용
            }
            .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter::class.java) // JWT 필터 추가 -> user 기반 인증 객체 앞에서 동작하게 작성

        return http.build()
    }

    @Bean
    fun jwtFilter() : JWTFilter {
        return JWTFilter()
    }
}