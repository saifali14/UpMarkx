package com.youngminds.upmarkx.recyclerview

import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.News
import com.youngminds.upmarkx.R
import com.youngminds.upmarkx.Utils.TimeStamp
import com.youngminds.upmarkx.WebviewActivity
import kotlinx.android.synthetic.main.newscard.view.*

class Newsrecy(val news: News, val context: Context):Item<ViewHolder>() {

    companion object{

        val WEB_KEY ="WEB_KEY"

    }

    override fun bind(viewHolder: ViewHolder, position: Int) {



        viewHolder.itemView.newscard_heading.text = news.heading
        viewHolder.itemView.newscard_body.text = news.body
        Glide.with(context).load(news.imageuri).into(viewHolder.itemView.newscard_imageview)
        viewHolder.itemView.newscard_date.text = news.time?.let { TimeStamp.getTimeAgo(it) }



        viewHolder.itemView.newscard_showmore.setOnClickListener{
           val intent = Intent(context,WebviewActivity::class.java)
            intent.putExtra(WEB_KEY,news.newslink)
            context.startActivity(intent)



        }


    }

    override fun getLayout(): Int {
      return R.layout.newscard
    }
}