package com.youngminds.upmarkx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.recyclerview.followerrecy
import kotlinx.android.synthetic.main.activity_folllowers.*

class folllowersActivity : AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore

    val adaptar = GroupAdapter<ViewHolder>()

    companion object{

        var useruid: String?=null

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folllowers)



        firestore = FirebaseFirestore.getInstance()


//        if (followers_searchview.text.isEmpty()){
//
//            retreivefollowers(useruid.toString())
//
//        }




        followers_backbtn.setOnClickListener { finish() }


        adaptar.setOnItemClickListener { item, view ->



            val user = item as followerrecy

            val intent = Intent(this,UserProfileActivity::class.java)
            intent.putExtra(SearchActivity.USERK,user.user)
            startActivity(intent)

        }


        followers_recyclerview.adapter = adaptar

        useruid = intent.getStringExtra(UserProfileActivity.USER_UID)

        useruid?.let { retreivefollowers(it) }




        followers_searchview.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {



            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {



                adaptar.clear()

                val seaerchuser = firestore.collection("UserInfo")
                    .document(useruid.toString())
                    .collection("Followers")
                    .whereArrayContains("keywords",

                    s?.trim().toString().toLowerCase()).limit(100)
                seaerchuser.addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {


                        if (error!=null){

                            Toast.makeText(this@folllowersActivity,"Network issue is there",
                                Toast.LENGTH_SHORT).show()


                        }

                        for (dc: DocumentChange in value?.documentChanges!!){


                            if (dc.type == DocumentChange.Type.ADDED){

                                val user = dc.document.toObject(User::class.java)

                                adaptar.add(followerrecy(this@folllowersActivity,user))

                            }

                        }


                    }


                })


                if (followers_searchview.text.isEmpty()){

                    retreivefollowers(useruid.toString())

                }




            }

            override fun afterTextChanged(s: Editable?) {

            }


        })



    }

    private fun retreivefollowers(useruid:String) {


        val db = firestore.collection("UserInfo").document(useruid)
            .collection("Followers")



        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(this@folllowersActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }


                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        val user = dc.document.toObject(User::class.java)


                        adaptar.add(followerrecy(this@folllowersActivity,user))

                        follower_emptylottie.visibility = View.GONE


                    }else{
                        follower_emptylottie.visibility = View.VISIBLE

                    }



                }


            }


        })






    }


}