package com.youngminds.upmarkx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.recyclerview.discoverprecy
import kotlinx.android.synthetic.main.activity_discoverpeople.*

class DiscoverpeopleActivity : AppCompatActivity() {



    val adaptar = GroupAdapter<ViewHolder>()
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discoverpeople)



        firestore = FirebaseFirestore.getInstance()



        discoverp_recyclerview.adapter =adaptar

        getusers()



        adaptar.setOnItemClickListener { item, view ->


            val user = item as discoverprecy

            val intent = Intent(applicationContext,UserProfileActivity::class.java)
            intent.putExtra(SearchActivity.USERK,user.user)
            startActivity(intent)


        }


        discoverp_backbtn.setOnClickListener {

            finish()
        }

    }

    private fun getusers() {

        val db = firestore.collection("UserInfo")
            .whereEqualTo("verified","true")


        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(this@DiscoverpeopleActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){


                        val user = dc.document.toObject(User::class.java)

                        adaptar.add(discoverprecy(this@DiscoverpeopleActivity,user))


                    }

                }






            }


        })


    }


}