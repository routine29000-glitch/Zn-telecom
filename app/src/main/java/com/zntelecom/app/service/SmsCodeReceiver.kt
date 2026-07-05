package com.zntelecom.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

/**
 * Listens for incoming SMS messages. When a message arrives from a sender
 * whose name/number contains "Ooredoo" (case-insensitive), it
 * extracts the verification code (a run of 4 to 6 digits) from the message
 * body and broadcasts it locally so ActivationActivity / the Accessibility
 * Service can auto-fill it.
 *
 * NOTE: requires the READ_SMS / RECEIVE_SMS permissions declared in the
 * manifest, and the user must grant them at runtime (Android 6+).
 */
class SmsCodeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        for (message in messages) {
            val sender = message.originatingAddress ?: message.displayOriginatingAddress ?: ""
            val body = message.messageBody ?: continue

            if (isFromKnownOperator(sender, body)) {
                val code = extractVerificationCode(body)
                if (code != null) {
                    Log.d(TAG, "Extracted code from $sender: $code")
                    broadcastCode(context, code)
                }
            }
        }
    }

    private fun isFromKnownOperator(sender: String, body: String): Boolean {
        val haystack = (sender + " " + body).lowercase()
        return haystack.contains("ooredoo")
    }

    /**
     * Looks for the first standalone run of 4 to 6 digits in the message.
     * This is intentionally generic since we don't yet have a sample of the
     * real Ooredoo SMS format. Adjust the regex once you have a real
     * example (e.g. to anchor around a specific keyword like "Code:").
     */
    private fun extractVerificationCode(body: String): String? {
        val regex = Regex("""\b(\d{4,6})\b""")
        return regex.find(body)?.groupValues?.get(1)
    }

    private fun broadcastCode(context: Context, code: String) {
        val localIntent = Intent(ACTION_CODE_RECEIVED)
        localIntent.putExtra(EXTRA_CODE, code)
        localIntent.setPackage(context.packageName)
        context.sendBroadcast(localIntent)
    }

    companion object {
        private const val TAG = "SmsCodeReceiver"
        const val ACTION_CODE_RECEIVED = "com.zntelecom.app.ACTION_CODE_RECEIVED"
        const val EXTRA_CODE = "extra_code"
    }
}
