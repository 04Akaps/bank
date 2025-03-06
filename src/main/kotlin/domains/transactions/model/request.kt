package org.example.domains.transactions.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class DepositRequest(
    @field:NotBlank(message = "계좌 ID는 필수입니다")
    val accountId: String,

    @field:NotBlank(message = "ULID는 필수입니다")
    val ulid: String,

    @field:Positive(message = "금액은 양수여야 합니다")
    val value: BigDecimal
)


data class TransferRequest(
    @field:NotBlank(message = "from 계좌 ID는 필수입니다")
    val fromAccountId: String,

    @field:NotBlank(message = "to 계좌 ID는 필수입니다")
    val toAccountId: String,

    @field:NotBlank(message = "from ULID는 필수입니다")
    val fromUlid: String,

    @field:Positive(message = "금액은 양수여야 합니다")
    val value: BigDecimal
)