package com.youngminds.upmarkx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.youngminds.upmarkx.Models.About
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import java.util.*

class AboutActivity : AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore



    var about:About?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)



        firestore = FirebaseFirestore.getInstance()




        val db = firestore.collection("About")
            .whereEqualTo("About","Description")

        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(this@AboutActivity,"Something went wrong",Toast.LENGTH_SHORT).show()
                }


                for (dc:DocumentChange in value?.documentChanges!!){

                  if (dc.type == DocumentChange.Type.ADDED){


                     about = dc.document.toObject(About::class.java)



                  }

                }

            }


        })


        val adsElement = Element()
        adsElement.setTitle("Advertise here")

        val aboutPage = AboutPage(this)
            .isRTL(false)
            .setImage(R.drawable.upmarks_logo_splash)
            .setDescription(about?.info)
            .addItem(Element().setTitle("Version 1.0"))
            .addItem(adsElement)
            .addGroup("Contact with us")
            .addEmail("youngmindsjourney@gmail.com")
            .addPlayStore("https://play.google.com/store/apps/details?id=com./com.youngminds.upmarkx&hl=en")
            .addInstagram("https://www.instagram.com/youngmindsjourney/")
            .addItem(createCopyright())
            .create()


        setContentView(aboutPage)


    }


    private fun createCopyright(): Element {


        val copyright = Element()
        val copyrightstring =
            String.format("Copyright by upMarks", Calendar.getInstance().get(Calendar.YEAR))
        copyright.setTitle(copyrightstring)
        copyright.setIcon(R.mipmap.ic_launcher)
        copyright.setGravity(Gravity.CENTER)

        copyright.setOnClickListener {


            Toast.makeText(this, copyrightstring, Toast.LENGTH_SHORT).show()

        }

        return copyright


    }
}

