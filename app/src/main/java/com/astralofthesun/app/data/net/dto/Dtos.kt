package com.astralofthesun.app.data.net.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * These mirror the bot's real response shapes exactly (field names, types,
 * nullability), read directly from lib/api-server.js's playerCore() /
 * serializeSelf() / the /api/stats, /api/leaderboard, /api/shop, /api/season,
 * /api/cards/prices handlers. Anything not used by a current screen is left
 * off rather than guessed at — add fields here as screens need them.
 */

@Serializable
data class SessionResponse(
    val ok: Boolean = true,
    val signedIn: Boolean = false,
    val needsRegistration: Boolean = false,
    val player: PlayerDto? = null,
)

@Serializable
data class WalletDto(
    val solars: Long = 0,
    val gems: Long = 0,
    val bankGold: Long = 0,
    val vault: Long = 0,
    val loan: Long = 0,
)

@Serializable
data class RankDto(
    val title: String? = null,
    val epithet: String? = null,
    val emoji: String? = null,
)

@Serializable
data class SeasonSelfDto(
    val id: String? = null,
    val name: String? = null,
    val tier: Int = 0,
    val tierCount: Int = 50,
    val seasonLevel: Int = 0,
    val premiumPass: Boolean = false,
    val progressPercent: Int = 0,
)

@Serializable
data class PlayerStateDto(
    val inDungeon: Boolean = false,
    val inBattle: Boolean = false,
    val floor: Int = 0,
)

@Serializable
data class InventoryItemDto(
    val id: String,
    val name: String,
    val emoji: String? = null,
    val rarity: String? = null,
    val type: String? = null,
    val image: String? = null,
    val description: String? = null,
    val qty: Int = 1,
)

@Serializable
data class PlayerDto(
    val uid: String,
    val name: String,
    val title: String? = null,
    val avatarUrl: String? = null,
    val bannerUrl: String? = null,
    val classId: String? = null,
    val raceId: String? = null,
    val level: Int = 1,
    val xp: Long = 0,
    val hp: Int = 0,
    val maxHp: Int = 0,
    val mp: Int = 0,
    val maxMp: Int = 0,
    val rank: RankDto? = null,
    val wallet: WalletDto = WalletDto(),
    val season: SeasonSelfDto? = null,
    val state: PlayerStateDto = PlayerStateDto(),
    val inventory: List<InventoryItemDto> = emptyList(),
)

@Serializable
data class SeasonMetaDto(
    val name: String,
    val number: Int,
    val endsAt: Long? = null,
)

/** GET /api/stats — global site stats, not the signed-in player's own. */
@Serializable
data class SiteStatsResponse(
    val ok: Boolean = true,
    val players: Int = 0,
    val newThisWeek: Int = 0,
    val premiumMembers: Int = 0,
    val commandsToday: Int = 0,
    val highestLevel: Int = 0,
    val deepestFloor: Int = 0,
    val dungeonsConquered: Int = 0,
    val charactersAvailable: Int = 0,
    val season: SeasonMetaDto? = null,
    val botsOnline: Int = 0,
)

@Serializable
data class LeaderboardRowDto(
    val uid: String,
    val name: String,
    val rank: Int,
    val value: Long,
    val avatarUrl: String? = null,
)

@Serializable
data class LeaderboardResponse(
    val ok: Boolean = true,
    val board: String = "level",
    val rows: List<LeaderboardRowDto> = emptyList(),
    val you: LeaderboardRowDto? = null,
)

@Serializable
data class ShopItemDto(
    val id: String,
    val name: String,
    val category: String = "misc",
    val rarity: String = "common",
    @SerialName("priceSolars") val priceSolars: Long? = null,
    @SerialName("priceGems") val priceGems: Long? = null,
)

@Serializable
data class ShopResponse(
    val ok: Boolean = true,
    val items: List<ShopItemDto> = emptyList(),
)

@Serializable
data class CardTierPriceDto(
    val tier: String,
    val priceSolars: Long? = null,
    val priceGems: Long? = null,
)

@Serializable
data class CardPricesResponse(
    val ok: Boolean = true,
    val tiers: List<CardTierPriceDto> = emptyList(),
)

@Serializable
data class SeasonResponse(
    val ok: Boolean = true,
    val id: String? = null,
    val name: String = "",
    val number: Int = 0,
    val endsAt: Long? = null,
)

/* ── auth flow ───────────────────────────────────────────────────────── */

@Serializable
data class LookupAccountResponse(
    val ok: Boolean = true,
    val found: Boolean = false,
    val name: String? = null,
    val level: Int? = null,
    val rank: String? = null,
    val avatarUrl: String? = null,
    val maskedPhone: String? = null,
    val handle: String? = null,
    val error: String? = null,
)

@Serializable
data class RequestOtpResponse(
    val ok: Boolean = true,
)

@Serializable
data class VerifyOtpResponse(
    val ok: Boolean = true,
    val token: String? = null,
    val needsRegistration: Boolean = false,
    val player: PlayerDto? = null,
)

/* ── pokémon (real endpoints — lib/api-pokemon.js) ──────────────────────
 * Mirrors the shared "Mon" shape returned by monView() everywhere:
 * GET /party, POST /party, POST /:id/rename, /:id/protect, /:id/feed,
 * /:id/train, /heal, /:id/release, /:id/give. */

@Serializable
data class ItemDto(
    val id: String,
    val name: String,
    val category: String? = null,
    val rarity: String? = null,
    val currency: String? = null,
    val buyPrice: Long? = null,
    val sellPrice: Long? = null,
    val description: String? = null,
    val iconUrl: String? = null,
    val ball: Boolean = false,
    val mega: Boolean = false,
    val usableInBattle: Boolean = false,
    val usableOutside: Boolean = false,
)

@Serializable
data class MonDto(
    val id: String,
    val dexId: Int,
    val name: String,
    val nickname: String? = null,
    val level: Int = 1,
    val exp: Long = 0,
    val expNext: Long = 0,
    val shiny: Boolean = false,
    val protected: Boolean = false,
    val main: Boolean = false,
    val inParty: Boolean = false,
    val partySlot: Int = -1,
    val types: List<String> = emptyList(),
    val hp: Int = 0,
    val maxHp: Int = 0,
    val fainted: Boolean = false,
    val caughtAt: Long? = null,
    val held: ItemDto? = null,
)

@Serializable
data class PartyWalletDto(
    val solars: Long = 0,
    val gems: Long = 0,
)

/** GET /api/pokemon/party */
@Serializable
data class PartyCountsDto(
    val party: Int = 0,
    val owned: Int = 0,
)

@Serializable
data class PartyResponse(
    val ok: Boolean = true,
    val party: List<MonDto> = emptyList(),
    val main: MonDto? = null,
    val partyMax: Int = 6,
    val counts: PartyCountsDto = PartyCountsDto(),
)

/** POST /api/pokemon/party body */
@Serializable
data class PartyActionBody(
    val action: String, // "add" | "remove" | "clear" | "main"
    val pokemonId: String? = null,
)

/** POST /api/pokemon/party response — note: distinct shape from GET /party (main is a bare id here) */
@Serializable
data class PartyActionResponse(
    val ok: Boolean = true,
    val party: List<MonDto> = emptyList(),
    val main: String? = null,
)

@Serializable
data class RenameBody(val nickname: String)

@Serializable
data class RenameResponse(
    val ok: Boolean = true,
    val mon: MonDto? = null,
)

@Serializable
data class ProtectBody(val protected: Boolean? = null)

@Serializable
data class ProtectResponse(
    val ok: Boolean = true,
    val mon: MonDto? = null,
    val protected: Boolean = false,
)

@Serializable
data class EvolutionDto(
    val from: String? = null,
    val to: String? = null,
    val toDexId: Int? = null,
)

@Serializable
data class LevelUpDto(
    val from: Int = 0,
    val to: Int = 0,
)

@Serializable
data class FeedResponse(
    val ok: Boolean = true,
    val gain: Int = 0,
    val leveledUp: LevelUpDto? = null,
    val mon: MonDto? = null,
    val wallet: PartyWalletDto = PartyWalletDto(),
    val evolutions: List<EvolutionDto> = emptyList(),
)

@Serializable
data class TrainBody(val stat: String) // hp | atk | def | spa | spd | spe

@Serializable
data class TrainResponse(
    val ok: Boolean = true,
    val added: Int = 0,
    val stat: String = "",
    val cost: Long = 0,
    val mon: MonDto? = null,
    val wallet: PartyWalletDto = PartyWalletDto(),
)

@Serializable
data class HealResponse(
    val ok: Boolean = true,
    val healed: Int = 0,
)

@Serializable
data class ReleaseResponse(
    val ok: Boolean = true,
    val released: String? = null,
    val reward: Long = 0,
    val wallet: PartyWalletDto = PartyWalletDto(),
)

@Serializable
data class GiveBody(val targetUsername: String)

@Serializable
data class GiftedDto(
    val id: String,
    val name: String,
    val nickname: String? = null,
    val level: Int = 1,
)

@Serializable
data class GiveTargetDto(
    val name: String? = null,
    val username: String? = null,
    val level: Int = 1,
)

@Serializable
data class GiveResponse(
    val ok: Boolean = true,
    val gifted: GiftedDto? = null,
    val to: GiveTargetDto? = null,
    val party: List<MonDto> = emptyList(),
)

/* ── dungeon (real endpoints — lib/api-dungeon.js) ──────────────────────
 * Mirrors DungeonState exactly as documented: GET /state, and as the
 * `state` field inside POST /enter, /action, /leave. */

@Serializable
data class DungeonPlayerDto(
    val name: String = "",
    val level: Int = 1,
    val hp: Int = 0,
    val maxHp: Int = 0,
    val mp: Int = 0,
    val maxMp: Int = 0,
    val defending: Boolean = false,
)

@Serializable
data class DungeonEnemyDto(
    val name: String = "",
    val emoji: String? = null,
    val hp: Int = 0,
    val maxHp: Int = 0,
    val atk: Int = 0,
    val def: Int = 0,
    val isBoss: Boolean = false,
    val tier: String? = null,
)

@Serializable
data class SwarmMonsterDto(
    val uid: String,
    val name: String,
    val emoji: String? = null,
    val hp: Int = 0,
    val maxHp: Int = 0,
    val atk: Int = 0,
    val lane: Int = 0,
    val range: String? = null,
    val telegraph: String? = null,
    val alive: Boolean = true,
)

@Serializable
data class DungeonStateDto(
    val inDungeon: Boolean = false,
    val inBattle: Boolean = false,
    val dungeonId: String? = null,
    val dungeonName: String? = null,
    val floor: Int = 0,
    val totalFloors: Int? = null,
    val isBossFloor: Boolean = false,
    val mode: String? = null, // "swarm" | "solo" | null
    val canAdvance: Boolean = false,
    val conquered: Boolean = false,
    val highestFloor: Int = 0,
    val playerLane: Int? = null,
    val player: DungeonPlayerDto = DungeonPlayerDto(),
    val enemy: DungeonEnemyDto? = null,
    val monsters: List<SwarmMonsterDto>? = null,
    val actions: List<String> = emptyList(),
)

@Serializable
data class DungeonEnterBody(val dungeonId: String)

@Serializable
data class DungeonEnterResponse(
    val ok: Boolean = true,
    val resumed: Boolean = false,
    val log: List<String> = emptyList(),
    val state: DungeonStateDto = DungeonStateDto(),
    val runsRemaining: Int? = null,
)

/** POST /api/dungeon/action body — only relevant fields are sent per action. */
@Serializable
data class DungeonActionBody(
    val action: String, // attack | skill | defend | flee | move | dodge
    val skillId: String? = null,
    val direction: String? = null, // left | right
    val target: Int? = null,
)

@Serializable
data class DungeonActionResponse(
    val ok: Boolean = true,
    val log: List<String> = emptyList(),
    val state: DungeonStateDto = DungeonStateDto(),
)

@Serializable
data class DungeonLeaveResponse(
    val ok: Boolean = true,
    val log: List<String> = emptyList(),
    val state: DungeonStateDto = DungeonStateDto(),
)

@Serializable
data class DungeonListEntryDto(
    val id: String,
    val name: String,
    val floors: Int? = null,
    val levelRange: List<Int>? = null,
    val entryLevel: Int? = null,
    val travelCost: Long = 0,
    val prerequisite: String? = null,
    val prerequisiteName: String? = null,
    val swarm: Boolean = false,
    val unlocked: Boolean = false,
    val conquered: Boolean = false,
    val highestFloor: Int = 0,
)

@Serializable
data class DungeonListResponse(
    val ok: Boolean = true,
    val dungeons: List<DungeonListEntryDto> = emptyList(),
    val runsRemaining: Int? = null,
)
