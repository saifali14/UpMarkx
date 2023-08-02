package com.youngminds.upmarkx.recyclerview

import android.content.Context
import android.view.View
import com.bumptech.glide.Glide
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.R
import kotlinx.android.synthetic.main.search_usercard.view.*

class followerrecy(val context:Context, val user:User):Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {



        viewHolder.itemView.searchview_username.text = "${user.firstname} ${user.lastname}"
        viewHolder.itemView.searchview_class.setText(user.standard)
        Glide.with(context).load(user.imageuri).into(viewHolder.itemView.searchview_userimage)



        if (user.verified == "true"){

           viewHolder.itemView.searchview_verified.visibility =View.VISIBLE

        }else{

            viewHolder.itemView.searchview_verified.visibility =View.GONE

        }

    }

    override fun getLayout(): Int {

        return R.layout.search_usercard

    }
}