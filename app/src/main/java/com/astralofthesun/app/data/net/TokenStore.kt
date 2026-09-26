package com.astralofthesun.app.data.net

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "astral_session")
private val TOKEN_KEY = stringPreferencesKey("astral_token")

/**
 * Persists the bearer token across app restarts. Direct counterpart to the
 * web client's `localStorage.getItem('astral:token')` — same role, same
 * "bearer token is the real credential, nothing else" design (see the
 * header comment in the web client's api.js for why: the session cookie
 * is SameSite=None on a third-party origin and gets dropped by browsers,
 * which doesn't apply to a native app anyway, so we only need the token).
 */
class TokenStore(private val context: Context) {

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }

    suspend fun getToken(): String? = context.dataStore.data.map { it[TOKEN_KEY] }.firstOrNull()

    suspend fun setToken(token: String?) {
        context.dataStore.edit { prefs ->
            if (token == null) prefs.remove(TOKEN_KEY) else prefs[TOKEN_KEY] = token
        }
    }
}
