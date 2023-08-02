package com.youngminds.upmarkx.recyclerview




import android.content.Context

import com.bumptech.glide.Glide
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.Videos
import com.youngminds.upmarkx.R
import kotlinx.android.synthetic.main.videos_card.view.*


class Videosrecy(val context:Context ,val videos: Videos):Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.videocard_tittle.text =  videos.tittle
        Glide.with(context).load(videos.thumbnail).into(viewHolder.itemView.videocardd_imageivew)


    }



    override fun getLayout(): Int {

        return R.layout.videos_card

    }
}