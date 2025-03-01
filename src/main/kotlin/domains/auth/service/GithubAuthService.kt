package org.example.domains.auth.service

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.SerialName
import org.example.common.exception.CustomException
import org.example.common.exception.ErrorCode
import org.example.config.OAuth2Config
import org.springframework.stereotype.Service
import kotlinx.serialization.Serializable
import org.example.interfaces.OAuth2UserResponse
import org.example.interfaces.OAuth2TokenResponse
import org.example.interfaces.OAuthService
import okhttp3.*
import org.example.common.JsonUtil.JsonUtil
import org.example.common.httpClinet.CallClient

private const val key = "github"

@Service(key)
class GithubAuthService (
    private val oAuth2Config: OAuth2Config,
    private val httpClient: CallClient
) : OAuthService {

    private val githubOAuth = oAuth2Config.providers[key] ?: throw CustomException(ErrorCode.GITHUB_AUTH_CONFIG_NOT_FOUND)
    override val providerName: String = key

    override fun getToken(code: String): OAuth2TokenResponse {
        val formBody = FormBody.Builder()
            .add("code", code)
            .add("client_id", githubOAuth.clientId)
            .add("client_secret", githubOAuth.clientSecret)
            .add("redirect_uri", githubOAuth.redirectUri)
            .add("grant_type", "authorization_code")
            .build()

        val headers = mapOf("Accept" to "application/json")
        val jsonString = httpClient.POST(tokenURL, headers, formBody)

        val response = JsonUtil.decodeFromJson(jsonString, GitHubTokenResponse.serializer())

        return response
    }

    override fun getUserInfo(accessToken: String): OAuth2UserResponse {
        val headers = mapOf(
            "Content-Type" to "application/json",
            "Authorization" to "Bearer $accessToken"
        )

        val jsonString = httpClient.GET(userInfoURL, headers)


        val userResponse = JsonUtil.decodeFromJson(jsonString, GitHubUserResponse.serializer())

        return userResponse.toOAuth2UserResponse()
    }

    companion object {
        private const val tokenURL = "https://github.com/login/oauth/access_token"
        private const val userInfoURL = "https://api.github.com/user"
    }
}

@Serializable
data class GitHubTokenResponse(
    @SerialName("access_token") override val accessToken: String,
    @SerialName("expires_in") val expiresIn: Int? = null,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("scope") val scope: String? = null,
    @SerialName("token_type") val tokenType: String? = null,
    @SerialName("id_token") val idToken: String? = null
) : OAuth2TokenResponse


@Serializable
data class GitHubUserResponse(
    val id: Int,
    val repos_url: String,
    val name: String,
) {
    fun toOAuth2UserResponse() = OAuth2UserResponse(
        id = id.toString(),
        email = repos_url,
        name = name
    )
}


@Serializable
data class OAuth2UserResponse(
    override val id: String,
    override val email: String?,
    override val name: String?,
) : OAuth2UserResponse

