package com.youngminds.upmarkx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.Chapter
import com.youngminds.upmarkx.Models.Chapters
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.recyclerview.Chaptersrecy

import kotlinx.android.synthetic.main.activity_chapters.*

class ChaptersActivity : AppCompatActivity() {


    val adaptar  = GroupAdapter<ViewHolder>()

    companion object{

        var user: User?=null
        var subject:String?=null
    }
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapters)


        chapters_recyclerview.adapter = adaptar

        firestore = FirebaseFirestore.getInstance()

        subject = intent.getStringExtra(CategoryActivity.SUBJECT_KEY)


        chapters_subject.setText(subject)


        retrievuser()


        chapters_backbtn.setOnClickListener {

            finish()
        }
        
        
        

    }

    private fun retrievuser() {

        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

        val dbuser = firestore.collection("UserInfo").whereEqualTo("uid",currentuseruid)

        dbuser.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(this@ChaptersActivity,"something went wrong",Toast.LENGTH_SHORT).show()


                }

                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){

                        user = dc.document.toObject(User::class.java)

                        chapters_class.text = user?.standard

                        when (user?.standard){

                            "Class10"  -> {  chapters_class.text = "10" }
                            "Class9"  -> {  chapters_class.text = "9" }
                            "Class8"  -> {  chapters_class.text = "8" }
                            "Class7"  -> {  chapters_class.text = "7" }
                            "Class6"  -> {  chapters_class.text = "6" }


                        }



                        retreivechapters()
                        retreivechapter()

                    }

                }


            }


        })

    }

    private fun retreivechapters(){


        val dbchapter = firestore.collection(user?.standard.toString())
            .document(subject.toString())
            .collection("Chapters").orderBy("chapternum",Query.Direction.ASCENDING)

        dbchapter.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                   Toast.makeText(this@ChaptersActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){

                    val chapters = dc.document.toObject(Chapters::class.java)

                        adaptar.add(Chaptersrecy(this@ChaptersActivity,chapters, subject))

                    }

                }

                chapters_shrimmer.stopShimmer()
                chapters_shrimmer.visibility = View.GONE




            }


        })


    }

    private fun retreivechapter(){


        val  db = firestore.collection(user?.standard.toString())
            .whereEqualTo("subject", subject)

        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(this@ChaptersActivity,"something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        val chapter =  dc.document.toObject(Chapter::class.java)

                        Glide.with(applicationContext).load(chapter.headerimage).into(chapters_imageview)
                        chapters_name.text = chapter.subject

                    }

                }

            }


        })



    }





}