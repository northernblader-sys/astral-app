package com.astralofthesun.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astralofthesun.app.data.Astral
import com.astralofthesun.app.data.LoadState
import com.astralofthesun.app.data.net.dto.DungeonStateDto
import com.astralofthesun.app.ui.theme.Danger
import com.astralofthesun.app.ui.theme.Primary
import com.astralofthesun.app.ui.theme.Surface
import com.astralofthesun.app.ui.theme.TextDim

/**
 * Real dungeon combat — drives POST /api/dungeon/enter, /action, /leave via
 * Astral. Handles both floor types the backend returns: "solo" (attack /
 * skill / defend / flee) and "swarm" (directional attack via al/ar, move
 * via ml/mr, dodge, skill with an optional target). The action set shown
 * is always exactly `state.actions` from the server — nothing is guessed.
 */
@Composable
fun DungeonBattleScreen(dungeonId: String, onLeave: () -> Unit) {
    val state by Astral.dungeonState
    val log by Astral.dungeonLog
    val load by Astral.dungeonLoad

    LaunchedEffect(dungeonId) {
        Astral.enterDungeon(dungeonId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            state?.dungeonName ?: dungeonId.replace('_', ' ').replaceFirstChar { it.uppercase() },
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )

        if (state != null) {
            Text(
                "Floor ${state!!.floor}" + (state!!.totalFloors?.let { " / $it" } ?: "") +
                    if (state!!.isBossFloor) " · Boss" else "",
                color = TextDim,
                fontSize = 12.sp,
            )
        }

        if (load == LoadState.LOADING && state == null) {
            Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (state != null) {
            val s = state!!
            if (s.mode == "swarm" && !s.monsters.isNullOrEmpty()) {
                SwarmPanel(s)
            } else {
                SoloPanel(s)
            }

            // player panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .padding(14.dp),
            ) {
                Text(s.player.name.ifBlank { "You" } + " · Lv. ${s.player.level}", color = TextDim, fontSize = 11.sp)
                Spacer(Modifier.height(8.dp))
                HealthBar(fraction = s.player.hp.toFloat() / s.player.maxHp.coerceAtLeast(1))
                Text("${s.player.hp}/${s.player.maxHp} HP", color = TextDim, fontSize = 11.sp)
                if (s.player.maxMp > 0) {
                    Text("${s.player.mp}/${s.player.maxMp} MP", color = TextDim, fontSize = 11.sp)
                }
            }

            Spacer(Modifier.height(4.dp))

            ActionButtons(s, onLeave)

            if (log.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Surface)
                        .padding(12.dp),
                ) {
                    log.takeLast(4).forEach { line ->
                        Text(line, color = TextDim, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SoloPanel(s: DungeonStateDto) {
    val enemy = s.enemy
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .padding(14.dp),
    ) {
        Text("Enemy", color = TextDim, fontSize = 11.sp)
        Text(
            (enemy?.emoji?.let { "$it " } ?: "") + (enemy?.name ?: "—"),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
        Spacer(Modifier.height(8.dp))
        HealthBar(fraction = (enemy?.hp?.toFloat() ?: 0f) / (enemy?.maxHp?.coerceAtLeast(1) ?: 1))
        if (enemy != null) Text("${enemy.hp}/${enemy.maxHp} HP", color = TextDim, fontSize = 11.sp)
    }
}

@Composable
private fun SwarmPanel(s: DungeonStateDto) {
    val monsters = s.monsters.orEmpty()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .padding(14.dp),
    ) {
        Text("Swarm · your lane: ${s.playerLane ?: 0}", color = TextDim, fontSize = 11.sp)
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(monsters, key = { it.uid }) { m ->
                if (m.alive) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("${m.emoji ?: ""} ${m.name} (lane ${m.lane})", fontSize = 13.sp)
                        Text("${m.hp}/${m.maxHp}", color = TextDim, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(s: DungeonStateDto, onLeave: () -> Unit) {
    val actions = s.actions
    if (actions.isEmpty()) {
        Button(
            onClick = { Astral.leaveDungeon(onLeave) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
        ) { Text(if (s.canAdvance) "Advance to next floor" else "Leave") }
        if (s.canAdvance) {
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { Astral.leaveDungeon(onLeave) },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Leave dungeon") }
        }
        return
    }

    val swarm = s.mode == "swarm"
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (swarm) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if ("al" in actions) OutlinedButton(
                    onClick = { Astral.dungeonAction("attack", direction = "left") },
                    modifier = Modifier.weight(1f),
                ) { Text("Attack ←") }
                if ("ar" in actions) OutlinedButton(
                    onClick = { Astral.dungeonAction("attack", direction = "right") },
                    modifier = Modifier.weight(1f),
                ) { Text("Attack →") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if ("ml" in actions) OutlinedButton(
                    onClick = { Astral.dungeonAction("move", direction = "left") },
                    modifier = Modifier.weight(1f),
                ) { Text("Move ←") }
                if ("mr" in actions) OutlinedButton(
                    onClick = { Astral.dungeonAction("move", direction = "right") },
                    modifier = Modifier.weight(1f),
                ) { Text("Move →") }
                if ("dodge" in actions) OutlinedButton(
                    onClick = { Astral.dungeonAction("dodge") },
                    modifier = Modifier.weight(1f),
                ) { Text("Dodge") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if ("skill" in actions) OutlinedButton(
                    onClick = { Astral.dungeonAction("skill") },
                    modifier = Modifier.weight(1f),
                ) { Text("Skill") }
                OutlinedButton(
                    onClick = { Astral.leaveDungeon(onLeave) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Danger),
                ) { Text("Flee") }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if ("attack" in actions) Button(
                    onClick = { Astral.dungeonAction("attack") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                ) { Text("Attack") }
                if ("skill" in actions) OutlinedButton(
                    onClick = { Astral.dungeonAction("skill") },
                    modifier = Modifier.weight(1f),
                ) { Text("Skill") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if ("defend" in actions) OutlinedButton(
                    onClick = { Astral.dungeonAction("defend") },
                    modifier = Modifier.weight(1f),
                ) { Text("Defend") }
                if ("flee" in actions) OutlinedButton(
                    onClick = { Astral.dungeonAction("flee") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Danger),
                ) { Text("Flee") }
            }
        }
    }
}

@Composable
private fun HealthBar(fraction: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(androidx.compose.ui.graphics.Color(0xFF1A1A1A)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .background(Primary),
        )
    }
}
