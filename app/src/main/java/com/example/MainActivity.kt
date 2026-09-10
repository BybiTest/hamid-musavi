package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AppTab
import com.example.ui.components.AboutDialog
import com.example.ui.components.AdBannerView
import com.example.ui.components.RewardedAdDialog
import com.example.ui.components.VipCheckoutDialog
import com.example.ui.screens.ContentPlannerScreen
import com.example.ui.screens.EngagementCalculatorScreen
import com.example.ui.screens.ScriptMakerScreen
import com.example.ui.screens.VipStoreScreen
import com.example.ui.screens.ViralHooksScreen
import com.example.ui.theme.ReelsStudioTheme
import com.example.ui.theme.SunsetOrange
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.VipGold
import com.example.ui.viewmodel.ReelsViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ReelsStudioTheme {
                CompositionLocalProvider(
                    LocalLayoutDirection provides LayoutDirection.Rtl
                ) {
                    ReelsStudioApp()
                }
            }
        }
    }
}

@Composable
fun ReelsStudioApp(
    viewModel: ReelsViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val vipState by viewModel.vipState.collectAsState()
    val rewardedHook by viewModel.rewardedAdForHook.collectAsState()
    val isAdWatching by viewModel.isAdWatching.collectAsState()
    val adCountdown by viewModel.adCountdown.collectAsState()
    val checkoutPlan by viewModel.selectedCheckoutPlan.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var showAboutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { message ->
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = Color.Transparent,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Bottom Ad Banner
                AdBannerView(
                    isVip = vipState.isVipActive,
                    onUpgradeClick = {
                        viewModel.selectTab(AppTab.VIP)
                    }
                )

                // Navigation Bar
                NavigationBar(
                    containerColor = SurfaceCard,
                    tonalElevation = 8.dp,
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.navigationBars
                    )
                ) {
                    val tabs = listOf(
                        Triple(
                            AppTab.HOOKS,
                            Icons.Default.Bolt,
                            "قلاب‌ها"
                        ),
                        Triple(
                            AppTab.SCRIPTS,
                            Icons.Default.Description,
                            "سناریوساز"
                        ),
                        Triple(
                            AppTab.PLANNER,
                            Icons.Default.CalendarMonth,
                            "تقویم"
                        ),
                        Triple(
                            AppTab.CALCULATOR,
                            Icons.Default.Analytics,
                            "تعامل"
                        ),
                        Triple(
                            AppTab.VIP,
                            Icons.Default.WorkspacePremium,
                            "VIP"
                        )
                    )

                    tabs.forEach { (tab, icon, label) ->
                        val isSelected = currentTab == tab
                        val isVipTab = tab == AppTab.VIP

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                viewModel.selectTab(tab)
                            },
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
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Medium
                                    }
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = if (isVipTab) {
                                    VipGold
                                } else {
                                    Color.White
                                },
                                selectedTextColor = if (isVipTab) {
                                    VipGold
                                } else {
                                    SunsetOrange
                                },
                                indicatorColor = if (isVipTab) {
                                    VipGold.copy(alpha = 0.2f)
                                } else {
                                    SunsetOrange
                                },
                                unselectedIconColor = if (isVipTab) {
                                    VipGold.copy(alpha = 0.7f)
                                } else {
                                    TextMuted
                                },
                                unselectedTextColor = TextMuted
                            ),
                            modifier = Modifier.testTag(
                                "tab_${tab.name.lowercase()}"
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // About button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 4.dp
                    ),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        showAboutDialog = true
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "درباره برنامه",
                        tint = TextMuted
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (currentTab) {
                    AppTab.HOOKS -> {
                        ViralHooksScreen(
                            viewModel = viewModel
                        )
                    }

                    AppTab.SCRIPTS -> {
                        ScriptMakerScreen(
                            viewModel = viewModel
                        )
                    }

                    AppTab.PLANNER -> {
                        ContentPlannerScreen(
                            viewModel = viewModel
                        )
                    }

                    AppTab.CALCULATOR -> {
                        EngagementCalculatorScreen(
                            viewModel = viewModel
                        )
                    }

                    AppTab.VIP -> {
                        VipStoreScreen(
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }

    // About App Modal
    if (showAboutDialog) {
        AboutDialog(
            onDismiss = {
                showAboutDialog = false
            }
        )
    }

    // Rewarded Ad Modal
    RewardedAdDialog(
        hook = rewardedHook,
        isWatching = isAdWatching,
        countdown = adCountdown,
        onStartWatch = {
            viewModel.startWatchingRewardedAd()
        },
        onDismiss = {
            viewModel.dismissRewardedAdPrompt()
        }
    )

    // VIP Checkout Modal
    VipCheckoutDialog(
        plan = checkoutPlan,
        onConfirm = {
            viewModel.confirmPurchase()
        },
        onDismiss = {
            viewModel.dismissCheckout()
        }
    )
}
