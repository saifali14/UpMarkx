package com.youngminds.upmarkx.recyclerview

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.ChatLogActivity
import com.youngminds.upmarkx.MembersActivity
import com.youngminds.upmarkx.Models.DeliverChat
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.R
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.shareprofile_card.view.*
import java.text.SimpleDateFormat
import java.util.*

class shareprofilerecy(val context: Context , val users:User , val shareuser:User):Item<ViewHolder>() {


    private lateinit var firestore: FirebaseFirestore

    override fun bind(viewHolder: ViewHolder, position: Int) {


        firestore = FirebaseFirestore.getInstance()


        viewHolder.itemView.shareprofile_nameview.text = "${users.firstname} ${users.lastname}"
        Glide.with(context).load(users.imageuri).into(viewHolder.itemView.shareprofile_imageview)
        viewHolder.itemView.shareprofile_class.setText(users.standard)



        viewHolder.itemView.shareprofile_sendbtn.setOnClickListener {


            val currentuser = FirebaseAuth.getInstance().uid.toString()
            val outid = users.uid.toString()
            val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()).toString()
            val date  = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()).toString()

            val messageid = UUID.randomUUID().toString()




            val sendmessagein =  firestore.collection("Messages").document(currentuser).collection(outid).document(messageid)
            val sendmessageout = firestore.collection("Messages").document(outid).collection(currentuser).document(messageid)

            val latestmessagein =  firestore.collection("LatestMessages").document(currentuser).collection("messsages").document(outid)
            val latestmessageout =  firestore.collection("LatestMessages").document(outid).collection("messsages").document(currentuser)
            val deliverChat = DeliverChat(sendmessagein.id,"Profile Shared",currentuser,outid,time,date,System.currentTimeMillis() / 1000,"false","ProfileShared",currentuser,false,false,shareuser)
            val latestdeliverChat = DeliverChat(messageid,"Profile Shared",currentuser, outid,time,date,System.currentTimeMillis(),"false","ProfileShared",currentuser,false,false,shareuser)



            sendmessagein.set(deliverChat).addOnSuccessListener {

                Log.d(ChatLogActivity.TAG,"successfully delivered message${sendmessagein.id}")


                sendmessageout.set(deliverChat).addOnSuccessListener {

                    latestmessageout.set(latestdeliverChat).addOnSuccessListener {

                        latestmessagein.set(latestdeliverChat).addOnSuccessListener {




                            viewHolder.itemView.shareprofile_sendbtn.visibility = View.GONE
                            viewHolder.itemView.shareprofile_sendedbtn.visibility = View.VISIBLE

                            val intent = Intent(context,ChatLogActivity::class.java)
                            intent.putExtra(MembersActivity.USER_KEY,users)
                            context.startActivity(intent)

                        }

                    }


                }



            }.addOnFailureListener {

                Toast.makeText(context,"failed to send message", Toast.LENGTH_SHORT).show()

            }




        }






    }

    override fun getLayout(): Int {

        return R.layout.shareprofile_card

    }
}