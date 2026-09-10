package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AppTab
import com.example.ui.components.AdBannerView
import com.example.ui.components.RewardedAdDialog
import com.example.ui.components.VipCheckoutDialog
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.ContentPlannerScreen
import com.example.ui.screens.EngagementCalculatorScreen
import com.example.ui.screens.ScriptMakerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ThumbnailStudioScreen
import com.example.ui.screens.ToolsHubScreen
import com.example.ui.screens.VipStoreScreen
import com.example.ui.screens.ViralHooksScreen
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.ReelsStudioTheme
import com.example.ui.theme.SunsetOrange
import com.example.ui.theme.VipGold
import com.example.ui.viewmodel.ReelsViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ReelsViewModel = viewModel()
            val appSettings by viewModel.appSettings.collectAsState()

            ReelsStudioTheme(
                isDarkMode = appSettings.isDarkMode,
                fontScale = appSettings.fontScale
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    ReelsStudioApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ReelsStudioApp(
    viewModel: ReelsViewModel
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val vipState by viewModel.vipState.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()
    val rewardedHook by viewModel.rewardedAdForHook.collectAsState()
    val isAdWatching by viewModel.isAdWatching.collectAsState()
    val adCountdown by viewModel.adCountdown.collectAsState()
    val checkoutPlan by viewModel.selectedCheckoutPlan.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Intercept back button when inside secondary sub-screens
    val isSubScreenOfTools = currentTab in listOf(
        AppTab.PLANNER,
        AppTab.CALCULATOR,
        AppTab.VIP,
        AppTab.SETTINGS
    )
    BackHandler(enabled = isSubScreenOfTools || currentTab != AppTab.HOOKS) {
        if (isSubScreenOfTools) {
            viewModel.selectTab(AppTab.TOOLS)
        } else {
            viewModel.selectTab(AppTab.HOOKS)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Logo & App Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.selectTab(AppTab.HOOKS) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.linearGradient(listOf(SunsetOrange, ElectricPurple))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ریمیکس استودیوی ریلز",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "دستیار وایرال و هوش مصنوعی",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Right side: VIP + Settings Button
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = VipGold.copy(alpha = 0.15f),
                            modifier = Modifier
                                .clickable { viewModel.selectTab(AppTab.VIP) }
                                .testTag("top_vip_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = "VIP",
                                    tint = VipGold,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (vipState.isVipActive) "VIP" else "الماس VIP",
                                    color = VipGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = { viewModel.selectTab(AppTab.SETTINGS) },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("top_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "تنظیمات برنامه",
                                tint = if (currentTab == AppTab.SETTINGS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Bottom Ad Banner (Tapsell simulated, hidden if VIP or ads toggled off)
                AdBannerView(
                    isVip = vipState.isVipActive,
                    isAdsEnabled = appSettings.isRealAdsEnabled,
                    onUpgradeClick = { viewModel.selectTab(AppTab.VIP) }
                )

                // 6-Tab Navigation Bar with direct Settings and AI Assistant
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    val tabs = listOf(
                        Triple(AppTab.HOOKS, Icons.Default.Bolt, "قلاب‌ها"),
                        Triple(AppTab.SCRIPTS, Icons.Default.Description, "سناریو"),
                        Triple(AppTab.THUMBNAIL_STUDIO, Icons.Default.FormatPaint, "کاور"),
                        Triple(AppTab.AI_ASSISTANT, Icons.Default.AutoAwesome, "دستیار"),
                        Triple(AppTab.TOOLS, Icons.Default.Widgets, "ابزارها"),
                        Triple(AppTab.SETTINGS, Icons.Default.Settings, "تنظیمات")
                    )

                    tabs.forEach { (tab, icon, label) ->
                        val isSelected = currentTab == tab || (
                            tab == AppTab.TOOLS && currentTab in listOf(
                                AppTab.PLANNER,
                                AppTab.CALCULATOR,
                                AppTab.VIP
                            )
                        )
                        val isAiTab = tab == AppTab.AI_ASSISTANT
                        val isStudioTab = tab == AppTab.THUMBNAIL_STUDIO

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(tab) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 10.5.sp,
                                    maxLines = 1,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = if (isAiTab) ElectricPurple else if (isStudioTab) SunsetOrange else MaterialTheme.colorScheme.primary,
                                selectedTextColor = if (isAiTab) ElectricPurple else if (isStudioTab) SunsetOrange else MaterialTheme.colorScheme.primary,
                                indicatorColor = if (isAiTab) ElectricPurple.copy(alpha = 0.2f) else if (isStudioTab) SunsetOrange.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.HOOKS -> ViralHooksScreen(viewModel = viewModel)
                AppTab.SCRIPTS -> ScriptMakerScreen(viewModel = viewModel)
                AppTab.THUMBNAIL_STUDIO -> ThumbnailStudioScreen(viewModel = viewModel)
                AppTab.AI_ASSISTANT -> AiAssistantScreen(viewModel = viewModel)
                AppTab.TOOLS -> ToolsHubScreen(viewModel = viewModel)
                AppTab.PLANNER -> ContentPlannerScreen(
                    viewModel = viewModel,
                    onBackClick = { viewModel.selectTab(AppTab.TOOLS) }
                )
                AppTab.CALCULATOR -> EngagementCalculatorScreen(
                    viewModel = viewModel,
                    onBackClick = { viewModel.selectTab(AppTab.TOOLS) }
                )
                AppTab.VIP -> VipStoreScreen(
                    viewModel = viewModel,
                    onBackClick = { viewModel.selectTab(AppTab.TOOLS) }
                )
                AppTab.SETTINGS -> SettingsScreen(
                    viewModel = viewModel,
                    onBackClick = { viewModel.selectTab(AppTab.TOOLS) }
                )
            }
        }
    }

    // Rewarded Ad Modal
    RewardedAdDialog(
        hook = rewardedHook,
        isWatching = isAdWatching,
        countdown = adCountdown,
        onStartWatch = { viewModel.startWatchingRewardedAd() },
        onDismiss = { viewModel.dismissRewardedAdPrompt() }
    )

    // VIP Checkout Modal
    VipCheckoutDialog(
        plan = checkoutPlan,
        onConfirm = { viewModel.confirmPurchase() },
        onDismiss = { viewModel.dismissCheckout() }
    )
}
