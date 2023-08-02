package com.youngminds.upmarkx.recyclerview

import android.content.Context
import com.bumptech.glide.Glide
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.News
import com.youngminds.upmarkx.R
import kotlinx.android.synthetic.main.home_newscard.view.*

class home_Newsrecy(val news: News, val context: Context):Item<ViewHolder>() {

    companion object{

        val WEB_KEY ="WEB_KEY"

    }

    override fun bind(viewHolder: ViewHolder, position: Int) {



        viewHolder.itemView.home_newscard_heading.text = news.heading

        Glide.with(context).load(news.imageuri).into(viewHolder.itemView.home_newscard_imageview)




//        viewHolder.itemView.home_newscard_showmore.setOnClickListener{
//           val intent = Intent(context,WebviewActivity::class.java)
//            intent.putExtra(WEB_KEY,news.newslink)
//            context.startActivity(intent)



        }

    override fun getLayout(): Int {
        return R.layout.home_newscard
    }


}

