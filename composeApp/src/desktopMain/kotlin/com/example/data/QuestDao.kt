package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {
    @Query("SELECT * FROM quests ORDER BY recommendedLevel ASC, title ASC")
    fun getAllQuests(): Flow<List<Quest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: Quest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(quests: List<Quest>)

    @Update
    suspend fun updateQuest(quest: Quest)

    @Query("UPDATE quests SET status = :status WHERE id = :id")
    suspend fun updateQuestStatus(id: Int, status: String)

    @Query("UPDATE quests SET notes = :notes WHERE id = :id")
    suspend fun updateQuestNotes(id: Int, notes: String)

    @Query("UPDATE quests SET narrativeChoices = :choices WHERE id = :id")
    suspend fun updateNarrativeChoices(id: Int, choices: String)

    @Delete
    suspend fun deleteQuest(quest: Quest)

    @Query("SELECT COUNT(*) FROM quests")
    suspend fun getQuestCount(): Int

    @Query("SELECT title FROM quests")
    suspend fun getAllQuestTitlesSnapshot(): List<String>
}
