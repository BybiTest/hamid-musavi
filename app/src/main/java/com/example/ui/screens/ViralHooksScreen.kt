package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PreloadedContent
import com.example.data.model.AppTab
import com.example.data.model.HookCategory
import com.example.data.model.HookItem
import com.example.data.model.ViralHook
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.SunsetOrange
import com.example.ui.theme.VipGold
import com.example.ui.theme.VipGoldDark
import com.example.ui.viewmodel.ReelsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViralHooksScreen(
    viewModel: ReelsViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showOnlyFavorites by viewModel.showOnlyFavorites.collectAsState()
    val vipState by viewModel.vipState.collectAsState()

    val context = LocalContext.current

    /*
     * PreloadedContent is the current source of the hook library.
     *
     * The current ViewModel uses Int IDs for unlocked/favorite hooks,
     * while HookItem uses String IDs. We therefore assign a stable
     * numeric ID based on the position in the bundled hook list.
     */
    val allHooks = PreloadedContent.hooks

    val hookIdMap: Map<String, Int> =
        allHooks.mapIndexed { index, hook ->
            hook.id to index + 1
        }.toMap()

    val favoriteIds: Set<String> =
        vipState.favoriteHookIds.mapNotNull { numericId ->
            allHooks.firstOrNull { hook ->
                hookIdMap[hook.id] == numericId
            }?.id
        }.toSet()

    val filteredHooks = allHooks.filter { hook ->
        val matchesCategory =
            selectedCategory == null ||
                    selectedCategory == HookCategory.ALL ||
                    hook.category == selectedCategory

        val matchesSearch =
            searchQuery.isBlank() ||
                    hook.title.contains(searchQuery, ignoreCase = true) ||
                    hook.hookFa.contains(searchQuery, ignoreCase = true) ||
                    hook.psychologyExplanation.contains(
                        searchQuery,
                        ignoreCase = true
                    )

        val matchesFavorite =
            !showOnlyFavorites || favoriteIds.contains(hook.id)

        matchesCategory &&
                matchesSearch &&
                matchesFavorite
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // ---------------------------------------------------------
        // Header
        // ---------------------------------------------------------

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "قلاب‌های وایرال ۳ ثانیه‌ای",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "جملات شوکه‌کننده برای توقف اسکرول مخاطب",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            if (vipState.isVipActive) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = VipGoldDark,
                    contentColor = Color.Black
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Black
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "کاربر VIP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.clickable {
                        viewModel.selectTab(AppTab.VIP)
                    },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, VipGold),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = VipGold
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "ارتقا به VIP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VipGold
                        )
                    }
                }
            }
        }

        // ---------------------------------------------------------
        // Search + Favorites
        // ---------------------------------------------------------

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    viewModel.setSearchQuery(it)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                placeholder = {
                    Text(
                        text = "جستجو در قلاب‌ها و کلمات کلیدی...",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                            .copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor =
                        MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor =
                        MaterialTheme.colorScheme.surface,
                    focusedBorderColor = SunsetOrange,
                    unfocusedBorderColor =
                        MaterialTheme.colorScheme.outline,
                    focusedTextColor =
                        MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor =
                        MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        viewModel.toggleFavoritesFilter()
                    },
                color =
                    if (showOnlyFavorites) {
                        SunsetOrange
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                border = BorderStroke(
                    1.dp,
                    if (showOnlyFavorites) {
                        SunsetOrange
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector =
                            if (showOnlyFavorites) {
                                Icons.Default.Star
                            } else {
                                Icons.Default.StarBorder
                            },
                        contentDescription = "علاقه‌مندی‌ها",
                        tint =
                            if (showOnlyFavorites) {
                                Color.White
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ---------------------------------------------------------
        // Categories
        // ---------------------------------------------------------

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(HookCategory.values()) { category ->

                val isSelected =
                    if (selectedCategory == null) {
                        category == HookCategory.ALL
                    } else {
                        category == selectedCategory
                    }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color =
                        if (isSelected) {
                            SunsetOrange
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) {
                            SunsetOrange
                        } else {
                            MaterialTheme.colorScheme.outline
                        }
                    ),
                    modifier = Modifier.clickable {
                        viewModel.selectCategory(
                            if (category == HookCategory.ALL) {
                                null
                            } else {
                                category
                            }
                        )
                    }
                ) {
                    Text(
                        text = category.titleFa,
                        color =
                            if (isSelected) {
                                Color.White
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        fontSize = 12.sp,
                        fontWeight =
                            if (isSelected) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Medium
                            },
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 8.dp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---------------------------------------------------------
        // Hooks
        // ---------------------------------------------------------

        if (filteredHooks.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "هیچ قلابی با این مشخصات یافت نشد!",
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "می‌توانید فیلتر جستجو را پاک کنید.",
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                                .copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 90.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(
                    items = filteredHooks,
                    key = { it.id }
                ) { hook ->

                    val numericId =
                        hookIdMap[hook.id] ?: 0

                    val isUnlocked =
                        vipState.isVipActive ||
                                vipState.unlockedHookIds.contains(
                                    numericId
                                ) ||
                                !hook.isVipOnly

                    val isFavorite =
                        favoriteIds.contains(hook.id)

                    HookCardItem(
                        hook = hook,
                        isUnlocked = isUnlocked,
                        isFavorite = isFavorite,

                        onFavoriteClick = {
                            if (numericId != 0) {
                                viewModel.toggleFavoriteHook(
                                    numericId
                                )
                            }
                        },

                        onCopyClick = {
                            val clipboard =
                                context.getSystemService(
                                    Context.CLIPBOARD_SERVICE
                                ) as ClipboardManager

                            clipboard.setPrimaryClip(
                                ClipData.newPlainText(
                                    "Reels Hook",
                                    hook.hookFa
                                )
                            )
                        },

                        onUnlockAdClick = {
                            val viralHook =
                                ViralHook(
                                    id = numericId,
                                    title = hook.title,
                                    template = hook.hookFa,
                                    category = hook.category,
                                    isVipOnly = hook.isVipOnly
                                )

                            viewModel.requestUnlockHook(
                                viralHook
                            )
                        },

                        onVipClick = {
                            viewModel.selectTab(AppTab.VIP)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HookCardItem(
    hook: HookItem,
    isUnlocked: Boolean,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onCopyClick: () -> Unit,
    onUnlockAdClick: () -> Unit,
    onVipClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (hook.isVipOnly && !isUnlocked) {
                VipGold.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.outline
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // -----------------------------------------------------
            // Header
            // -----------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color =
                            MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = hook.category.titleFa,
                            color = SunsetOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x3300E676)
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 6.dp,
                                vertical = 3.dp
                            ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Color(0xFF00E676),
                                modifier =
                                    Modifier.size(12.dp)
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(4.dp)
                            )

                            Text(
                                text =
                                    hook.estimatedViewPotential,
                                color = Color(0xFF00E676),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    if (hook.isVipOnly) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = VipGoldDark
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 6.dp,
                                    vertical = 3.dp
                                ),
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector =
                                        Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier =
                                        Modifier.size(12.dp)
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(2.dp)
                                )

                                Text(
                                    text = "VIP",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector =
                                if (isFavorite) {
                                    Icons.Default.Star
                                } else {
                                    Icons.Default.StarBorder
                                },
                            contentDescription = "ذخیره",
                            tint =
                                if (isFavorite) {
                                    SunsetOrange
                                } else {
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant
                                        .copy(alpha = 0.5f)
                                }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = hook.title,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            // -----------------------------------------------------
            // Unlocked
            // -----------------------------------------------------

            if (isUnlocked) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    )
                ) {
                    Text(
                        text = "« ${hook.hookFa} »",
                        color =
                            MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💡 چرا جواب میده؟ ",
                        color = VipGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = hook.psychologyExplanation,
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🎬 ایده تصویربرداری: ",
                        color = ElectricPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = hook.visualSceneTip,
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                                .copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onCopyClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                ) {
                    Icon(
                        imageVector =
                            Icons.Default.ContentCopy,
                        contentDescription = null,
                        tint =
                            MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = "کپی کردن متن قلاب",
                        color =
                            MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            } else {

                // -------------------------------------------------
                // Locked
                // -------------------------------------------------

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(
                        1.dp,
                        VipGold.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = VipGold,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "این قلاب وایرال قفل است (مخصوص VIP)",
                            color =
                                MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "با خرید اشتراک VIP یا مشاهده یک ویدیوی کوتاه ۵ ثانیه‌ای به آن دسترسی پیدا کنید:",
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            Button(
                                onClick = onUnlockAdClick,
                                shape = RoundedCornerShape(10.dp),
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor =
                                            SunsetOrange
                                    ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                            ) {
                                Icon(
                                    imageVector =
                                        Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier =
                                        Modifier.size(16.dp)
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(4.dp)
                                )

                                Text(
                                    text = "دیدن ویدیو (رایگان)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = onVipClick,
                                shape = RoundedCornerShape(10.dp),
                                border =
                                    BorderStroke(
                                        1.dp,
                                        VipGold
                                    ),
                                colors =
                                    ButtonDefaults
                                        .outlinedButtonColors(
                                            contentColor =
                                                VipGold
                                        ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                            ) {
                                Icon(
                                    imageVector =
                                        Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    modifier =
                                        Modifier.size(16.dp),
                                    tint = VipGold
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(4.dp)
                                )

                                Text(
                                    text = "خرید VIP",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
