package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReelsDao {

    // =========================================================
    // Saved Scripts
    // =========================================================

    @Query("SELECT * FROM saved_scripts ORDER BY timestamp DESC")
    fun getAllSavedScripts(): Flow<List<SavedScriptEntity>>

    @Query("SELECT * FROM saved_scripts ORDER BY timestamp DESC")
    fun getAllScripts(): Flow<List<SavedScriptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScript(script: SavedScriptEntity)

    @Update
    suspend fun updateScript(script: SavedScriptEntity)

    @Query("DELETE FROM saved_scripts WHERE id = :id")
    suspend fun deleteScriptById(id: Int)

    suspend fun deleteScript(script: SavedScriptEntity) {
        deleteScriptById(script.id)
    }

    // =========================================================
    // Favorite Hooks
    // =========================================================

    @Query("SELECT hookId FROM favorite_hooks")
    fun getAllFavoriteHookIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteHook(fav: FavoriteHookEntity)

    @Query("DELETE FROM favorite_hooks WHERE hookId = :hookId")
    suspend fun removeFavoriteHook(hookId: String)

    // =========================================================
    // Planner Progress
    // =========================================================

    @Query("SELECT * FROM planner_progress")
    fun getAllPlannerProgress(): Flow<List<PlannerProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setPlannerProgress(
        progress: PlannerProgressEntity
    )

    // =========================================================
    // VIP State
    // =========================================================

    @Query("SELECT * FROM vip_state WHERE id = 1")
    fun getVipState(): Flow<VipStateEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveVipState(
        vipState: VipStateEntity
    )

    // =========================================================
    // App Settings
    // =========================================================

    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getAppSettings(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAppSettings(
        settings: AppSettingsEntity
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(
        settings: AppSettingsEntity
    )

    // =========================================================
    // Compatibility aliases
    // =========================================================

    /*
     * ReelsViewModel currently expects:
     *
     * db.viralHookDao()
     * db.scriptDao()
     * db.settingsDao()
     *
     * We will resolve those at AppDatabase level.
     *
     * This DAO intentionally remains the single Room DAO
     * so we don't duplicate database logic.
     */
}
