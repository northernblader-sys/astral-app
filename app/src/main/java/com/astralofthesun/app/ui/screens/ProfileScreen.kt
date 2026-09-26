package com.astralofthesun.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astralofthesun.app.data.Astral
import com.astralofthesun.app.ui.components.AstralCard
import com.astralofthesun.app.ui.components.EmptyNote
import com.astralofthesun.app.ui.components.SectionHeader
import com.astralofthesun.app.ui.theme.TextDim

/** Profile header and wallet are REAL data from /api/me. Notifications are placeholder. */
@Composable
fun ProfileScreen(
    goInventory: () -> Unit,
    goTransfer: () -> Unit,
    goNotifications: () -> Unit,
) {
    val player by Astral.player
    val signedIn by Astral.isSignedIn
    val notifications = Astral.notifications
    val unreadCount = notifications.count { !it.read }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF111111))
                        .border(0.5.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp)),
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        player?.name ?: if (signedIn) "Loading…" else "Not signed in",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                    Text(
                        player?.rank?.title ?: "—",
                        color = TextDim,
                        fontSize = 13.sp,
                    )
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileStat("Level", player?.level?.toString() ?: "—")
                ProfileStat("Floor", player?.state?.floor?.toString() ?: "—")
            }
        }

        item { SectionHeader("Inventory", "") }
        item {
            AstralCard(onClick = goInventory) {
                Text("Open inventory", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Sort and filter everything you own", color = TextDim, fontSize = 12.sp)
            }
        }

        item { SectionHeader("Transfer", "") }
        item {
            AstralCard(onClick = goTransfer) {
                Text("Send solars, gems or items", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("To another player by username", color = TextDim, fontSize = 12.sp)
            }
        }

        item { SectionHeader("Notifications", if (unreadCount > 0) "$unreadCount unread" else "All caught up") }
        item {
            AstralCard(onClick = goNotifications) {
                if (notifications.isEmpty()) {
                    Text("No notifications", color = TextDim, fontSize = 13.sp)
                } else {
                    Text(notifications.first().title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(notifications.first().body, color = TextDim, fontSize = 12.sp)
                }
            }
        }

        item { SectionHeader("Spin & collection history", "") }
        item { EmptyNote("No spins yet") }
    }
}

@Composable
private fun ProfileStat(label: String, value: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF060606))
            .border(0.5.dp, Color(0x1AFFFFFF), RoundedCornerShape(14.dp))
            .padding(12.dp),
    ) {
        Text(label, color = TextDim, fontSize = 10.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
