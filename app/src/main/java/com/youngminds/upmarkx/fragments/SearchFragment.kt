package com.youngminds.upmarkx.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.R
import com.youngminds.upmarkx.SearchActivity
import com.youngminds.upmarkx.UserProfileActivity
import com.youngminds.upmarkx.recyclerview.Searchrecyle
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*


class SearchFragment : Fragment() {




    private lateinit var firestore: FirebaseFirestore
    val searchadaptar = GroupAdapter<ViewHolder>()

    companion object{

        var user: User?=null
        val USERK = "USERK"
        var text:String?=null
        var subject:String?=null


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_search, container, false)



//        val args = this.arguments
//
//        val text = args?.get("key").toString()
//
//
//        view.textviewr.text = text





        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

//        text = intent.getStringExtra(MainActivity.TEXT).toString()

//        search_searchview.setText(text)




        if (view.searchfragment_searchview.text.isEmpty()){

            view.searchfragment_lottie_search.visibility = View.VISIBLE

        }else{

            view.searchfragment_lottie_search.visibility = View.GONE

        }




        firestore  = FirebaseFirestore.getInstance()
        view.fragmentsearch_recy.adapter =searchadaptar





        searchadaptar.setOnItemClickListener { item, _ ->

            val user  = item as Searchrecyle
            val intent = Intent(activity, UserProfileActivity::class.java)
            intent.putExtra(SearchActivity.USERK,user.user)
            startActivity(intent)

            val dbrecentsearch = firestore.collection("UserInfo")
                .document(currentuseruid)
                .collection("RecentlySearchUsers")
                .document(user.user.uid.toString())

            dbrecentsearch.set(user.user)

        }






        view.searchfragment_searchview.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {


                if (view.searchfragment_searchview.text.isEmpty()){

                    view.searchfragment_lottie_search.visibility = View.VISIBLE

                }else{

                    view.searchfragment_lottie_search.visibility = View.GONE

                }
                view.searchfragment_lottie_search.visibility = View.VISIBLE
                searchadaptar.clear()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                searchadaptar.clear()


                if (view.searchfragment_searchview.text.isEmpty()){

                    view.searchfragment_lottie_search.visibility = View.VISIBLE

                }else{

                    view.searchfragment_lottie_search.visibility = View.GONE

                }

                view.searchfragment_lottie_search.visibility = View.GONE

//                val text = s.toString()

                val seaerchuser = firestore.collection("UserInfo").whereArrayContains("keywords",
                    s?.trim().toString().toLowerCase()).limit(100)
                seaerchuser.addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {


                        if (error!=null){

                            Toast.makeText(activity,"Network issue is there",
                                Toast.LENGTH_SHORT).show()


                        }

                        for (dc: DocumentChange in value?.documentChanges!!){


                            if (dc.type == DocumentChange.Type.ADDED){

                                val user = dc.document.toObject(User::class.java)

                                searchadaptar.add(Searchrecyle(activity!!,user,false))

                            }

                        }


                    }


                })

            }

            override fun afterTextChanged(s: Editable?) {


                if (view.searchfragment_searchview.text.isEmpty()){

                    view.searchfragment_lottie_search.visibility = View.VISIBLE

                }else{

                    view.searchfragment_lottie_search.visibility = View.GONE

                }

            }


        })


        retreiveuser()


        return view
    }


    private fun retreiveuser() {

        val currentuser = FirebaseAuth.getInstance().uid.toString()
        val dbuser = firestore.collection("UserInfo").whereEqualTo("uid",currentuser)
        dbuser.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){
                    Toast.makeText(activity,"Network error",Toast.LENGTH_SHORT).show()

                }

                for(dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        val user = dc.document.toObject(User::class.java)


                        Glide.with(requireContext()).load(user.imageuri).into(fragmentsearch_imageview)

                        view?.fragmentsearch_headerUsername?.text ="${user.firstname}"




                    }

                }

            }


        })


    }


}