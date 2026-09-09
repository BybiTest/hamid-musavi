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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun VipStoreScreen(
    viewModel: ReelsViewModel,
    modifier: Modifier = Modifier
) {
    val vipState by viewModel.vipState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Top Header
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = "فروشگاه اشتراک VIP ریلز استودیو",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "دسترسی نامحدود به سناریوهای انفجار بازدید و حذف کامل تبلیغات",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Current VIP Status Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (vipState.isVipActive) Color(0xFF261E05) else SurfaceCard
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (vipState.isVipActive) VipGold else SurfaceCardBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            if (vipState.isVipActive) listOf(VipGoldDark, VipGold)
                                            else listOf(SurfaceCardBorder, SurfaceCardElevated)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = if (vipState.isVipActive) Color.Black else TextSecondary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = if (vipState.isVipActive) "اشتراک VIP شما فعال است" else "طرح فعلی: کاربر رایگان",
                                    color = if (vipState.isVipActive) VipGold else TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (vipState.isVipActive) "پلن: ${vipState.planName} (تا ${vipState.expirationDateString})"
                                    else "دسترسی محدود با نمایش تبلیغات درون‌برنامه‌ای",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Subscription Plan Cards
            item {
                Text(
                    text = "پلن‌های اشتراک ویژه را انتخاب کنید:",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Plan 1: 1 Month
            item {
                PlanCard(
                    title = "اشتراک ۱ ماهه",
                    price = "۴۹,۰۰۰ تومان",
                    originalPrice = null,
                    discountBadge = null,
                    isPopular = false,
                    features = listOf("دسترسی به تمام قلاب‌های وایرال", "حذف تمام تبلیغات درون‌برنامه‌ای", "پشتیبانی استاندارد"),
                    onSelect = { viewModel.openCheckout("۱ ماهه", "۴۹,۰۰۰ تومان", 30) }
                )
            }

            // Plan 2: 3 Months (Most Popular)
            item {
                PlanCard(
                    title = "اشتراک ۳ ماهه (فوق‌العاده برای رشد)",
                    price = "۹۹,۰۰۰ تومان",
                    originalPrice = "۱۴۷,۰۰۰ تومان",
                    discountBadge = "۳۳٪ تخفیف ویژه",
                    isPopular = true,
                    features = listOf("دسترسی نامحدود به تمام سناریوها", "تقویم کامل ۳۰ روزه چالش وایرال", "حذف کامل تبلیغات بدون وقفه", "آپدیت هفتگی سناریوهای ترند"),
                    onSelect = { viewModel.openCheckout("۳ ماهه", "۹۹,۰۰۰ تومان", 90) }
                )
            }

            // Plan 3: 1 Year (Best Value)
            item {
                PlanCard(
                    title = "اشتراک سالانه (بصرفه‌ترین)",
                    price = "۱۳۹,۰۰۰ تومان",
                    originalPrice = "۵۸۸,۰۰۰ تومان",
                    discountBadge = "۷۵٪ تخفیف سالانه",
                    isPopular = false,
                    features = listOf("یک سال کامل بدون تبلیغات", "دسترسی به همه ابزارهای آینده", "پکیج طلایی قلاب‌های میلیونی", "عضویت در باشگاه تولیدکنندگان حرفه‌ای"),
                    onSelect = { viewModel.openCheckout("سالانه", "۱۳۹,۰۰۰ تومان", 365) }
                )
            }

            // Feature Comparison Table
            item {
                Text(
                    text = "مقایسه نسخه رایگان با نسخه VIP:",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("امکانات", color = TextMuted, fontSize = 11.sp, modifier = Modifier.weight(1.5f))
                            Text("رایگان", color = TextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                            Text("طلایی VIP", color = VipGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = SurfaceCardBorder)

                        ComparisonRow("دسترسی به قلاب‌های ۳ ثانیه‌ای", "پایه (با تبلیغ)", "نامحدود فوری")
                        ComparisonRow("قالب‌های سناریوساز ریلز", "۲ سناریو", "تمام سناریوها")
                        ComparisonRow("تقویم چالش ۳۰ روزه", "۱۵ روز اول", "۳۰ روز کامل")
                        ComparisonRow("تبلیغات درون‌برنامه‌ای", "دارد (بنر و ویدیو)", "کاملاً حذف شده")
                        ComparisonRow("ذخیره در سناریوهای من", "حداکثر ۳ عدد", "نامحدود")
                    }
                }
            }

            // Trust & Security Info
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceCardElevated)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "پرداخت امن از طریق درگاه رسمی کافه‌بازار و مایکت با ضمانت بازگشت وجه ۷ روزه",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PlanCard(
    title: String,
    price: String,
    originalPrice: String?,
    discountBadge: String?,
    isPopular: Boolean,
    features: List<String>,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPopular) SurfaceCardElevated else SurfaceCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isPopular) 1.5.dp else 1.dp,
            if (isPopular) SunsetOrange else SurfaceCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                if (discountBadge != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SunsetOrange
                    ) {
                        Text(
                            text = discountBadge,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = price,
                    color = VipGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                if (originalPrice != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = originalPrice,
                        color = TextMuted,
                        fontSize = 12.sp,
                        style = androidx.compose.ui.text.TextStyle(
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            features.forEach { feat ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = feat, color = TextSecondary, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onSelect,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPopular) SunsetOrange else SurfaceCardBorder,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth().height(44.dp)
            ) {
                Text(
                    text = if (isPopular) "خرید اشتراک با تخفیف ویژه" else "انتخاب این پلن",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun ComparisonRow(feature: String, freeValue: String, vipValue: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(feature, color = TextSecondary, fontSize = 11.sp, modifier = Modifier.weight(1.5f))
        Text(freeValue, color = TextMuted, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
        Text(vipValue, color = VipGold, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
    }
}
