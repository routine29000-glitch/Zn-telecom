package com.zntelecom.app.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Dials a real USSD code exactly as the user would from the dialer.
 * Reading the interactive USSD menu response requires the
 * OperatorAccessibilityService (Android restricts USSD response reading
 * via normal APIs from Android 8+).
 */
object UssdHelper {

    fun hasCallPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED

    fun requestCallPermission(activity: android.app.Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity, arrayOf(Manifest.permission.CALL_PHONE), requestCode
        )
    }

    /**
     * @param ussdCode e.g. "#500#" for the Ooredoo Dima menu.
     * Note: '#' must be URL-encoded as %23 in the tel: Uri.
     */
    fun dial(context: Context, ussdCode: String) {
        val encoded = Uri.encode(ussdCode)
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$encoded"))
        context.startActivity(intent)
    }

    // Real, documented Ooredoo Algeria codes.
    const val OOREDOO_DIMA_MENU = "#500#"      // opens Dima subscriptions menu
    const val OOREDOO_INTERNET_MENU = "*151#"  // internet subscriptions menu
}
