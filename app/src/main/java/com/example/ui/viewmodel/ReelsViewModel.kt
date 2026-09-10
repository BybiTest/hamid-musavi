package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.SavedScriptEntity
import com.example.data.local.VipStateEntity
import com.example.data.model.AppTab
import com.example.data.model.ContentDayPlan
import com.example.data.model.EngagementResult
import com.example.data.model.HookCategory
import com.example.data.model.HookItem
import com.example.data.model.ScriptTemplate
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
}
Copied!
