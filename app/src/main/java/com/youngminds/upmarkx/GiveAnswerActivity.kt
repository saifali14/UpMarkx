package com.youngminds.upmarkx

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.storage.FirebaseStorage
import com.youngminds.upmarkx.Models.Answer
import com.youngminds.upmarkx.Models.NotiAnswer
import com.youngminds.upmarkx.Models.Question
import com.youngminds.upmarkx.Utils.AskLoadingbar
import com.youngminds.upmarkx.recyclerview.Communityparentrecy
import kotlinx.android.synthetic.main.activity_give_answer.*
import java.util.*

class GiveAnswerActivity : AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore



    val STORAGE_READ = 101
    val STORAGE_WRITE = 107

    companion object{

        var  imageuri:Uri?=null
        var imageurl:String?=null
        var question: Question?=null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_give_answer)


        firestore = FirebaseFirestore.getInstance()

        val  questionid = intent.getStringExtra(Communityparentrecy.QUESTION_KEY).toString()



        retreivequestion(questionid)






        giveans_attachimage.setOnClickListener {

            checkForPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE,"read storage",STORAGE_READ)

            checkForPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,"write storage",STORAGE_WRITE)

        }



        giveans_backbtn.setOnClickListener {
            finish()

        }

        askans_submitansbtn.setOnClickListener {

            if (askans_answer.text.isNotEmpty()){

            decesion()

            }else{


                Toast.makeText(this,"Please enter the answer",Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }


        }


    }





    private fun checkForPermission(permission:String , name:String , requestCode: Int){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            when{

                ContextCompat.checkSelfPermission(applicationContext,permission) == PackageManager.PERMISSION_GRANTED ->{

                    Toast.makeText(applicationContext,"$name permission granted",Toast.LENGTH_SHORT).show()

                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent,0)


                }

                shouldShowRequestPermissionRationale(permission) -> showDialog(permission,name,requestCode)

                else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)

            }


        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String){

            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){

                Toast.makeText(applicationContext,"$name permission refused",Toast.LENGTH_SHORT).show()

            }else{

                Toast.makeText(applicationContext,"$name permission granted",Toast.LENGTH_SHORT).show()

                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent,0)
            }


        }


        when(requestCode){

            STORAGE_READ -> innerCheck("read storage")

            STORAGE_WRITE -> innerCheck("write storage")

        }


    }


    private fun showDialog(permission: String, name:String, requestCode: Int){



        val builder = AlertDialog.Builder(this)

        builder.apply {

            setMessage("Permission to access your $name is required to use this app")
            setTitle("Permission required")
            setPositiveButton("Ok"){
                    dialog , which ->


                ActivityCompat.requestPermissions(this@GiveAnswerActivity, arrayOf(permission),requestCode)
            }

        }

        val dialog = builder.create()
        dialog.show()


    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode==0 && resultCode == Activity.RESULT_OK && data!=null){

            imageuri = data.data!!

            giveans_imagepreview.setImageURI(imageuri)
        }
    }



    private fun retreivequestion(questionid:String){


        val db = firestore.collection("CommunityQuestions")
            .whereEqualTo("questionid", questionid)


        db.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {



                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){


                        question = dc.document.toObject(Question::class.java)

                        giveans_question.text  = question?.question
                    }

                }



            }


        })


    }


    private fun decesion(){

        if (imageuri == null){

            submitanswer()

        }else{

            uploadimagetostorage()

        }



    }



    private fun uploadimagetostorage() {


        val uid = UUID.randomUUID()


        val dbstorage = FirebaseStorage.getInstance().getReference("AnswerImages").child(uid.toString())
        dbstorage.putFile(imageuri!!).addOnSuccessListener {

            Log.d("Info","user image uploaded")
            dbstorage.downloadUrl.addOnSuccessListener {

                imageurl = it.toString()

                /// submitting answer

                submitanswer()






            }.addOnFailureListener{
                Log.d("Info","failed to upload image")
            }

        }



    }
    private fun submitanswer() {

        val answerid = UUID.randomUUID().toString()

        val answertext = askans_answer.text.toString()
        val currenuseruid =  FirebaseAuth.getInstance().uid.toString()

        val date = System.currentTimeMillis()

        val db = firestore.collection("CommunityQuestions")
            .document(question?.questionid.toString())
            .collection("Answers")
            .document(answerid)

        val answer = Answer(answerid, question?.questionid,answertext,currenuseruid,
            question?.useruid, imageurl,
            question?.question,"False",date,)


        val notification = NotiAnswer(question?.useruid,answerid,currenuseruid, question?.questionid,
            question?.question,date,false,"NotifyAnswer")

        db.set(answer).addOnSuccessListener {

            Toast.makeText(this,"Your answer is uploaded",Toast.LENGTH_SHORT).show()
            finish()

        }


        val notifydb = firestore.collection("UserInfo")
            .document(question?.useruid.toString())
            .collection("Notifications")
            .document(answerid)

        notifydb.set(notification)

        val dbuseranssaved = firestore.collection("UserInfo")
            .document(currenuseruid)
            .collection("Answers")
            .document(answerid)

        dbuseranssaved.set(answer)


    }






}