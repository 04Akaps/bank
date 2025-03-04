package org.example.domains.transfer.service

import com.github.f4b6a3.ulid.UlidCreator
import org.example.common.cache.RedisClient
import org.example.common.exception.CustomException
import org.example.common.exception.ErrorCode
import org.example.common.logger.Logging
import org.example.common.transaction.TxAdvice
import org.example.common.types.Response
import org.example.common.types.ResponseProvider
import org.example.domains.auth.repository.model.User
import org.example.domains.bank.repository.BankAccountRepository
import org.example.domains.bank.repository.BankUserRepository
import org.example.domains.bank.repository.model.Account
import org.slf4j.Logger
import org.springframework.stereotype.Service
import java.lang.Math.random

//import java.lang.Math.random

@Service
class BankService(
    private val redisClient: RedisClient,
    private val txAdvice: TxAdvice,
    private val userRepository: BankUserRepository,
    private val bankAccountRepository: BankAccountRepository
) {

    fun createAccount(ulid : String) : Response<String>  = Logging.loggingStopWatch(logger) { it
        it["ulid"] = ulid

        // 굳이 분산락은 필요가 없어 보이기 떄문에 DB 처리만 진행

        txAdvice.run {
            val user = userRepository.findByUlid(ulid)

            user?.let {
                val userUlid = user.ulid
                val ulid = UlidCreator.getUlid().toString()
                val accountNumber = generateRandomAccountNumber()

                val account = Account(
                    ulid = ulid,
                    userUlid = userUlid,
                    accountNumber = accountNumber,
                )

                bankAccountRepository.save<Account>(account)
            } ?: run {
                throw CustomException(ErrorCode.FailedToFindUserByUlid, ulid)
            }
        }

        return@loggingStopWatch ResponseProvider.success("SUCCESS")
    }

    fun balance(ulid : String, accountID : String) {

    }

    fun removeAccount(ulid : String, accountID : String) {

    }


    private fun generateRandomAccountNumber(): String {
        val bankCode = "003"
        val section = "12"

        val number = (1..8).joinToString("") { random().toString() }
        return "$bankCode-$section-$number"
    }

    companion object {
        private val logger: Logger = Logging.getLogger(BankService::class.java)
    }
}