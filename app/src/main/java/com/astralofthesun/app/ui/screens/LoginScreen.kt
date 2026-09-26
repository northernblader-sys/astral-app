package com.astralofthesun.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astralofthesun.app.data.Astral
import com.astralofthesun.app.data.net.ApiError
import com.astralofthesun.app.ui.theme.Danger
import com.astralofthesun.app.ui.theme.Primary
import com.astralofthesun.app.ui.theme.TextDim
import kotlinx.coroutines.launch

private enum class LoginStep { USERNAME, CONFIRM, OTP }

/**
 * REAL sign-in flow: username lookup -> confirm the masked account is yours
 * -> OTP sent to that account's WhatsApp -> verify. Every step here calls
 * the real /auth/lookup, /auth/request-otp, /auth/verify-otp endpoints —
 * matches the web client's login flow exactly (see api.js lookupAccount /
 * requestOtp / verifyOtp), including the "handle" indirection so the app
 * never sees anyone's real phone number.
 */
@Composable
fun LoginScreen(onSignedIn: () -> Unit) {
    var step by remember { mutableStateOf(LoginStep.USERNAME) }
    var username by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    var foundName by remember { mutableStateOf("") }
    var maskedPhone by remember { mutableStateOf("") }
    var handle by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Sign in", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        when (step) {
            LoginStep.USERNAME -> {
                Text("Enter your character's name.", color = TextDim, fontSize = 12.sp)
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                error?.let { Text(it, color = Danger, fontSize = 12.sp) }
                Button(
                    onClick = {
                        loading = true; error = null
                        scope.launch {
                            try {
                                val res = Astral.lookupAccount(username)
                                if (res.found && res.handle != null) {
                                    foundName = res.name ?: username
                                    maskedPhone = res.maskedPhone ?: ""
                                    handle = res.handle
                                    step = LoginStep.CONFIRM
                                } else {
                                    error = res.error ?: "No character found with that name."
                                }
                            } catch (e: ApiError) {
                                error = e.message
                            } finally {
                                loading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    enabled = username.isNotBlank() && !loading,
                ) {
                    if (loading) CircularProgressIndicator(modifier = Modifier.padding(2.dp))
                    else Text("Continue")
                }
            }

            LoginStep.CONFIRM -> {
                Text("Is this you?", color = TextDim, fontSize = 12.sp)
                Text(foundName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(maskedPhone, color = TextDim, fontSize = 13.sp)
                error?.let { Text(it, color = Danger, fontSize = 12.sp) }
                Button(
                    onClick = {
                        loading = true; error = null
                        scope.launch {
                            try {
                                Astral.requestOtpLogin(handle)
                                step = LoginStep.OTP
                            } catch (e: ApiError) {
                                error = e.message
                            } finally {
                                loading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    enabled = !loading,
                ) {
                    if (loading) CircularProgressIndicator(modifier = Modifier.padding(2.dp))
                    else Text("Send code to WhatsApp")
                }
            }

            LoginStep.OTP -> {
                Text("Enter the 6-digit code sent to your WhatsApp.", color = TextDim, fontSize = 12.sp)
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.filter { c -> c.isDigit() }.take(6) },
                    label = { Text("Code") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                error?.let { Text(it, color = Danger, fontSize = 12.sp) }
                Button(
                    onClick = {
                        loading = true; error = null
                        scope.launch {
                            try {
                                val ok = Astral.verifyOtpLogin(handle, code)
                                if (ok) onSignedIn() else error = "Incorrect code."
                            } catch (e: ApiError) {
                                error = e.message
                            } finally {
                                loading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    enabled = code.length == 6 && !loading,
                ) {
                    if (loading) CircularProgressIndicator(modifier = Modifier.padding(2.dp))
                    else Text("Verify & sign in")
                }
            }
        }
    }
}
