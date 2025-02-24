package org.example.common.jwt

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtProvider {

    @Value("\${jwt.secret-key}")
    lateinit var secretKey: String

    @Value("\${jwt.refresh-secret-key}")
    lateinit var refreshSecretKey: String

    @Value("\${jwt.token-time-for-minute}")
    var tokenTimeForMinute: Long = 0

    @Value("\${jwt.refresh-token-time-for-hour}")
    var refreshTokenTimeForHour: Long = 0
}

