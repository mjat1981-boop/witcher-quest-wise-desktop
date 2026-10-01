package com.example.data.quests

import com.example.data.Quest

internal fun heartsOfStoneQuests(): List<Quest> = listOf(
    quest("Evil's Soft First Touches", "MAIN", "HEART_OF_STONE", 32, "A contract near Oxenfurt puts Gaunter O'Dimm's mark on Geralt's face.", "Gaunter O'Dimm", "300 XP"),
    quest("Open Sesame!", "MAIN", "HEART_OF_STONE", 33, "Olgierd von Everec wants a heist on a heavily guarded auction house.", "Olgierd von Everec", "350 XP"),
    quest("Whatsoever a Man Soweth...", "MAIN", "HEART_OF_STONE", 35, "Geralt faces Gaunter O'Dimm at the Temple of Lilvani and can save Olgierd or take the reward.", "Olgierd von Everec", "500 XP"),
    quest("Scenes From a Marriage", "MAIN", "HEART_OF_STONE", 34, "The rose-tinted world of Olgierd and Iris still holds the truth of their wedding.", "Olgierd von Everec", "400 XP"),
    quest("Dead Man's Party", "SIDE", "HEART_OF_STONE", 32, "Olgierd's dead brother wants one last night of drinking among the living.", "Shani", "300 XP"),
    quest("A Midnight Clear", "SIDE", "HEART_OF_STONE", 33, "Shani asks Geralt to spend the solstice with her, and the evening can stay or end.", "Shani", "200 XP"),
    quest("Rose on a Red Field", "SIDE", "HEART_OF_STONE", 33, "The Von Everec rose will not open until the family's dead are faced.", "Olgierd von Everec", "200 XP"),
    quest("Without a Trace", "SIDE", "HEART_OF_STONE", 32, "A man vanished from the Oxenfurt road, and the Ofieri are the reason.", "Notice", "150 XP"),
    quest("From Ofier's Distant Shores", "SIDE", "HEART_OF_STONE", 34, "Ofieri runes and a ship from the east are still in the sewers under Oxenfurt.", "Ofieri", "200 XP"),
    quest("Enchanting: Start-up Costs", "SIDE", "HEART_OF_STONE", 32, "The runewright will work if Geralt funds the first of his rituals.", "Runewright", "Runewords"),
    quest("Enchanting: Quality Has Its Price", "SIDE", "HEART_OF_STONE", 34, "The runewright's better work costs more than coin.", "Runewright", "Runewords"),
    quest("Avid Collector", "SIDE", "HEART_OF_STONE", 33, "A collector in Oxenfurt pays for paintings that are not all paintings.", "Collector", "Crowns"),
    quest("Races: Swift as the Western Winds", "SIDE", "HEART_OF_STONE", 30, "A horse race outside Oxenfurt is a cover for a debt.", "Race official", "Crowns"),
    quest("Contract: The Oxenfurt Drunk", "CONTRACT", "HEART_OF_STONE", 22, "A drowned dead man keeps climbing out of Oxenfurt's canals.", "Notice board", "300 crowns", "Necrophage Oil"),
    quest("Contract: An Elusive Thief", "CONTRACT", "HEART_OF_STONE", 20, "Oxenfurt's thief is a katakan.", "Notice board", "320 crowns", "Vampire Oil, Black Blood")
)
