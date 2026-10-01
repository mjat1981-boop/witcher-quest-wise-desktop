package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * In-memory DAO fakes shared by the ViewModel tests. They keep real mutable
 * state (unlike the inert fakes in QuestViewModelSkillBuildTest) so tests can
 * observe the effects of repository writes. SQL semantics themselves are
 * covered separately by AppDatabaseDaoTest against a real Room database.
 */

internal class InMemoryQuestDao(
    initial: List<Quest> = emptyList(),
    /**
     * QuestRepository.initializeDefaultQuests bulk-inserts every preloaded
     * quest that is missing by title. Tests that need a fixed fixture set
     * this to false so the fixture isn't polluted.
     */
    private val acceptBulkInserts: Boolean = true,
) : QuestDao {
    private var nextId = 1
    private val state = MutableStateFlow(initial.map { withId(it) })

    private fun withId(q: Quest): Quest = if (q.id == 0) q.copy(id = nextId++) else q.also { nextId = maxOf(nextId, it.id + 1) }

    override fun getAllQuests(): Flow<List<Quest>> =
        state.map { list -> list.sortedWith(compareBy({ it.recommendedLevel }, { it.title })) }

    override suspend fun insertQuest(quest: Quest) = state.update { it + withId(quest) }

    override suspend fun insertQuests(quests: List<Quest>) {
        if (!acceptBulkInserts) return
        state.update { cur -> cur + quests.map { withId(it) } }
    }

    override suspend fun updateQuest(quest: Quest) =
        state.update { l -> l.map { if (it.id == quest.id) quest else it } }

    override suspend fun updateQuestStatus(id: Int, status: String) =
        state.update { l -> l.map { if (it.id == id) it.copy(status = status) else it } }

    override suspend fun updateQuestTracked(id: Int, tracked: Boolean) =
        state.update { l -> l.map { if (it.id == id) it.copy(tracked = tracked) else it } }

    override suspend fun updateQuestNotes(id: Int, notes: String) =
        state.update { l -> l.map { if (it.id == id) it.copy(notes = notes) else it } }

    override suspend fun updateNarrativeChoices(id: Int, choices: String) =
        state.update { l -> l.map { if (it.id == id) it.copy(narrativeChoices = choices) else it } }

    override suspend fun deleteQuest(quest: Quest) =
        state.update { l -> l.filterNot { it.id == quest.id } }

    override suspend fun getQuestCount(): Int = state.value.size

    override suspend fun getAllQuestTitlesSnapshot(): List<String> = state.value.map { it.title }
}

internal class InMemorySaddlebagDao(
    initial: List<SaddlebagItem> = emptyList(),
) : SaddlebagItemDao {
    private var nextId = 1
    private val state = MutableStateFlow(initial.map { withId(it) })

    private fun withId(i: SaddlebagItem): SaddlebagItem =
        if (i.id == 0) i.copy(id = nextId++) else i.also { nextId = maxOf(nextId, it.id + 1) }

    /** Current contents, for assertions. */
    fun snapshot(): List<SaddlebagItem> = state.value

    override fun getAllSaddlebagItems(): Flow<List<SaddlebagItem>> = state

    override suspend fun getItemsCount(): Int = state.value.size

    override suspend fun insertItem(item: SaddlebagItem) = state.update { it + withId(item) }

    override suspend fun insertItems(items: List<SaddlebagItem>) =
        state.update { cur -> cur + items.map { withId(it) } }

    override suspend fun deleteItemById(id: Int) =
        state.update { l -> l.filterNot { it.id == id } }

    override suspend fun updateQuantity(id: Int, quantity: Int) =
        state.update { l -> l.map { if (it.id == id) it.copy(quantity = quantity) else it } }
}

internal class InMemoryMonsterDao(
    initial: List<Monster> = emptyList(),
) : MonsterDao {
    private var nextId = 1
    private val state = MutableStateFlow(initial.map { withId(it) })

    private fun withId(m: Monster): Monster =
        if (m.id == 0) m.copy(id = nextId++) else m.also { nextId = maxOf(nextId, it.id + 1) }

    override fun getAllMonsters(): Flow<List<Monster>> = state

    override fun searchMonsters(query: String): Flow<List<Monster>> {
        // Mirror the DAO's `LIKE :query` semantics for a %term% pattern.
        val term = query.trim('%')
        return state.map { l ->
            l.filter { it.name.contains(term, true) || it.category.contains(term, true) }
        }
    }

    override suspend fun insertMonster(monster: Monster) = state.update { it + withId(monster) }

    override suspend fun updateMonster(monster: Monster) =
        state.update { l -> l.map { if (it.id == monster.id) monster else it } }

    override suspend fun deleteMonster(monster: Monster) =
        state.update { l -> l.filterNot { it.id == monster.id } }
}
