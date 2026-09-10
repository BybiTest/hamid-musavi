package com.example

import android.app.Application
import com.google.android.gms.ads.MobileAds

class ReelsStudioApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this) {
            // AdMob SDK initialized.
        }
    }
}
