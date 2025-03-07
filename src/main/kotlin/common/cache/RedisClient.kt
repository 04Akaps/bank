package org.example.common.cache

import org.example.common.exception.CustomException
import org.example.common.exception.ErrorCode
import org.redisson.api.RedissonClient
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RedisClient (
    private val template : RedisTemplate<String, String>,
    private val redissonClient: RedissonClient
) {

    fun get(key : String) : String? {
        return template.opsForValue().get(key)
    }

    fun <T> get(
        key : String,
        kSerializer : (Any) -> T?
    ): T? {
        val value = template.opsForValue().get(key)

        value?.let {
            return kSerializer(it)
        } ?: run {
            return null
        }
    }

    fun setIfNotExists(key: String, value: String): Boolean {
        return template.opsForValue().setIfAbsent(key, value) ?: false
    }

    fun <T> invokeWithMutex(mutexKey: String, function : () -> T?) : T? {
        val lock = redissonClient.getLock(mutexKey)

        try {
            lock.lock(15, TimeUnit.SECONDS)
            return function.invoke()
        } catch (e : Exception) {
            throw CustomException(ErrorCode.FailedToInvokeWithMutex, e.toString())
        } finally {
            lock.unlock()
        }

    }

}