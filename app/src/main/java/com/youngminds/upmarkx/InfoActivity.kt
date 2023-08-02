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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.Utils.RegisterationLoadingbar
import kotlinx.android.synthetic.main.activity_info.*
import java.util.*

class InfoActivity : AppCompatActivity() {


    private lateinit var database: FirebaseFirestore
    private lateinit var userclass:String


    val STORAGE_READ = 101
    val STORAGE_WRITE = 107

    companion object{

        val EMAIL_KEY = "EMAIL_KEY"
        val PASSWORD_KEY = "PASSWORD_KEY"
        val FIRSTNAME_KEY =  "FIRST_NAME_KEY"
        val LAST_NAME = "LAST_NAME"
        val CLASS_KEY ="CLASS_KEY"

    }

    val loading = RegisterationLoadingbar(this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        database = FirebaseFirestore.getInstance()




        next_button.setOnClickListener {




            nextpage()
        }



        class_selector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {


                userclass = parent?.getItemAtPosition(position).toString()



            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }


        }


    }





//     private fun checkForPermission(permission:String , name:String , requestCode: Int){
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//
//            when{
//
//                ContextCompat.checkSelfPermission(applicationContext,permission) == PackageManager.PERMISSION_GRANTED ->{
//
//                    Toast.makeText(applicationContext,"$name permission granted",Toast.LENGTH_SHORT).show()
//
//                    val intent = Intent(Intent.ACTION_PICK)
//                    intent.type = "image/*"
//                    startActivityForResult(intent,0)
//
//
//                }
//
//                shouldShowRequestPermissionRationale(permission) -> showDialog(permission,name,requestCode)
//
//                else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
//
//            }
//
//
//        }
//
//    }
//
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        fun innerCheck(name: String){
//
//            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
//
//                Toast.makeText(applicationContext,"$name permission refused",Toast.LENGTH_SHORT).show()
//
//            }else{
//
//                Toast.makeText(applicationContext,"$name permission granted",Toast.LENGTH_SHORT).show()
//
//                val intent = Intent(Intent.ACTION_PICK)
//                intent.type = "image/*"
//                startActivityForResult(intent,0)
//            }
//
//
//        }
//
//
//        when(requestCode){
//
//            STORAGE_READ -> innerCheck("read storage")
//
//            STORAGE_WRITE -> innerCheck("write storage")
//
//        }
//
//
//    }
//
//
//    private fun showDialog(permission: String, name:String, requestCode: Int){
//
//
//
//        val builder = AlertDialog.Builder(this)
//
//        builder.apply {
//
//            setMessage("Permission to access your $name is required to use this app")
//            setTitle("Permission required")
//            setPositiveButton("Ok"){
//                dialog , which ->
//
//
//                ActivityCompat.requestPermissions(this@InfoActivity, arrayOf(permission),requestCode)
//            }
//
//        }
//
//        val dialog = builder.create()
//        dialog.show()
//
//
//    }



    private  fun nextpage(){



        val email = intent.getStringExtra("email").toString()
        val password = intent.getStringExtra("password").toString()
        val firstname = info_firstname_edxt.text?.trim().toString().toLowerCase()
        val lastname = info_secondname_edtxt.text?.trim().toString().toLowerCase()


        val intent = Intent(this,InfoImageActivity::class.java)
        intent.putExtra(EMAIL_KEY,email)
        intent.putExtra(PASSWORD_KEY,password)
        intent.putExtra(FIRSTNAME_KEY,firstname)
        intent.putExtra(LAST_NAME,lastname)
        intent.putExtra(CLASS_KEY,userclass)

        startActivity(intent)


    }

//
//    private fun uploadimagetostorage(){
//
//
//        val uid = UUID.randomUUID()
//
//
//        val dbstorage = FirebaseStorage.getInstance().getReference("UserPorfileImages").child(uid.toString())
//        dbstorage.putFile(imageuri).addOnSuccessListener {
//
//            Log.d("Info","user image uploaded")
//            dbstorage.downloadUrl.addOnSuccessListener {
//
//                Log.d("Info","user image download")
//                saveusertodb(it.toString())
//            }.addOnFailureListener{
//                Log.d("Info","failed to upload image")
//            }
//
//        }
//
//    }


//    private fun saveusertodb(imageurl:String){
//
//
//        val useruid = FirebaseAuth.getInstance().uid
//        val useremail = intent.getStringExtra("email")
//        val firstname = info_firstname_edxt.text?.trim().toString().toLowerCase()
//        val lastname = info_secondname_edtxt.text?.trim().toString().toLowerCase()
//
//        val user = User(firstname,lastname,useremail,userclass,imageurl,useruid,"false",keywords(firstname+ " "+lastname),null,System.currentTimeMillis(),null,null,null )
//
//
//        database.collection("UserInfo").document(useruid.toString()).set(user).addOnCompleteListener {
//
//            loading.startloading()
//
//
//            if (!it.isSuccessful)return@addOnCompleteListener
//
//
//
//
//
//            val intent = Intent(this,MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//            finish()
//
//
//
//        }.addOnFailureListener {
//
//            Toast.makeText(this,"User Registered",Toast.LENGTH_SHORT).show()
//
//
//        }
//
//
//    }


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