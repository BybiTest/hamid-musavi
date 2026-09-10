package com.example.data.repository

import com.example.data.local.AppSettingsEntity
import com.example.data.local.FavoriteHookEntity
import com.example.data.local.PlannerProgressEntity
import com.example.data.local.SavedScriptEntity
import com.example.data.local.VipStateEntity
import com.example.data.local.PreloadedContent
import com.example.data.local.ReelsDao
import com.example.data.model.ContentDayPlan
import com.example.data.model.EngagementResult
import com.example.data.model.HookItem
import com.example.data.model.ScriptTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReelsRepository(private val reelsDao: ReelsDao) {

    val allSavedScripts: Flow<List<SavedScriptEntity>> = reelsDao.getAllSavedScripts()

    val favoriteHookIds: Flow<List<String>> = reelsDao.getAllFavoriteHookIds()

    val vipState: Flow<VipStateEntity> = reelsDao.getVipState().map { it ?: VipStateEntity() }

    val appSettings: Flow<AppSettingsEntity> = reelsDao.getAppSettings().map { it ?: AppSettingsEntity() }

    val plannerProgress: Flow<Map<Int, Boolean>> = reelsDao.getAllPlannerProgress().map { list ->
        list.associate { it.dayNumber to it.isCompleted }
    }

    suspend fun updateSettings(settings: AppSettingsEntity) {
        reelsDao.saveAppSettings(settings)
    }

    fun getAllHooks(): List<HookItem> = PreloadedContent.hooks

    fun getAllTemplates(): List<ScriptTemplate> = PreloadedContent.templates

    fun getAllPlannerDays(): List<ContentDayPlan> = PreloadedContent.plannerDays

    fun getAllCoverTemplates(): List<com.example.data.model.CoverTemplate> = PreloadedContent.coverTemplates

    suspend fun toggleFavorite(hookId: String, currentFavorites: List<String>) {
        if (currentFavorites.contains(hookId)) {
            reelsDao.removeFavoriteHook(hookId)
        } else {
            reelsDao.addFavoriteHook(FavoriteHookEntity(hookId = hookId))
        }
    }

    suspend fun saveScript(title: String, fullScript: String) {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd - HH:mm", Locale.getDefault())
        val dateString = dateFormat.format(Date())
        reelsDao.insertScript(
            SavedScriptEntity(
                title = title.ifBlank { "سناریوی ریلز جدید" },
                fullScript = fullScript,
                dateCreated = dateString
            )
        )
    }

    suspend fun deleteSavedScript(id: Int) {
        reelsDao.deleteScriptById(id)
    }

    suspend fun togglePlannerDay(dayNumber: Int, currentState: Boolean) {
        reelsDao.setPlannerProgress(
            PlannerProgressEntity(
                dayNumber = dayNumber,
                isCompleted = !currentState
            )
        )
    }

    suspend fun activateVip(planName: String, durationDays: Int) {
        val expiryDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(
            Date(System.currentTimeMillis() + durationDays * 24L * 3600 * 1000)
        )
        reelsDao.saveVipState(
            VipStateEntity(
                id = 1,
                isVipActive = true,
                planName = planName,
                expirationDateString = expiryDate
            )
        )
    }

    suspend fun unlockHookWithRewardedAd(hookId: String, currentVipState: VipStateEntity) {
        val existing = currentVipState.temporaryUnlockedHooks
            .split(",")
            .filter { it.isNotBlank() }
            .toMutableSet()
        existing.add(hookId)
        reelsDao.saveVipState(
            currentVipState.copy(
                temporaryUnlockedHooks = existing.joinToString(",")
            )
        )
    }

    fun calculateEngagement(
        followers: Long,
        likes: Long,
        comments: Long,
        saves: Long,
        shares: Long
    ): EngagementResult {
        if (followers <= 0) {
            return EngagementResult(
                engagementRate = 0f,
                status = "داده‌ها نامعتبر",
                statusColor = 0xFF9E9E9E,
                viralPotentialScore = 0,
                algorithmDiagnosis = "لطفاً تعداد فالوورهای پیج را به درستی وارد کنید.",
                actionTips = listOf("حداقل یک فالوور برای محاسبه نیاز است.")
            )
        }

        // Total interactions weighted for Reels: Likes(1) + Comments(2.5) + Shares(4) + Saves(5)
        val weightedInteractions = (likes * 1.0) + (comments * 2.5) + (shares * 4.0) + (saves * 5.0)
        val basicRate = ((likes + comments + saves + shares).toFloat() / followers.toFloat()) * 100f
        val formattedRate = Math.round(basicRate * 100f) / 100f

        val (status, color, viralScore, diagnosis, tips) = when {
            formattedRate >= 6.0f -> Tuple5(
                "فوق‌العاده وایرال (عالی)",
                0xFF00E676,
                95,
                "پیج شما تعامل فوق‌العاده بالایی دارد! الگوریتم اینستاگرام ریلزهای شما را در صف اول اکسپلور قرار می‌دهد.",
                listOf(
                    "همین سبک قلاب‌های ۳ ثانیه‌ای را ادامه دهید.",
                    "در استوری‌ها روی کال تو اکشن‌های دایرکت تمرکز کنید.",
                    "هفته‌ای ۳ الی ۴ ریلز با ریتم تند منتشر کنید."
                )
            )
            formattedRate in 3.0f..5.99f -> Tuple5(
                "خوب و استاندارد",
                0xFFFFD600,
                75,
                "نرخ تعامل پیج شما در سطح سلامت است، اما برای انفجار وایرال باید روی سیو و شیر (اشتراک‌گذاری) تمرکز بیشتری کنید.",
                listOf(
                    "در انتهای ریلز از ترفند «کلمه کلیدی بفرست تا لینک بدم» استفاده کنید تا کامنت‌ها ۳ برابر شود.",
                    "از قلاب‌های شوکه‌کننده و رازهای پنهان در ثانیه اول بهره ببرید.",
                    "متن‌های آموزشی داخل ویدیو را غنی‌تر کنید تا سیو پست افزایش یابد."
                )
            )
            else -> Tuple5(
                "نیاز به بهبود فوری",
                0xFFFF3D00,
                45,
                "نرخ تعامل شما پایین‌تر از میانگین است. احتمالاً مخاطبان ویدیو را رد می‌کنند (Skip Rate بالا) یا انگیزه کافی برای تعامل ندارند.",
                listOf(
                    "مقدمه‌چینی و سلام و احوال‌پرسی اول ویدیو را کاملاً حذف کنید.",
                    "از قلاب‌های معکوس و متناقض دسته «جنجالی» در برنامه استفاده کنید.",
                    "سایز فونت زیرنویس را بزرگ‌تر و رنگ متضاد زرد یا سفید قرار دهید."
                )
            )
        }

        return EngagementResult(
            engagementRate = formattedRate,
            status = status,
            statusColor = color,
            viralPotentialScore = viralScore,
            algorithmDiagnosis = diagnosis,
            actionTips = tips
        )
    }

    private data class Tuple5(
        val status: String,
        val color: Long,
        val score: Int,
        val diagnosis: String,
        val tips: List<String>
    )
}
