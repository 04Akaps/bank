package org.example.domains.auth.service

import com.fasterxml.jackson.annotation.JsonProperty
import org.example.config.GoogleOAuthConfig
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class GoogleAuthService(
    private val googleOAuth: GoogleOAuthConfig
) {

    fun getGoogleToken(code: String): GoogleTokenResponse {
        val restTemplate = RestTemplate()
        val request = LinkedMultiValueMap<String, String>().apply {
            add("code", code)
            add("client_id", googleOAuth.clientID)
            add("client_secret", googleOAuth.secret)
            add("redirect_uri", googleOAuth.redirectUri)
            add("grant_type", "authorization_code")
        }
        val response = restTemplate.postForEntity(
            "https://oauth2.googleapis.com/token",
            HttpEntity(request, HttpHeaders().apply { contentType = MediaType.APPLICATION_FORM_URLENCODED }),
            GoogleTokenResponse::class.java
        )
        return response.body ?: throw RuntimeException("Failed to get Google token")
    }

    fun getGoogleUserInfo(accessToken: String): GoogleUserResponse {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(accessToken)
        }
        val response = restTemplate.exchange(
            "https://www.googleapis.com/oauth2/v2/userinfo",
            HttpMethod.GET,
            HttpEntity(null, headers),
            GoogleUserResponse::class.java
        )

        return response.body ?: throw RuntimeException("Failed to get Google user info")
    }
}

data class GoogleTokenResponse(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("expires_in") val expiresIn: Int,
    @JsonProperty("refresh_token") val refreshToken: String?,
    @JsonProperty("scope") val scope: String,
    @JsonProperty("token_type") val tokenType: String,
    @JsonProperty("id_token") val idToken: String
)

data class GoogleUserResponse(
    val id: String,
    val email: String,
    val name: String,
    val picture: String
)