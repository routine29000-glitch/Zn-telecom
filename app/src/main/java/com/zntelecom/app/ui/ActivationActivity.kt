package com.zntelecom.app.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.zntelecom.app.R
import com.zntelecom.app.util.AdIds
import com.zntelecom.app.util.OfferType
import com.zntelecom.app.util.UssdHelper

class ActivationActivity : AppCompatActivity() {

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activation)

        val offerTypeName = intent.getStringExtra(SelectionActivity.EXTRA_OFFER_TYPE)
            ?: OfferType.DAILY_GIFT.name
        val offerType = OfferType.valueOf(offerTypeName)

        loadInterstitial()
        loadRewarded()

        val watchAdBtn = findViewById<Button>(R.id.btnWatchAdInstant)
        watchAdBtn.setOnClickListener { showRewardedThenActivate(offerType) }

        startActivation(offerType)
    }

    private fun startActivation(offerType: OfferType) {
        if (!UssdHelper.hasCallPermission(this)) {
            UssdHelper.requestCallPermission(this, REQUEST_CALL_PERMISSION)
            return
        }
        // Both flows open the same Dima menu (#500#); the Accessibility
        // Service then picks the right sub-option automatically:
        // - DAILY_GIFT -> daily offers sub-menu (option 5)
        // - WEEKLY_OFFERS -> weekly internet offers sub-menu
        UssdHelper.dial(this, UssdHelper.OOREDOO_DIMA_MENU)
        onActivationSucceeded()
    }

    private fun onActivationSucceeded() {
        findViewById<TextView>(R.id.statusText).text =
            getString(R.string.activation_success)
        findViewById<Button>(R.id.btnWatchAdInstant).visibility =
            android.view.View.VISIBLE
        showInterstitialIfReady()
    }

    // ---- Interstitial: shown once, right after a successful activation ----
    private fun loadInterstitial() {
        InterstitialAd.load(
            this,
            AdIds.INTERSTITIAL_SUCCESS,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    private fun showInterstitialIfReady() {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                }
            }
            ad.show(this)
        }
    }

    // ---- Rewarded: optional "watch ad for instant activation" ----
    private fun loadRewarded() {
        RewardedAd.load(
            this,
            AdIds.REWARDED_INSTANT,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                }
            }
        )
    }

    private fun showRewardedThenActivate(offerType: OfferType) {
        val ad = rewardedAd
        if (ad != null) {
            ad.show(this) { _ ->
                startActivation(offerType)
            }
        } else {
            startActivation(offerType)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CALL_PERMISSION) {
            val offerTypeName = intent.getStringExtra(SelectionActivity.EXTRA_OFFER_TYPE)
                ?: OfferType.DAILY_GIFT.name
            startActivation(OfferType.valueOf(offerTypeName))
        }
    }

    /** Call this once from a settings screen to guide the user to enable
     * the accessibility service (required for reading Ooredoo's USSD
     * menu and auto-selecting the right option). */
    private fun openAccessibilitySettings() {
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    companion object {
        const val REQUEST_CALL_PERMISSION = 1001
    }
}
