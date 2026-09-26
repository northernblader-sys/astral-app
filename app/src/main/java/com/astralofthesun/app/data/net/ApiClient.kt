package com.astralofthesun.app.data.net

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

/**
 * Real HTTP client for the Astral bot's API — a direct Kotlin port of the
 * web client's `request()` in main-sitr/assets/js/api.js: same base URL,
 * same bearer-token-first auth, same error shape ({ error|message, code,
 * retryAfterMs }), same "401 drops the token" behavior, same 12s timeout.
 * Every call this makes hits astral-bot-production-afb0.up.railway.app for
 * real — nothing in this file is mocked.
 */
class ApiClient(context: Context) {

    val tokenStore = TokenStore(context.applicationContext)

    private val http = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .writeTimeout(12, TimeUnit.SECONDS)
        .build()

    val json = Json { ignoreUnknownKeys = true; explicitNulls = false; encodeDefaults = true }

    companion object {
        const val BASE_URL = "https://astral-bot-production-afb0.up.railway.app"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }

    /** Called on a 401 so the caller (repo layer) can route back to sign-in. */
    var onAuthLost: (suspend () -> Unit)? = null

    suspend inline fun <reified T> get(path: String, auth: Boolean = true): T =
        json.decodeFromJsonElement(rawRequest(path, "GET", null, auth))

    suspend inline fun <reified B, reified T> post(path: String, body: B, auth: Boolean = true): T {
        val bodyJson = json.encodeToJsonElement(body)
        return json.decodeFromJsonElement(rawRequest(path, "POST", bodyJson, auth))
    }

    suspend inline fun <reified T> postNoBody(path: String, auth: Boolean = true): T =
        json.decodeFromJsonElement(rawRequest(path, "POST", JsonObject(emptyMap()), auth))

    suspend fun rawRequest(
        path: String,
        method: String,
        bodyJson: JsonElement?,
        auth: Boolean,
    ): JsonElement = withContext(Dispatchers.IO) {
        val token = if (auth) tokenStore.getToken() else null

        val builder = Request.Builder().url("$BASE_URL/api$path")
        builder.header("Accept", "application/json")
        if (token != null) builder.header("Authorization", "Bearer $token")

        val body = bodyJson?.let { json.encodeToString(JsonElement.serializer(), it).toRequestBody(JSON_MEDIA_TYPE) }
        when (method) {
            "GET" -> builder.get()
            "DELETE" -> builder.delete()
            "POST" -> builder.post(body ?: "{}".toRequestBody(JSON_MEDIA_TYPE))
            "PATCH" -> builder.patch(body ?: "{}".toRequestBody(JSON_MEDIA_TYPE))
        }

        val response = try {
            http.newCall(builder.build()).execute()
        } catch (e: IOException) {
            val timedOut = e is SocketTimeoutException
            throw ApiError(
                if (timedOut) "The server took too long to respond." else "Cannot reach the server.",
                status = 0,
                code = if (timedOut) "timeout" else "network",
            )
        }

        val text = response.use { it.body?.string().orEmpty() }
        val payload: JsonElement? = if (text.isNotBlank()) {
            try { json.parseToJsonElement(text) } catch (_: Exception) { null }
        } else null

        if (!response.isSuccessful) {
            if (response.code == 401) {
                tokenStore.setToken(null)
                onAuthLost?.invoke()
            }
            val obj = (payload as? JsonObject)
            val message = obj?.get("error")?.jsonPrimitive?.contentOrNull
                ?: obj?.get("message")?.jsonPrimitive?.contentOrNull
                ?: "Request failed (${response.code})"
            val code = obj?.get("code")?.jsonPrimitive?.contentOrNull
            val retryAfter = obj?.get("retryAfterMs")?.jsonPrimitive?.contentOrNull?.toLongOrNull()
            throw ApiError(message, status = response.code, code = code, retryAfterMs = retryAfter)
        }

        payload ?: JsonObject(emptyMap())
    }
}
