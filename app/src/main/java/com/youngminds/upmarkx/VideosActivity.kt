package com.youngminds.upmarkx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.Models.Videos
import com.youngminds.upmarkx.recyclerview.Videosrecy
import com.youngminds.upmarkx.recyclerview.Chaptersrecy
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.android.synthetic.main.activity_videos.*


class VideosActivity : AppCompatActivity() {



    companion object{

        val YOUTUBE_KEY ="YOUTUBE_KEY"


    }

    private lateinit var firestore: FirebaseFirestore

    val adaptar = GroupAdapter<ViewHolder>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videos)

        firestore = FirebaseFirestore.getInstance()


        videos_recyclerview.adapter =adaptar


      val  chapterid = intent.getStringExtra(Chaptersrecy.CHAPTER_KEY)
       val   subject =  intent.getStringExtra(Chaptersrecy.SUBJECT_KEY)


        if (chapterid != null) {
            if (subject != null) {
                retreiveuser(chapterid,subject)
            }
        }


        videos_backbtn.setOnClickListener {

            finish()
        }
        videos_message.setOnClickListener {

            val intent = Intent(this,LatestMessageseActivity::class.java)
            startActivity(intent)
        }





        adaptar.setOnItemClickListener { item, view ->


            val videoitem = item as Videosrecy

            val intent = Intent(this,VideopreviewActivity::class.java)
            intent.putExtra(YOUTUBE_KEY,videoitem.videos)
            startActivity(intent)

        }


    }

    private fun retreiveuser(chapterid:String, subject:String) {


        val useruid = FirebaseAuth.getInstance().uid.toString()
        val db = firestore.collection("UserInfo")
            .whereEqualTo("uid",useruid)

        db.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(this@VideosActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }


                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                       val user = dc.document.toObject(User::class.java)
                        retreivevideos(user,chapterid,subject)



                    }

                }


            }


        })


    }

    private fun retreivevideos(user: User,chapterid: String,subject: String) {

        val db = firestore.collection(user.standard.toString())
            .document(subject)
            .collection("Chapters")
            .document(chapterid)
            .collection("Videos")

        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(this@VideosActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){

                        val videos = dc.document.toObject(Videos::class.java)

                        Log.e("VideosActivity",videos.tittle.toString())

                        adaptar.add(Videosrecy(this@VideosActivity,videos))


                    }



                }


                videos_shrimmer.stopShimmer()
                videos_shrimmer.visibility =View.GONE


            }


        })


    }


}