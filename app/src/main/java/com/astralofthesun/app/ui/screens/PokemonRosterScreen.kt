package com.astralofthesun.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astralofthesun.app.data.Astral
import com.astralofthesun.app.data.LoadState
import com.astralofthesun.app.data.net.dto.MonDto
import com.astralofthesun.app.ui.components.AstralCard
import com.astralofthesun.app.ui.components.EmptyNote
import com.astralofthesun.app.ui.theme.Danger
import com.astralofthesun.app.ui.theme.Primary
import com.astralofthesun.app.ui.theme.Success
import com.astralofthesun.app.ui.theme.Surface
import com.astralofthesun.app.ui.theme.TextDim

private val TRAINABLE_STATS = listOf("hp", "atk", "def", "spa", "spd", "spe")

/**
 * Real Pokémon roster — GET /api/pokemon/party, backed entirely by
 * lib/api-pokemon.js. Replaces the old PvP battle stub (removed — there's
 * no PvP in this app). Feed/heal/party actions plus train/release/rename/
 * protect/give all fire real mutations through Astral and reload the party
 * after. The last four were wired server-side (repo + Astral state) but had
 * no caller anywhere in the UI until this pass.
 */
@Composable
fun PokemonRosterScreen() {
    val party by Astral.party
    val mainMon by Astral.mainMon
    val partyMax by Astral.partyMax
    val ownedCount by Astral.ownedCount
    val load by Astral.partyLoad
    val actionMessage by Astral.pokemonActionMessage
    val error by Astral.lastError

    val snackbarHostState = remember { SnackbarHostState() }

    var renameTarget by remember { mutableStateOf<MonDto?>(null) }
    var trainTarget by remember { mutableStateOf<MonDto?>(null) }
    var giveTarget by remember { mutableStateOf<MonDto?>(null) }
    var releaseTarget by remember { mutableStateOf<MonDto?>(null) }

    LaunchedEffect(Unit) {
        if (load == LoadState.IDLE) Astral.loadParty()
    }

    LaunchedEffect(actionMessage) {
        actionMessage?.let {
            snackbarHostState.showSnackbar(it)
            Astral.pokemonActionMessage.value = null
        }
    }
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            Astral.lastError.value = null
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) { Snackbar(it) } },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Your Pokémon", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("${party.size}/$partyMax in party · $ownedCount owned", color = TextDim, fontSize = 12.sp)
                }
            }

            item {
                Button(
                    onClick = { Astral.healParty() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                ) { Text("Heal Party (free)") }
            }

            when {
                load == LoadState.LOADING && party.isEmpty() -> item {
                    Box(Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                party.isEmpty() -> item { EmptyNote("No Pokémon in your party yet") }
                else -> items(party, key = { it.id }) { mon ->
                    MonRow(
                        mon = mon,
                        isMain = mainMon?.id == mon.id,
                        onRename = { renameTarget = mon },
                        onTrain = { trainTarget = mon },
                        onGive = { giveTarget = mon },
                        onRelease = { releaseTarget = mon },
                    )
                }
            }
        }
    }

    renameTarget?.let { mon ->
        RenameDialog(
            mon = mon,
            onDismiss = { renameTarget = null },
            onConfirm = { nickname -> Astral.renameMon(mon.id, nickname); renameTarget = null },
        )
    }
    trainTarget?.let { mon ->
        TrainDialog(
            mon = mon,
            onDismiss = { trainTarget = null },
            onConfirm = { stat -> Astral.trainMon(mon.id, stat); trainTarget = null },
        )
    }
    giveTarget?.let { mon ->
        GiveDialog(
            mon = mon,
            onDismiss = { giveTarget = null },
            onConfirm = { username -> Astral.giveMon(mon.id, username); giveTarget = null },
        )
    }
    releaseTarget?.let { mon ->
        ReleaseConfirmDialog(
            mon = mon,
            onDismiss = { releaseTarget = null },
            onConfirm = { Astral.releaseMon(mon.id); releaseTarget = null },
        )
    }
}

@Composable
private fun MonRow(
    mon: MonDto,
    isMain: Boolean,
    onRename: () -> Unit,
    onTrain: () -> Unit,
    onGive: () -> Unit,
    onRelease: () -> Unit,
) {
    AstralCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                (mon.nickname ?: mon.name) + if (mon.shiny) " ✦" else "",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            )
            if (isMain) Text("MAIN", color = Success, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Text(
            "Lv. ${mon.level} · ${mon.types.joinToString(" / ")}" + if (mon.protected) " · protected" else "",
            color = TextDim,
            fontSize = 12.sp,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Surface),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth((mon.hp.toFloat() / mon.maxHp.coerceAtLeast(1)).coerceIn(0f, 1f))
                    .background(if (mon.fainted) Danger else Primary),
            )
        }
        Text("${mon.hp}/${mon.maxHp} HP", color = TextDim, fontSize = 11.sp)

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = { Astral.feedMon(mon.id) },
                modifier = Modifier.weight(1f),
                enabled = !mon.fainted,
            ) { Text("Feed", fontSize = 12.sp) }

            if (mon.inParty) {
                OutlinedButton(
                    onClick = { Astral.removeFromParty(mon.id) },
                    modifier = Modifier.weight(1f),
                ) { Text("Remove", fontSize = 12.sp) }
            } else {
                OutlinedButton(
                    onClick = { Astral.addToParty(mon.id) },
                    modifier = Modifier.weight(1f),
                ) { Text("Add to Party", fontSize = 12.sp) }
            }

            if (!isMain) {
                OutlinedButton(
                    onClick = { Astral.setMainMon(mon.id) },
                    modifier = Modifier.weight(1f),
                ) { Text("Set Main", fontSize = 12.sp) }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = onTrain,
                modifier = Modifier.weight(1f),
                enabled = !mon.fainted,
            ) { Text("Train", fontSize = 12.sp) }

            OutlinedButton(
                onClick = onRename,
                modifier = Modifier.weight(1f),
            ) { Text("Rename", fontSize = 12.sp) }

            OutlinedButton(
                onClick = { Astral.protectMon(mon.id) },
                modifier = Modifier.weight(1f),
            ) { Text(if (mon.protected) "Unprotect" else "Protect", fontSize = 12.sp) }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = onGive,
                modifier = Modifier.weight(1f),
                enabled = !mon.protected,
            ) { Text("Give", fontSize = 12.sp) }

            OutlinedButton(
                onClick = onRelease,
                modifier = Modifier.weight(1f),
                enabled = !mon.protected,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Danger),
            ) { Text("Release", fontSize = 12.sp) }
        }
        if (mon.protected) {
            Text(
                "Unprotect to give away or release this Pokémon",
                color = TextDim,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun RenameDialog(mon: MonDto, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var nickname by rememberSaveable(mon.id) { mutableStateOf(mon.nickname ?: mon.name) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename ${mon.nickname ?: mon.name}") },
        text = {
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("Nickname") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(nickname.trim()) },
                enabled = nickname.isNotBlank(),
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

/** Stat picker — one row of buttons per stat, no confirm step since tapping a stat trains it immediately. */
@Composable
private fun TrainDialog(mon: MonDto, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Train ${mon.nickname ?: mon.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Choose a stat to invest in:", color = TextDim, fontSize = 12.sp)
                TRAINABLE_STATS.chunked(3).forEach { rowStats ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        rowStats.forEach { stat ->
                            OutlinedButton(
                                onClick = { onConfirm(stat) },
                                modifier = Modifier.weight(1f),
                            ) { Text(stat.uppercase(), fontSize = 12.sp) }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun GiveDialog(mon: MonDto, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var username by rememberSaveable(mon.id) { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Give ${mon.nickname ?: mon.name} away") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("This transfers ownership immediately — it can't be undone.", color = TextDim, fontSize = 12.sp)
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Recipient username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(username.trim()) },
                enabled = username.isNotBlank(),
            ) { Text("Give") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun ReleaseConfirmDialog(mon: MonDto, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Release ${mon.nickname ?: mon.name}?") },
        text = { Text("This permanently removes it from your collection. This can't be undone.", color = TextDim) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Release", color = Danger) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
