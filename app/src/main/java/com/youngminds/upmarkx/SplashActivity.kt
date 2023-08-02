package com.youngminds.upmarkx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)




        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        Handler().postDelayed({

                     checklogin()

        },3000)


    }

    private fun checklogin() {

        val currentuser = FirebaseAuth.getInstance().currentUser?.uid
        if (currentuser!=null){

            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()

        }else{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()

        }


    }
}