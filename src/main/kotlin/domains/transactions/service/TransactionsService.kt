package org.example.domains.transactions.service

import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TransactionsService {


    fun deposit(ulid: String, accountId: String, value: BigDecimal) {

    }

    fun transfer(fromUlid: String, fromAccountId: String, toAccountId : String, value: BigDecimal) {

    }


}