package com.youngminds.upmarkx.recyclerview

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.*
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.GiveAnswerActivity
import com.youngminds.upmarkx.Models.Question
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.R
import com.youngminds.upmarkx.SearchActivity
import com.youngminds.upmarkx.UserProfileActivity
import com.youngminds.upmarkx.Utils.TimeStamp
import kotlinx.android.synthetic.main.home_questionscard.view.*

class home_questionrecy(val context: Context, val question:Question):Item<ViewHolder>() {

    private lateinit var firestore: FirebaseFirestore


    companion object{

        var user:User?=null

    }

    override fun bind(viewHolder: ViewHolder, position: Int) {


        firestore = FirebaseFirestore.getInstance()



        val db = firestore.collection("UserInfo")
            .whereEqualTo("uid",question.useruid)

        db.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){


                         user = dc.document.toObject(User::class.java)


                        viewHolder.itemView.home_questionnameview.text = "${user?.firstname} ${user?.lastname}"
                        viewHolder.itemView.home_questionclass.setText(user?.standard)
                        Glide.with(context).load(user?.imageuri).into(viewHolder.itemView.home_questionimageview)


                        if (user?.verified == "true"){

                            viewHolder.itemView.home_questionverified.visibility = View.VISIBLE

                        }else{

                            viewHolder.itemView.home_questionverified.visibility = View.GONE
                        }

                    }

                }


            }


        })




        viewHolder.itemView.home_question_question.setOnLongClickListener {



            val clipboardmanager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text",question.question)
            clipboardmanager.setPrimaryClip(clipData)



            Toast.makeText(context,"Question copied to Clipboard",Toast.LENGTH_SHORT).show()

            true
        }

        viewHolder.itemView.home_question_question.text = question.question
        viewHolder.itemView.home_question_time.text = TimeStamp.getTimeAgo(question.date!!.toLong())



        viewHolder.itemView.home_question_giveanscard.setOnClickListener {


            val intent = Intent(context,GiveAnswerActivity::class.java)
            intent.putExtra(Communityparentrecy.QUESTION_KEY,question.questionid)
            context.startActivity(intent)



        }


        viewHolder.itemView.home_questionimageview.setOnClickListener {


            val intent =Intent(context,UserProfileActivity::class.java)
            intent.putExtra(SearchActivity.USERK, user)
            context.startActivity(intent)

        }




    }

    override fun getLayout(): Int {

        return R.layout.home_questionscard
    }
}