package com.youngminds.upmarkx


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.youngminds.upmarkx.Models.LeaderboardUser
import com.youngminds.upmarkx.Models.User




   const val TOPIC = "/topics/myTopic"

class LeaderboardAdminActivity : AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore



       var usersuid = mutableListOf<String>()
    var usersuggest = mutableListOf<String>()
    var finalsuggest = mutableListOf<User>()

    val TAG = "LeaderboardAdminActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard_admin)

        firestore = FirebaseFirestore.getInstance()



//        allusersuid()


//        retreiveusers()


        uploadleaderboarduser()

//        suggestionalgorithem()





//        admin_sendbtn.setOnClickListener {
//
////
////            val title = admin_title_edtxt.text.toString()
////            val message = admin_message_edtxt.text.toString()
////
////
////            if (title.isNotEmpty() && message.isNotEmpty()){
////
////
////                PushNotification(NotificationData(title, message), TOPIC)
////
////                    .also {
////
////                        sendNotification(it)
////
////
////                    }
////
////            }
//
//        }





        setupPiechat()
        loadpiechat()






    }



    private fun setupPiechat(){

//        piechat.isDrawHoleEnabled = true
//        piechat.isUsePercentValuesEnabled
//        piechat.setEntryLabelTextSize(13F)
//        piechat.setEntryLabelColor(Color.WHITE)
//        piechat.centerText = "Overview"
//        piechat.setCenterTextSize(24F)
//        piechat.description.isEnabled = true
//        piechat.setUsePercentValues(true)




    }

    private fun loadpiechat(){

//
//        val entries = ArrayList<PieEntry>()
//
//        entries.add(PieEntry(25F,"Answers Given"))
//        entries.add(PieEntry(25F,"Questions Asked"))
//        entries.add(PieEntry(25F,"Thanks"))
//        entries.add(PieEntry(25F,"Reported"))
//
//
//        val colors = ArrayList<Int>()
//
//        for (color in ColorTemplate.MATERIAL_COLORS){
//
//            colors.add(color)
//
//        }
//
//
//        for (color in ColorTemplate.VORDIPLOM_COLORS){
//
//            colors.add(color)
//
//        }
//
//
//        val dataset = PieDataSet(entries,"Overview")
//        dataset.setColors(colors)
//
//
//
//        val data = PieData(dataset)
//        data.setDrawValues(true)
//        data.setValueTextColor(Color.WHITE)
//        data.setValueFormatter(PercentFormatter(piechat))
//        data.setValueTextSize(12f)
//        data.setValueTextColor(Color.WHITE)
//
//
//        piechat.data = data
//        piechat.invalidate()
//
//        piechat.animateY(1400,Easing.EaseInOutQuad)



    }





//    private fun  sendNotification(notification:PushNotification) = CoroutineScope(Dispatchers.IO).launch{
//
//
//        try {
//
//            val response = RetrofitInstance.api.postNotification(notification)
//
//            if (response.isSuccessful){
//
//
//                Log.e(TAG,"Response: ${Gson().toJson(response)}")
//
//
//            }else{
//
//                Log.e(TAG,response.errorBody().toString())
//
//
//
//            }
//
//
//        }catch (e:Exception){
//
//            Log.e(TAG,e.toString())
//
//
//        }
//
//
//    }


    private fun suggestionalgorithem() {

        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

        val db = firestore.collection("UserInfo")
            .document(currentuseruid)
            .collection("Following")


        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(this@LeaderboardAdminActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }


                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        val user = dc.document.toObject(User::class.java)


                        usersuggest.add(user.uid.toString())



                    }

                }

                suggestionuser(usersuggest)

//                Log.e("shaikhsahab",usersuggest.toString())


            }


        })



    }


    private fun suggestionuser(uid:MutableList<String>){
        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

        for (i in uid){

            val db = firestore.collection("UserInfo")
                .document(i)
                .collection("Following")



         db.addSnapshotListener(object :EventListener<QuerySnapshot>{
             override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                 if (error!=null){

                     Toast.makeText(this@LeaderboardAdminActivity,"Something went wrong",Toast.LENGTH_SHORT).show()
                 }


                 for (dc:DocumentChange in value?.documentChanges!!){

                     if (dc.type == DocumentChange.Type.ADDED){


                         val users = dc.document.toObject(User::class.java)


                         Log.e("shaikhsahab" ,"all the suggestion users:  "+  users)
                         val dbi = firestore.collection("UserInfo")
                             .document(currentuseruid)
                             .collection("UserSuggestion")
                             .document(users.uid.toString())


                         dbi.set(users)

//                         finalsuggest.add(users)
                     }

                 }




//                 Log.e("shaikhsahab", "suggestion userse = "+finalsuggest.toString())

             }


         })


        }



    }


    private fun addsuggestusers(users:MutableList<User>){

        val currentuseruid = FirebaseAuth.getInstance().uid.toString()


        for (i in users){





        }




    }

    private fun retreiveusers() {


        val db = firestore.collection("Leaderboard")
            .orderBy("likes",Query.Direction.DESCENDING)


        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {



                var index= 0

                if (error!=null){

                    Toast.makeText(this@LeaderboardAdminActivity,"Something went wrong",Toast.LENGTH_SHORT).show()
                }


                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        index++


                      val user = dc.document.toObject(LeaderboardUser::class.java)

                        Log.e("shaikhsahab"," index:$index   ${user.uid} -> ${user.likes}")

                    }

                }

            }


        })


    }


    private fun uploadleaderboarduser(){


        val db = firestore.collection("UserInfo")
            db.addSnapshotListener(object :EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    if (error!=null){


                        Toast.makeText(this@LeaderboardAdminActivity,"Something went wrong",Toast.LENGTH_SHORT).show()


                    }

                    for (dc:DocumentChange in value?.documentChanges!!){


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

            db.addSnapshotListener(object :EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    if (error!=null){

                        Toast.makeText(this@LeaderboardAdminActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                    }

                    var likescount= 0


                    for (dc:DocumentChange in value?.documentChanges!!){


                        if (dc.type == DocumentChange.Type.ADDED){

                            likescount++

                        }

                    }


                    Log.e("shaikhsahab","$i -> likes: $likescount")


                    val luser = LeaderboardUser(i,likescount)

                    val dbadd = firestore.collection("Leaderboard")
                        .document(i)
                    dbadd.set(luser).addOnSuccessListener {

                        Toast.makeText(this@LeaderboardAdminActivity,"Successfully completed",Toast.LENGTH_SHORT).show()
                        Log.e("shaikhsahab","Successfully completed")


                    }





                }


            })




        }


//        Log.e("shaikhsahab",uid.toString())


    }

    private fun uploadtopusers() {


        val useruid ="hdhddhshhh"

        val leaderboarduser =  Leaderboarduser("hdhddhshhh","2","Zayn","","89","10","zayn@gmail.com")

        val db= firestore.collection("Leaderboard").document(useruid)
        db.set(leaderboarduser)


        val useruid1 ="dkhhfhhshkf"

        val leaderboarduser1 =  Leaderboarduser("dkhhfhhshkf","3","Ariana","","77","10","ariana@gmail.com")

        val db1= firestore.collection("Leaderboard").document(useruid1)
        db1.set(leaderboarduser1)


        val useruid2 ="hfshfhhfhfk"

        val leaderboarduser2 =  Leaderboarduser("hfshfhhfhfk","4","Taylor","","76","10","taylor@gmail.com")

        val db2= firestore.collection("Leaderboard").document(useruid2)
        db2.set(leaderboarduser2)



        val useruid3 ="dhshhkhhfs"

        val leaderboarduser3 =  Leaderboarduser("dhshhkhhfs","5","Weekend","","70","10","weekend@gmail.com")

        val db3= firestore.collection("Leaderboard").document(useruid3)
        db3.set(leaderboarduser3)


        val useruid4 ="hhgalhisho"

        val leaderboarduser4 =  Leaderboarduser("hhgalhisho","6","Selena","","69","10","selena@gmail.com")

        val db4= firestore.collection("Leaderboard").document(useruid4)
        db4.set(leaderboarduser4)


        val useruid5 ="ksadkllgd"

        val leaderboarduser5 =  Leaderboarduser("ksadkllgd","7","Justin","","63","10","justin@gmail.com")

        val db5= firestore.collection("Leaderboard").document(useruid5)
        db5.set(leaderboarduser5)


        val useruid6 ="dngnskldnknlknl"

        val leaderboarduser6 =  Leaderboarduser("dngnskldnknlknl","8","Billie","","60","10","billie@gmail.com")

        val db6= firestore.collection("Leaderboard").document(useruid6)
        db6.set(leaderboarduser6)



        val useruid7 ="hhsssshhss"

        val leaderboarduser7 =  Leaderboarduser("hhsssshhss","9","Atif","","55","9","atif@gmail.com")

        val db7= firestore.collection("Leaderboard").document(useruid7)
        db7.set(leaderboarduser7)



        val useruid8 ="kdkhfhk"

        val leaderboarduser8 =  Leaderboarduser("kdkhfhk","10","Arjit","","33","8","arjit@gmail.com")

        val db8= firestore.collection("Leaderboard").document(useruid8)
        db8.set(leaderboarduser8)



    }




}