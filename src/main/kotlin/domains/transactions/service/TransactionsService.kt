package org.example.domains.transactions.service

import org.example.common.transaction.TxAdvice
import org.example.domains.transactions.repository.TransactionsAccountRepository
import org.example.domains.transactions.repository.TransactionsUserRepository
import org.example.types.entity.Account
import org.example.types.entity.User
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class TransactionsService(
    private val transactionsUserRepository: TransactionsUserRepository,
    private val transactionsAccountRepository: TransactionsAccountRepository,
    private val txAdvice: TxAdvice
) {


    fun deposit(fromUlid : String, fromAccountID: String, value: BigDecimal) {

        var account : Account? = null

        txAdvice.readOnly {
            val user = transactionsUserRepository.findByUlid(fromUlid)
                ?: throw IllegalArgumentException("User with ULID $fromUlid not found")

            account = transactionsAccountRepository.findByUlidAndUser(fromAccountID, user)
                ?: throw IllegalArgumentException("Account with ID $fromAccountID for user $fromUlid not found")
        }


        account?.let { it
            txAdvice.run {
                it.balance = it.balance.add(value)
                it.updatedAt = LocalDateTime.now()
                transactionsAccountRepository.save(account)
            }
        } ?: run {
            // throw
        }

    }

    fun transfer(fromUlid: String, fromAccountId: String, toAccountId : String, value: BigDecimal) {

    }


}