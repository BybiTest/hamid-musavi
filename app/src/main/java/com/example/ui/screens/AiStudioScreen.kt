package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.ReelsViewModel

@Composable
fun AiStudioScreen(viewModel: ReelsViewModel) {
    val studioState by viewModel.aiStudioState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // عنوان صفحه
        Text(
            text = "انتخاب وظیفه هوش مصنوعی",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // انتخاب حالت
        ModeSelector(
            selectedMode = studioState.selectedMode,
            onModeSelected = { viewModel.setAiStudioMode(it) }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // فیلدهای ورودی
        InputFields(
            topic = studioState.topicInput,
            audience = studioState.targetAudience,
            tone = studioState.selectedTone,
            onTopicChange = { viewModel.updateAiStudioInputs(topic = it) },
            onAudienceChange = { viewModel.updateAiStudioInputs(audience = it) },
            onToneChange = { viewModel.updateAiStudioInputs(tone = it) }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // دکمه تولید
        GenerateButton(
            isGenerating = studioState.isGenerating,
            onGenerate = { viewModel.generateAiStudioContent() }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // نمایش نتیجه
        AnimatedVisibility(
            visible = studioState.showResult,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ResultDisplay(
                result = studioState.aiResult,
                mode = studioState.selectedMode,
                errorMessage = studioState.errorMessage,
                onCopy = {
                    copyToClipboard(context, studioState.aiResult)
                },
                onClear = { viewModel.clearAiStudioResult() }
            )
        }
    }
}

@Composable
private fun ModeSelector(selectedMode: String, onModeSelected: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        ModeChip(
            text = "⚡ قلاب‌های وایرال",
            isSelected = selectedMode == "HOOK_GENERATOR",
            onClick = { onModeSelected("HOOK_GENERATOR") }
        )
        ModeChip(
            text = " سناریونویسی کامل",
            isSelected = selectedMode == "SCRIPT_WRITER",
            onClick = { onModeSelected("SCRIPT_WRITER") }
        )
        ModeChip(
            text = "#️⃣ هشتگ و کپشن",
            isSelected = selectedMode == "HASHTAG_FINDER",
            onClick = { onModeSelected("HASHTAG_FINDER") }
        )
    }
}

@Composable
private fun ModeChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) {
        Brush.linearGradient(colors = listOf(Color(0xFFFF6B35), Color(0xFFF7931E)))
    } else {
        Brush.linearGradient(colors = listOf(Color.LightGray, Color.LightGray))
    }
    
    val textColor = if (isSelected) Color.White else Color.DarkGray
    
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = if (isSelected) 8.dp else 2.dp
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun InputFields(
    topic: String,
    audience: String,
    tone: String,
    onTopicChange: (String) -> Unit,
    onAudienceChange: (String) -> Unit,
    onToneChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // موضوع اصلی
        Text(
            text = "موضوع اصلی ریلز یا ویدیو:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        OutlinedTextField(
            value = topic,
            onValueChange = onTopicChange,
            placeholder = { Text("مثلاً: نحوه لاغری بدون رژیم، آموزش فتوشاپ، ترفندهای آیفون") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        
        // مخاطبان هدف
        Text(
            text = "مخاطبان هدف (اختیاری):",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        OutlinedTextField(
            value = audience,
            onValueChange = onAudienceChange,
            placeholder = { Text("مثلاً: کنکوری‌ها، خانم‌های خانه‌دار، کارآفرینان") },
            modifier = Modifier.fillMaxWidth()
        )
        
        // لحن بیان
        Text(
            text = "لحن بیان:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ToneChip(
                text = "هیجانی و شوکه‌کننده",
                isSelected = tone == "هیجانی و شوکه‌کننده",
                onClick = { onToneChange("هیجانی و شوکه‌کننده") }
            )
            ToneChip(
                text = "آموزشی و معتبر",
                isSelected = tone == "آموزشی و معتبر",
                onClick = { onToneChange("آموزشی و معتبر") }
            )
            ToneChip(
                text = "طنز و کنایه‌آمیز",
                isSelected = tone == "طنز و کنایه‌آمیز",
                onClick = { onToneChange("طنز و کنایه‌آمیز") }
            )
        }
    }
}

@Composable
private fun ToneChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) {
        Brush.linearGradient(colors = listOf(Color(0xFF9C27B0), Color(0xFFE040FB)))
    } else {
        Brush.linearGradient(colors = listOf(Color.LightGray, Color.LightGray))
    }
    
    val textColor = if (isSelected) Color.White else Color.DarkGray
    
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun GenerateButton(isGenerating: Boolean, onGenerate: () -> Unit) {
    Button(
        onClick = onGenerate,
        enabled = !isGenerating,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF6B35)
        )
    ) {
        if (isGenerating) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "در حال تولید...",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        } else {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ساخت با هوش مصنوعی ✨",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ResultDisplay(
    result: String,
    mode: String,
    errorMessage: String?,
    onCopy: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2D2D2D))
            .padding(16.dp)
    ) {
        // هدر نتیجه
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (mode) {
                    "HOOK_GENERATOR" -> "🔥 قلاب‌های تولید شده"
                    "SCRIPT_WRITER" -> "📝 سناریوی کامل"
                    "HASHTAG_FINDER" -> "#️ هشتگ و کپشن"
                    else -> "نتیجه"
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onCopy) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "کپی",
                        tint = Color.White
                    )
                }
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "بستن",
                        tint = Color.White
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // متن نتیجه
        Text(
            text = result,
            fontSize = 16.sp,
            color = Color.White,
            lineHeight = 24.sp
        )
        
        // پیام خطا
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "⚠️ $errorMessage",
                fontSize = 14.sp,
                color = Color(0xFFFF6B35)
            )
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("AI Result", text)
    clipboard.setPrimaryClip(clip)
}
