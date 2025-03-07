package org.example.types.message

import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.kafka.common.protocol.types.Field.Str
import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionsMessage(
    @JsonProperty("fromUlid")
    private val fromUlid: String,
    @JsonProperty("fromName")
    private val fromName: String,
    @JsonProperty("fromAccountID")
    private val fromAccountID: String,

    @JsonProperty("toUlid")
    private val toUlid: String,
    @JsonProperty("toName")
    private val toName: String,
    @JsonProperty("toAccountID")
    private val toAccountID: String,

    @JsonProperty("value")
    private val value: BigDecimal,
    @JsonProperty("time")
    private val time : LocalDateTime
)