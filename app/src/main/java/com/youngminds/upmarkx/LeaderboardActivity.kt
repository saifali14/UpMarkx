package com.youngminds.upmarkx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.LeaderboardUser
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.recyclerview.Leaderrecy
import com.youngminds.upmarkx.recyclerview.home_leaderboard_recy
import kotlinx.android.synthetic.main.activity_leaderboard.*

class LeaderboardActivity : AppCompatActivity() {

    val adaptar = GroupAdapter<ViewHolder>()
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        firestore  = FirebaseFirestore.getInstance()



        leaderboar_recyclerview.adapter =adaptar



        leaderboard_aboutbtn.setOnClickListener {

            val intent = Intent(this,LeaderboardaboutActivity::class.java)
            startActivity(intent)

        }

        leaderboard_backbtn.setOnClickListener {

            finish()

        }
       retreiveuser()

    }

    private fun retreiveuser() {


        val db = firestore.collection("Leaderboard").orderBy("likes",Query.Direction.DESCENDING)
            .limit(100)
        db.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {



                var rank = 0


                if (error!=null){

                    Toast.makeText(this@LeaderboardActivity,"Network issue is there",Toast.LENGTH_SHORT).show()

                }


                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type== DocumentChange.Type.ADDED){

                        rank++

                        val user = dc.document.toObject(LeaderboardUser::class.java)


                        when(rank){


                            1 ->{

                               firstcard(rank,user)

                            }


                            2 ->{


                                secondcard(rank,user)
                            }


                            3->{


                                thirdcard(rank,user)
                            }

                        }



                        if (rank >= 4){


                            adaptar.add(home_leaderboard_recy(this@LeaderboardActivity,user,rank.toString()))
                        }






                    }

                }

                leaderboard_shrimmer.stopShimmer()
                leaderboard_shrimmer.visibility = View.GONE

            }


        })


    }



    private fun firstcard(rank:Int , user: LeaderboardUser){




        val db = firestore.collection("UserInfo")
            .whereEqualTo("uid",user.uid)


        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(this@LeaderboardActivity,"Something went wrong",Toast.LENGTH_SHORT).show()
                }

                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){


                        val users = dc.document.toObject(User::class.java)


                        leaderboard_first_name.text = users.firstname
                        leaderboard_first_class.text = users.standard
                        Glide.with(applicationContext).load(users.imageuri).into(leaderboard_first_imagview)
                        leaderboard_first_likes.text = user.likes.toString()




                    }
                }


            }


        })


    }

    private fun secondcard(rank:Int , user: LeaderboardUser){

        val db = firestore.collection("UserInfo")
            .whereEqualTo("uid",user.uid)


        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(this@LeaderboardActivity,"Something went wrong",Toast.LENGTH_SHORT).show()
                }

                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){


                        val users = dc.document.toObject(User::class.java)


                        leaderboard_second_name.text = users.firstname
                        leaderboard_second_class.text = users.standard
                        Glide.with(applicationContext).load(users.imageuri).into(leaderboard_second_imagview)
                        leaderboard_second_likes.text = user.likes.toString()



                    }
                }


            }


        })

    }


    private fun thirdcard(rank:Int , user: LeaderboardUser){

        val db = firestore.collection("UserInfo")
            .whereEqualTo("uid",user.uid)


        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(this@LeaderboardActivity,"Something went wrong",Toast.LENGTH_SHORT).show()
                }

                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){


                        val users = dc.document.toObject(User::class.java)


                        leaderboard_third_name.text = users.firstname
                        leaderboard_third_class.text = users.standard
                        Glide.with(applicationContext).load(users.imageuri).into(leaderboard_third_imagview)
                        leaderboard_third_likes.text = user.likes.toString()



                    }
                }


            }


        })

    }


}