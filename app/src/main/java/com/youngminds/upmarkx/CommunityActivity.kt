package com.youngminds.upmarkx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.Answer
import com.youngminds.upmarkx.Models.Question
import com.youngminds.upmarkx.Models.User

import com.youngminds.upmarkx.recyclerview.Communityparentrecy

import kotlinx.android.synthetic.main.activity_community.*


class CommunityActivity : AppCompatActivity() {


    val adaptar = GroupAdapter<ViewHolder>()
    val suggestionadaptar = GroupAdapter<ViewHolder>()

    private lateinit var firestore: FirebaseFirestore

    companion object{

        var question: Question?=null
        var answer: Answer?=null
        var subjectid:String?=null
        var questiontext:String?=null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        firestore = FirebaseFirestore.getInstance()

        community_recyclerview.adapter  =adaptar

        community_suggestion_recyclerview.adapter = suggestionadaptar


        retreivequestions()


        community_askquestionbtn.setOnClickListener{

            val intent = Intent(this,AskQuestionActivity::class.java)
            startActivity(intent)



        }

        suggestionadaptar.setOnItemClickListener { item, view ->


            val question = item as communitysuggestionrecy

            retreiveparticularq(question.question)

        }



        community_refresh.setOnRefreshListener {

            retreivequestions()

            community_refresh.isRefreshing =false




        }

        community_searchview.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                suggestionadaptar.clear()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                suggestionadaptar.clear()

                val seaerchuser = firestore.collection("CommunityQuestions").whereArrayContains("keywords",s?.trim().toString().toLowerCase()).limit(10)
                seaerchuser.addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {


                        if (error!=null){

                            Toast.makeText(this@CommunityActivity,"Network issue is there",
                                Toast.LENGTH_SHORT).show()


                        }

                        for (dc: DocumentChange in value?.documentChanges!!){


                            if (dc.type == DocumentChange.Type.ADDED){

                                val questions = dc.document.toObject(Question::class.java)

                                suggestionadaptar.add(communitysuggestionrecy(questions))

                            }

                        }


                    }


                })



            }

            override fun afterTextChanged(s: Editable?) {

            }


        })






        var physicsclick = 0
        chip_physics.setOnClickListener {
            physicsclick ++
            if (physicsclick  %  2==0){
                retreivequestions()
            }else{
                particualarquestions("Physics")

            }
        }

        var chemclick = 0
        chip_chemistry.setOnClickListener {

            chemclick ++

            if (chemclick  %  2==0){
                retreivequestions()
            }else{
                particualarquestions("Chemistry")

            }


        }

        var bioclick =0
        chip_biology.setOnClickListener {


            bioclick ++

            if (bioclick  %  2==0){
                retreivequestions()
            }else{
                particualarquestions("Biology")

            }

        }

        var histclick =0
        chip_history.setOnClickListener {

            histclick ++

            if (histclick  %  2==0){
                retreivequestions()
            }else{
                particualarquestions("History")

            }
        }
        var geoclick =0
        chip_geography.setOnClickListener {

            geoclick ++

            if (geoclick  %  2==0){
                retreivequestions()
            }else{
                particualarquestions("Geography")

            }
        }
        var ecoclick = 0
        chip_economics.setOnClickListener {

            ecoclick ++

            if (ecoclick  %  2==0){
                retreivequestions()
            }else{
                particualarquestions("Economics")

            }
        }

        var polclick =0
        chip_politicalscience.setOnClickListener {


            polclick ++

            if (polclick  %  2==0){
                retreivequestions()
            }else{
                particualarquestions("Political Science")

            }
        }

        var hindiclick =0
        chip_hindi.setOnClickListener {

            hindiclick ++

            if (hindiclick  %  2==0){
                retreivequestions()
            }else{
                particualarquestions("Hindi")

            }
        }

        var engclick = 0
        chip_english.setOnClickListener {

            engclick ++

            if (engclick  %  2==0){
                retreivequestions()
            }else{
                particualarquestions("English")

            }
        }

        var mathclick =0
        chip_maths.setOnClickListener {

            mathclick ++

            if (mathclick %  2==0){
                retreivequestions()
            }else{
                particualarquestions("Maths")

            }
        }

        var recentclick = 0
        chip_recents.setOnClickListener {

            recentclick ++

            if (recentclick  %  2==0){
                retreivequestions()
            }else{
               recentquestions()

            }

        }












    }




    private fun retreiveparticularq(questions: Question) {


        adaptar.clear()

        val dbq = firestore.collection("CommunityQuestions").whereArrayContains("keywords",
            questions.question!!
        )

        dbq.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(this@CommunityActivity,"Something went wrong",Toast.LENGTH_SHORT).show()
                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        question = dc.document.toObject(Question::class.java)
                        adaptar.add(Communityparentrecy(this@CommunityActivity, question!!))

                        suggestionadaptar.clear()

                    }

                }


            }


        })

    }


    private fun recentquestions(){


        adaptar.clear()

        val db  = firestore.collection("CommunityQuestions").orderBy("date",Query.Direction.DESCENDING)
        db.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(this@CommunityActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in  value?.documentChanges!!){


                    if (dc.type ==  DocumentChange.Type.ADDED){


                        question = dc.document.toObject(Question::class.java)
                        adaptar.add(Communityparentrecy(this@CommunityActivity, question!!))


                    }

                }


            }


        })

    }

    private fun particualarquestions(subject:String){

        adaptar.clear()
        community_searchview.text.clear()
        suggestionadaptar.clear()


        val db  = firestore.collection("CommunityQuestions").whereEqualTo("subject",subject)
        db.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(this@CommunityActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in  value?.documentChanges!!){


                    if (dc.type ==  DocumentChange.Type.ADDED){


                        question = dc.document.toObject(Question::class.java)
                        adaptar.add(Communityparentrecy(this@CommunityActivity, question!!))


                    }

                }


            }


        })

    }

    private fun retreivequestions() {

        adaptar.clear()
        val db  = firestore.collection("CommunityQuestions")
        db.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(this@CommunityActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in  value?.documentChanges!!){


                    if (dc.type ==  DocumentChange.Type.ADDED){


                            question = dc.document.toObject(Question::class.java)
                            adaptar.add(Communityparentrecy(this@CommunityActivity, question!!))


                    }

                }


            }


        })


    }

    private fun retreiveanswer(question: Question) {

        val dbans = firestore.collection("CommunityQuestions")
            .document(question.useruid.toString())
            .collection("Answers")

        dbans.addSnapshotListener(object :  EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(this@CommunityActivity,"Error while fetching the answers",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){


                        answer = dc.document.toObject(Answer::class.java)



                    }

                }


            }


        })


    }



    override fun onPause() {
        super.onPause()

        val currentuseid = FirebaseAuth.getInstance().uid.toString()

        val  userstatus = firestore.collection("UserInfo").document(currentuseid)
        userstatus.update("status","Offline")

    }


    override fun onResume() {
        super.onResume()

        val currentuseid = FirebaseAuth.getInstance().uid.toString()

        val  userstatus = firestore.collection("UserInfo").document(currentuseid)
        userstatus.update("status","Online")
    }

}