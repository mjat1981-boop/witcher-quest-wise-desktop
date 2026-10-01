package com.example.data.quests

import com.example.data.Quest

object QuestCatalog {
    fun all(): List<Quest> = whiteOrchardQuests() +
        velenQuests() +
        novigradQuests() +
        skelligeQuests() +
        kaerMorhenQuests() +
        toussaintQuests() +
        heartsOfStoneQuests()
}
