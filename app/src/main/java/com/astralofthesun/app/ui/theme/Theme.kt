package com.astralofthesun.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

/**
 * Astral's palette — near-black surfaces, a single gold accent, and a very
 * restrained set of dim/bright text tones. Every screen in this app pulls
 * from these instead of inlining hex values, so a future re-skin is a
 * one-file change.
 */

// Surfaces
val Background = Color(0xFF000000)
val Surface = Color(0xFF060606)
val SurfaceRaised = Color(0xFF0A0A0A)
val SurfaceCard = Color(0xFF111111)
val BorderSubtle = Color(0x1AFFFFFF) // 10% white, matches the mock's card border

// Accent
val Primary = Color(0xFFD4AF37) // gold — matches astral-bot's --gold web token
val PrimaryDim = Color(0xFF8A712A)

// Text
val TextBright = Color(0xFFF5F5F5)
val TextDim = Color(0xFF8B8B8B)
val TextFaint = Color(0xFF555555)

// Status
val Success = Color(0xFF3DDC84)
val Danger = Color(0xFFE5484D)
val CoinGold = Color(0xFFE9C46A)
val GemBlue = Color(0xFF5AC8FA)

private val AstralColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color(0xFF1A1400),
    background = Background,
    onBackground = TextBright,
    surface = Surface,
    onSurface = TextBright,
    surfaceVariant = SurfaceRaised,
    onSurfaceVariant = TextDim,
    error = Danger,
)

@Composable
fun AstralTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AstralColorScheme,
        typography = MaterialTheme.typography,
        content = content,
    )
}

object AstralType {
    val kicker = TextStyle(fontSize = 11.sp, color = TextDim)
    val title = TextStyle(fontSize = 15.sp, color = TextBright)
    val body = TextStyle(fontSize = 13.sp, color = TextBright)
    val caption = TextStyle(fontSize = 10.sp, color = TextDim)
}
