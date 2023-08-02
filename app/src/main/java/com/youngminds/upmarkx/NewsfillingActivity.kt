package com.youngminds.upmarkx

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.youngminds.upmarkx.Models.News
import com.youngminds.upmarkx.Utils.RegisterationLoadingbar
import kotlinx.android.synthetic.main.activity_info_image.*
import kotlinx.android.synthetic.main.activity_newsfilling.*
import java.util.*

class NewsfillingActivity : AppCompatActivity() {



    private lateinit var firestore: FirebaseFirestore
    val loading = RegisterationLoadingbar(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newsfilling)

        firestore = FirebaseFirestore.getInstance()



        newsuimageview.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)

        }


        newsubmit.setOnClickListener {

            loading.startloading()
           uploadimagetostorage()

        }




    }



    var imageuri: Uri = Uri.parse("android.resource://com.youngminds.upmarkx/drawable/cbselogo")

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode==0 && resultCode== Activity.RESULT_OK && data!=null){

            imageuri = data.data!!

            Glide.with(applicationContext).load(imageuri).into(newsuimageview)
        }
    }


    private fun uploadimagetostorage(){


        val uid = UUID.randomUUID()


        val dbstorage = FirebaseStorage.getInstance().getReference("NewsImages").child(uid.toString())
        dbstorage.putFile(imageuri).addOnSuccessListener {

            Log.d("adminnews","user image uploaded")
            dbstorage.downloadUrl.addOnSuccessListener {

                Log.d("adminnews","user image download")
                submitnews(it.toString())
            }.addOnFailureListener{
                Log.d("adminnews","failed to upload image")
            }

        }

    }

    private fun submitnews(imageurl:String) {


        Toast.makeText(this,imageurl,Toast.LENGTH_SHORT).show()

        val newsid = UUID.randomUUID().toString()
        val heading = newsuheading_edxt.text.toString().toLowerCase()
        val body = newsubody_edxt.text.toString().toLowerCase()
        val link = newsulink_edxt.text.toString()
        val index = newsuindex_edxt.text.toString()
        val time = System.currentTimeMillis()

        val key = removespecialcharacter(body)


        Log.e("admin",key)

        val news = News(newsid,imageurl,heading,body,link,time,keywords(key))



        val dbupload = firestore.collection("News")
            .document(newsid)

        dbupload.set(news).addOnSuccessListener {

            loading.isDismiss()


        }.addOnFailureListener {

            Toast.makeText(this,"failed to upload news:${it.message}",Toast.LENGTH_SHORT).show()
            loading.isDismiss()
            Log.e("admin","failed to upload news: ${it.message}")

        }


    }

    private fun keywords(question:String):List<String>{

        val keywords = mutableListOf<String>()
        for ( i in 0 until question.length){

            for (j in (i+1)..question.length){

                keywords.add(question.slice(i until j))

            }

        }
//        Toast.makeText(this,"${keywords[0]}, ${keywords[1]}, ${keywords[2]}, ${keywords[3]}",Toast.LENGTH_SHORT).show()

        return keywords



    }


    private fun removespecialcharacter(txt:String):String{

        var str = txt

        val re = "[^A-Za-z0-9 ]".toRegex()

        str = re.replace(str, "")

        return str

    }


}