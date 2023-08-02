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
import kotlinx.android.synthetic.main.horizontal_suggestioncard.view.*

class horisuggestionrecy(val context: Context , val user:User):Item<ViewHolder>() {


    private lateinit var firestore:FirebaseFirestore
    var followinguser = mutableListOf<String>()
    var currentuser:User?=null

    override fun bind(viewHolder: ViewHolder, position: Int) {


        firestore = FirebaseFirestore.getInstance()

        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

        viewHolder.itemView.hori_suggestioncard_nameview.text = "${user.firstname} ${user.lastname}"
        viewHolder.itemView.hori_suggestioncard_class.setText(user.standard)

        Glide.with(context).load(user.imageuri).into(viewHolder.itemView.hori_suggestioncard_imageview)



        if(user.verified == "true"){

            viewHolder.itemView.hori_suggestioncard_verified.visibility = View.VISIBLE

        }else{

            viewHolder.itemView.hori_suggestioncard_verified.visibility = View.GONE

        }





        val dbcurrentuser = firestore.collection("UserInfo")
            .whereEqualTo("uid",currentuseruid)

        dbcurrentuser.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()
                }

                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type  ==DocumentChange.Type.ADDED){


                        currentuser = dc.document.toObject(User::class.java)




                    }
                }

            }


        })







        val checkdb = firestore.collection("UserInfo")
            .document(currentuseruid)
            .collection("Following")
            .whereEqualTo("uid",user.uid)

        checkdb.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(context,"Something went wrong", Toast.LENGTH_SHORT).show()

                }

                for (dc: DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        viewHolder.itemView.hori_suggestioncard_followbtn.visibility = View.GONE
                        viewHolder.itemView.hori_suggestioncard_unfollowbtn.visibility = View.VISIBLE

                    }else{

                        viewHolder.itemView.hori_suggestioncard_followbtn.visibility = View.VISIBLE
                        viewHolder.itemView.hori_suggestioncard_unfollowbtn.visibility = View.GONE
                    }

                }


            }


        })



        viewHolder.itemView.hori_suggestioncard_followbtn.setOnClickListener {


            val db = firestore.collection("UserInfo")
                .document(currentuseruid)
                .collection("Following")
                .document(user.uid.toString())

            val dbi = firestore.collection("UserInfo")
                .document(user.uid.toString())
                .collection("Followers")
                .document(currentuseruid)

            db.set(user).addOnSuccessListener {

                currentuser.let { it1 ->
                    it1?.let { it2 ->
                        dbi.set(it2).addOnSuccessListener {


                            val checkdbin = firestore.collection("UserInfo")
                                .document(currentuseruid)
                                .collection("Following")
                                .whereEqualTo("uid",user.uid)

                            checkdbin.addSnapshotListener(object : EventListener<QuerySnapshot> {
                                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                                    if (error!=null){

                                        Toast.makeText(context,"Something went wrong", Toast.LENGTH_SHORT).show()

                                    }

                                    for (dc: DocumentChange in value?.documentChanges!!){

                                        if (dc.type == DocumentChange.Type.ADDED){

                                            viewHolder.itemView.hori_suggestioncard_followbtn.visibility = View.GONE
                                            viewHolder.itemView.hori_suggestioncard_unfollowbtn.visibility = View.VISIBLE

                                        }else{

                                            viewHolder.itemView.hori_suggestioncard_followbtn.visibility = View.VISIBLE
                                            viewHolder.itemView.hori_suggestioncard_unfollowbtn.visibility = View.GONE
                                        }

                                    }


                                }


                            })


                        }
                    }
                }


            }




        }




        viewHolder.itemView.hori_suggestioncard_unfollowbtn.setOnClickListener {


            val db = firestore.collection("UserInfo")
                .document(currentuseruid)
                .collection("Following")
                .document(user.uid.toString())


            val dbi = firestore.collection("UserInfo")
                .document(user.uid.toString())
                .collection("Followers")
                .document(currentuseruid)

            db.delete().addOnSuccessListener {

                  dbi.delete().addOnSuccessListener {


                    val checkdbin = firestore.collection("UserInfo")
                        .document(currentuseruid)
                        .collection("Following")
                        .whereEqualTo("uid",user.uid)

                    checkdbin.addSnapshotListener(object : EventListener<QuerySnapshot> {
                        override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                            if (error!=null){

                                Toast.makeText(context,"Something went wrong", Toast.LENGTH_SHORT).show()

                            }

                            for (dc: DocumentChange in value?.documentChanges!!){

                                if (dc.type == DocumentChange.Type.ADDED){

                                    viewHolder.itemView.hori_suggestioncard_followbtn.visibility = View.GONE
                                    viewHolder.itemView.hori_suggestioncard_unfollowbtn.visibility = View.VISIBLE

                                }else{

                                    viewHolder.itemView.hori_suggestioncard_followbtn.visibility = View.VISIBLE
                                    viewHolder.itemView.hori_suggestioncard_unfollowbtn.visibility = View.GONE
                                }

                            }


                        }


                    })



                } }


            }




        }








    override fun getLayout(): Int {

        return R.layout.horizontal_suggestioncard
    }


}