package org.example.common.exception


import org.example.common.logger.Logging

class CustomException(
    private val codeInterface: CodeInterface,
    private val additionalMessage: String? = null,
) : RuntimeException(
    "${ codeInterface.message}-${additionalMessage}"
) {
    init {
        logger.error("Exception created with code: ${codeInterface.code} and message: ${super.message}")
    }

    fun getCodeInterface(): CodeInterface {
        var codeInterface = codeInterface

        if (additionalMessage != null) {
            codeInterface.message += additionalMessage.toString()
        }

        return codeInterface
    }

    companion object {
        private val logger = Logging.getLogger(CustomException::class.java)
    }
}

interface CodeInterface {
    val code: Int
    var message: String
}
