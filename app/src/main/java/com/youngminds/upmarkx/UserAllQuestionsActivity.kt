package com.youngminds.upmarkx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.Question
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.recyclerview.Communitychildrecy
import com.youngminds.upmarkx.recyclerview.userallquestionsrecy
import kotlinx.android.synthetic.main.activity_user_all_questions.*

class UserAllQuestionsActivity : AppCompatActivity() {

  private  val adaptar = GroupAdapter<ViewHolder>()
    private lateinit var firestore: FirebaseFirestore


    companion object{

        var user:User?=null

    }


    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_all_questions)



        firestore = FirebaseFirestore.getInstance()

        user = intent.getParcelableExtra(SearchActivity.USERK)




        // interstiall ad

        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-2181621433616722/1295214268", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("Webview", adError.message)

                InterstitialAd.load(this@UserAllQuestionsActivity,"ca-app-pub-2181621433616722/1295214268", adRequest, object : InterstitialAdLoadCallback() {
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

                InterstitialAd.load(this@UserAllQuestionsActivity,"ca-app-pub-2181621433616722/1295214268", adRequest, object : InterstitialAdLoadCallback() {
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




        adaptar.setOnItemClickListener { item, _ ->



            val question = item  as userallquestionsrecy

            val intent = Intent(this, MoreanswersActivity::class.java)
//            intent.putExtra(MORE_ANS,answer)
            intent.putExtra(Communitychildrecy.MORE_QUESTION,question.questions.questionid)
           startActivity(intent)
        }



        askedquestions_backbtn.setOnClickListener {



            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }
            finish()

        }


        userallquestions_askedquestion_recyclerview.adapter =adaptar


        retreivequestions()

    }

    private fun retreivequestions() {

        val db = firestore.collection("UserInfo")
            .document(user?.uid.toString())
            .collection("Questions")
            .orderBy("date",Query.Direction.DESCENDING)


        db.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(this@UserAllQuestionsActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type ==DocumentChange.Type.ADDED){



                        userallquestions_lottie.visibility = View.GONE

                        val questions = dc.document.toObject(Question::class.java)
                        adaptar.add(userallquestionsrecy(questions))

                    }else{
                        userallquestions_lottie.visibility = View.VISIBLE

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