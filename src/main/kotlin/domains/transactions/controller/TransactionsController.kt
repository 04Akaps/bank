package org.example.domains.transactions.controller

import org.example.common.types.Response
import org.example.domains.history.service.HistoryService
import org.example.domains.transactions.model.DepositRequest
import org.example.domains.transactions.model.DepositResponse
import org.example.domains.transactions.model.TransferRequest
import org.example.domains.transactions.model.TransferResponse
import org.example.domains.transactions.service.TransactionsService
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/transactions")
class TransactionsController(
    private val transactionsService: TransactionsService
) {

    @PostMapping("/deposit")
    fun deposit(@RequestBody(required = true) request: DepositRequest) : Response<DepositResponse> {
        return transactionsService.deposit(request.fromUlid, request.fromAccountId, request.value)
    }

    @PostMapping("/transfer")
    fun transfer(@RequestBody(required = true) request: TransferRequest) : Response<TransferResponse> {
        return transactionsService.transfer(request.fromUlid, request.fromAccountId, request.toAccountId, request.value)
    }

}