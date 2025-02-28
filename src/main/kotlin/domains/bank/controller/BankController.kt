package org.example.domains.transfer.controller

import org.example.domains.transfer.service.BankService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/bank")
class BankController(
    private val bankService: BankService
) {

    @PostMapping("/create/{ulid}")
    fun createAccount(
        @RequestParam("ulid", required = true) ulid: String,
    ) {
        bankService.createAccount(ulid)
    }

    @GetMapping("/balance/{ulid}/{account_id}")
    fun balance(
        @RequestParam("ulid", required = true) ulid: String,
        @RequestParam("account_id", required = true) accountId: String,
    ) {
        bankService.balance(ulid, accountId)
    }

    @PostMapping("/remove/{ulid}/{account_id}")
    fun removeAccount(
        @RequestParam("ulid", required = true) ulid: String,
        @RequestParam("account_id", required = true) accountId: String,
    ) {
        bankService.removeAccount(ulid, accountId)
    }

}