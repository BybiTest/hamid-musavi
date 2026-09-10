package com.example.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.data.remote.GeminiApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// =========================================================
// AI Studio - Auto Content Generation (Extension)
// =========================================================

data class AiStudioState(
    val selectedMode: String = "HOOK_GENERATOR",
    val topicInput: String = "",
    val targetAudience: String = "",
    val selectedTone: String = "هیجانی و شوکه‌کننده",
    val isGenerating: Boolean = false,
    val aiResult: String = "",
    val errorMessage: String? = null,
    val showResult: Boolean = false,
    val generationHistory: List<GenerationHistoryItem> = emptyList()
)

data class GenerationHistoryItem(
    val id: Int = 0,
    val mode: String,
    val topic: String,
    val timestamp: Long = System.currentTimeMillis(),
    val result: String
)

// این بخش را به کلاس ReelsViewModel اضافه کنید:
/*

private val _aiStudioState = MutableStateFlow(AiStudioState())
val aiStudioState: StateFlow<AiStudioState> = _aiStudioState.asStateFlow()

fun setAiStudioMode(mode: String) {
    _aiStudioState.value = _aiStudioState.value.copy(selectedMode = mode)
}

fun updateAiStudioInputs(
    topic: String? = null,
    audience: String? = null,
    tone: String? = null
) {
    val current = _aiStudioState.value
    _aiStudioState.value = current.copy(
        topicInput = topic ?: current.topicInput,
        targetAudience = audience ?: current.targetAudience,
        selectedTone = tone ?: current.selectedTone
    )
}

fun generateAiStudioContent() {
    val state = _aiStudioState.value
    
    if (state.topicInput.isBlank()) {
        viewModelScope.launch {
            _toastMessage.emit("لطفاً موضوع اصلی ریلز را وارد کنید")
        }
        return
    }
    
    _aiStudioState.value = state.copy(
        isGenerating = true,
        errorMessage = null,
        showResult = false
    )
    
    val prompt = when (state.selectedMode) {
        "HOOK_GENERATOR" -> """
            برای موضوع «${state.topicInput}» دقیقاً ۵ قلاب جذاب برای سه ثانیه اول ویدیو بنویس.
            مخاطب هدف: ${state.targetAudience.ifBlank { "عموم کاربران" }}
            لحن: ${state.selectedTone}
            برای هر قلاب دلیل جذابیت و ایده اجرای تصویری بده.
            پاسخ فارسی باشد.
        """.trimIndent()
        
        "SCRIPT_WRITER" -> """
            یک سناریوی کامل و زیر ۴۵ ثانیه برای موضوع «${state.topicInput}» بنویس.
            شامل:
            . قلاب (۰-۳ ثانیه)
            ۲. بدنه (۳-۳۵ ثانیه) - سه نکته کلیدی
            ۳. CTA (۳۵-۴۵ ثانیه)
            مخاطب هدف: ${state.targetAudience.ifBlank { "عموم کاربران" }}
            لحن: ${state.selectedTone}
            پاسخ فارسی باشد.
        """.trimIndent()
        
        "HASHTAG_FINDER" -> """
            برای موضوع «${state.topicInput}» یک کپشن جذاب و هشتگ‌های مرتبط بنویس.
            مخاطب هدف: ${state.targetAudience.ifBlank { "عموم کاربران" }}
            لحن: ${state.selectedTone}
            کپشن باید کوتاه، طبیعی و تعامل‌محور باشد.
            ۵-۸ هشتگ مرتبط پیشنهاد بده.
            پاسخ فارسی باشد.
        """.trimIndent()
        
        else -> ""
    }
    
    viewModelScope.launch {
        try {
            val key = appSettings.value.customApiKey
            if (key.isNotBlank()) {
                val result = GeminiApiService.generateContent(
                    prompt = prompt,
                    customApiKey = key
                )
                
                result.fold(
                    onSuccess = { response ->
                        val historyItem = GenerationHistoryItem(
                            id = System.currentTimeMillis().toInt(),
                            mode = state.selectedMode,
                            topic = state.topicInput,
                            result = response
                        )
                        
                        _aiStudioState.value = _aiStudioState.value.copy(
                            isGenerating = false,
                            aiResult = response,
                            showResult = true,
                            generationHistory = _aiStudioState.value.generationHistory + historyItem
                        )
                        
                        _toastMessage.emit("محتوا با موفقیت تولید شد ✨")
                    },
                    onFailure = { error ->
                        val fallback = generateLocalStudioContent(
                            state.selectedMode,
                            state.topicInput,
                            state.targetAudience,
                            state.selectedTone
                        )
                        
                        _aiStudioState.value = _aiStudioState.value.copy(
                            isGenerating = false,
                            aiResult = fallback,
                            showResult = true,
                            errorMessage = error.message
                        )
                    }
                )
            } else {
                delay(500)
                val fallback = generateLocalStudioContent(
                    state.selectedMode,
                    state.topicInput,
                    state.targetAudience,
                    state.selectedTone
                )
                
                _aiStudioState.value = _aiStudioState.value.copy(
                    isGenerating = false,
                    aiResult = fallback,
                    showResult = true
                )
            }
        } catch (e: Exception) {
            _aiStudioState.value = _aiStudioState.value.copy(
                isGenerating = false,
                errorMessage = e.message
            )
        }
    }
}

private fun generateLocalStudioContent(
    mode: String,
    topic: String,
    audience: String,
    tone: String
): String {
    return when (mode) {
        "HOOK_GENERATOR" -> """
            🔥 ۵ قلاب پیشنهادی برای «$topic»:
            
            ۱. «باور نمی‌کنی درباره $topic این نکته وجود داشته باشه!»
            ۲. «اگر درباره $topic این اشتباه رو می‌کنی، همین الان ببین.»
            ۳. «قبل از اینکه سراغ $topic بری، اینو بدون.»
            . «فقط ۳۰ ثانیه وقت بذار تا نکته مهم $topic رو بفهمی.»
            ۵. «بیشتر مردم درباره $topic این قسمت رو نمی‌دونن.»
            
            💡 نکته: لحن $tone را در اجرا حفظ کن.
        """.trimIndent()
        
        "SCRIPT_WRITER" -> """
            📝 سناریوی کامل برای «$topic»:
            
            ⏱️ قلاب (۰-۳ ثانیه):
            «بیشتر مردم درباره $topic یک اشتباه مهم انجام می‌دهند.»
            
             بدنه (۳-۳۵ ثانیه):
            • نکته اول: مشکل را مشخص کن
            • نکته دوم: راه‌حل را ساده توضیح بده
            • نکته سوم: نتیجه را سریع نشان بده
            
            🎬 CTA (۳۵-۴۵ ثانیه):
            «اگر این نکته برات مفید بود ذخیره‌اش کن.»
            
            🎯 مخاطب: ${audience.ifBlank { "عموم کاربران" }}
        """.trimIndent()
        
        "HASHTAG_FINDER" -> """
            #️⃣ کپشن و هشتگ برای «$topic»:
            
            📝 کپشن:
            اگر درباره $topic کنجکاوی، این ویدیو رو تا آخر ببین 👀
            
            #️⃣ هشتگ‌ها:
            #$topic #تولید\_محتوا #ریلز #اینستاگرام #وایرال #${audience.ifBlank { "آموزش" }}
        """.trimIndent()
        
        else -> "لطفاً یک حالت انتخاب کنید."
    }
}

fun clearAiStudioResult() {
    _aiStudioState.value = _aiStudioState.value.copy(
        showResult = false,
        aiResult = "",
        errorMessage = null
    )
}

fun resetAiStudioForm() {
    _aiStudioState.value = AiStudioState()
}

*/
