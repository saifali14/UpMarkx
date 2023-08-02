package com.youngminds.upmarkx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.Answer
import com.youngminds.upmarkx.Models.Question
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.recyclerview.Communitychildrecy
import com.youngminds.upmarkx.recyclerview.Communityparentrecy
import com.youngminds.upmarkx.recyclerview.Moreansrecy
import kotlinx.android.synthetic.main.activity_moreanswers.*

class MoreanswersActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    val adaptar = GroupAdapter<ViewHolder>()

    private var mInterstitialAd: InterstitialAd? = null
    companion object{

        var answer: Answer?=null
        var user: User?=null
//        var question: Question?=null
        var answers: Answer?=null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moreanswers)

        firestore = FirebaseFirestore.getInstance()


        // interstiall ad

        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-2181621433616722/9281515495", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("Webview", adError.message)
                InterstitialAd.load(this@MoreanswersActivity,"ca-app-pub-2181621433616722/9281515495", adRequest, object : InterstitialAdLoadCallback() {
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

                InterstitialAd.load(this@MoreanswersActivity,"ca-app-pub-2181621433616722/9281515495", adRequest, object : InterstitialAdLoadCallback() {
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








        moreans_recyclerview.adapter = adaptar

//        answer = intent.getParcelableExtra(Communitychildrecy.MORE_ANS)
        val questionid = intent.getStringExtra(Communitychildrecy.MORE_QUESTION)


        moreans_giveans.setOnClickListener {

            val intent = Intent(this@MoreanswersActivity,GiveAnswerActivity::class.java)
            intent.putExtra(Communityparentrecy.QUESTION_KEY, questionid)
            startActivity(intent)

        }


        Log.e("MoreAnswer",questionid.toString())



        retreivequestions(questionid.toString())




    }


    private  fun retreivequestions(questionid:String){


        val dbans = firestore.collection("CommunityQuestions")
            .whereEqualTo("questionid",questionid)

        dbans.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(this@MoreanswersActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){




                    if (dc.type == DocumentChange.Type.ADDED){

                       val  question = dc.document.toObject(Question::class.java)




                        moreans_subject.text =  question.subject
                        moreans_question.text = question.question
                        retreiveanswer(question)
                        retreiveuser(question)





                    }



                }


            }


        })
    }


//
//    private fun retreivecurrentuser(){
//
//       val currentuseruid = FirebaseAuth.getInstance().uid.toString()
//
//
//        val dbuser = firestore.collection("UserInfo").whereEqualTo("uid",currentuseruid)
//
//        dbuser.addSnapshotListener(object :EventListener<QuerySnapshot>{
//            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
//
//                if (error!=null){
//
//                    Toast.makeText(this@MoreanswersActivity,"Something went wrong",Toast.LENGTH_SHORT).show()
//
//                }
//
//                for (dc:DocumentChange in value?.documentChanges!!){
//
//
//                    if (dc.type == DocumentChange.Type.ADDED){
//
//
//                       val  currentuser = dc.document.toObject(User::class.java)
//
//                        retreiveanswer(currentuser)
//
//
//
//                    }
//
//
//                }
//
//            }
//
//
//        })
//
//
//
//
//    }

    private fun retreiveuser(question: Question) {


        val dbuser = firestore.collection("UserInfo").whereEqualTo("uid", question.useruid)
        dbuser.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(this@MoreanswersActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        user = dc.document.toObject(User::class.java)

                       moreans_username.text = "${user?.firstname} ${user?.lastname}"
                        if (user?.verified == "true"){

                            moreansact_verified.visibility = View.VISIBLE

                        }else{
                            moreansact_verified.visibility = View.GONE

                        }
                        moreansact_class.setText(user?.standard)

                        Glide.with(this@MoreanswersActivity).load(user?.imageuri).into(moreans_imageview)

                    }

                }


            }


        })


    }


    private fun retreiveanswer(question: Question){


        val dbans = firestore.collection("CommunityQuestions")
            .document(question.questionid.toString())
            .collection("Answers").orderBy("date",Query.Direction.DESCENDING)

        dbans.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(this@MoreanswersActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){




                    if (dc.type == DocumentChange.Type.ADDED){

                        answers = dc.document.toObject(Answer::class.java)

                        adaptar.add(Moreansrecy(this@MoreanswersActivity, answers!!,
                            question))


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