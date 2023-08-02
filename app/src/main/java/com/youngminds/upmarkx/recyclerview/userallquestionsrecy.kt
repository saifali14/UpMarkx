package com.youngminds.upmarkx.recyclerview

import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.Question
import com.youngminds.upmarkx.R
import com.youngminds.upmarkx.Utils.TimeStamp
import kotlinx.android.synthetic.main.userallquestions_layout_card.view.*

class userallquestionsrecy(val questions: Question): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.userallquestions_question.text = questions.question
        viewHolder.itemView.userallquestion_time.text = TimeStamp.getTimeAgo(questions.date!!.toLong())
        viewHolder.itemView.userallquestion_subject.text = questions.subject

    }

    override fun getLayout(): Int {

        return R.layout.userallquestions_layout_card
    } }
