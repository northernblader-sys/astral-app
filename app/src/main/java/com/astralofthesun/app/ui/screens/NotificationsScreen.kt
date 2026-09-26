package com.astralofthesun.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astralofthesun.app.data.Astral
import com.astralofthesun.app.ui.components.AstralCard
import com.astralofthesun.app.ui.components.EmptyNote
import com.astralofthesun.app.ui.theme.Primary
import com.astralofthesun.app.ui.theme.TextDim

@Composable
fun NotificationsScreen() {
    val notes = Astral.notifications

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item { Text("Notifications", fontWeight = FontWeight.Bold, fontSize = 20.sp) }

        if (notes.isEmpty()) {
            item { EmptyNote("Nothing yet") }
        } else {
            items(notes) { n ->
                AstralCard {
                    Text(
                        n.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (n.read) TextDim else Primary,
                    )
                    Text(n.body, color = TextDim, fontSize = 12.sp)
                }
            }
        }
    }
}
