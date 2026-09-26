package com.astralofthesun.app.data.repo

import android.content.Context
import com.astralofthesun.app.data.net.ApiClient
import com.astralofthesun.app.data.net.dto.CardPricesResponse
import com.astralofthesun.app.data.net.dto.DungeonActionBody
import com.astralofthesun.app.data.net.dto.DungeonActionResponse
import com.astralofthesun.app.data.net.dto.DungeonEnterBody
import com.astralofthesun.app.data.net.dto.DungeonEnterResponse
import com.astralofthesun.app.data.net.dto.DungeonLeaveResponse
import com.astralofthesun.app.data.net.dto.DungeonListResponse
import com.astralofthesun.app.data.net.dto.DungeonStateDto
import com.astralofthesun.app.data.net.dto.FeedResponse
import com.astralofthesun.app.data.net.dto.GiveBody
import com.astralofthesun.app.data.net.dto.GiveResponse
import com.astralofthesun.app.data.net.dto.HealResponse
import com.astralofthesun.app.data.net.dto.LeaderboardResponse
import com.astralofthesun.app.data.net.dto.LookupAccountResponse
import com.astralofthesun.app.data.net.dto.PartyActionBody
import com.astralofthesun.app.data.net.dto.PartyActionResponse
import com.astralofthesun.app.data.net.dto.PartyResponse
import com.astralofthesun.app.data.net.dto.PlayerDto
import com.astralofthesun.app.data.net.dto.ProtectBody
import com.astralofthesun.app.data.net.dto.ProtectResponse
import com.astralofthesun.app.data.net.dto.ReleaseResponse
import com.astralofthesun.app.data.net.dto.RenameBody
import com.astralofthesun.app.data.net.dto.RenameResponse
import com.astralofthesun.app.data.net.dto.RequestOtpResponse
import com.astralofthesun.app.data.net.dto.SeasonResponse
import com.astralofthesun.app.data.net.dto.SessionResponse
import com.astralofthesun.app.data.net.dto.ShopResponse
import com.astralofthesun.app.data.net.dto.SiteStatsResponse
import com.astralofthesun.app.data.net.dto.TrainBody
import com.astralofthesun.app.data.net.dto.TrainResponse
import com.astralofthesun.app.data.net.dto.VerifyOtpResponse
import kotlinx.serialization.Serializable

/**
 * Every call in this file is a REAL request to the bot's production API —
 * the same server the WhatsApp bot and the web client use. There is no
 * mock layer here. This now includes Pokémon party management
 * (the /api/pokemon endpoints) and dungeon combat (the /api/dungeon
 * endpoints). Player-to-player transfer (the /api/transfer endpoints)
 * has no backend yet and is NOT in this file.
 */
class AstralRepo(context: Context) {

    val client = ApiClient(context)

    /** Boot call — never throws on "not signed in", matches the web client's session() behavior. */
    suspend fun session(): SessionResponse =
        try {
            client.get("/auth/session")
        } catch (e: Exception) {
            SessionResponse(signedIn = false)
        }

    suspend fun me(): PlayerDto = client.get<PlayerWrap>("/me").player

    suspend fun siteStats(): SiteStatsResponse = client.get("/stats")

    suspend fun leaderboard(board: String = "level", limit: Int = 50): LeaderboardResponse =
        client.get("/leaderboard?board=$board&limit=$limit")

    suspend fun player(uid: String): PlayerDto = client.get<PlayerWrap>("/players/$uid").player

    suspend fun shop(): ShopResponse = client.get("/shop")

    suspend fun buyItem(id: String, qty: Int = 1): PlayerWrap =
        client.post("/shop/buy", BuyItemBody(id, qty))

    suspend fun cardPrices(): CardPricesResponse = client.get("/cards/prices")

    suspend fun buyCardTier(tier: String): PlayerWrap =
        client.post("/cards/buy-tier", BuyTierBody(tier))

    suspend fun season(): SeasonResponse = client.get("/season")

    /* ── auth ─────────────────────────────────────────────────────────── */

    suspend fun lookupAccount(username: String): LookupAccountResponse =
        client.post("/auth/lookup", UsernameBody(username))

    /** Pass a phone number to sign UP, or a handle (from lookupAccount) to sign IN. */
    suspend fun requestOtpForSignup(phone: String): RequestOtpResponse =
        client.post("/auth/request-otp", PhoneBody(phone))

    suspend fun requestOtpForLogin(handle: String): RequestOtpResponse =
        client.post("/auth/request-otp", HandleBody(handle))

    suspend fun verifyOtpSignup(phone: String, code: String): VerifyOtpResponse {
        val result = client.post<VerifyOtpBodyPhone, VerifyOtpResponse>(
            "/auth/verify-otp",
            VerifyOtpBodyPhone(phone, code),
        )
        result.token?.let { client.tokenStore.setToken(it) }
        return result
    }

    suspend fun verifyOtpLogin(handle: String, code: String): VerifyOtpResponse {
        val result = client.post<VerifyOtpBodyHandle, VerifyOtpResponse>(
            "/auth/verify-otp",
            VerifyOtpBodyHandle(handle, code),
        )
        result.token?.let { client.tokenStore.setToken(it) }
        return result
    }

    suspend fun logout() {
        try { client.postNoBody<Unit>("/auth/logout") } catch (_: Exception) { }
        client.tokenStore.setToken(null)
    }

    /* ── pokémon (real endpoints — the /api/pokemon family) ───────────── */

    suspend fun party(): PartyResponse = client.get("/pokemon/party")

    suspend fun partyAction(action: String, pokemonId: String? = null): PartyActionResponse =
        client.post("/pokemon/party", PartyActionBody(action, pokemonId))

    suspend fun addToParty(pokemonId: String) = partyAction("add", pokemonId)
    suspend fun removeFromParty(pokemonId: String) = partyAction("remove", pokemonId)
    suspend fun clearParty() = partyAction("clear")
    suspend fun setMain(pokemonId: String) = partyAction("main", pokemonId)

    suspend fun renameMon(id: String, nickname: String): RenameResponse =
        client.post("/pokemon/$id/rename", RenameBody(nickname))

    suspend fun protectMon(id: String, protected: Boolean? = null): ProtectResponse =
        client.post("/pokemon/$id/protect", ProtectBody(protected))

    suspend fun feedMon(id: String): FeedResponse =
        client.postNoBody("/pokemon/$id/feed")

    suspend fun trainMon(id: String, stat: String): TrainResponse =
        client.post("/pokemon/$id/train", TrainBody(stat))

    suspend fun healParty(): HealResponse =
        client.postNoBody("/pokemon/heal")

    suspend fun releaseMon(id: String): ReleaseResponse =
        client.postNoBody("/pokemon/$id/release")

    suspend fun giveMon(id: String, targetUsername: String): GiveResponse =
        client.post("/pokemon/$id/give", GiveBody(targetUsername))

    /* ── dungeon (real endpoints — the /api/dungeon family) ───────────── */

    suspend fun dungeonList(): DungeonListResponse = client.get("/dungeon/list")

    suspend fun dungeonState(): DungeonStateDto = client.get("/dungeon/state")

    suspend fun dungeonEnter(dungeonId: String): DungeonEnterResponse =
        client.post("/dungeon/enter", DungeonEnterBody(dungeonId))

    suspend fun dungeonAction(
        action: String,
        skillId: String? = null,
        direction: String? = null,
        target: Int? = null,
    ): DungeonActionResponse =
        client.post("/dungeon/action", DungeonActionBody(action, skillId, direction, target))

    suspend fun dungeonLeave(): DungeonLeaveResponse =
        client.postNoBody("/dungeon/leave")
}

@Serializable
data class PlayerWrap(val ok: Boolean = true, val player: PlayerDto)

@Serializable
data class BuyItemBody(val id: String, val qty: Int)

@Serializable
data class BuyTierBody(val tier: String)

@Serializable
data class UsernameBody(val username: String)

@Serializable
data class PhoneBody(val phone: String)

@Serializable
data class HandleBody(val handle: String)

@Serializable
data class VerifyOtpBodyPhone(val phone: String, val code: String)

@Serializable
data class VerifyOtpBodyHandle(val handle: String, val code: String)
