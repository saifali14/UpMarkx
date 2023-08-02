package com.youngminds.upmarkx.Utils

import android.app.Activity
import android.app.AlertDialog
import com.youngminds.upmarkx.R

class EditprofileLoadingbar(val mActivity:Activity) {


    private lateinit var isdialog:AlertDialog


     fun startloading(){


        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_editprofile,null)

        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)

        isdialog = builder.create()
        isdialog.show()


    }


    fun isDismiss(){

        isdialog.cancel()


    }


}