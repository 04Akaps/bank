package org.example.domains.auth.service

import org.example.common.exception.CustomException
import org.example.common.exception.ErrorCode
import org.example.config.OAuth2Config
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import okhttp3.FormBody
import org.example.common.JsonUtil.JsonUtil
import org.example.common.httpClinet.CallClient
import org.example.interfaces.OAuth2TokenResponse
import org.example.interfaces.OAuth2UserResponse
import org.example.interfaces.OAuthService

private const val key = "google"

@Service(key)
class GoogleAuthService(
    private val oAuth2Config: OAuth2Config,
    private val httpClient: CallClient
) : OAuthService{
    private val googleOAuth = oAuth2Config.providers[key] ?: throw CustomException(ErrorCode.GOOGLE_AUTH_CONFIG_NOT_FOUND)
    override val providerName: String = key

    override fun getToken(code : String): GoogleTokenResponse {
        val formBody = FormBody.Builder()
            .add("code", code)
            .add("client_id", googleOAuth.clientId)
            .add("client_secret", googleOAuth.clientSecret)
            .add("redirect_uri", googleOAuth.redirectUri)
            .add("grant_type", "authorization_code")
            .build()

        val headers = mapOf("Accept" to "application/json")
        val jsonString = httpClient.POST(tokenURL, headers, formBody)

        val response = JsonUtil.decodeFromJson(jsonString, GoogleTokenResponse.serializer())

        return response
    }

    override fun getUserInfo(accessToken: String): GoogleUserResponse {
        val headers = mapOf(
            "Content-Type" to "application/json",
            "Authorization" to "Bearer $accessToken"
        )

        val jsonString = httpClient.GET(userInfoURL, headers)

        val userResponse = JsonUtil.decodeFromJson(jsonString, GoogleUserResponse.serializer())

        return userResponse
    }

    companion object {
        private const val tokenURL = "https://oauth2.googleapis.com/token"
        private const val userInfoURL = "https://www.googleapis.com/oauth2/v2/userinfo"
    }
}

@Serializable
data class GoogleTokenResponse(
    @SerialName("access_token") override val accessToken: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("refresh_token") val refreshToken: String?,
    @SerialName("scope") val scope: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("id_token") val idToken: String
) : OAuth2TokenResponse

@Serializable
data class GoogleUserResponse(
    override val id: String,
    override val email: String,
    override val name: String,
) : OAuth2UserResponse