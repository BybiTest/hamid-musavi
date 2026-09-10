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

data class HooksUiState(
    val allHooks: List<HookItem> = emptyList(),
    val selectedCategory: HookCategory? = null,
    val searchQuery: String = "",
    val showOnlyFavorites: Boolean = false,
    val favoriteHookIds: Set<Int> = emptySet(),
    val unlockedHookIds: Set<Int> = emptySet(),
    val isVipActive: Boolean = false
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

data class SettingsState(
    val isDarkMode: Boolean = false,
    val fontScale: Float = 1.0f
)

class ReelsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReelsRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = ReelsRepository(db.reelsDao())
    }

    private val _currentTab = MutableStateFlow(AppTab.HOOKS)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    val vipState: StateFlow<VipStateEntity> = repository.vipState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        VipStateEntity()
    )

    val savedScripts: StateFlow<List<SavedScriptEntity>> = repository.allSavedScripts.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val plannerProgress: StateFlow<Map<Int, Boolean>> = repository.plannerProgress.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyMap()
    )

    private val _selectedCategory = MutableStateFlow<HookCategory?>(null)
    val selectedCategory: StateFlow<HookCategory?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showOnlyFavorites = MutableStateFlow(false)
    val showOnlyFavorites: StateFlow<Boolean> = _showOnlyFavorites.asStateFlow()

    private val _favoriteHookIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteHookIds: StateFlow<Set<Int>> = _favoriteHookIds.asStateFlow()

    private val _unlockedHookIds = MutableStateFlow<Set<Int>>(emptySet())
    val unlockedHookIds: StateFlow<Set<Int>> = _unlockedHookIds.asStateFlow()

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()

    val allHooks: List<HookItem> = repository.getAllHooks()
    val allTemplates: List<ScriptTemplate> = repository.getAllTemplates()
    val allPlannerDays: List<ContentDayPlan> = repository.getAllPlannerDays()

    private val _scriptMakerState = MutableStateFlow(
        ScriptMakerUiState(
            templates = allTemplates,
            selectedTemplate = allTemplates.firstOrNull()
        )
    )
    val scriptMakerState: StateFlow<ScriptMakerUiState> = _scriptMakerState.asStateFlow()

    private val _calculatorState = MutableStateFlow(CalculatorUiState())
    val calculatorState: StateFlow<CalculatorUiState> = _calculatorState.asStateFlow()

    private val _rewardedAdForHook = MutableStateFlow<HookItem?>(null)
    val rewardedAdForHook: StateFlow<HookItem?> = _rewardedAdForHook.asStateFlow()

    private val _isAdWatching = MutableStateFlow(false)
    val isAdWatching: StateFlow<Boolean> = _isAdWatching.asStateFlow()

    private val _adCountdown = MutableStateFlow(5)
    val adCountdown: StateFlow<Int> = _adCountdown.asStateFlow()

    data class CheckoutPlan(val title: String, val price: String, val days: Int)
    private val _selectedCheckoutPlan = MutableStateFlow<CheckoutPlan?>(null)
    val selectedCheckoutPlan: StateFlow<CheckoutPlan?> = _selectedCheckoutPlan.asStateFlow()

    init {
        allTemplates.firstOrNull()?.let { selectTemplate(it) }
        calculateEngagement()
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setCategory(category: HookCategory?) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavoritesFilter() {
        _showOnlyFavorites.value = !_showOnlyFavorites.value
    }

    fun toggleFavoriteHook(hookId: Int) {
        viewModelScope.launch {
            val current = _favoriteHookIds.value.toMutableSet()
            if (current.contains(hookId)) {
                current.remove(hookId)
            } else {
                current.add(hookId)
            }
            _favoriteHookIds.value = current
            _toastMessage.emit(if (current.contains(hookId)) "به علاقه‌مندی‌ها اضافه شد ★" else "از علاقه‌مندی‌ها حذف شد")
        }
    }

    fun isHookUnlocked(hook: HookItem): Boolean {
        if (!hook.isVipOnly) return true
        if (vipState.value.isVipActive) return true
        return _unlockedHookIds.value.contains(hook.id.hashCode()) 
    }

    fun requestUnlockHook(hook: HookItem) {
        _rewardedAdForHook.value = hook
    }

    fun requestUnlockHook(viralHook: ViralHook) {
        _rewardedAdForHook.value = allHooks.find { it.id.hashCode() == viralHook.id }
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
            val currentUnlocked = _unlockedHookIds.value.toMutableSet()
            currentUnlocked.add(hook.id.hashCode())
            _unlockedHookIds.value = currentUnlocked
            
            _isAdWatching.value = false
            _rewardedAdForHook.value = null
            _toastMessage.emit("تبریک! قلاب وایرال برای شما باز شد 🎁")
        }
    }

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
        val body = template.bodyTemplate
            .replace("[علت تفاوت کیفیت یا تامین مستقیم]", state.customBenefit.ifBlank { "کیفیت بالا" })
        val cta = state.customCta.ifBlank { template.ctaTemplate }
        val full = "🎬 [قلاب]:\n$hook\n\n📌 [بدنه]:\n$body\n\n🚀 [CTA]:\n$cta"

        _scriptMakerState.value = _scriptMakerState.value.copy(generatedScript = full)
    }

    fun saveCurrentScript() {
        viewModelScope.launch {
            val state = _scriptMakerState.value
            val title = state.selectedTemplate?.title ?: "سناریوی ریلز"
            repository.saveScript(title, state.generatedScript)
            _toastMessage.emit("سناریو ذخیره شد ✓")
        }
    }

    fun deleteSavedScript(id: Int) {
        viewModelScope.launch {
            repository.deleteSavedScript(id)
            _toastMessage.emit("سناریو حذف شد")
        }
    }

    fun togglePlannerDay(dayNumber: Int) {
        viewModelScope.launch {
            val current = plannerProgress.value[dayNumber] ?: false
            repository.togglePlannerDay(dayNumber, current)
            _toastMessage.emit(if (!current) "روز $dayNumber تکمیل شد! 🎉" else "وضعیت روز $dayNumber ریست شد")
        }
    }

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
            _toastMessage.emit("اشتراک ${plan.title} فعال شد! 👑")
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _settingsState.value = _settingsState.value.copy(isDarkMode = enabled)
    }

    fun setFontScale(scale: Float) {
        _settingsState.value = _settingsState.value.copy(fontScale = scale)
    }
}
