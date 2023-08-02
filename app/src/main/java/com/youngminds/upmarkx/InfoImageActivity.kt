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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.Utils.RegisterationLoadingbar
import com.youngminds.upmarkx.recyclerview.Newsrecy
import kotlinx.android.synthetic.main.activity_info_image.*
import java.util.*

class InfoImageActivity : AppCompatActivity() {




    private lateinit var database: FirebaseFirestore

    private lateinit var auth: FirebaseAuth


 

    val STORAGE_READ = 101
    val STORAGE_WRITE = 107

     var email:String = ""
     var password:String = ""
    var firstname:String = ""
    var lastname:String =""
    var standard:String =""


    val loading = RegisterationLoadingbar(this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_image)


        auth = FirebaseAuth.getInstance()

        database = FirebaseFirestore.getInstance()


         email = intent.getStringExtra(InfoActivity.EMAIL_KEY).toString()
         password = intent.getStringExtra(InfoActivity.PASSWORD_KEY).toString()
         firstname = intent.getStringExtra(InfoActivity.FIRSTNAME_KEY).toString()
         lastname = intent.getStringExtra(InfoActivity.LAST_NAME).toString()
         standard = intent.getStringExtra(InfoActivity.CLASS_KEY).toString()




        infoimage_termsandcondi.setOnClickListener {



            val intent = Intent(this,WebviewActivity::class.java)
            intent.putExtra(Newsrecy.WEB_KEY,"https://pages.flycricket.io/upmarks-0/terms.html")
            startActivity(intent)
        }



        infoimage_privacypoli.setOnClickListener {

            val intent = Intent(this,WebviewActivity::class.java)
            intent.putExtra(Newsrecy.WEB_KEY,"https://pages.flycricket.io/upmarks-0/privacy.html")
            startActivity(intent)
        }


        infoimage_pickimage.setOnClickListener {


            checkForPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE,"read storage",STORAGE_READ)

            checkForPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,"write storage",STORAGE_WRITE)

        }


        Infoimage_submit_btn.setOnClickListener {

            loading.startloading()
            createaccount()
        }

    }



    var imageuri: Uri = Uri.parse("android.resource://com.youngminds.upmarkx/drawable/default_usericon")

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode==0 && resultCode== Activity.RESULT_OK && data!=null){

            imageuri = data.data!!

            Glide.with(applicationContext).load(imageuri).into(profileimage_preview)
        }
    }




    private fun checkForPermission(permission:String , name:String , requestCode: Int){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            when{

                ContextCompat.checkSelfPermission(applicationContext,permission) == PackageManager.PERMISSION_GRANTED ->{

                    Toast.makeText(applicationContext,"$name permission granted", Toast.LENGTH_SHORT).show()

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

                Toast.makeText(applicationContext,"$name permission refused", Toast.LENGTH_SHORT).show()

            }else{

                Toast.makeText(applicationContext,"$name permission granted", Toast.LENGTH_SHORT).show()

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


                ActivityCompat.requestPermissions(this@InfoImageActivity, arrayOf(permission),requestCode)
            }

        }

        val dialog = builder.create()
        dialog.show()


    }



    private  fun createaccount(){


        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {

            if (!it.isSuccessful)return@addOnCompleteListener

            Log.d("Info","user email registered")
            uploadimagetostorage()

        }.addOnFailureListener { Toast.makeText(this,it.toString(), Toast.LENGTH_LONG).show() }
    }


    private fun uploadimagetostorage(){


        val uid = UUID.randomUUID()


        val dbstorage = FirebaseStorage.getInstance().getReference("UserPorfileImages").child(uid.toString())
        dbstorage.putFile(imageuri).addOnSuccessListener {

            Log.d("Info","user image uploaded")
            dbstorage.downloadUrl.addOnSuccessListener {

                Log.d("Info","user image download")
                saveusertodb(it.toString())
            }.addOnFailureListener{
                Log.d("Info","failed to upload image")
            }

        }

    }


    private fun saveusertodb(imageurl:String){


        val useruid = FirebaseAuth.getInstance().uid


        val user = User(firstname,lastname,email,standard,imageurl,useruid,System.currentTimeMillis(),"false",keywords(firstname+ " "+lastname),null,System.currentTimeMillis(),"Hey!, Im $firstname studying in $standard",null,null )


        database.collection("UserInfo").document(useruid.toString()).set(user).addOnCompleteListener {

            loading.startloading()


            if (!it.isSuccessful)return@addOnCompleteListener





            val intent = Intent(this,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()



        }.addOnFailureListener {

            Toast.makeText(this,"User Registered", Toast.LENGTH_SHORT).show()


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