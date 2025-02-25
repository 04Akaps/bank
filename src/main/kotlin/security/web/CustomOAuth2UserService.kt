package org.example.security.web

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService : OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val delegate = DefaultOAuth2UserService()
        val oAuth2User = delegate.loadUser(userRequest)

        // 어떤 OAuth2 서비스인지 확인
        val registrationId = userRequest.clientRegistration.registrationId
        val attributes = oAuth2User.attributes

        // 이메일 정보 가져오기
        val email = when (registrationId) {
            "google" -> attributes["email"] as String
            "github" -> {
                val emails = oAuth2User.attributes["emails"] as List<Map<String, Any>>?
                emails?.firstOrNull()?.get("email") as String?
            }
            else -> null
        }

        if (email == null) {
            throw OAuth2AuthenticationException("Email not found from $registrationId")
        }

        return DefaultOAuth2User(
            setOf(SimpleGrantedAuthority("ROLE_USER")),
            mapOf("email" to email),
            "email"
        )
    }
}