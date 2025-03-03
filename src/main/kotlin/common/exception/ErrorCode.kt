package org.example.common.exception

enum class ErrorCode(
    override val code : Int,
    override var message : String
) : CodeInterface {
    FailedToLoggerInvoke(-100, "Failed to logger invoke"),
    FailedToClientCall(-101, "Failed to client call"),
    CALL_RESULT_BODY_NIL(-102, "Call result body"),


    TOKEN_IS_INVALID(-200, "Token is invalid"),
    TOKEN_IS_EXPIRED(-201, "Token is expired"),
    ACCESS_TOKEN_IS_NOT_EXPIRED(-202, "Access token is not expired"),
    ACCESS_TOKEN_NEED(-203, "Access token need"),
    NOT_SUPPORTED_ROUTER(-204, "Not implemented"),

    AUTH_STATE_NOT_SUPPORTED(-300, "Auth state not supported"),

    GET_GOOGLE_TOKEN(-301, "Failed to get google token"),
    GET_GOOGLE_USER_INFO(-302, "Failed to get google user info"),
    GOOGLE_AUTH_CONFIG_NOT_FOUND(-303, "Google OAuth config not found"),
    GET_GITHUB_TOKEN(-304, "Failed to get github token"),
    GET_GITHUB_USER_INFO(-305, "Failed to get github user information"),
    GITHUB_AUTH_CONFIG_NOT_FOUND(-306, "GitHub Auth config not found"),


    FailedToInvokeWithMutex(-400, "Failed to invoke with mutex"),

}