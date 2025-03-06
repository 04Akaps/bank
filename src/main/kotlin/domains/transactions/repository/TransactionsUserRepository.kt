package org.example.domains.transactions.repository

import org.example.types.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionsUserRepository : JpaRepository<User, String> {
}