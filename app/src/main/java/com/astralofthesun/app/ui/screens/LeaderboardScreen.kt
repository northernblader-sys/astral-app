package com.astralofthesun.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astralofthesun.app.data.Astral
import com.astralofthesun.app.ui.components.AstralCard
import com.astralofthesun.app.ui.components.EmptyNote
import com.astralofthesun.app.ui.theme.TextDim

@Composable
fun LeaderboardScreen(goPlayerDetail: (String) -> Unit) {
    val rows by Astral.leaderboard

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item { Text("Leaderboard", fontWeight = FontWeight.Bold, fontSize = 20.sp) }

        if (rows.isEmpty()) {
            item { EmptyNote("Loading the standings…") }
        } else {
            items(rows) { row ->
                AstralCard(onClick = { goPlayerDetail(row.uid) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("#${row.rank}  ${row.name}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(row.value.toString(), color = TextDim, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
