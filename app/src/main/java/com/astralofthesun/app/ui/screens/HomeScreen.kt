package com.astralofthesun.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astralofthesun.app.R
import com.astralofthesun.app.data.Astral
import com.astralofthesun.app.data.CharacterEntry
import com.astralofthesun.app.data.LoadState
import com.astralofthesun.app.ui.components.Coin
import com.astralofthesun.app.ui.components.EmptyNote
import com.astralofthesun.app.ui.components.Gem
import com.astralofthesun.app.ui.components.SectionHeader
import com.astralofthesun.app.ui.components.StarRating
import com.astralofthesun.app.ui.theme.BorderSubtle
import com.astralofthesun.app.ui.theme.Primary
import com.astralofthesun.app.ui.theme.Surface
import com.astralofthesun.app.ui.theme.TextBright
import com.astralofthesun.app.ui.theme.TextDim

/**
 * Home tab. Player name/level/wallet, shop preview, top-5 board, Pokémon
 * party, and dungeons are all REAL data loaded from the bot's API (see
 * Astral.kt). Only the character roster below is still a client-side
 * placeholder (no backend endpoint for that system yet).
 */
@Composable
fun HomeScreen(
    goTopUp: (String) -> Unit,
    goDungeon: () -> Unit,
    goLeaderboard: () -> Unit,
    goPokemonRoster: () -> Unit,
    goCharacterDetail: (String) -> Unit,
) {
    val player by Astral.player
    val shop by Astral.shop
    val characters = Astral.characters
    val dungeons by Astral.dungeons
    val friends = Astral.friends
    val leaderboard by Astral.leaderboard
    val leaderboardLoad by Astral.leaderboardLoad
    val signedIn by Astral.isSignedIn

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // profile header — real data once signed in
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF111111))
                        .border(0.5.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp)),
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        player?.name ?: if (signedIn) "Loading…" else "Sign in to see your character",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    )
                    Text(
                        player?.let { "Level ${it.level}${it.title?.let { t -> " · $t" } ?: ""}" } ?: "—",
                        color = TextDim,
                        fontSize = 12.sp,
                    )
                }
            }
        }

        // stats row — real wallet from /api/me
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Level", player?.level?.toString() ?: "—", null)
                StatCard("Solars", player?.wallet?.solars?.toString() ?: "0", "coin")
                StatCard("Gems", player?.wallet?.gems?.toString() ?: "0", "gem")
            }
        }

        // hero banner — current event
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0A0A0A))
                    .border(0.5.dp, Color(0x1AFFFFFF), RoundedCornerShape(20.dp)),
            ) {
                Image(
                    painter = painterResource(R.drawable.event_beginning_of_the_end),
                    contentDescription = "The Beginning of the End — current event",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop,
                )
                // scrim so the label stays readable over the art
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC000000)),
                                startY = 40f,
                            )
                        )
                        .clip(RoundedCornerShape(20.dp)),
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp),
                ) {
                    Text(
                        "LIVE EVENT",
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                    )
                    Text(
                        "The Beginning of the End",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                }
            }
        }

        // top-up buttons (deep-link with currency preselected)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionHeader("Solars & Gems Top-up", "Top-up")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = { goTopUp("solars") }, modifier = Modifier.weight(1f)) {
                        Text("Solars Top-up")
                    }
                    Button(
                        onClick = { goTopUp("gems") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    ) { Text("Gems Top-up") }
                }
            }
        }

        // shop highlights — real catalog from /api/shop
        item { SectionHeader("Shop", "${shop.size} items") }
        item {
            if (shop.isEmpty()) EmptyNote("No items in the shop")
            else Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                shop.take(4).forEach { Text(it.name, fontSize = 13.sp) }
            }
        }

        // characters — real client-side roster, tap through to full detail
        item { SectionHeader("Your Characters", "${characters.size} recruited") }
        item {
            if (characters.isEmpty()) {
                EmptyNote("No characters recruited yet")
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(characters, key = { it.id }) { character ->
                        CharacterRosterCard(character, onClick = { goCharacterDetail(character.id) })
                    }
                }
            }
        }

        // dungeons — real data from /api/dungeon/list
        item { SectionHeader("Dungeons & Runs", "${dungeons.size} available") }
        item {
            if (dungeons.isEmpty()) {
                EmptyNote("No dungeons available")
            } else {
                Button(
                    onClick = goDungeon,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                ) { Text("Prepare for Dungeon") }
            }
        }

        // pokémon party — real data from /api/pokemon/party
        item {
            OutlinedButton(
                onClick = goPokemonRoster,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Your Pokémon") }
        }

        // top of the board — real leaderboard from /api/leaderboard
        item { SectionHeader("Top of the Board", "See all") }
        item {
            when {
                leaderboardLoad == LoadState.LOADING && leaderboard.isEmpty() ->
                    Box(Modifier.fillMaxWidth().padding(vertical = 20.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                leaderboard.isEmpty() -> EmptyNote("No standings yet")
                else -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    leaderboard.take(5).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("${row.rank}. ${row.name}", fontSize = 13.sp)
                            Text(row.value.toString(), color = TextDim, fontSize = 13.sp)
                        }
                    }
                    OutlinedButton(onClick = goLeaderboard, modifier = Modifier.fillMaxWidth()) {
                        Text("Full leaderboard")
                    }
                }
            }
        }

        // friends (placeholder — no backend yet)
        item { SectionHeader("Friends", "${friends.size}") }
        item { if (friends.isEmpty()) EmptyNote("No friends yet") }
    }
}

private fun rosterPortraitRes(spriteClass: String): Int = when (spriteClass) {
    "shinobi" -> R.drawable.portrait_shinobi_sm
    "samurai" -> R.drawable.portrait_samurai_sm
    "fighter" -> R.drawable.portrait_fighter_sm
    else -> R.drawable.portrait_fighter_sm
}

/** Small tappable character card for the Home roster row — portrait, name, stars. */
@Composable
private fun CharacterRosterCard(character: CharacterEntry, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFF0A0A0A)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(rosterPortraitRes(character.spriteClass)),
                contentDescription = character.name,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Fit,
            )
        }
        Text(
            character.name,
            color = TextBright,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        )
        Text("Lv. ${character.level}", color = TextDim, fontSize = 10.sp)
        StarRating(count = character.stars, sizeDp = 9)
    }
}

@Composable
private fun RowScope.StatCard(label: String, value: String, icon: String?) {
    Row(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF060606))
            .border(0.5.dp, Color(0x1AFFFFFF), RoundedCornerShape(14.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Column {
            Text(label, color = TextDim, fontSize = 10.sp)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                when (icon) {
                    "coin" -> Coin(12)
                    "gem" -> Gem(12)
                }
                Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
