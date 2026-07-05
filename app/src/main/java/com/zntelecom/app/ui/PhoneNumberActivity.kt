package com.zntelecom.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zntelecom.app.R
import com.zntelecom.app.util.OfferType

class PhoneNumberActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_number)

        val offerTypeName = intent.getStringExtra(SelectionActivity.EXTRA_OFFER_TYPE)
            ?: OfferType.DAILY_GIFT.name

        val inputPhone = findViewById<EditText>(R.id.inputPhone)

        findViewById<Button>(R.id.btnContinue).setOnClickListener {
            val phone = inputPhone.text.toString().trim()
            if (phone.length < 9) {
                Toast.makeText(this, "أدخل رقم صحيح", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, OtpActivity::class.java)
            intent.putExtra(SelectionActivity.EXTRA_OFFER_TYPE, offerTypeName)
            intent.putExtra(EXTRA_PHONE, phone)
            startActivity(intent)
        }
    }

    companion object {
        const val EXTRA_PHONE = "extra_phone"
    }
}
