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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppTab
import com.example.data.model.ContentDayPlan
import com.example.ui.theme.DarkBg
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.SunsetOrange
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VipGold
import com.example.ui.theme.VipGoldDark
import com.example.ui.viewmodel.ReelsViewModel

@Composable
fun ContentPlannerScreen(
    viewModel: ReelsViewModel,
    modifier: Modifier = Modifier
) {
    val progressMap by viewModel.plannerProgress.collectAsState()
    val vipState by viewModel.vipState.collectAsState()
    val days = viewModel.allPlannerDays

    val completedCount = progressMap.values.count { it }
    val progressFraction = completedCount.toFloat() / days.size.toFloat()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = "تقویم ۳۰ روزه چالش وایرال ریلز",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "برنامه منظم انتشار پست روزانه برای تسخیر الگوریتم",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Progress & Streak Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(SunsetOrange, Color(0xFFFF9F1C)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "پیشرفت چالش ۳۰ روزه",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$completedCount روز از ${days.size} روز انجام شده",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = "${(progressFraction * 100).toInt()}٪",
                        color = SunsetOrange,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = SunsetOrange,
                    trackColor = SurfaceCardElevated
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Days List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(days, key = { it.dayNumber }) { dayPlan ->
                val isCompleted = progressMap[dayPlan.dayNumber] ?: false
                val isLocked = dayPlan.isVipOnly && !vipState.isVipActive

                DayPlanCard(
                    dayPlan = dayPlan,
                    isCompleted = isCompleted,
                    isLocked = isLocked,
                    onToggleComplete = { viewModel.togglePlannerDay(dayPlan.dayNumber) },
                    onVipClick = { viewModel.selectTab(AppTab.VIP) }
                )
            }
        }
    }
}

@Composable
fun DayPlanCard(
    dayPlan: ContentDayPlan,
    isCompleted: Boolean,
    isLocked: Boolean,
    onToggleComplete: () -> Unit,
    onVipClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Color(0xFF14241B) else SurfaceCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCompleted) Color(0xFF10B981) else if (isLocked) VipGold.copy(alpha = 0.5f) else SurfaceCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Day Number + Category + Completed Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isCompleted) Color(0xFF10B981) else SunsetOrange
                    ) {
                        Text(
                            text = "روز ${dayPlan.dayNumber}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceCardElevated
                    ) {
                        Text(
                            text = dayPlan.category,
                            color = TextSecondary,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                if (isLocked) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = VipGoldDark,
                        modifier = Modifier.clickable { onVipClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("روزهای VIP", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onToggleComplete() },
                        color = Color.Transparent
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "انجام شد",
                            tint = if (isCompleted) Color(0xFF10B981) else TextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = dayPlan.title,
                color = if (isCompleted) Color(0xFF10B981) else TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (!isLocked) {
                // Hook idea
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("💡 قلاب پیشنهادی: ", color = VipGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("« ${dayPlan.hookIdea} »", color = TextPrimary, fontSize = 11.sp, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Filming tip
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("🎬 نکته فیلمبرداری: ", color = ElectricPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(dayPlan.filmingTip, color = TextMuted, fontSize = 11.sp, modifier = Modifier.weight(1f))
                }
            } else {
                Text(
                    text = "سناریو و نکات تخصصی نیمه دوم چالش مخصوص کاربران VIP است.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
