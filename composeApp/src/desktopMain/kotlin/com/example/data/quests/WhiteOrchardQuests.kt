package com.example.data.quests

import com.example.data.Quest

internal fun whiteOrchardQuests(): List<Quest> = listOf(
    quest("Lilac and Gooseberries", "MAIN", "WHITE_ORCHARD", 1, "Geralt follows a letter scented with lilac and gooseberries to find Yennefer.", "Vesemir", "150 XP"),
    quest("The Beast of White Orchard", "MAIN", "WHITE_ORCHARD", 3, "The Nilfgaardian garrison hires Geralt to kill the griffin nesting by the mill.", "Captain Peter Saar Gwynleve", "250 XP, 100 crowns", "Hybrid Oil, Grapeshot, Aard"),
    quest("The Incident at White Orchard", "MAIN", "WHITE_ORCHARD", 2, "After the griffin falls, Geralt is questioned about Yennefer and the war in the village.", "Captain Peter Saar Gwynleve", "100 XP"),
    quest("Imperial Audience", "MAIN", "WHITE_ORCHARD", 4, "Emhyr var Emreis summons Geralt to Vizima and sets him on Ciri's trail.", "Emhyr var Emreis", "200 XP"),
    quest("Missing in Action", "SIDE", "WHITE_ORCHARD", 2, "Duny asks Geralt to find his brother Bastien, missing after the battle.", "Duny", "50 XP"),
    quest("Twisted Firestarter", "SIDE", "WHITE_ORCHARD", 3, "The dwarf smith Willis wants the arsonist who burned his forge.", "Willis", "80 XP"),
    quest("Precious Cargo", "SIDE", "WHITE_ORCHARD", 4, "A merchant's box of medicine in the swamp is not what it seems.", "Merchant", "70 XP"),
    quest("On Death's Bed", "SIDE", "WHITE_ORCHARD", 2, "Tomira needs a swallow potion to save a wounded woman from the battlefield.", "Tomira", "Swallow diagram, 50 XP"),
    quest("A Frying Pan, Spanner in the Works", "SIDE", "WHITE_ORCHARD", 3, "A woman and a Nilfgaardian deserter each claim the other stole their livelihood.", "Peasant woman", "40 XP"),
    quest("Faithful Friend", "SIDE", "WHITE_ORCHARD", 5, "A letter sends Geralt back to White Orchard to settle a dog and a dead man's last request.", "Letter", "60 XP"),
    quest("Devil by the Well", "CONTRACT", "WHITE_ORCHARD", 4, "A noonwraith haunts the well at the abandoned village.", "Odolan", "200 crowns", "Specter Oil, Yrden, Moon Dust"),
    quest("Scavenger Hunt: Viper Gear", "TREASURE", "WHITE_ORCHARD", 5, "Diagrams for the Viper School silver and steel swords are hidden around White Orchard.", "Notice", "Viper sword diagrams"),
    quest("Deserter Gold", "TREASURE", "WHITE_ORCHARD", 3, "A Nilfgaardian deserter buried his pay near the garrison road.", "Note", "Crowns"),
    quest("Dirty Funds", "TREASURE", "WHITE_ORCHARD", 4, "Stolen coin is stashed where the fighting passed through the orchard.", "Note", "Crowns"),
    quest("Temerian Valuables", "TREASURE", "WHITE_ORCHARD", 4, "A Temerian soldier hid valuables before the Nilfgaardian advance.", "Note", "Crowns, crafting parts"),
    quest("Places of Power: Exploration", "TREASURE", "WHITE_ORCHARD", 1, "Places of Power around the region grant a skill point the first time they are drawn.", "Exploration", "Skill point"),
    quest("Something Ends, Something Begins", "MAIN", "WHITE_ORCHARD", 30, "The story ends at the White Orchard inn, with whoever still walks the Path beside Geralt.", "Ciri", "The ending"),
    quest("Collect 'em All", "SIDE", "WHITE_ORCHARD", 5, "The innkeep at White Orchard starts Geralt collecting the Gwent cards still scattered across the world.", "Elsa", "Gwent cards")
)
