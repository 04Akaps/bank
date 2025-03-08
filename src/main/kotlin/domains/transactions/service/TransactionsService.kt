package org.example.domains.transactions.service

import kotlinx.serialization.builtins.serializer
import org.example.common.JsonUtil.JsonUtil
import org.example.common.cache.RedisClient
import org.example.common.cache.RedisKeyProvider
import org.example.common.exception.CustomException
import org.example.common.exception.ErrorCode
import org.example.common.logger.Logging
import org.example.common.message.KafkaProducer
import org.example.common.message.Topics
import org.example.common.transaction.TxAdvice
import org.example.common.types.Response
import org.example.common.types.ResponseProvider
import org.example.domains.auth.service.AuthService
import org.example.domains.transactions.model.DepositResponse
import org.example.domains.transactions.model.TransferResponse
import org.example.domains.transactions.repository.TransactionsAccountRepository
import org.example.domains.transactions.repository.TransactionsUserRepository
import org.example.types.message.TransactionsMessage
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import org.slf4j.Logger

@Service
class TransactionsService(
    private val transactionsUserRepository: TransactionsUserRepository,
    private val transactionsAccountRepository: TransactionsAccountRepository,
    private val txAdvice: TxAdvice,
    private val redisClient: RedisClient,
    private val kafkaProducer: KafkaProducer,
    private val logger: Logger = Logging.getLogger(AuthService::class.java)
) {

    fun deposit(toUlid : String, fromAccountId: String, value: BigDecimal) : Response<DepositResponse> = Logging.loggingStopWatch(logger) { log ->
        log["fromUlid"] = toUlid
        log["fromAccountId"] = fromAccountId
        log["value"] = value

        val key = RedisKeyProvider.bankMutexKey(toUlid, fromAccountId)

        redisClient.invokeWithMutex(key) {
            return@invokeWithMutex txAdvice.run {
                val user = transactionsUserRepository.findByUlid(toUlid)
                    ?: throw CustomException(ErrorCode.FailedToFindUserByUlid, "User with ULID $toUlid not found")

                val account = transactionsAccountRepository.findByUlidAndUser(fromAccountId, user)
                    ?: throw CustomException(ErrorCode.FailedToFindAccount, "Account with ID $fromAccountId for user $toUlid not found")

                account.balance = account.balance.add(value)
                account.updatedAt = LocalDateTime.now()
                transactionsAccountRepository.save(account)

                val message = JsonUtil.encodeToJson(TransactionsMessage(
                    fromUlid = "0x0",
                    fromName = "0x0",
                    fromAccountID = "0x0",
                    toUlid = toUlid,
                    toName = user.username,
                    toAccountID = fromAccountId,
                    value = value,
                    time = LocalDateTime.now(),
                ), TransactionsMessage.serializer(),)

                kafkaProducer.sendMessage(Topics.Transactions.topic, message)

                ResponseProvider.success(DepositResponse(account.balance))
            }
        }


    }

    fun transfer(fromUlid: String, fromAccountId: String, toAccountId : String, value: BigDecimal) : Response<TransferResponse> = Logging.loggingStopWatch(logger) { log ->
        log["fromUlid"] = fromUlid
        log["fromAccountId"] = fromAccountId
        log["value"] = value
        log["toAccountId"] = toAccountId

        val key = RedisKeyProvider.bankMutexKey(fromUlid, fromAccountId)

        redisClient.invokeWithMutex(key) {
            return@invokeWithMutex  txAdvice.run {
                val fromAccount = transactionsAccountRepository.findByUlid(fromAccountId)
                    ?: throw CustomException(ErrorCode.FailedToFindAccount, "출금 계좌를 찾을 수 없습니다: $fromAccountId")

                val toAccount = transactionsAccountRepository.findByUlid(toAccountId)
                    ?: throw CustomException(ErrorCode.FailedToFindAccount, "입금 계좌를 찾을 수 없습니다: $toAccountId")

                if (fromAccount.user.ulid != fromUlid) {
                    throw CustomException(ErrorCode.AccountNotOwnedByUser, "계좌에 대한 접근 권한이 없습니다")
                } else if (fromAccount.balance < value) {
                    throw CustomException(ErrorCode.ValueAmountLimit, "잔액이 부족합니다: 현재 ${fromAccount.balance}, 필요 $value")
                } else if (value <= BigDecimal.ZERO) {
                    throw CustomException(ErrorCode.NonZeroBalance, "이체 금액은 0보다 커야 합니다")
                }

                fromAccount.balance = fromAccount.balance.subtract(value)
                toAccount.balance = toAccount.balance.add(value)

                transactionsAccountRepository.save(fromAccount)
                transactionsAccountRepository.save(toAccount)

                kafkaProducer.sendMessage(Topics.Transactions.topic, TransactionsMessage(
                    fromUlid = fromUlid,
                    fromName = fromAccount.user.username,
                    fromAccountID = fromAccountId,
                    toUlid = toAccount.user.ulid,
                    toName = toAccount.user.username,
                    toAccountID = toAccountId,
                    value = value,
                    time = LocalDateTime.now(),
                ))

                ResponseProvider.success(TransferResponse(fromAccount.balance, toAccount.balance))

            }
        }


    }


}