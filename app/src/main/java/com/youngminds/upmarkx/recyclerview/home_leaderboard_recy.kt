package com.youngminds.upmarkx.recyclerview

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.*
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.LeaderboardUser
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.R
import com.youngminds.upmarkx.SearchActivity
import com.youngminds.upmarkx.UserProfileActivity
import kotlinx.android.synthetic.main.home_leaderboard_card.view.*

class home_leaderboard_recy(val context: Context ,  val leaderboardcard:LeaderboardUser , val rank:String):Item<ViewHolder>() {

    private lateinit var firestore: FirebaseFirestore

    var user:User?=null

    override fun bind(viewHolder: ViewHolder, position: Int) {


        firestore = FirebaseFirestore.getInstance()




        viewHolder.itemView.home_leaderboard_thankstext.text =leaderboardcard.likes.toString()

        val dbuser = firestore.collection("UserInfo")
            .whereEqualTo("uid",leaderboardcard.uid)

        dbuser.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()
                }



                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){


                         user = dc.document.toObject(User::class.java)

                        viewHolder.itemView.home_leaderboard_name.text = "${user?.firstname} ${user?.lastname}"
                        viewHolder.itemView.home_leaderboard_rank.text = "$rank."

                        if (user?.verified == "true"){

                            viewHolder.itemView.home_leaderboard_verified.visibility = View.VISIBLE

                        }else{

                            viewHolder.itemView.home_leaderboard_verified.visibility = View.GONE
                        }


                        Glide.with(context).load(user?.imageuri).into(viewHolder.itemView.home_leaderboard_imageview)

                    }

                }


            }


        })



        viewHolder.itemView.home_leaderboardcard.setOnClickListener {

            val intent = Intent(context,UserProfileActivity::class.java)
            intent.putExtra(SearchActivity.USERK,user)
            context.startActivity(intent)


        }








    }

    override fun getLayout(): Int {



        return R.layout.home_leaderboard_card
    }
}