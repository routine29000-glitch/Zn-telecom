package com.zntelecom.app

import android.app.Application
import com.google.android.gms.ads.MobileAds

class ZnApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize AdMob once, as early as possible.
        MobileAds.initialize(this) { /* init status callback, can log if needed */ }
    }
}
