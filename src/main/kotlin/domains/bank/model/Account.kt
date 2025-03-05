package org.example.domains.bank.repository.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "account")
data class Account(
    @Id
    @Column(name = "ulid", length = 26, nullable = false)
    val ulid: String,

    @Column(name = "user_ulid", length = 26, nullable = false)
    val userUlid: String,

    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    val balance: BigDecimal = BigDecimal.ZERO,

    @Column(name = "account_number", length = 20, nullable = false, unique = true)
    val accountNumber: String,

    @Column(name = "is_deleted", nullable = false)
    val isDeleted: Boolean = false,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "deleted_at")
    val deletedAt: LocalDateTime? = null
)