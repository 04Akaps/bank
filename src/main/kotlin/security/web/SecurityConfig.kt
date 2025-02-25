package org.example.security.web

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.web.filter.CharacterEncodingFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService,
   private val oAuth2SuccessHandler: OAuth2SuccessHandler
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/", "/login/**", "/oauth2/**", "/images/**").permitAll()
                    .anyRequest().authenticated()
            }
            // OAuth2 성공 URL
            .oauth2Login { oauth2Login ->
                oauth2Login.userInfoEndpoint { userInfo -> userInfo.userService(customOAuth2UserService) }
                oauth2Login.successHandler(oAuth2SuccessHandler) // JWT 생성 후 리디렉트 처리
            }
            // Iframe 사용 유무
            .headers { headers -> headers.frameOptions().disable() }
            // 인증 안되어 있으면 전환
            .exceptionHandling { exceptions -> exceptions.authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login")) }
            // 로그인 인증 된다면 이동
            .formLogin { formLogin -> formLogin.successForwardUrl("/welcome") }
            // 로그아웃
            .logout { logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login")
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
            }
            .addFilterAt(CharacterEncodingFilter(), CsrfFilter::class.java)
            .csrf { csrf -> csrf.disable() }

        return http.build()
    }

    @Bean
    fun clientRegistrationRepository(
        oAuth2ClientProperties: OAuth2ClientProperties,
        customProperties: CustomOAuth2ClientProperties
    ): InMemoryClientRegistrationRepository {

        // 소셜 설정 등록
        val registrations = oAuth2ClientProperties.registration.keys
            .map { getRegistration(oAuth2ClientProperties, it) }
            .filter { it != null }
            .toMutableList()

        // 추가 설정 프로퍼티
        val customRegistrations = customProperties.registration

        // 추가 소셜 설정을 기본 소셜 설정에 추가
        for (customRegistration in customRegistrations) {

            when (customRegistration.key) {
                "kakao" -> registrations.add(
                    OAuth2Provider.KAKAO.getBuilder("kakao")
                    .clientId(customRegistration.value.clientId)
                    .clientSecret(customRegistration.value.clientSecret)
                    .jwkSetUri(customRegistration.value.jwkSetUri)
                    .build())
                "naver" -> registrations.add(
                    OAuth2Provider.NAVER.getBuilder("naver")
                    .clientId(customRegistration.value.clientId)
                    .clientSecret(customRegistration.value.clientSecret)
                    .jwkSetUri(customRegistration.value.jwkSetUri)
                    .build())
            }

        }

        return InMemoryClientRegistrationRepository(registrations)
    }

    // 공통 소셜 설정을 호출합니다.
    private fun getRegistration(clientProperties: OAuth2ClientProperties, client: String): ClientRegistration? {
        val registration = clientProperties.registration[client]
        return when(client) {
//            "google" -> OAuth2Provider.GOOGLE.getBuilder(client)
//                .clientId(registration?.clientId)
//                .clientSecret(registration?.clientSecret)
//                .scope("email", "profile")
//                .build()
            else -> null
        }
    }


}