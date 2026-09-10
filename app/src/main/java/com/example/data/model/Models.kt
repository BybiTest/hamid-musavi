package com.example.data.model

enum class HookCategory(
    val titleFa: String,
    val iconName: String
) {
    ALL("همه", "AllInclusive"),
    ONLINE_SHOP("فروش و آنلاین‌شاپ", "ShoppingBag"),
    EDUCATION("آموزش و ترفند", "School"),
    LIFESTYLE("بلاگری و روزمرگی", "CameraAlt"),
    CONTROVERSIAL("جنجالی و کنجکاوی", "Psychology"),
    TECH_AI("تکنولوژی و هوش مصنوعی", "Memory")
}

/**
 * مدل اصلی قلاب‌هایی که در صفحه ViralHooksScreen نمایش داده می‌شوند.
 */
data class HookItem(
    val id: String,
    val title: String,
    val category: HookCategory,
    val hookFa: String,
    val psychologyExplanation: String,
    val visualSceneTip: String,
    val isVipOnly: Boolean = false,
    val estimatedViewPotential: String = "+500K"
)

/**
 * مدل سازنده سناریو.
 */
data class ScriptTemplate(
    val id: String,
    val title: String,
    val category: HookCategory,
    val durationSeconds: Int,
    val hookTemplate: String,
    val bodyTemplate: String,
    val ctaTemplate: String,
    val isVipOnly: Boolean = false,
    val placeholderTopic: String = "محصول یا آموزش شما",
    val placeholderBenefit: String = "سود یا راه‌حل بزرگ",
    val placeholderObstacle: String = "بزرگترین اشتباه رایج"
)

/**
 * مدل قدیمی/اصلی تقویم محتوا که PreloadedContent از آن استفاده می‌کند.
 */
data class ContentDayPlan(
    val dayNumber: Int,
    val title: String,
    val category: String,
    val hookIdea: String,
    val filmingTip: String,
    val isVipOnly: Boolean = false
)

/**
 * مدل سازگار با ساختار جدید ViewModel برای برنامه‌ریزی محتوا.
 */
data class ContentPlanDay(
    val dayNumber: Int,
    val title: String,
    val suggestedHook: String,
    val contentType: String,
    val description: String,
    val callToAction: String,
    val isVipOnly: Boolean = false
)

/**
 * مدل سازگار با ViewModel برای قلاب‌های قابل بازشدن.
 */
data class ViralHook(
    val id: Int,
    val title: String,
    val template: String,
    val category: HookCategory = HookCategory.ALL,
    val isVipOnly: Boolean = false
)

/**
 * مدل قالب Thumbnail که ViewModel برای Thumbnail Studio استفاده می‌کند.
 */
data class ThumbnailTemplate(
    val id: String,
    val defaultTitle: String,
    val defaultSubtitle: String,
    val defaultBadge: String,
    val isVipOnly: Boolean = false
)

/**
 * مدل پلن VIP.
 */
data class VipPlan(
    val title: String,
    val price: String = "",
    val durationDays: Int = 30
)

data class EngagementResult(
    val engagementRate: Float,
    val status: String,
    val statusColor: Long,
    val viralPotentialScore: Int,
    val algorithmDiagnosis: String,
    val actionTips: List<String>
)

enum class AppTab(
    val titleFa: String
) {
    HOOKS("قلاب‌ها"),
    SCRIPTS("سناریوساز"),
    THUMBNAIL_STUDIO("کاور ساز"),
    AI_ASSISTANT("هوش‌مصنوعی"),
    TOOLS("ابزارها و تنظیمات"),
    PLANNER("تقویم ۳۰ روزه"),
    CALCULATOR("نرخ تعامل"),
    VIP("الماس VIP"),
    SETTINGS("تنظیمات")
}

enum class CoverCategory(
    val titleFa: String
) {
    ALL("همه قالب‌ها"),
    TECH("تکنولوژی و هوش مصنوعی"),
    FINANCE("مالی، بیزینس و موفقیت"),
    VIRAL("وایرال، جنجالی و ترند"),
    LIFESTYLE("بلاگری و روزمره")
}

data class CoverTemplate(
    val id: String,
    val title: String,
    val category: CoverCategory,
    val drawableResId: Int,
    val defaultMainHeadline: String,
    val defaultSubHeadline: String,
    val badgeText: String,
    val accentColorHex: Long = 0xFFFF5E3A,
    val textColorHex: Long = 0xFFFFFFFF,
    val isVipOnly: Boolean = false
)
