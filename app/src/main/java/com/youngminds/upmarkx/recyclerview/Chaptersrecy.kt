package com.youngminds.upmarkx.recyclerview

import android.content.Context
import android.content.Intent
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.Chapters
import com.youngminds.upmarkx.PdfActivity
import com.youngminds.upmarkx.R
import com.youngminds.upmarkx.VideosActivity
import kotlinx.android.synthetic.main.chaptercard.view.*

class Chaptersrecy(val context: Context,val chapters: Chapters, val subject:String?=null):Item<ViewHolder>() {

    companion object{

        val CHAPTER_KEY ="CHAPTER_KEY"
        val SUBJECT_KEY ="SUBJECT_KEY"
    }
    override fun bind(viewHolder: ViewHolder, position: Int) {


        viewHolder.itemView.chaptercard_chaptername.text = chapters.chaptername
        viewHolder.itemView.chaptercard_chapternum.text = chapters.chapternum.toString()
        viewHolder.itemView.chaptercard_numberpdf.text = "Videos: ${chapters.pdfcount} Pdf: ${chapters.videoscount}"



        viewHolder.itemView.chaptercard_card.setOnClickListener {

            if (viewHolder.itemView.chaptercard_foldablelayout.visibility == View.GONE) {

                TransitionManager.beginDelayedTransition(viewHolder.itemView.chaptercard, AutoTransition())
                viewHolder.itemView.chaptercard_foldablelayout.visibility = View.VISIBLE


            } else {
                TransitionManager.beginDelayedTransition(viewHolder.itemView.chaptercard, AutoTransition())
                viewHolder.itemView.chaptercard_foldablelayout.visibility = View.GONE



            }


        }


        viewHolder.itemView.chaptercard_pdfcard.setOnClickListener {

            val intent = Intent(context, PdfActivity::class.java)
            intent.putExtra(CHAPTER_KEY,chapters.documentid)
            intent.putExtra(SUBJECT_KEY,subject)
            context.startActivity(intent)

        }
        viewHolder.itemView.chaptercard_videoscard.setOnClickListener {

            val intent = Intent(context, VideosActivity::class.java)
            intent.putExtra(CHAPTER_KEY,chapters.documentid)
            intent.putExtra(SUBJECT_KEY,subject)
            context.startActivity(intent)

        }


    }

    override fun getLayout(): Int {

        return R.layout.chaptercard
    }
}