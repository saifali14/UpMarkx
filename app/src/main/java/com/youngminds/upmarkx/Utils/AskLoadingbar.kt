package com.youngminds.upmarkx.Utils

import android.app.Activity
import android.app.AlertDialog
import android.os.Handler
import com.youngminds.upmarkx.R

class AskLoadingbar(val mActivity:Activity) {


    private lateinit var isdialog:AlertDialog


     fun startloading(){


        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_ask,null)

        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)

        isdialog = builder.create()
        isdialog.show()



         Handler().postDelayed({isdialog.cancel()},7000)

    }


    fun isDismiss(){

        isdialog.cancel()


    }


}