package com.youngminds.upmarkx

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import androidx.core.app.ActivityCompat
import java.util.jar.Manifest

class SMSActivity : AppCompatActivity() {



    private lateinit var numbers:ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smsactivity)




         numbers = arrayListOf()


        numbers.add("7869897007")
        numbers.add("7869897007")
        numbers.add("7869897007")
        numbers.add("7869897007")
        numbers.add("7869897007")
        numbers.add("7869897007")
        numbers.add("7869897007")
        numbers.add("9826080407")
        numbers.add("9826080407")
        numbers.add("9826080407")
        numbers.add("9826080407")

        checkpermission()



    }



    private fun checkpermission() {


        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS),111)


        }else{


            sendmessage()


        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            sendmessage()
        }
    }

    private fun sendmessage(){




        for (number in numbers){



            var obj = SmsManager.getDefault()

            obj.sendTextMessage(number,null,"Hello ",null,null)

        }

    }


}