package com.youngminds.upmarkx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        Login_btn.setOnClickListener {


            if (Login_editxt_email.text!!.isNotEmpty() && Login_editxt_password.text!!.isNotEmpty()){

                login()

            }else{

                Toast.makeText(this,"Please Enter your Email and Password",Toast.LENGTH_SHORT).show()

            }



        }

        login_forgotpasstext.setOnClickListener {

            val intent = Intent(this,ForgotPasswordActivity::class.java)
            startActivity(intent)

        }




        login_registertext.setOnClickListener {

            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)

        }


    }



    private fun login() {


        val email = Login_editxt_email.text.toString()
        val password = Login_editxt_password.text.toString()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if (!it.isSuccessful)return@addOnCompleteListener

            val intent = Intent(this,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()






        }.addOnFailureListener {

            Toast.makeText(this,it.toString(), Toast.LENGTH_LONG).show()
            Log.d("Login",it.message.toString())

        }



    }
    }
