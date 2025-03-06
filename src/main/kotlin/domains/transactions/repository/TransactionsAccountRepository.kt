package org.example.domains.transactions.repository

import org.example.types.entity.Account
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionsAccountRepository : JpaRepository<Account, String> {
}