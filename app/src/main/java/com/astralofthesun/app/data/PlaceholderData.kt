package com.astralofthesun.app.data

/**
 * Placeholder data ONLY for systems that genuinely have no API endpoint on
 * the bot server yet: character roster, friends, notifications. Pokémon
 * party and dungeons are now REAL — loaded live in Astral.kt from
 * /api/pokemon/* and /api/dungeon/*. This file exists so it's obvious, at
 * a glance, which things are still fake.
 */
object PlaceholderData {

    val characters = listOf(
        CharacterEntry(
            id = "shinobi",
            name = "Kaguya",
            title = "Blade of the Hollow Moon",
            spriteClass = "shinobi",
            level = 42,
            maxLevel = 80,
            stars = 5,
            element = "Shadow",
            lore = "Raised in the Hollow, where sunlight never reaches the ground, Kaguya learned " +
                "to move as the dark moves — without sound, without trace. She serves no banner, " +
                "only the debt she owes the one who pulled her from the ash.",
            quote = "The moon doesn't ask permission to fall.",
            atk = 2856,
            def = 1120,
            hp = 8420,
            critRate = 32,
            speed = 108,
            skills = listOf(
                CharacterSkill("Moonless Step", "Vanishes into shadow, dodging the next attack and striking back for 180% ATK."),
                CharacterSkill("Hollow Fang", "A three-hit combo that ignores 20% of the target's defense."),
                CharacterSkill("Eclipse (Ultimate)", "Plunges the battlefield into darkness, dealing heavy Shadow damage to all enemies and reducing their accuracy."),
            ),
        ),
        CharacterEntry(
            id = "samurai",
            name = "Torahime",
            title = "Last Blossom of the Dunes",
            spriteClass = "samurai",
            level = 38,
            maxLevel = 80,
            stars = 5,
            element = "Radiant",
            lore = "The dunes swallowed her clan's estate a generation ago, but Torahime still carries " +
                "its blade and its name. She fights with the precision of someone who has nothing " +
                "left to lose and a house to avenge.",
            quote = "A single cut is enough, if it is the right one.",
            atk = 3120,
            def = 1480,
            hp = 7960,
            critRate = 28,
            speed = 94,
            skills = listOf(
                CharacterSkill("Iaijutsu", "A single precise strike with a high critical chance and bonus damage on crit."),
                CharacterSkill("Dune Guard", "Raises her own defense and taunts enemies for two turns."),
                CharacterSkill("Blossomfall (Ultimate)", "Unleashes a flurry of radiant slashes, dealing massive single-target damage."),
            ),
        ),
        CharacterEntry(
            id = "fighter",
            name = "Bram",
            title = "Fist of the Grotto",
            spriteClass = "fighter",
            level = 35,
            maxLevel = 80,
            stars = 4,
            element = "Stone",
            lore = "Bram grew up brawling in the flooded grotto markets, trading bruises for coin " +
                "before he was old enough to understand why. He hits like the tide — relentless, " +
                "and never quite finished.",
            quote = "Get back up. That's the whole trick.",
            atk = 2640,
            def = 1960,
            hp = 9240,
            critRate = 18,
            speed = 76,
            skills = listOf(
                CharacterSkill("Grotto Hook", "A heavy strike that lowers the target's defense for two turns."),
                CharacterSkill("Iron Stance", "Braces for impact, sharply reducing incoming damage for one turn."),
                CharacterSkill("Tidebreaker (Ultimate)", "Slams the ground, dealing Stone damage to all enemies and stunning the front row."),
            ),
        ),
    )

    val friends = listOf(
        FriendEntry("u1", "Nightqueen", online = true),
        FriendEntry("u2", "Ashborn", online = false),
    )

    val notifications = listOf(
        NotificationEntry("n1", "Season tier unlocked", "You reached tier 4 of the battle pass.", read = false),
        NotificationEntry("n2", "Friend request", "Ashborn wants to add you.", read = true),
    )
}
