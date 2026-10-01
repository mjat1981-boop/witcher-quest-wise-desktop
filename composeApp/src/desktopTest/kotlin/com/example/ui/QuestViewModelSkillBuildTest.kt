package com.example.ui

import com.example.data.Monster
import com.example.data.MonsterDao
import com.example.data.MonsterRepository
import com.example.data.Quest
import com.example.data.QuestDao
import com.example.data.QuestRepository
import com.example.data.SaddlebagItem
import com.example.data.SaddlebagItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Exercises the synchronous skill-point and reforge logic in QuestViewModel —
 * the point pool, three-rank cap, prerequisite gates, recommended builds, and
 * Yoana/Hattori reforging. These paths are pure state updates on
 * MutableStateFlow, so they run without Room or the network; the fake DAOs
 * below exist only to satisfy the repository constructors. viewModelScope needs
 * a Main dispatcher, so we install a StandardTestDispatcher (its coroutines
 * stay parked since we never advance it — the methods under test don't launch).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class QuestViewModelSkillBuildTest {

    private val dispatcher = StandardTestDispatcher()

    private fun newViewModel(): QuestViewModel {
        val questRepo = QuestRepository(FakeQuestDao(), FakeSaddlebagItemDao())
        val monsterRepo = MonsterRepository(FakeMonsterDao())
        return QuestViewModel(questRepo, monsterRepo)
    }

    private fun QuestViewModel.levelOf(id: String): Int =
        witcherSkills.value.first { it.id == id }.level

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun setWitcherLevelClampsToValidRange() {
        val vm = newViewModel()

        vm.setWitcherLevel(42)
        assertEquals(42, vm.witcherLevel.value)

        vm.setWitcherLevel(0)   // below range, ignored
        assertEquals(42, vm.witcherLevel.value)

        vm.setWitcherLevel(101) // above range, ignored
        assertEquals(42, vm.witcherLevel.value)

        vm.setWitcherLevel(1)
        assertEquals(1, vm.witcherLevel.value)
        vm.setWitcherLevel(100)
        assertEquals(100, vm.witcherLevel.value)
    }

    @Test
    fun adjustSkillPointsRespectsMaxLevelAndFloor() {
        val vm = newViewModel()
        vm.setWitcherLevel(50) // large pool so we test clamping, not exhaustion

        // strength_training starts at 0, max 3
        vm.adjustSkillPoints("strength_training", 1)
        assertEquals(1, vm.levelOf("strength_training"))

        // cannot drop below 0
        vm.adjustSkillPoints("strength_training", -1)
        vm.adjustSkillPoints("strength_training", -1)
        assertEquals(0, vm.levelOf("strength_training"))

        // cannot jump past max in one step (newLevel must be within 0..maxLevel)
        vm.adjustSkillPoints("strength_training", 5)
        assertEquals(0, vm.levelOf("strength_training"))

        // unknown skill id is a no-op; Remastered starts every skill at rank 0
        vm.adjustSkillPoints("does_not_exist", 1)
        assertEquals(0, vm.witcherSkills.value.sumOf { it.level })
    }

    @Test
    fun adjustSkillPointsIsBlockedWhenPointPoolIsExhausted() {
        val vm = newViewModel()
        // Default: witcherLevel 1 -> pool of 6. Remastered reset spends nothing.
        assertEquals(0, vm.witcherSkills.value.sumOf { it.level })

        repeat(3) { vm.adjustSkillPoints("muscle_memory", 1) }
        repeat(3) { vm.adjustSkillPoints("strength_training", 1) }
        assertEquals(6, vm.witcherSkills.value.sumOf { it.level })

        // Rend's prerequisite is met, but the pool is exhausted.
        vm.adjustSkillPoints("rend", 1)
        assertEquals(6, vm.witcherSkills.value.sumOf { it.level })
        assertEquals(0, vm.levelOf("rend"))
    }

    @Test
    fun rankUpIsBlockedUntilPrerequisiteIsLearned() {
        val vm = newViewModel()
        vm.setWitcherLevel(20)

        vm.adjustSkillPoints("fleet_footed", 1)
        assertEquals(0, vm.levelOf("fleet_footed"))
        vm.adjustSkillPoints("arrow_deflection", 1)
        assertEquals(0, vm.levelOf("arrow_deflection"))

        vm.adjustSkillPoints("muscle_memory", 1)
        vm.adjustSkillPoints("fleet_footed", 1)
        assertEquals(1, vm.levelOf("fleet_footed"))
        vm.adjustSkillPoints("arrow_deflection", 1)
        assertEquals(1, vm.levelOf("arrow_deflection"))
    }

    @Test
    fun rankDownToZeroIsBlockedWhileADependentIsAllocated() {
        val vm = newViewModel()
        vm.setWitcherLevel(20)

        vm.adjustSkillPoints("muscle_memory", 1)
        vm.adjustSkillPoints("whirl", 1)
        vm.adjustSkillPoints("muscle_memory", -1)
        assertEquals(1, vm.levelOf("muscle_memory"))
        assertEquals(1, vm.levelOf("whirl"))

        vm.adjustSkillPoints("whirl", -1)
        vm.adjustSkillPoints("muscle_memory", -1)
        assertEquals(0, vm.levelOf("whirl"))
        assertEquals(0, vm.levelOf("muscle_memory"))
    }

    @Test
    fun generalSkillsAcceptThreeRanks() {
        val vm = newViewModel()
        vm.setWitcherLevel(20)

        val general = vm.witcherSkills.value.filter { it.category == "GENERAL" }
        assertTrue(general.isNotEmpty())
        assertTrue(general.all { it.maxLevel == 3 && it.level == 0 })

        repeat(3) { vm.adjustSkillPoints("gourmet", 1) }
        assertEquals(3, vm.levelOf("gourmet"))
        vm.adjustSkillPoints("gourmet", 1)
        assertEquals(3, vm.levelOf("gourmet"))
    }

    @Test
    fun recommendedBuildsFitTheSkillTree() {
        val vm = newViewModel()
        for (school in listOf("Cat", "Griffin", "Bear", "Wolf", "Viper", "Manticore")) {
            vm.applyRecommendedBuild(school)
            val skills = vm.witcherSkills.value
            skills.filter { it.level > 0 }.forEach { skill ->
                skill.prerequisiteIds.forEach { prereqId ->
                    val prerequisite = skills.first { it.id == prereqId }
                    assertTrue(
                        prerequisite.level >= 1,
                        "${skill.id} in $school requires $prereqId"
                    )
                }
            }
        }
    }

    @Test
    fun reforgeChangesAppearanceAndKeepsStatsAndDye() {
        val vm = newViewModel()
        val before = vm.gearSlots.value.first { it.id == "armor" }
        assertEquals("Toussaint Burgundy", before.dye)

        vm.unlockSchoolLook("cat")
        assertTrue(vm.reforgeEquipment("armor", "cat", "Yoana"))

        val after = vm.gearSlots.value.first { it.id == "armor" }
        assertEquals("Cat", after.appearance)
        assertEquals(before.statName, after.statName)
        assertEquals(before.dye, after.dye)
    }

    @Test
    fun reforgeIsRefusedForLockedLooksAndOtherCraftsmen() {
        val vm = newViewModel()
        val before = vm.gearSlots.value.map { it.appearance to it.statName to it.dye }

        assertFalse(vm.reforgeEquipment("armor", "griffin", "Yoana"))
        vm.unlockSchoolLook("griffin")
        assertFalse(vm.reforgeEquipment("armor", "griffin", "Fergus"))
        assertEquals(before, vm.gearSlots.value.map { it.appearance to it.statName to it.dye })

        assertTrue(vm.reforgeEquipment("armor", "griffin", "Hattori"))
        val after = vm.gearSlots.value.first { it.id == "armor" }
        assertEquals("Griffin", after.appearance)
        assertEquals("Mastercrafted Wolven Armor", after.statName)
        assertEquals("Toussaint Burgundy", after.dye)
    }

    @Test
    fun applyRecommendedBuildAllocatesAndAutoBumpsLevel() {
        val vm = newViewModel()
        // "Cat": cat_school=1, muscle_memory=3, fleet_footed=3, whirl=3 => 10 points.
        // Default pool (level 1 -> 6) is too small, so level should auto-bump.
        vm.applyRecommendedBuild("Cat")

        assertEquals(1, vm.levelOf("cat_school"))
        assertEquals(3, vm.levelOf("muscle_memory"))
        assertEquals(3, vm.levelOf("fleet_footed"))
        assertEquals(3, vm.levelOf("whirl"))
        // A previously seeded skill outside the build is cleared.
        assertEquals(0, vm.levelOf("delusion"))
        // neededPoints(10) - 5 + 1 == 6, so the pool now covers the 10 spent.
        assertEquals(6, vm.witcherLevel.value)
        assertEquals(10, vm.witcherSkills.value.sumOf { it.level })
    }

    @Test
    fun applyRecommendedBuildWithUnknownSchoolClearsAllSkills() {
        val vm = newViewModel()
        vm.applyRecommendedBuild("NotARealSchool")

        assertEquals(0, vm.witcherSkills.value.sumOf { it.level })
    }
}

// --- Fakes: satisfy repository constructors; no persistence needed. ---

private class FakeQuestDao : QuestDao {
    override fun getAllQuests(): Flow<List<Quest>> = flowOf(emptyList())
    override suspend fun insertQuest(quest: Quest) {}
    override suspend fun insertQuests(quests: List<Quest>) {}
    override suspend fun updateQuest(quest: Quest) {}
    override suspend fun updateQuestStatus(id: Int, status: String) {}
    override suspend fun updateQuestTracked(id: Int, tracked: Boolean) {}
    override suspend fun updateQuestNotes(id: Int, notes: String) {}
    override suspend fun updateNarrativeChoices(id: Int, choices: String) {}
    override suspend fun deleteQuest(quest: Quest) {}
    override suspend fun getQuestCount(): Int = 0
    override suspend fun getAllQuestTitlesSnapshot(): List<String> = emptyList()
}

private class FakeSaddlebagItemDao : SaddlebagItemDao {
    override fun getAllSaddlebagItems(): Flow<List<SaddlebagItem>> = flowOf(emptyList())
    override suspend fun getItemsCount(): Int = 0
    override suspend fun insertItem(item: SaddlebagItem) {}
    override suspend fun insertItems(items: List<SaddlebagItem>) {}
    override suspend fun deleteItemById(id: Int) {}
    override suspend fun updateQuantity(id: Int, quantity: Int) {}
}

private class FakeMonsterDao : MonsterDao {
    override fun getAllMonsters(): Flow<List<Monster>> = flowOf(emptyList())
    override fun searchMonsters(query: String): Flow<List<Monster>> = flowOf(emptyList())
    override suspend fun insertMonster(monster: Monster) {}
    override suspend fun updateMonster(monster: Monster) {}
    override suspend fun deleteMonster(monster: Monster) {}
}
