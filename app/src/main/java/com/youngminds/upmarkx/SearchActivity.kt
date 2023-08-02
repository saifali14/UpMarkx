package com.youngminds.upmarkx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.recyclerview.Searchrecyle
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.activity_latest_messagese.*
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    val searchadaptar = GroupAdapter<ViewHolder>()

    companion object{

        var user: User?=null
        val USERK = "USERK"
        var text:String?=null
        var subject:String?=null


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)




        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

//        text = intent.getStringExtra(MainActivity.TEXT).toString()

//        search_searchview.setText(text)







        firestore  = FirebaseFirestore.getInstance()
        search_recy.adapter =searchadaptar








        searchadaptar.setOnItemClickListener { item, view ->

            val user  = item as Searchrecyle
            val intent = Intent(this,UserProfileActivity::class.java)
            intent.putExtra(USERK,user.user)
            startActivity(intent)

            val dbrecentsearch = firestore.collection("UserInfo")
                .document(currentuseruid)
                .collection("RecentlySearchUsers")
                .document(user.user.uid.toString())

            dbrecentsearch.set(user.user)

        }


//        recentlysearchusers()
//
//
//        if (search_searchview.text.isEmpty()){
//
//            recentlysearchusers()
//        }









//        var physicsclick = 0
//        search_physics.setOnClickListener {
//            physicsclick ++
//            if (physicsclick  %  2==0){
//
//               subject = "All"
//            }else{
//                subject = "Physics"
//
//            }
//        }
//
//        var chemclick = 0
//        search_chemistry.setOnClickListener {
//
//            chemclick ++
//
//            if (chemclick  %  2==0){
//                subject = "All"
//            }else{
//                subject = "Chemistry"
//
//            }
//
//
//        }

//        var bioclick =0
//        chip_biology.setOnClickListener {
//
//
//            bioclick ++
//
//            if (bioclick  %  2==0){
//                subject = "All"
//            }else{
//                subject = "Biology"
//
//            }
//
//        }
//
//        var histclick =0
//        search_history.setOnClickListener {
//
//            histclick ++
//
//            if (histclick  %  2==0){
//                subject = "All"
//            }else{
//                subject = "History"
//
//            }
//        }
//        var geoclick =0
//        chip_geography.setOnClickListener {
//
//            geoclick ++
//
//            if (geoclick  %  2==0){
//                subject = "All"
//            }else{
//                subject = "Geography"
//
//            }
//        }
//        var ecoclick = 0
//        chip_economics.setOnClickListener {
//
//            ecoclick ++
//
//            if (ecoclick  %  2==0){
//                subject = "All"
//            }else{
//                subject = "Economics"
//
//            }
//        }

//        var polclick =0
//        chip_politicalscience.setOnClickListener {
//
//
//            polclick ++
//
//            if (polclick  %  2==0){
//
//                subject = "All"
//
//            }else{
//                subject = "Political Science"
//
//            }
//        }

//        var hindiclick =0
//        chip_hindi.setOnClickListener {
//
//            hindiclick ++
//
//            if (hindiclick  %  2==0){
//
//                subject = "All"
//
//            }else{
//
//                subject = "Hindi"
//            }
//        }

//        var engclick = 0
//        search_english.setOnClickListener {
//
//            engclick ++
//
//            if (engclick  %  2==0){
//
//                search_searchview.editableText.clear()
//
//                subject = "All"
//
//            }else{
//                subject = "English"
//
//                search_searchview.editableText.clear()
//
//            }
//        }

//        var mathclick =0
//        chip_maths.setOnClickListener {
//
//            mathclick ++
//
//            if (mathclick %  2==0){
//                subject = "All"
//            }else{
//                subject = "Maths"
//
//            }
//        }




        retreiveuser()



        search_searchview.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                searchadaptar.clear()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                searchadaptar.clear()



//                val text = s.toString()

                val seaerchuser = firestore.collection("UserInfo").whereArrayContains("keywords",
                    s?.trim().toString().toLowerCase()).limit(100)
                seaerchuser.addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {


                        if (error!=null){

                            Toast.makeText(this@SearchActivity,"Network issue is there",
                                Toast.LENGTH_SHORT).show()


                        }

                        for (dc: DocumentChange in value?.documentChanges!!){


                            if (dc.type == DocumentChange.Type.ADDED){

                                val user = dc.document.toObject(User::class.java)

                                searchadaptar.add(Searchrecyle(this@SearchActivity,user,false))

                            }

                        }


                    }


                })

            }

            override fun afterTextChanged(s: Editable?) {

            }


        })

    }



    private fun recentlysearchusers(){


        searchadaptar.clear()

        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

        val dbrecentusers = firestore.collection("UserInfo")
            .document(currentuseruid)
            .collection("RecentlySearchUsers")

        dbrecentusers.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(this@SearchActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }


                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){


                        val user = dc.document.toObject(User::class.java)


                        searchadaptar.add(Searchrecyle(this@SearchActivity,user,true))


                    }
                    if (dc.type == DocumentChange.Type.REMOVED){

                        recentlysearchusers()

                    }

                }

            }


        })



    }






//    private fun partsuer(text: String){
//
//
//        searchadaptar.clear()
//        val seaerchusersub = firestore.collection("UserInfo").whereArrayContains("keywords",text.trim().toString())
//            .whereEqualTo("bestsub", subject)
//        seaerchusersub.addSnapshotListener(object : EventListener<QuerySnapshot> {
//            override fun onEvent(
//                value: QuerySnapshot?,
//                error: FirebaseFirestoreException?
//            ) {
//
//
//                if (error!=null){
//
//                    Toast.makeText(this@SearchActivity,"Network issue is there",
//                        Toast.LENGTH_SHORT).show()
//
//
//                }
//
//                for (dc: DocumentChange in value?.documentChanges!!){
//
//
//                    if (dc.type == DocumentChange.Type.ADDED){
//
//                        val user = dc.document.toObject(User::class.java)
//
//                        searchadaptar.add(Searchrecyle(this@SearchActivity,user))
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
//
//
//    }

    private fun retreiveuser() {

        val currentuser = FirebaseAuth.getInstance().uid.toString()
       val dbuser = firestore.collection("UserInfo").whereEqualTo("uid",currentuser)
           dbuser.addSnapshotListener(object : EventListener<QuerySnapshot>{
               override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                   if (error!=null){
                       Toast.makeText(this@SearchActivity,"Network error",Toast.LENGTH_SHORT).show()

                   }

                   for(dc:DocumentChange in value?.documentChanges!!){

                       if (dc.type == DocumentChange.Type.ADDED){

                            user = dc.document.toObject(User::class.java)


                               Glide.with(this@SearchActivity).load(user?.imageuri).into(search_imageview)

                           search_headerUsername.text ="${user?.firstname}"




                       }

                   }

               }


           })

    }


}