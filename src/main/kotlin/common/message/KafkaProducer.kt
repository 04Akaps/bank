package org.example.common.message

import org.example.common.logger.Logging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.slf4j.Logger

enum class Topics(
    val topic: String,
) {
    Transactions("transactions"),
}

@Component
class KafkaProducer(
    private val template : KafkaTemplate<String, Any>,
    private val logger: Logger = Logging.getLogger(KafkaProducer::class.java)
) {
    fun sendMessage(topic: String, message: Any, key: String? = null){

        val future = if (key != null) {
            template.send(topic, key, message)
        } else {
            template.send(topic, message)
        }

        future.whenComplete { result, ex ->
            if (ex == null) {
                logger.info("메시지 발행 성공 - " +
                        "topic: ${result.recordMetadata.topic()}, " +
                        "partition: ${result.recordMetadata.partition()}, " +
                        "offset: ${result.recordMetadata.offset()}")
            } else {
                logger.error("메시지 발행 실패 - ${ex.message}", ex)
            }
        }
    }
}