package org.example.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


/*
    oauth2:
        providers:
            google:
              client-id: "sample"
              client-secret: "sample"
              redirect-uri: "sample"
            github:
              client-id: "sample"
              client-secret: "sample"
              redirect-uri: "sample"

             // 추가 된다면,
            facebook:
              client-id: "sample"
              client-secret: "sample"
              redirect-uri: "sample"
 */


@Configuration
@ConfigurationProperties(prefix = "oauth2")
class OAuth2Config {
    val providers: MutableMap<String, OAuth2ProviderConfig> = mutableMapOf()
}

data class OAuth2ProviderConfig(
    var clientId: String = "",
    var clientSecret: String = "",
    var redirectUri: String = ""
)