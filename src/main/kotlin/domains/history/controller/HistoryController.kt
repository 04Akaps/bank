package org.example.domains.history.controller

import org.example.domains.history.service.HistoryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/history")
class HistoryController(
    private val historyService: HistoryService
) {

    @GetMapping("/{ulid}/{account_id}")
    fun history(
        @PathVariable("ulid", required = true) ulid: String,
        @PathVariable("account_id", required = true) accountId: String,
    ) {
        historyService.history(ulid, accountId)
    }
}