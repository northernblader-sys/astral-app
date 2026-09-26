package com.astralofthesun.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
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
import com.astralofthesun.app.data.Astral
import com.astralofthesun.app.ui.components.AstralCard
import com.astralofthesun.app.ui.components.EmptyNote
import com.astralofthesun.app.ui.theme.TextDim

private enum class SortMode { NAME, RARITY, TYPE }

/**
 * REAL inventory — player.inventory from /api/me, which the server already
 * returns stacked with quantities (see stackInventory() in api-server.js).
 * Sort/filter here is purely client-side over that real list.
 */
@Composable
fun InventoryScreen() {
    var sortMode by remember { mutableStateOf(SortMode.NAME) }
    val player by Astral.player
    val items = player?.inventory ?: emptyList()
    val sorted = when (sortMode) {
        SortMode.NAME -> items.sortedBy { it.name }
        SortMode.RARITY -> items.sortedBy { it.rarity ?: "" }
        SortMode.TYPE -> items.sortedBy { it.type ?: "" }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item { Text("Inventory", fontWeight = FontWeight.Bold, fontSize = 20.sp) }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = sortMode == SortMode.NAME, onClick = { sortMode = SortMode.NAME }, label = { Text("Name") })
                FilterChip(selected = sortMode == SortMode.RARITY, onClick = { sortMode = SortMode.RARITY }, label = { Text("Rarity") })
                FilterChip(selected = sortMode == SortMode.TYPE, onClick = { sortMode = SortMode.TYPE }, label = { Text("Type") })
            }
        }

        if (sorted.isEmpty()) {
            item { EmptyNote("Nothing in your inventory") }
        } else {
            items(sorted) { item ->
                AstralCard {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier) {
                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Text(
                        listOfNotNull(item.type, item.rarity).joinToString(" · ").ifEmpty { "—" } + "  ×${item.qty}",
                        color = TextDim,
                        fontSize = 11.sp,
                    )
                }
            }
        }
    }
}
