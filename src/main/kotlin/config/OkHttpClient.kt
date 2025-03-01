package org.example.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@Configuration
class OkHttpClientConfig {

    @Bean
    fun httpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)  // 연결 타임아웃 설정
            .readTimeout(30, TimeUnit.SECONDS)     // 읽기 타임아웃 설정
            .writeTimeout(30, TimeUnit.SECONDS)    // 쓰기 타임아웃 설정
            .followRedirects(true) // 리디렉트 자동 허용
            .followSslRedirects(true)
            .build()
    }
}