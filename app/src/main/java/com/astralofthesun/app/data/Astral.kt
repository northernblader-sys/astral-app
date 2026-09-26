package com.astralofthesun.app.data

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.astralofthesun.app.data.net.ApiError
import com.astralofthesun.app.data.net.dto.DungeonListEntryDto
import com.astralofthesun.app.data.net.dto.DungeonStateDto
import com.astralofthesun.app.data.net.dto.LeaderboardRowDto
import com.astralofthesun.app.data.net.dto.MonDto
import com.astralofthesun.app.data.net.dto.PlayerDto
import com.astralofthesun.app.data.repo.AstralRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

enum class LoadState { IDLE, LOADING, LOADED, ERROR }

/**
 * Single in-memory state holder for the whole app. Screens read only from
 * the State objects below and never call the network directly.
 *
 * As of this pass, everything reachable through today's real API — player
 * profile, wallet, leaderboard, shop, card prices, season, Pokémon party,
 * and dungeon combat — is loaded with REAL network calls via AstralRepo,
 * not placeholder data. Only the character roster, friends, and
 * notifications still have no backend endpoint — those stay in
 * PlaceholderData.kt so it's obvious what's real. There is no PvP system.
 */
object Astral {

    private lateinit var repo: AstralRepo
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun init(context: Context) {
        if (::repo.isInitialized) return
        repo = AstralRepo(context)
        repo.client.onAuthLost = {
            _signedIn.value = false
            player.value = null
        }
        refreshSession()
    }

    // ── auth / session ──────────────────────────────────────────────────
    val isSignedIn: State<Boolean> get() = _signedIn
    private val _signedIn = mutableStateOf(false)

    val player = mutableStateOf<PlayerDto?>(null)
    val sessionLoad = mutableStateOf(LoadState.IDLE)

    fun refreshSession() {
        sessionLoad.value = LoadState.LOADING
        scope.launch {
            val out = repo.session()
            _signedIn.value = out.signedIn
            player.value = out.player
            sessionLoad.value = LoadState.LOADED
            if (out.signedIn) refreshAll()
        }
    }

    fun signOut() {
        scope.launch {
            repo.logout()
            _signedIn.value = false
            player.value = null
        }
    }

    // ── real data, loaded from the bot API ──────────────────────────────
    val leaderboard = mutableStateOf<List<LeaderboardRowDto>>(emptyList())
    val leaderboardLoad = mutableStateOf(LoadState.IDLE)

    val shop = mutableStateOf<List<com.astralofthesun.app.data.net.dto.ShopItemDto>>(emptyList())
    val shopLoad = mutableStateOf(LoadState.IDLE)

    val cardTiers = mutableStateOf<List<com.astralofthesun.app.data.net.dto.CardTierPriceDto>>(emptyList())

    val season = mutableStateOf<com.astralofthesun.app.data.net.dto.SeasonResponse?>(null)

    val siteStats = mutableStateOf<com.astralofthesun.app.data.net.dto.SiteStatsResponse?>(null)

    val lastError = mutableStateOf<String?>(null)

    fun refreshAll() {
        loadLeaderboard()
        loadShop()
        loadCardPrices()
        loadSeason()
        loadSiteStats()
        loadMe()
        loadParty()
        loadDungeons()
    }

    fun loadMe() = scope.launch {
        runCatching { repo.me() }
            .onSuccess { player.value = it }
            .onFailure { reportError(it) }
    }

    fun loadLeaderboard(board: String = "level") = scope.launch {
        leaderboardLoad.value = LoadState.LOADING
        runCatching { repo.leaderboard(board) }
            .onSuccess { leaderboard.value = it.rows; leaderboardLoad.value = LoadState.LOADED }
            .onFailure { reportError(it); leaderboardLoad.value = LoadState.ERROR }
    }

    fun loadShop() = scope.launch {
        shopLoad.value = LoadState.LOADING
        runCatching { repo.shop() }
            .onSuccess { shop.value = it.items; shopLoad.value = LoadState.LOADED }
            .onFailure { reportError(it); shopLoad.value = LoadState.ERROR }
    }

    fun loadCardPrices() = scope.launch {
        runCatching { repo.cardPrices() }
            .onSuccess { cardTiers.value = it.tiers }
            .onFailure { reportError(it) }
    }

    fun loadSeason() = scope.launch {
        runCatching { repo.season() }
            .onSuccess { season.value = it }
            .onFailure { reportError(it) }
    }

    fun loadSiteStats() = scope.launch {
        runCatching { repo.siteStats() }
            .onSuccess { siteStats.value = it }
            .onFailure { reportError(it) }
    }

    suspend fun lookupAccount(username: String) = repo.lookupAccount(username)
    suspend fun requestOtpSignup(phone: String) = repo.requestOtpForSignup(phone)
    suspend fun requestOtpLogin(handle: String) = repo.requestOtpForLogin(handle)

    suspend fun verifyOtpSignup(phone: String, code: String): Boolean {
        val out = repo.verifyOtpSignup(phone, code)
        if (out.token != null) {
            _signedIn.value = true
            player.value = out.player
            refreshAll()
        }
        return out.token != null
    }

    suspend fun verifyOtpLogin(handle: String, code: String): Boolean {
        val out = repo.verifyOtpLogin(handle, code)
        if (out.token != null) {
            _signedIn.value = true
            player.value = out.player
            refreshAll()
        }
        return out.token != null
    }

    private fun reportError(t: Throwable) {
        lastError.value = when (t) {
            is ApiError -> t.message
            else -> "Something went wrong. Check your connection."
        }
    }

    // ── pokémon party — real data from /api/pokemon/* ───────────────────
    val party = mutableStateOf<List<MonDto>>(emptyList())
    val mainMon = mutableStateOf<MonDto?>(null)
    val partyMax = mutableStateOf(6)
    val ownedCount = mutableStateOf(0)
    val partyLoad = mutableStateOf(LoadState.IDLE)
    val pokemonActionMessage = mutableStateOf<String?>(null)

    fun loadParty() = scope.launch {
        partyLoad.value = LoadState.LOADING
        runCatching { repo.party() }
            .onSuccess {
                party.value = it.party
                mainMon.value = it.main
                partyMax.value = it.partyMax
                ownedCount.value = it.counts.owned
                partyLoad.value = LoadState.LOADED
            }
            .onFailure { reportError(it); partyLoad.value = LoadState.ERROR }
    }

    fun addToParty(pokemonId: String) = scope.launch {
        runCatching { repo.addToParty(pokemonId) }.onSuccess { loadParty() }.onFailure { reportError(it) }
    }

    fun removeFromParty(pokemonId: String) = scope.launch {
        runCatching { repo.removeFromParty(pokemonId) }.onSuccess { loadParty() }.onFailure { reportError(it) }
    }

    fun setMainMon(pokemonId: String) = scope.launch {
        runCatching { repo.setMain(pokemonId) }.onSuccess { loadParty() }.onFailure { reportError(it) }
    }

    fun feedMon(id: String) = scope.launch {
        runCatching { repo.feedMon(id) }.onSuccess { loadParty(); loadMe() }.onFailure { reportError(it) }
    }

    fun trainMon(id: String, stat: String) = scope.launch {
        runCatching { repo.trainMon(id, stat) }
            .onSuccess {
                pokemonActionMessage.value = "Trained ${it.stat.uppercase()} +${it.added} (cost ${it.cost})"
                loadParty(); loadMe()
            }
            .onFailure { reportError(it) }
    }

    fun healParty() = scope.launch {
        runCatching { repo.healParty() }.onSuccess { loadParty() }.onFailure { reportError(it) }
    }

    fun releaseMon(id: String) = scope.launch {
        runCatching { repo.releaseMon(id) }
            .onSuccess {
                pokemonActionMessage.value = "Released — +${it.reward} solars"
                loadParty(); loadMe()
            }
            .onFailure { reportError(it) }
    }

    fun renameMon(id: String, nickname: String) = scope.launch {
        runCatching { repo.renameMon(id, nickname) }
            .onSuccess { pokemonActionMessage.value = "Renamed to $nickname"; loadParty() }
            .onFailure { reportError(it) }
    }

    fun protectMon(id: String) = scope.launch {
        runCatching { repo.protectMon(id) }
            .onSuccess {
                pokemonActionMessage.value = if (it.protected) "Protected" else "Unprotected"
                loadParty()
            }
            .onFailure { reportError(it) }
    }

    fun giveMon(id: String, targetUsername: String) = scope.launch {
        runCatching { repo.giveMon(id, targetUsername) }
            .onSuccess {
                val giftedName = it.gifted?.nickname ?: it.gifted?.name ?: "Pokémon"
                val toName = it.to?.username ?: it.to?.name ?: targetUsername
                pokemonActionMessage.value = "Gave $giftedName to $toName"
                loadParty()
            }
            .onFailure { reportError(it) }
    }

    // ── dungeon — real data from /api/dungeon/* ─────────────────────────
    val dungeons = mutableStateOf<List<DungeonListEntryDto>>(emptyList())
    val dungeonsLoad = mutableStateOf(LoadState.IDLE)

    val dungeonState = mutableStateOf<DungeonStateDto?>(null)
    val dungeonLog = mutableStateOf<List<String>>(emptyList())
    val dungeonLoad = mutableStateOf(LoadState.IDLE)

    fun loadDungeons() = scope.launch {
        dungeonsLoad.value = LoadState.LOADING
        runCatching { repo.dungeonList() }
            .onSuccess { dungeons.value = it.dungeons; dungeonsLoad.value = LoadState.LOADED }
            .onFailure { reportError(it); dungeonsLoad.value = LoadState.ERROR }
    }

    fun enterDungeon(dungeonId: String) = scope.launch {
        dungeonLoad.value = LoadState.LOADING
        runCatching { repo.dungeonEnter(dungeonId) }
            .onSuccess {
                dungeonState.value = it.state
                dungeonLog.value = it.log
                dungeonLoad.value = LoadState.LOADED
            }
            .onFailure { reportError(it); dungeonLoad.value = LoadState.ERROR }
    }

    fun dungeonAction(action: String, skillId: String? = null, direction: String? = null, target: Int? = null) =
        scope.launch {
            runCatching { repo.dungeonAction(action, skillId, direction, target) }
                .onSuccess { dungeonState.value = it.state; dungeonLog.value = it.log }
                .onFailure { reportError(it) }
        }

    fun leaveDungeon(onDone: () -> Unit = {}) = scope.launch {
        runCatching { repo.dungeonLeave() }
            .onSuccess {
                dungeonState.value = null
                dungeonLog.value = emptyList()
                loadDungeons()
                onDone()
            }
            .onFailure { reportError(it); onDone() }
    }

    // ── not-yet-real systems (no backend endpoint exists yet) ───────────
    val characters = PlaceholderData.characters
    val friends = PlaceholderData.friends
    val notifications = PlaceholderData.notifications
}
