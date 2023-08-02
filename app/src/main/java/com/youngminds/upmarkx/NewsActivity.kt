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
import com.youngminds.upmarkx.Models.News
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.recyclerview.Newsrecy

import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {

    val adaptar = GroupAdapter<ViewHolder>()

    private lateinit var firestore: FirebaseFirestore

    companion object{
        var news: News?=null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)



        firestore = FirebaseFirestore.getInstance()




        news_recyclerview.adapter = adaptar




        adaptar.setOnItemClickListener { item, _ ->


            val  news = item as Newsrecy

            val intent = Intent(this,WebviewActivity::class.java)
            intent.putExtra(Newsrecy.WEB_KEY,news.news.newslink)
          startActivity(intent)

        }


        retreivenews()



        news_backbtn.setOnClickListener {

            finish()
        }


        news_messagebtn.setOnClickListener {

            val intent = Intent(this,LatestMessageseActivity::class.java)
            startActivity(intent)


        }










        news_searchview.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {



            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {



                adaptar.clear()

                val searchnews = firestore.collection("News")
                    .whereArrayContains("keywords",
                        s?.trim().toString()).limit(100)
                searchnews.addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {


                        if (error!=null){

                            Toast.makeText(this@NewsActivity,"Network issue is there",
                                Toast.LENGTH_SHORT).show()


                        }

                        for (dc: DocumentChange in value?.documentChanges!!){


                            if (dc.type == DocumentChange.Type.ADDED){

                                val news = dc.document.toObject(News::class.java)

                                adaptar.add(Newsrecy(news,this@NewsActivity))

                            }

                        }


                    }


                })


                if (news_searchview.text.isEmpty()){

                    retreivenews()

                }




            }

            override fun afterTextChanged(s: Editable?) {

            }


        })



    }

    private fun retreiveuser() {


        val currentuseruid  = FirebaseAuth.getInstance().uid.toString()

        val db = firestore.collection("UserInfo").whereEqualTo("uid",currentuseruid)
        db.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(this@NewsActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type  == DocumentChange.Type.ADDED){

                        val user = dc.document.toObject(User::class.java)

                    }


                }


            }


        })


    }


    private fun retreivenews() {


        val dbnews = firestore.collection("News").orderBy("time",Query.Direction.DESCENDING)
        dbnews.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(this@NewsActivity,"Something went wrong refresh the page",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type  == DocumentChange.Type.ADDED){

                        news = dc.document.toObject(News::class.java)

                        adaptar.add(Newsrecy(news!!,this@NewsActivity))

                    }

                }


            }


        })


    }


}