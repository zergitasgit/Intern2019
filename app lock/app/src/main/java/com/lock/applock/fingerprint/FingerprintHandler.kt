package com.lock.applock.fingerprint

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.lock.applock.R

@RequiresApi(Build.VERSION_CODES.M)
class FingerprintHandler(private var context: Context) :
    FingerprintManager.AuthenticationCallback() {


    // Constructor

    fun startAuth(
        manager: FingerprintManager,
        cryptoObject: FingerprintManager.CryptoObject?
    ) {
        val cancellationSignal = CancellationSignal()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.USE_FINGERPRINT
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
        this.update("$errString")
    }

    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
        this.update("$helpString")
    }

    override fun onAuthenticationFailed() {
        this.update("Fingerprint Authentication failed.")
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
        this.update("dung")
    }

    private  fun update(e: String) {
        Toast.makeText(context,e,Toast.LENGTH_SHORT).show()
    }

}
