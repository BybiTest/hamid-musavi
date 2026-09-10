package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.AppSettingsEntity
import com.example.data.local.SavedScriptEntity
import com.example.data.model.AppTab
import com.example.data.model.ContentDayPlan
import com.example.data.model.CoverCategory
import com.example.data.model.CoverTemplate
import com.example.data.model.EngagementResult
import com.example.data.model.HookCategory
import com.example.data.model.HookItem
import com.example.data.model.ScriptTemplate
import com.example.data.model.ThumbnailTemplate
import com.example.data.model.VipPlan
import com.example.data.model.ViralHook
import com.example.data.repository.ReelsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class VipUiState(
    val isVipActive: Boolean = false,
    val unlockedHookIds: Set<Int> = emptySet(),
    val favoriteHookIds: Set<Int> = emptySet(),
    val completedPlannerDays: Set<Int> = emptySet(),
    val planName: String = "رایگان",
    val expirationDateString: String = "نامحدود"
)

data class ScriptDraftState(
    val title: String = "",
    val hookText: String = "",
    val bodyText: String = "",
    val ctaText: String = "",
    val notes: String = "",
    val editingScriptId: Int? = null
)

data class EngagementCalculatorState(
    val followers: String = "15000",
    val likes: String = "1200",
    val comments: String = "140",
    val shares: String = "85",
    val saves: String = "320",
    val calculatedRate: Double = 0.0,
    val engagementGrade: String = "خوب",
    val recommendation: String = ""
)

data class ThumbnailStudioUiState(
    val currentRatio: String = "9:16",
    val selectedTemplateId: String = "tech_hacks",
    val primaryTitle: String = "ترفند مخفی ریلز!",
    val subtitle: String = "چطور در ۳ روز به اکسپلور برسی؟",
    val badgeText: String = "شوکه‌کننده 🔥",
    val showBadge: Boolean = true,
    val badgePosition: String = "TOP_RIGHT",
    val selectedGradientIndex: Int = 0,
    val isAiGeneratingTitle: Boolean = false
)

data class CoverStudioUiState(
    val selectedTemplateId: String = "",
    val selectedCategory: CoverCategory = CoverCategory.ALL,
    val headlineText: String = "ترفند مخفی ریلز!",
    val subHeadlineText: String = "توضیحات تکمیلی جذاب",
    val badgeText: String = "جدید",
    val isYoutubeShortsBadge: Boolean = false,
    val customImageUri: String? = null,
    val selectedAccentColor: Long = 0xFFFF6B35,
    val selectedTextColor: Long = 0xFFFFFFFF,
    val isAiGeneratingTitle: Boolean = false
)

data class AiAssistantUiState(
    val activeSubTab: Int = 0,
    val chatInput: String = "",
    val chatMessages: List<ChatMessage> = emptyList(),
    val isChatTyping: Boolean = false,
    val errorMessage: String? = null,
    val selectedMode: String = "HOOK_GENERATOR",
    val topicInput: String = "",
    val targetAudience: String = "",
    val tone: String = "هیجانی و شوکه‌کننده",
    val isGenerating: Boolean = false,
    val aiResult: String = ""
)

data class ChatMessage(
    val id: Int = (0..999999).random(),
    val text: String,
    val isUser: Boolean
)

class ReelsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReelsRepository
    private val db = AppDatabase.getInstance(application)

    init {
        repository = ReelsRepository(db.reelsDao())
    }

    private val _currentTab = MutableStateFlow(AppTab.HOOKS)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    val appSettings: StateFlow<AppSettingsEntity> = db.reelsDao().getSettings().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AppSettingsEntity()
    )

    private val _vipState = MutableStateFlow(VipUiState())
    val vipState: StateFlow<VipUiState> = _vipState.asStateFlow()

    val savedScripts: StateFlow<List<SavedScriptEntity>> = db.reelsDao().getAllScripts().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val plannerProgress: StateFlow<Map<Int, Boolean>> = vipState.map { state ->
        (1..30).associateWith { it in state.completedPlannerDays }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), (1..30).associateWith { false })

    private val _selectedCategory = MutableStateFlow<HookCategory?>(null)
    val selectedCategory: StateFlow<HookCategory?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showOnlyFavorites = MutableStateFlow(false)
    val showOnlyFavorites: StateFlow<Boolean> = _showOnlyFavorites.asStateFlow()

    private val _thumbnailStudioState = MutableStateFlow(ThumbnailStudioUiState())
    val thumbnailStudioState: StateFlow<ThumbnailStudioUiState> = _thumbnailStudioState.asStateFlow()

    private val _coverStudioState = MutableStateFlow(CoverStudioUiState())
    val coverStudioState: StateFlow<CoverStudioUiState> = _coverStudioState.asStateFlow()

    private val _aiState = MutableStateFlow(AiAssistantUiState())
    val aiState: StateFlow<AiAssistantUiState> = _aiState.asStateFlow()

    private val _scriptDraft = MutableStateFlow(ScriptDraftState())
    val scriptDraft: StateFlow<ScriptDraftState> = _scriptDraft.asStateFlow()

    private val _calculatorState = MutableStateFlow(EngagementCalculatorState())
    val calculatorState: StateFlow<EngagementCalculatorState> = _calculatorState.asStateFlow()

    private val _rewardedAdForHook = MutableStateFlow<ViralHook?>(null)
    val rewardedAdForHook: StateFlow<ViralHook?> = _rewardedAdForHook.asStateFlow()

    private val _isAdWatching = MutableStateFlow(false)
    val isAdWatching: StateFlow<Boolean> = _isAdWatching.asStateFlow()

    private val _adCountdown = MutableStateFlow(5)
    val adCountdown: StateFlow<Int> = _adCountdown.asStateFlow()

    private val _selectedCheckoutPlan = MutableStateFlow<VipPlan?>(null)
    val selectedCheckoutPlan: StateFlow<VipPlan?> = _selectedCheckoutPlan.asStateFlow()

    init {
        calculateEngagementRate()
        viewModelScope.launch {
            val settings = db.reelsDao().getSettingsDirect()
            if (settings != null) {
                _vipState.value = _vipState.value.copy(
                    isVipActive = settings.isVipUser,
                    unlockedHookIds = settings.unlockedHookIds.split(",").filter { it.isNotBlank() }.mapNotNull { it.toIntOrNull() }.toSet(),
                    completedPlannerDays = settings.completedPlannerDays.split(",").filter { it.isNotBlank() }.mapNotNull { it.toIntOrNull() }.toSet()
                )
            }
        }
    }

    fun selectTab(tab: AppTab) { _currentTab.value = tab }
    fun setCategory(category: HookCategory?) { _selectedCategory.value = category }
    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun toggleFavoritesFilter() { _showOnlyFavorites.value = !_showOnlyFavorites.value }

    fun toggleFavoriteHook(hookId: Int) {
        val current = _vipState.value.favoriteHookIds.toMutableSet()
        if (current.contains(hookId)) current.remove(hookId) else current.add(hookId)
        _vipState.value = _vipState.value.copy(favoriteHookIds = current)
    }

    fun isHookUnlocked(hookId: Int): Boolean {
        return _vipState.value.isVipActive || _vipState.value.unlockedHookIds.contains(hookId)
    }

    fun requestUnlockHook(hook: ViralHook) { _rewardedAdForHook.value = hook }
    fun dismissRewardedAdPrompt() { _rewardedAdForHook.value = null; _isAdWatching.value = false }

    fun startWatchingRewardedAd() {
        viewModelScope.launch {
            _isAdWatching.value = true
            _adCountdown.value = 5
            for (i in 5 downTo 1) { _adCountdown.value = i; delay(1000) }
            
            val hook = _rewardedAdForHook.value
            if (hook != null) {
                val newUnlocked = _vipState.value.unlockedHookIds + hook.id
                _vipState.value = _vipState.value.copy(unlockedHookIds = newUnlocked)
                persistUnlockedHook(hook.id)
                _toastMessage.emit("قفل «${hook.title}» با موفقیت باز شد! 🎉")
            }
            dismissRewardedAdPrompt()
        }
    }

    private fun persistUnlockedHook(hookId: Int) {
        viewModelScope.launch {
            val current = db.reelsDao().getSettingsDirect() ?: AppSettingsEntity()
            val ids = current.unlockedHookIds.split(",").filter { it.isNotBlank() }.toMutableSet()
            ids.add(hookId.toString())
            db.reelsDao().saveSettings(current.copy(unlockedHookIds = ids.joinToString(",")))
        }
    }

    fun updateScriptDraft(title: String? = null, hookText: String? = null, bodyText: String? = null, ctaText: String? = null, notes: String? = null) {
        val old = _scriptDraft.value
        _scriptDraft.value = old.copy(title = title ?: old.title, hookText = hookText ?: old.hookText, bodyText = bodyText ?: old.bodyText, ctaText = ctaText ?: old.ctaText, notes = notes ?: old.notes)
    }

    fun saveCurrentScript() {
        viewModelScope.launch {
            val draft = _scriptDraft.value
            if (draft.title.isBlank() && draft.hookText.isBlank()) {
                _toastMessage.emit("لطفاً حداقل عنوان یا قلاب سناریو را وارد کنید")
                return@launch
            }
            db.reelsDao().insertScript(SavedScriptEntity(id = draft.editingScriptId ?: 0, title = draft.title.ifBlank { "سناریوی بدون عنوان" }, hookText = draft.hookText, bodyText = draft.bodyText, ctaText = draft.ctaText, notes = draft.notes, durationSeconds = 30))
            _toastMessage.emit("سناریو با موفقیت ذخیره شد ✅")
            _scriptDraft.value = ScriptDraftState()
        }
    }

    fun deleteSavedScript(script: SavedScriptEntity) {
        viewModelScope.launch {
            db.reelsDao().deleteScript(script)
            _toastMessage.emit("سناریو حذف شد 🗑️")
        }
    }

    fun togglePlannerDay(dayNumber: Int) {
        val days = _vipState.value.completedPlannerDays.toMutableSet()
        if (days.contains(dayNumber)) days.remove(dayNumber) else days.add(dayNumber)
        _vipState.value = _vipState.value.copy(completedPlannerDays = days)
        viewModelScope.launch {
            val current = db.reelsDao().getSettingsDirect() ?: AppSettingsEntity()
            db.reelsDao().saveSettings(current.copy(completedPlannerDays = days.joinToString(",")))
        }
    }

    fun updateCalculatorFields(followers: String? = null, likes: String? = null, comments: String? = null, shares: String? = null, saves: String? = null) {
        val old = _calculatorState.value
        _calculatorState.value = old.copy(followers = followers ?: old.followers, likes = likes ?: old.likes, comments = comments ?: old.comments, shares = shares ?: old.shares, saves = saves ?: old.saves)
        calculateEngagementRate()
    }

    private fun calculateEngagementRate() {
        val state = _calculatorState.value
        val followers = state.followers.toDoubleOrNull() ?: 1.0
        if (followers <= 0) return
        val interactions = (state.likes.toDoubleOrNull() ?: 0.0) + (state.comments.toDoubleOrNull() ?: 0.0) * 2.0 + (state.shares.toDoubleOrNull() ?: 0.0) * 3.5 + (state.saves.toDoubleOrNull() ?: 0.0) * 3.0
        val rate = kotlin.math.round(((interactions / followers) * 100.0) * 100) / 100.0
        val result = when {
            rate >= 8.0 -> "فوق‌العاده وایرال 🔥" to "تعامل پیج شما در سطح بسیار بالایی قرار دارد."
            rate >= 4.5 -> "بسیار عالی 🚀" to "نرخ تعامل شما بسیار خوب است."
            rate >= 2.5 -> "متوسط رو به رشد 📈" to "وضعیت خوب است اما جای بهبود وجود دارد."
            else -> "نیازمند بهینه‌سازی ⚠️" to "روی قلاب، ارزش محتوا و تعامل بیشتر کار کنید."
        }
        _calculatorState.value = state.copy(calculatedRate = rate, engagementGrade = result.first, recommendation = result.second)
    }

    fun setThumbnailRatio(ratio: String) { _thumbnailStudioState.value = _thumbnailStudioState.value.copy(currentRatio = ratio) }
    fun selectThumbnailTemplate(template: ThumbnailTemplate) { _thumbnailStudioState.value = _thumbnailStudioState.value.copy(selectedTemplateId = template.id, primaryTitle = template.defaultTitle, subtitle = template.defaultSubtitle, badgeText = template.defaultBadge) }
    fun updateThumbnailText(primaryTitle: String? = null, subtitle: String? = null, badgeText: String? = null) {
        val old = _thumbnailStudioState.value
        _thumbnailStudioState.value = old.copy(primaryTitle = primaryTitle ?: old.primaryTitle, subtitle = subtitle ?: old.subtitle, badgeText = badgeText ?: old.badgeText)
    }
    fun toggleThumbnailBadge(show: Boolean) { _thumbnailStudioState.value = _thumbnailStudioState.value.copy(showBadge = show) }
    fun setThumbnailBadgePosition(position: String) { _thumbnailStudioState.value = _thumbnailStudioState.value.copy(badgePosition = position) }
    fun setThumbnailGradient(index: Int) { _thumbnailStudioState.value = _thumbnailStudioState.value.copy(selectedGradientIndex = index) }

    fun selectCoverCategory(category: CoverCategory) { _coverStudioState.value = _coverStudioState.value.copy(selectedCategory = category) }
    fun selectCoverTemplate(template: CoverTemplate) { _coverStudioState.value = _coverStudioState.value.copy(selectedTemplateId = template.id, headlineText = template.defaultMainHeadline, subHeadlineText = template.defaultSubHeadline, badgeText = template.badgeText, selectedAccentColor = template.accentColorHex, selectedTextColor = template.textColorHex) }
    fun updateCoverTexts(headline: String, subHeadline: String, badge: String) { _coverStudioState.value = _coverStudioState.value.copy(headlineText = headline, subHeadlineText = subHeadline, badgeText = badge) }
    fun toggleShortsBadge(enabled: Boolean) { _coverStudioState.value = _coverStudioState.value.copy(isYoutubeShortsBadge = enabled) }
    fun setCoverCustomImage(uri: String?) { _coverStudioState.value = _coverStudioState.value.copy(customImageUri = uri) }
    fun setCoverColors(accentColor: Long, textColor: Long) { _coverStudioState.value = _coverStudioState.value.copy(selectedAccentColor = accentColor, selectedTextColor = textColor) }

    fun setAiSubTab(tab: Int) { _aiState.value = _aiState.value.copy(activeSubTab = tab) }
    fun updateChatInput(text: String) { _aiState.value = _aiState.value.copy(chatInput = text) }
    fun sendChatMessage(promptText: String = "") {
        val message = promptText.ifBlank { _aiState.value.chatInput }.trim()
        if (message.isBlank() || _aiState.value.isChatTyping) return
        _aiState.value = _aiState.value.copy(chatMessages = _aiState.value.chatMessages + ChatMessage(text = message, isUser = true), chatInput = "", isChatTyping = true, errorMessage = null)
        viewModelScope.launch {
            delay(500)
            _aiState.value = _aiState.value.copy(
                chatMessages = _aiState.value.chatMessages + ChatMessage(text = "پاسخ هوشمند به: $message (این یک پاسخ آزمایشی است)", isUser = false),
                isChatTyping = false
            )
        }
    }
    fun clearChatHistory() { _aiState.value = _aiState.value.copy(chatMessages = emptyList()) }

    fun setAiMode(mode: String) { _aiState.value = _aiState.value.copy(selectedMode = mode) }
    fun updateAiInputs(topic: String, audience: String, tone: String) { _aiState.value = _aiState.value.copy(topicInput = topic, targetAudience = audience, tone = tone) }
    fun generateAiContent() {
        val state = _aiState.value
        if (state.topicInput.isBlank()) { viewModelScope.launch { _toastMessage.emit("لطفاً ابتدا موضوع ریلز را وارد کنید") }; return }
        _aiState.value = state.copy(isGenerating = true, errorMessage = null)
        viewModelScope.launch {
            delay(1000)
            _aiState.value = _aiState.value.copy(
                isGenerating = false,
                aiResult = "محتوای تولید شده برای: ${state.topicInput}\nلحن: ${state.tone}\n(این یک پاسخ آزمایشی است)"
            )
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            val current = db.reelsDao().getSettingsDirect() ?: AppSettingsEntity()
            db.reelsDao().saveSettings(current.copy(isDarkMode = enabled))
        }
    }

    fun setFontScale(scale: Float) {
        viewModelScope.launch {
            val current = db.reelsDao().getSettingsDirect() ?: AppSettingsEntity()
            db.reelsDao().saveSettings(current.copy(fontScale = scale))
        }
    }

    fun openCheckout(title: String, price: String, days: Int) {
        _selectedCheckoutPlan.value = VipPlan(title = title, price = price, durationDays = days)
    }

    fun dismissCheckout() { _selectedCheckoutPlan.value = null }

    fun confirmPurchase() {
        val plan = _selectedCheckoutPlan.value ?: return
        viewModelScope.launch {
            _vipState.value = _vipState.value.copy(isVipActive = true, planName = plan.title, expirationDateString = "${plan.durationDays} روز")
            val current = db.reelsDao().getSettingsDirect() ?: AppSettingsEntity()
            db.reelsDao().saveSettings(current.copy(isVipUser = true))
            _toastMessage.emit("اشتراک ${plan.title} فعال شد! به خانواده VIP خوش آمدید 👑")
            dismissCheckout()
        }
    }
}
