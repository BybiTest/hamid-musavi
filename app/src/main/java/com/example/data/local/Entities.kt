package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_scripts")
data class SavedScriptEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val fullScript: String,
    val dateCreated: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_hooks")
data class FavoriteHookEntity(
    @PrimaryKey val hookId: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "planner_progress")
data class PlannerProgressEntity(
    @PrimaryKey val dayNumber: Int,
    val isCompleted: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "vip_state")
data class VipStateEntity(
    @PrimaryKey val id: Int = 1,
    val isVipActive: Boolean = false,
    val planName: String = "رایگان",
    val expirationDateString: String = "نامحدود",
    val temporaryUnlockedHooks: String = "" // comma-separated hook IDs unlocked by rewarded ads
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val isDarkMode: Boolean = true,
    val fontScale: Float = 1.0f, // 0.85f, 1.0f, 1.15f, 1.3f
    val isRealAdsEnabled: Boolean = true,
    val customApiKey: String = ""
)

