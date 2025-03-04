package org.example.common.transaction


import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional


interface TransactionRunner {
    fun <T> run(function: () -> T?): T?
    fun <T> readOnly(function: () -> T?): T?
    fun <T> runNewTransaction(function: () -> T?): T?
}

@Component
class TxAdvice(
    private val advice: TransactionRunner = Advice()
) {
    fun <T> run(function: () -> T?): T? = advice.run(function)
    fun <T> readOnly(function: () -> T?): T? = advice.readOnly(function)
    fun <T> runNewTransaction(function: () -> T?): T? = advice.runNewTransaction(function)

    @Component
    private class Advice : TransactionRunner {
        @Transactional
        override fun <T> run(function: () -> T?): T? = function()

        @Transactional(readOnly = true)
        override fun <T> readOnly(function: () -> T?): T? = function()

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        override fun <T> runNewTransaction(function: () -> T?): T? = function()
    }
}