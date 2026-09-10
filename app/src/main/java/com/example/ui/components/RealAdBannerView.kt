package com.example.ui.components

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

private const val TEST_BANNER_AD_UNIT_ID =
    "ca-app-pub-3940256099942544/6300978111"

@Composable
fun RealAdBannerView(
    isVip: Boolean,
    isAdsEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (isVip || !isAdsEnabled) {
        return
    }

    val context = LocalContext.current

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        factory = {
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = TEST_BANNER_AD_UNIT_ID

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                loadAd(
                    AdRequest.Builder().build()
                )
            }
        },
        update = { adView ->
            if (adView.adSize == null) {
                adView.setAdSize(AdSize.BANNER)
            }
        }
    )
}
