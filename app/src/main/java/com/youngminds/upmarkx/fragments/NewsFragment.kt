package com.youngminds.upmarkx.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.News
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.R
import com.youngminds.upmarkx.recyclerview.Newsrecy
import kotlinx.android.synthetic.main.fragment_news.view.*


class NewsFragment : Fragment() {

    val adaptar = GroupAdapter<ViewHolder>()

    private lateinit var firestore: FirebaseFirestore

    companion object{
        var news: News?=null
        var count = 0

    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_news, container, false)


        firestore = FirebaseFirestore.getInstance()




        view.news_recyclerview.adapter = adaptar







//        news_backbtn.setOnClickListener {
//
//
//        }


//        news_messagebtn.setOnClickListener {
//
//            val intent = Intent(this, LatestMessageseActivity::class.java)
//            startActivity(intent)
//
//
//        }

        count++


        if (count == 1){

            retreiveuser()
        }



        return view
    }


    private fun retreiveuser() {


        val currentuseruid  = FirebaseAuth.getInstance().uid.toString()

        val db = firestore.collection("UserInfo").whereEqualTo("uid",currentuseruid)
        db.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(activity,"Something went wrong", Toast.LENGTH_SHORT).show()

                }

                for (dc: DocumentChange in value?.documentChanges!!){

                    if (dc.type  == DocumentChange.Type.ADDED){

                        val user = dc.document.toObject(User::class.java)


                    }


                }


            }


        })


    }


    private fun retreivenews() {


        val dbnews = firestore.collection("News").orderBy("index", Query.Direction.DESCENDING)
        dbnews.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(activity,"Something went wrong refresh the page", Toast.LENGTH_SHORT).show()

                }

                for (dc: DocumentChange in value?.documentChanges!!){

                    if (dc.type  == DocumentChange.Type.ADDED){

                       news = dc.document.toObject(News::class.java)

                        adaptar.add(Newsrecy(news!!,activity!!))

                    }

                }


            }


        })


    }


    override fun onPause() {
        super.onPause()

        adaptar.clear()

    }



}