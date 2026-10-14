package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class QuestRepository(
    private val questDao: QuestDao,
    private val saddlebagItemDao: SaddlebagItemDao
) {
    val allQuests: Flow<List<Quest>> = questDao.getAllQuests()
    val allSaddlebagItems: Flow<List<SaddlebagItem>> = saddlebagItemDao.getAllSaddlebagItems()

    suspend fun initializeDefaultQuests() {
        val count = questDao.getQuestCount()
        val preloaded = AppDatabase.getPreloadedQuests()
        if (count == 0) {
            questDao.insertQuests(preloaded)
        } else {
            // Incremental Upgrade: Insert any new walkthrough quests that are missing from database
            val existingTitles = questDao.getAllQuestTitlesSnapshot().toSet()
            val missingQuests = preloaded.filter { it.title !in existingTitles }
            if (missingQuests.isNotEmpty()) {
                questDao.insertQuests(missingQuests)
            }
        }
        // Keep seeded regions, so Hearts of Stone moves to Oxenfurt without touching status.
        for (quest in preloaded) {
            questDao.updateQuestRegionByTitle(quest.title, quest.region)
        }
        
        val catSchoolDiagram = SaddlebagItem(
            name = "Cat School Steel Sword Diagram",
            category = "DIAGRAM",
            quantity = 1,
            description = "A master diagram from the Cat School. Owning it unlocks the Cat appearance when Yoana or Hattori reforge a piece.",
            rarity = "MAGIC",
            iconLabel = "📜"
        )

        // Populate default saddlebag item loadout if empty
        if (saddlebagItemDao.getItemsCount() == 0) {
            val defaultItems = listOf(
                SaddlebagItem(
                    name = "Keira's Magic Lamp",
                    category = "QUEST_ITEM",
                    quantity = 1,
                    description = "An ancient brass lamp gifted by sorceress Keira Metz. Illuminates details of spectral memories of the deceased.",
                    rarity = "RELIC",
                    iconLabel = "🔮"
                ),
                SaddlebagItem(
                    name = "Crystal Raven Skull",
                    category = "QUEST_ITEM",
                    quantity = 1,
                    description = "A powerful military magical focus dropped near the battleground of White Orchard. Exudes magical warmth.",
                    rarity = "RELIC",
                    iconLabel = "💀"
                ),
                SaddlebagItem(
                    name = "Eye of Nehaleni",
                    category = "QUEST_ITEM",
                    quantity = 1,
                    description = "A magical amulet of illusion-breaking gifted by Keira. Instantly disperses glamour barriers and false walls.",
                    rarity = "RELIC",
                    iconLabel = "👁️"
                ),
                SaddlebagItem(
                    name = "Necrophage Oil",
                    category = "ALCHEMY_INGREDIENT",
                    quantity = 1,
                    description = "Lethal blade coating for Silver Swords. Boosts damage by +10% against ghouls, drowners, and grave hags.",
                    rarity = "MAGIC",
                    iconLabel = "🧪"
                ),
                SaddlebagItem(
                    name = "Drowner Brain",
                    category = "ALCHEMY_INGREDIENT",
                    quantity = 4,
                    description = "Preserved brain of water drowners. A vital reagent for concoctions and the critical Swallow healing elixir.",
                    rarity = "COMMON",
                    iconLabel = "🧠"
                ),
                SaddlebagItem(
                    name = "Alcohest Spirit",
                    category = "ALCHEMY_INGREDIENT",
                    quantity = 8,
                    description = "Pure high-potency distillation. Geralt consumes 1 draught automatically during meditation to refresh all decoctions.",
                    rarity = "COMMON",
                    iconLabel = "🍺"
                ),
                SaddlebagItem(
                    name = "Celandine Petals",
                    category = "ALCHEMY_INGREDIENT",
                    quantity = 12,
                    description = "Delicate gold marshlands flower found in Velen. The primary herbal material in brewing the standard Swallow potion.",
                    rarity = "COMMON",
                    iconLabel = "🌿"
                ),
                SaddlebagItem(
                    name = "Fiend Mutagen",
                    category = "ALCHEMY_INGREDIENT",
                    quantity = 1,
                    description = "Glows with raw primal force. When slotted in the character skill tree, boosts physical sword damage.",
                    rarity = "RELIC",
                    iconLabel = "🩸"
                ),
                catSchoolDiagram
            )
            saddlebagItemDao.insertItems(defaultItems)
        } else if (catSchoolDiagram.name !in saddlebagItemDao.getItemNames()) {
            saddlebagItemDao.insertItem(catSchoolDiagram)
        }
    }

    suspend fun insertSaddlebagItem(item: SaddlebagItem) {
        saddlebagItemDao.insertItem(item)
    }

    suspend fun updateSaddlebagItemQuantity(id: Int, quantity: Int) {
        if (quantity <= 0) {
            saddlebagItemDao.deleteItemById(id)
        } else {
            saddlebagItemDao.updateQuantity(id, quantity)
        }
    }

    suspend fun deleteSaddlebagItem(id: Int) {
        saddlebagItemDao.deleteItemById(id)
    }

    suspend fun insert(quest: Quest) {
        questDao.insertQuest(quest)
    }

    suspend fun update(quest: Quest) {
        questDao.updateQuest(quest)
    }

    suspend fun updateStatus(id: Int, status: String) {
        questDao.updateQuestStatus(id, status)
    }

    suspend fun updateTracked(id: Int, tracked: Boolean) {
        questDao.updateQuestTracked(id, tracked)
    }

    suspend fun updateNotes(id: Int, notes: String) {
        questDao.updateQuestNotes(id, notes)
    }

    suspend fun updateNarrativeChoices(id: Int, choices: String) {
        questDao.updateNarrativeChoices(id, choices)
    }

    suspend fun delete(quest: Quest) {
        questDao.deleteQuest(quest)
    }
}
