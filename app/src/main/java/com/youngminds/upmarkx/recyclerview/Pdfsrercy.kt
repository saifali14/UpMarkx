package com.youngminds.upmarkx.recyclerview

import android.content.Context
import android.content.Intent
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.PdfpreviewActivity
import com.youngminds.upmarkx.Models.Pdfs
import com.youngminds.upmarkx.R
import kotlinx.android.synthetic.main.pdfcard.view.*

class Pdfsrercy(val context: Context , val  pdfs: Pdfs):Item<ViewHolder>() {

    companion object{

        val PDF_KEY ="PDF_KEY"

    }

    override fun bind(viewHolder: ViewHolder, position: Int) {


        viewHolder.itemView.pdfcard_pdfname.text = pdfs.name


        viewHolder.itemView.pdfcard_card.setOnClickListener{

            val intent  =  Intent(context, PdfpreviewActivity::class.java)
            intent.putExtra(PDF_KEY,pdfs)
            context.startActivity(intent)


        }



    }

    override fun getLayout(): Int {


        return R.layout.pdfcard

    }
}