package com.youngminds.upmarkx

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.storage.FirebaseStorage
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.Utils.EditprofileLoadingbar
import com.youngminds.upmarkx.Utils.RegisterationLoadingbar
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_info.*
import java.util.*

class EditProfileActivity : AppCompatActivity() {


      var user:User?=null
      var firstname:String?=null
      var lastname:String?=null
      var standard:String?=null
      var bio:String?=null
      var userimage:String?=null
      var userimageurl:String?=null

    val STORAGE_READ = 101
    val STORAGE_WRITE = 107


    val loading = EditprofileLoadingbar(this)

    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)


        firestore  = FirebaseFirestore.getInstance()


        fetchuserinfoo()



        editprofile_imageeditbtn.setOnClickListener {


            checkForPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE,"read storage",STORAGE_READ)

            checkForPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,"write storage",STORAGE_WRITE)
        }

        editprofile_backbtn.setOnClickListener { finish() }



        editprofile_savebtn.setOnClickListener {


            loading.startloading()

            decision()

        }


        editprofile_class_selector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {


                standard = parent?.getItemAtPosition(position).toString()



            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }


        }


    }


    private fun checkForPermission(permission:String , name:String , requestCode: Int){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            when{

                ContextCompat.checkSelfPermission(applicationContext,permission) == PackageManager.PERMISSION_GRANTED ->{

                    Toast.makeText(applicationContext,"$name permission granted",Toast.LENGTH_SHORT).show()

                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent,0)


                }

                shouldShowRequestPermissionRationale(permission) -> showDialog(permission,name,requestCode)

                else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)

            }


        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String){

            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){

                Toast.makeText(applicationContext,"$name permission refused",Toast.LENGTH_SHORT).show()

            }else{

                Toast.makeText(applicationContext,"$name permission granted",Toast.LENGTH_SHORT).show()

                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent,0)
            }


        }


        when(requestCode){

            STORAGE_READ -> innerCheck("read storage")

            STORAGE_WRITE -> innerCheck("write storage")

        }


    }


    private fun showDialog(permission: String, name:String, requestCode: Int){



        val builder = AlertDialog.Builder(this)

        builder.apply {

            setMessage("Permission to access your $name is required to use this app")
            setTitle("Permission required")
            setPositiveButton("Ok"){
                    dialog , which ->


                ActivityCompat.requestPermissions(this@EditProfileActivity, arrayOf(permission),requestCode)
            }

        }

        val dialog = builder.create()
        dialog.show()


    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode==0 && resultCode== Activity.RESULT_OK && data!=null){

            userimageurl = data.data.toString()

            Glide.with(applicationContext).load(userimageurl).into(editprofile_imageview)
        }
    }



    private fun uploadimagetostorage(){


        val uid = UUID.randomUUID()


        val dbstorage = FirebaseStorage.getInstance().getReference("UserPorfileImages").child(uid.toString()).child("Edited")
        dbstorage.putFile(userimageurl!!.toUri()).addOnSuccessListener {

            Log.d("Info","user image uploaded")
            dbstorage.downloadUrl.addOnSuccessListener {

                Log.d("Info","user image download")
               userimage = it.toString()

                upgradedata()

            }.addOnFailureListener{
                Log.d("Info","failed to upload image")
            }

        }

    }



    private fun decision(){



        if(userimageurl == null){

            upgradedata()

        }else{

            uploadimagetostorage()

        }


    }




    private fun fetchuserinfoo() {



        val currentuseruid = FirebaseAuth.getInstance().uid.toString()


        val dbuser = firestore.collection("UserInfo").whereEqualTo("uid",currentuseruid)
        dbuser.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){
                    Toast.makeText(this@EditProfileActivity,"Something went wrong", Toast.LENGTH_SHORT).show()

                }

                for (dc: DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){

                        user = dc.document.toObject(User::class.java)


                        editprofile_bio_edxt.setText(user?.bio)
                        editprofile_bioview.setText(user?.bio)

                        editprofile_classview.setText(user?.standard)

                        Glide.with(applicationContext).load(user?.imageuri).into(editprofile_imageview)

                        userimage = user?.imageuri.toString()

                        editprofile_nameview.setText(user?.firstname +" " + user?.lastname)
                        editprofile_firstname_edxt.setText(user?.firstname)
                        editprofile_secondname_edxt.setText(user?.lastname)


                    }



                }


            }
        })



    }



    private fun upgradedata(){



        val firstname = editprofile_firstname_edxt.text?.trim().toString().toLowerCase()
        val lastname = editprofile_secondname_edxt.text?.trim().toString().toLowerCase()
        val bio = editprofile_bio_edxt.text.toString()


        val currentuseruid = FirebaseAuth.getInstance().uid.toString()

        val db = firestore.collection("UserInfo")
            .document(currentuseruid)

        db.update("firstname",firstname).addOnSuccessListener {

            db.update("lastname",lastname).addOnSuccessListener {




                db.update("imageuri",userimage).addOnSuccessListener {
                    db.update("bio",bio).addOnSuccessListener {


                        db.update("keywords",keywords(firstname + "" + lastname))

                        db.update("standard",standard).addOnSuccessListener {

                            loading.startloading()

                            finish()

                        }
                    }
                }
            }






        }


    }


    private fun keywords(username:String):List<String>{

        val keywords = mutableListOf<String>()
        for ( i in 0 until username.length){

            for (j in (i+1)..username.length){

                keywords.add(username.slice(i until j))

            }

        }
//        Toast.makeText(this,"${keywords[0]}, ${keywords[1]}, ${keywords[2]}, ${keywords[3]}",Toast.LENGTH_SHORT).show()

        return keywords



    }
}