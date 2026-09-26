package com.astralofthesun.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.astralofthesun.app.ui.theme.BorderSubtle
import com.astralofthesun.app.ui.theme.Surface

/**
 * The card shell used everywhere in the app: near-black fill, 10% white
 * hairline border, rounded corners. Matches the stat-card / hero-banner
 * treatment from the original mock so every screen stays visually
 * consistent without each one re-declaring the same four modifiers.
 *
 * Pass `onClick` to make the whole card tappable (used for list rows that
 * navigate elsewhere); omit it for a static display card.
 */
@Composable
fun AstralCard(
    modifier: Modifier = Modifier,
    corner: Dp = 14.dp,
    fill: Color = Surface,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(corner)
    val clickMod = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(fill, shape)
            .border(0.5.dp, BorderSubtle, shape)
            .then(clickMod)
            .padding(14.dp),
        content = content,
    )
}
