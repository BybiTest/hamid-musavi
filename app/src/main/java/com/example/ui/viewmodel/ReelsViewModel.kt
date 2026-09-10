package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.ReelsAiExpert
import com.example.data.local.AppDatabase
import com.example.data.local.AppSettingsEntity
import com.example.data.local.SavedScriptEntity
import com.example.data.model.AppTab
import com.example.data.model.ContentPlanDay
import com.example.data.model.CoverCategory
import com.example.data.model.CoverTemplate
import com.example.data.model.HookCategory
import com.example.data.model.ThumbnailTemplate
import com.example.data.model.VipPlan
import com.example.data.model.ViralHook
import com.example.data.remote.GeminiApiService
import com.example.data.local.PreloadedContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

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
    val subHeadlineText: String = "چطور در ۳ روز به اکسپلور برسی؟",
    val badgeText: String = "شوکه‌کننده 🔥",
    val isYoutubeShortsBadge: Boolean = false,
    val customImageUri: String? = null,
    val selectedAccentColor: Long = 0xFFFF3D00,
    val selectedTextColor: Long = 0xFFFFFFFF,
    val isAiGeneratingTitle: Boolean = false
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class AiAssistantUiState(
    val activeSubTab: Int = 0,
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            text = "سلام دوست من! 👋\n\nمن دستیار هوشمند Reels Studio هستم. هر سؤالی داری بپرس؛ چه سؤال عمومی باشه و چه درباره ریلز، تولید محتوا، ایده، سناریو یا رشد پیج.",
            isUser = false
        )
    ),
    val chatInput: String = "",
    val isChatTyping: Boolean = false,
    val userPrompt: String = "",
    val selectedMode: String = "HOOK_GENERATOR",
    val topicInput: String = "",
    val targetAudience: String = "",
    val tone: String = "هیجانی و شوکه‌کننده",
    val isGenerating: Boolean = false,
    val aiResult: String = "",
    val errorMessage: String? = null
)

class ReelsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val dao = db.reelsDao()

    // ---------------------------------------------------------
    // Navigation
    // ---------------------------------------------------------

    private val _currentTab = MutableStateFlow(AppTab.HOOKS)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    // ---------------------------------------------------------
    // Settings
    // ---------------------------------------------------------

    val appSettings: StateFlow<AppSettingsEntity> =
        dao.getAppSettings()
            .map { it ?: AppSettingsEntity() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AppSettingsEntity()
            )

    // ---------------------------------------------------------
    // VIP
    // ---------------------------------------------------------

    private val _vipState = MutableStateFlow(VipUiState())
    val vipState: StateFlow<VipUiState> = _vipState.asStateFlow()

    // ---------------------------------------------------------
    // Planner compatibility
    // ---------------------------------------------------------

    val plannerProgress: StateFlow<Map<Int, Boolean>> =
        vipState
            .map { state ->
                (1..30).associateWith {
                    it in state.completedPlannerDays
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = (1..30).associateWith { false }
            )

    // ---------------------------------------------------------
    // Saved Scripts
    // ---------------------------------------------------------

    val savedScripts: StateFlow<List<SavedScriptEntity>> =
        dao.getAllScripts()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // ---------------------------------------------------------
    // Script Draft
    // ---------------------------------------------------------

    private val _scriptDraft = MutableStateFlow(ScriptDraftState())
    val scriptDraft: StateFlow<ScriptDraftState> =
        _scriptDraft.asStateFlow()

    // ---------------------------------------------------------
    // Calculator
    // ---------------------------------------------------------

    private val _calculatorState =
        MutableStateFlow(EngagementCalculatorState())

    val calculatorState: StateFlow<EngagementCalculatorState> =
        _calculatorState.asStateFlow()

    // ---------------------------------------------------------
    // Thumbnail Studio - old API
    // ---------------------------------------------------------

    private val _thumbnailStudioState =
        MutableStateFlow(ThumbnailStudioUiState())

    val thumbnailStudioState: StateFlow<ThumbnailStudioUiState> =
        _thumbnailStudioState.asStateFlow()

    // ---------------------------------------------------------
    // Cover Studio - current API
    // ---------------------------------------------------------

    private val defaultCoverTemplates: List<CoverTemplate> =
        PreloadedContent.coverTemplates

    private val _coverStudioState =
        MutableStateFlow(
            CoverStudioUiState(
                selectedTemplateId =
                    defaultCoverTemplates.firstOrNull()?.id ?: ""
            )
        )

    val coverStudioState: StateFlow<CoverStudioUiState> =
        _coverStudioState.asStateFlow()

    fun getAllCoverTemplates(): List<CoverTemplate> {
        return defaultCoverTemplates
    }

    // ---------------------------------------------------------
    // AI Assistant
    // ---------------------------------------------------------

    private val _aiState =
        MutableStateFlow(AiAssistantUiState())

    val aiState: StateFlow<AiAssistantUiState> =
        _aiState.asStateFlow()

    // ---------------------------------------------------------
    // Hooks
    // ---------------------------------------------------------

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> =
        _searchQuery.asStateFlow()

    private val _selectedCategory =
        MutableStateFlow<HookCategory?>(null)

    val selectedCategory: StateFlow<HookCategory?> =
        _selectedCategory.asStateFlow()

    private val _showOnlyFavorites =
        MutableStateFlow(false)

    val showOnlyFavorites: StateFlow<Boolean> =
        _showOnlyFavorites.asStateFlow()

    val allHooks = PreloadedContent.hooks

    // ---------------------------------------------------------
    // Ads / Rewarded Hooks
    // ---------------------------------------------------------

    private val _rewardedAdForHook =
        MutableStateFlow<ViralHook?>(null)

    val rewardedAdForHook: StateFlow<ViralHook?> =
        _rewardedAdForHook.asStateFlow()

    private val _isAdWatching =
        MutableStateFlow(false)

    val isAdWatching: StateFlow<Boolean> =
        _isAdWatching.asStateFlow()

    private val _adCountdown =
        MutableStateFlow(5)

    val adCountdown: StateFlow<Int> =
        _adCountdown.asStateFlow()

    // ---------------------------------------------------------
    // VIP Checkout
    // ---------------------------------------------------------

    private val _selectedCheckoutPlan =
        MutableStateFlow<VipPlan?>(null)

    val selectedCheckoutPlan: StateFlow<VipPlan?> =
        _selectedCheckoutPlan.asStateFlow()

    // ---------------------------------------------------------
    // Toast
    // ---------------------------------------------------------

    private val _toastMessage =
        MutableSharedFlow<String>()

    val toastMessage: SharedFlow<String> =
        _toastMessage.asSharedFlow()

    // ---------------------------------------------------------
    // Initialization
    // ---------------------------------------------------------

    init {
        calculateEngagementRate()

        viewModelScope.launch {
            val settings =
                dao.getSettingsDirect()

            if (settings != null) {

                val unlockedIds =
                    settings.unlockedHookIds
                        .split(",")
                        .filter { it.isNotBlank() }
                        .mapNotNull { it.toIntOrNull() }
                        .toSet()

                val completedDays =
                    settings.completedPlannerDays
                        .split(",")
                        .filter { it.isNotBlank() }
                        .mapNotNull { it.toIntOrNull() }
                        .toSet()

                _vipState.value =
                    _vipState.value.copy(
                        isVipActive = settings.isVipUser,
                        unlockedHookIds = unlockedIds,
                        completedPlannerDays = completedDays
                    )
            }
        }
    }

    // =========================================================
    // Hooks
    // =========================================================

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: HookCategory?) {
        _selectedCategory.value = category
    }

    fun setCategory(category: HookCategory?) {
        selectCategory(category)
    }

    fun toggleFavoritesFilter() {
        _showOnlyFavorites.value =
            !_showOnlyFavorites.value
    }

    fun toggleFavoriteHook(hookId: Int) {

        val current =
            _vipState.value.favoriteHookIds.toMutableSet()

        if (current.contains(hookId)) {
            current.remove(hookId)
        } else {
            current.add(hookId)
        }

        _vipState.value =
            _vipState.value.copy(
                favoriteHookIds = current
            )
    }

    fun isHookUnlocked(hook: ViralHook): Boolean {
        return _vipState.value.isVipActive ||
                _vipState.value.unlockedHookIds.contains(hook.id)
    }

    fun requestUnlockHook(hook: ViralHook) {

        if (isHookUnlocked(hook)) {
            return
        }

        _rewardedAdForHook.value = hook
    }

    fun promptWatchRewardedAd(hook: ViralHook) {
        requestUnlockHook(hook)
    }

    fun dismissRewardedAdPrompt() {
        _rewardedAdForHook.value = null
        _isAdWatching.value = false
        _adCountdown.value = 5
    }

    fun startWatchingRewardedAd() {

        viewModelScope.launch {

            _isAdWatching.value = true
            _adCountdown.value = 5

            while (_adCountdown.value > 0) {
                delay(1000)
                _adCountdown.value -= 1
            }

            val hook =
                _rewardedAdForHook.value

            if (hook != null) {

                val newUnlocked =
                    _vipState.value.unlockedHookIds + hook.id

                _vipState.value =
                    _vipState.value.copy(
                        unlockedHookIds = newUnlocked
                    )

                persistUnlockedHook(hook.id)

                _toastMessage.emit(
                    "قفل «${hook.title}» با موفقیت باز شد! 🎉"
                )
            }

            dismissRewardedAdPrompt()
        }
    }

    private fun persistUnlockedHook(hookId: Int) {

        viewModelScope.launch {

            val current =
                dao.getSettingsDirect()
                    ?: AppSettingsEntity()

            val ids =
                current.unlockedHookIds
                    .split(",")
                    .filter { it.isNotBlank() }
                    .toMutableSet()

            ids.add(hookId.toString())

            dao.saveSettings(
                current.copy(
                    unlockedHookIds =
                        ids.joinToString(",")
                )
            )
        }
    }

    // =========================================================
    // VIP
    // =========================================================

    fun openVipCheckout(plan: VipPlan) {
        _selectedCheckoutPlan.value = plan
    }

    fun openCheckout(
        planName: String,
        price: String,
        durationDays: Int
    ) {
        openVipCheckout(
            VipPlan(
                title = planName,
                price = price,
                durationDays = durationDays
            )
        )
    }

    fun dismissCheckout() {
        _selectedCheckoutPlan.value = null
    }

    fun confirmPurchase() {

        viewModelScope.launch {

            val plan =
                _selectedCheckoutPlan.value
                    ?: return@launch

            _vipState.value =
                _vipState.value.copy(
                    isVipActive = true,
                    planName = plan.title,
                    expirationDateString =
                        "${plan.durationDays} روز"
                )

            val current =
                dao.getSettingsDirect()
                    ?: AppSettingsEntity()

            dao.saveSettings(
                current.copy(
                    isVipUser = true
                )
            )

            _toastMessage.emit(
                "اشتراک ${plan.title} فعال شد 💎"
            )

            dismissCheckout()
        }
    }

    // =========================================================
    // Planner
    // =========================================================

    fun toggleDayCompleted(dayNumber: Int) {

        val days =
            _vipState.value.completedPlannerDays
                .toMutableSet()

        if (days.contains(dayNumber)) {
            days.remove(dayNumber)
        } else {
            days.add(dayNumber)
        }

        _vipState.value =
            _vipState.value.copy(
                completedPlannerDays = days
            )

        viewModelScope.launch {

            val current =
                dao.getSettingsDirect()
                    ?: AppSettingsEntity()

            dao.saveSettings(
                current.copy(
                    completedPlannerDays =
                        days.joinToString(",")
                )
            )
        }
    }

    // =========================================================
    // Script Maker
    // =========================================================

    fun updateScriptDraft(
        title: String? = null,
        hookText: String? = null,
        bodyText: String? = null,
        ctaText: String? = null,
        notes: String? = null
    ) {

        val old =
            _scriptDraft.value

        _scriptDraft.value =
            old.copy(
                title = title ?: old.title,
                hookText = hookText ?: old.hookText,
                bodyText = bodyText ?: old.bodyText,
                ctaText = ctaText ?: old.ctaText,
                notes = notes ?: old.notes
            )
    }

    fun loadScriptIntoEditor(
        script: SavedScriptEntity
    ) {

        _scriptDraft.value =
            ScriptDraftState(
                title = script.title,
                hookText = script.hookText,
                bodyText = script.bodyText,
                ctaText = script.ctaText,
                notes = script.notes,
                editingScriptId = script.id
            )

        _currentTab.value = AppTab.SCRIPTS
    }

    fun applyHookToScript(
        hook: ViralHook
    ) {

        val old =
            _scriptDraft.value

        _scriptDraft.value =
            old.copy(
                title =
                    if (old.title.isBlank()) {
                        "سناریوی ریلز: ${hook.title}"
                    } else {
                        old.title
                    },
                hookText = hook.template
            )

        _currentTab.value = AppTab.SCRIPTS
    }

    fun applyPlannerDayToScript(
        planDay: ContentPlanDay
    ) {

        _scriptDraft.value =
            _scriptDraft.value.copy(
                title =
                    "چالش روز ${planDay.dayNumber}: ${planDay.title}",
                hookText = planDay.suggestedHook,
                bodyText =
                    "نوع ویدیو: ${planDay.contentType}\n" +
                            "سناریو پیشنهادی: ${planDay.description}",
                ctaText = planDay.callToAction
            )

        _currentTab.value = AppTab.SCRIPTS
    }

    fun resetScriptDraft() {
        _scriptDraft.value = ScriptDraftState()
    }

    fun saveCurrentScript() {

        val draft =
            _scriptDraft.value

        if (
            draft.title.isBlank() &&
            draft.hookText.isBlank()
        ) {
            viewModelScope.launch {
                _toastMessage.emit(
                    "لطفاً حداقل عنوان یا قلاب سناریو را وارد کنید"
                )
            }
            return
        }

        viewModelScope.launch {

            dao.insertScript(
                SavedScriptEntity(
                    id = draft.editingScriptId ?: 0,
                    title =
                        draft.title.ifBlank {
                            "سناریوی بدون عنوان"
                        },
                    hookText = draft.hookText,
                    bodyText = draft.bodyText,
                    ctaText = draft.ctaText,
                    notes = draft.notes,
                    durationSeconds = 30
                )
            )

            _toastMessage.emit(
                "سناریو با موفقیت ذخیره شد ✅"
            )

            resetScriptDraft()
        }
    }

    fun deleteScript(
        script: SavedScriptEntity
    ) {

        viewModelScope.launch {
            dao.deleteScript(script)

            _toastMessage.emit(
                "سناریو حذف شد 🗑️"
            )
        }
    }

    // =========================================================
    // Calculator
    // =========================================================

    fun updateCalculatorField(
        followers: String? = null,
        likes: String? = null,
        comments: String? = null,
        shares: String? = null,
        saves: String? = null
    ) {

        val old =
            _calculatorState.value

        _calculatorState.value =
            old.copy(
                followers =
                    followers ?: old.followers,
                likes =
                    likes ?: old.likes,
                comments =
                    comments ?: old.comments,
                shares =
                    shares ?: old.shares,
                saves =
                    saves ?: old.saves
            )

        calculateEngagementRate()
    }

    private fun calculateEngagementRate() {

        val state =
            _calculatorState.value

        val followers =
            state.followers.toDoubleOrNull()
                ?: 1.0

        if (followers <= 0) {
            return
        }

        val likes =
            state.likes.toDoubleOrNull()
                ?: 0.0

        val comments =
            state.comments.toDoubleOrNull()
                ?: 0.0

        val shares =
            state.shares.toDoubleOrNull()
                ?: 0.0

        val saves =
            state.saves.toDoubleOrNull()
                ?: 0.0

        val interactions =
            likes +
                    comments * 2.0 +
                    shares * 3.5 +
                    saves * 3.0

        val rate =
            kotlin.math.round(
                ((interactions / followers) * 100.0) * 100
            ) / 100.0

        val result =
            when {
                rate >= 8.0 ->
                    "فوق‌العاده وایرال 🔥" to
                            "تعامل پیج شما در سطح بسیار بالایی قرار دارد."

                rate >= 4.5 ->
                    "بسیار عالی 🚀" to
                            "نرخ تعامل شما بسیار خوب است."

                rate >= 2.5 ->
                    "متوسط رو به رشد 📈" to
                            "وضعیت خوب است اما جای بهبود وجود دارد."

                else ->
                    "نیازمند بهینه‌سازی ⚠️" to
                            "روی قلاب، ارزش محتوا و تعامل بیشتر کار کنید."
            }

        _calculatorState.value =
            state.copy(
                calculatedRate = rate,
                engagementGrade = result.first,
                recommendation = result.second
            )
    }

    // =========================================================
    // Old Thumbnail Studio API
    // =========================================================

    fun setThumbnailRatio(
        ratio: String
    ) {
        _thumbnailStudioState.value =
            _thumbnailStudioState.value.copy(
                currentRatio = ratio
            )
    }

    fun selectThumbnailTemplate(
        template: ThumbnailTemplate
    ) {
        _thumbnailStudioState.value =
            _thumbnailStudioState.value.copy(
                selectedTemplateId = template.id,
                primaryTitle = template.defaultTitle,
                subtitle = template.defaultSubtitle,
                badgeText = template.defaultBadge
            )
    }

    fun updateThumbnailText(
        primaryTitle: String? = null,
        subtitle: String? = null,
        badgeText: String? = null
    ) {

        val old =
            _thumbnailStudioState.value

        _thumbnailStudioState.value =
            old.copy(
                primaryTitle =
                    primaryTitle ?: old.primaryTitle,
                subtitle =
                    subtitle ?: old.subtitle,
                badgeText =
                    badgeText ?: old.badgeText
            )
    }

    fun toggleThumbnailBadge(
        show: Boolean
    ) {
        _thumbnailStudioState.value =
            _thumbnailStudioState.value.copy(
                showBadge = show
            )
    }

    fun setThumbnailBadgePosition(
        position: String
    ) {
        _thumbnailStudioState.value =
            _thumbnailStudioState.value.copy(
                badgePosition = position
            )
    }

    fun setThumbnailGradient(
        index: Int
    ) {
        _thumbnailStudioState.value =
            _thumbnailStudioState.value.copy(
                selectedGradientIndex = index
            )
    }

    // =========================================================
    // Cover Studio API
    // =========================================================

    fun selectCoverCategory(
        category: CoverCategory
    ) {
        _coverStudioState.value =
            _coverStudioState.value.copy(
                selectedCategory = category
            )
    }

    fun selectCoverTemplate(
        template: CoverTemplate
    ) {

        _coverStudioState.value =
            _coverStudioState.value.copy(
                selectedTemplateId = template.id,
                headlineText = template.defaultMainHeadline,
                subHeadlineText = template.defaultSubHeadline,
                badgeText = template.badgeText,
                selectedAccentColor = template.accentColorHex,
                selectedTextColor = template.textColorHex
            )
    }

    fun updateCoverTexts(
        headline: String,
        subHeadline: String,
        badge: String
    ) {

        _coverStudioState.value =
            _coverStudioState.value.copy(
                headlineText = headline,
                subHeadlineText = subHeadline,
                badgeText = badge
            )
    }

    fun toggleShortsBadge(
        enabled: Boolean
    ) {
        _coverStudioState.value =
            _coverStudioState.value.copy(
                isYoutubeShortsBadge = enabled
            )
    }

    fun setCoverCustomImage(
        uri: String?
    ) {
        _coverStudioState.value =
            _coverStudioState.value.copy(
                customImageUri = uri
            )
    }

    fun setCoverColors(
        accentColor: Long,
        textColor: Long
    ) {
        _coverStudioState.value =
            _coverStudioState.value.copy(
                selectedAccentColor = accentColor,
                selectedTextColor = textColor
            )
    }

    fun generateAiCoverHeadline(
        topic: String
    ) {

        if (topic.isBlank()) {
            viewModelScope.launch {
                _toastMessage.emit(
                    "ابتدا موضوع ویدیو را وارد کنید."
                )
            }
            return
        }

        _coverStudioState.value =
            _coverStudioState.value.copy(
                isAiGeneratingTitle = true
            )

        viewModelScope.launch {

            val key =
                appSettings.value.customApiKey

            if (key.isNotBlank()) {

                val result =
                    GeminiApiService.generateContent(
                        prompt = """
                            برای موضوع «$topic»
                            سه تیتر کوتاه و بسیار جذاب برای کاور
                            ویدیوی کوتاه پیشنهاد بده.
                            تیترها کلیک‌بیت مثبت، طبیعی و فارسی باشند.
                        """.trimIndent(),
                        customApiKey = key
                    )

                result.onSuccess { text ->

                    val firstLine =
                        text.lines()
                            .firstOrNull {
                                it.isNotBlank()
                            }
                            ?.trim()
                            ?: "این ترفند رو از دست نده!"

                    _coverStudioState.value =
                        _coverStudioState.value.copy(
                            headlineText = firstLine,
                            isAiGeneratingTitle = false
                        )
                }.onFailure {

                    useLocalCoverHeadline(topic)
                }

            } else {
                useLocalCoverHeadline(topic)
            }
        }
    }

    private fun useLocalCoverHeadline(
        topic: String
    ) {

        _coverStudioState.value =
            _coverStudioState.value.copy(
                headlineText =
                    "۳ نکته مهم درباره $topic",
                isAiGeneratingTitle = false
            )
    }

    // =========================================================
    // AI Assistant - Natural Chat
    // =========================================================

    fun setAiSubTab(tab: Int) {
        _aiState.value =
            _aiState.value.copy(
                activeSubTab = tab
            )
    }

    fun updateChatInput(text: String) {
        _aiState.value =
            _aiState.value.copy(
                chatInput = text
            )
    }

    private fun getNaturalLocalReply(
        message: String
    ): String? {

        val text =
            message.trim().lowercase()

        return when {

            text.matches(
                Regex(
                    "^(سلام|سلاممم|درود|های|hello|hi)[!.، ؟? ]*$"
                )
            ) ->
                "سلام دوست من! 👋😊\nخوش اومدی. بگو ببینم چطور می‌تونم کمکت کنم؟"

            text.contains("یه سوال دارم") ||
                    text.contains("یک سوال دارم") ||
                    text.contains("سؤال دارم") ||
                    text.contains("یه سئوال دارم") ->
                "حتماً 😊 بپرس، گوشم با توئه."

            text.matches(
                Regex(
                    "^(خوبی|خوبی؟|چطوری|چطوری؟|چه خبر)[!.، ؟? ]*$"
                )
            ) ->
                "مرسی که پرسیدی 😊 آماده‌ام کمکت کنم. بگو چه کاری داریم؟"

            text.matches(
                Regex(
                    "^(ممنون|مرسی|دمت گرم|خیلی ممنون)[!.، ؟? ]*$"
                )
            ) ->
                "خواهش می‌کنم ❤️ خوشحالم که تونستم کمکت کنم."

            text.matches(
                Regex(
                    "^(خداحافظ|فعلاً|فعلا|بای|bye)[!.، ؟? ]*$"
                )
            ) ->
                "فعلاً دوست من 👋 هر وقت برگشتی من اینجام."

            else -> null
        }
    }

    private fun isReelsRelatedMessage(
        message: String
    ): Boolean {

        val keywords =
            listOf(
                "ریلز",
                "ریل",
                "شورت",
                "short",
                "shorts",
                "reels",
                "اینستاگرام",
                "instagram",
                "اکسپلور",
                "explore",
                "الگوریتم",
                "قلاب",
                "هوک",
                "hook",
                "سناریو",
                "اسکریپت",
                "script",
                "کپشن",
                "caption",
                "هشتگ",
                "hashtag",
                "فالوور",
                "فالوئر",
                "پیج",
                "محتوا",
                "وایرال",
                "viral",
                "بازدید",
                "ویو",
                "فروش",
                "تولید محتوا",
                "کاور",
                "thumbnail",
                "ادز",
                "تبلیغ"
            )

        val text =
            message.lowercase()

        return keywords.any {
            text.contains(it)
        }
    }

    private fun buildChatHistory(
        messages: List<ChatMessage>,
        currentMessage: String
    ): String {

        val previous =
            messages
                .dropLast(1)
                .takeLast(20)

        if (previous.isEmpty()) {
            return "هنوز مکالمه قبلی وجود ندارد."
        }

        return buildString {

            previous.forEach { message ->

                append(
                    if (message.isUser) {
                        "کاربر"
                    } else {
                        "دستیار"
                    }
                )

                append(": ")
                append(message.text)
                append("\n\n")
            }

            append("کاربر: ")
            append(currentMessage)
        }
    }

    fun sendChatMessage(
        promptText: String = ""
    ) {

        val message =
            promptText
                .ifBlank {
                    _aiState.value.chatInput
                }
                .trim()

        if (
            message.isBlank() ||
            _aiState.value.isChatTyping
        ) {
            return
        }

        val userMessage =
            ChatMessage(
                text = message,
                isUser = true
            )

        _aiState.value =
            _aiState.value.copy(
                chatMessages =
                    _aiState.value.chatMessages +
                            userMessage,
                chatInput = "",
                isChatTyping = true,
                errorMessage = null
            )

        viewModelScope.launch {

            try {

                val natural =
                    getNaturalLocalReply(message)

                if (natural != null) {

                    delay(350)

                    _aiState.value =
                        _aiState.value.copy(
                            chatMessages =
                                _aiState.value.chatMessages +
                                        ChatMessage(
                                            text = natural,
                                            isUser = false
                                        ),
                            isChatTyping = false
                        )

                    return@launch
                }

                val history =
                    buildChatHistory(
                        _aiState.value.chatMessages,
                        message
                    )

                val expert =
                    isReelsRelatedMessage(message)

                val prompt =
                    if (expert) {

                        """
                        تو دستیار هوشمند حرفه‌ای Reels Studio هستی.

                        مثل یک دستیار واقعی و طبیعی صحبت کن.

                        اگر سؤال درباره ریلز، اینستاگرام،
                        YouTube Shorts، تولید محتوا، قلاب،
                        هوک، سناریو، کپشن، هشتگ، وایرال،
                        رشد پیج یا فروش است، پاسخ تخصصی بده.

                        اگر سؤال عمومی است، همان سؤال عمومی
                        را پاسخ بده و آن را بی‌دلیل به ریلز
                        مرتبط نکن.

                        از تاریخچه مکالمه برای درک منظور کاربر استفاده کن.

                        پاسخ فارسی، روان، دوستانه و کاربردی باشد.

                        تاریخچه:
                        $history
                        """.trimIndent()

                    } else {

                        """
                        تو یک دستیار مکالمه‌ای طبیعی و دوستانه هستی.

                        سؤال کاربر را همان‌طور که پرسیده پاسخ بده.
                        سؤال عمومی را به ریلز یا اینستاگرام ربط نده.
                        اگر ادامه مکالمه قبلی است، از تاریخچه استفاده کن.
                        پاسخ فارسی و طبیعی باشد.

                        تاریخچه:
                        $history
                        """.trimIndent()
                    }

                val key =
                    appSettings.value.customApiKey

                if (key.isNotBlank()) {

                    val result =
                        GeminiApiService.generateContent(
                            prompt = prompt,
                            customApiKey = key
                        )

                    result.fold(
                        onSuccess = { reply ->

                            _aiState.value =
                                _aiState.value.copy(
                                    chatMessages =
                                        _aiState.value.chatMessages +
                                                ChatMessage(
                                                    text =
                                                        reply.trim().ifBlank {
                                                            "متأسفم، این بار نتونستم پاسخ مناسبی تولید کنم."
                                                        },
                                                    isUser = false
                                                ),
                                    isChatTyping = false
                                )
                        },
                        onFailure = {

                            val fallback =
                                if (expert) {
                                    ReelsAiExpert.answerQuery(
                                        message
                                    )
                                } else {
                                    "در حال حاضر اتصال به هوش مصنوعی ابری برقرار نیست. دوباره امتحان کن 🙏"
                                }

                            _aiState.value =
                                _aiState.value.copy(
                                    chatMessages =
                                        _aiState.value.chatMessages +
                                                ChatMessage(
                                                    text = fallback,
                                                    isUser = false
                                                ),
                                    isChatTyping = false
                                )
                        }
                    )

                } else {

                    delay(450)

                    val fallback =
                        if (expert) {
                            ReelsAiExpert.answerQuery(message)
                        } else {
                            "حتماً 😊 بگو دقیقاً درباره چی می‌خوای بدونی؟"
                        }

                    _aiState.value =
                        _aiState.value.copy(
                            chatMessages =
                                _aiState.value.chatMessages +
                                        ChatMessage(
                                            text = fallback,
                                            isUser = false
                                        ),
                            isChatTyping = false
                        )
                }

            } catch (e: Exception) {

                _aiState.value =
                    _aiState.value.copy(
                        chatMessages =
                            _aiState.value.chatMessages +
                                    ChatMessage(
                                        text =
                                            "یه مشکلی در پردازش پیام پیش اومد. دوباره امتحان کن 🙏",
                                        isUser = false
                                    ),
                        isChatTyping = false,
                        errorMessage = e.message
                    )
            }
        }
    }

    // =========================================================
    // AI Content Generator
    // =========================================================

    fun setAiMode(mode: String) {
        _aiState.value =
            _aiState.value.copy(
                selectedMode = mode
            )
    }

    fun updateAiInputs(
        topic: String,
        audience: String,
        tone: String
    ) {
        _aiState.value =
            _aiState.value.copy(
                topicInput = topic,
                targetAudience = audience,
                tone = tone
            )
    }

    fun generateAiContent() {

        val state =
            _aiState.value

        val topic =
            state.topicInput.trim()

        if (topic.isBlank()) {

            viewModelScope.launch {
                _toastMessage.emit(
                    "لطفاً ابتدا موضوع ریلز را وارد کنید"
                )
            }

            return
        }

        _aiState.value =
            state.copy(
                isGenerating = true,
                errorMessage = null
            )

        val prompt =
            when (state.selectedMode) {

                "HOOK_GENERATOR" ->
                    """
                    برای موضوع «$topic»
                    دقیقاً ۵ قلاب جذاب برای سه ثانیه اول ویدیو بنویس.

                    مخاطب:
                    ${state.targetAudience.ifBlank { "عموم کاربران" }}

                    لحن:
                    ${state.tone}

                    برای هر قلاب دلیل جذابیت و ایده اجرای تصویری بده.
                    پاسخ فارسی باشد.
                    """.trimIndent()

                "SCRIPT_WRITER" ->
                    """
                    یک سناریوی کامل و زیر ۴۵ ثانیه برای موضوع
                    «$topic» بنویس.

                    شامل:
                    ۱. قلاب
                    ۲. بدنه
                    ۳. سه نکته کلیدی
                    ۴. CTA

                    مخاطب:
                    ${state.targetAudience.ifBlank { "عموم کاربران" }}

                    لحن:
                    ${state.tone}

                    پاسخ فارسی باشد.
                    """.trimIndent()

                "HASHTAG_FINDER" ->
                    """
                    برای موضوع «$topic»
                    هشتگ‌های عمومی، تخصصی و مرتبط با نیچ
                    پیشنهاد بده و یک کپشن کوتاه هم بنویس.
                    """.trimIndent()

                else ->
                    """
                    برای موضوع «$topic»
                    یک کپشن جذاب برای ریلز بنویس.

                    لحن:
                    ${state.tone}

                    کپشن باید کوتاه، طبیعی و تعامل‌محور باشد.
                    """.trimIndent()
            }

        viewModelScope.launch {

            val key =
                appSettings.value.customApiKey

            if (key.isNotBlank()) {

                val result =
                    GeminiApiService.generateContent(
                        prompt = prompt,
                        customApiKey = key
                    )

                result.fold(
                    onSuccess = { response ->

                        _aiState.value =
                            _aiState.value.copy(
                                isGenerating = false,
                                aiResult = response,
                                errorMessage = null
                            )
                    },
                    onFailure = {

                        val fallback =
                            generateLocalAiContent(
                                state.selectedMode,
                                topic,
                                state.targetAudience,
                                state.tone
                            )

                        _aiState.value =
                            _aiState.value.copy(
                                isGenerating = false,
                                aiResult = fallback,
                                errorMessage = null
                            )
                    }
                )

            } else {

                delay(300)

                val fallback =
                    generateLocalAiContent(
                        state.selectedMode,
                        topic,
                        state.targetAudience,
                        state.tone
                    )

                _aiState.value =
                    _aiState.value.copy(
                        isGenerating = false,
                        aiResult = fallback
                    )
            }
        }
    }

    private fun generateLocalAiContent(
        mode: String,
        topic: String,
        audience: String,
        tone: String
    ): String {

        return when (mode) {

            "HOOK_GENERATOR" ->
                """
                ۱. باور نمی‌کنی درباره $topic این نکته وجود داشته باشه!
                ۲. اگر درباره $topic این اشتباه رو می‌کنی، همین الان ببین.
                ۳. قبل از اینکه سراغ $topic بری، اینو بدون.
                ۴. فقط ۳۰ ثانیه وقت بذار تا نکته مهم $topic رو بفهمی.
                ۵. بیشتر مردم درباره $topic این قسمت رو نمی‌دونن.
                """.trimIndent()

            "SCRIPT_WRITER" ->
                """
                قلاب:
                بیشتر مردم درباره $topic یک اشتباه مهم انجام می‌دهند.

                بدنه:
                اول مشکل را مشخص کن، بعد راه‌حل را ساده و مرحله‌به‌مرحله توضیح بده.

                نکته ۱:
                از یک شروع واضح استفاده کن.

                نکته ۲:
                نتیجه را سریع نشان بده.

                نکته ۳:
                در پایان مخاطب را به تعامل دعوت کن.

                CTA:
                اگر این نکته برات مفید بود ذخیره‌اش کن.
                """.trimIndent()

            "HASHTAG_FINDER" ->
                "#$topic #تولید_محتوا #ریلز #اینستاگرام #وایرال"

            else ->
                "اگر درباره $topic کنجکاوی، این ویدیو رو تا آخر ببین 👀"
        }
    }

    // =========================================================
    // Settings
    // =========================================================

    fun toggleDarkMode(
        enabled: Boolean
    ) {

        viewModelScope.launch {

            val current =
                dao.getSettingsDirect()
                    ?: AppSettingsEntity()

            dao.saveSettings(
                current.copy(
                    isDarkMode = enabled
                )
            )
        }
    }

    fun setFontScale(
        scale: Float
    ) {

        viewModelScope.launch {

            val current =
                dao.getSettingsDirect()
                    ?: AppSettingsEntity()

            dao.saveSettings(
                current.copy(
                    fontScale = scale
                )
            )
        }
    }

    fun toggleRealAds(
        enabled: Boolean
    ) {

        viewModelScope.launch {

            val current =
                dao.getSettingsDirect()
                    ?: AppSettingsEntity()

            dao.saveSettings(
                current.copy(
                    isRealAdsEnabled = enabled
                )
            )
        }
    }

    fun saveCustomApiKey(
        key: String
    ) {

        viewModelScope.launch {

            val current =
                dao.getSettingsDirect()
                    ?: AppSettingsEntity()

            dao.saveSettings(
                current.copy(
                    customApiKey = key.trim()
                )
            )

            _toastMessage.emit(
                "کلید اختصاصی Gemini ذخیره شد 🔑"
            )
        }
    }
}
