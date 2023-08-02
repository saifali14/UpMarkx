package com.youngminds.upmarkx.recyclerview

import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.Answer
import com.youngminds.upmarkx.R
import kotlinx.android.synthetic.main.userprofile_ansrow.view.*

class Userinforecy(val answer: Answer): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.userinfo_anscard_answer.text = answer.answer

    }

    override fun getLayout(): Int {
        return R.layout.userprofile_ansrow
    }
}