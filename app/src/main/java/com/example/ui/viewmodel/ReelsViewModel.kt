package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.ReelsAiExpert
import com.example.data.local.AppDatabase
import com.example.data.local.AppSettingsEntity
import com.example.data.local.SavedScriptEntity
import com.example.data.local.VipStateEntity
import com.example.data.model.AppTab
import com.example.data.model.ContentDayPlan
import com.example.data.model.CoverCategory
import com.example.data.model.CoverTemplate
import com.example.data.model.EngagementResult
import com.example.data.model.HookCategory
import com.example.data.model.HookItem
import com.example.data.model.ScriptTemplate
import com.example.data.remote.GeminiApiService
import com.example.data.repository.ReelsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HooksUiState(
    val allHooks: List<HookItem> = emptyList(),
    val selectedCategory: HookCategory = HookCategory.ALL,
    val searchQuery: String = "",
    val showOnlyFavorites: Boolean = false,
    val favoriteIds: Set<String> = emptySet(),
    val unlockedHookIds: Set<String> = emptySet(),
    val isVipUser: Boolean = false
)

data class ScriptMakerUiState(
    val templates: List<ScriptTemplate> = emptyList(),
    val selectedTemplate: ScriptTemplate? = null,
    val customTopic: String = "",
    val customBenefit: String = "",
    val customObstacle: String = "",
    val customCta: String = "",
    val generatedScript: String = "",
    val isVipUser: Boolean = false
)

data class CalculatorUiState(
    val followers: String = "15000",
    val likes: String = "850",
    val comments: String = "120",
    val saves: String = "430",
    val shares: String = "95",
    val result: EngagementResult? = null
)

data class CoverStudioUiState(
    val selectedCategory: CoverCategory = CoverCategory.ALL,
    val selectedTemplateId: String = "cover_tech_1",
    val headlineText: String = "۵ ابزار رایگان هوش مصنوعی",
    val subHeadlineText: String = "که بدون آن‌ها در سال جدید جا می‌مانید!",
    val badgeText: String = "هوش مصنوعی ۲۰۲۶",
    val selectedAccentColor: Long = 0xFF00F0FF,
    val selectedTextColor: Long = 0xFFFFFFFF,
    val customImageUri: String? = null,
    val isYoutubeShortsBadge: Boolean = true, // Shows Shorts badge or Reels badge
    val isAiGeneratingTitle: Boolean = false
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class AiAssistantUiState(
    val activeSubTab: Int = 0, // 0 = Chat Assistant, 1 = Content Generator
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            text = "سلام دوست من! 👋 من دستیار هوشمند اختصاصی ریلز و شورتز هستم.\n\nهر سوالی در مورد رشد پیج، دور زدن الگوریتم اکسپلور، ایده‌های وایرال، سناریونویسی و قلاب‌های ۳ ثانیه‌ای داری بپرس تا کمکت کنم! 👇",
            isUser = false
        )
    ),
    val chatInput: String = "",
    val isChatTyping: Boolean = false,
    val userPrompt: String = "",
    val selectedMode: String = "HOOK_GENERATOR", // HOOK_GENERATOR, SCRIPT_WRITER, HASHTAG_FINDER, CAPTION_WRITER
    val topicInput: String = "",
    val targetAudience: String = "",
    val tone: String = "هیجانی و شوکه‌کننده",
    val isGenerating: Boolean = false,
    val aiResult: String = "",
    val errorMessage: String? = null
)

class ReelsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReelsRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = ReelsRepository(db.reelsDao())
    }

    // Active Navigation Tab
    private val _currentTab = MutableStateFlow(AppTab.HOOKS)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Snackbars / Feedback Messages
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    // VIP State Flow from DB
    val vipState: StateFlow<VipStateEntity> = repository.vipState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        VipStateEntity()
    )

    // App Settings Flow from DB
    val appSettings: StateFlow<AppSettingsEntity> = repository.appSettings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AppSettingsEntity()
    )

    // AI Assistant State
    private val _aiState = MutableStateFlow(AiAssistantUiState())
    val aiState: StateFlow<AiAssistantUiState> = _aiState.asStateFlow()

    // Cover / Thumbnail Studio State
    private val _coverStudioState = MutableStateFlow(CoverStudioUiState())
    val coverStudioState: StateFlow<CoverStudioUiState> = _coverStudioState.asStateFlow()

    fun getAllCoverTemplates(): List<CoverTemplate> = repository.getAllCoverTemplates()

    // Saved Scripts Flow from DB
    val savedScripts: StateFlow<List<SavedScriptEntity>> = repository.allSavedScripts.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Planner Progress Flow from DB
    val plannerProgress: StateFlow<Map<Int, Boolean>> = repository.plannerProgress.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyMap()
    )

    // Hooks State
    private val _selectedCategory = MutableStateFlow(HookCategory.ALL)
    val selectedCategory: StateFlow<HookCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showOnlyFavorites = MutableStateFlow(false)
    val showOnlyFavorites: StateFlow<Boolean> = _showOnlyFavorites.asStateFlow()

    val favoriteIds: StateFlow<Set<String>> = repository.favoriteHookIds.combine(repository.favoriteHookIds) { ids, _ ->
        ids.toSet()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val allHooks: List<HookItem> = repository.getAllHooks()
    val allTemplates: List<ScriptTemplate> = repository.getAllTemplates()
    val allPlannerDays: List<ContentDayPlan> = repository.getAllPlannerDays()

    // Script Maker State
    private val _scriptMakerState = MutableStateFlow(
        ScriptMakerUiState(
            templates = allTemplates,
            selectedTemplate = allTemplates.firstOrNull()
        )
    )
    val scriptMakerState: StateFlow<ScriptMakerUiState> = _scriptMakerState.asStateFlow()

    // Calculator State
    private val _calculatorState = MutableStateFlow(CalculatorUiState())
    val calculatorState: StateFlow<CalculatorUiState> = _calculatorState.asStateFlow()

    // Rewarded Ad Simulation Dialog State
    private val _rewardedAdForHook = MutableStateFlow<HookItem?>(null)
    val rewardedAdForHook: StateFlow<HookItem?> = _rewardedAdForHook.asStateFlow()

    private val _isAdWatching = MutableStateFlow(false)
    val isAdWatching: StateFlow<Boolean> = _isAdWatching.asStateFlow()

    private val _adCountdown = MutableStateFlow(5)
    val adCountdown: StateFlow<Int> = _adCountdown.asStateFlow()

    // Checkout Confirmation Dialog
    data class CheckoutPlan(val title: String, val price: String, val days: Int)
    private val _selectedCheckoutPlan = MutableStateFlow<CheckoutPlan?>(null)
    val selectedCheckoutPlan: StateFlow<CheckoutPlan?> = _selectedCheckoutPlan.asStateFlow()

    init {
        // Initialize default script
        allTemplates.firstOrNull()?.let { selectTemplate(it) }
        calculateEngagement()
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setCategory(category: HookCategory) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavoritesFilter() {
        _showOnlyFavorites.value = !_showOnlyFavorites.value
    }

    fun toggleFavoriteHook(hookId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(hookId, favoriteIds.value.toList())
            val isFav = !favoriteIds.value.contains(hookId)
            _toastMessage.emit(if (isFav) "به قلاب‌های ذخیره‌شده اضافه شد ★" else "از ذخیره‌شده‌ها حذف شد")
        }
    }

    fun isHookUnlocked(hook: HookItem): Boolean {
        if (!hook.isVipOnly) return true
        if (vipState.value.isVipActive) return true
        val unlocked = vipState.value.temporaryUnlockedHooks.split(",")
        return unlocked.contains(hook.id)
    }

    fun promptWatchRewardedAd(hook: HookItem) {
        _rewardedAdForHook.value = hook
    }

    fun dismissRewardedAdPrompt() {
        _rewardedAdForHook.value = null
        _isAdWatching.value = false
    }

    fun startWatchingRewardedAd() {
        val hook = _rewardedAdForHook.value ?: return
        _isAdWatching.value = true
        _adCountdown.value = 5

        viewModelScope.launch {
            for (i in 5 downTo 1) {
                _adCountdown.value = i
                delay(1000)
            }
            // Reward user
            repository.unlockHookWithRewardedAd(hook.id, vipState.value)
            _isAdWatching.value = false
            _rewardedAdForHook.value = null
            _toastMessage.emit("تبریک! قلاب وایرال برای شما باز شد 🎁")
        }
    }

    // Script Maker functions
    fun selectTemplate(template: ScriptTemplate) {
        _scriptMakerState.value = _scriptMakerState.value.copy(
            selectedTemplate = template,
            customTopic = template.placeholderTopic,
            customBenefit = template.placeholderBenefit,
            customObstacle = template.placeholderObstacle,
            customCta = template.ctaTemplate
        )
        generateScript()
    }

    fun updateCustomFields(topic: String, benefit: String, obstacle: String, cta: String) {
        _scriptMakerState.value = _scriptMakerState.value.copy(
            customTopic = topic,
            customBenefit = benefit,
            customObstacle = obstacle,
            customCta = cta
        )
        generateScript()
    }

    private fun generateScript() {
        val state = _scriptMakerState.value
        val template = state.selectedTemplate ?: return

        val hook = template.hookTemplate
            .replace("[محصول یا سرویس]", state.customTopic.ifBlank { "محصول شما" })
            .replace("[مهارت اصلی]", state.customTopic.ifBlank { "مهارت جدید" })

        val body = template.bodyTemplate
            .replace("[علت تفاوت کیفیت یا تامین مستقیم]", state.customBenefit.ifBlank { "کیفیت بالا و حذف واسطه" })
            .replace("[ویژگی کلیدی یا گارانتی محصول]", state.customBenefit.ifBlank { "ضمانت اختصاصی" })
            .replace("[بزرگترین ضرر مشتری]", state.customObstacle.ifBlank { "هزینه اضافی و پشیمانی" })
            .replace("[اشتباه رایج مثل هشتگ نامربوط یا شروع بدون قلاب]", state.customObstacle.ifBlank { "شروع بدون قلاب شوکه‌کننده" })
            .replace("[نام ابزار ۱]", "CapCut / AutoCap")
            .replace("[نام ابزار ۲]", "Adobe Podcast AI")
            .replace("[نام ابزار ۳]", "Reels Studio")
            .replace("[نتیجه ملموس]", state.customBenefit.ifBlank { "رشد ۳ برابری فروش و بازدید" })

        val cta = state.customCta.ifBlank { template.ctaTemplate }

        val full = "🎬 [قلاب ۳ ثانیه‌ای اول ویدیو]:\n$hook\n\n📌 [بدنه اصلی و ارائه ارزش]:\n$body\n\n🚀 [کال تو اکشن و دعوت به اقدام نهایی]:\n$cta"

        _scriptMakerState.value = _scriptMakerState.value.copy(generatedScript = full)
    }

    fun saveCurrentScript() {
        viewModelScope.launch {
            val state = _scriptMakerState.value
            val title = state.selectedTemplate?.title ?: "سناریوی ریلز"
            repository.saveScript(title, state.generatedScript)
            _toastMessage.emit("سناریو با موفقیت در بخش «سناریوهای من» ذخیره شد ✓")
        }
    }

    fun deleteSavedScript(id: Int) {
        viewModelScope.launch {
            repository.deleteSavedScript(id)
            _toastMessage.emit("سناریو حذف شد")
        }
    }

    // Planner functions
    fun togglePlannerDay(dayNumber: Int) {
        viewModelScope.launch {
            val current = plannerProgress.value[dayNumber] ?: false
            repository.togglePlannerDay(dayNumber, current)
            _toastMessage.emit(if (!current) "روز $dayNumber با موفقیت تکمیل شد! آفرین 🎉" else "وضعیت روز $dayNumber ریست شد")
        }
    }

    // Calculator functions
    fun updateCalculatorFields(followers: String, likes: String, comments: String, saves: String, shares: String) {
        _calculatorState.value = _calculatorState.value.copy(
            followers = followers.filter { it.isDigit() },
            likes = likes.filter { it.isDigit() },
            comments = comments.filter { it.isDigit() },
            saves = saves.filter { it.isDigit() },
            shares = shares.filter { it.isDigit() }
        )
        calculateEngagement()
    }

    fun calculateEngagement() {
        val s = _calculatorState.value
        val followers = s.followers.toLongOrNull() ?: 0L
        val likes = s.likes.toLongOrNull() ?: 0L
        val comments = s.comments.toLongOrNull() ?: 0L
        val saves = s.saves.toLongOrNull() ?: 0L
        val shares = s.shares.toLongOrNull() ?: 0L

        val result = repository.calculateEngagement(followers, likes, comments, saves, shares)
        _calculatorState.value = _calculatorState.value.copy(result = result)
    }

    // VIP Store & Checkout
    fun openCheckout(title: String, price: String, days: Int) {
        _selectedCheckoutPlan.value = CheckoutPlan(title, price, days)
    }

    fun dismissCheckout() {
        _selectedCheckoutPlan.value = null
    }

    fun confirmPurchase() {
        val plan = _selectedCheckoutPlan.value ?: return
        viewModelScope.launch {
            repository.activateVip(plan.title, plan.days)
            _selectedCheckoutPlan.value = null
            _toastMessage.emit("اشتراک ${plan.title} با موفقیت فعال شد! به خانواده VIP خوش آمدید 👑")
        }
    }

    // Settings actions
    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            repository.updateSettings(appSettings.value.copy(isDarkMode = isDark))
            _toastMessage.emit(if (isDark) "حالت تاریک فعال شد 🌙" else "حالت روشن فعال شد ☀️")
        }
    }

    fun setFontScale(scale: Float) {
        viewModelScope.launch {
            repository.updateSettings(appSettings.value.copy(fontScale = scale))
            _toastMessage.emit("اندازه قلم تنظیم شد")
        }
    }

    fun toggleAdsStatus(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings(appSettings.value.copy(isRealAdsEnabled = enabled))
            _toastMessage.emit(if (enabled) "تبلیغات فعال شد 📢" else "تبلیغات غیرفعال شد")
        }
    }

    fun saveCustomApiKey(key: String) {
        viewModelScope.launch {
            repository.updateSettings(appSettings.value.copy(customApiKey = key.trim()))
            _toastMessage.emit("کلید هوش مصنوعی با موفقیت ذخیره شد ✓")
        }
    }

    // AI Assistant actions
    fun setAiSubTab(tab: Int) {
        _aiState.value = _aiState.value.copy(activeSubTab = tab)
    }

    fun updateChatInput(text: String) {
        _aiState.value = _aiState.value.copy(chatInput = text)
    }

    fun clearChatHistory() {
        _aiState.value = _aiState.value.copy(
            chatMessages = listOf(
                ChatMessage(
                    text = "سلام دوست من! 👋 چت جدید بازنشانی شد. هر سوالی درباره الگوریتم اکسپلور، رشد پیج، ایده‌های وایرال یا سناریوی ریلز داری در خدمتم! 👇",
                    isUser = false
                )
            )
        )
    }

    fun sendChatMessage(promptText: String = "") {
        val messageToSend = promptText.ifBlank { _aiState.value.chatInput }.trim()
        if (messageToSend.isBlank()) return

        val userMessage = ChatMessage(text = messageToSend, isUser = true)
        val currentMessages = _aiState.value.chatMessages.toMutableList().apply { add(userMessage) }

        _aiState.value = _aiState.value.copy(
            chatMessages = currentMessages,
            chatInput = "",
            isChatTyping = true
        )

        viewModelScope.launch {
            val customKey = appSettings.value.customApiKey
            var reply = ""

            if (customKey.isNotBlank()) {
                val systemPrompt = """
                    تو یک دستیار هوشمند، حرفه‌ای و دلسوز برای تولیدکنندگان محتوای ریلز اینستاگرام و یوتیوب شورتز هستی.
                    به زبان فارسی روان، جذاب، با انرژی مثبت، ساختاریافته همراه با ایموجی و بولت‌پوینت‌های کاربردی پاسخ بده.
                    سوال کاربر: $messageToSend
                """.trimIndent()

                val geminiResult = GeminiApiService.generateContent(
                    prompt = systemPrompt,
                    customApiKey = customKey
                )
                geminiResult.fold(
                    onSuccess = { reply = it },
                    onFailure = {
                        // Fallback to local expert engine if network/key error
                        reply = ReelsAiExpert.answerQuery(messageToSend)
                    }
                )
            } else {
                // Instant expert response using built-in engine
                delay(600) // Small natural response feel
                reply = ReelsAiExpert.answerQuery(messageToSend)
            }

            val assistantMessage = ChatMessage(text = reply, isUser = false)
            _aiState.value = _aiState.value.copy(
                chatMessages = _aiState.value.chatMessages + assistantMessage,
                isChatTyping = false
            )
        }
    }

    fun setAiMode(mode: String) {
        _aiState.value = _aiState.value.copy(selectedMode = mode)
    }

    fun updateAiInputs(topic: String, audience: String, tone: String) {
        _aiState.value = _aiState.value.copy(
            topicInput = topic,
            targetAudience = audience,
            tone = tone
        )
    }

    fun generateAiContent() {
        val state = _aiState.value
        val topic = state.topicInput.trim()
        if (topic.isBlank()) {
            viewModelScope.launch {
                _toastMessage.emit("لطفاً موضوع ریلز را وارد کنید")
            }
            return
        }

        _aiState.value = _aiState.value.copy(isGenerating = true, errorMessage = null)

        val prompt = when (state.selectedMode) {
            "HOOK_GENERATOR" -> """
                به عنوان متخصص ارشد الگوریتم ریلز اینستاگرام و یوتیوب شورتز، ۵ قلاب وایرال شوکه‌کننده و به شدت جذاب برای موضوع «$topic» بنویس.
                مخاطب هدف: ${state.targetAudience.ifBlank { "عموم مخاطبان علاقه‌مند" }}
                لحن: ${state.tone}
                فرمت پاسخ:
                شماره‌گذاری شده همراه با توضیح روانشناسی کوتاه برای هر قلاب در ۱ جمله و یک ایده بصری ۳ ثانیه اول. کاملا کاربردی و آماده استفاده به زبان فارسی روان.
            """.trimIndent()

            "SCRIPT_WRITER" -> """
                یک سناریوی کامل، ساختاریافته و تضمینی ریلز اینستاگرام (زیر ۶۰ ثانیه) برای موضوع «$topic» بنویس.
                مخاطب هدف: ${state.targetAudience.ifBlank { "مخاطبان اینستاگرام" }}
                لحن: ${state.tone}
                حتما این سه بخش را با تیتر مشخص کن:
                ۱. [قلاب ۳ ثانیه اول (Hook)]: شوکه‌کننده و جلوگیری‌کننده از اسکرول
                ۲. [بدنه سناریو (Body)]: ۳ نکته طلایی و ریتم سریع با تصویرسازی
                ۳. [کال تو اکشن انفجاری (CTA)]: ترغیب به کامنت گذاشتن یا سیو
            """.trimIndent()

            "HASHTAG_FINDER" -> """
                به عنوان الگوریتم اکسپلور اینستاگرام، بهترین و بهینه‌ترین لیست هشتگ‌های پربازدید و هدفمند برای موضوع «$topic» را بنویس.
                دسته‌بندی کن:
                - هشتگ‌های میلیونی و پرطرفدار
                - هشتگ‌های تخصصی و نیچ
                - ۳ جمله کپشن آماده برای زیر ریلز
            """.trimIndent()

            else -> """
                برای ریلز اینستاگرام با موضوع «$topic»، یک کپشن حرفه‌ای و درگیرکننده با قلاب متنی در خط اول، متن جذاب و ۳ فراخوان تعاملی بنویس.
            """.trimIndent()
        }

        viewModelScope.launch {
            val customKey = appSettings.value.customApiKey
            if (customKey.isNotBlank()) {
                val result = GeminiApiService.generateContent(
                    prompt = prompt,
                    customApiKey = customKey
                )

                result.fold(
                    onSuccess = { responseText ->
                        _aiState.value = _aiState.value.copy(
                            isGenerating = false,
                            aiResult = responseText,
                            errorMessage = null
                        )
                        _toastMessage.emit("محتوای هوش مصنوعی با موفقیت تولید شد ✨")
                    },
                    onFailure = {
                        // Resilient fallback to offline engine
                        val fallbackContent = when (state.selectedMode) {
                            "HOOK_GENERATOR" -> ReelsAiExpert.generateHooksForTopic(topic, state.targetAudience, state.tone)
                            "SCRIPT_WRITER" -> ReelsAiExpert.generateScriptForTopic(topic, state.targetAudience, state.tone)
                            "HASHTAG_FINDER" -> ReelsAiExpert.generateHashtagsForTopic(topic)
                            else -> ReelsAiExpert.generateCaptionForTopic(topic, state.tone)
                        }
                        _aiState.value = _aiState.value.copy(
                            isGenerating = false,
                            aiResult = fallbackContent,
                            errorMessage = null
                        )
                        _toastMessage.emit("محتوای هوش مصنوعی آماده شد ✨")
                    }
                )
            } else {
                delay(500)
                val generated = when (state.selectedMode) {
                    "HOOK_GENERATOR" -> ReelsAiExpert.generateHooksForTopic(topic, state.targetAudience, state.tone)
                    "SCRIPT_WRITER" -> ReelsAiExpert.generateScriptForTopic(topic, state.targetAudience, state.tone)
                    "HASHTAG_FINDER" -> ReelsAiExpert.generateHashtagsForTopic(topic)
                    else -> ReelsAiExpert.generateCaptionForTopic(topic, state.tone)
                }
                _aiState.value = _aiState.value.copy(
                    isGenerating = false,
                    aiResult = generated,
                    errorMessage = null
                )
                _toastMessage.emit("محتوا توسط موتور هوشمند تولید شد ✨")
            }
        }
    }

    // Quick AI action: Enhance current script in Script Maker
    fun enhanceCurrentScriptWithAi() {
        val currentScript = _scriptMakerState.value.generatedScript
        if (currentScript.isBlank()) {
            viewModelScope.launch { _toastMessage.emit("ابتدا یک سناریو انتخاب یا ایجاد کنید") }
            return
        }

        viewModelScope.launch {
            _toastMessage.emit("درحال بهینه‌سازی سناریو با هوش مصنوعی...")
            val prompt = """
                سناریوی ریلز زیر را بررسی کن و آن را به سطح یک ویدیوی وایرال میلیونی اینستاگرام ارتقا بده.
                کلمات را کوبنده‌تر کن، ریتم را تندتر کن و یک قلاب به شدت جذاب‌تر جایگزین کن:
                $currentScript
            """.trimIndent()

            val result = GeminiApiService.generateContent(
                prompt = prompt,
                customApiKey = appSettings.value.customApiKey
            )

            result.fold(
                onSuccess = { enhanced ->
                    _scriptMakerState.value = _scriptMakerState.value.copy(generatedScript = enhanced)
                    _toastMessage.emit("سناریو با موفقیت توسط هوش مصنوعی تقویت شد ✨")
                },
                onFailure = { err ->
                    _toastMessage.emit("خطا در هوش مصنوعی: ${err.message}")
                }
            )
        }
    }

    // Quick AI action: Generate viral hook on demand for current topic
    fun generateAiHookForScript() {
        val topic = _scriptMakerState.value.customTopic.ifBlank { "ترفندهای رشد اینستاگرام" }
        viewModelScope.launch {
            _toastMessage.emit("درحال نوشتن قلاب هوشمند...")
            val prompt = "برای ریلز با موضوع «$topic»، یک قلاب ۳ ثانیه‌ای شوکه‌کننده و به شدت جذاب به زبان فارسی بنویس که مانع اسکرول شود. فقط متن قلاب را در ۱ یا ۲ خط بنویس بدون حاشیه."
            val result = GeminiApiService.generateContent(
                prompt = prompt,
                customApiKey = appSettings.value.customApiKey
            )
            result.fold(
                onSuccess = { hookText ->
                    val cleanHook = hookText.trim().removeSurrounding("\"")
                    val state = _scriptMakerState.value
                    updateCustomFields(
                        topic = state.customTopic,
                        benefit = state.customBenefit,
                        obstacle = cleanHook,
                        cta = state.customCta
                    )
                    _toastMessage.emit("قلاب هوش مصنوعی اعمال شد 🚀")
                },
                onFailure = { err ->
                    _toastMessage.emit("خطا در ارتباط با هوش مصنوعی: ${err.message}")
                }
            )
        }
    }

    // Cover & Thumbnail Studio Methods
    fun selectCoverTemplate(template: CoverTemplate) {
        _coverStudioState.value = _coverStudioState.value.copy(
            selectedTemplateId = template.id,
            headlineText = template.defaultMainHeadline,
            subHeadlineText = template.defaultSubHeadline,
            badgeText = template.badgeText,
            selectedAccentColor = template.accentColorHex,
            selectedTextColor = template.textColorHex,
            customImageUri = null
        )
    }

    fun selectCoverCategory(category: CoverCategory) {
        _coverStudioState.value = _coverStudioState.value.copy(selectedCategory = category)
    }

    fun updateCoverTexts(headline: String, subHeadline: String, badge: String) {
        _coverStudioState.value = _coverStudioState.value.copy(
            headlineText = headline,
            subHeadlineText = subHeadline,
            badgeText = badge
        )
    }

    fun setCoverCustomImage(uriString: String?) {
        _coverStudioState.value = _coverStudioState.value.copy(customImageUri = uriString)
    }

    fun setCoverColors(accentColor: Long, textColor: Long) {
        _coverStudioState.value = _coverStudioState.value.copy(
            selectedAccentColor = accentColor,
            selectedTextColor = textColor
        )
    }

    fun toggleShortsBadge(isShorts: Boolean) {
        _coverStudioState.value = _coverStudioState.value.copy(isYoutubeShortsBadge = isShorts)
    }

    fun generateAiCoverHeadline(topic: String) {
        val promptTopic = topic.ifBlank { "ایده‌های وایرال تولید محتوا" }
        viewModelScope.launch {
            _coverStudioState.value = _coverStudioState.value.copy(isAiGeneratingTitle = true)
            _toastMessage.emit("درحال ایده پردازی تیتر جذاب با هوش مصنوعی...")
            val prompt = "برای کاور و تامبنیل ریلز/شورتز با موضوع «$promptTopic»، دو تیتر فارسی بسیار جذاب بنویس. خط اول: یک تیتر کوتاه و کنجکاوکننده اصلی (حداکثر ۶ کلمه). خط دوم: یک زیرتیتر مکمل ترغیب‌کننده. فقط این دو خط را با فرمت:\nتیتر اصلی\nزیرتیتر بنویس بدون هیچ متن اضافی."
            val result = GeminiApiService.generateContent(
                prompt = prompt,
                customApiKey = appSettings.value.customApiKey
            )
            _coverStudioState.value = _coverStudioState.value.copy(isAiGeneratingTitle = false)
            result.fold(
                onSuccess = { text ->
                    val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
                    val head = lines.getOrNull(0)?.removePrefix("-")?.trim() ?: "ترفند انفجاری بازدید"
                    val sub = lines.getOrNull(1)?.removePrefix("-")?.trim() ?: "قبل از انتشار ویدیوی بعدی حتما ببینید!"
                    _coverStudioState.value = _coverStudioState.value.copy(
                        headlineText = head,
                        subHeadlineText = sub
                    )
                    _toastMessage.emit("تیترهای هوش مصنوعی در کاور جای‌گذاری شدند! 🎨")
                },
                onFailure = { err ->
                    _toastMessage.emit("خطا در ایده هوش مصنوعی: ${err.message}")
                }
            )
        }
    }
}
