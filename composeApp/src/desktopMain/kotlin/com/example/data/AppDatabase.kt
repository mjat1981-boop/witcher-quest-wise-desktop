package com.example.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.data.utils.Converters
import kotlinx.coroutines.Dispatchers
import java.io.File

@Database(entities = [Quest::class, SaddlebagItem::class, Monster::class], version = 7, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questDao(): QuestDao
    abstract fun saddlebagItemDao(): SaddlebagItemDao
    abstract fun monsterDao(): MonsterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val dbFile = File(System.getProperty("user.home"), ".witcher-quest-wise/witcher_quest_wise_db")
                dbFile.parentFile?.mkdirs()
                val instance = Room.databaseBuilder<AppDatabase>(dbFile.absolutePath)
                    .setDriver(BundledSQLiteDriver())
                    .setQueryCoroutineContext(Dispatchers.IO)
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getPreloadedQuests(): List<Quest> =
            com.example.data.quests.QuestCatalog.all()
    }
}
