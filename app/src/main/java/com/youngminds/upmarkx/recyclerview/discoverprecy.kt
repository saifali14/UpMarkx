package com.youngminds.upmarkx.recyclerview

import android.content.Context
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.R
import kotlinx.android.synthetic.main.discoverp_card.view.*

class discoverprecy(val context: Context, val user:User):Item<ViewHolder>() {



    companion object{

        var currentuser:User?=null

    }

    private lateinit var firestore: FirebaseFirestore

    override fun bind(viewHolder: ViewHolder, position: Int) {


        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

        firestore = FirebaseFirestore.getInstance()


        viewHolder.itemView.discoverpc_username.text = "${user.firstname} ${user.lastname}"
        viewHolder.itemView.discoverpc_class.setText(user.standard)
        Glide.with(context).load(user.imageuri).into(viewHolder.itemView.discoverpc_userimage)






        if (user.verified == "true"){

            viewHolder.itemView.discoverpc_verified.visibility = View.VISIBLE

        }else{
            viewHolder.itemView.discoverpc_verified.visibility = View.GONE

        }



        val userdb = firestore.collection("UserInfo")

            .whereEqualTo("uid",currentuseruid)

        userdb.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!) {

                    if (dc.type == DocumentChange.Type.ADDED) {

                        currentuser = dc.document.toObject(User::class.java)

                    }
                }


            }


        })

        val checkdb = firestore.collection("UserInfo")
            .document(currentuseruid)
            .collection("Following")
            .whereEqualTo("uid",user.uid)

        checkdb.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        viewHolder.itemView.discoverpc_followbtn.visibility = View.GONE
                        viewHolder.itemView.discoverpc_unfollowbtn.visibility = View.VISIBLE

                    }else{

                        viewHolder.itemView.discoverpc_followbtn.visibility = View.VISIBLE
                        viewHolder.itemView.discoverpc_unfollowbtn.visibility = View.GONE
                    }

                }


            }


        })



        viewHolder.itemView.discoverpc_followbtn.setOnClickListener {


            val db = firestore.collection("UserInfo")
                .document(currentuseruid)
                .collection("Following")
                .document(user.uid.toString())

            val dbi = firestore.collection("UserInfo")
                .document(user.uid.toString())
                .collection("Followers")
                .document(currentuseruid)

            db.set(user).addOnSuccessListener {

                currentuser?.let { it1 -> dbi.set(it1).addOnSuccessListener {


                    val checkdbin = firestore.collection("UserInfo")
                        .document(currentuseruid)
                        .collection("Following")
                        .whereEqualTo("uid",user.uid)

                    checkdbin.addSnapshotListener(object :EventListener<QuerySnapshot>{
                        override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                            if (error!=null){

                                Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()

                            }

                            for (dc:DocumentChange in value?.documentChanges!!){

                                if (dc.type == DocumentChange.Type.ADDED){

                                    viewHolder.itemView.discoverpc_followbtn.visibility = View.GONE
                                    viewHolder.itemView.discoverpc_unfollowbtn.visibility = View.VISIBLE

                                }else{

                                    viewHolder.itemView.discoverpc_followbtn.visibility = View.VISIBLE
                                    viewHolder.itemView.discoverpc_unfollowbtn.visibility = View.GONE
                                }

                            }


                        }


                    })



                } }


            }




        }




        viewHolder.itemView.discoverpc_unfollowbtn.setOnClickListener {


            val db = firestore.collection("UserInfo")
                .document(currentuseruid)
                .collection("Following")
                .document(user.uid.toString())


            val dbi = firestore.collection("UserInfo")
                .document(user.uid.toString())
                .collection("Followers")
                .document(currentuseruid)

            db.delete().addOnSuccessListener {

                currentuser?.let { it1 -> dbi.delete().addOnSuccessListener {


                    val checkdbin = firestore.collection("UserInfo")
                        .document(currentuseruid)
                        .collection("Following")
                        .whereEqualTo("uid",user.uid)

                    checkdbin.addSnapshotListener(object :EventListener<QuerySnapshot>{
                        override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                            if (error!=null){

                                Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()

                            }

                            for (dc:DocumentChange in value?.documentChanges!!){

                                if (dc.type == DocumentChange.Type.ADDED){

                                    viewHolder.itemView.discoverpc_followbtn.visibility = View.GONE
                                    viewHolder.itemView.discoverpc_unfollowbtn.visibility = View.VISIBLE

                                }else{

                                    viewHolder.itemView.discoverpc_followbtn.visibility = View.VISIBLE
                                    viewHolder.itemView.discoverpc_unfollowbtn.visibility = View.GONE
                                }

                            }


                        }


                    })



                } }


            }




        }







    }


    override fun getLayout(): Int {

        return R.layout.discoverp_card
    }



}