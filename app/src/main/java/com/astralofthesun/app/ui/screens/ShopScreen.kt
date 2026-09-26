package com.astralofthesun.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.astralofthesun.app.data.net.dto.ShopItemDto
import com.astralofthesun.app.ui.components.AstralCard
import com.astralofthesun.app.ui.components.Coin
import com.astralofthesun.app.ui.components.EmptyNote
import com.astralofthesun.app.ui.components.Gem
import com.astralofthesun.app.ui.components.SectionHeader
import com.astralofthesun.app.ui.theme.TextDim

/**
 * Shop tab. Catalog browsing only — no in-app purchase flow (see the
 * confirmed constraint: purchases stay chat/web-only for now). Tapping a
 * row still opens item detail so the app is a real catalog, not just a list.
 */
@Composable
fun ShopScreen(
    goItemDetail: (String) -> Unit,
    goCardVault: () -> Unit,
    goPremium: () -> Unit,
) {
    val shop by Astral.shop
    val cardTiers by Astral.cardTiers

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { Text("Shop", fontWeight = FontWeight.Bold, fontSize = 20.sp) }

        item { SectionHeader("Catalog", "${shop.size} items") }
        if (shop.isEmpty()) {
            item { EmptyNote("Nothing in the shop right now") }
        } else {
            items(shop) { item -> ShopRow(item, onClick = { goItemDetail(item.id) }) }
        }

        item { SectionHeader("Card Vault", "${cardTiers.size} tiers") }
        item {
            AstralCard(onClick = goCardVault) {
                Text("Browse the card vault", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Buy a guaranteed tier — the card itself is a random pull", color = TextDim, fontSize = 12.sp)
            }
        }

        item { SectionHeader("Premium", "") }
        item {
            AstralCard(onClick = goPremium) {
                Text("Premium plans & gem packages", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Optional — the whole game is playable without it", color = TextDim, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ShopRow(item: ShopItemDto, onClick: () -> Unit) {
    AstralCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(item.category, color = TextDim, fontSize = 11.sp)
            }
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                when {
                    item.priceSolars != null -> {
                        Coin(12)
                        Text(" ${item.priceSolars}", fontSize = 13.sp)
                    }
                    item.priceGems != null -> {
                        Gem(12)
                        Text(" ${item.priceGems}", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
