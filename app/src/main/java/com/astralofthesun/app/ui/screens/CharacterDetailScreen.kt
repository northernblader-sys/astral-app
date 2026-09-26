package com.astralofthesun.app.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astralofthesun.app.R
import com.astralofthesun.app.data.CharacterEntry
import com.astralofthesun.app.data.CharacterSkill
import com.astralofthesun.app.ui.components.AstralCard
import com.astralofthesun.app.ui.components.StarRating
import com.astralofthesun.app.ui.theme.Background
import com.astralofthesun.app.ui.theme.BorderSubtle
import com.astralofthesun.app.ui.theme.Danger
import com.astralofthesun.app.ui.theme.GemBlue
import com.astralofthesun.app.ui.theme.Primary
import com.astralofthesun.app.ui.theme.Surface
import com.astralofthesun.app.ui.theme.SurfaceRaised
import com.astralofthesun.app.ui.theme.TextBright
import com.astralofthesun.app.ui.theme.TextDim

/** Which element an Astral belongs to and what color to render it in. */
private fun elementColor(element: String): Color = when (element.lowercase()) {
    "shadow" -> Color(0xFF8B7FD6)
    "radiant" -> Color(0xFFE9C46A)
    "stone" -> Color(0xFF7FA88A)
    else -> Primary
}

private fun portraitRes(spriteClass: String): Int = when (spriteClass) {
    "shinobi" -> R.drawable.portrait_shinobi
    "samurai" -> R.drawable.portrait_samurai
    "fighter" -> R.drawable.portrait_fighter
    else -> R.drawable.portrait_fighter
}

/**
 * Full character sheet: portrait with a slow-pulsing elemental glow, star
 * rarity, level progress, core stats as bars, a lore passage with the
 * character's signature line, and their skill kit. Pulls from
 * Astral.characters — no backend endpoint needed since this is entirely
 * client-side flavor + placeholder progression data.
 */
@Composable
fun CharacterDetailScreen(character: CharacterEntry) {
    val accent = elementColor(character.element)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { PortraitHero(character, accent) }
        item { IdentityBlock(character, accent) }
        item { LevelBar(character, accent) }
        item { StatsBlock(character, accent) }
        item { LoreBlock(character, accent) }
        item { SkillsBlock(character.skills, accent) }
    }
}

@Composable
private fun PortraitHero(character: CharacterEntry, accent: Color) {
    val infinite = rememberInfiniteTransition(label = "glow")
    val glow by infinite.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowAlpha",
    )
    val twinkle by infinite.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "twinkle",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.05f)
            .clip(RoundedCornerShape(24.dp))
            .background(Background)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center,
    ) {
        // radial glow behind the portrait, pulsing with the character's element color
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(accent.copy(alpha = glow * 0.55f), Color.Transparent),
                        radius = 480f,
                    )
                ),
        )
        // scattered "stars" — small glowing dots, twinkling in and out
        StarField(accent = accent, twinkle = twinkle)

        Image(
            painter = painterResource(portraitRes(character.spriteClass)),
            contentDescription = character.name,
            modifier = Modifier
                .fillMaxWidth(0.62f)
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp),
            contentScale = ContentScale.Fit,
        )

        // rarity + element chip, top-left
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xCC000000))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(accent),
            )
            Text(character.element, color = TextBright, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * A handful of small twinkling dots scattered around the portrait field.
 * Each dot is a separate fillMaxSize Box using a BiasAlignment so it lands
 * at a fixed fractional position without any custom layout logic.
 */
@Composable
private fun StarField(accent: Color, twinkle: Float) {
    val positions = listOf(
        -0.75f to -0.65f, 0.7f to -0.55f, -0.55f to -0.15f, 0.55f to 0f,
        -0.8f to 0.25f, 0.8f to 0.35f, 0f to -0.8f, -0.3f to -0.4f,
    )
    positions.forEachIndexed { i, (x, y) ->
        val phase = if (i % 2 == 0) twinkle else (1f - twinkle)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.BiasAlignment(x, y),
        ) {
            Box(
                modifier = Modifier
                    .size((2 + (i % 3)).dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.25f + phase * 0.6f)),
            )
        }
    }
}

@Composable
private fun IdentityBlock(character: CharacterEntry, accent: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        StarRating(count = character.stars)
        Text(character.name, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = TextBright)
        Text(character.title, color = accent, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun LevelBar(character: CharacterEntry, accent: Color) {
    val progress = character.level.toFloat() / character.maxLevel.toFloat()
    AstralCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Level", color = TextDim, fontSize = 12.sp)
            Text(
                "${character.level} / ${character.maxLevel}",
                color = TextBright,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Box(Modifier.padding(top = 8.dp)) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = accent,
                trackColor = SurfaceRaised,
            )
        }
    }
}

@Composable
private fun StatsBlock(character: CharacterEntry, accent: Color) {
    AstralCard {
        Text("Ability Stats", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextBright)
        Box(Modifier.height(10.dp))
        StatRow("ATK", character.atk.toString(), character.atk, 4000, accent)
        StatRow("DEF", character.def.toString(), character.def, 2500, accent)
        StatRow("HP", character.hp.toString(), character.hp, 12000, accent)
        StatRow("Crit Rate", "${character.critRate}%", character.critRate, 100, Danger)
        StatRow("Speed", character.speed.toString(), character.speed, 150, GemBlue)
    }
}

@Composable
private fun StatRow(label: String, display: String, value: Int, max: Int, color: Color) {
    val fraction = (value.toFloat() / max.toFloat()).coerceIn(0.03f, 1f)
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label, color = TextDim, fontSize = 12.sp)
            Text(display, color = TextBright, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(SurfaceRaised),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color),
            )
        }
    }
}

@Composable
private fun LoreBlock(character: CharacterEntry, accent: Color) {
    AstralCard {
        Text("Lore", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextBright)
        Text(
            character.lore,
            color = TextDim,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            modifier = Modifier.padding(top = 8.dp),
        )
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Surface)
                .border(0.5.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                .padding(12.dp),
        ) {
            Text(
                "\u201C${character.quote}\u201D",
                color = accent,
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun SkillsBlock(skills: List<CharacterSkill>, accent: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Skills", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextBright)
        skills.forEachIndexed { index, skill ->
            AstralCard {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(accent.copy(alpha = 0.18f))
                            .border(0.5.dp, accent.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            if (index == skills.lastIndex) "U" else "${index + 1}",
                            color = accent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        )
                    }
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(skill.name, color = TextBright, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            skill.description,
                            color = TextDim,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
            }
        }
    }
}
