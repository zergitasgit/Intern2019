package com.swipe.navigationbar

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

    interface RewardedVideoListener {
        fun onRewarded(rewardItem: RewardItem)

        fun onRewardedVideoAdClosed()

        fun onRewardedVideoAdFailedToLoad(i: Int)

        fun onRewardedVideoAdLeftApplication()

        fun onRewardedVideoAdLoaded()

        fun onRewardedVideoAdOpened()

        fun onRewardedVideoStarted()
    }

    fun initBanner(
        activity: Activity,
        relativeLayout: RelativeLayout,
        onAdsListener: OnAdsListener
    ) {
        try {
            if (zu.s(activity) && !TextUtils.isEmpty(zah.getBannerAds(activity))) {
                val adView = AdView(activity)
                adView.adSize = AdSize.BANNER
                adView.adUnitId = zah.getBannerAds(activity)
                val adRequest = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build()
                adView.adListener = object : AdListener() {
                    override fun onAdFailedToLoad(i: Int) {
                        onAdsListener.onError()
                        Log.e("DEBUG", "$i")
                        super.onAdFailedToLoad(i)
                    }

                    override fun onAdLoaded() {
                        Log.e("DEBUG", "onloaded")

                        onAdsListener.onAdLoaded()
                        super.onAdLoaded()
                    }

                    override fun onAdOpened() {
                        Log.e("DEBUG", "open")

                        onAdsListener.onAdOpened()
                        super.onAdOpened()
                    }

                    override fun onAdClosed() {
                        Log.e("DEBUG", "cloese")

                        onAdsListener.onAdClose()
                        super.onAdClosed()
                    }
                }
                adView.loadAd(adRequest)

                val rLParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                rLParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1)
                relativeLayout.addView(adView, rLParams)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun loadBannerAds(mActivity: Activity, layoutAds: RelativeLayout) {
        Ads.initBanner(mActivity, layoutAds, object : OnAdsListener {
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
            val adRequest = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build()
            adView.loadAd(adRequest)

            val rLParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
            )
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
                    override fun onAdLoaded() {
                        showInterstitial()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun showInterstitial() {
        if (interstitialAd!!.isLoaded) {
            interstitialAd!!.show()
        }
    }

    lateinit var rewardedVideoAd: RewardedVideoAd


    private fun initVideoRewardedAds(context: Context) {
        rewardedVideoAd.loadAd(zah.getVideoAds(context), AdRequest.Builder().build())

    }

    interface OnAdsRewardedListener {
        fun onRewarded(p0: RewardItem?)
    }
}
