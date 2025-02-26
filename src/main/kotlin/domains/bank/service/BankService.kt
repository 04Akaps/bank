package org.example.domains.transfer.service

import org.example.common.logger.Logging
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
class BankService {

    companion object {
        private val logger: Logger = Logging.getLogger(BankService::class.java)
    }
}