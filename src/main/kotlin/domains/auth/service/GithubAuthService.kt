package org.example.domains.auth.service



import org.example.common.exception.CustomException
import org.example.common.exception.ErrorCode
import org.example.config.OAuth2Config
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import org.example.interfaces.OAuth2TokenResponse
import org.example.interfaces.OAuth2UserResponse
import org.example.interfaces.OAuthService

private const val key = "github"

@Service(key)
class GithubAuthService (
    private val oAuth2Config: OAuth2Config
) : OAuthService {

    private val githubOAuth = oAuth2Config.providers[key] ?: throw CustomException(ErrorCode.GITHUB_AUTH_CONFIG_NOT_FOUND)
    override val providerName: String = key

    override fun getToken(code: String): GitHubTokenResponse {
        val restTemplate = RestTemplate()

        val request = LinkedMultiValueMap<String, String>().apply {
            add("code", code)
            add("client_id", githubOAuth.clientId)
            add("client_secret", githubOAuth.clientSecret)
            add("redirect_uri", githubOAuth.redirectUri)
            add("grant_type", "authorization_code")
        }

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            accept = listOf(MediaType.APPLICATION_JSON) // GitHub는 JSON 응답을 지원
        }

        val response = restTemplate.postForEntity(
            tokenURL,
            HttpEntity(request, headers),
            GitHubTokenResponse::class.java
        )

        return response.body ?: throw CustomException(ErrorCode.GET_GITHUB_TOKEN)
    }

    override fun getUserInfo(accessToken: String): OAuth2UserResponse {
        val restTemplate = RestTemplate()

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(accessToken)
        }

        val response = restTemplate.exchange(
            userInfoURL,
            HttpMethod.GET,
            HttpEntity(null, headers),
            GitHubUserResponse::class.java
        )

        return response.body?.toOAuth2UserResponse()
            ?: throw CustomException(ErrorCode.GET_GITHUB_USER_INFO)
    }

    companion object {
        private const val tokenURL = "https://github.com/login/oauth/access_token"
        private const val userInfoURL = "https://api.github.com/user"
    }
}

@Serializable
data class GitHubTokenResponse(
    @SerialName("access_token") override val accessToken: String,
    @SerialName("scope") val scope: String,
    @SerialName("token_type") val tokenType: String
) : OAuth2TokenResponse

@Serializable
data class GitHubUserResponse(
    val id: Int,
    val login: String,
    val email: String?,
    val name: String?,
) {
    fun toOAuth2UserResponse() = OAuth2UserResponse(
        id = id.toString(),
        email = email,
        name = name
    )
}


@Serializable
data class OAuth2UserResponse(
    override val id: String,
    override val email: String?,
    override val name: String?,
) : OAuth2UserResponse

