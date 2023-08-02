package com.youngminds.upmarkx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_admin_login.*

class AdminLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)


        adminlogin_submtibtn.setOnClickListener {

            verifyuser()

        }


    }

    private fun verifyuser() {
        val password = adminlogin_editxt_email.text.toString()


        if (password == "saif786"){

            val intent = Intent(applicationContext,AdminpanelActivity::class.java)
            startActivity(intent)
            finish()

        }else{

            Toast.makeText(this,"invalid password",Toast.LENGTH_SHORT).show()
        }

    }


}