package com.youngminds.upmarkx.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.youngminds.upmarkx.Models.Question
import com.youngminds.upmarkx.R
import kotlinx.android.synthetic.main.activity_ask_question.*
import kotlinx.android.synthetic.main.fragment_ask_doubt.view.*
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.util.*


class AskDoubtFragment : Fragment() {


    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var firestore: FirebaseFirestore
    private var mRewardedAd: RewardedAd? = null


    companion object{

        var  subject:String?=null
         val  TAG = "MainActivity"

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_ask_doubt, container, false)



        firestore = FirebaseFirestore.getInstance()



        // rewarded ads
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(requireContext(),"ca-app-pub-2181621433616722/7411413517", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.message)


                val adRequestfailed = AdRequest.Builder().build()

                RewardedAd.load(requireContext(),"ca-app-pub-2181621433616722/7411413517", adRequestfailed, object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(TAG, adError.message)
                        mRewardedAd = null
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        Log.d(TAG, "Ad was loaded.")
                        mRewardedAd = rewardedAd
                    }
                })

            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d(TAG, "Ad was loaded.")
                mRewardedAd = rewardedAd
            }
        })



//
//
//        // interstiall ad
//        var adRequestinter = AdRequest.Builder().build()
//
//        InterstitialAd.load(requireContext(),"ca-app-pub-3940256099942544/1033173712", adRequestinter, object : InterstitialAdLoadCallback() {
//            override fun onAdFailedToLoad(adError: LoadAdError) {
//                Log.d("Webview", adError.message)
//                mInterstitialAd = null
//            }
//
//            override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                Log.d("Webview", "Ad was loaded.")
//                mInterstitialAd = interstitialAd
//            }
//        })
//
//
//
//        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
//            override fun onAdDismissedFullScreenContent() {
//                Log.d("Webview", "Ad was dismissed.")
//            }
//
//            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
//
//                InterstitialAd.load(requireContext(),"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
//                    override fun onAdFailedToLoad(adError: LoadAdError) {
//                        Log.d("Webview", adError.message)
//                        mInterstitialAd = null
//                    }
//
//                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                        Log.d("Webview", "Ad was loaded.")
//                        mInterstitialAd = interstitialAd
//                    }
//                })
//
//                Log.d("Webview", "Ad failed to show.")
//            }
//
//            override fun onAdShowedFullScreenContent() {
//                Log.d("Webview", "Ad showed fullscreen content.")
//                mInterstitialAd = null
//            }
//        }
//
//





        view.subject_selector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {


            subject = parent?.getItemAtPosition(position).toString()



            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }


        }



        view.askquestion_askbtn.setOnClickListener {



            sendquestion()


        }

        return view


    }

    private fun sendquestion() {


        if (askquestion_edxt.text.isEmpty()){
            Toast.makeText(activity,"Please write down the question", Toast.LENGTH_SHORT).show()
            return

        }

        val questiontext = askquestion_edxt.text.trim().toString().toLowerCase()
        val time = System.currentTimeMillis()
        val useruid = FirebaseAuth.getInstance().uid.toString()
        val  questionid = UUID.randomUUID().toString()



        val db = firestore.collection("CommunityQuestions").document(questionid)

        val userdb = firestore.collection("UserInfo").document(useruid)
            .collection("Questions").document(questionid)


        val question = Question(questionid,useruid, questiontext,time, subject,keywords(questiontext.trim()))




        db.set(question).addOnSuccessListener {



            userdb.set(question).addOnSuccessListener {




                // rewarded show

                if (mRewardedAd != null) {
                    mRewardedAd?.show(requireActivity(), OnUserEarnedRewardListener() {
                        fun onUserEarnedReward(rewardItem: RewardItem) {


                            Log.d(TAG, "User earned the reward.")
                        }
                    })
                } else {
                    Log.d(TAG, "The rewarded ad wasn't ready yet.")
                }



//
//                if (mInterstitialAd != null) {
//                    mInterstitialAd?.show(requireActivity())
//                } else {
//                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
//                }





                askquestion_edxt.text.clear()
                MotionToast.createToast(requireActivity()
                ,"Question Published!"
                ,"Your question is successfully published"
                ,MotionToastStyle.SUCCESS
                ,MotionToast.GRAVITY_BOTTOM
                ,MotionToast.LONG_DURATION
                ,ResourcesCompat.getFont(requireContext(),R.font.lato_bold))




            }


        }



    }


    private fun keywords(question:String):List<String>{

        val keywords = mutableListOf<String>()
        for ( i in 0 until question.length){

            for (j in (i+1)..question.length){

                keywords.add(question.slice(i until j))

            }

        }
//        Toast.makeText(this,"${keywords[0]}, ${keywords[1]}, ${keywords[2]}, ${keywords[3]}",Toast.LENGTH_SHORT).show()

        return keywords



    }


}