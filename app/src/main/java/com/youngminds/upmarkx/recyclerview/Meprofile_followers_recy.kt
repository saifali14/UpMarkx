package com.youngminds.upmarkx.recyclerview

import android.content.Context
import com.bumptech.glide.Glide
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.R
import kotlinx.android.synthetic.main.meprofile_followers_card.view.*

class Meprofile_followers_recy(val context:Context , val user:User):Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {


        Glide.with(context).load(user.imageuri).into(viewHolder.itemView.meprofile_followers_imageview)
        viewHolder.itemView.meprofile_followers_name.text = user.firstname.toString()



    }

    override fun getLayout(): Int {

         return  R.layout.meprofile_followers_card
    }
}