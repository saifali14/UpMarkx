package com.youngminds.upmarkx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.Gifs
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.Models.Testing
import kotlinx.android.synthetic.main.activity_testing.*


class TestingActivity : AppCompatActivity() {

    lateinit var firestore: FirebaseFirestore
    lateinit var settings: FirebaseFirestoreSettings

    val adaptar = GroupAdapter<ViewHolder>()

    companion object{

        var usercount = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)
        firestore = FirebaseFirestore.getInstance()





        Glide.with(this@TestingActivity).asGif().load(R.drawable.giphy).into(gifimageview)



        arraytest()





    }

    private fun arraytest() {



        val array = ArrayList<String>()
        array.add("saif")


        val testing = Testing("hello","saif",array)
//
//        val db  = firestore.collection("Arraytest").document("1")
//        db.set(testing)


        val adddata = firestore.collection("Arraytest").document("1")

        adddata.update("likes","ali",SetOptions.merge())


    }

    private fun  loadgifs() {

        val db = firestore.collection("Gifs")
        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if(error!=null){
                    Toast.makeText(this@TestingActivity,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        val gifs = dc.document.toObject(Gifs::class.java)

                        Toast.makeText(this@TestingActivity,gifs.url,Toast.LENGTH_SHORT).show()

                        Glide.with(this@TestingActivity).asGif().load(R.drawable.giphy).into(gifimageview)

                    }

                }

            }


        })


    }


    private fun generatingkeywords(name:String):List<String>{


        val keywords = mutableListOf<String>()
        for ( i in 0 until name.length){

            for (j in (i+1)..name.length){

                keywords.add(name.slice(i until j))

            }

        }
//        Toast.makeText(this,"${keywords[0]}, ${keywords[1]}, ${keywords[2]}, ${keywords[3]}",Toast.LENGTH_SHORT).show()

        return keywords


    }


    private fun leaderboardfinder() {


        val db = firestore.collection("UserInfo")
        db.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(this@TestingActivity,"Something went wrong",Toast.LENGTH_SHORT).show()
                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        val user  =  dc.document.toObject(User::class.java)

                        adaptar.add(Testingrecy(user))
                    }

                }


            }


        })


    }


    private fun addlikesystem(){


        val likes = ArrayList<String>()

        likes.add("saif")
        if (likes.contains("saif")){
            Toast.makeText(this,"yes  it  contains",Toast.LENGTH_SHORT).show()

        }else{

            Toast.makeText(this,"no  it  doesnt  contains",Toast.LENGTH_SHORT).show()
        }
        val testdata = Testing("dontknow","reallydontknow",likes )

        val adduser = firestore.collection("likes").document("11").set(testdata).addOnSuccessListener {

            Toast.makeText(this,"done",Toast.LENGTH_SHORT).show()

        }}
    private fun countdocument() {



        val db = firestore.collection("UserInfo")
        db.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {



                if (error!=null){

                    Toast.makeText(this@TestingActivity,error.message,Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type  == DocumentChange.Type.ADDED){

                        usercount++


                    }

                }



                Toast.makeText(this@TestingActivity, usercount.toString(),Toast.LENGTH_SHORT).show()

            }


        })


    }

    private fun hellousee() {


        val  test = Testing("fdd","fd",)
        val db  = firestore.collection("testing").document().set(test).addOnSuccessListener {


            Toast.makeText(this,"succesfully  saved the test data",Toast.LENGTH_SHORT).show()



        }


    }


    private fun likecount(){


        val  db = firestore.collection("user").document("FLEaM1n7r0NLceM1gvPK").collection("likes")
        db.addSnapshotListener{value , e ->

            Toast.makeText(this,"${value?.toObjects(Testing::class.java)}",Toast.LENGTH_SHORT).show()
            Log.d("Testing",value?.toObjects(Testing::class.java).toString())



        }

    }

    private fun test3(){


        val db = firestore.collection("user").document("FLEaM1n7r0NLceM1gvPK").collection("likes").get()
        db.addOnCompleteListener { task ->

            if (task.isSuccessful){
                var count = 0
                task.result?.let {

                    for (snapshot in it){

                        count++

                    }
                }

                Toast.makeText(this,count,Toast.LENGTH_SHORT).show()

        }else{

            task.exception?.message.let {


                    Log.d("Testing", it.toString())


            }
            }







        }


    }

    private fun test2(){


        val test = Testing("saif","shaikh")
        val db = firestore.collection("user").document("FLEaM1n7r0NLceM1gvPK").set(test).addOnCanceledListener {

            Toast.makeText(this,"hello done bro",Toast.LENGTH_SHORT).show()

        }


    }



    private fun test1(){

        val test = Testing("saif","shaikh")
        val db = firestore.collection("user").document().collection("likes").document().set(test)
        db.addOnSuccessListener {

            Toast.makeText(this,"hello done bro",Toast.LENGTH_SHORT).show()
        }



    }

//
//    private fun liikesystem(){
//
//        val likecheck = firestore.collection("likes").whereEqualTo("senderid","dontknow")
//
//        likecheck.addz {
//
//
//
//                val post  = it.toObject
//                post.likes
//
//
//
//
//        }













//    private fun adduser(){
//
//
//        val user = User("saif","shaikh","saifarisha@gmail.com","10")
//
//        firestore.collection("teest").document("helllo").set(user).addOnSuccessListener {
//
//
//            Toast.makeText(this,"done",Toast.LENGTH_SHORT).show()
//        }
//
//    }


    private fun retreiveuser(){



        val db =  firestore.collection("messages").whereEqualTo("senderid","saif")

        db.addSnapshotListener{value,e ->

            if (e!=null){

                Toast.makeText(this,"error while fetching the data",Toast.LENGTH_SHORT).show()
                return@addSnapshotListener

            }

            Toast.makeText(this,"${value?.toObjects(Testing::class.java)}",Toast.LENGTH_SHORT).show()
            Log.d("Testing", value!!.toObjects(Testing::class.java).toString())

        }
    }

}