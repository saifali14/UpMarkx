package com.youngminds.upmarkx.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.*
import com.youngminds.upmarkx.Models.Answer
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.R
import com.youngminds.upmarkx.recyclerview.Meprofile_followers_recy
import com.youngminds.upmarkx.recyclerview.Newsrecy
import com.youngminds.upmarkx.recyclerview.horisuggestionrecy
import com.youngminds.upmarkx.recyclerview.shareprofilerecy
import kotlinx.android.synthetic.main.activity_chapters.*
import kotlinx.android.synthetic.main.fragment_me_profile.*
import kotlinx.android.synthetic.main.fragment_me_profile.view.*
import kotlinx.android.synthetic.main.meprofile_menulayout.view.*
import kotlinx.android.synthetic.main.profileshare_layout.view.*


class MeProfileFragment : Fragment() {



    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth


    companion object{
        val  adaptar  =  GroupAdapter<ViewHolder>()
        val followingadaptar = GroupAdapter<ViewHolder>()
        val USERK = "USERK"
        var count = 0
        val suggestionuser:ArrayList<User> =ArrayList()
        val shareprofileadaptar = GroupAdapter<ViewHolder>()
        var user:User?=null
        val showsuggesetion = GroupAdapter<ViewHolder>()
        var followingusercheck = mutableListOf<String>()


        var thankspie = 0
        var answerspie= 0
        var questionspie = 0







    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_me_profile, container, false)


        firestore =  FirebaseFirestore.getInstance()

        auth = FirebaseAuth.getInstance()

        count++


        adaptar.clear()
        followingadaptar.clear()


        showsuggesetion.setOnItemClickListener { item, view ->



            val user = item as horisuggestionrecy

            val intent = Intent(requireContext(),UserProfileActivity::class.java)
            intent.putExtra(SearchActivity.USERK,user.user)
            startActivity(intent)

        }




//
//
//        if (count == 1){
//
//
//
//
//        }

        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

        val db = firestore.collection("UserInfo")
            .whereEqualTo("uid",currentuseruid)
        db.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(activity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }


                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){


                        user = dc.document.toObject(User::class.java)


////
//                        Glide.with(activity?.applicationContext!!).load(user.imageuri).into(view!!.meprofile_blurebackground)
//                        view?.meprofile_blurebackground?.setBlur(3)
                        Glide.with(requireContext()).load(user?.imageuri).into(view!!.meprofile_imageview)

                        view.meprofile_bioview.setText(user?.bio)
                        view.meprofile_emailview?.text = user?.email
                        view.meprofile_nameview?.text = "${user?.firstname} ${user?.lastname}"


                        when (user?.standard){

                            "Class10"  -> {     view.meprofile_classview?.text = "10" }
                            "Class9"  -> {  view.meprofile_classview?.text = "9" }
                            "Class8"  -> {  view.meprofile_classview?.text = "8" }
                            "Class7"  -> {  view.meprofile_classview?.text = "7" }
                            "Class6"  -> {  view.meprofile_classview?.text = "6" }


                        }


                        if (user?.verified == "true"){

                            view.meprofile_verfied.visibility =View.VISIBLE

                        }else{
                            view.meprofile_verfied.visibility =View.GONE
                        }

                    }

                }

            }


        })




        view.meprofile_notificationsbtn.setOnClickListener {

            val intent = Intent(requireContext(),NotificationActivity::class.java)
            startActivity(intent)

        }

        view.meprofile_editbtn.setOnClickListener {

            val intent = Intent(requireContext(),EditProfileActivity::class.java)
            startActivity(intent)


        }
        view.meprofile_edit.setOnClickListener {
            val intent = Intent(requireContext(),EditProfileActivity::class.java)
            startActivity(intent)
        }






        view.meprofile_followers_recyclerview.adapter = adaptar
        view.meprofile_following_recyclerview.adapter = followingadaptar

        view.meprofile_suggestionrecy.adapter = showsuggesetion




        view.meprofile_messagebtn.setOnClickListener {


            val intent = Intent(requireContext(),LatestMessageseActivity::class.java)
            startActivity(intent)


        }

        view.meprofile_followerscard.setOnClickListener {


            val intent = Intent(requireContext(),folllowersActivity::class.java)
            intent.putExtra(UserProfileActivity.USER_UID,currentuseruid)
            startActivity(intent)

        }

        view.meprofile_followingcard.setOnClickListener {

            val intent = Intent(requireContext(),FollowingActivity::class.java)
            intent.putExtra(UserProfileActivity.USER_UID,currentuseruid)
            startActivity(intent)

        }


        view.meprofile_followersshow.setOnClickListener {

            val intent = Intent(requireContext(),folllowersActivity::class.java)
            intent.putExtra(UserProfileActivity.USER_UID,currentuseruid)
            startActivity(intent)


        }
        view.meprofile_followingshow.setOnClickListener {

            val intent = Intent(requireContext(),FollowingActivity::class.java)
            intent.putExtra(UserProfileActivity.USER_UID,currentuseruid)
            startActivity(intent)


        }




        adaptar.setOnItemClickListener { item, _ ->


            val user  = item as Meprofile_followers_recy
            val intent = Intent(activity, UserProfileActivity::class.java)
            intent.putExtra(SearchActivity.USERK,user.user)
            startActivity(intent)
        }
        followingadaptar.setOnItemClickListener { item, _ ->


            val user  = item as Meprofile_followers_recy
            val intent = Intent(activity, UserProfileActivity::class.java)
            intent.putExtra(SearchActivity.USERK,user.user)
            startActivity(intent)
        }




        view.meprofile_menu.setOnClickListener {


            val dialog = BottomSheetDialog(requireContext())
            val view1 = layoutInflater.inflate(R.layout.meprofile_menulayout,null)

            view1.meprofile_menu_savedans.setOnClickListener {


                val intent = Intent(requireContext(),SavedAnswersActivity::class.java)
                startActivity(intent)

            }



            view1.meprofile_menu_logout.setOnClickListener {


                auth.signOut()
                val intent = Intent(requireContext(),LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()

            }



            view1.meprofile_menu_about.setOnClickListener {

                val intent = Intent(requireContext(),AboutActivity::class.java)
                startActivity(intent)

            }

            view1.meprofile_menu_discoverpeople.setOnClickListener {

                val intent = Intent(requireContext(),DiscoverpeopleActivity::class.java)
                startActivity(intent)

            }


            view1.meprofile_menu_askedquestions.setOnClickListener {


                val intent = Intent(requireContext(),UserAllQuestionsActivity::class.java)
                intent.putExtra(SearchActivity.USERK, user)
                startActivity(intent)

            }
            view1.meprofile_menu_termscondi.setOnClickListener {

                val intent = Intent(requireContext(),WebviewActivity::class.java)
                intent.putExtra(Newsrecy.WEB_KEY,"https://pages.flycricket.io/upmarks-0/terms.html")
                startActivity(intent)

            }


            view1.meprofile_menu_privacypoli.setOnClickListener {


                val intent = Intent(requireContext(),WebviewActivity::class.java)
                intent.putExtra(Newsrecy.WEB_KEY,"https://pages.flycricket.io/upmarks-0/privacy.html")
                startActivity(intent)

            }


            view1.meprofile_menu_adminpanel.setOnClickListener {

                val intent = Intent(requireContext(),AdminLoginActivity::class.java)
                startActivity(intent)

            }





            dialog.setContentView(view1)
            dialog.show()



        }

        view.meprofile_sharebtn.setOnClickListener {


            shareprofileadaptar.clear()




            val dialog = BottomSheetDialog(requireContext())
            val view1 = layoutInflater.inflate(R.layout.profileshare_layout,null)

            view1.shareprofile_recyclerview.adapter = shareprofileadaptar


            val dbshare = firestore.collection("UserInfo")
                .document(currentuseruid)
                .collection("Following")



            dbshare.addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    if(error!=null){

                        Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()

                    }


                    for (dc:DocumentChange in value?.documentChanges!!){


                        if (dc.type == DocumentChange.Type.ADDED){


                            val users = dc.document.toObject(User::class.java)

                            shareprofileadaptar.add(shareprofilerecy(requireContext(),users,
                                user!!))

                        }


                    }



                }


            })



            dialog.setContentView(view1)
            dialog.show()



        }






//        addsuggestion()







        return view
    }

//    private fun retreivieuser() {
//
//
//        val currentuseruid = FirebaseAuth.getInstance().uid.toString()
//
//        val db = firestore.collection("UserInfo")
//            .whereEqualTo("uid",currentuseruid)
//        db.addSnapshotListener(object : EventListener<QuerySnapshot>{
//            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
//
//                if (error!=null){
//
//                    Toast.makeText(activity,"Something went wrong",Toast.LENGTH_SHORT).show()
//
//                }
//
//
//                for (dc:DocumentChange in value?.documentChanges!!){
//
//                    if (dc.type == DocumentChange.Type.ADDED){
//
//
//                       user = dc.document.toObject(User::class.java)
//
//
//////
////                        Glide.with(activity?.applicationContext!!).load(user.imageuri).into(view!!.meprofile_blurebackground)
////                        view?.meprofile_blurebackground?.setBlur(3)
//                        Glide.with(activity?.applicationContext!!).load(user?.imageuri).into(view!!.meprofile_imageview)
//
//                        view?.meprofile_emailview?.text = user.email
//                        view?.meprofile_nameview?.text = "${user.firstname} ${user.lastname}"
//
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





    private fun questionasked(){


        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

        val db = firestore.collection("UserInfo")
            .document(currentuseruid)
            .collection("Questions")

        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                }


                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){


                        questionspie++


                    }


                }

                retreivelikes()

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

                    Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()

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

                    Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()

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
                        meprofile_suggestionlayout?.visibility =  View.GONE


                    }else{

                        meprofile_suggestionlayout?.visibility =  View.VISIBLE

                        showsuggesetion.add(horisuggestionrecy(requireContext(), user))
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



    private fun retreivefollowers() {


        adaptar.clear()

        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

        val db = firestore.collection("UserInfo")
            .document(currentuseruid)
            .collection("Followers")


        db.addSnapshotListener(object : EventListener<QuerySnapshot>{


            var followerscount = 0




            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){


                    Toast.makeText(activity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){



                    if (dc.type == DocumentChange.Type.ADDED){

                        followerscount++

                        if (followerscount <= 10){


                            val user = dc.document.toObject(User::class.java)
                            adaptar.add(Meprofile_followers_recy(activity?.applicationContext!!,user))

                        }


                    }

                }



                view?.meprofile_followers?.text = followerscount.toString()

            }


        })







    }


    private fun addsuggestion() {

        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

        val followerdb = firestore.collection("UserInfo")
            .document(currentuseruid)
            .collection("Following")

        followerdb.addSnapshotListener(object :EventListener<QuerySnapshot>{



            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){


                    Toast.makeText(activity?.applicationContext!!,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        val user = dc.document.toObject(User::class.java)



//                        followingsuggestion(user)



                    }

                }


            }


        })




        Log.e("MeProfile",suggestionuser.toString())




    }



    private fun followingsuggestion(users: User){



        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

        val db = firestore.collection("UserInfo")
            .document(users.uid.toString())
            .collection("Following")

        db.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(activity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }


                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){


                        val user = dc.document.toObject(User::class.java)

                        suggestionuser.add(user)


                        val checkdb = firestore.collection("UserInfo")
                            .document(currentuseruid)
                            .collection("SuggestionUser")
                            .whereNotEqualTo("uid",user.uid)

                        checkdb.addSnapshotListener(object :EventListener<QuerySnapshot>{
                            override fun onEvent(
                                value: QuerySnapshot?,
                                error: FirebaseFirestoreException?
                            ) {


                                if (error!=null){

                                    Toast.makeText(activity?.applicationContext,"Something went wrong",Toast.LENGTH_SHORT).show()

                                }

                                for (dci:DocumentChange in value?.documentChanges!!){


                                    if (dci.type ==  DocumentChange.Type.ADDED){

                                        val dbuser = firestore.collection("UserInfo")
                                            .document(currentuseruid)
                                            .collection("SuggestionUser")
                                        dbuser.add(user)

                                    }

                                }

                            }


                        })




                    }

                }



//                for (useri in suggestionuser){
//
//                    val dbuser = firestore.collection("UserInfo")
//                        .document(currentuseruid)
//                        .collection("SuggestionUser")
//                    dbuser.add(useri)
//
//                    Log.e("Meprofile", useri.toString())
//                }

            }


        })




    }



    private fun retreivefollowing() {



        followingadaptar.clear()

        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

        val db = firestore.collection("UserInfo")
            .document(currentuseruid)
            .collection("Following")


        db.addSnapshotListener(object : EventListener<QuerySnapshot>{



            var followingcount = 0


            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){


                    Toast.makeText(activity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){



                    if (dc.type == DocumentChange.Type.ADDED){

                        followingcount++



                        if (followingcount <= 10){
                            val user = dc.document.toObject(User::class.java)
                            followingadaptar.add(Meprofile_followers_recy(activity?.applicationContext!!,user))

                        }


                    }

                }
                view?.meprofile_following?.text = followingcount.toString()




            }


        })

    }


    private fun retreiveanswer(){


        val curruntuseruid = FirebaseAuth.getInstance().uid.toString()
        val dbanswer = firestore.collection("UserInfo")
            .document(curruntuseruid)
            .collection("Answers")

        dbanswer.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                var answerscount = 0
                if (error!=null){

                    Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){

                        answerscount++


                        answerspie++



                        val answer = dc.document.toObject(Answer::class.java)

                        view?.meprofile_ans?.text = answerscount.toString()


//                        adaptar.add(Userinforecy(answer))

                    }

                }

                setupchrt()




            }


        })

    }


    private fun retreivelikes(){



        val curruntuseruid = FirebaseAuth.getInstance().uid.toString()
        val dbanswer = firestore.collection("UserInfo")
            .document(curruntuseruid)
            .collection("Likes")

        dbanswer.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                var likescount = 0
                if (error!=null){

                    Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){

                        likescount++

                        thankspie++

//                        meprofile_ans.text = likescount.toString()


//                        adaptar.add(Userinforecy(answer))

                    }

                }

                retreiveanswer()







            }


        })
    }


    private fun setupchrt(){








        view?.piechat?.isDrawHoleEnabled = true
        view?.piechat?.isUsePercentValuesEnabled
        view?.piechat?.setEntryLabelTextSize(13F)
        view?.piechat?.setEntryLabelColor(Color.WHITE)
        view?.piechat?.centerText = "Status"
        view?.piechat?.setCenterTextSize(24F)
        view?.piechat?.description?.isEnabled = true
        view?.piechat?.description?.text = "upMarks"
        view?.piechat?.setUsePercentValues(true)







        val total = answerspie + questionspie + thankspie


        if (answerspie == 0 || questionspie == 0 || thankspie == 0){


            Log.e("Meprofile", answerspie.toString())
            Log.e("Meprofile", questionspie.toString())
            Log.e("Meprofile", thankspie.toString())

            view?.meprofile_piecard?.visibility = View.GONE

        }else {

            var answerspercent = (answerspie.toDouble() / total) * 100
            var quesionspercent = (questionspie.toDouble() / total) * 100
            var thankspercent = (thankspie.toDouble() / total) * 100

            Log.e("Meprofile", "answers -> "+ answerspercent.toString())
            Log.e("Meprofile","questions ->"+ quesionspercent.toString())
            Log.e("Meprofile","thanks ->"+ thankspercent.toString())
            Log.e("Meprofile",total.toString())


            val entries = ArrayList<PieEntry>()

            entries.add(PieEntry(answerspercent.toFloat(), "Answers Given"))
            entries.add(PieEntry(quesionspercent.toFloat(), "Questions Asked"))
            entries.add(PieEntry(thankspercent.toFloat(), "Thanks"))


            val colors = ArrayList<Int>()

            for (color in ColorTemplate.MATERIAL_COLORS) {

                colors.add(color)

            }


            for (color in ColorTemplate.VORDIPLOM_COLORS) {

                colors.add(color)

            }


            val dataset = PieDataSet(entries, "Overview")
            dataset.setColors(colors)


            val data = PieData(dataset)
            data.setDrawValues(true)
            data.setValueTextColor(Color.WHITE)
            data.setValueFormatter(PercentFormatter(piechat))
            data.setValueTextSize(12f)
            data.setValueTextColor(Color.WHITE)


            view?.piechat?.data = data
            view?.piechat?.invalidate()

            view?.piechat?.animateY(1400, Easing.EaseInOutQuad)


        }


    }







    override fun onStart() {
        super.onStart()


        adaptar.clear()
        followingadaptar.clear()
        showsuggesetion.clear()


        retreivefollowers()
        retreivefollowing()

        questionasked()
        usersuggestion()

    }


}