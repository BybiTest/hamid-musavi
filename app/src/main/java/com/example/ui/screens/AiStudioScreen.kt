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
        Text(
            text = "انتخاب وظیفه هوش مصنوعی",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ModeChip(
                text = "قلاب‌های وایرال",
                isSelected = studioState.selectedMode == "HOOK_GENERATOR",
                onClick = { viewModel.setAiStudioMode("HOOK_GENERATOR") }
            )
            ModeChip(
                text = "سناریونویسی کامل",
                isSelected = studioState.selectedMode == "SCRIPT_WRITER",
                onClick = { viewModel.setAiStudioMode("SCRIPT_WRITER") }
            )
            ModeChip(
                text = "هشتگ و کپشن",
                isSelected = studioState.selectedMode == "HASHTAG_FINDER",
                onClick = { viewModel.setAiStudioMode("HASHTAG_FINDER") }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "موضوع اصلی ریلز یا ویدیو:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        OutlinedTextField(
            value = studioState.topicInput,
            onValueChange = { viewModel.updateAiStudioInputs(topic = it) },
            placeholder = { Text("مثلاً: نحوه لاغری بدون رژیم، آموزش فتوشاپ، ترفندهای آیفون") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "مخاطبان هدف (اختیاری):",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        OutlinedTextField(
            value = studioState.targetAudience,
            onValueChange = { viewModel.updateAiStudioInputs(audience = it) },
            placeholder = { Text("مثلاً: کنکوری‌ها، خانم‌های خانه‌دار، کارآفرینان") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
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
                isSelected = studioState.selectedTone == "هیجانی و شوکه‌کننده",
                onClick = { viewModel.updateAiStudioInputs(tone = "هیجانی و شوکه‌کننده") }
            )
            ToneChip(
                text = "آموزشی و معتبر",
                isSelected = studioState.selectedTone == "آموزشی و معتبر",
                onClick = { viewModel.updateAiStudioInputs(tone = "آموزشی و معتبر") }
            )
            ToneChip(
                text = "طنز و کنایه‌آمیز",
                isSelected = studioState.selectedTone == "طنز و کنایه‌آمیز",
                onClick = { viewModel.updateAiStudioInputs(tone = "طنز و کنایه‌آمیز") }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { viewModel.generateAiStudioContent() },
            enabled = !studioState.isGenerating,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF6B35)
            )
        ) {
            if (studioState.isGenerating) {
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
        
        Spacer(modifier = Modifier.height(24.dp))
        
        AnimatedVisibility(
            visible = studioState.showResult,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2D2D2D))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (studioState.selectedMode) {
                            "HOOK_GENERATOR" -> "🔥 قلاب‌های تولید شده"
                            "SCRIPT_WRITER" -> " سناریوی کامل"
                            "HASHTAG_FINDER" -> "#️⃣ هشتگ و کپشن"
                            else -> "نتیجه"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("AI Result", studioState.aiResult)
                            clipboard.setPrimaryClip(clip)
                        }) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "کپی",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = { viewModel.clearAiStudioResult() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "بستن",
                                tint = Color.White
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = studioState.aiResult,
                    fontSize = 16.sp,
                    color = Color.White,
                    lineHeight = 24.sp
                )
                
                if (studioState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "⚠️ ${studioState.errorMessage}",
                        fontSize = 14.sp,
                        color = Color(0xFFFF6B35)
                    )
                }
            }
        }
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
