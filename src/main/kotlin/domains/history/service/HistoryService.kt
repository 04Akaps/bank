package org.example.domains.history.service

import kotlinx.serialization.builtins.ListSerializer
import org.example.common.JsonUtil.JsonUtil
import org.example.common.cache.RedisClient
import org.example.common.cache.RedisKeyProvider
import org.example.common.logger.Logging
import org.example.common.types.Response
import org.example.common.types.ResponseProvider
import org.example.domains.history.repository.HistoryRepositoryCustom
import org.example.types.dto.History
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
class HistoryService(
    private val redisClient: RedisClient,
    private val historyRepository: HistoryRepositoryCustom,
    private val logger: Logger = Logging.getLogger(HistoryService::class.java)
) {

    fun history(ulid: String, accountID : String) : Response<List<History>> = Logging.loggingStopWatch(logger) { it
        it["ulid"] = ulid
        it["accountID"] = accountID

        // 캐시에서 가져 올꺼다. -> 거래가 일어날 떄 마다, 메시지 큐를 활용하여 관련된 키를 업데이트 하는 방식으로
        val key = RedisKeyProvider.historyCacheKey(ulid,accountID)
        val cacheValue = redisClient.get(key)

        return@loggingStopWatch when {
            cacheValue == null -> {
                // 혹시 모르니 없다면, DB 조회 후 설정
                val result = historyRepository.findLatestTransactionHistory(ulid)
                redisClient.setIfNotExists(key, JsonUtil.encodeToJson(result, ListSerializer(History.serializer())))
                ResponseProvider.success(result)
            }

            else -> {
                val cacheData = JsonUtil.decodeFromJson(cacheValue, ListSerializer(History.serializer()))
                ResponseProvider.success(cacheData)
            }
        }
    }
}