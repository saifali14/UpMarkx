package com.youngminds.upmarkx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.youngminds.upmarkx.Utils.AskLoadingbar
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {


    private lateinit var auth:FirebaseAuth


    val loading = AskLoadingbar(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)



        auth = FirebaseAuth.getInstance()


        forgotpass_backbtn.setOnClickListener {
            finish()
        }


        forgot_submtibtn.setOnClickListener {


            val email = forgotpass_editxt_email.text.toString()


            if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){


                Log.d("RegisterActivity", " $email")


                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener {

                        if (it.isSuccessful){

                            loading.startloading()
                            Toast.makeText(this,"Email sent...",Toast.LENGTH_LONG).show()

                            forgotpass_editxt_email.text?.clear()

                        }



                    }


            }else{

                Toast.makeText(this, "Invalid Email Address!", Toast.LENGTH_SHORT).show()

            }

        }


    }



}