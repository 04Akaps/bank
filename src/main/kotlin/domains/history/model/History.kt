package org.example.domains.history.model

import kotlinx.serialization.Serializable
import org.example.common.json.BigDecimalSerializer
import org.example.common.json.LocalDateTimeSerializer
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class History(
    val from : String,
    val fromUser : String,
    val to : String,
    val toUser : String,

    @Serializable(with = BigDecimalSerializer::class)
    val value : BigDecimal,
    @Serializable(with = LocalDateTimeSerializer::class)
    val time : LocalDateTime
)