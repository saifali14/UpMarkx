package com.youngminds.upmarkx

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.youngminds.upmarkx.Models.Answer
import com.youngminds.upmarkx.recyclerview.Communitychildrecy
import kotlinx.android.synthetic.main.activity_edit_answer.*

class EditAnswerActivity : AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore
    private lateinit var builder:AlertDialog.Builder

    companion object{

        var answertext:String?=null
        var imageuri:String?=null


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_answer)

        builder  = AlertDialog.Builder(this)

        val answer = intent.getParcelableExtra<Answer>(Communitychildrecy.EDIT_ANSWER)


        answertext = answer?.answer

        firestore = FirebaseFirestore.getInstance()

        editans_answer.setText(answertext)

        editans_submitansbtn.setOnClickListener {

         addans(answer as Answer)


        }

        editans_delete.setOnClickListener {


            delete(answer as Answer)

        }

    }

    private fun delete(answer: Answer) {


        builder.setMessage("Are you sure want to remove this Answer")
            .setCancelable(true)
            .setPositiveButton("Yes"){dialogInterface,it ->



                val delete = firestore.collection("UserInfo")
                    .document(answer.userid.toString())
                    .collection("Answers")
                    .document(answer.answerid.toString())

                delete.delete().addOnSuccessListener {

                    val dbdelte =  firestore.collection("CommunityQuestions")
                        .document(answer.questionid.toString())
                        .collection("Answers")
                        .document(answer.answerid.toString())

                    dbdelte.delete().addOnSuccessListener {


                        finish()

                    }

                }


            }
            .setNegativeButton("No"){dialogInterface, it ->

                dialogInterface.cancel()

            }
            .show()

    }

    private fun addans(answer:Answer) {

        answertext = editans_answer.text.toString()



        val dbuserans = firestore.collection("UserInfo")
            .document(answer.userid.toString())
            .collection("Answers")
            .document(answer.answerid.toString())

        dbuserans.update("answer", answertext)
        dbuserans.update("edited","True")



        val db =  firestore.collection("CommunityQuestions")
            .document(answer.questionid.toString())
            .collection("Answers")
            .document(answer.answerid.toString())

        db.update("answer", answertext)
        db.update("edited","True").addOnSuccessListener {


            finish()

        }

    }


}