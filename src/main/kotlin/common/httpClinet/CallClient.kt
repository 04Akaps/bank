package org.example.common.httpClinet

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.example.common.exception.CustomException
import org.example.common.exception.ErrorCode
import org.springframework.stereotype.Component

@Component
class CallClient(private val httpClient: OkHttpClient) {
    fun GET(uri: String, headers: Map<String, String> = emptyMap()): String {
        val requestBuilder = Request.Builder().url(uri)

        headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
        val request = requestBuilder.build()

        return resultHandler(httpClient.newCall(request).execute())
    }

    fun POST(uri: String, headers: Map<String, String> = emptyMap(), body : RequestBody): String {
        val requestBuilder = Request.Builder()
            .url(uri)
            .post(body)

        headers.forEach { (key, value) ->
            requestBuilder.addHeader(key, value)
        }

        return resultHandler(httpClient.newCall(requestBuilder.build()).execute())
    }


    private fun resultHandler(response : okhttp3.Response) : String {
        println(response.toString())
        response.use {
            if (!it.isSuccessful) {
                val msg = " HTTP ${it.code}: ${it.body?.string() ?: "Unknown error"}"
                throw CustomException(ErrorCode.FailedToClientCall, msg)
            }
            return it.body?.string() ?: throw CustomException(ErrorCode.CALL_RESULT_BODY_NIL)
        }
    }
}