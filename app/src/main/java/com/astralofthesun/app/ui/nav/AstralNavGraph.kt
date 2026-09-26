package com.astralofthesun.app.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.astralofthesun.app.data.Astral
import com.astralofthesun.app.ui.screens.CharacterDetailScreen
import com.astralofthesun.app.ui.screens.DetailScreen
import com.astralofthesun.app.ui.screens.DungeonBattleScreen
import com.astralofthesun.app.ui.screens.DungeonSelectScreen
import com.astralofthesun.app.ui.screens.HomeScreen
import com.astralofthesun.app.ui.screens.InventoryScreen
import com.astralofthesun.app.ui.screens.LeaderboardScreen
import com.astralofthesun.app.ui.screens.LoginScreen
import com.astralofthesun.app.ui.screens.NotificationsScreen
import com.astralofthesun.app.ui.screens.PokemonRosterScreen
import com.astralofthesun.app.ui.screens.ProfileScreen
import com.astralofthesun.app.ui.screens.SeasonScreen
import com.astralofthesun.app.ui.screens.SettingsScreen
import com.astralofthesun.app.ui.screens.ShopScreen
import com.astralofthesun.app.ui.screens.TransferScreen
import com.astralofthesun.app.ui.theme.Background

/**
 * Every tab root shows the bottom bar; every pushed screen does too, since
 * this app has no "full screen takeover" flows yet besides battle screens,
 * which intentionally hide chrome to keep focus on the fight.
 */
private val NO_BOTTOM_BAR = setOf(
    Routes.DUNGEON_BATTLE,
)

@Composable
fun AstralApp() {
    val navController = rememberNavController()

    Scaffold(
        containerColor = Background,
        bottomBar = { AstralBottomBar(navController) },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding),
        ) {
            // ── Bottom tab roots ─────────────────────────────────────────
            composable(Routes.HOME) {
                HomeScreen(
                    goTopUp = { currency -> navController.navigate(Routes.GEMS_CHECKOUT) },
                    goDungeon = { navController.navigate(Routes.DUNGEON_SELECT) },
                    goLeaderboard = { navController.navigate(Routes.LEADERBOARD) },
                    goPokemonRoster = { navController.navigate(Routes.POKEMON_ROSTER) },
                    goCharacterDetail = { id -> navController.navigate(Routes.characterDetail(id)) },
                )
            }
            composable(Routes.SHOP) {
                ShopScreen(
                    goItemDetail = { id -> navController.navigate(Routes.itemDetail(id)) },
                    goCardVault = { navController.navigate(Routes.CARD_VAULT) },
                    goPremium = { navController.navigate(Routes.PREMIUM) },
                )
            }
            composable(Routes.SEASON) {
                SeasonScreen(
                    goTierDetail = { tier -> navController.navigate(Routes.tierDetail(tier)) },
                    goSpinBanner = { navController.navigate(Routes.SPIN_BANNER) },
                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    goInventory = { navController.navigate(Routes.INVENTORY) },
                    goTransfer = { navController.navigate(Routes.TRANSFER) },
                    goNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(goLogin = { navController.navigate(Routes.LOGIN) })
            }

            // ── Pushed from Home ─────────────────────────────────────────
            composable(Routes.LEADERBOARD) {
                LeaderboardScreen(goPlayerDetail = { uid -> navController.navigate(Routes.playerDetail(uid)) })
            }
            composable(
                Routes.PLAYER_DETAIL,
                arguments = listOf(navArgument("uid") { type = NavType.StringType }),
            ) { entry ->
                DetailScreen("Player", "Public stats & alerts", entry.arguments?.getString("uid") ?: "")
            }
            composable(
                Routes.CHARACTER_DETAIL,
                arguments = listOf(navArgument("characterId") { type = NavType.StringType }),
            ) { entry ->
                val id = entry.arguments?.getString("characterId") ?: ""
                val character = Astral.characters.find { it.id == id }
                if (character != null) {
                    CharacterDetailScreen(character)
                } else {
                    DetailScreen("Character", "Not found", id)
                }
            }
            composable(Routes.DUNGEON_SELECT) {
                DungeonSelectScreen(goBattle = { id -> navController.navigate(Routes.dungeonBattle(id)) })
            }
            composable(
                Routes.DUNGEON_BATTLE,
                arguments = listOf(navArgument("dungeonId") { type = NavType.StringType }),
            ) { entry ->
                DungeonBattleScreen(
                    dungeonId = entry.arguments?.getString("dungeonId") ?: "",
                    onLeave = { navController.popBackStack() },
                )
            }
            composable(Routes.POKEMON_ROSTER) { PokemonRosterScreen() }

            // ── Pushed from Shop ─────────────────────────────────────────
            composable(
                Routes.ITEM_DETAIL,
                arguments = listOf(navArgument("itemId") { type = NavType.StringType }),
            ) { entry ->
                DetailScreen("Item", "Buy confirmation", entry.arguments?.getString("itemId") ?: "")
            }
            composable(Routes.CARD_VAULT) {
                DetailScreen("Card Vault", "Buyable tiers, cheapest first", "")
            }
            composable(Routes.CARD_CATALOG) {
                DetailScreen("Card Catalog", "Browse the full collection", "")
            }
            composable(
                Routes.CARD_DETAIL,
                arguments = listOf(navArgument("cardId") { type = NavType.StringType }),
            ) { entry ->
                DetailScreen("Card", "Card detail", entry.arguments?.getString("cardId") ?: "")
            }
            composable(Routes.PREMIUM) {
                DetailScreen("Premium", "Plans & gem packages", "")
            }
            composable(Routes.GEMS_CHECKOUT) {
                DetailScreen("Top-up", "Solars & gem packages", "")
            }

            // ── Pushed from Season ───────────────────────────────────────
            composable(
                Routes.TIER_DETAIL,
                arguments = listOf(navArgument("tier") { type = NavType.StringType }),
            ) { entry ->
                DetailScreen("Tier", "Free & premium rewards", entry.arguments?.getString("tier") ?: "")
            }
            composable(Routes.SPIN_BANNER) {
                DetailScreen("Spin Banner", "Pull for this season's exclusive", "")
            }
            composable(Routes.PULL_RESULTS) {
                DetailScreen("Results", "What you pulled", "")
            }

            // ── Pushed from Profile ──────────────────────────────────────
            composable(Routes.INVENTORY) { InventoryScreen() }
            composable(Routes.TRANSFER) { TransferScreen() }
            composable(Routes.NOTIFICATIONS) { NotificationsScreen() }

            // ── Outside tab bar ──────────────────────────────────────────
            composable(Routes.LOGIN) {
                LoginScreen(onSignedIn = { navController.popBackStack() })
            }
            composable(Routes.SIGNUP) {
                DetailScreen("Join Astral", "Phone \u2192 OTP \u2192 character", "")
            }
        }
    }
}
