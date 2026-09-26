package com.astralofthesun.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astralofthesun.app.ui.theme.CoinGold
import com.astralofthesun.app.ui.theme.GemBlue
import com.astralofthesun.app.ui.theme.Primary
import com.astralofthesun.app.ui.theme.TextDim
import com.astralofthesun.app.ui.theme.TextFaint

/** Section label + right-aligned meta, used above every list/grid in the app. */
@Composable
fun SectionHeader(title: String, meta: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        if (meta != null) Text(meta, color = TextDim, fontSize = 12.sp)
    }
}

/** Small dot placeholder for an empty list/section, with a one-line reason. */
@Composable
fun EmptyNote(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = TextFaint, fontSize = 12.sp, textAlign = TextAlign.Center)
    }
}

/** Small solid coin glyph — a colored circle, not an image asset, so it scales cleanly at any size. */
@Composable
fun Coin(sizeDp: Int = 12) {
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .background(CoinGold, CircleShape),
    )
}

/** Small solid gem glyph, same treatment as Coin() but in the gem accent color. */
@Composable
fun Gem(sizeDp: Int = 12) {
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .background(GemBlue, CircleShape),
    )
}

/** Row of gold stars for character/card rarity — filled up to `count`, outlined for the rest. */
@Composable
fun StarRating(count: Int, max: Int = 5, sizeDp: Int = 14) {
    Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
        repeat(max) { i ->
            Icon(
                imageVector = if (i < count) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = if (i < count) Primary else TextFaint,
                modifier = Modifier.size(sizeDp.dp),
            )
        }
    }
}
