package com.youngminds.upmarkx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.*
import com.youngminds.upmarkx.Models.Leaderboardabout
import kotlinx.android.synthetic.main.activity_leaderboardabout.*

class LeaderboardaboutActivity : AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboardabout)



        firestore = FirebaseFirestore.getInstance()


        leaderboardabout_backbtn.setOnClickListener {

            finish()
        }


        val db = firestore.collection("LeaderBoardAbout")
            .whereEqualTo("About","Rules")


        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(this@LeaderboardaboutActivity,"Something went wrong",Toast.LENGTH_SHORT).show()
                }


                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){


                        val leaderboardabout = dc.document.toObject(Leaderboardabout::class.java)


                        leaderboardabout_info.setText(leaderboardabout.info)

                    }

                }


            }


        })


    }
}