package com.youngminds.upmarkx.recyclerview

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.*
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.Comments
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.R
import com.youngminds.upmarkx.SearchActivity
import com.youngminds.upmarkx.UserProfileActivity
import com.youngminds.upmarkx.Utils.TimeStamp
import kotlinx.android.synthetic.main.commentcard.view.*

class moreans_commentsrecy(val context:Context, val comments: Comments):Item<ViewHolder>() {

    private lateinit var firestore: FirebaseFirestore

    companion object{

        var user:User?=null

    }


    override fun bind(viewHolder: ViewHolder, position: Int) {

        firestore = FirebaseFirestore.getInstance()






        viewHolder.itemView.commentcard_time.text = comments.time?.let {
            TimeStamp.getTimeAgo(
                it
            )
        }

        viewHolder.itemView.commentcard_comment.text = comments.comment


        val dbuser = firestore.collection("UserInfo")
            .whereEqualTo("uid",comments.userid)

        dbuser.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()
                }


                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                       user = dc.document.toObject(User::class.java)

                        viewHolder.itemView.commentcard_name_class.text = "${user?.firstname} ${user?.lastname} . ${user?.standard}"
                        Glide.with(context).load(user?.imageuri).into(viewHolder.itemView.commentcard_imageview)







                    }


                }




            }


        })



        val dblikescount = firestore.collection("CommunityQuestions")
            .document(comments.questionid.toString())
            .collection("Answers")
            .document(comments.answerid.toString())
            .collection("Comments")
            .document(comments.commentid.toString())
            .collection("Likes")

            dblikescount.addSnapshotListener{ value , error ->



                var likescount = 0

                if (error!=null){

                    Toast.makeText(context,"Somwthing went wrong",Toast.LENGTH_SHORT).show()
                }


                for (dc:DocumentChange in value?.documentChanges!!){



                    if (dc.type == DocumentChange.Type.ADDED){



                        likescount++

                    }

                }
                viewHolder.itemView.commentcard_numlikes.text = "Likes $likescount"


            }




        val dbchecklike = firestore.collection("CommunityQuestions")
            .document(comments.questionid.toString())
            .collection("Answers")
            .document(comments.answerid.toString())
            .collection("Comments")
            .document(comments.commentid.toString())
            .collection("Likes")
            .whereEqualTo("uid", user?.uid)


        dbchecklike.addSnapshotListener { value, error ->


            if (error!=null){

                Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()
            }


            for (dc:DocumentChange in value?.documentChanges!!){


                if (dc.type == DocumentChange.Type.ADDED){


                    viewHolder.itemView.commentcard_like.visibility = View.GONE
                    viewHolder.itemView.commentcard_liked.visibility = View.VISIBLE

                }else{


                    viewHolder.itemView.commentcard_like.visibility = View.VISIBLE
                    viewHolder.itemView.commentcard_liked.visibility = View.GONE
                }


            }




        }




        viewHolder.itemView.commentcard_like.setOnClickListener {


            val dblike = firestore.collection("CommunityQuestions")
                .document(comments.questionid.toString())
                .collection("Answers")
                .document(comments.answerid.toString())
                .collection("Comments")
                .document(comments.commentid.toString())
                .collection("Likes")
                .document(user?.uid.toString())

            dblike.set(user!!).addOnSuccessListener {




                val dbchecklikei = firestore.collection("CommunityQuestions")
                    .document(comments.questionid.toString())
                    .collection("Answers")
                    .document(comments.answerid.toString())
                    .collection("Comments")
                    .document(comments.commentid.toString())
                    .collection("Likes")
                    .whereEqualTo("uid", user?.uid)


                dbchecklikei.addSnapshotListener { value, error ->


                    if (error!=null){

                        Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()
                    }


                    for (dc:DocumentChange in value?.documentChanges!!){


                        if (dc.type == DocumentChange.Type.ADDED){


                            viewHolder.itemView.commentcard_like.visibility = View.GONE
                            viewHolder.itemView.commentcard_liked.visibility = View.VISIBLE

                        }else{


                            viewHolder.itemView.commentcard_like.visibility = View.VISIBLE
                            viewHolder.itemView.commentcard_liked.visibility = View.GONE
                        }


                    }




                }




            }






        }




        viewHolder.itemView.commentcard_imageview.setOnClickListener {

            val intent = Intent(context, UserProfileActivity::class.java)
            intent.putExtra(SearchActivity.USERK, user)
            context.startActivity(intent)

        }





    }

    override fun getLayout(): Int {


        return R.layout.commentcard
    }
}