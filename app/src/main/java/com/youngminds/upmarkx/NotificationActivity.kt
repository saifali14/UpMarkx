package com.youngminds.upmarkx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.NotiAnswer
import com.youngminds.upmarkx.Models.Request
import com.youngminds.upmarkx.recyclerview.Communitychildrecy
import com.youngminds.upmarkx.recyclerview.Notifyanswerrecy
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore
    val adaptar = GroupAdapter<ViewHolder>()

    companion object{



    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        firestore = FirebaseFirestore.getInstance()

        notification_recyclerview.adapter  =  adaptar




        notification_messagebtn.setOnClickListener {
            val intent = Intent(this, LatestMessageseActivity::class.java)
            startActivity(intent)
        }

        adaptar.setOnItemClickListener { item, view ->


            val notification = item as Notifyanswerrecy

            val intent = Intent(this,MoreanswersActivity::class.java)
            intent.putExtra(Communitychildrecy.MORE_QUESTION,notification.message.questionid)
            startActivity(intent)
        }



        notification_backbtn.setOnClickListener {
            finish()
        }

        retreivenotification()


    }


    private fun retreivenotification() {


        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

        val notifydb = firestore.collection("UserInfo")
            .document(currentuseruid)
            .collection("Notifications")


        notifydb.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {



                if (error!=null){


                    Toast.makeText(this@NotificationActivity,"Something went wrong",Toast.LENGTH_SHORT)
                }


                var count = 0


                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){


                        count++

                        val notifications = dc.document.toObject(NotiAnswer::class.java)


                        adaptar.add(Notifyanswerrecy(applicationContext,notifications))


                    }

                }

                if (count == 0){

                    notification_lottie.visibility = View.VISIBLE
                }else{
                    notification_lottie.visibility = View.GONE
                }


            }


        })

    }


//    private fun retreiverequests() {
//
//
//        val currentuseruid = FirebaseAuth.getInstance().uid.toString()
//
//
//        val dbrequests =  firestore.collection("UserInfo")
//            .document(currentuseruid)
//            .collection("Requests")
//
//
//        dbrequests.addSnapshotListener(object  :EventListener<QuerySnapshot>{
//            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
//
//
//                adaptar.clear()
//
//                if (error!=null){
//
//                    Toast.makeText(this@NotificationActivity,"Something went wrong",Toast.LENGTH_SHORT).show()
//
//                }
//
//                for (dc:DocumentChange in value?.documentChanges!!){
//
//                    if (dc.type == DocumentChange.Type.ADDED){
//
//                        val request = dc.document.toObject(Request::class.java)
//
//                        adaptar.add(requestrecy(this@NotificationActivity,request))
//
//
//                    }
//
//                    if (dc.type == DocumentChange.Type.REMOVED){
//
//
//                       retreiverequests()
//                    }
//
//                }
//
//            }
//
//
//        })
//
//
//    }


}