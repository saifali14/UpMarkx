package com.youngminds.upmarkx

import android.content.Context
import com.bumptech.glide.Glide
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.User
import kotlinx.android.synthetic.main.latestmess_uppercard.view.*

class Latestmessupperrecy(val context: Context , val user: User):Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        Glide.with(context).load(user.imageuri).into(viewHolder.itemView.latestmessupper_imageview)
        viewHolder.itemView.latestmessupper_name.text = "${user.firstname} ${user.lastname}"



    }

    override fun getLayout(): Int {

        return R.layout.latestmess_uppercard
    }


}