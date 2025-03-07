package org.example.common.global

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class Global(
    private val userIdToNameMap: ConcurrentHashMap<String, String> = ConcurrentHashMap<String, String>()
) {
    fun addUserMapping(ulid: String, username: String) {
        userIdToNameMap[ulid] = username
    }

    fun getUsernameByUlid(ulid: String): String {
        return userIdToNameMap[ulid] ?: ""
    }
}