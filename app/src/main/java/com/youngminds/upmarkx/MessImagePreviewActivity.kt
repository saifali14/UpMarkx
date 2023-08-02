package com.youngminds.upmarkx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_mess_image_preview.*

class MessImagePreviewActivity : AppCompatActivity() {





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mess_image_preview)



         val imageuri = intent.getStringExtra(ChatLogActivity.IMAGE_KEY).toString()

        previewimage(imageuri)


    }

    private fun previewimage(imageurl:String) {

        Glide.with(this).load(imageurl).into(sendimagepreview_imageview)

    }
}