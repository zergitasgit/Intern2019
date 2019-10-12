package com.document.pdfviewer.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object Utils {
    fun rateApp(context: Context) {
        val uri = Uri.parse("market://details?id=" + context.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            context.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                    Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)
                    )
            )
        }

    }
}