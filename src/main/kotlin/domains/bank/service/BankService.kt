package org.example.domains.transfer.service

import com.github.f4b6a3.ulid.UlidCreator
import org.example.common.cache.RedisClient
import org.example.common.cache.RedisKeyProvider
import org.example.common.exception.CustomException
import org.example.common.exception.ErrorCode
import org.example.common.logger.Logging
import org.example.common.transaction.TxAdvice
import org.example.common.types.Response
import org.example.common.types.ResponseProvider
import org.example.domains.bank.repository.BankAccountRepository
import org.example.domains.bank.repository.BankUserRepository
import org.example.types.entity.Account
import org.slf4j.Logger
import org.springframework.stereotype.Service
import java.lang.Math.random
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class BankService(
    private val redisClient: RedisClient,
    private val txAdvice: TxAdvice,
    private val userRepository: BankUserRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val logger: Logger = Logging.getLogger(BankService::class.java)
) {

    fun createAccount(ulid : String) : Response<String> = Logging.loggingStopWatch(logger) { it
        it["ulid"] = ulid

        txAdvice.run {
            val user = userRepository.findByUlid(ulid) ?: throw CustomException(ErrorCode.FailedToFindUserByUlid, ulid)

            val ulid = UlidCreator.getUlid().toString()
            val accountNumber = generateRandomAccountNumber()

            val account = Account(
                ulid = ulid,
                user = user,
                accountNumber = accountNumber
            )

            try {
                bankAccountRepository.save(account)
            } catch (e : Exception) {
                throw CustomException(ErrorCode.FailedToSaveAccount, e.toString())
            }
        }

        return@loggingStopWatch ResponseProvider.success("SUCCESS")
    }

    fun balance(ulid : String, accountID : String) : Response<BigDecimal>? = Logging.loggingStopWatch(logger) { it
        it["ulid"] = ulid
        it["accountID"] = accountID

        return@loggingStopWatch txAdvice.readOnly {
            val account = bankAccountRepository.findByUlid(accountID) ?: throw CustomException(ErrorCode.FailedToFindAccount, accountID)
            if (account.user.ulid != ulid) throw CustomException(ErrorCode.AccountNotOwnedByUser, "계좌 소유자가 아님")
            ResponseProvider.success(account.balance)
        }
    }

    fun removeAccount(ulid : String, accountID : String) : Response<String> = Logging.loggingStopWatch(logger) { it
        it["ulid"] = ulid
        it["accountID"] = accountID

        // transfer 경우에 대해서 동시성 처리를 위한 redis 분산 키 적용 --> 계좌 삭제 할 떄, 요청이 들어올 수도 있으니
        redisClient.invokeWithMutex(RedisKeyProvider.bankMutexKey(ulid, accountID)) {
            return@invokeWithMutex txAdvice.run {
                val user = userRepository.findByUlid(ulid) ?: run {
                    logger.warn("User not found for ulid: $ulid")
                    throw CustomException(ErrorCode.FailedToFindUserByUlid, ulid)
                }

                val account = bankAccountRepository.findByUlid(accountID) ?: run {
                    logger.warn("Account not found for accountID: $accountID")
                    throw CustomException(ErrorCode.FailedToFindAccount, accountID)
                }

                if (account.user.ulid != user.ulid) {
                    logger.warn("Account $accountID does not belong to user $ulid")
                    throw CustomException(ErrorCode.AccountNotOwnedByUser, "계좌 소유자가 아님")
                } else if (account.balance.compareTo(BigDecimal.ZERO) != 0){
                    logger.warn("Account $accountID has non-zero balance: ${account.balance}")
                    throw CustomException(ErrorCode.NonZeroBalance, "계좌 잔액이 0이어야 삭제 가능")
                }

                val updatedAccount = account.copy(
                    isDeleted = true,
                    deletedAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )

                bankAccountRepository.save(updatedAccount)

                ResponseProvider.success("SUCCESS")
            }
        }
    }

    private fun generateRandomAccountNumber(): String {
        val bankCode = "003"
        val section = "12"

        val number = (1..8).joinToString("") { random().toString() }
        return "$bankCode-$section-$number"
    }
}