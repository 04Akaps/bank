package org.example.common.cache

object RedisKeyProvider {
    private const val BANK_HANDLING_MUTEX_BASE_KEY = "bankMutexKey"

    fun bankMutexKey(ulid: String, accountUlid: String): String {
        return "$BANK_HANDLING_MUTEX_BASE_KEY:$ulid:$accountUlid"
    }
}