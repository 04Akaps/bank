package org.example.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class GlobalConfig {

    @Value("\${global.client-auth-success-url}")
    lateinit var clientURL: String
}