package com.example.data

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Exercises the actual SQL through an in-memory Room database with the same
 * bundled SQLite driver the app uses — ordering clauses, targeted UPDATEs,
 * the LIKE search, the Converters round-trip against a real column, and the
 * repository's initializeDefaultQuests seeding/upgrade logic.
 */
class AppDatabaseDaoTest {

    private lateinit var db: AppDatabase

    @BeforeTest
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder<AppDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.Default)
            .build()
    }

    @AfterTest
    fun tearDown() {
        db.close()
    }

    private fun quest(title: String, level: Int, type: String = "SIDE") = Quest(
        title = title, type = type, region = "VELEN", recommendedLevel = level,
        description = "d", questgiver = "g", rewards = "r", status = "NOT_STARTED",
    )

    @Test
    fun questsAreOrderedByLevelThenTitle() = runTest {
        val dao = db.questDao()
        dao.insertQuests(listOf(quest("Beta", 5), quest("Alpha", 5), quest("Gamma", 1)))

        assertEquals(listOf("Gamma", "Alpha", "Beta"), dao.getAllQuests().first().map { it.title })
    }

    @Test
    fun updateQueriesTouchOnlyTheTargetRow() = runTest {
        val dao = db.questDao()
        dao.insertQuests(listOf(quest("One", 1), quest("Two", 2)))
        val (one, two) = dao.getAllQuests().first()

        dao.updateQuestStatus(one.id, "COMPLETED")
        dao.updateQuestNotes(one.id, "wind's howling")
        dao.updateNarrativeChoices(one.id, "spared the ghoul")
        dao.updateQuestTracked(one.id, true)

        val after = dao.getAllQuests().first().associateBy { it.id }
        assertEquals("COMPLETED", after.getValue(one.id).status)
        assertEquals("wind's howling", after.getValue(one.id).notes)
        assertEquals("spared the ghoul", after.getValue(one.id).narrativeChoices)
        assertEquals(true, after.getValue(one.id).tracked)
        assertEquals("NOT_STARTED", after.getValue(two.id).status)
        assertEquals("", after.getValue(two.id).notes)
        assertEquals(false, after.getValue(two.id).tracked)
    }

    @Test
    fun countAndTitleSnapshotReflectInserts() = runTest {
        val dao = db.questDao()
        assertEquals(0, dao.getQuestCount())

        dao.insertQuests(listOf(quest("A", 1), quest("B", 2)))
        assertEquals(2, dao.getQuestCount())
        assertEquals(setOf("A", "B"), dao.getAllQuestTitlesSnapshot().toSet())
    }

    @Test
    fun monsterWeaknessesListSurvivesARealRoundTrip() = runTest {
        val dao = db.monsterDao()
        val weaknesses = listOf("Yrden (Sign)", "Specter Oil", "Moon \"Dust\" Bomb")
        dao.insertMonster(
            Monster(name = "Test Wraith", category = "SPECTER", description = "d",
                weaknesses = weaknesses, combatGuide = "c")
        )

        // Exercises Converters through an actual SQLite column, not just in memory.
        assertEquals(weaknesses, dao.getAllMonsters().first().single().weaknesses)
    }

    @Test
    fun searchMonstersMatchesNameOrCategoryViaLike() = runTest {
        val dao = db.monsterDao()
        val repo = MonsterRepository(dao)
        dao.insertMonster(Monster(name = "Noonwraith", category = "SPECTER", description = "d", weaknesses = emptyList(), combatGuide = "c"))
        dao.insertMonster(Monster(name = "Royal Griffin", category = "HYBRID", description = "d", weaknesses = emptyList(), combatGuide = "c"))

        assertEquals(listOf("Noonwraith"), repo.searchMonsters("wraith").first().map { it.name })
        assertEquals(listOf("Royal Griffin"), repo.searchMonsters("HYBRID").first().map { it.name })
        assertEquals(emptyList(), repo.searchMonsters("kikimora").first())
    }

    @Test
    fun saddlebagUpdateAndDeleteTargetById() = runTest {
        val dao = db.saddlebagItemDao()
        dao.insertItems(
            listOf(
                SaddlebagItem(name = "Petals", category = "ALCHEMY_INGREDIENT", quantity = 3, description = "", rarity = "COMMON", iconLabel = "🌿"),
                SaddlebagItem(name = "Lamp", category = "QUEST_ITEM", quantity = 1, description = "", rarity = "RELIC", iconLabel = "🔮"),
            )
        )
        val items = dao.getAllSaddlebagItems().first()
        val petals = items.first { it.name == "Petals" }

        dao.updateQuantity(petals.id, 7)
        assertEquals(7, dao.getAllSaddlebagItems().first().first { it.name == "Petals" }.quantity)

        dao.deleteItemById(petals.id)
        assertEquals(listOf("Lamp"), dao.getAllSaddlebagItems().first().map { it.name })
        assertEquals(1, dao.getItemsCount())
    }

    @Test
    fun initializeDefaultQuestsSeedsOnceAndBackfillsMissingTitles() = runTest {
        val repo = QuestRepository(db.questDao(), db.saddlebagItemDao())
        val preloadedCount = AppDatabase.getPreloadedQuests().size

        // Fresh DB: full seed (quests + default saddlebag loadout).
        repo.initializeDefaultQuests()
        assertEquals(preloadedCount, db.questDao().getQuestCount())
        assertTrue(db.saddlebagItemDao().getItemsCount() > 0)

        // Second run: idempotent.
        repo.initializeDefaultQuests()
        assertEquals(preloadedCount, db.questDao().getQuestCount())

        // Delete one preloaded quest; the incremental-upgrade path restores it.
        val victim = db.questDao().getAllQuests().first().first()
        db.questDao().deleteQuest(victim)
        assertEquals(preloadedCount - 1, db.questDao().getQuestCount())
        repo.initializeDefaultQuests()
        assertEquals(preloadedCount, db.questDao().getQuestCount())
        assertTrue(db.questDao().getAllQuestTitlesSnapshot().contains(victim.title))
    }

    @Test
    fun everyPaintedRegionHasQuestsAndHeartsOfStoneIsOxenfurt() {
        val quests = AppDatabase.getPreloadedQuests()
        val painted = listOf(
            "WHITE_ORCHARD", "VELEN", "NOVIGRAD", "SKELLIGE",
            "KAER_MORHEN", "TOUSSAINT", "HEART_OF_STONE"
        )
        for (region in painted) {
            assertTrue(quests.any { it.region == region }, region)
        }
        assertEquals(
            "HEART_OF_STONE",
            quests.first { it.title == "Evil's Soft First Touches" }.region
        )
    }

    @Test
    fun choiceQuestHasDialogueAndPlainTreasureDoesNot() {
        assertNotNull(QuestDecisionTree.getTreeForQuest("Family Matters"))
        assertNotNull(QuestDecisionTree.getTreeForQuest("Whatsoever a Man Soweth..."))
        assertNotNull(QuestDecisionTree.getTreeForQuest("For the Advancement of Learning"))
        assertNotNull(QuestDecisionTree.getTreeForQuest("Final Preparations"))
        assertNotNull(QuestDecisionTree.getTreeForQuest("The Child of the Elder Blood"))
        val battlefield = QuestDecisionTree.getTreeForQuest("Blood on the Battlefield")
        assertNotNull(battlefield)
        val lines = battlefield.paths.joinToString(" ") { it.choiceName }
        assertTrue(lines.contains("lift your spirits"))
        assertTrue(lines.contains("don't have to be good at everything"))
        assertTrue(!lines.contains("laboratory") && !lines.contains("Go for it"))
        assertNull(QuestDecisionTree.getTreeForQuest("Dirty Funds"))
    }
}
