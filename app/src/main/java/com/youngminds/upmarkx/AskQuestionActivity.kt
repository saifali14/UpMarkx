package com.youngminds.upmarkx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.youngminds.upmarkx.Models.Question
import kotlinx.android.synthetic.main.activity_ask_question.*
import java.util.*

class AskQuestionActivity : AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore

    companion object{

        var  subject:String?=null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask_question)


        firestore = FirebaseFirestore.getInstance()

        subject_selector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {


                subject = parent?.getItemAtPosition(position).toString()



            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }


        }




        askquestion_askbtn.setOnClickListener {


            sendquestion()


        }




    }

    private fun sendquestion() {


        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

        if (askquestion_edxt.text.isEmpty()){
            Toast.makeText(this,"Please write down the question",Toast.LENGTH_SHORT).show()
            return

        }

        val questiontext = askquestion_edxt.text.trim().toString().toLowerCase()
        val time = System.currentTimeMillis()
        val useruid = FirebaseAuth.getInstance().uid.toString()
        val  questionid = UUID.randomUUID().toString()



        val db = firestore.collection("CommunityQuestions").document(questionid)
        val userdb = firestore.collection("UserInfo").document(currentuseruid)
            .collection("Questions").document(questionid)



        val question = Question(questionid,useruid, questiontext,time, subject,keywords(questiontext.trim()))




        db.set(question).addOnSuccessListener {


            }.addOnFailureListener{

                Log.e("AskQuestion","here is the reason " +it.message.toString())

            }



        userdb.set(question).addOnSuccessListener {
            Toast.makeText(this,"Question is uploaded",Toast.LENGTH_SHORT).show()
            finish()


        }

//        userdb.set(question)



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