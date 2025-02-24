package org.example.domains.auth.service

import org.example.common.logger.Logging
import org.springframework.stereotype.Service
import org.slf4j.Logger

@Service
class AuthService {

    companion object {
        private val logger: Logger = Logging.getLogger(AuthService::class.java)
    }
}