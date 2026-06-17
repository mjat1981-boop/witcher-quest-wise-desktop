package com.example.data

import kotlinx.coroutines.flow.Flow

class MonsterRepository(private val monsterDao: MonsterDao) {
    val allMonsters: Flow<List<Monster>> = monsterDao.getAllMonsters()

    fun searchMonsters(query: String): Flow<List<Monster>> = monsterDao.searchMonsters("%$query%")

    suspend fun insertMonster(monster: Monster) = monsterDao.insertMonster(monster)
    suspend fun updateMonster(monster: Monster) = monsterDao.updateMonster(monster)
    suspend fun deleteMonster(monster: Monster) = monsterDao.deleteMonster(monster)
}
