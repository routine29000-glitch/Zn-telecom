package com.zntelecom.app.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zntelecom.app.R
import com.zntelecom.app.service.SmsCodeReceiver
import com.zntelecom.app.util.OfferType

class OtpActivity : AppCompatActivity() {

    private lateinit var inputOtp: EditText

    private val codeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val code = intent?.getStringExtra(SmsCodeReceiver.EXTRA_CODE) ?: return
            inputOtp.setText(code)
            Toast.makeText(this@OtpActivity, "تم تعبئة الرمز تلقائياً", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        val offerTypeName = intent.getStringExtra(SelectionActivity.EXTRA_OFFER_TYPE)
            ?: OfferType.DAILY_GIFT.name
        val phone = intent.getStringExtra(PhoneNumberActivity.EXTRA_PHONE) ?: ""

        inputOtp = findViewById(R.id.inputOtp)
        requestSmsPermissionsIfNeeded()

        findViewById<Button>(R.id.btnVerify).setOnClickListener {
            val otp = inputOtp.text.toString().trim()
            if (otp.length < 4) {
                Toast.makeText(this, "أدخل الرمز كاملاً", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, ActivationActivity::class.java)
            intent.putExtra(SelectionActivity.EXTRA_OFFER_TYPE, offerTypeName)
            intent.putExtra(PhoneNumberActivity.EXTRA_PHONE, phone)
            startActivity(intent)
            finish()
        }
    }

    private fun requestSmsPermissionsIfNeeded() {
        val needed = arrayOf(Manifest.permission.RECEIVE_SMS)
        val notGranted = needed.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (notGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, notGranted.toTypedArray(), REQUEST_SMS_PERMS)
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(SmsCodeReceiver.ACTION_CODE_RECEIVED)
        ContextCompat.registerReceiver(
            this, codeReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(codeReceiver)
    }

    companion object {
        private const val REQUEST_SMS_PERMS = 2001
    }
}
