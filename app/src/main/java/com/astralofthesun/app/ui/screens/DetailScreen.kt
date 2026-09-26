package com.astralofthesun.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.astralofthesun.app.ui.theme.TextDim

/**
 * Shared shell for detail screens whose real content depends on an API
 * endpoint that doesn't exist yet (player detail, item detail, card detail,
 * tier detail). Every one of these routes and renders correctly today;
 * swap the body for real data once the corresponding endpoint ships.
 */
@Composable
fun DetailScreen(title: String, subtitle: String, id: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(subtitle, color = TextDim, fontSize = 13.sp)
        Text("ID: $id", color = TextDim, fontSize = 12.sp)
    }
}
