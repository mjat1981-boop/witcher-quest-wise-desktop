package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quests")
data class Quest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val type: String, // "MAIN", "SIDE", "CONTRACT", "TREASURE"
    val region: String, // "WHITE_ORCHARD", "VELEN", "NOVIGRAD", "SKELLIGE", "KAER_MORHEN", "TOUSSAINT"
    val recommendedLevel: Int,
    val description: String,
    val questgiver: String,
    val rewards: String,
    val status: String, // "NOT_STARTED", "IN_PROGRESS", "COMPLETED", "FAILED"
    val priority: String = "MEDIUM", // "LOW", "MEDIUM", "HIGH"
    val monsterWeaknesses: String? = null,
    val notes: String = "",
    val isCustom: Boolean = false,
    val geraltAdvice: String? = null,
    val advisorAdvice: String? = null,
    val narrativeChoices: String = "",
    val tracked: Boolean = false
)
