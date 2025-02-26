package org.example.domains.transfer.controller

import org.example.domains.transfer.service.BankService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/bank")
class BankController(
    private val bankService: BankService
) {

    @PostMapping("/create-account/{ulid}")
    fun createAccount() {

    }

    @GetMapping("/account/{account_id}/{ulid}")
    fun balance() {

    }

    @PostMapping("/remove/{account_id}")
    fun removeAccount() {

    }

}