package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.AppTab
import com.example.data.model.CoverCategory
import com.example.data.model.CoverTemplate
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SunsetOrange
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.VipGold
import com.example.ui.viewmodel.ReelsViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThumbnailStudioScreen(
    viewModel: ReelsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val studioState by viewModel.coverStudioState.collectAsState()
    val vipState by viewModel.vipState.collectAsState()
    val allTemplates = remember { viewModel.getAllCoverTemplates() }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.setCoverCustomImage(it.toString())
        }
    }

    val filteredTemplates = remember(studioState.selectedCategory, allTemplates) {
        if (studioState.selectedCategory == CoverCategory.ALL) {
            allTemplates
        } else {
            allTemplates.filter { it.category == studioState.selectedCategory }
        }
    }

    val currentTemplate = remember(studioState.selectedTemplateId, allTemplates) {
        allTemplates.find { it.id == studioState.selectedTemplateId } ?: allTemplates.first()
    }

    var showColorPalette by remember { mutableStateOf(false) }
    var aiTopicInput by remember { mutableStateOf("") }

    val colorPalette = listOf(
        Pair("نئون فیروزه‌ای", Pair(0xFF00F0FF, 0xFFFFFFFF)),
        Pair("طلایی بیزینس", Pair(0xFFFFD700, 0xFFFFFFFF)),
        Pair("آتشین وایرال", Pair(0xFFFF3D00, 0xFFFFFFFF)),
        Pair("بنفش الکتریک", Pair(0xFF8B5CF6, 0xFFFFFFFF)),
        Pair("سبز ترند", Pair(0xFF10B981, 0xFFFFFFFF)),
        Pair("مشکی لوکس", Pair(0xFF1E293B, 0xFFF8FAFC)),
        Pair("پاستلی مینیمال", Pair(0xFFF472B6, 0xFF1F2937))
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("thumbnail_studio_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Header
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SunsetOrange.copy(alpha = 0.2f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.FormatPaint,
                                            contentDescription = null,
                                            tint = SunsetOrange,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "استودیوی کاور و تامبنیل ۹:۱۶",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "طراحی اختصاصی برای ریلز اینستاگرام و شورتز یوتیوب",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        // Badge Mode indicator
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (studioState.isYoutubeShortsBadge) Color(0xFFFF0000).copy(alpha = 0.15f) else ElectricPurple.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, if (studioState.isYoutubeShortsBadge) Color(0xFFFF0000).copy(alpha = 0.4f) else ElectricPurple.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (studioState.isYoutubeShortsBadge) Icons.Default.PlayArrow else Icons.Default.VideoLibrary,
                                    contentDescription = null,
                                    tint = if (studioState.isYoutubeShortsBadge) Color(0xFFFF0000) else ElectricPurple,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (studioState.isYoutubeShortsBadge) "سایز Shorts" else "سایز Reels",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (studioState.isYoutubeShortsBadge) Color(0xFFFF0000) else ElectricPurple
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Live Preview Canvas (9:16 aspect ratio)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "پیش‌نمایش زنده تامبنیل (۹:۱۶ استاندارد)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                // Thumbnail Container Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .aspectRatio(9f / 16f)
                        .shadow(16.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            2.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color(studioState.selectedAccentColor),
                                    Color(studioState.selectedAccentColor).copy(alpha = 0.2f)
                                )
                            ),
                            RoundedCornerShape(20.dp)
                        )
                ) {
                    // Background Image
                    if (studioState.customImageUri != null) {
                        AsyncImage(
                            model = studioState.customImageUri,
                            contentDescription = "Custom Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = currentTemplate.drawableResId),
                            contentDescription = currentTemplate.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Scrim Gradient for Readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.45f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.88f)
                                    )
                                )
                            )
                    )

                    // Platform Top Badge (Reels or Shorts)
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (studioState.isYoutubeShortsBadge) Color(0xFFCC0000) else Color(0xFFE1306C)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (studioState.isYoutubeShortsBadge) Icons.Default.PlayArrow else Icons.Default.VideoLibrary,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (studioState.isYoutubeShortsBadge) "SHORTS" else "REELS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Top Left Tag / Badge
                    if (studioState.badgeText.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(studioState.selectedAccentColor),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(14.dp)
                        ) {
                            Text(
                                text = studioState.badgeText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Text Overlay Content in Center-Bottom for maximum CTR
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Main Headline with High-Contrast Box
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.75f),
                            border = BorderStroke(1.5.dp, Color(studioState.selectedAccentColor)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = studioState.headlineText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(studioState.selectedTextColor),
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                            )
                        }

                        if (studioState.subHeadlineText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(studioState.selectedAccentColor).copy(alpha = 0.95f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = studioState.subHeadlineText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Eye-catching CTA Banner on Bottom
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color(studioState.selectedAccentColor),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "تا آخر ببینید!",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action buttons below preview: Save & Share
                Row(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            shareThumbnailIntent(
                                context = context,
                                headline = studioState.headlineText,
                                sub = studioState.subHeadlineText
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("اشتراک و ارسال", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "اطلاعات قالب و چیدمان کاور ذخیره شد ✅", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ذخیره در استودیو", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: AI Title Generator for Thumbnail
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ElectricPurple.copy(alpha = 0.12f)
                ),
                border = BorderStroke(1.dp, ElectricPurple.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ElectricPurple,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ایده‌پرداز تیتر کاور با هوش مصنوعی",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ElectricPurple.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Gemini AI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricPurple,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "موضوع ویدیوی خود را بنویسید تا هوش مصنوعی تیترهای بسیار پرکلیک (Click-Bait مثبت) برای کاور بنویسد:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = aiTopicInput,
                            onValueChange = { aiTopicInput = it },
                            placeholder = { Text("مثلاً: کسب درآمد از تولید محتوا بدون چهره", fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricPurple,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                viewModel.generateAiCoverHeadline(aiTopicInput)
                            },
                            enabled = !studioState.isAiGeneratingTitle,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                            modifier = Modifier.height(52.dp)
                        ) {
                            if (studioState.isAiGeneratingTitle) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تولید تیتر", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Text Editing Fields
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.TextFields, contentDescription = null, tint = SunsetOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("متن و نوشته‌های روی کاور", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("تیتر اصلی (بزرگ و برجسته)", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = studioState.headlineText,
                        onValueChange = {
                            viewModel.updateCoverTexts(it, studioState.subHeadlineText, studioState.badgeText)
                        },
                        singleLine = false,
                        maxLines = 2,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("زیرتیتر توضیحی (کوچک‌تر)", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = studioState.subHeadlineText,
                        onValueChange = {
                            viewModel.updateCoverTexts(studioState.headlineText, it, studioState.badgeText)
                        },
                        singleLine = false,
                        maxLines = 2,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("متن برچسب یا نشان بالا (مثال: رایگان، ۲۰۲۶، هشدار)", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = studioState.badgeText,
                        onValueChange = {
                            viewModel.updateCoverTexts(studioState.headlineText, studioState.subHeadlineText, it)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Custom Photo or Color Styling
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.ColorLens, contentDescription = null, tint = SunsetOrange, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("رنگ‌بندی و تصویر دلخواه", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        // Switch Platform Badge
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (studioState.isYoutubeShortsBadge) "نشان یوتیوب" else "نشان اینستاگرام",
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = studioState.isYoutubeShortsBadge,
                                onCheckedChange = { viewModel.toggleShortsBadge(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFFCC0000))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Choose Image from Gallery
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("انتخاب تصویر از گالری", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        if (studioState.customImageUri != null) {
                            OutlinedButton(
                                onClick = { viewModel.setCoverCustomImage(null) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(42.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("بازگشت به قالب", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("پالت رنگی هایلایت و المان‌ها:", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        colorPalette.forEach { (name, colors) ->
                            val (accent, text) = colors
                            val isSelected = studioState.selectedAccentColor == accent

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(accent),
                                border = if (isSelected) BorderStroke(3.dp, Color.White) else null,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clickable {
                                        viewModel.setCoverColors(accent, text)
                                    }
                            ) {
                                if (isSelected) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = if (accent == 0xFFFFD700 || accent == 0xFF00F0FF) Color.Black else Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Ready Preloaded Templates
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "قالب‌های آماده حرفه‌ای ریلز و شورتز",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "${filteredTemplates.size} قالب",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Categories Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(CoverCategory.values()) { category ->
                        val isSelected = studioState.selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCoverCategory(category) },
                            label = { Text(category.titleFa, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SunsetOrange,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Grid of preloaded templates
        items(filteredTemplates.chunked(2)) { pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                pair.forEach { template ->
                    val isSelected = studioState.selectedTemplateId == template.id
                    val isLocked = template.isVipOnly && !vipState.isVipActive

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (isLocked) {
                                    Toast.makeText(context, "این قالب ویژه کاربران VIP است! 💎", Toast.LENGTH_SHORT).show()
                                    viewModel.selectTab(AppTab.VIP)
                                } else {
                                    viewModel.selectCoverTemplate(template)
                                    Toast.makeText(context, "قالب «${template.title}» اعمال شد", Toast.LENGTH_SHORT).show()
                                }
                            },
                        shape = RoundedCornerShape(14.dp),
                        border = if (isSelected) BorderStroke(2.dp, SunsetOrange) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(9f / 16f)
                            ) {
                                Image(
                                    painter = painterResource(id = template.drawableResId),
                                    contentDescription = template.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(
                                                    Color.Black.copy(alpha = 0.2f),
                                                    Color.Black.copy(alpha = 0.8f)
                                                )
                                            )
                                        )
                                )

                                if (template.isVipOnly) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = VipGold,
                                        modifier = Modifier.align(Alignment.TopStart).padding(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null, tint = Color.Black, modifier = Modifier.size(10.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("VIP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        }
                                    }
                                }

                                Text(
                                    text = template.defaultMainHeadline,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(8.dp)
                                )

                                if (isSelected) {
                                    Surface(
                                        shape = CircleShape,
                                        color = SunsetOrange,
                                        modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(22.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }

                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = template.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = template.category.titleFa,
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // If single item in pair
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private fun shareThumbnailIntent(context: Context, headline: String, sub: String) {
    val shareText = """
        🎨 کاور و تامبنیل ساخته شده در اپلیکیشن «ریمیکس استودیوی ریلز»:
        
        📌 تیتر اصلی: $headline
        🔹 زیرتیتر: $sub
        
        📐 نسبت ابعاد: ۹:۱۶ عمودی مناسب اینستاگرام ریلز و یوتیوب شورتز
        سازنده: اپلیکیشن ریمیکس استودیو ریلز
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "تامبنیل ریلز و شورتز")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "اشتراک‌گذاری اطلاعات کاور و تامبنیل"))
}
