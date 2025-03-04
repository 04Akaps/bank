package org.example.domains.transfer.controller

import org.example.common.types.Response
import org.example.domains.transfer.service.BankService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
        @PathVariable("ulid", required = true) ulid: String,
    ) : Response<String> {
        return bankService.createAccount(ulid)
    }

    @GetMapping("/balance/{ulid}/{account_id}")
    fun balance(
        @PathVariable("ulid", required = true) ulid: String,
        @PathVariable("account_id", required = true) accountId: String,
    ) {
        bankService.balance(ulid, accountId)
    }

    @PostMapping("/remove/{ulid}/{account_id}")
    fun removeAccount(
        @PathVariable("ulid", required = true) ulid: String,
        @PathVariable("account_id", required = true) accountId: String,
    ) {
        bankService.removeAccount(ulid, accountId)
    }

}