package org.example.domains.transactions.controller

import org.example.domains.history.service.HistoryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/transactions")
class TransactionsController(
) {

    @PostMapping("/deposit/{account_id}")
    fun deposit() {

    }

    @PostMapping("/withdraw/{account_id}")
    fun withdraw() {

    }

    @PostMapping("/transfer/{account_id}")
    fun transfer() {

    }

}