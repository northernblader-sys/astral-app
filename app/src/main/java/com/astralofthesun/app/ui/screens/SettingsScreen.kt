package com.astralofthesun.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astralofthesun.app.data.Astral
import com.astralofthesun.app.ui.components.AstralCard
import com.astralofthesun.app.ui.components.SectionHeader
import com.astralofthesun.app.ui.theme.Danger
import com.astralofthesun.app.ui.theme.TextDim

@Composable
fun SettingsScreen(goLogin: () -> Unit) {
    val signedIn by Astral.isSignedIn

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { Text("Settings", fontWeight = FontWeight.Bold, fontSize = 20.sp) }

        item { SectionHeader("Account", "") }
        item {
            AstralCard {
                Text(if (signedIn) "Signed in" else "Not signed in", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    if (signedIn) "Manage your session below" else "Sign in to sync your character",
                    color = TextDim,
                    fontSize = 12.sp,
                )
            }
        }

        item { SectionHeader("Preferences", "") }
        item {
            AstralCard {
                Text("Notifications", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Hidden from leaderboard, muted alerts, DM toggle", color = TextDim, fontSize = 12.sp)
            }
        }

        item {
            if (signedIn) {
                Button(
                    onClick = { Astral.signOut() },
                    colors = ButtonDefaults.buttonColors(containerColor = Danger),
                ) { Text("Sign out") }
            } else {
                Button(onClick = goLogin) { Text("Sign in") }
            }
        }
    }
}
