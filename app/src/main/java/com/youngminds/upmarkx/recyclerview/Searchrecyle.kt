package com.youngminds.upmarkx.recyclerview

import android.content.Context
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.R
import kotlinx.android.synthetic.main.chatusercard.view.*
import kotlinx.android.synthetic.main.search_usercard.view.*

class Searchrecyle(val context: Context ,val user: User, val recent:Boolean):Item<ViewHolder>(){

    private lateinit var firestore: FirebaseFirestore

    override fun bind(viewHolder: ViewHolder, position: Int) {

        firestore = FirebaseFirestore.getInstance()


        viewHolder.itemView.searchview_username.text = "${user.firstname} ${user.lastname}"
        Glide.with(context).load(user.imageuri).into(viewHolder.itemView.searchview_userimage)

        viewHolder.itemView.searchview_class.setText(user.standard)



        if(recent == true){

           viewHolder.itemView.searchrecent_removebttn.visibility  = View.VISIBLE

        }
        if (recent == false){

            viewHolder.itemView.searchrecent_removebttn.visibility  = View.GONE
        }

        viewHolder.itemView.searchrecent_removebttn.setOnClickListener {

            val currentuseruid = FirebaseAuth.getInstance().uid.toString()

            val dbremove = firestore.collection("UserInfo")
                .document(currentuseruid)
                .collection("RecentlySearchUsers")
                .document(user.uid.toString())

            dbremove.delete()



        }



        if(user.verified == "true"){
            viewHolder.itemView.searchview_verified.visibility = View.VISIBLE
        }else{

            viewHolder.itemView.searchview_verified.visibility = View.GONE
        }


    }

    override fun getLayout(): Int {


        return R.layout.search_usercard
    }


}