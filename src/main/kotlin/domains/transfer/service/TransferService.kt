package org.example.domains.transfer.service

import org.example.common.logger.Logging
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
class TransferService {

    companion object {
        private val logger: Logger = Logging.getLogger(TransferService::class.java)
    }
}