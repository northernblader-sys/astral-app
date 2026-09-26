package com.astralofthesun.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astralofthesun.app.data.Astral
import com.astralofthesun.app.data.LoadState
import com.astralofthesun.app.data.net.dto.DungeonListEntryDto
import com.astralofthesun.app.ui.components.AstralCard
import com.astralofthesun.app.ui.components.EmptyNote
import com.astralofthesun.app.ui.theme.Danger
import com.astralofthesun.app.ui.theme.Primary
import com.astralofthesun.app.ui.theme.Success
import com.astralofthesun.app.ui.theme.TextDim

/**
 * Dungeon select — real data from GET /api/dungeon/list. Not present on the
 * web frontend at all; new for the Android app. Tapping an unlocked dungeon
 * pushes DungeonBattleScreen; a locked one shows its requirement instead.
 */
@Composable
fun DungeonSelectScreen(goBattle: (String) -> Unit) {
    val dungeons by Astral.dungeons
    val load by Astral.dungeonsLoad

    LaunchedEffect(Unit) {
        if (load == LoadState.IDLE) Astral.loadDungeons()
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item { Text("Choose a dungeon", fontWeight = FontWeight.Bold, fontSize = 20.sp) }

        when {
            load == LoadState.LOADING && dungeons.isEmpty() -> item {
                Box(Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            }
            dungeons.isEmpty() -> item { EmptyNote("No dungeons available") }
            else -> items(dungeons, key = { it.id }) { d ->
                DungeonRow(d, onClick = { if (d.unlocked) goBattle(d.id) })
            }
        }
    }
}

@Composable
private fun DungeonRow(d: DungeonListEntryDto, onClick: () -> Unit) {
    AstralCard(onClick = onClick) {
        Text(d.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(
            when {
                !d.unlocked -> d.prerequisiteName?.let { "Locked — clear $it first" } ?: "Locked"
                d.conquered -> "Conquered · highest floor ${d.highestFloor}"
                else -> "Highest floor ${d.highestFloor}" + (d.floors?.let { " / $it" } ?: "")
            },
            color = if (!d.unlocked) Danger else if (d.conquered) Success else TextDim,
            fontSize = 12.sp,
        )
    }
}
