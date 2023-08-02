package com.youngminds.upmarkx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.MembersActivity.Companion.USER_KEY
import com.youngminds.upmarkx.Models.Answer
import com.youngminds.upmarkx.Models.Question
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.fragments.MeProfileFragment
import com.youngminds.upmarkx.recyclerview.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.home_questionscard.view.*
import kotlinx.android.synthetic.main.horizontal_suggestioncard.view.*
import kotlinx.android.synthetic.main.profileshare_layout.view.*

class UserProfileActivity : AppCompatActivity() {



//    val adaptar = GroupAdapter<ViewHolder>()
    val followeradaptar  =  GroupAdapter<ViewHolder>()
    val followingadaptar = GroupAdapter<ViewHolder>()
    val shareprofileadaptar = GroupAdapter<ViewHolder>()
    val questionsaskedadaptar= GroupAdapter<ViewHolder>()
    val suggestionadaptar = GroupAdapter<ViewHolder>()
    private lateinit var firestore: FirebaseFirestore
    companion object{

        var  user: User?=null
        var currentuser: User?=null
        val USER_UID = "USER_UID"
        var usersuggest = mutableListOf<String>()
        var followingusers = mutableListOf<String>()
        var followingusercheck = mutableListOf<String>()


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        firestore  = FirebaseFirestore.getInstance()

        user = intent.getParcelableExtra(SearchActivity.USERK)
        Glide.with(applicationContext).load(user?.imageuri).into(userprofile_imageview)
        userprofile_nameview.text  = "${user?.firstname} ${user?.lastname}"
        userprofile_emailview.text = user?.email
        userprofile_headerUsername.text = user?.firstname

        userprofile_bioview.setText(user?.bio)

        Glide.with(applicationContext).load(user?.imageuri).into(userprofile_header_imageview)


        if (user?.verified == "true"){

            userprofile_verified.visibility = View.VISIBLE

        }else{

            userprofile_verified.visibility = View.GONE
        }







        userprofile_followerscard.setOnClickListener {

            val intent = Intent(this,folllowersActivity::class.java)
            intent.putExtra(USER_UID, user?.uid)
            startActivity(intent)

        }


        userprofile_followingcard.setOnClickListener {
            val intent = Intent(this,FollowingActivity::class.java)
            intent.putExtra(USER_UID, user?.uid)
            startActivity(intent)

        }


//
//
//        userprofile_recy.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//        userprofile_recy.adapter =adaptar







        followbtnstatus()
        retreivefollowing()

        retreivecurrentuser()

        usersuggestion()

        retrivefollowers()


        retreivelikes()

        retreiveanswer()




        userprofile_messagebtn.setOnClickListener {

            val intent = Intent(this,LatestMessageseActivity::class.java)
            startActivity(intent)

        }



        userprofile_following_viewall.setOnClickListener {


            val intent = Intent(this,FollowingActivity::class.java)
            intent.putExtra(USER_UID, user?.uid)
            startActivity(intent)



        }



        userprofile_followers_viewall.setOnClickListener {


            val intent = Intent(this,folllowersActivity::class.java)
            intent.putExtra(USER_UID, user?.uid)
            startActivity(intent)



        }


        userprofile_recentlyasked_viewall.setOnClickListener {

            val intent = Intent(this,UserAllQuestionsActivity::class.java)
            intent.putExtra(SearchActivity.USERK, user)
            startActivity(intent)


        }


        suggestionadaptar.setOnItemClickListener{ item , _ ->


            val user = item as horisuggestionrecy

            val intent = Intent(applicationContext,UserProfileActivity::class.java)
            intent.putExtra(SearchActivity.USERK,user.user)
            startActivity(intent)



        }

        questionsaskedadaptar.setOnItemClickListener { item, _ ->


            val question = item as userprofile_askedquesrecy
            val intent  = Intent(applicationContext,GiveAnswerActivity::class.java)
            intent.putExtra(Communityparentrecy.QUESTION_KEY,question.questions.questionid)
            startActivity(intent)

        }

        followeradaptar.setOnItemClickListener { item, _ ->


            val user  = item as Meprofile_followers_recy
            val intent = Intent(applicationContext, UserProfileActivity::class.java)
            intent.putExtra(MeProfileFragment.USERK,user.user)
            startActivity(intent)
        }
        followingadaptar.setOnItemClickListener { item, _ ->


            val user  = item as Meprofile_followers_recy
            val intent = Intent(applicationContext, UserProfileActivity::class.java)
            intent.putExtra(MeProfileFragment.USERK,user.user)
            startActivity(intent)
        }

        userprofile_followers_recyclerview.adapter = followeradaptar
        userprofile_following_recyclerview.adapter = followingadaptar
        userprofile_questionsked_recyclerview.adapter = questionsaskedadaptar
        userprofile_suggestionrecy.adapter =  suggestionadaptar




        retreivequestions(user?.uid.toString())



        if (user == null){

            userprofile_messsagebtn.visibility = View.GONE

        }




        userprofile_backbtn.setOnClickListener {

            finish()
        }





        userprofile_followbtn.setOnClickListener {

            followingadaptar.clear()
            followeradaptar.clear()

                adduser()


        }
        userprofile_followingbtn.setOnClickListener {


            followingadaptar.clear()
            followeradaptar.clear()
            unfollowuser()

        }



        userprofile_messsagebtn.setOnClickListener {

            val intent = Intent(this,ChatLogActivity::class.java)
            intent.putExtra(USER_KEY,user)
            startActivity(intent)

        }




        userprofile_sharebtn.setOnClickListener {


            shareprofileadaptar.clear()

            val currentuseruid = FirebaseAuth.getInstance().uid.toString()


            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.profileshare_layout,null)

            view.shareprofile_recyclerview.adapter = shareprofileadaptar


            val dbshare = firestore.collection("UserInfo")
                .document(currentuseruid)
                .collection("Following")



            dbshare.addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    if(error!=null){

                        Toast.makeText(this@UserProfileActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                    }


                    for (dc:DocumentChange in value?.documentChanges!!){


                        if (dc.type == DocumentChange.Type.ADDED){


                            val users = dc.document.toObject(User::class.java)

                            shareprofileadaptar.add(
                                shareprofilerecy(this@UserProfileActivity,users,
                                user!!)
                            )

                            followingusers.add(users.uid.toString())

                        }


                    }



                }


            })



            dialog.setContentView(view)
            dialog.show()


        }


//        retreviewquestions()
//        retreivelikes()

    }



    private fun retreivefollowing(){


        followingadaptar.clear()


        val db  = firestore.collection("UserInfo").document(user?.uid.toString())
            .collection("Following")


        db.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {



                var count =0
                var followingcount = 0

                if (error!=null){

                    Toast.makeText(this@UserProfileActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }
                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type ==  DocumentChange.Type.ADDED){


                        count++


                        userprofile_following_emptylottie.visibility  =View.GONE





                        val user = dc.document.toObject(User::class.java)


                        if (count <= 7){

                            followingadaptar.add(Meprofile_followers_recy(applicationContext,user))
                        }


                        followingcount++

                    }else{

                        userprofile_following_emptylottie.visibility  =View.VISIBLE
                    }
                    if (dc.type == DocumentChange.Type.REMOVED){

                        followingadaptar.clear()

                        retreivefollowing()

                    }


                }

                userprofile_following.text  = followingcount.toString()


            }


        })


    }



    private fun  usersuggestion(){

        val currentuseruid = FirebaseAuth.getInstance().uid.toString()
        val db = firestore.collection("UserInfo")
            .document(currentuseruid)
            .collection("UserSuggestion")
            .orderBy("timestamp",Query.Direction.DESCENDING)
            .limit(10)



        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(applicationContext,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){



                    if (dc.type == DocumentChange.Type.ADDED){


                        val users = dc.document.toObject(User::class.java)

                        confirmsuggestion(users)





                    }


                }


            }


        })

    }


    private fun confirmsuggestion(user:User){


        val currentuseruid = FirebaseAuth.getInstance().uid.toString()


        val dbcheck = firestore.collection("UserInfo")
            .document(currentuseruid)
            .collection("Following")


        dbcheck.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {



                var followinguser:User
                var count = 0


                if (error!=null){

                    Toast.makeText(this@UserProfileActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }


                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){


                         followinguser = dc.document.toObject(User::class.java)

                        followingusercheck.add(followinguser.uid.toString())

                    }

                }



                if (followingusercheck.contains(user.uid)){



                   Log.e("UserProfile","it contain")


                }else{
//                    suggestionadaptar.add(horisuggestionrecy(applicationContext,user))
//                    Log.e("UserProfile","it  doesnt contain")


                    count++


                    if (count == 0){
                        userprofile_suggestionlayout?.visibility =  View.GONE


                    }else{

                        userprofile_suggestionlayout?.visibility =  View.VISIBLE

                        suggestionadaptar.add(horisuggestionrecy(applicationContext, user))
                        Log.e("UserProfile", "it  doesnt contain")
                    }

                }


            }


        })




//        val currentuseruid = FirebaseAuth.getInstance().uid.toString()
//
//        val db = firestore.collection("UserInfo")
//            .document(currentuseruid)
//            .collection("Following")
//            .whereEqualTo("uid",user.uid)
//
//        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
//            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
//
//                if (error!=null){
//
//
//                    Toast.makeText(applicationContext,"Something went wrong",Toast.LENGTH_SHORT).show()
//                }
//
//                for (dc:DocumentChange in value?.documentChanges!!){
//
//                    if (dc.type == DocumentChange.Type.ADDED){
//
//
//                        val users = dc.document.toObject(User::class.java)
//
//
//                    }
//                }
//
//            }
//
//
//        })



    }

    private fun retreivequestions(uid:String) {


        val currentuseruid = FirebaseAuth.getInstance().uid.toString()


        val db = firestore.collection("UserInfo")
            .document(uid)
            .collection("Questions")
            .orderBy("date",Query.Direction.DESCENDING)
            .limit(3)



        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(this@UserProfileActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }


                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type ==DocumentChange.Type.ADDED){

                        userprofile_recentlyasked_emptylottie.visibility = View.GONE

                        val question = dc.document.toObject(Question::class.java)

                        questionsaskedadaptar.add(userprofile_askedquesrecy(question))


                    }else{

                         userprofile_recentlyasked_emptylottie.visibility = View.VISIBLE

                    }

                }


            }


        })

    }


    private fun retreivelikes(){

        val dblikes = firestore.collection("UserInfo").document(user?.uid.toString())
            .collection("Likes")

        dblikes.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                var likes = 0

                if (error!=null){

                    Toast.makeText(this@UserProfileActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type== DocumentChange.Type.ADDED){

                        likes++

                    }

                }


                count_animation_textView.setAnimationDuration(2000)
                    .countAnimation(0,likes)

//                userprofile_likes.text = likes.toString()

            }


        })


    }


  private fun retreviewquestions(){



      val  dbquestions  = firestore.collection("CommunityQuestions").whereEqualTo("useruid", user?.uid)

      dbquestions.addSnapshotListener(object : EventListener<QuerySnapshot>{
          override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

              var questionscount  = 0

              if (error!=null){

                  Toast.makeText(this@UserProfileActivity,"something went wrong",Toast.LENGTH_SHORT).show()

              }

              for (dc:DocumentChange  in value?.documentChanges!!){

                  if (dc.type == DocumentChange.Type.ADDED){
                      questionscount++


                  }

              }

//              userprofile_questions.text = questionscount.toString()


          }


      })

  }

    private fun retreiveanswer() {

        val curruntuseruid = FirebaseAuth.getInstance().uid.toString()
        val dbanswer = firestore.collection("UserInfo")
            .document(user?.uid.toString())
            .collection("Answers")


        dbanswer.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                var answerscount = 0
                if (error!=null){

                    Toast.makeText(this@UserProfileActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){

                        answerscount++

                        val answer = dc.document.toObject(Answer::class.java)
//                        adaptar.add(Userinforecy(answer))

                    }

                }

                userprofile_ans.text = answerscount.toString()


            }


        })



    }







    private fun retrivefollowers() {


        followeradaptar.clear()

      val db  = firestore.collection("UserInfo").document(user?.uid.toString())
          .collection("Followers")

        db.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                var followerscount = 0

                var count = 0

                if (error!=null){

                    Toast.makeText(this@UserProfileActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }
                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type ==  DocumentChange.Type.ADDED){


                        count++


                        userprofile_followers_emptylottie.visibility  =View.GONE

                        val user = dc.document.toObject(User::class.java)

                        if (count <= 7){


                            followeradaptar.add(Meprofile_followers_recy(applicationContext,user))

                        }



                        followerscount++

                    }else{

                        userprofile_followers_emptylottie.visibility  =View.VISIBLE

                    }
                    if (dc.type == DocumentChange.Type.REMOVED){

                        followeradaptar.clear()

                        retrivefollowers()

                    }


                }

                userprofile_followers.text  = followerscount.toString()


            }


        })


    }

    private fun followbtnstatus() {

        val curruntuseruid = FirebaseAuth.getInstance().uid.toString()
        val db = firestore.collection("UserInfo").document(curruntuseruid)
            .collection("Following").whereEqualTo("uid", user?.uid)

        db.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(this@UserProfileActivity,"Something went wrong",Toast.LENGTH_SHORT).show()
                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED) {


                        userprofile_followingbtn.visibility =View.VISIBLE
                        userprofile_followbtn.visibility = View.GONE


                    }else{

                        userprofile_followingbtn.visibility =View.GONE
                        userprofile_followbtn.visibility = View.VISIBLE


                    }

                }


            }


        })





    }

//    private fun requeststatus(){
//
//        val curruntuseruid = FirebaseAuth.getInstance().uid.toString()
//        val requestcheck = firestore.collection("UserInfo")
//            .document(curruntuseruid)
//            .collection("SendedRequest")
//            .whereEqualTo("receiveruid", user?.uid)
//
//        requestcheck.addSnapshotListener(object :EventListener<QuerySnapshot>{
//            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
//
//                if (error!=null){
//
//                    Toast.makeText(this@UserProfileActivity,"Something went wrong",Toast.LENGTH_SHORT).show()
//                }
//
//                for (dc:DocumentChange in value?.documentChanges!!){
//
//                    if (dc.type ==DocumentChange.Type.ADDED){
//
//                        userinfo_requestbtn.visibility = View.VISIBLE
//                        userinfo_followbtn.visibility  = View.GONE
//                        userinfo_unfollowbtn.visibility = View.GONE
//
//                    }else{
//
//                        userinfo_requestbtn.visibility = View.GONE
//                        userinfo_followbtn.visibility  = View.VISIBLE
//                        userinfo_unfollowbtn.visibility = View.GONE
//
//                    }
//
//                }
//
//
//            }
//
//
//        })
//
//    }


    private fun  retreivecurrentuser(){



        val curruntuseruid = FirebaseAuth.getInstance().uid.toString()
        val dbuser = firestore.collection("UserInfo").whereEqualTo("uid",curruntuseruid)
        dbuser.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(this@UserProfileActivity,"failed to load current user",Toast.LENGTH_SHORT).show()
                    userprofile_followbtn.visibility = View.GONE
                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        currentuser = dc.document.toObject(User::class.java)

                    }

                    if (dc.type == DocumentChange.Type.MODIFIED){

                        currentuser = dc.document.toObject(User::class.java)

                    }

                }
            }


        })


    }


//    private fun sendrequest(){
//
//        val currentuseruid = FirebaseAuth.getInstance().uid.toString()
//
//        val request  =  Request(currentuseruid, user?.uid,System.currentTimeMillis().toString())
//
//        val dbrequest = firestore.collection("UserInfo")
//            .document(user?.uid.toString())
//            .collection("Requests")
//            .document(currentuseruid)
//
//        dbrequest.set(request).addOnSuccessListener {
//
//            val senddrequest = Request(currentuseruid, user?.uid,System.currentTimeMillis().toString())
//
//            val sendrequest = firestore.collection("UserInfo")
//                .document(currentuseruid)
//                .collection("SendedRequest")
//                .document(user?.uid.toString())
//
//            sendrequest.set(senddrequest).addOnSuccessListener {
//
//                Toast.makeText(this,"Request Sended",Toast.LENGTH_SHORT).show()
//
//                requeststatus()
//
//            }
//
//
//        }
//
//
//    }


    private fun adduser() {

        val currentuseruidd = FirebaseAuth.getInstance().uid.toString()

        val userfollower =  firestore.collection("UserInfo")
            .document(user?.uid.toString())
            .collection("Followers").document(currentuseruidd)


        val  userfollowing  = firestore.collection("UserInfo")
            .document(currentuseruidd).collection("Following").document(user?.uid.toString())

            userfollower.set(currentuser!!).addOnSuccessListener {

                userfollowing.set(user!!).addOnSuccessListener {


                   


//                    formatcollection()

//                suggestionalgorithem()

                    suggestionalgorithem()

                    retrivefollowers()
                    retreivefollowing()


                }
//
            }







    }


    private fun formatcollection(){

        val currentuseruidd = FirebaseAuth.getInstance().uid.toString()


        val db = firestore.collection("UserInfo")
            .document(currentuseruidd)
            .collection("UserSuggestion")


        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {



                if (error!=null){


                    Toast.makeText(applicationContext,"Something went wrong",Toast.LENGTH_SHORT).show()
                }



                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){


                        val user = dc.document.toObject(User::class.java)

                        val dbdelete = firestore.collection("UserInfo")
                            .document(currentuseruidd)
                            .collection("UserSuggestion")
                            .document(user.uid.toString())


                        dbdelete.delete()



                    }

                }

            }


        })


    }





    private fun suggestionalgorithem() {

        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

        val db = firestore.collection("UserInfo")
            .document(currentuseruid)
            .collection("Following")


        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(applicationContext,"Something went wrong",Toast.LENGTH_SHORT).show()

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

                        Toast.makeText(applicationContext,"Something went wrong",Toast.LENGTH_SHORT).show()
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


//    private fun unsendrequest(){
//
//        val currentuseruid = FirebaseAuth.getInstance().uid.toString()
//
//        val dbrequest = firestore.collection("UserInfo")
//            .document(user?.uid.toString())
//            .collection("Requests")
//            .document(currentuseruid)
//
//        dbrequest.delete().addOnSuccessListener {
//
//            val usendrequest = firestore.collection("UserInfo")
//                .document(currentuseruid)
//                .collection("SendedRequest")
//                .document(user?.uid.toString())
//
//            usendrequest.delete().addOnSuccessListener {
//                requeststatus()
//            }
//
//        }
//
//
//
//
//
//    }






    private fun unfollowuser(){
        val currentuseruidd = FirebaseAuth.getInstance().uid.toString()


        val dbfo = firestore.collection("UserInfo").document(user?.uid.toString())
            .collection("Followers").document(currentuseruidd)

        dbfo.delete()

        val dbfi = firestore.collection("UserInfo").document(currentuseruidd)
            .collection("Following").document(user?.uid.toString())

        dbfi.delete()


        suggestionalgorithem()





        retrivefollowers()
        retreivefollowing()
    }


}