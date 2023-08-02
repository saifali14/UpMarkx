package com.youngminds.upmarkx.recyclerview

import android.content.Context
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.*
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Leaderboarduser
import com.youngminds.upmarkx.Models.LeaderboardUser
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.R
import kotlinx.android.synthetic.main.leadercard.view.*

class Leaderrecy(val context: Context, val user: LeaderboardUser ,  val rank:Int): Item<ViewHolder>() {


    private lateinit var firestore: FirebaseFirestore

    override fun bind(viewHolder: ViewHolder, position: Int) {


        firestore = FirebaseFirestore.getInstance()


        val db = firestore.collection("UserInfo")
            .whereEqualTo("uid",user.uid)


        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()
                }

                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){


                        val users = dc.document.toObject(User::class.java)
                        viewHolder.itemView.leaderboard_index.text = "$rank."
                        viewHolder.itemView.leaderboard_class.setText(users.standard)
                        viewHolder.itemView.leaderboard_username.text = "${users.firstname} ${users.lastname}"
                        viewHolder.itemView.leaderboard_likes.text = user.likes.toString()
                        Glide.with(context).load(users.imageuri).into(viewHolder.itemView.leaderboard_imagview)

                    }
                }


            }


        })








    }

    override fun getLayout(): Int {


        return R.layout.home_leaderboard_card


    }
}