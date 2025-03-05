package org.example.domains.history.service

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.builtins.ListSerializer
import org.example.common.JsonUtil.JsonUtil
import org.example.common.cache.RedisClient
import org.example.common.exception.CustomException
import org.example.common.exception.ErrorCode
import org.example.common.logger.Logging
import org.example.common.types.Response
import org.example.common.types.ResponseProvider
import org.example.domains.history.model.History
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
class HistoryService(
    private val redisClient: RedisClient,
) {

    fun history(ulid: String, accountID : String) : Response<List<History>>? {
        // 캐시에서 가져 올꺼다. -> 거래가 일어날 떄 마다, 관련된 키를 업데이트 하는 방식으로
        // 데이터 간격을 해결
        val key = "history:$ulid:$accountID"

        val cacheValue = redisClient.get(key)

        return when {
            cacheValue == null -> {
                // TODO -> DB 조회
                return ResponseProvider.success(emptyList())
            }

            else -> {
                val cacheData = JsonUtil.decodeFromJson(cacheValue, ListSerializer(History.serializer()))
                ResponseProvider.success(cacheData)
            }
        }
    }

    companion object {
        private val logger: Logger = Logging.getLogger(HistoryService::class.java)
    }
}