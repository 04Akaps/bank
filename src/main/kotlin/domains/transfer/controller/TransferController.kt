package org.example.domains.transfer.controller

import org.example.domains.transfer.service.TransferService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/transfer")
class TransferController(private val transferService: TransferService) {
}