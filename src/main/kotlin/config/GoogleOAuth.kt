package org.example.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class GoogleOAuthConfig() {
    @Value("\${oauth2.google.client-id}")
    lateinit var clientID: String

    @Value("\${oauth2.google.client-secret}")
    lateinit var secret: String

    @Value("\${oauth2.google.scope}")
    lateinit var scope: String

    @Value("\${oauth2.google.redirect-uri}")
    lateinit var redirectUri: String
}