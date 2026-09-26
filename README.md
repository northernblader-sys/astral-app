# Astral — Android App

Native Kotlin + Jetpack Compose companion app for the Astral WhatsApp bot.
Same backend as the web client (`astral-bot-production-afb0.up.railway.app`),
new native UI, extra features (dungeon combat, Pokémon PvP, transfers).

## Status

All 5 bottom-tab screens and their pushed sub-screens are built and wired
with real navigation. **Real network calls are wired for everything that
has a backend endpoint today** — no mocking on these:

- Sign in (`/auth/lookup`, `/auth/request-otp`, `/auth/verify-otp`) — full 3-step flow
- Session boot (`/auth/session`), sign out (`/auth/logout`)
- Player profile, wallet, rank, season progress, **real inventory** (`/api/me`)
- Leaderboard (`/api/leaderboard`)
- Shop catalog (`/api/shop`)
- Card tier prices (`/api/cards/prices`)
- Season info (`/api/season`)
- Site-wide stats (`/api/stats`)

Screens for systems with **no backend endpoint yet** (dungeon combat,
Pokémon collection/battle, friends, notifications, transfer) still use
placeholder data, clearly isolated in `data/PlaceholderData.kt` so it's
obvious which four things aren't real yet. See
`bot-api-handoff-prompt.md` for the spec to close that gap.

## Trying it for real

Build and run on a device or emulator, tap **Settings → Sign in**, and use
your actual Astral WhatsApp account. You should see your real name, level,
wallet, and inventory on Home/Profile, and the real live leaderboard.

## Opening this project

1. Open the `astral/` folder in Android Studio (Koala or newer).
2. Let Gradle sync — Android Studio will generate the wrapper
   (`gradlew`, `gradle-wrapper.jar`) automatically on first sync if it's
   missing. If it doesn't, use **File → Sync Project with Gradle Files**,
   or Terminal → `gradle wrapper` once you have Gradle installed locally.
3. Run on an emulator or device (minSdk 24).

## Structure

```
app/src/main/java/com/astralofthesun/app/
  MainActivity.kt          entry point
  data/
    Models.kt               data classes (Player, ShopItem, DungeonEntry, ...)
    Astral.kt                single in-memory state holder, placeholder data
  ui/
    theme/Theme.kt           colors, dark palette, type scale
    components/              AstralCard, SectionHeader, EmptyNote, Coin, Gem
    nav/
      Routes.kt               every route in the app, one place
      BottomBar.kt            5-tab bottom navigation
      AstralNavGraph.kt       NavHost wiring every screen together
    screens/                  one file per screen (see IA below)
```

## Information architecture

```
Home            Shop            Season          Profile         Settings
├ Leaderboard   ├ Item detail   ├ Tier detail    ├ Inventory     └ (sign in/out)
├ Player detail ├ Card vault    ├ Spin banner     ├ Transfer
├ Dungeon select├ Card catalog  └ Pull results    └ Notifications
├ Dungeon battle├ Card detail
└ Pokémon battle├ Premium
                └ Gems checkout

Outside tabs: Splash, Login, Signup
```

## Known placeholders / next steps

- **Signup flow** (`SignupScreen`) still needs building — Login is done and
  real, but the phone → OTP → character-creation path for brand new players
  isn't wired yet.
- **Combat has no backend yet.** The bot's dungeon/Pokémon battle logic
  currently only runs inside the WhatsApp chat handler (`combat-engine.js`,
  `boss-engine.js`, `pokebattle.js` in the bot repo) — there's no API
  surface for the app to call. `DungeonBattleScreen` and
  `PokemonBattleScreen` are UI-complete but their action buttons are
  no-ops until the bot repo adds endpoints like `POST /api/dungeon/action`
  and `POST /api/pokebattle/move` that reuse that same engine server-side.
  See `bot-api-handoff-prompt.md`.
- **In-app purchases are intentionally absent.** Shop/Premium/Gems screens
  are browse-only — no billing flow, per the agreed scope.
- **Detail screens are generic stubs** (`DetailScreen.kt`) for
  item/card/tier/player detail — real layouts pending, though the data
  to back them (player(uid), etc.) is already wired in `AstralRepo`.
- **Battle art** (`res/drawable/char_*`, `bg_*`) is copied from the bot
  repo's CraftPix-licensed sprite pack — fine to ship inside the app,
  see `lib/assets/battle/CREDITS.txt` in the bot repo for provenance.
- **No offline caching / retry UI yet** — a failed request surfaces via
  `Astral.lastError`, but no screen currently displays it or offers retry.
