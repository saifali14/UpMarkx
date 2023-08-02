package com.youngminds.upmarkx

import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.Question
import kotlinx.android.synthetic.main.community_search_suggestion_car.view.*

class communitysuggestionrecy(val question: Question):Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {


        viewHolder.itemView.community_suggestion_textview.text = question.question

    }

    override fun getLayout(): Int {


        return  R.layout.community_search_suggestion_car
    }
}