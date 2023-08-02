package com.youngminds.upmarkx.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.Answer
import com.youngminds.upmarkx.Models.Question
import com.youngminds.upmarkx.MoreanswersActivity
import com.youngminds.upmarkx.R
import com.youngminds.upmarkx.communitysuggestionrecy
import com.youngminds.upmarkx.recyclerview.Communitychildrecy.Companion.MORE_QUESTION
import com.youngminds.upmarkx.recyclerview.Communityparentrecy
import kotlinx.android.synthetic.main.activity_community.community_refresh
import kotlinx.android.synthetic.main.activity_community.community_searchview
import kotlinx.android.synthetic.main.activity_community.view.*
import kotlinx.android.synthetic.main.fragment_doubts.*

class DoubtsFragment : Fragment() {


    val adaptar = GroupAdapter<ViewHolder>()
    val suggestionadaptar = GroupAdapter<ViewHolder>()

    private lateinit var firestore: FirebaseFirestore

    companion object{

        var question: Question?=null
        var answer: Answer?=null
        var subjectid:String?=null
        var questiontext:String?=null

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_doubts, container, false)



//        val lac = LayoutAnimationController(AnimationUtils.loadAnimation(requireContext(),R.anim.layout_animation))
//        lac.delay = 0.20f
//        lac.order = LayoutAnimationController.ORDER_NORMAL
//        view.community_recyclerview.layoutAnimation = lac


        firestore = FirebaseFirestore.getInstance()

        view.community_recyclerview.adapter  =adaptar

        view.community_suggestion_recyclerview.adapter = suggestionadaptar





        adaptar.setOnItemClickListener { item, view ->


            val question = item as Communityparentrecy

            val intent = Intent(context, MoreanswersActivity::class.java)
//            intent.putExtra(MORE_ANS,answer)
            intent.putExtra(MORE_QUESTION,question.question.questionid)
            startActivity(intent)


        }


        retreivequestions()


//        view.community_askquestionbtn.setOnClickListener{
//
//            val intent = Intent(activity, AskQuestionActivity::class.java)
//            startActivity(intent)
//
//
//
//        }

        suggestionadaptar.setOnItemClickListener { item, view ->


            val question = item as communitysuggestionrecy

            retreiveparticularq(question.question)

        }



        view.community_refresh.setOnRefreshListener {

            retreivequestions()

            community_refresh.isRefreshing =false




        }

        view.community_searchview.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                suggestionadaptar.clear()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                suggestionadaptar.clear()

                val seaerchuser = firestore.collection("CommunityQuestions").whereArrayContains("keywords",s?.trim().toString().toLowerCase()).limit(15)
                seaerchuser.addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {


                        if (error!=null){

                            Toast.makeText(activity,"Network issue is there",
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
        view.chip_physics.setOnClickListener {
            physicsclick ++
            if (physicsclick  %  2==0){
                retreivequestions()
            }else{
                particualarquestions("Physics")

            }
        }

        var chemclick = 0
        view.chip_chemistry.setOnClickListener {

            chemclick ++

            if (chemclick  %  2==0){
                retreivequestions()
            }else{
                particualarquestions("Chemistry")

            }


        }

        var bioclick =0
        view.chip_biology.setOnClickListener {


            bioclick ++

            if (bioclick  %  2==0){
                retreivequestions()
            }else{
                particualarquestions("Biology")

            }

        }

        var histclick =0
        view.chip_history.setOnClickListener {

            histclick ++

            if (histclick  %  2==0){
                retreivequestions()
            }else{
                particualarquestions("History")

            }
        }
        var geoclick =0
        view.chip_geography.setOnClickListener {

            geoclick ++

            if (geoclick  %  2==0){
                retreivequestions()
            }else{
                particualarquestions("Geography")

            }
        }
        var ecoclick = 0
        view.chip_economics.setOnClickListener {

            ecoclick ++

            if (ecoclick  %  2==0){
                retreivequestions()
            }else{
                particualarquestions("Economics")

            }
        }

        var polclick =0
        view.chip_politicalscience.setOnClickListener {


            polclick ++

            if (polclick  %  2==0){
                retreivequestions()
            }else{
                particualarquestions("Political Science")

            }
        }

        var hindiclick =0
        view.chip_hindi.setOnClickListener {

            hindiclick ++

            if (hindiclick  %  2==0){
                retreivequestions()
            }else{
                particualarquestions("Hindi")

            }
        }

        var engclick = 0
        view.chip_english.setOnClickListener {

            engclick ++

            if (engclick  %  2==0){
                retreivequestions()
            }else{
                particualarquestions("English")

            }
        }

        var mathclick =0
        view.chip_maths.setOnClickListener {

            mathclick ++

            if (mathclick %  2==0){
                retreivequestions()
            }else{
                particualarquestions("Maths")

            }
        }

        var recentclick = 0
        view.chip_recents.setOnClickListener {

            recentclick ++

            if (recentclick  %  2==0){
                retreivequestions()
            }else{
                recentquestions()

            }

        }




        return   view
    }



    private fun retreiveparticularq(questions: Question) {


        adaptar.clear()

        val dbq = firestore.collection("CommunityQuestions").whereArrayContains("keywords",
            questions.question!!
        )

        dbq.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(activity,"Something went wrong",Toast.LENGTH_SHORT).show()
                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        question = dc.document.toObject(Question::class.java)
                        adaptar.add(Communityparentrecy(activity!!, question!!))

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

                    Toast.makeText(activity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in  value?.documentChanges!!){


                    if (dc.type ==  DocumentChange.Type.ADDED){

                        question = dc.document.toObject(Question::class.java)
                        adaptar.add(Communityparentrecy(activity!!, question!!))


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

                    Toast.makeText(activity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in  value?.documentChanges!!){


                    if (dc.type ==  DocumentChange.Type.ADDED){


                        question = dc.document.toObject(Question::class.java)
                        adaptar.add(Communityparentrecy(activity!!, question!!))


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

                    Toast.makeText(activity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in  value?.documentChanges!!){


                    if (dc.type ==  DocumentChange.Type.ADDED){


                        question = dc.document.toObject(Question::class.java)
                        adaptar.add(Communityparentrecy(activity!!, question!!))


                    }

                }

                doubtfrag_shrimmer.stopShimmer()
                doubtfrag_shrimmer.visibility = View.GONE


            }


        })


    }




}