package com.youngminds.upmarkx.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.*
import com.youngminds.upmarkx.Models.*
import com.youngminds.upmarkx.R
import com.youngminds.upmarkx.recyclerview.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {


    private lateinit var firestore: FirebaseFirestore


    val questionadaptar = GroupAdapter<ViewHolder>()
    val newsadaptar = GroupAdapter<ViewHolder>()
    val leaderboardadaptar = GroupAdapter<ViewHolder>()
    val suggestionadaptar = GroupAdapter<ViewHolder>()


    var usersuggest = mutableListOf<String>()
    var followingusercheck = mutableListOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        firestore = FirebaseFirestore.getInstance()

        retreivieuser()



        newsadaptar.clear()
        questionadaptar.clear()
        leaderboardadaptar.clear()
        suggestionadaptar.clear()

//

        retreivequestions()
        retreiveleaderboarduser()
        usersuggestion()
        retreivenews()

        checknotification()


        suggestionadaptar.setOnItemClickListener { item, _ -> }


        newsadaptar.setOnItemClickListener { item, _ ->

            val news = item as home_Newsrecy

            val intent = Intent(requireContext(), WebviewActivity::class.java)
            intent.putExtra(Newsrecy.WEB_KEY, news.news.newslink)
            startActivity(intent)


        }


        suggestionadaptar.setOnItemClickListener { item, _ ->


            val user = item as horisuggestionrecy

            val intent = Intent(requireContext(), UserProfileActivity::class.java)
            intent.putExtra(SearchActivity.USERK, user.user)
            startActivity(intent)


        }


        questionadaptar.setOnItemClickListener { item, _ ->


            val question = item as home_questionrecy

            val intent = Intent(requireContext(), MoreanswersActivity::class.java)
            intent.putExtra(Communitychildrecy.MORE_QUESTION, question.question.questionid)
            startActivity(intent)


        }


        view.home_notificationbtn.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)

        }


        view.home_leaderboardshow.setOnClickListener {


            val intent = Intent(requireContext(), LeaderboardActivity::class.java)
            startActivity(intent)


        }



        view.home_submath.setOnClickListener {


            val intent = Intent(requireContext(),ChaptersActivity::class.java)
            intent.putExtra(CategoryActivity.SUBJECT_KEY,"Maths")
            startActivity(intent)


        }

        view.home_subchem.setOnClickListener {

            val intent = Intent(requireContext(),ChaptersActivity::class.java)
            intent.putExtra(CategoryActivity.SUBJECT_KEY,"Chemistry")
            startActivity(intent)

        }


        view.home_subphysics.setOnClickListener {

            val intent = Intent(requireContext(),ChaptersActivity::class.java)
            intent.putExtra(CategoryActivity.SUBJECT_KEY,"Physics")
            startActivity(intent)
        }



        view.home_subeng.setOnClickListener {


            val intent = Intent(requireContext(),ChaptersActivity::class.java)
            intent.putExtra(CategoryActivity.SUBJECT_KEY,"English")
            startActivity(intent)
        }


        view.home_newsshowmore.setOnClickListener {

            val intent = Intent(requireContext(), NewsActivity::class.java)
            startActivity(intent)

        }

        view.home_messagebtn.setOnClickListener {

            val intent = Intent(requireContext(), LatestMessageseActivity::class.java)
            startActivity(intent)

        }

        view.home_showallsub.setOnClickListener {

            val intent = Intent(requireContext(), CategoryActivity::class.java)
            startActivity(intent)

        }


        view.home_askedquestionrecyclerview.adapter = questionadaptar
        view.home_newsrecyclerview.adapter = newsadaptar
        view.home_leaderboard_recyclerview.adapter = leaderboardadaptar
        view.home_suggestion_recyclerview.adapter = suggestionadaptar


//        view.allsubjectscard.setOnClickListener{
//
////
////            val searchFragment = SearchFragment()
////
////            val bundle = Bundle()
////            bundle.putString("key","Hello World")
////            searchFragment.arguments = bundle
////
////            val transaction = requireFragmentManager().beginTransaction()
////            transaction.replace(R.id.main_framelayout,searchFragment)
////                .addToBackStack(null)
////            transaction.commit()
//
//
//
//            val intent = Intent(activity?.applicationContext,CategoryActivity::class.java)
//            startActivity(intent)
//
//
//
//
//        }


//
//        view.newscard.setOnClickListener {
//
//
////            val newsFragment = NewsFragment()
////
////            val transaction = requireFragmentManager().beginTransaction()
////            transaction.replace(R.id.main_framelayout,newsFragment)
////                .addToBackStack(null)
////            transaction.commit()
//
//
//            val intent = Intent(activity?.applicationContext,NewsActivity::class.java)
//            startActivity(intent)
//
//
//        }


        return view
    }




    private fun checkmessages(){


        val currentuseruid  = FirebaseAuth.getInstance().uid.toString()

        val db  = firestore.collection("LatestMessages")
            .document(currentuseruid)
            .collection("messsages")
            .whereEqualTo("outid",currentuseruid)
            .whereEqualTo("seen","false")


        db.addSnapshotListener { value , error ->


            if (error!=null){

                Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
            }


            for (dc:DocumentChange in value?.documentChanges!!){


                if (dc.type == DocumentChange.Type.ADDED){


                    val message = dc.document.toObject(DeliverChat::class.java)


                    if (message.inid != currentuseruid){

                        view?.home_messageball?.visibility =View.VISIBLE
                    }

                }else{
                    view?.home_messageball?.visibility =View.GONE

                }

            }



        }


    }




    private fun checknotification(){


        val currentuseruid  = FirebaseAuth.getInstance().uid.toString()

        val db = firestore.collection("UserInfo")
            .document(currentuseruid)
            .collection("Notifications")
            .whereEqualTo("seen",false)


        db.addSnapshotListener { value, error ->


          if (error!=null){

              Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
          }

            for (dc:DocumentChange in value?.documentChanges!!){

                if (dc.type == DocumentChange.Type.ADDED){


                    view?.home_notificationball?.visibility = View.VISIBLE

                }else{
                    view?.home_notificationball?.visibility = View.GONE

                }
            }

        }




    }







    private fun retreiveleaderboarduser() {


        val db = firestore.collection("Leaderboard")
            .orderBy("likes", Query.Direction.DESCENDING)
            .limit(3)


        db.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                var rank = 0

                if (error != null) {
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }



                for (dc: DocumentChange in value?.documentChanges!!) {


                    if (dc.type == DocumentChange.Type.ADDED) {


                        rank++


                        val leaderboardcard = dc.document.toObject(LeaderboardUser::class.java)


                        leaderboardadaptar.add(
                            home_leaderboard_recy(
                                requireContext(),
                                leaderboardcard,
                                rank.toString()
                            )
                        )

                    }

                }

                view?.home_leaderboard_shrimmer?.stopShimmer()
                view?.home_leaderboard_shrimmer?.visibility = View.GONE


            }


        })


    }

    private fun retreivequestions() {


        val db = firestore.collection("CommunityQuestions")
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(7)

        db.addSnapshotListener { value, error ->
            if (error != null) {

                Toast.makeText(
                    activity?.applicationContext,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()

            }


            for (dc: DocumentChange in value?.documentChanges!!) {

                if (dc.type == DocumentChange.Type.ADDED) {


                    val question = dc.document.toObject(Question::class.java)

                    questionadaptar.add(home_questionrecy(requireContext(), question))

                }


            }

            view?.home_askedquestions_shrimmer?.stopShimmer()
            view?.home_askedquestions_shrimmer?.visibility = View.GONE
        }


    }


    private fun retreivenews() {


        val db = firestore.collection("News")
            .orderBy("time",Query.Direction.DESCENDING)


        db.addSnapshotListener { value, error ->


            if (error!=null){


                Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
            }



            for (dc: DocumentChange in value?.documentChanges!!) {


                if (dc.type == DocumentChange.Type.ADDED) {


                    val news = dc.document.toObject(News::class.java)

                    newsadaptar.add(home_Newsrecy(news, requireContext()))


                }


            }

            view?.home_news_shrimmer?.stopShimmer()
            view?.home_news_shrimmer?.visibility = View.GONE
        }


    }


    private fun retreivieuser() {

        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

        val db = firestore.collection("UserInfo")
            .whereEqualTo("uid", currentuseruid)

        db.addSnapshotListener { value, error ->
            if (error != null) {

                Toast.makeText(
                    activity?.applicationContext,
                    "something went wrong",
                    Toast.LENGTH_SHORT
                ).show()

            }

            for (dc: DocumentChange in value?.documentChanges!!) {

                if (dc.type == DocumentChange.Type.ADDED) {


                    val user = dc.document.toObject(User::class.java)

                    //                        view?.home_username?.text = user.firstname
                    //                        activity?.let { Glide.with(it).load(user.imageuri).into(view!!.home_imageview) }

                    view?.home_username?.text = user.firstname + " " + user.lastname
                    Glide.with(requireActivity().applicationContext).load(user.imageuri)
                        .into(requireView().home_userimage)


                }

            }
        }


    }


    private fun usersuggestion() {

        val currentuseruid = FirebaseAuth.getInstance().uid.toString()
        val db = firestore.collection("UserInfo")
            .document(currentuseruid)
            .collection("UserSuggestion")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(10)



        db.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error != null) {

                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()

                }

                for (dc: DocumentChange in value?.documentChanges!!) {


                    if (dc.type == DocumentChange.Type.ADDED) {


                        val users = dc.document.toObject(User::class.java)

                        confirmsuggestion(users)


                    }


                }


            }


        })

    }


    private fun confirmsuggestion(user: User) {


        val currentuseruid = FirebaseAuth.getInstance().uid.toString()


        val dbcheck = firestore.collection("UserInfo")
            .document(currentuseruid)
            .collection("Following")


        dbcheck.addSnapshotListener { value, error ->
            var followinguser: User

            var count = 0


            if (error != null) {

                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                    .show()

            }


            for (dc: DocumentChange in value?.documentChanges!!) {


                if (dc.type == DocumentChange.Type.ADDED) {


                    followinguser = dc.document.toObject(User::class.java)

                    followingusercheck.add(followinguser.uid.toString())

                }

            }



            if (followingusercheck.contains(user.uid)) {


                Log.e("UserProfile", "it contain")


            } else {

                count++


                if (count >= 0) {

                    view?.home_suggestion?.visibility = View.VISIBLE

                    suggestionadaptar.add(horisuggestionrecy(requireContext(), user))
                    Log.e("UserProfile", "it  doesnt contain")
                } else {

                    view?.home_suggestion?.visibility = View.GONE
                }


            }
        }

    }






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





//    private fun replacefragment(fragment: Fragment){
//
//
//        if (fragment !=null){
//
//
//            val transaction:FragmentTransaction = requireFragmentManager().beginTransaction()
//            transaction.replace(R.id.homefragment_main,fragment)
//            transaction.commit()
//
//        }
//
//
//
//    }







}