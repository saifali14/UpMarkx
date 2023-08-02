package com.youngminds.upmarkx

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.bumptech.glide.Glide
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.User
import kotlinx.android.synthetic.main.activity_latest_messagese.*
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.chatusercard.view.*

import java.util.*

class MembersActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    val adaptar = GroupAdapter<ViewHolder>()



    companion object{

        val USER_KEY =  "USER_KEY"

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)


        firestore = FirebaseFirestore.getInstance()


        member_ecylerview.adapter = adaptar









        retreivewmembers()



    }

    private fun retreivewmembers() {

        val dbmem = firestore.collection("UserInfo").addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                var user: User

                if(error!=null){

                    Log.d("MembersActivity",error.message.toString())
                }


                for (dc : DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){

                         user = dc.document.toObject(User::class.java)
                        adaptar.add(membersrecy(user,this@MembersActivity))

                    }

                }


                adaptar.setOnItemClickListener { item, view ->

                    val  useritem = item as membersrecy

                    val intent = Intent(view.context,ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY,useritem.user)
                    startActivity(intent)

                }


            }


        })










    }





    class membersrecy(val  user: User, val context: Context):Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

            viewHolder.itemView.search_username.text = user.firstname
            Glide.with(context).load(user.imageuri).into(viewHolder.itemView.search_userimage)




        }

        override fun getLayout(): Int {

            return R.layout.chatusercard
        }
    }

}