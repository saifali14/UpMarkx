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
import com.youngminds.upmarkx.recyclerview.discoverprecy
import com.youngminds.upmarkx.recyclerview.followerrecy
import kotlinx.android.synthetic.main.activity_following.*

class FollowingActivity : AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore

    val adaptar = GroupAdapter<ViewHolder>()

    companion object{

        var useruid: String?=null

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following)





        firestore = FirebaseFirestore.getInstance()


//        if (followers_searchview.text.isEmpty()){
//
//            retreivefollowers(useruid.toString())
//
//        }



        following_backbtn.setOnClickListener { finish() }



        adaptar.setOnItemClickListener { item, view ->



            val user = item as followerrecy

            val intent = Intent(this,UserProfileActivity::class.java)
            intent.putExtra(SearchActivity.USERK,user.user)
            startActivity(intent)

        }


        following_recyclerview.adapter = adaptar

       useruid = intent.getStringExtra(UserProfileActivity.USER_UID)

       useruid?.let { retreivefollowers(it) }




        following_searchview.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {



            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {



                adaptar.clear()

                val seaerchuser = firestore.collection("UserInfo")
                    .document(useruid.toString())
                    .collection("Following")
                    .whereArrayContains("keywords",

                        s?.trim().toString().toLowerCase()).limit(100)
                seaerchuser.addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {


                        if (error!=null){

                            Toast.makeText(this@FollowingActivity,"Network issue is there",
                                Toast.LENGTH_SHORT).show()


                        }

                        for (dc: DocumentChange in value?.documentChanges!!){


                            if (dc.type == DocumentChange.Type.ADDED){

                                val user = dc.document.toObject(User::class.java)

                                adaptar.add(followerrecy(this@FollowingActivity,user))

                            }

                        }


                    }


                })


                if (following_searchview.text.isEmpty()){

                    retreivefollowers(useruid.toString())

                }




            }

            override fun afterTextChanged(s: Editable?) {

            }


        })



    }

    private fun retreivefollowers(useruid:String) {


        val db = firestore.collection("UserInfo").document(useruid)
            .collection("Following")



        db.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(this@FollowingActivity,"Something went wrong", Toast.LENGTH_SHORT).show()

                }


                for (dc: DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        val user = dc.document.toObject(User::class.java)


                        following_emptylottie.visibility = View.GONE

                        adaptar.add(followerrecy(this@FollowingActivity,user))


                    }else{


                        following_emptylottie.visibility = View.VISIBLE


                    }



                }


            }


        })






    }


}
