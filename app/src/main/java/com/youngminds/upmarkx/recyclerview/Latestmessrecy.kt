package com.youngminds.upmarkx.recyclerview

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.DeliverChat
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.R
import com.youngminds.upmarkx.Utils.TimeStamp
import kotlinx.android.synthetic.main.latestmess_card.view.*


class Latestmessrecy(val context: Context, val message: DeliverChat, val inuser: User):
    Item<ViewHolder>() {

    private lateinit var firestore: FirebaseFirestore

    var outuser: User?=null
    var inid:String?=null
    var outid:String?=null
    var recentmessage:DeliverChat?=null
    var recentmessageup:DeliverChat?=null


    override fun bind(viewHolder: ViewHolder, position: Int) {


        firestore = FirebaseFirestore.getInstance()

        val  currentuser  = FirebaseAuth.getInstance().uid
//        viewHolder.itemView.latestmeses_messageview.text  =  message.message
//        viewHolder.itemView.latestmess_timeviiew.text = TimeStamp.getTimeAgo(message.timestamp!!)



        val latestmess =  firestore.collection("LatestMessages")
            .document(inuser.uid.toString())
            .collection("messsages")
            .whereEqualTo("messageid",message.messageid)

        latestmess.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type ==DocumentChange.Type.ADDED){


                        recentmessage  = dc.document.toObject(DeliverChat::class.java)
                        viewHolder.itemView.latestmeses_messageview.text  =  recentmessage?.message

                        if (recentmessage!!.deleted == true){

                            viewHolder.itemView.latestmeses_messageview.text = "Message Deleted"

                        }else{

                            viewHolder.itemView.latestmeses_messageview.text = recentmessage?.message
                        }

                        viewHolder.itemView.latestmess_timeviiew.text = TimeStamp.getTimeAgo(recentmessage?.timestamp!!)


                    }
                    if(dc.type == DocumentChange.Type.REMOVED){

                        recentmessage  = dc.document.toObject(DeliverChat::class.java)
                        viewHolder.itemView.latestmess_timeviiew.text = TimeStamp.getTimeAgo(recentmessage?.timestamp!!)
                        if (recentmessage!!.deleted == true){

                            viewHolder.itemView.latestmeses_messageview.text = "Message Deleted"

                        }else{
                            viewHolder.itemView.latestmeses_messageview.text = recentmessage?.message

                        }

                    }


                }

            }


        })





        if(message.outid == currentuser ){

            outid = message.inid
            viewHolder.itemView.latestmesage_seen.visibility = View.GONE

        }else{

            outid = message.outid
            viewHolder.itemView.latestmesage_seen.visibility = View.VISIBLE


        }






        if (message.seen == "true") {
            Glide.with(context).load(R.drawable.seen_icon)
                .into(viewHolder.itemView.latestmesage_seen)
        } else {

            Glide.with(context).load(R.drawable.notseen_icon)
                .into(viewHolder.itemView.latestmesage_seen)

        }







        val db = firestore.collection("UserInfo").whereEqualTo("uid", outid)
        db.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error != null) {


                    Toast.makeText(context, "Network issue is there", Toast.LENGTH_SHORT).show()

                }

                for (dc: DocumentChange in value?.documentChanges!!) {

                    if (dc.type == DocumentChange.Type.ADDED) {


                        outuser = dc.document.toObject(User::class.java)

                        Glide.with(context).load(outuser!!.imageuri)
                            .into(viewHolder.itemView.latestmess_imageview)
                        viewHolder.itemView.latestmes_username.text = outuser!!.firstname

                        if (outuser?.verified == "true"){

                            viewHolder.itemView.latestmess_verified.visibility = View.VISIBLE



                        }else{
                            viewHolder.itemView.latestmess_verified.visibility = View.GONE

                        }





                        if (message.seen == "true") {
                            Glide.with(context).load(R.drawable.seen_icon)
                                .into(viewHolder.itemView.latestmesage_seen)
                        } else {

                            Glide.with(context).load(R.drawable.notseen_icon)
                                .into(viewHolder.itemView.latestmesage_seen)

                        }



                        if (outuser!!.status == "Online" || outuser!!.status == "Inchat") {

                            Glide.with(context).load(R.drawable.greendot_icon)
                                .into(viewHolder.itemView.activeimageview)

                        }else{
                            Glide.with(context).load(R.color.light_gray)
                                .into(viewHolder.itemView.activeimageview)

                        }

                    }

                    if (dc.type == DocumentChange.Type.MODIFIED){

                        outuser = dc.document.toObject(User::class.java)

                        Glide.with(context).load(outuser!!.imageuri)
                            .into(viewHolder.itemView.latestmess_imageview)
                        viewHolder.itemView.latestmes_username.text = outuser!!.firstname









                        if (outuser!!.status == "Online" || outuser!!.status == "Inchat") {

                            Glide.with(context).load(R.drawable.greendot_icon)
                                .into(viewHolder.itemView.activeimageview)

                        }else{

                            Glide.with(context).load(R.color.gray)
                                .into(viewHolder.itemView.activeimageview)
                        }

                    }


                }


            } })



        val currentuseruid  = FirebaseAuth.getInstance().uid.toString()
        val dbnotseen = firestore.collection("Messages")
            .document(outid.toString())
            .collection(inuser.uid.toString())
            .whereEqualTo("seen","false").whereEqualTo("inid",outid)


        dbnotseen.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                var messagenotseen = 0

                if (error!=null){

                    Toast.makeText(context,error.message, Toast.LENGTH_SHORT).show()
                    Log.e("Latestmessrecy",error.message.toString())
                }

                for (dc:DocumentChange in  value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        viewHolder.itemView.latestmess_notseencard.visibility = View.VISIBLE
                        messagenotseen++


                    }

                    if (dc.type == DocumentChange.Type.MODIFIED){

                        val dbnotseenin = firestore.collection("Messages")
                            .document(outid.toString())
                            .collection(inuser.uid.toString())
                            .whereEqualTo("seen","false").whereEqualTo("inid",outid)


                        dbnotseenin.addSnapshotListener(object : EventListener<QuerySnapshot>{
                            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                                var messagenotseenin = 0

                                if (error!=null){

                                    Toast.makeText(context,error.message, Toast.LENGTH_SHORT).show()
                                    Log.e("Latestmessrecy",error.message.toString())
                                }

                                for (dcin:DocumentChange in  value?.documentChanges!!){

                                    if (dcin.type == DocumentChange.Type.ADDED){

                                        viewHolder.itemView.latestmess_notseencard.visibility = View.VISIBLE
                                        messagenotseenin++


                                    }



                                }

                                viewHolder.itemView.latestmess_messagecount.text  = messagenotseenin.toString()
                                if (messagenotseenin == 0){

                                    viewHolder.itemView.latestmess_notseencard.visibility = View.GONE
                                }


                            }




                        })



                    }

                }

                viewHolder.itemView.latestmess_messagecount.text  = messagenotseen.toString()
                if (messagenotseen == 0){

                    viewHolder.itemView.latestmess_notseencard.visibility = View.GONE
                }


            }




        })






    }

    override fun getLayout(): Int {


        return R.layout.latestmess_card
    }
}