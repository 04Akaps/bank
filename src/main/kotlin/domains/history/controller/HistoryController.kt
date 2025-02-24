package org.example.domains.history.controller

import org.example.domains.history.service.HistoryService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/history")
class HistoryController(private val historyService: HistoryService) {
}