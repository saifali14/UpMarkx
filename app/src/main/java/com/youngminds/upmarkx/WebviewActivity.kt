package com.youngminds.upmarkx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.youngminds.upmarkx.recyclerview.Newsrecy
import kotlinx.android.synthetic.main.activity_webview.*

class WebviewActivity : AppCompatActivity() {



    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)


        val link = intent.getStringExtra(Newsrecy.WEB_KEY).toString()


        web_linkview.text = link




        // interstiall ad

        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-2181621433616722/5168393558", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("Webview", adError.message)

                InterstitialAd.load(this@WebviewActivity,"ca-app-pub-2181621433616722/5168393558", adRequest, object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d("Webview", adError.message)
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d("Webview", "Ad was loaded.")
                        mInterstitialAd = interstitialAd
                    }
                })

            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("Webview", "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })




        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("Webview", "Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {

                InterstitialAd.load(this@WebviewActivity,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d("Webview", adError.message)
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d("Webview", "Ad was loaded.")
                        mInterstitialAd = interstitialAd
                    }
                })

                Log.d("Webview", "Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("Webview", "Ad showed fullscreen content.")
                mInterstitialAd = null
            }
        }


        // banner ad

        MobileAds.initialize(this) {}


        val adRequestb = AdRequest.Builder().build()
        webview_adview_banner.loadAd(adRequestb)


        webview_adview_banner.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.

                super.onAdLoaded()
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.

                super.onAdFailedToLoad(adError)
                webview_adview_banner.loadAd(adRequestb)

                adError.cause?.let { Log.e("Webview", it.toString()) }
                Log.e("Webview",adError.code.toString())
                Log.e("Webview",adError.message)
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.

                super.onAdOpened()
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.

                super.onAdClicked()
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.

                super.onAdClosed()
            }
        }

        web_cancelbtn.setOnClickListener {


            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }


            finish()

        }



        webview.loadUrl(link)

        val webSettings = webview.settings

        webSettings.javaScriptEnabled

    }


    override fun onBackPressed() {
        super.onBackPressed()


        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }
}