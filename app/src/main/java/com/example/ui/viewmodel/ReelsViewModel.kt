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
import com.example.data.model.HookCategory
import com.example.data.model.ThumbnailTemplate
import com.example.data.model.VipPlan
import com.example.data.model.ViralHook
import com.example.data.remote.GeminiApiService
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
    val completedPlannerDays: Set<Int> = emptySet()
)

data class ScriptDraftState(
    val title: String = "",
    val hookText: String = "",
    val bodyText: String = "",
    val ctaText: String = "",
    val notes: String = "",
    val editingScriptId: Long? = null
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

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
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
    private val hooksDao = db.viralHookDao()
    private val scriptDao = db.scriptDao()
    private val settingsDao = db.settingsDao()

    // ---------------------------------------------------------
    // Navigation
    // ---------------------------------------------------------

    private val _currentTab = MutableStateFlow(AppTab.HOOKS)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // ---------------------------------------------------------
    // VIP
    // ---------------------------------------------------------

    private val _vipState = MutableStateFlow(VipUiState())
    val vipState: StateFlow<VipUiState> = _vipState.asStateFlow()

    // ---------------------------------------------------------
    // App Settings
    // ---------------------------------------------------------

    val appSettings: StateFlow<AppSettingsEntity> = settingsDao.getSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettingsEntity()
        )

    // ---------------------------------------------------------
    // Saved Scripts
    // ---------------------------------------------------------

    val savedScripts: StateFlow<List<SavedScriptEntity>> = scriptDao.getAllScripts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ---------------------------------------------------------
    // Script Draft
    // ---------------------------------------------------------

    private val _scriptDraft = MutableStateFlow(ScriptDraftState())
    val scriptDraft: StateFlow<ScriptDraftState> = _scriptDraft.asStateFlow()

    // ---------------------------------------------------------
    // Calculator
    // ---------------------------------------------------------

    private val _calculatorState = MutableStateFlow(EngagementCalculatorState())
    val calculatorState: StateFlow<EngagementCalculatorState> =
        _calculatorState.asStateFlow()

    // ---------------------------------------------------------
    // Thumbnail Studio
    // ---------------------------------------------------------

    private val _thumbnailStudioState = MutableStateFlow(ThumbnailStudioUiState())
    val thumbnailStudioState: StateFlow<ThumbnailStudioUiState> =
        _thumbnailStudioState.asStateFlow()

    // ---------------------------------------------------------
    // AI Assistant
    // ---------------------------------------------------------

    private val _aiState = MutableStateFlow(AiAssistantUiState())
    val aiState: StateFlow<AiAssistantUiState> = _aiState.asStateFlow()

    // ---------------------------------------------------------
    // Hooks Filters
    // ---------------------------------------------------------

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<HookCategory?>(null)
    val selectedCategory: StateFlow<HookCategory?> =
        _selectedCategory.asStateFlow()

    private val _showOnlyFavorites = MutableStateFlow(false)
    val showOnlyFavorites: StateFlow<Boolean> =
        _showOnlyFavorites.asStateFlow()

    // ---------------------------------------------------------
    // Ads
    // ---------------------------------------------------------

    private val _rewardedAdForHook = MutableStateFlow<ViralHook?>(null)
    val rewardedAdForHook: StateFlow<ViralHook?> =
        _rewardedAdForHook.asStateFlow()

    private val _isAdWatching = MutableStateFlow(false)
    val isAdWatching: StateFlow<Boolean> =
        _isAdWatching.asStateFlow()

    private val _adCountdown = MutableStateFlow(5)
    val adCountdown: StateFlow<Int> =
        _adCountdown.asStateFlow()

    // ---------------------------------------------------------
    // VIP Checkout
    // ---------------------------------------------------------

    private val _selectedCheckoutPlan = MutableStateFlow<VipPlan?>(null)
    val selectedCheckoutPlan: StateFlow<VipPlan?> =
        _selectedCheckoutPlan.asStateFlow()

    // ---------------------------------------------------------
    // Toast
    // ---------------------------------------------------------

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> =
        _toastMessage.asSharedFlow()

    // ---------------------------------------------------------
    // Initialization
    // ---------------------------------------------------------

    init {
        calculateEngagementRate()

        viewModelScope.launch {
            val settings = settingsDao.getSettingsDirect()

            if (settings != null) {

                val unlockedIds = settings.unlockedHookIds
                    .split(",")
                    .filter { it.isNotBlank() }
                    .mapNotNull { it.toIntOrNull() }
                    .toSet()

                val completedDays = settings.completedPlannerDays
                    .split(",")
                    .filter { it.isNotBlank() }
                    .mapNotNull { it.toIntOrNull() }
                    .toSet()

                _vipState.value = _vipState.value.copy(
                    isVipActive = settings.isVipUser,
                    unlockedHookIds = unlockedIds,
                    completedPlannerDays = completedDays
                )
            }
        }
    }

    // =========================================================
    // Navigation
    // =========================================================

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
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

    fun toggleFavoritesFilter() {
        _showOnlyFavorites.value = !_showOnlyFavorites.value
    }

    fun toggleFavoriteHook(hookId: Int) {

        val currentFavs =
            _vipState.value.favoriteHookIds.toMutableSet()

        if (currentFavs.contains(hookId)) {
            currentFavs.remove(hookId)
        } else {
            currentFavs.add(hookId)
        }

        _vipState.value =
            _vipState.value.copy(
                favoriteHookIds = currentFavs
            )
    }

    // =========================================================
    // Rewarded Ads
    // =========================================================

    fun requestUnlockHook(hook: ViralHook) {

        if (
            _vipState.value.isVipActive ||
            _vipState.value.unlockedHookIds.contains(hook.id)
        ) {
            return
        }

        _rewardedAdForHook.value = hook
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

            val hook = _rewardedAdForHook.value

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

            val currentSettings =
                settingsDao.getSettingsDirect()
                    ?: AppSettingsEntity()

            val set = currentSettings.unlockedHookIds
                .split(",")
                .filter { it.isNotBlank() }
                .toMutableSet()

            set.add(hookId.toString())

            val updated =
                currentSettings.copy(
                    unlockedHookIds = set.joinToString(",")
                )

            settingsDao.saveSettings(updated)
        }
    }

    // =========================================================
    // VIP
    // =========================================================

    fun openVipCheckout(plan: VipPlan) {
        _selectedCheckoutPlan.value = plan
    }

    fun dismissCheckout() {
        _selectedCheckoutPlan.value = null
    }

    fun confirmPurchase() {

        viewModelScope.launch {

            _selectedCheckoutPlan.value?.let { plan ->

                _vipState.value =
                    _vipState.value.copy(
                        isVipActive = true
                    )

                val current =
                    settingsDao.getSettingsDirect()
                        ?: AppSettingsEntity()

                settingsDao.saveSettings(
                    current.copy(
                        isVipUser = true
                    )
                )

                _toastMessage.emit(
                    "تبریک! اشتراک ${plan.title} با موفقیت فعال شد 💎"
                )
            }

            dismissCheckout()
        }
    }

    // =========================================================
    // Content Planner
    // =========================================================

    fun toggleDayCompleted(dayNumber: Int) {

        val currentDays =
            _vipState.value.completedPlannerDays.toMutableSet()

        if (currentDays.contains(dayNumber)) {
            currentDays.remove(dayNumber)
        } else {
            currentDays.add(dayNumber)
        }

        _vipState.value =
            _vipState.value.copy(
                completedPlannerDays = currentDays
            )

        viewModelScope.launch {

            val currentSettings =
                settingsDao.getSettingsDirect()
                    ?: AppSettingsEntity()

            val updated =
                currentSettings.copy(
                    completedPlannerDays =
                        currentDays.joinToString(",")
                )

            settingsDao.saveSettings(updated)
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

        _scriptDraft.value =
            _scriptDraft.value.copy(
                title = title ?: _scriptDraft.value.title,
                hookText = hookText ?: _scriptDraft.value.hookText,
                bodyText = bodyText ?: _scriptDraft.value.bodyText,
                ctaText = ctaText ?: _scriptDraft.value.ctaText,
                notes = notes ?: _scriptDraft.value.notes
            )
    }

    fun loadScriptIntoEditor(script: SavedScriptEntity) {

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

    fun applyHookToScript(hook: ViralHook) {

        _scriptDraft.value =
            _scriptDraft.value.copy(
                title =
                    if (_scriptDraft.value.title.isBlank()) {
                        "سناریوی ریلز: ${hook.title}"
                    } else {
                        _scriptDraft.value.title
                    },
                hookText = hook.template
            )

        _currentTab.value = AppTab.SCRIPTS

        viewModelScope.launch {
            _toastMessage.emit(
                "قلاب وارد سناریوساز شد ✍️"
            )
        }
    }

    fun applyPlannerDayToScript(planDay: ContentPlanDay) {

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

        viewModelScope.launch {
            _toastMessage.emit(
                "ایده روز ${planDay.dayNumber} وارد سناریوساز شد ✍️"
            )
        }
    }

    fun resetScriptDraft() {
        _scriptDraft.value = ScriptDraftState()
    }

    fun saveCurrentScript() {

        val draft = _scriptDraft.value

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

            val entity =
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

            scriptDao.insertScript(entity)

            _toastMessage.emit(
                "سناریو با موفقیت ذخیره شد ✅"
            )

            resetScriptDraft()
        }
    }

    fun deleteScript(script: SavedScriptEntity) {

        viewModelScope.launch {

            scriptDao.deleteScript(script)

            _toastMessage.emit(
                "سناریو حذف شد 🗑️"
            )
        }
    }

    // =========================================================
    // Engagement Calculator
    // =========================================================

    fun updateCalculatorField(
        followers: String? = null,
        likes: String? = null,
        comments: String? = null,
        shares: String? = null,
        saves: String? = null
    ) {

        _calculatorState.value =
            _calculatorState.value.copy(
                followers =
                    followers ?: _calculatorState.value.followers,
                likes =
                    likes ?: _calculatorState.value.likes,
                comments =
                    comments ?: _calculatorState.value.comments,
                shares =
                    shares ?: _calculatorState.value.shares,
                saves =
                    saves ?: _calculatorState.value.saves
            )

        calculateEngagementRate()
    }

    private fun calculateEngagementRate() {

        val f =
            _calculatorState.value.followers.toDoubleOrNull()
                ?: 1.0

        val l =
            _calculatorState.value.likes.toDoubleOrNull()
                ?: 0.0

        val c =
            _calculatorState.value.comments.toDoubleOrNull()
                ?: 0.0

        val sh =
            _calculatorState.value.shares.toDoubleOrNull()
                ?: 0.0

        val sa =
            _calculatorState.value.saves.toDoubleOrNull()
                ?: 0.0

        if (f <= 0.0) return

        val totalInteractions =
            l +
                    (c * 2.0) +
                    (sh * 3.5) +
                    (sa * 3.0)

        val rawRate =
            (totalInteractions / f) * 100.0

        val rate =
            kotlin.math.round(rawRate * 100) / 100.0

        val (grade, rec) =
            when {

                rate >= 8.0 ->
                    Pair(
                        "فوق‌العاده وایرال 🔥",
                        "تعامل پیج شما در سطح بسیار بالایی قرار دارد."
                    )

                rate >= 4.5 ->
                    Pair(
                        "بسیار عالی 🚀",
                        "نرخ تعامل شما بسیار خوب است. روی محتوای ذخیره‌محور تمرکز کنید."
                    )

                rate >= 2.5 ->
                    Pair(
                        "متوسط رو به رشد 📈",
                        "پیج شما وضعیت خوبی دارد اما می‌توان نرخ تعامل را بهتر کرد."
                    )

                else ->
                    Pair(
                        "نیازمند بهینه‌سازی ⚠️",
                        "پیشنهاد می‌شود روی قلاب، ارزش محتوا و دعوت به تعامل بیشتر کار کنید."
                    )
            }

        _calculatorState.value =
            _calculatorState.value.copy(
                calculatedRate = rate,
                engagementGrade = grade,
                recommendation = rec
            )
    }

    // =========================================================
    // Thumbnail Studio
    // =========================================================

    fun setThumbnailRatio(ratio: String) {

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

        _thumbnailStudioState.value =
            _thumbnailStudioState.value.copy(
                primaryTitle =
                    primaryTitle
                        ?: _thumbnailStudioState.value.primaryTitle,
                subtitle =
                    subtitle
                        ?: _thumbnailStudioState.value.subtitle,
                badgeText =
                    badgeText
                        ?: _thumbnailStudioState.value.badgeText
            )
    }

    fun toggleThumbnailBadge(show: Boolean) {

        _thumbnailStudioState.value =
            _thumbnailStudioState.value.copy(
                showBadge = show
            )
    }

    fun setThumbnailBadgePosition(position: String) {

        _thumbnailStudioState.value =
            _thumbnailStudioState.value.copy(
                badgePosition = position
            )
    }

    fun setThumbnailGradient(index: Int) {

        _thumbnailStudioState.value =
            _thumbnailStudioState.value.copy(
                selectedGradientIndex = index
            )
    }

    // =========================================================
    // AI Assistant - Chat
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

    /**
     * پاسخ‌های کاملاً ساده و روزمره.
     *
     * این قسمت باعث می‌شود پیام‌هایی مثل:
     * سلام
     * خوبی؟
     * یه سوال دارم
     * ممنون
     * خداحافظ
     *
     * به موتور تخصصی ریلز ارسال نشوند.
     */
    private fun getNaturalLocalReply(
        message: String
    ): String? {

        val text =
            message
                .trim()
                .lowercase()

        return when {

            text.matches(
                Regex(
                    "^(سلام|سلاممم|درود|های|hello|hi)[!.، ؟? ]*$"
                )
            ) -> {
                "سلام دوست من! 👋😊\nخوش اومدی. بگو ببینم چطور می‌تونم کمکت کنم؟"
            }

            text.contains("یه سوال دارم") ||
                    text.contains("یک سوال دارم") ||
                    text.contains("سؤال دارم") ||
                    text.contains("یه سئوال دارم") -> {
                "حتماً 😊 بپرس، گوشم با توئه."
            }

            text.matches(
                Regex(
                    "^(خوبی|خوبی؟|چطوری|چطوری؟|چه خبر)[!.، ؟? ]*$"
                )
            ) -> {
                "مرسی که پرسیدی 😊 آماده‌ام کمکت کنم. بگو چه کاری داریم؟"
            }

            text.matches(
                Regex(
                    "^(ممنون|مرسی|دمت گرم|خیلی ممنون)[!.، ؟? ]*$"
                )
            ) -> {
                "خواهش می‌کنم ❤️ خوشحالم که تونستم کمکت کنم."
            }

            text.matches(
                Regex(
                    "^(خداحافظ|فعلاً|فعلا|بای|bye)[!.، ؟? ]*$"
                )
            ) -> {
                "فعلاً دوست من 👋 هر وقت برگشتی من اینجام."
            }

            else -> null
        }
    }

    /**
     * تشخیص می‌دهد که پیام به حوزه تخصصی Reels Studio مربوط است یا خیر.
     */
    private fun isReelsRelatedMessage(
        message: String
    ): Boolean {

        val text =
            message
                .lowercase()

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

        return keywords.any {
            text.contains(it)
        }
    }

    /**
     * ساخت context مکالمه.
     *
     * حداکثر ۲۰ پیام اخیر ارسال می‌شود تا مکالمه
     * بیش از حد طولانی نشود.
     */
    private fun buildChatHistory(
        messages: List<ChatMessage>,
        currentMessage: String
    ): String {

        val previousMessages =
            messages
                .dropLast(1)
                .takeLast(20)

        if (previousMessages.isEmpty()) {
            return "هنوز مکالمه قبلی وجود ندارد."
        }

        return buildString {

            previousMessages.forEach { message ->

                val role =
                    if (message.isUser) {
                        "کاربر"
                    } else {
                        "دستیار"
                    }

                append(role)
                append(": ")
                append(message.text)
                append("\n\n")
            }

            append("کاربر: ")
            append(currentMessage)
        }
    }

    /**
     * ارسال پیام چت.
     *
     * تفاوت اصلی با نسخه قبلی:
     *
     * 1. پیام‌های ساده محلی پاسخ می‌گیرند.
     * 2. Gemini تاریخچه مکالمه را دریافت می‌کند.
     * 3. موضوعات تخصصی وارد حالت متخصص می‌شوند.
     * 4. موضوعات عمومی مجبور به تبدیل شدن به سؤال ریلز نمی‌شوند.
     */
    fun sendChatMessage(
        promptText: String = ""
    ) {

        val messageToSend =
            promptText
                .ifBlank {
                    _aiState.value.chatInput
                }
                .trim()

        if (
            messageToSend.isBlank() ||
            _aiState.value.isChatTyping
        ) {
            return
        }

        val userMessage =
            ChatMessage(
                text = messageToSend,
                isUser = true
            )

        val currentMessages =
            _aiState.value.chatMessages
                .toMutableList()
                .apply {
                    add(userMessage)
                }

        _aiState.value =
            _aiState.value.copy(
                chatMessages = currentMessages,
                chatInput = "",
                isChatTyping = true,
                errorMessage = null
            )

        viewModelScope.launch {

            try {

                // -------------------------------------------------
                // پاسخ سریع برای پیام‌های کاملاً طبیعی
                // -------------------------------------------------

                val naturalReply =
                    getNaturalLocalReply(
                        messageToSend
                    )

                if (naturalReply != null) {

                    delay(350)

                    _aiState.value =
                        _aiState.value.copy(
                            chatMessages =
                                _aiState.value.chatMessages +
                                        ChatMessage(
                                            text = naturalReply,
                                            isUser = false
                                        ),
                            isChatTyping = false
                        )

                    return@launch
                }

                // -------------------------------------------------
                // ساخت تاریخچه
                // -------------------------------------------------

                val history =
                    buildChatHistory(
                        messages =
                            _aiState.value.chatMessages,
                        currentMessage =
                            messageToSend
                    )

                val isExpertTopic =
                    isReelsRelatedMessage(
                        messageToSend
                    )

                // -------------------------------------------------
                // Prompt اصلی
                // -------------------------------------------------

                val systemPrompt =

                    if (isExpertTopic) {

                        """
                        تو دستیار هوشمند حرفه‌ای اپلیکیشن Reels Studio هستی.

                        نقش تو:
                        یک دستیار مکالمه‌ای طبیعی، دوستانه و متخصص در زمینه
                        Instagram Reels، YouTube Shorts و تولید محتوا.

                        قوانین بسیار مهم:

                        1. مثل یک دستیار واقعی و طبیعی صحبت کن.

                        2. سؤال کاربر را دقیقاً همان‌طور که پرسیده پاسخ بده.

                        3. اگر سؤال مربوط به ریلز، اینستاگرام، شورتز،
                        قلاب، هوک، سناریو، کپشن، هشتگ، وایرال شدن،
                        رشد پیج یا فروش است، پاسخ تخصصی و کاربردی بده.

                        4. از تاریخچه مکالمه استفاده کن.

                        5. اگر کاربر گفت «همین»، «این»، «اون»،
                        «موضوع قبلی» یا عبارتی مشابه، منظور او را
                        با توجه به مکالمه قبلی درک کن.

                        6. سؤال را بی‌دلیل دوباره از کاربر نپرس،
                        اگر پاسخ آن در تاریخچه وجود دارد.

                        7. پاسخ‌ها فارسی، روان و طبیعی باشند.

                        8. از ایموجی به اندازه مناسب استفاده کن.

                        9. درباره الگوریتم اینستاگرام ادعاهای قطعی
                        و غیرقابل اثبات نکن.

                        10. پاسخ‌ها کاربردی باشند و از توضیحات
                        بی‌دلیل طولانی پرهیز کن.

                        تاریخچه مکالمه:
                        $history
                        """.trimIndent()

                    } else {

                        """
                        تو دستیار هوشمند و مکالمه‌ای Reels Studio هستی.

                        مهم‌ترین وظیفه تو این است که مثل یک دستیار
                        واقعی و طبیعی با کاربر صحبت کنی.

                        قوانین:

                        1. سؤال کاربر را همان‌طور که هست پاسخ بده.

                        2. اگر سؤال عمومی است، آن را به ریلز،
                        اینستاگرام یا تولید محتوا ربط نده.

                        3. فقط زمانی که موضوع سؤال مربوط به ریلز،
                        تولید محتوا، شبکه‌های اجتماعی یا حوزه تخصصی
                        برنامه شد، از تخصص خودت استفاده کن.

                        4. از تاریخچه مکالمه برای درک منظور کاربر استفاده کن.

                        5. اگر سؤال ادامه سؤال قبلی است،
                        پاسخ را بر اساس همان context بده.

                        6. فارسی روان و دوستانه صحبت کن.

                        7. از پاسخ‌های خشک، رباتیک و قالبی پرهیز کن.

                        8. پاسخ غیرضروری را طولانی نکن.

                        تاریخچه مکالمه:
                        $history
                        """.trimIndent()
                    }

                // -------------------------------------------------
                // Gemini
                // -------------------------------------------------

                val customKey =
                    appSettings.value.customApiKey

                if (customKey.isNotBlank()) {

                    val geminiResult =
                        GeminiApiService.generateContent(
                            prompt = systemPrompt,
                            customApiKey = customKey
                        )

                    geminiResult.fold(

                        onSuccess = { reply ->

                            val cleanReply =
                                reply
                                    .trim()
                                    .ifBlank {
                                        "متأسفم، این بار نتونستم پاسخ مناسبی تولید کنم. دوباره امتحان کن."
                                    }

                            _aiState.value =
                                _aiState.value.copy(
                                    chatMessages =
                                        _aiState.value.chatMessages +
                                                ChatMessage(
                                                    text = cleanReply,
                                                    isUser = false
                                                ),
                                    isChatTyping = false,
                                    errorMessage = null
                                )
                        },

                        onFailure = {

                            val fallbackReply =

                                if (isExpertTopic) {

                                    ReelsAiExpert.answerQuery(
                                        messageToSend
                                    )

                                } else {

                                    "در حال حاضر اتصال به هوش مصنوعی ابری برقرار نیست. " +
                                            "اگر سؤال دیگری داری، دوباره امتحان کن 🙏"
                                }

                            _aiState.value =
                                _aiState.value.copy(
                                    chatMessages =
                                        _aiState.value.chatMessages +
                                                ChatMessage(
                                                    text = fallbackReply,
                                                    isUser = false
                                                ),
                                    isChatTyping = false,
                                    errorMessage = null
                                )
                        }
                    )

                } else {

                    // -------------------------------------------------
                    // Offline mode
                    // -------------------------------------------------

                    delay(450)

                    val fallbackReply =

                        if (isExpertTopic) {

                            ReelsAiExpert.answerQuery(
                                messageToSend
                            )

                        } else {

                            "حتماً 😊 بگو دقیقاً درباره چی می‌خوای بدونی؟"
                        }

                    _aiState.value =
                        _aiState.value.copy(
                            chatMessages =
                                _aiState.value.chatMessages +
                                        ChatMessage(
                                            text = fallbackReply,
                                            isUser = false
                                        ),
                            isChatTyping = false,
                            errorMessage = null
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

        val state = _aiState.value

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
            _aiState.value.copy(
                isGenerating = true,
                errorMessage = null
            )

        val prompt =

            when (state.selectedMode) {

                "HOOK_GENERATOR" -> """

                    به عنوان متخصص تولید محتوای کوتاه،
                    برای موضوع «$topic» دقیقاً ۵ قلاب جذاب
                    برای ۳ ثانیه اول ویدیو بنویس.

                    مخاطب هدف:
                    ${state.targetAudience.ifBlank {
                        "عموم کاربران اینستاگرام"
                    }}

                    لحن:
                    ${state.tone}

                    برای هر قلاب:
                    - متن قلاب
                    - دلیل جذابیت
                    - ایده تصویر یا اجرای ثانیه اول

                    پاسخ کاملاً فارسی باشد.

                """.trimIndent()

                "SCRIPT_WRITER" -> """

                    یک سناریوی کامل و زیر ۴۵ ثانیه
                    برای یک ویدیوی کوتاه بنویس.

                    موضوع:
                    $topic

                    مخاطب:
                    ${state.targetAudience.ifBlank {
                        "عموم کاربران"
                    }}

                    لحن:
                    ${state.tone}

                    سناریو شامل:

                    ۱. قلاب ۳ ثانیه اول
                    ۲. بدنه اصلی
                    ۳. سه نکته کلیدی
                    ۴. CTA نهایی

                    پاسخ کاملاً فارسی باشد.

                """.trimIndent()

                "HASHTAG_FINDER" -> """

                    برای موضوع «$topic» یک مجموعه هشتگ
                    مناسب و مرتبط پیشنهاد بده.

                    شامل:

                    - هشتگ‌های عمومی
                    - هشتگ‌های تخصصی
                    - هشتگ‌های مرتبط با نیچ

                    همچنین یک کپشن کوتاه و جذاب برای ویدیو بنویس.

                """.trimIndent()

                else -> """

                    یک کپشن جذاب برای ریلز بنویس.

                    موضوع:
                    $topic

                    لحن:
                    ${state.tone}

                    کپشن باید:
                    - شروع کنجکاوی‌برانگیز داشته باشد
                    - کوتاه و خوانا باشد
                    - مخاطب را به تعامل دعوت کند

                """.trimIndent()
            }

        viewModelScope.launch {

            val customKey =
                appSettings.value.customApiKey

            if (customKey.isNotBlank()) {

                val result =
                    GeminiApiService.generateContent(
                        prompt = prompt,
                        customApiKey = customKey
                    )

                result.fold(

                    onSuccess = { responseText ->

                        _aiState.value =
                            _aiState.value.copy(
                                isGenerating = false,
                                aiResult = responseText,
                                errorMessage = null
                            )

                        _toastMessage.emit(
                            "محتوای هوش مصنوعی با موفقیت تولید شد ✨"
                        )
                    },

                    onFailure = {

                        val fallbackContent =

                            when (state.selectedMode) {

                                "HOOK_GENERATOR" ->
                                    ReelsAiExpert.generateHooksForTopic(
                                        topic,
                                        state.targetAudience,
                                        state.tone
                                    )

                                "SCRIPT_WRITER" ->
                                    ReelsAiExpert.generateScriptForTopic(
                                        topic,
                                        state.targetAudience,
                                        state.tone
                                    )

                                "HASHTAG_FINDER" ->
                                    ReelsAiExpert.generateHashtagsForTopic(
                                        topic
                                    )

                                else ->
                                    ReelsAiExpert.generateCaptionForTopic(
                                        topic,
                                        state.tone
                                    )
                            }

                        _aiState.value =
                            _aiState.value.copy(
                                isGenerating = false,
                                aiResult = fallbackContent,
                                errorMessage = null
                            )

                        _toastMessage.emit(
                            "محتوای هوش مصنوعی آماده شد ✨"
                        )
                    }
                )

            } else {

                delay(500)

                val generated =

                    when (state.selectedMode) {

                        "HOOK_GENERATOR" ->
                            ReelsAiExpert.generateHooksForTopic(
                                topic,
                                state.targetAudience,
                                state.tone
                            )

                        "SCRIPT_WRITER" ->
                            ReelsAiExpert.generateScriptForTopic(
                                topic,
                                state.targetAudience,
                                state.tone
                            )

                        "HASHTAG_FINDER" ->
                            ReelsAiExpert.generateHashtagsForTopic(
                                topic
                            )

                        else ->
                            ReelsAiExpert.generateCaptionForTopic(
                                topic,
                                state.tone
                            )
                    }

                _aiState.value =
                    _aiState.value.copy(
                        isGenerating = false,
                        aiResult = generated,
                        errorMessage = null
                    )

                _toastMessage.emit(
                    "محتوا توسط موتور هوشمند تولید شد ✨"
                )
            }
        }
    }

    // =========================================================
    // Settings
    // =========================================================

    fun toggleDarkMode(enabled: Boolean) {

        viewModelScope.launch {

            val current =
                settingsDao.getSettingsDirect()
                    ?: AppSettingsEntity()

            settingsDao.saveSettings(
                current.copy(
                    isDarkMode = enabled
                )
            )
        }
    }

    fun setFontScale(scale: Float) {

        viewModelScope.launch {

            val current =
                settingsDao.getSettingsDirect()
                    ?: AppSettingsEntity()

            settingsDao.saveSettings(
                current.copy(
                    fontScale = scale
                )
            )
        }
    }

    fun toggleRealAds(enabled: Boolean) {

        viewModelScope.launch {

            val current =
                settingsDao.getSettingsDirect()
                    ?: AppSettingsEntity()

            settingsDao.saveSettings(
                current.copy(
                    isRealAdsEnabled = enabled
                )
            )
        }
    }

    fun saveCustomApiKey(key: String) {

        viewModelScope.launch {

            val current =
                settingsDao.getSettingsDirect()
                    ?: AppSettingsEntity()

            settingsDao.saveSettings(
                current.copy(
                    customApiKey = key.trim()
                )
            )

            _toastMessage.emit(
                "کلید اختصاصی Gemini ذخیره شد 🔑"
            )
        }
    }

    // =========================================================
    // Clear Local Data
    // =========================================================

    fun clearAllSavedData() {

        viewModelScope.launch {

            scriptDao.deleteAll()

            val current =
                settingsDao.getSettingsDirect()
                    ?: AppSettingsEntity()

            settingsDao.saveSettings(
                current.copy(
                    unlockedHookIds = "",
                    completedPlannerDays = "",
                    isVipUser = false
                )
            )

            _vipState.value =
                VipUiState()

            _toastMessage.emit(
                "تمام داده‌های محلی برنامه ریست شدند"
            )
        }
    }
}
