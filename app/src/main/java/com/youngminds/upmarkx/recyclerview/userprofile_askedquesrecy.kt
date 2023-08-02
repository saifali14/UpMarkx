package com.youngminds.upmarkx.recyclerview

import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.Question
import com.youngminds.upmarkx.R
import kotlinx.android.synthetic.main.profile_questionasked_layout.view.*

class userprofile_askedquesrecy(val questions:Question):Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.profile_questionasked_question.text = questions.question

    }

    override fun getLayout(): Int {

        return R.layout.profile_questionasked_layout
    }
}