package org.example.domains.bank.repository

import org.example.domains.auth.repository.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface BankUserRepository : JpaRepository<User, String> {
    fun findByUlid(ulid: String): User?
}