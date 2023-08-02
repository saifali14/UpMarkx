package com.youngminds.upmarkx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.youngminds.upmarkx.recyclerview.Newsrecy
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)





        Register_backbtn.setOnClickListener {

            finish()
        }


        Register_logintext.setOnClickListener {

            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)

        }

        register_termsandcondi.setOnClickListener {

            val intent = Intent(this,WebviewActivity::class.java)
            intent.putExtra(Newsrecy.WEB_KEY,"https://pages.flycricket.io/upmarks-0/terms.html")
            startActivity(intent)
        }

        register_privacypoli.setOnClickListener {


            val intent = Intent(this,WebviewActivity::class.java)
            intent.putExtra(Newsrecy.WEB_KEY,"https://pages.flycricket.io/upmarks-0/privacy.html")
            startActivity(intent)


        }


        Register_btn.setOnClickListener {




            val email = Register_editxt_email.text.toString()
            val password = Register_editxt_password.text.toString()
            if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){


                Log.d("RegisterActivity", " $email , $password")


                val intent = Intent(this, InfoActivity::class.java)
                intent.putExtra("email", email)
                intent.putExtra("password", password)
                startActivity(intent)


            }else{

                Toast.makeText(this, "Invalid Email Address!",Toast.LENGTH_SHORT).show()

            }



        }






    }

}
