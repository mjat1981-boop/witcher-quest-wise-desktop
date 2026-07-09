package com.example.ui

import com.example.data.InMemoryMonsterDao
import com.example.data.InMemoryQuestDao
import com.example.data.InMemorySaddlebagDao
import com.example.data.MonsterRepository
import com.example.data.Quest
import com.example.data.QuestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Covers the filteredQuests pipeline: search across title/description/
 * questgiver/region/type, the type/region/status/level-range filters
 * (including the SIDE_CONTRACT combo), and the three sort orders. The quest
 * flow is fed by an in-memory DAO with a fixed fixture; bulk inserts are
 * disabled so initializeDefaultQuests can't pollute it.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class QuestViewModelFilteredQuestsTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun quest(
        title: String,
        type: String,
        region: String,
        level: Int,
        status: String,
        questgiver: String = "Notice board",
        description: String = "A quest.",
    ) = Quest(
        title = title, type = type, region = region, recommendedLevel = level,
        description = description, questgiver = questgiver, rewards = "XP", status = status,
    )

    private val fixture = listOf(
        quest("Axii Lessons", "MAIN", "VELEN", 3, "IN_PROGRESS"),
        quest("Bear Necessities", "SIDE", "SKELLIGE", 24, "NOT_STARTED"),
        quest("Contract: Griffin", "CONTRACT", "WHITE_ORCHARD", 8, "COMPLETED", description = "A griffin nests near the mill."),
        quest("Zenith Treasure", "TREASURE", "NOVIGRAD", 30, "NOT_STARTED"),
        quest("Apple Hunt", "SIDE", "VELEN", 3, "FAILED", questgiver = "Bloody Baron"),
    )

    /** Spin up a VM over the fixture, keep filteredQuests collected, run assertions. */
    private fun withVm(block: TestScope.(QuestViewModel) -> Unit) = runTest(dispatcher) {
        val vm = QuestViewModel(
            QuestRepository(InMemoryQuestDao(fixture, acceptBulkInserts = false), InMemorySaddlebagDao()),
            MonsterRepository(InMemoryMonsterDao()),
        )
        backgroundScope.launch { vm.filteredQuests.collect { } }
        advanceUntilIdle()
        block(vm)
    }

    private fun titles(vm: QuestViewModel) = vm.filteredQuests.value.map { it.title }

    @Test
    fun defaultStateShowsAllQuestsSortedByLevelAscending() = withVm { vm ->
        assertEquals(
            listOf("Apple Hunt", "Axii Lessons", "Contract: Griffin", "Bear Necessities", "Zenith Treasure"),
            titles(vm),
        )
    }

    @Test
    fun searchMatchesTitleDescriptionQuestgiverRegionAndType() = withVm { vm ->
        vm.setSearchQuery("griffin") // title + description of the same quest
        advanceUntilIdle()
        assertEquals(listOf("Contract: Griffin"), titles(vm))

        vm.setSearchQuery("baron") // questgiver
        advanceUntilIdle()
        assertEquals(listOf("Apple Hunt"), titles(vm))

        vm.setSearchQuery("white orchard") // region, underscore replaced by space
        advanceUntilIdle()
        assertEquals(listOf("Contract: Griffin"), titles(vm))

        vm.setSearchQuery("treasure") // quest type, case-insensitive
        advanceUntilIdle()
        assertEquals(listOf("Zenith Treasure"), titles(vm))

        vm.setSearchQuery("no such thing anywhere")
        advanceUntilIdle()
        assertEquals(emptyList(), titles(vm))
    }

    @Test
    fun typeFilterIncludesSideContractCombo() = withVm { vm ->
        vm.setTypeFilter("MAIN")
        advanceUntilIdle()
        assertEquals(listOf("Axii Lessons"), titles(vm))

        vm.setTypeFilter("SIDE_CONTRACT")
        advanceUntilIdle()
        assertEquals(listOf("Apple Hunt", "Contract: Griffin", "Bear Necessities"), titles(vm))

        vm.setTypeFilter("ALL")
        advanceUntilIdle()
        assertEquals(5, titles(vm).size)
    }

    @Test
    fun regionAndStatusFiltersNarrowTheList() = withVm { vm ->
        vm.setRegionFilter("VELEN")
        advanceUntilIdle()
        assertEquals(listOf("Apple Hunt", "Axii Lessons"), titles(vm))

        vm.setRegionFilter("ALL")
        vm.setStatusFilter("NOT_STARTED")
        advanceUntilIdle()
        assertEquals(listOf("Bear Necessities", "Zenith Treasure"), titles(vm))
    }

    @Test
    fun levelRangeFiltersMatchDocumentedBuckets() = withVm { vm ->
        val cases = mapOf(
            "1_5" to listOf("Apple Hunt", "Axii Lessons"),
            "6_15" to listOf("Contract: Griffin"),
            "16_25" to listOf("Bear Necessities"),
            "26_UP" to listOf("Zenith Treasure"),
        )
        for ((range, expected) in cases) {
            vm.setLevelRangeFilter(range)
            advanceUntilIdle()
            assertEquals(expected, titles(vm), "range $range")
        }
    }

    @Test
    fun sortOrdersAreApplied() = withVm { vm ->
        vm.setSortBy("LEVEL_DESC")
        advanceUntilIdle()
        assertEquals(
            listOf("Zenith Treasure", "Bear Necessities", "Contract: Griffin", "Apple Hunt", "Axii Lessons"),
            titles(vm),
        )

        vm.setSortBy("TITLE_ASC")
        advanceUntilIdle()
        assertEquals(
            listOf("Apple Hunt", "Axii Lessons", "Bear Necessities", "Contract: Griffin", "Zenith Treasure"),
            titles(vm),
        )
    }

    @Test
    fun filtersCompose() = withVm { vm ->
        vm.setTypeFilter("SIDE")
        vm.setRegionFilter("VELEN")
        advanceUntilIdle()
        assertEquals(listOf("Apple Hunt"), titles(vm))

        vm.setSearchQuery("bear") // no SIDE quest in VELEN mentions bear
        advanceUntilIdle()
        assertEquals(emptyList(), titles(vm))
    }
}
