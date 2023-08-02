package com.youngminds.upmarkx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.Answer
import com.youngminds.upmarkx.recyclerview.savedrecy
import kotlinx.android.synthetic.main.activity_saved_answers.*

class SavedAnswersActivity : AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore

    val adaptar = GroupAdapter<ViewHolder>()

    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_answers)


        firestore = FirebaseFirestore.getInstance()






        // interstiall ad

        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-2181621433616722/5697493707", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("Webview", adError.message)
                InterstitialAd.load(this@SavedAnswersActivity,"ca-app-pub-2181621433616722/5697493707", adRequest, object : InterstitialAdLoadCallback() {
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

                InterstitialAd.load(this@SavedAnswersActivity,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
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











        savedans_recy.adapter = adaptar



        saved_backbtn.setOnClickListener {


            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }

            finish()
        }

     retreivesavedans()

    }

    private fun retreivesavedans() {

        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

        val db = firestore.collection("UserInfo")
            .document(currentuseruid)
            .collection("SavedAnswers")


        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(applicationContext,"Something went wrong",Toast.LENGTH_SHORT).show()


                }

                for (dc:DocumentChange in value?.documentChanges!!){



                    if (dc.type == DocumentChange.Type.ADDED){

                        val answer = dc.document.toObject(Answer::class.java)


                        adaptar.add(savedrecy(this@SavedAnswersActivity,answer))


                    }

                }


            }


        })


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