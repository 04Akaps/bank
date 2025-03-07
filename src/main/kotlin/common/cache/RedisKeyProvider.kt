package org.example.common.cache

object RedisKeyProvider {
    private const val BANK_HANDLING_MUTEX_BASE_KEY = "bankMutexKey"
    private const val HISTORY_CACHE_KEY = "history"

    fun bankMutexKey(ulid: String, accountUlid: String): String {
        return "$BANK_HANDLING_MUTEX_BASE_KEY:$ulid:$accountUlid"
    }

    fun historyCacheKey(ulid: String, accountUlid: String): String {
        return "$HISTORY_CACHE_KEY:$ulid:$accountUlid"
    }
}