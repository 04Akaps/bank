package org.example.domains.auth.repository.model

import jakarta.persistence.Entity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user")
data class User(
    @Id
    @Column(name = "ulid", length = 26)
    val ulid: String,

    @Column(name = "platform", nullable = false, length = 25)
    val platform: String,

    @Column(name = "username", nullable = false, unique = true, length = 50)
    val username: String,

    @Column(name = "access_token", length = 255)
    val accessToken: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)