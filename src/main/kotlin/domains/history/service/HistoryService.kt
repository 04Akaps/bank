package org.example.domains.history.service

import org.example.common.logger.Logging
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
class HistoryService {

    companion object {
        private val logger: Logger = Logging.getLogger(HistoryService::class.java)
    }
}