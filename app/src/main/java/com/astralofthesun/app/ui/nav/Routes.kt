package com.astralofthesun.app.ui.nav

/**
 * Every destination in the app, grouped the same way as the agreed IA:
 * 5 bottom-tab roots, everything else pushed from one of them.
 */
object Routes {
    // Bottom nav roots
    const val HOME = "home"
    const val SHOP = "shop"
    const val SEASON = "season"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"

    // Pushed from Home
    const val LEADERBOARD = "leaderboard"
    const val PLAYER_DETAIL = "player/{uid}"
    fun playerDetail(uid: String) = "player/$uid"
    const val CHARACTER_DETAIL = "character/{characterId}"
    fun characterDetail(characterId: String) = "character/$characterId"
    const val DUNGEON_SELECT = "dungeon_select"
    const val DUNGEON_BATTLE = "dungeon_battle/{dungeonId}"
    fun dungeonBattle(dungeonId: String) = "dungeon_battle/$dungeonId"
    const val POKEMON_ROSTER = "pokemon_roster"

    // Pushed from Shop
    const val ITEM_DETAIL = "item/{itemId}"
    fun itemDetail(itemId: String) = "item/$itemId"
    const val CARD_VAULT = "card_vault"
    const val CARD_CATALOG = "card_catalog"
    const val CARD_DETAIL = "card/{cardId}"
    fun cardDetail(cardId: String) = "card/$cardId"
    const val PREMIUM = "premium"
    const val GEMS_CHECKOUT = "gems_checkout"

    // Pushed from Season
    const val TIER_DETAIL = "tier/{tier}"
    fun tierDetail(tier: String) = "tier/$tier"
    const val SPIN_BANNER = "spin_banner"
    const val PULL_RESULTS = "pull_results"

    // Pushed from Profile
    const val INVENTORY = "inventory"
    const val TRANSFER = "transfer"
    const val NOTIFICATIONS = "notifications"

    // Outside tab bar
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
}
