package com.youngminds.upmarkx

import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.*
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

import com.youngminds.upmarkx.Models.Leaderboarduseradmin
import com.youngminds.upmarkx.Models.User
import kotlinx.android.synthetic.main.likescount_card.view.*

class Testingrecy(val user: User): Item<ViewHolder>() {

    private lateinit var firestore: FirebaseFirestore

    override fun bind(viewHolder: ViewHolder, position: Int) {


        firestore = FirebaseFirestore.getInstance()

        viewHolder.itemView.likescount_username.text = user.firstname

        val db = firestore.collection("UserInfo")
            .document(user.uid.toString())
            .collection("Likes")

        db.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                var likescount = 0

                if (error!=null){
                    Log.e("Testingrecy","something went wrong while counting the likes")

                }

                for (dc:DocumentChange in  value?.documentChanges!!){

                    if (dc.type  == DocumentChange.Type.ADDED){

                        likescount++

                    }

                }

                viewHolder.itemView.likescount_likes.text = likescount.toString()


                val dbuserr = firestore.collection("LeaderboardAdmin")
                    .document(user.uid.toString())

                val leaderboaruser = Leaderboarduseradmin(user.firstname,user.lastname,user.email)



            }


        })








    }

    override fun getLayout(): Int {

        return R.layout.likescount_card
    }
}