package org.example.domains.transfer.service

import org.example.common.logger.Logging
import org.slf4j.Logger
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Service
class BankService {

    fun createAccount(ulid : String) {

    }

    fun balance(ulid : String, accountID : String) {

    }

    fun removeAccount(ulid : String, accountID : String) {

    }

    companion object {
        private val logger: Logger = Logging.getLogger(BankService::class.java)
    }
}