package com.youngminds.upmarkx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.*
import com.youngminds.upmarkx.Models.LeaderboardUser
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.Utils.EditprofileLoadingbar
import kotlinx.android.synthetic.main.activity_adminpanel.*

class AdminpanelActivity : AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore
    var usersuid = mutableListOf<String>()
    var clearuid = mutableListOf<String>()



    val loading = EditprofileLoadingbar(this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adminpanel)



        firestore = FirebaseFirestore.getInstance()



        admin_smstesting.setOnClickListener {

            val intent = Intent(this,SMSActivity::class.java)
            startActivity(intent)

        }

        admin_clearleaderboard.setOnClickListener {


            loading.startloading()
            clearleaderboard()

        }
        admin_uploadleaderboard.setOnClickListener {

            loading.startloading()
            uploadleaderboarduser()
        }



        admin_newsupdate.setOnClickListener {

            val intent = Intent(this,NewsfillingActivity::class.java)
            startActivity(intent)

        }


    }

    private fun clearleaderboard() {


        val dbclear = firestore.collection("Leaderboard")
        dbclear.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(this@AdminpanelActivity,"Something went wrong",Toast.LENGTH_SHORT).show()
                }


                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){

                        val user = dc.document.toObject(LeaderboardUser::class.java)

                        clearuid.add(user.uid.toString())




                    }

                }


            clear2user()






            }


        })


    }

    private fun clear2user(){



        for (i in clearuid){


            val dbdelete = firestore.collection("Leaderboard")
                .document(i)
            dbdelete.delete()

        }
        loading.isDismiss()

        Toast.makeText(this,"successfully deleted users",Toast.LENGTH_SHORT).show()


    }


    private fun uploadleaderboarduser(){


        val db = firestore.collection("UserInfo")
        db.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){


                    Toast.makeText(this@AdminpanelActivity,"Something went wrong", Toast.LENGTH_SHORT).show()


                }

                for (dc: DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){

                        val user = dc.document.toObject(User::class.java)

                        usersuid.add(user.uid.toString())


                    }


                }





                likescount(usersuid)



            }




        })










    }


    private fun likescount(uid:MutableList<String>){




        for (i in uid){

            Log.e("shaikhsahab",i)


            val db = firestore.collection("UserInfo")
                .document(i)
                .collection("Likes")

            db.addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    if (error!=null){

                        Toast.makeText(this@AdminpanelActivity,"Something went wrong", Toast.LENGTH_SHORT).show()

                    }

                    var likescount= 0


                    for (dc: DocumentChange in value?.documentChanges!!){


                        if (dc.type == DocumentChange.Type.ADDED){

                            likescount++

                        }

                    }


                    Log.e("shaikhsahab","$i -> likes: $likescount")


                    val luser = LeaderboardUser(i,likescount)

                    val dbadd = firestore.collection("Leaderboard")
                        .document(i)
                    dbadd.set(luser).addOnSuccessListener {

                        Toast.makeText(this@AdminpanelActivity,"Successfully completed",
                            Toast.LENGTH_SHORT).show()
                        Log.e("shaikhsahab","Successfully completed")


                    }





                }


            })




        }


        loading.isDismiss()


//        Log.e("shaikhsahab",uid.toString())


    }

}