package com.youngminds.upmarkx.recyclerview

import android.content.Context
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.NotiAnswer
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.R
import com.youngminds.upmarkx.Utils.TimeStamp
import kotlinx.android.synthetic.main.notifyanswercard.view.*

class Notifyanswerrecy(val context: Context ,val message:NotiAnswer):Item<ViewHolder>() {

    private lateinit var firestore: FirebaseFirestore


    override fun bind(viewHolder: ViewHolder, position: Int) {


        firestore = FirebaseFirestore.getInstance()

        val currentuseruid = FirebaseAuth.getInstance().uid.toString()



        viewHolder.itemView.notifyanswer_message.setText("Has given the answer of your question -> '${message.question}'")


        viewHolder.itemView.notifyanswer_time.setText(TimeStamp.getTimeAgo(message.timestamp!!))

        if (message.seen == true){


            viewHolder.itemView.notifyanswer_notseen.visibility  =View.GONE

        }else{

            viewHolder.itemView.notifyanswer_notseen.visibility  =View.VISIBLE
        }


        val dbseen = firestore.collection("UserInfo")
            .document(currentuseruid)
            .collection("Notifications")
            .document(message.answerid.toString())

        dbseen.update("seen",true)


        val dbuser = firestore.collection("UserInfo")
            .whereEqualTo("uid",message.useruid)

        dbuser.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){


                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()
                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){



                        val user = dc.document.toObject(User::class.java)

                        viewHolder.itemView.notifyanswer_nameview.setText("${user.firstname} ${user.lastname}")

                        Glide.with(context).load(user.imageuri).into(viewHolder.itemView.notifyanswer_imageview)




                    }
                }


            }


        })






    }

    override fun getLayout(): Int {


        return R.layout.notifyanswercard
    }
}