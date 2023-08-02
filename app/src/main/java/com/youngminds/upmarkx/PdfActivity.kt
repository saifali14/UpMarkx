package com.youngminds.upmarkx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.Pdfs
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.recyclerview.Pdfsrercy
import com.youngminds.upmarkx.recyclerview.Chaptersrecy
import kotlinx.android.synthetic.main.activity_pdf.*

class PdfActivity : AppCompatActivity() {


    private lateinit var firestore:FirebaseFirestore

    val adaptar = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)



        pdf_backbtn.setOnClickListener {

            finish()
        }


        pdf_recyclerview.adapter =adaptar

        firestore = FirebaseFirestore.getInstance()

        val  chapterid = intent.getStringExtra(Chaptersrecy.CHAPTER_KEY)
        val   subject =  intent.getStringExtra(Chaptersrecy.SUBJECT_KEY)


        chapterid?.let { subject?.let { it1 -> retreiveuser(it, it1) } }
    }


    private fun retreiveuser(chapterid:String, subject:String) {


        val useruid = FirebaseAuth.getInstance().uid.toString()
        val db = firestore.collection("UserInfo")
            .whereEqualTo("uid",useruid)

        db.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(this@PdfActivity,"Something went wrong", Toast.LENGTH_SHORT).show()

                }


                for (dc: DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        val user = dc.document.toObject(User::class.java)
                        retreivepdfs(user,chapterid,subject)

                    }

                }


            }


        })


    }



    private fun retreivepdfs(user: User,chapterid: String,subject: String) {

        val db = firestore.collection(user.standard.toString())
            .document(subject)
            .collection("Chapters")
            .document(chapterid)
            .collection("Pdfs")

        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(this@PdfActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){

                        val pdfs = dc.document.toObject(Pdfs::class.java)

                        Log.e("PdfActivity",pdfs.name.toString())

                        adaptar.add(Pdfsrercy(this@PdfActivity,pdfs))


                    }



                }


            }


        })


    }

}