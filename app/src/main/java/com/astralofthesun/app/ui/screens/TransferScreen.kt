package com.astralofthesun.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astralofthesun.app.ui.theme.Primary
import com.astralofthesun.app.ui.theme.TextDim

/**
 * Send solars/gems/items to another player by username. Not present on the
 * web frontend — new for the app. Backed by POST /api/transfer/solars,
 * /api/transfer/item, /api/transfer/character once those ship; this screen
 * mirrors the same "confirm the account before sending" pattern the bot's
 * login flow already uses, since transfers should never fire blind.
 */
@Composable
fun TransferScreen() {
    var username by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Send to a player", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("We'll show you who this is before anything sends.", color = TextDim, fontSize = 12.sp)

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it.filter { c -> c.isDigit() } },
            label = { Text("Amount (Solars)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Button(
            onClick = { /* wire to GET /api/transfer/lookup, then POST /api/transfer/solars */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            enabled = username.isNotBlank() && amount.isNotBlank(),
        ) { Text("Find account") }
    }
}
