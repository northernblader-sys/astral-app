package com.astralofthesun.app.data

/**
 * Models for the systems that still have no backend endpoint (see
 * PlaceholderData.kt). Player/wallet/shop/season/leaderboard/card-price
 * models, plus Pokémon (Mon) and Dungeon models, now live in
 * data/net/dto/Dtos.kt, matching the real API shapes — they were removed
 * from here to avoid competing types.
 */

/** One skill entry shown on the character detail screen. */
data class CharacterSkill(
    val name: String,
    val description: String,
)

/**
 * A playable Astral character. `spriteClass` maps to the existing
 * char_{spriteClass}_default.png family of sprite sheets shipped in
 * res/drawable (fighter, samurai, shinobi) — no new art pipeline needed.
 */
data class CharacterEntry(
    val id: String,
    val name: String,
    val title: String,
    val spriteClass: String,
    val level: Int,
    val maxLevel: Int,
    val stars: Int,
    val element: String,
    val lore: String,
    val quote: String,
    val atk: Int,
    val def: Int,
    val hp: Int,
    val critRate: Int,
    val speed: Int,
    val skills: List<CharacterSkill>,
)

data class FriendEntry(
    val uid: String,
    val name: String,
    val online: Boolean = false,
)

data class NotificationEntry(
    val id: String,
    val title: String,
    val body: String,
    val read: Boolean = false,
    val createdAt: Long = 0L,
)
