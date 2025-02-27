package org.example.config

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration
import java.util.Objects
import java.util.concurrent.Executors

@Configuration
class RedisConfig {

    @Bean
    fun redisConnectionFactory(
        @Value("\${database.redis.host}") host: String,
        @Value("\${database.redis.port}") port: Int,
        @Value("\${database.redis.password:#{null}}") password: String?,
        @Value("\${database.redis.database:0}") database: Int,
        @Value("\${database.redis.timeout:10000}") timeout: Long
    ) : LettuceConnectionFactory {
        val config = RedisStandaloneConfiguration(host, port).apply {
            password?.let { setPassword(RedisPassword.of(it)) } // 비밀번호가 있을 경우 설정
            setDatabase(database)
        }

        val clientConfig = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofMillis(timeout)) // 타임아웃 설정
            .build()

        return LettuceConnectionFactory(config, clientConfig)
    }

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Objects> {
        val template = RedisTemplate<String, Objects>()

        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = Jackson2JsonRedisSerializer(Objects::class.java)
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = Jackson2JsonRedisSerializer(Objects::class.java)
        template.afterPropertiesSet()

        return template
    }

    @Bean
    fun redisDispatcher(
        @Value("\${database.redis.thread-pool:10}") poolSize: Int,
    ): CoroutineDispatcher {
        return Executors.newFixedThreadPool(poolSize).asCoroutineDispatcher()
    }

}