package com.youngminds.upmarkx


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isEmpty
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.MembersActivity.Companion.USER_KEY
import com.youngminds.upmarkx.Models.DeliverChat
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.recyclerview.Latestmessrecy

import kotlinx.android.synthetic.main.activity_latest_messagese.*

class LatestMessageseActivity : AppCompatActivity() {



    private lateinit var firestore: FirebaseFirestore

    companion object{
        val  adaptar  =  GroupAdapter<ViewHolder>()
        val upperadaptar = GroupAdapter<ViewHolder>()

        var inuser: User?=null
        var message: DeliverChat?=null
        var count = 0


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messagese)


        firestore  =  FirebaseFirestore.getInstance()


        latestmess_recy.adapter =  adaptar


        retreiveuser()



        count++


//        if (count==1){
//
//            listenmessages()
//
//        }





        latestmess_upperrecy.adapter = upperadaptar





        laetestmess_refresh.setOnRefreshListener {



            adaptar.clear()
            retreiveuser()

            listenmessages()

            retreivefollowingonline()


            laetestmess_refresh.isRefreshing = false
        }


        latestmessage_bacbtn.setOnClickListener {
            finish()
        }







        adaptar.setOnItemClickListener { item, view ->


            val useritem  =  item  as Latestmessrecy

            val intent = Intent(this,ChatLogActivity::class.java)
            intent.putExtra(USER_KEY,useritem.outuser)
            startActivity(intent)

        }


        upperadaptar.setOnItemClickListener{ item, view ->


            val useritemup  =  item  as Latestmessupperrecy


            val intent = Intent(this,ChatLogActivity::class.java)
            intent.putExtra(USER_KEY,useritemup.user)
            startActivity(intent)


        }


//        latestmessage_searchview.addTextChangedListener(object : TextWatcher{
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                Log.e(MainActivity.TAG,s.toString())
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//                val intent  = Intent(applicationContext,SearchActivity::class.java)
////                intent.putExtra(MainActivity.TEXT,s.toString())
//                latestmessage_searchview.editableText.clear()
//                startActivity(intent)
//
//
//
//
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                Log.e(MainActivity.TAG,s.toString())
//            }
//
//
//        })




        retreivefollowingonline()
    }



    private fun retreivefollowingonline(){


        upperadaptar.clear()


        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

        val dbonline = firestore.collection("UserInfo")
            .document(currentuseruid)
            .collection("Following")

        dbonline.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if(error!=null){
                    Toast.makeText(applicationContext,"Something went wrong",Toast.LENGTH_SHORT).show()


                }


                for (dc:DocumentChange in value?.documentChanges!!){



                    if (dc.type == DocumentChange.Type.ADDED){


                        val user = dc.document.toObject(User::class.java)

                        Log.e("LatestMessage",user.firstname.toString())

                        retreiveonlineone(user)


                    }

                }



            }


        })



    }



    private fun retreiveonlineone(user:User){




        val db = firestore.collection("UserInfo")
            .whereEqualTo("uid",user.uid)


        db.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){


                    Toast.makeText(this@LatestMessageseActivity,"Something went wrong",Toast.LENGTH_SHORT).show()


                }


                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){

                        val uuser = dc.document.toObject(User::class.java)


                        if (uuser.status == "Online"){
                            upperadaptar.add(Latestmessupperrecy(applicationContext,uuser))

                        }



                        Log.e("LatestMessage",user.status.toString())

                    }

                }




            }


        })


        if (latestmess_upperrecy.isEmpty()){

            latestmess_upperrecy.visibility = View.VISIBLE

        }else{

            latestmess_upperrecy.visibility = View.GONE

        }


    }

    private fun retreiveuser() {

        val currentuseruid = FirebaseAuth.getInstance().uid.toString()


        val dbuser = firestore.collection("UserInfo").whereEqualTo("uid",currentuseruid)
        dbuser.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){
                    Toast.makeText(this@LatestMessageseActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){

                        inuser = dc.document.toObject(User::class.java)



                        Glide.with(applicationContext).load(inuser?.imageuri).into(latestmess_imageview)



                    }

                    if (dc.type == DocumentChange.Type.MODIFIED){


                        inuser = dc.document.toObject(User::class.java)


                    }

                }


            }
        })

    }





    private fun listenmessages() {



        val currentuseruid = FirebaseAuth.getInstance().uid.toString()
        val lamess  = firestore.collection("LatestMessages").document(currentuseruid).collection("messsages").orderBy("timestamp",Query.Direction.DESCENDING)
        lamess.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(this@LatestMessageseActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }



                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){

                        message  = dc.document.toObject(DeliverChat::class.java)



                        if (currentuseruid  != message?.outid || currentuseruid !=  message?.inid){

                            adaptar.add(
                                Latestmessrecy(applicationContext, message!!,
                                inuser!!)
                            )

                        }




                    }

//                    if (dc.type == DocumentChange.Type.MODIFIED){
//
//
//
//                        adaptar.clear()
//                        var count = 0
//
//
//                        count++
//                        if (count == 1){
//
//                            adaptar.clear()
//
//                            listenmessages()
//                        }
//
//
////                        message  = dc.document.toObject(DeliverChat::class.java)
////                        if (currentuseruid  != message?.outid || currentuseruid !=  message?.inid){
////
////                            adaptar.add(
////                                Latestmessrecy(applicationContext, message!!,
////                                inuser!!)
////                            )
////
////                        }
//
//
//                    }


                }


                latestmessage_shrimmerFrameLayout.stopShimmer()
                latestmessage_shrimmerFrameLayout.visibility = View.GONE




            }


        })


    }



    override fun onPause() {
        super.onPause()

        val currentuseid = FirebaseAuth.getInstance().uid.toString()



        val  userstatus = firestore.collection("UserInfo").document(currentuseid)
        userstatus.update("status","Offline")


        val lastseen  = firestore.collection("UserInfo").document(currentuseid)
        lastseen.update("timestamp",System.currentTimeMillis())


    }


    override fun onResume() {
        super.onResume()



        adaptar.clear()

        listenmessages()


        val currentuseid = FirebaseAuth.getInstance().uid.toString()

        val  userstatus = firestore.collection("UserInfo").document(currentuseid)
        userstatus.update("status","Online")
    }









}