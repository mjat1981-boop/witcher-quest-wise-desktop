package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MonsterDao {
    @Query("SELECT * FROM monsters")
    fun getAllMonsters(): Flow<List<Monster>>

    @Query("SELECT * FROM monsters WHERE name LIKE :query OR category LIKE :query")
    fun searchMonsters(query: String): Flow<List<Monster>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonster(monster: Monster)

    @Update
    suspend fun updateMonster(monster: Monster)

    @Delete
    suspend fun deleteMonster(monster: Monster)
}
