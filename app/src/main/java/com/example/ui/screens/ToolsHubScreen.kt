package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppTab
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SunsetOrange
import com.example.ui.theme.VipGold
import com.example.ui.viewmodel.ReelsViewModel

@Composable
fun ToolsHubScreen(
    viewModel: ReelsViewModel,
    modifier: Modifier = Modifier
) {
    val appSettings by viewModel.appSettings.collectAsState()
    val vipState by viewModel.vipState.collectAsState()
    val plannerProgress by viewModel.plannerProgress.collectAsState()

    val completedDaysCount = plannerProgress.values.count { it }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "ابزارها و تنظیمات کاربردی",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "مدیریت تنظیمات، شخصی‌سازی، تقویم و امکانات ویژه ریلز استودیو",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.5.sp
                )
            }
        }

        // 1. Settings Card (Primary Highlight)
        item {
            HubFeatureCard(
                title = "تنظیمات کامل برنامه",
                subtitle = "حالت تیره/روشن، اندازه قلم سراسری، کلید Gemini و درباره ما",
                badgeText = if (appSettings.isDarkMode) "تم تاریک فعال" else "تم روشن فعال",
                badgeColor = SunsetOrange,
                icon = Icons.Default.Settings,
                iconGradient = listOf(SunsetOrange, ElectricPurple),
                testTag = "hub_card_settings",
                onClick = { viewModel.selectTab(AppTab.SETTINGS) }
            )
        }

        // 2. Content Planner Card
        item {
            HubFeatureCard(
                title = "تقویم ۳۰ روزه محتوای وایرال",
                subtitle = "برنامه روزانه انتشار، موضوعات سناریو و اهداف رشد الگوریتم",
                badgeText = "$completedDaysCount از ۳۰ روز انجام شد",
                badgeColor = SuccessGreen,
                icon = Icons.Default.CalendarMonth,
                iconGradient = listOf(Color(0xFF00B4D8), Color(0xFF0077B6)),
                testTag = "hub_card_planner",
                onClick = { viewModel.selectTab(AppTab.PLANNER) }
            )
        }

        // 3. Engagement Calculator Card
        item {
            HubFeatureCard(
                title = "محاسبه‌گر نرخ تعامل (Engagement)",
                subtitle = "آنالیز تعامل، فرمول امتیاز الگوریتم و توصیه‌های تخصصی رشد",
                badgeText = "فرمول رسمی اینستاگرام",
                badgeColor = ElectricPurple,
                icon = Icons.Default.Analytics,
                iconGradient = listOf(ElectricPurple, Color(0xFF9333EA)),
                testTag = "hub_card_calculator",
                onClick = { viewModel.selectTab(AppTab.CALCULATOR) }
            )
        }

        // 4. VIP Store Card
        item {
            HubFeatureCard(
                title = "فروشگاه اشتراک VIP و الماس",
                subtitle = "باز کردن تمام قالب‌های قفل، دسترسی به سناریوهای انفجاری و حذف تبلیغات",
                badgeText = if (vipState.isVipActive) "اشتراک طلایی فعال است ✓" else "پلن‌های تخفیف ویژه",
                badgeColor = VipGold,
                icon = Icons.Default.WorkspacePremium,
                iconGradient = listOf(VipGold, Color(0xFFE65100)),
                testTag = "hub_card_vip",
                onClick = { viewModel.selectTab(AppTab.VIP) }
            )
        }

        // Quick Settings Toggles Strip
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "دسترسی سریع به تنظیمات ظاهری",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Toggle Dark/Light Mode button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.toggleDarkMode(!appSettings.isDarkMode)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (appSettings.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = null,
                                    tint = if (appSettings.isDarkMode) VipGold else SunsetOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (appSettings.isDarkMode) "تغییر به روشن" else "تغییر به تاریک",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = if (appSettings.isDarkMode) "حالت روز" else "حالت شب",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        // Font scale quick button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.selectTab(AppTab.SETTINGS)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatSize,
                                    contentDescription = null,
                                    tint = ElectricPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "اندازه قلم متن",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "مقیاس ${String.format("%.1f", appSettings.fontScale)}x",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HubFeatureCard(
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    icon: ImageVector,
    iconGradient: List<Color>,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(iconGradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeColor,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
