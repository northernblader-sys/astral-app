package com.astralofthesun.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.astralofthesun.app.ui.nav.AstralApp
import com.astralofthesun.app.ui.theme.AstralTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        com.astralofthesun.app.data.Astral.init(applicationContext)
        setContent {
            AstralTheme {
                AstralApp()
            }
        }
    }
}
