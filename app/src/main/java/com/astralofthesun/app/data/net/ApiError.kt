package com.astralofthesun.app.data.net

/**
 * Mirrors the web client's ApiError: { message, status, code, retryAfterMs }.
 * `status = 0` means the request never reached the server (network/timeout) —
 * same convention as the web client, so error handling logic reads the
 * same way on both platforms.
 */
class ApiError(
    message: String,
    val status: Int,
    val code: String? = null,
    val retryAfterMs: Long? = null,
) : Exception(message)
