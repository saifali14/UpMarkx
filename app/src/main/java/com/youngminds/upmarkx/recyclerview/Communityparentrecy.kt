package com.youngminds.upmarkx.recyclerview

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.GiveAnswerActivity
import com.youngminds.upmarkx.Models.Answer
import com.youngminds.upmarkx.Models.Question
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.R
import com.youngminds.upmarkx.Utils.TimeStamp
import kotlinx.android.synthetic.main.communityparentcard.view.*

class Communityparentrecy(val context: Context , val question: Question):Item<ViewHolder>() {

    private lateinit var firestore: FirebaseFirestore
    val  childadaptar =  GroupAdapter<ViewHolder>()
    companion object{

        var user: User?=null
        var answer: Answer?=null

        val QUESTION_KEY = "QUESTION_KEY"


    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        firestore = FirebaseFirestore.getInstance()

        val db = firestore.collection("UserInfo").whereEqualTo("uid",question.useruid)
        db.addSnapshotListener(object :  EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {




                viewHolder.itemView.communityparentcard_subject.text = question.subject

                viewHolder.itemView.communitychild_recycleview.adapter = childadaptar

                val date = question.date?.let { TimeStamp.getTimeAgo(it) }

                viewHolder.itemView.communityparentcard_date.text = " ($date)"



                if (error!=null){

                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()

                }


//                if(viewHolder.itemView.communitychild_recycleview.isEmpty()){
//
//
//                    viewHolder.itemView.communityparentcard_noanstext.visibility = View.VISIBLE
//
//                }else{
//
//                    viewHolder.itemView.communityparentcard_noanstext.visibility = View.GONE
//
//                }

                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){

                        user = dc.document.toObject(User::class.java)
                        Glide.with(context).load(user?.imageuri).into(viewHolder.itemView.communityparentcard_uerimage)
                        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

                        if (user?.verified =="true"){

                            viewHolder.itemView.communityparentcard_verified.visibility = View.VISIBLE

                        }else{
                            viewHolder.itemView.communityparentcard_verified.visibility = View.GONE

                        }

                        if (user?.uid  == currentuseruid){


                            viewHolder.itemView.communityparentcard_username.text = "${user?.firstname} (You)"
                        }else{


                            viewHolder.itemView.communityparentcard_username.text = "${user?.firstname}"
                        }



                        viewHolder.itemView.communityparentcard_class.setText(user?.standard)


                        viewHolder.itemView.communityparentcard_question.text = "Quest: ${question.question}"
                    }

                }

            }


        })


        if (childadaptar.itemCount == 0) {


            val dbans = firestore.collection("CommunityQuestions")
                .document(question.questionid.toString())
                .collection("Answers")

            dbans.addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    var count = 0

                    if (error != null) {

                        Toast.makeText(
                            context,
                            "There is an issue while fetching the answers",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {


                        if (dc.type == DocumentChange.Type.ADDED) {




                            count++
                            if (count == 1) {

                                answer = dc.document.toObject(Answer::class.java)

                                childadaptar.add(Communitychildrecy(context, answer!!, question))


                            }


                        }
                        if (dc.type == DocumentChange.Type.MODIFIED) {
                            childadaptar.clear()

                            answer = dc.document.toObject(Answer::class.java)
                            childadaptar.add(Communitychildrecy(context, answer!!, question))

                        }

                    }

                }


            })

        }


//        if(count==0){
//
//            viewHolder.itemView.communityparentcard_seebtn.visibility = View.GONE
//            viewHolder.itemView.communityparentcard_giveansbtn.visibility = View.VISIBLE
//
//        }else{
//
//            viewHolder.itemView.communityparentcard_seebtn.visibility = View.VISIBLE
//            viewHolder.itemView.communityparentcard_giveansbtn.visibility = View.GONE
//
//        }



        viewHolder.itemView.communityparentcard_giveansbtn.setOnClickListener {


            val intent = Intent(context, GiveAnswerActivity::class.java)
            intent.putExtra(QUESTION_KEY,question.questionid)
            context.startActivity(intent)



        }



        viewHolder.itemView.communityparentcard_question.setOnLongClickListener {



            val clipboardmanager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text",question.question)
            clipboardmanager.setPrimaryClip(clipData)



            Toast.makeText(context,"Question copied to Clipboard",Toast.LENGTH_SHORT).show()

            true
        }





        viewHolder.itemView.communityparentcard_seebtn.setOnClickListener {


            if (viewHolder.itemView.communityparentcard_expandablelayout.visibility == View.GONE) {

                TransitionManager.beginDelayedTransition(
                    viewHolder.itemView.communityparentcard,
                    AutoTransition()
                )
                viewHolder.itemView.communityparentcard_expandablelayout.visibility = View.VISIBLE
                viewHolder.itemView.communityparentcard_seebtn.visibility = View.GONE
                viewHolder.itemView.communityparentcard_showlessbtn.visibility = View.VISIBLE

            }


            viewHolder.itemView.communityparentcard_showlessbtn.setOnClickListener {
                TransitionManager.beginDelayedTransition(
                    viewHolder.itemView.communityparentcard,
                    AutoTransition()
                )
                viewHolder.itemView.communityparentcard_expandablelayout.visibility = View.GONE
                viewHolder.itemView.communityparentcard_seebtn.visibility = View.VISIBLE

            }






        }







    }

    override fun getLayout(): Int {

        return R.layout.communityparentcard
    }




}