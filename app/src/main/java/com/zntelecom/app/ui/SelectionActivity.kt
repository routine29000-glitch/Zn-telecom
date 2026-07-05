package com.zntelecom.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.zntelecom.app.R
import com.zntelecom.app.util.AdIds
import com.zntelecom.app.util.OfferType

class SelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        findViewById<Button>(R.id.btnDailyGift).setOnClickListener {
            goToPhoneNumber(OfferType.DAILY_GIFT)
        }
        findViewById<Button>(R.id.btnWeeklyOffers).setOnClickListener {
            goToPhoneNumber(OfferType.WEEKLY_OFFERS)
        }

        loadBanner()
    }

    private fun goToPhoneNumber(offerType: OfferType) {
        val intent = Intent(this, PhoneNumberActivity::class.java)
        intent.putExtra(EXTRA_OFFER_TYPE, offerType.name)
        startActivity(intent)
    }

    private fun loadBanner() {
        val container = findViewById<FrameLayout>(R.id.bannerContainer)
        val adView = AdView(this)
        adView.setAdSize(AdSize.BANNER)
        adView.adUnitId = AdIds.BANNER_SELECTION_SCREEN
        container.addView(adView)
        adView.loadAd(AdRequest.Builder().build())
    }

    companion object {
        const val EXTRA_OFFER_TYPE = "extra_offer_type"
    }
}
