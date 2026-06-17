package com.example.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "saddlebag_items")
data class SaddlebagItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // "QUEST_ITEM", "ALCHEMY_INGREDIENT"
    val quantity: Int,
    val description: String,
    val rarity: String, // "COMMON", "RARE", "MAGIC", "RELIC"
    val iconLabel: String // Graphic emoticon representer (e.g. "🧪", "📜", "💀")
)

@Dao
interface SaddlebagItemDao {
    @Query("SELECT * FROM saddlebag_items ORDER BY category ASC, id DESC")
    fun getAllSaddlebagItems(): Flow<List<SaddlebagItem>>

    @Query("SELECT COUNT(*) FROM saddlebag_items")
    suspend fun getItemsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: SaddlebagItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<SaddlebagItem>)

    @Query("DELETE FROM saddlebag_items WHERE id = :id")
    suspend fun deleteItemById(id: Int)

    @Query("UPDATE saddlebag_items SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: Int, quantity: Int)
}
