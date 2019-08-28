package com.os13lockscreen.ilockscreenos13.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.os13lockscreen.ilockscreenos13.R
import com.znitenda.zah
import com.znitenda.zu

object Ads {

    private var interstitialAd: InterstitialAd? = null

    interface OnAdsListener {
        fun onError()

        fun onAdLoaded()

        fun onAdOpened()

        fun onAdClose()
    }

    fun initBanner(activity: Activity, relativeLayout: RelativeLayout, onAdsListener: OnAdsListener) {
        try {
            Log.e("ADS_ERROR", "initBanner: ${zu.s(activity)} ${TextUtils.isEmpty(zah.getBannerAds(activity))} ${zah.getBannerAds(activity)}")

            if (zu.s(activity) && !TextUtils.isEmpty(zah.getBannerAds(activity))) {
                Log.e("ADS_ERROR", "initBanner: show Ads")
                val adView = AdView(activity)
                adView.adSize = AdSize.BANNER
                adView.adUnitId = zah.getBannerAds(activity)
                val adRequest = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build()
                adView.adListener = object : AdListener() {
                    override fun onAdFailedToLoad(i: Int) {
                        Log.e("ADS_ERROR", i.toString())
//                        Toast.makeText(activity, "BANNER_ADS_ERROR: $i", Toast.LENGTH_SHORT).show()
                        onAdsListener.onError()
                        super.onAdFailedToLoad(i)
                    }

                    override fun onAdLoaded() {
                        onAdsListener.onAdLoaded()
                        super.onAdLoaded()
                    }

                    override fun onAdOpened() {
                        onAdsListener.onAdOpened()
                        super.onAdOpened()
                    }

                    override fun onAdClosed() {
                        onAdsListener.onAdClose()
                        super.onAdClosed()
                    }
                }
                adView.loadAd(adRequest)

                val rLParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT)
                rLParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1)
                relativeLayout.addView(adView, rLParams)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun loadBannerAds(mActivity: Activity, layoutAds: RelativeLayout) {
        Log.e("ADS_ERROR", "loadBannerAds: show Ads")
        initBanner(mActivity, layoutAds, object : OnAdsListener {
            override fun onError() {
                layoutAds.visibility = View.GONE
            }

            override fun onAdLoaded() {
                layoutAds.visibility = View.VISIBLE
            }

            override fun onAdOpened() {
                layoutAds.visibility = View.VISIBLE
            }

            override fun onAdClose() {
                layoutAds.visibility = View.GONE
                loadBannerAds(mActivity, layoutAds)
            }
        })
    }

    fun largeBanner(activity: Activity, relativeLayout: RelativeLayout) {
        if (zu.s(activity) && !TextUtils.isEmpty(zah.getBannerAds(activity))) {
            val adView = AdView(activity)
            adView.adSize = AdSize.LARGE_BANNER
            adView.adUnitId = zah.getBannerAds(activity)
            val adRequest = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build()
            adView.loadAd(adRequest)

            val rLParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            rLParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1)
            relativeLayout.addView(adView, rLParams)
        }
    }

    fun loadFullScreenAds(context: Context) {
        try {
            if (zu.s(context) && !TextUtils.isEmpty(zah.getInterAdsId(context))) {
                interstitialAd = InterstitialAd(context)
                interstitialAd!!.adUnitId = zah.getInterAdsId(context)
                val adRequest = AdRequest.Builder().build()
                interstitialAd!!.loadAd(adRequest)
                interstitialAd!!.adListener = object : AdListener() {
                    override fun onAdFailedToLoad(p0: Int) {
//                        Toast.makeText(context, "FULL_ADS_ERROR: $p0", Toast.LENGTH_SHORT).show()
                        Log.e("ADS_ERROR", p0.toString())
                        super.onAdFailedToLoad(p0)
                    }

                    override fun onAdLoaded() {
                        showInterstitial()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

//    fun loadVideoRewardedAd(context: Context, callback: RewardedAdCallback){
//        var rewardedAd = RewardedAd(context, "ca-app-pub-3940256099942544/5224354917")
//
//        val adLoadCallback = object : RewardedAdLoadCallback() {
//            override fun onRewardedAdLoaded() {
//                rewardedAd.show(context as Activity, callback)
//            }
//
//            override fun onRewardedAdFailedToLoad(errorCode: Int) {
//                // Ad failed to load.
//            }
//        }
//
//        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
//
////
////        rewardedVideoAd.setAdListener(callback)
////        rewardedVideoAd
//    }


    private fun showInterstitial() {
        if (interstitialAd!!.isLoaded) {
            interstitialAd!!.show()
        }
    }

    lateinit var rewardedVideoAd: RewardedVideoAd

    fun loadVideoRewardedAds(context: Context, listener: OnAdsRewardedListener){
        var dialog = ProgressDialog(context)
        dialog.setIcon(R.mipmap.ic_launcher)
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog.show()

        MobileAds.initialize(context, zah.getVideoAds(context))
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context)
        rewardedVideoAd.rewardedVideoAdListener = object : RewardedVideoAdListener {
            override fun onRewardedVideoAdClosed() {

            }

            override fun onRewardedVideoAdLeftApplication() {

            }

            override fun onRewardedVideoAdLoaded() {
//                initVideoRewardedAds(context)
                dialog.dismiss()
                rewardedVideoAd.show()
            }

            override fun onRewardedVideoAdOpened() {

            }

            override fun onRewarded(p0: RewardItem?) {
                listener.onRewarded(p0)
            }

            override fun onRewardedVideoAdFailedToLoad(p0: Int) {
                dialog.dismiss()
            }

            override fun onRewardedVideoStarted() {

            }
        }

        initVideoRewardedAds(context)

    }

    private fun initVideoRewardedAds(context: Context){
        Log.e("REWARDED_ADS", zah.getVideoAds(context))
        rewardedVideoAd.loadAd(zah.getVideoAds(context), AdRequest.Builder().build())

    }

    interface OnAdsRewardedListener{
        fun onRewarded(p0: RewardItem?)
    }
}