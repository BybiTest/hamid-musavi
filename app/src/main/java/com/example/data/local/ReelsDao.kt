package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReelsDao {
    // Saved Scripts
    @Query("SELECT * FROM saved_scripts ORDER BY timestamp DESC")
    fun getAllSavedScripts(): Flow<List<SavedScriptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScript(script: SavedScriptEntity)

    @Query("DELETE FROM saved_scripts WHERE id = :id")
    suspend fun deleteScriptById(id: Int)

    // Favorite Hooks
    @Query("SELECT hookId FROM favorite_hooks")
    fun getAllFavoriteHookIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteHook(fav: FavoriteHookEntity)

    @Query("DELETE FROM favorite_hooks WHERE hookId = :hookId")
    suspend fun removeFavoriteHook(hookId: String)

    // Planner progress
    @Query("SELECT * FROM planner_progress")
    fun getAllPlannerProgress(): Flow<List<PlannerProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setPlannerProgress(progress: PlannerProgressEntity)

    // VIP State
    @Query("SELECT * FROM vip_state WHERE id = 1")
    fun getVipState(): Flow<VipStateEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveVipState(vipState: VipStateEntity)
}
