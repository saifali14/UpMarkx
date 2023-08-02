package com.youngminds.upmarkx.recyclerview

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.Request
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.R
import com.youngminds.upmarkx.Utils.TimeStamp
import kotlinx.android.synthetic.main.request_card.view.*

class Requestrecy(val context:Context, val request:Request):Item<ViewHolder>() {

    private lateinit var firestore: FirebaseFirestore

    companion object{

        var sender:User?=null
        var receiver:User?=null

    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        firestore = FirebaseFirestore.getInstance()

        Toast.makeText(context,request.receiveruid,Toast.LENGTH_SHORT).show()
        Toast.makeText(context,request.senderuid,Toast.LENGTH_SHORT).show()


        val db = firestore.collection("UserInfo").whereEqualTo("uid",request.senderuid)
        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for(dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                      sender = dc.document.toObject(User::class.java)
//                        Log.e("Notification", "sender: ${sender.toString()}")
                        viewHolder.itemView.requestcard_name.text = "${sender?.firstname} ${sender?.lastname}"
                        viewHolder.itemView.requestcard_time.text = TimeStamp.getTimeAgo(request.timestamp?.toLong()!!)
                        Glide.with(context).load(sender?.imageuri).into(viewHolder.itemView.requestcard_imageview)

                    }

                }

            }


        })
        val currentuseruid = FirebaseAuth.getInstance().uid.toString()
        val dbcurrentuser = firestore.collection("UserInfo").whereEqualTo("uid",currentuseruid)
        dbcurrentuser.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for(dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        receiver = dc.document.toObject(User::class.java)

                        Log.e("Notification", "receiver: ${receiver.toString()}")



                    }

                }

            }


        })




        viewHolder.itemView.requestcard_decline.setOnClickListener {


            val dbdecline = firestore.collection("UserInfo")
                .document(currentuseruid)
                .collection("Requests")
                .document(request.senderuid.toString())

            dbdecline.delete().addOnSuccessListener {


                val dbdeclineout = firestore.collection("UserInfo")
                    .document(request.senderuid.toString())
                    .collection("SendedRequest")
                    .document(currentuseruid)

                dbdeclineout.delete().addOnSuccessListener {


                    Toast.makeText(context,"Declined the user",Toast.LENGTH_SHORT).show()

                }

            }


        }


        viewHolder.itemView.requestcard_accept.setOnClickListener {


           val dbfollower = firestore.collection("UserInfo")
               .document(currentuseruid)
               .collection("Followers")
               .document(sender?.uid.toString())


            dbfollower.set(sender!!).addOnSuccessListener {


                val dbfollowing = firestore.collection("UserInfo")
                    .document(sender?.uid.toString())
                    .collection("Following")
                    .document(currentuseruid)

                dbfollowing.set(receiver!!).addOnSuccessListener {


                    val dbaccept = firestore.collection("UserInfo")
                        .document(currentuseruid)
                        .collection("Requests")
                        .document(request.senderuid.toString())

                    dbaccept.delete().addOnSuccessListener {


                        val dbacceptout = firestore.collection("UserInfo")
                            .document(request.senderuid.toString())
                            .collection("SendedRequest")
                            .document(currentuseruid)

                        dbacceptout.delete()

                    }
                    Toast.makeText(context,"${sender?.lastname} started following you",Toast.LENGTH_SHORT).show()

                }

            }




        }






    }

    override fun getLayout(): Int {
        return R.layout.request_card
    }
}