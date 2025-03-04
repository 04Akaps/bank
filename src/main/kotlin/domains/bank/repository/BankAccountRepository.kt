package org.example.domains.bank.repository

import org.example.domains.bank.repository.model.Account
import org.springframework.data.jpa.repository.JpaRepository

interface BankAccountRepository : JpaRepository<Account, String> {
}