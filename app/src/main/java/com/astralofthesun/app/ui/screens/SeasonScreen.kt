package com.astralofthesun.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astralofthesun.app.data.Astral
import com.astralofthesun.app.ui.components.AstralCard
import com.astralofthesun.app.ui.components.SectionHeader
import com.astralofthesun.app.ui.theme.TextDim

/** Season name/number is REAL data from /api/season. Player's own tier progress is real too, from /api/me's player.season. */
@Composable
fun SeasonScreen(
    goTierDetail: (String) -> Unit,
    goSpinBanner: () -> Unit,
) {
    val season by Astral.season
    val player by Astral.player
    val progress = player?.season

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // season hero
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0A0A0A))
                    .border(0.5.dp, Color(0x1AFFFFFF), RoundedCornerShape(20.dp)),
            )
        }

        item {
            Text(season?.name ?: "Loading season…", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            season?.number?.let { Text("Season $it", color = TextDim, fontSize = 13.sp) }
        }

        item { SectionHeader("Battle pass progress", "Free & premium") }
        item {
            AstralCard {
                Text(
                    "Tier ${progress?.tier ?: 0} of ${progress?.tierCount ?: 50}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
                Text("Keep playing to unlock the next reward", color = TextDim, fontSize = 12.sp)
            }
        }

        item { SectionHeader("Tier rewards", "") }
        items(6) { i ->
            AstralCard(onClick = { goTierDetail((i + 1).toString()) }) {
                Text("Tier ${i + 1}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Tap to see free & premium rewards", color = TextDim, fontSize = 12.sp)
            }
        }

        item { SectionHeader("Spin banner", "Pull for a character") }
        item {
            AstralCard(onClick = goSpinBanner) {
                Text("Spin for this season's exclusive", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Cannot be bought at any price", color = TextDim, fontSize = 12.sp)
            }
        }
    }
}
