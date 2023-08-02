package com.youngminds.upmarkx



import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.Models.DeliverChat
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.Utils.TimeStamp
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.inchat_card.view.*
import kotlinx.android.synthetic.main.inchat_shareprofile.view.*
import kotlinx.android.synthetic.main.inimagechat.view.*
import kotlinx.android.synthetic.main.outchat_card.view.*
import kotlinx.android.synthetic.main.outchat_shareprofile.view.*
import kotlinx.android.synthetic.main.outimagechat.view.*
import java.text.SimpleDateFormat
import java.util.*


class ChatLogActivity : AppCompatActivity() {


    val adaptar =  GroupAdapter<ViewHolder>()

    private lateinit var firestore: FirebaseFirestore
    private val GALLERY_REQUEST_CODE = 1234


    companion object{

        val TAG = "ChatLogActivity"

        var outuser: User?=null
        var inuser: User?=null
        val IMAGE_KEY = "IMAGE_KEY"

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)



        firestore =  FirebaseFirestore.getInstance()

        chatlog_recyclerview.adapter  = adaptar

        outuser =  intent.getParcelableExtra(MembersActivity.USER_KEY)




        supportActionBar?.title = outuser?.firstname
        chatlog_headerUsername.text = outuser?.firstname
        Glide.with(this).load(outuser?.imageuri).into(chatlog_imageview)


        if (outuser?.verified == "true"){

            chatlog_verfied.visibility = View.VISIBLE

        }else{
            chatlog_verfied.visibility = View.GONE

        }


        chatlog_backbtn.setOnClickListener{

            finish()

        }




        chatlog_imageview.setOnClickListener {


            val intent = Intent(applicationContext,UserProfileActivity::class.java)
            intent.putExtra(SearchActivity.USERK, outuser)
            startActivity(intent)

        }






        retreivewuser()

        listenmessages()

        statuscheck(outuser?.uid.toString())


        chatlog_sendbtn.setOnClickListener {

            if (chatlog_edtxt.text.isNotEmpty()){

                sendmessages()
            }else{

                Toast.makeText(this@ChatLogActivity,"Message is Empty",Toast.LENGTH_SHORT).show()
            }


        }

        chatlog_emoji_btn.setOnClickListener {


            pickimagefromgallery()

        }




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            GALLERY_REQUEST_CODE -> {

                if (resultCode == Activity.RESULT_OK) {

                    data?.data?.let { uri ->

                        launchImageCrop(uri)
                    }


                } else {

                    Log.e(TAG, "Image selection error: Couldn't select image from memory")
                }

            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {

                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {

                    result.uri?.let {

                        uploadtostorage(it)
                    }


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                    Log.e(TAG, "crop error: ${result.error}")

                }

            }

        }
    }





    private fun launchImageCrop(uri: Uri) {

        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)

    }


    private fun pickimagefromgallery(){

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
          intent.type = "image/*"
        val mimeType = arrayOf("image/jpeg","image/png","image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeType)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent,GALLERY_REQUEST_CODE)
    }


    private fun statuscheck(uid:String){


        val statusdb  = firestore.collection("UserInfo").whereEqualTo("uid",uid)
        statusdb.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {



                if (error!=null){

                    Toast.makeText(applicationContext,"Network issue",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){

                        val  statusuer = dc.document.toObject(User::class.java)


                        if (statusuer.status == "Offline"){

                            chatlog_status_Textview.text = "Active " +statusuer.timestamp?.let { TimeStamp.getTimeAgo(it) }

                        }
                        if(statusuer.status == "Online"){
                            chatlog_status_Textview.text = "Active now"


                        }





                    }
                    if (dc.type == DocumentChange.Type.MODIFIED){

                        val  statusuer = dc.document.toObject(User::class.java)

                        if (statusuer.status == "Offline"){

                            chatlog_status_Textview.text = "Active " +statusuer.timestamp?.let { TimeStamp.getTimeAgo(it) }

                        }
                        if(statusuer.status == "Online"){
                            chatlog_status_Textview.text = "Active now"


                        }

                    }

                }



            }
        })




    }




    private fun retreivewuser() {


        val currentuser = FirebaseAuth.getInstance().uid.toString()


        val dbmem = firestore.collection("UserInfo").whereEqualTo("uid",currentuser)
            dbmem.addSnapshotListener { value, error ->
                if (error != null) {

                    Log.d(TAG, error.message.toString())
                }


                for (dc: DocumentChange in value?.documentChanges!!) {


                    if (dc.type == DocumentChange.Type.ADDED) {

                        inuser = dc.document.toObject(User::class.java)


                    }

                }
            }
    }



    private fun listenmessages(){



        val currentuser = FirebaseAuth.getInstance().uid.toString()
        val outid = outuser?.uid.toString()

        val dblisten = firestore.collection("Messages").document(currentuser).collection(outid).orderBy("timestamp",Query.Direction.ASCENDING)
        dblisten.addSnapshotListener(object : EventListener<QuerySnapshot>{


            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(this@ChatLogActivity,error.message,Toast.LENGTH_SHORT).show()
                }




                for (dc: DocumentChange in  value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                   




                     val message = dc.document.toObject(DeliverChat::class.java)

                        if (message.inid == currentuser){



                            if(message.type =="Text"){

                                adaptar.add(inchatrecy(this@ChatLogActivity,applicationContext,message, inuser!!, outuser!!))
                            }else if(message.type == "Image"){


                                adaptar.add(InImagerecy(this@ChatLogActivity,message, inuser!!, outuser!!))
                            }else{


                                adaptar.add(inchatshareprofile(this@ChatLogActivity,message, inuser!! , outuser!!))


                            }



                        }else{

                            if (message.type == "Text"){

                                adaptar.add(outchatrecy(this@ChatLogActivity,applicationContext,message, outuser!!, inuser!!))
                            }else if(message.type == "Image"){

                                adaptar.add(OutImagerecy(this@ChatLogActivity,message, outuser!!,
                                    inuser!!))

                            }else{

                                adaptar.add(outchatshareprofilerecy(this@ChatLogActivity,message , outuser!! , inuser!!))
                            }



                        }
                        chatlog_recyclerview.scrollToPosition(adaptar.itemCount -1)

                    }








                }




            }


        })



    }

    private fun uploadtostorage(uri:Uri?){
        val uid = UUID.randomUUID()

        val dbstorage = FirebaseStorage.getInstance().getReference("MessageImages").child(uid.toString())
        if (uri != null) {
            dbstorage.putFile(uri).addOnSuccessListener {

                Log.d("Info","user image uploaded")
                dbstorage.downloadUrl.addOnSuccessListener {

                    Log.d("Info","user image download")
                    uploadimage(it)
                }.addOnFailureListener{
                    Log.d("Info","failed to upload image")
                }

            }
        }

    }

    private fun uploadimage(uri: Uri?) {

        val currentuser = FirebaseAuth.getInstance().uid.toString()
        val messageid = UUID.randomUUID().toString()
        val message  = uri.toString()
        val outid = outuser?.uid.toString()
        val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()).toString()
        val date  = SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(Date()).toString()
        val sendImagein =  firestore.collection("Messages").document(currentuser).collection(outid).document(messageid)
        val sendImageout = firestore.collection("Messages").document(outid).collection(currentuser).document(messageid)

        val latestImagein =  firestore.collection("LatestMessages").document(currentuser).collection("messsages").document(outid)
        val latestImageout =  firestore.collection("LatestMessages").document(outid).collection("messsages").document(currentuser)
        val deliverChat = DeliverChat(sendImagein.id,message,currentuser,outid,time,date,System.currentTimeMillis() / 1000,"false","Image",currentuser,false,false)
        val latestdeliverChat = DeliverChat(messageid,"Photo",currentuser, outid,time,date,System.currentTimeMillis(),"false","Image",currentuser,false,false)


        sendImagein.set(deliverChat).addOnSuccessListener {

            Log.d(TAG,"successfully Image delivered${sendImagein.id}")

            sendImageout.set(deliverChat).addOnSuccessListener {

                latestImageout.set(latestdeliverChat) .addOnSuccessListener {
                    latestImagein.set(latestdeliverChat)





                }
            }


        }.addOnFailureListener {

            Toast.makeText(this,"failed to send Image",Toast.LENGTH_SHORT).show()

        }




    }





    private fun sendmessages() {


        val currentuser = FirebaseAuth.getInstance().uid.toString()

        val message  = chatlog_edtxt.text.toString()
        val outid = outuser?.uid.toString()
        val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()).toString()
        val date  = SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(Date()).toString()

        val messageid = UUID.randomUUID().toString()




        val sendmessagein =  firestore.collection("Messages").document(currentuser).collection(outid).document(messageid)
        val sendmessageout = firestore.collection("Messages").document(outid).collection(currentuser).document(messageid)
        val notificationmess = firestore.collection("UserInfo").document(outid).collection("Notification_Messages").document(messageid)

        val latestmessagein =  firestore.collection("LatestMessages").document(currentuser).collection("messsages").document(outid)
        val latestmessageout =  firestore.collection("LatestMessages").document(outid).collection("messsages").document(currentuser)
        val deliverChat = DeliverChat(sendmessagein.id,message,currentuser,outid,time,date,System.currentTimeMillis() / 1000,"false","Text",currentuser,false,false)
        val latestdeliverChat = DeliverChat(messageid,message,currentuser, outid,time,date,System.currentTimeMillis(),"false","Text",currentuser,false,false)




        sendmessagein.set(deliverChat).addOnSuccessListener {

            Log.d(TAG,"successfully delivered message${sendmessagein.id}")


            sendmessageout.set(deliverChat).addOnSuccessListener {

                latestmessageout.set(latestdeliverChat).addOnSuccessListener {

                    latestmessagein.set(latestdeliverChat).addOnSuccessListener {



                            chatlog_edtxt.text.clear()








                    }

                }


            }



        }.addOnFailureListener {

            Toast.makeText(this,"failed to send message",Toast.LENGTH_SHORT).show()

        }










    }




    class inchatrecy(val context: Context,val imagecontext:Context, val message: DeliverChat, val inuser: User, val  outser: User):Item<ViewHolder>(){



        private lateinit var builder:AlertDialog.Builder
        private lateinit var firestore: FirebaseFirestore


        companion object{
            var recentmessage: DeliverChat?=null
            var currentmessage:DeliverChat?=null

        }


        override fun bind(viewHolder: ViewHolder, position: Int) {


            firestore = FirebaseFirestore.getInstance()

            builder = AlertDialog.Builder(context)

         //   viewHolder.itemView.inchat_textview.text = message.message



            val lmess = firestore.collection("Messages").document(inuser.uid.toString())
                .collection(outuser?.uid.toString()).whereEqualTo("messageid",message.messageid.toString())



            lmess.addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    if (error!=null){

                        Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()

                    }
                    for (dc:DocumentChange in value?.documentChanges!!){

                        if (dc.type == DocumentChange.Type.ADDED){


                            currentmessage = dc.document.toObject(DeliverChat::class.java)


                            if (message.deleted == false){
                                viewHolder.itemView.inchat_textview.text = currentmessage?.message.toString()

                            }else{


                                viewHolder.itemView.inchat_textview.text = "You Deletd this message"
                            }


                            if (currentmessage!!.like == true){
                                viewHolder.itemView.inchatcard_like.visibility = View.VISIBLE


                            }else{

                                viewHolder.itemView.inchatcard_like.visibility = View.GONE

                            }



                            val currentimestamp  = System.currentTimeMillis() / 1000
                            val currenttimeinmin = currentimestamp /60
                            val currenttimeinhour = currenttimeinmin / 60
                            val currentimeinday  = currenttimeinhour /24

                            // message time

                            val messagetimeinmin = message.timestamp!! / 60
                            val messagetimeinhour = messagetimeinmin  / 60
                            val messagetimeinday =  messagetimeinhour /  24

                            val valuea  = 1



                            val timediff: Long = currentimeinday - messagetimeinday

                            if (timediff >= valuea){

                                viewHolder.itemView.inchat_datetextview.text =  currentmessage?.date
                                viewHolder.itemView.inchat_timetextview.text =  currentmessage?.time

                            }else{

                                viewHolder.itemView.inchat_timetextview.text =  currentmessage?.time

                            }

                            if (currentmessage?.seen == "true"){

                                Glide.with(imagecontext).load(R.drawable.seen_icon).into(viewHolder.itemView.inchat_seenicon)
                            }else{

                                Glide.with(imagecontext).load(R.drawable.notseen_icon).into(viewHolder.itemView.inchat_seenicon)
                            }


                            if (currentmessage!!.like == true){
                                viewHolder.itemView.inchatcard_like.visibility = View.VISIBLE


                            }else{

                                viewHolder.itemView.inchatcard_like.visibility = View.GONE

                            }


                        }

                        if (dc.type == DocumentChange.Type.MODIFIED){

                            currentmessage = dc.document.toObject(DeliverChat::class.java)


                            if (currentmessage?.deleted == false){
                                viewHolder.itemView.inchat_textview.text = currentmessage?.message.toString()

                            }else{

                                viewHolder.itemView.inchat_textview.text = "You Deleted this message"

                            }




                            val currentimestamp  = System.currentTimeMillis() / 1000
                            val currenttimeinmin = currentimestamp /60
                            val currenttimeinhour = currenttimeinmin / 60
                            val currentimeinday  = currenttimeinhour /24

                            // message time

                            val messagetimeinmin = message.timestamp!! / 60
                            val messagetimeinhour = messagetimeinmin  / 60
                            val messagetimeinday =  messagetimeinhour /  24

                            val value  = 1



                            val timediff: Long = currentimeinday - messagetimeinday

                            if (timediff >= value){

                                viewHolder.itemView.inchat_datetextview.text =  currentmessage?.date
                                viewHolder.itemView.inchat_timetextview.text =  currentmessage?.time

                            }else{

                                viewHolder.itemView.inchat_timetextview.text =  currentmessage?.time

                            }

                            if (currentmessage?.seen == "true"){

                                Glide.with(imagecontext).load(R.drawable.seen_icon).into(viewHolder.itemView.inchat_seenicon)
                            }else{

                                Glide.with(imagecontext).load(R.drawable.notseen_icon).into(viewHolder.itemView.inchat_seenicon)
                            }



                            if (currentmessage!!.like == true){
                                viewHolder.itemView.inchatcard_like.visibility = View.VISIBLE

                                viewHolder.itemView.inchat_heartlottie.visibility = View.VISIBLE
                                viewHolder.itemView.inchat_heartlottie.playAnimation()

                            }else{

                                viewHolder.itemView.inchatcard_like.visibility = View.GONE
                                viewHolder.itemView.inchat_heartlottie.visibility = View.GONE

                            }
                        }

                    }


                }


            })
























            val currentimestamp  = System.currentTimeMillis() / 1000
            val currenttimeinmin = currentimestamp /60
            val currenttimeinhour = currenttimeinmin / 60
            val currentimeinday  = currenttimeinhour /24

            // message time

            val messagetimeinmin = message.timestamp!! / 60
            val messagetimeinhour = messagetimeinmin  / 60
            val messagetimeinday =  messagetimeinhour /  24

            val value  = 1



            val timediff: Long = currentimeinday - messagetimeinday

            if (timediff >= value){

                viewHolder.itemView.inchat_datetextview.text =  currentmessage?.date
                viewHolder.itemView.inchat_timetextview.text =  currentmessage?.time

            }else{

                viewHolder.itemView.inchat_timetextview.text =  currentmessage?.time

            }

            if (currentmessage?.seen == "true"){

                Glide.with(imagecontext).load(R.drawable.seen_icon).into(viewHolder.itemView.inchat_seenicon)
            }else{

                Glide.with(imagecontext).load(R.drawable.notseen_icon).into(viewHolder.itemView.inchat_seenicon)
            }



            //delete message






            viewHolder.itemView.chatlog_inchatcard.setOnLongClickListener {


                builder.setMessage("Do you want to unsend this message")
                    .setCancelable(true)
                    .setIcon(R.drawable.delete_message)
                    .setPositiveButton("Yes"){dialogInterface , _  ->




                        Log.d(TAG,"messageid:${message.messageid}, outuser:${outser.uid}, inuser:${inuser.uid}")

                        val dbin = firestore.collection("Messages").document(inuser.uid.toString())
                            .collection(outuser?.uid.toString()).document(message.messageid.toString())
                         dbin.update("deleted",true).addOnSuccessListener {



                             val dbout = firestore.collection("Messages").document(outser.uid.toString()).collection(inuser.uid.toString())
                                 .document(message.messageid.toString())
                             dbout.update("deleted",true)

                         }.addOnSuccessListener {


                             val dblatestmess  = firestore.collection("LatestMessages")
                                 .document(inuser.uid.toString())
                                 .collection("messsages")

                             dblatestmess.addSnapshotListener(object : EventListener<QuerySnapshot>{
                                 override fun onEvent(
                                     value: QuerySnapshot?,
                                     error: FirebaseFirestoreException?
                                 ) {

                                     if (error!=null){


                                         Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()

                                     }

                                     for (dc:DocumentChange in value?.documentChanges!!){


                                         if (dc.type ==  DocumentChange.Type.ADDED){

                                             recentmessage = dc.document.toObject(DeliverChat::class.java)

                                             if (recentmessage!!.messageid == currentmessage?.messageid){

                                                 val updaterecentmess  =  firestore.collection("LatestMessages").document(inuser.uid.toString())
                                                     .collection("messsages").document(outser.uid.toString())

                                                 updaterecentmess.update("deleted",true)

                                                     val updaterecentmessout = firestore.collection("LatestMessages").document(outser.uid.toString())
                                                         .collection("messsages").document(inuser.uid.toString())

                                                     updaterecentmessout.update("deleted",true)





                                             }


                                         }

                                     }
                                 }


                             })




                         }








                    }
                    .setNegativeButton("No"){dialogInterface, _ ->

                        dialogInterface.cancel()

                    }
                    .show()

                 false


            }









        }



        override fun getLayout(): Int {

           return R.layout.inchat_card
        }


    }



    class outchatrecy(val context: Context,val imagecontext: Context, val message: DeliverChat, val outser: User, val inuser: User):Item<ViewHolder>(){

        private lateinit var firestore: FirebaseFirestore

        companion object{
            var recentmessage:DeliverChat?=null

        }

        override fun bind(viewHolder: ViewHolder, position: Int) {

            firestore =  FirebaseFirestore.getInstance()

            Glide.with(imagecontext).load(outser.imageuri).into(viewHolder.itemView.outchat_imageview)
            viewHolder.itemView.outchat_timetextview.text = message.time
            val seen = message.seen
            val messageid = message.messageid.toString()




            val lmess = firestore.collection("Messages").document(inuser.uid.toString())
                .collection(outuser?.uid.toString()).whereEqualTo("messageid",message.messageid.toString())


                lmess.addSnapshotListener(object : EventListener<QuerySnapshot>{
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {


                        for (dc:DocumentChange in value?.documentChanges!!){

                            if (dc.type == DocumentChange.Type.ADDED) {

                                recentmessage = dc.document.toObject(DeliverChat::class.java)


                                if (recentmessage?.deleted ==false){

                                    viewHolder.itemView.outchat_textview.text = recentmessage?.message

                                }else{

                                    viewHolder.itemView.outchat_textview.text = "User deleted this message"

                                }




                                if (recentmessage!!.like == true){
                                    viewHolder.itemView.outchatcard_like.visibility = View.VISIBLE
                                    viewHolder.itemView.outchatcard_removelikebtn.visibility = View.VISIBLE

                                }else{

                                    viewHolder.itemView.outchatcard_like.visibility = View.GONE
                                    viewHolder.itemView.outchatcard_removelikebtn.visibility = View.GONE
                                }

                            }
                            if (dc.type == DocumentChange.Type.MODIFIED){

                                recentmessage = dc.document.toObject(DeliverChat::class.java)

                                viewHolder.itemView.outchat_textview.text = recentmessage?.message


                                if (recentmessage!!.like == true){
                                    viewHolder.itemView.outchatcard_like.visibility = View.VISIBLE
                                    viewHolder.itemView.outchatcard_removelikebtn.visibility = View.VISIBLE
//                                    viewHolder.itemView.outchat_heartlottie.visibility =View.VISIBLE
//                                    viewHolder.itemView.outchat_heartlottie.playAnimation()


                                }else{

                                    viewHolder.itemView.outchatcard_like.visibility = View.GONE
                                    viewHolder.itemView.outchatcard_removelikebtn.visibility = View.GONE
//                                    viewHolder.itemView.outchat_heartlottie.visibility =View.GONE
                                }


                                if (recentmessage?.deleted ==false){

                                    viewHolder.itemView.outchat_textview.text = recentmessage?.message

                                }else{

                                    viewHolder.itemView.outchat_textview.text = "User deleted this message"

                                }
                            }
                        }
                    }


                })



            if (message.like == true){
                viewHolder.itemView.outchatcard_like.visibility = View.VISIBLE

            }else{

                viewHolder.itemView.outchatcard_like.visibility = View.GONE
            }



            viewHolder.itemView.outchatcard_removelikebtn.setOnClickListener {

                val dblikerem = firestore.collection("Messages").document(outser.uid.toString()).collection(inuser.uid.toString())
                    .document(message.messageid.toString())

                dblikerem.update("like",false)

                val dblikeinrem = firestore.collection("Messages").document(inuser.uid.toString()).collection(outser.uid.toString())
                    .document(message.messageid.toString())

                dblikeinrem.update("like",false)



            }



            viewHolder.itemView.chatlog_outchatcard.setOnLongClickListener {





//                val bottomSheetDialog = BottomSheetDialog(context,R.style.BottomSheetDialogTheme_chatlog)
//                val bottomSheeetView = LayoutInflater.from(context).inflate(R.layout.outchatcard_bottomsheet,null) as RelativeLayout?
//
//
//
//                bottomSheeetView?.outchat_bottomsheet_reply






                    val dblikeadd = firestore.collection("Messages").document(outser.uid.toString()).collection(inuser.uid.toString())
                        .document(message.messageid.toString())

                    dblikeadd.update("like",true).addOnSuccessListener {
                        val dblikeinadd = firestore.collection("Messages").document(inuser.uid.toString()).collection(outser.uid.toString())
                            .document(message.messageid.toString())

                        dblikeinadd.update("like",true)
                            .addOnSuccessListener {


                                val latestmessi =  firestore.collection("LatestMessages")
                                    .document(inuser.uid.toString())
                                    .collection("messsages")
                                    .document(message.messageid.toString())

                                latestmessi.update("like",true)


                                    val latestmess0 =  firestore.collection("LatestMessages")
                                        .document(inuser.uid.toString())
                                        .collection("messsages")
                                        .document(message.messageid.toString())

                                    latestmess0.update("like",true)







                            }




                    }

















                true

            }













            viewHolder.itemView.outchat_username.text = outser.firstname

            val currentimestamp  = System.currentTimeMillis() / 1000
            val currenttimeinmin = currentimestamp /60
            val currenttimeinhour = currenttimeinmin / 60
            val currentimeinday  = currenttimeinhour /24

            // message time

            val messagetimeinmin = message.timestamp!! / 60
            val messagetimeinhour = messagetimeinmin  / 60
            val messagetimeinday =  messagetimeinhour /  24

            val value  = 1



            val timediff: Long = currentimeinday - messagetimeinday

            if (timediff >= value){

                viewHolder.itemView.outchat_datetextviwe.text =  message.date
                viewHolder.itemView.outchat_timetextview.text =  message.time

            }else{

                viewHolder.itemView.outchat_timetextview.text =  message.time

            }


            if (seen == "false"){


                val dbseen = firestore.collection("Messages").document(outser.uid.toString()).collection(inuser.uid.toString()).document(messageid)
                dbseen.update("seen","true")



                    val latestseen =  firestore.collection("LatestMessages").document(outser.uid.toString())
                        .collection("messsages").document(inuser.uid.toString())
                    latestseen.update("seen","true")









            }








        }

        override fun getLayout(): Int {

            return R.layout.outchat_card
        }


    }




    class InImagerecy(val context: Context, val message: DeliverChat, val inuser: User, val  outser: User):Item<ViewHolder>(){

        private lateinit var builder:AlertDialog.Builder
        private lateinit var firestore: FirebaseFirestore

        companion object{

            var recentmessage: DeliverChat?=null
            var currentmessage:DeliverChat?=null

        }

        override fun bind(viewHolder: ViewHolder, position: Int) {

            firestore = FirebaseFirestore.getInstance()

            builder = AlertDialog.Builder(context)

            Glide.with(context).load(message.message).into(viewHolder.itemView.inImage_Imageview)


            if (message.deleted == true){

                viewHolder.itemView.inImage_Imageview.visibility  =View.GONE
                viewHolder.itemView.inimage_deletetext.visibility = View.VISIBLE
                viewHolder.itemView.inimage_deletetext.text = "You deleted the image..."


            }


            val lmess = firestore.collection("Messages").document(inuser.uid.toString())
                .collection(outuser?.uid.toString()).whereEqualTo("messageid",message.messageid.toString())

            lmess.addSnapshotListener(object :EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    if (error!=null){
                        Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()

                    }

                    for (dc:DocumentChange in value?.documentChanges!!){

                        if (dc.type  == DocumentChange.Type.ADDED){

                            currentmessage = dc.document.toObject(DeliverChat::class.java)



                            if (currentmessage?.deleted == true){

                                viewHolder.itemView.inImage_Imageview.visibility  =View.GONE
                                viewHolder.itemView.inimage_deletetext.visibility = View.VISIBLE
                                viewHolder.itemView.inimage_deletetext.text = "You deleted the image..."


                            }

                            val currentimestamp  = System.currentTimeMillis() / 1000
                            val currenttimeinmin = currentimestamp /60
                            val currenttimeinhour = currenttimeinmin / 60
                            val currentimeinday  = currenttimeinhour /24

                            // message time

                            val messagetimeinmin = message.timestamp!! / 60
                            val messagetimeinhour = messagetimeinmin  / 60
                            val messagetimeinday =  messagetimeinhour /  24

                            val valuee  = 1



                            val timediff: Long = currentimeinday - messagetimeinday

                            if (timediff >= valuee){

                                viewHolder.itemView.inImage_datetextview.text =  currentmessage?.date
                                viewHolder.itemView.inImage_timetextview.text =  currentmessage?.time

                            }else{

                                viewHolder.itemView.inImage_timetextview.text =  currentmessage?.time

                            }

                            if (currentmessage?.seen == "true"){

                                Glide.with(context).load(R.drawable.seen_icon).into(viewHolder.itemView.inImage_seenicon)
                            }else{

                                Glide.with(context).load(R.drawable.notseen_icon).into(viewHolder.itemView.inImage_seenicon)
                            }






                        }
                        if (dc.type == DocumentChange.Type.MODIFIED){

                            currentmessage = dc.document.toObject(DeliverChat::class.java)




                            if (currentmessage?.deleted == true){

                                viewHolder.itemView.inImage_Imageview.visibility  =View.GONE
                                viewHolder.itemView.inimage_deletetext.visibility = View.VISIBLE
                                viewHolder.itemView.inimage_deletetext.text = "You deleted the image..."


                            }


                            val currentimestamp  = System.currentTimeMillis() / 1000
                            val currenttimeinmin = currentimestamp /60
                            val currenttimeinhour = currenttimeinmin / 60
                            val currentimeinday  = currenttimeinhour /24

                            // message time

                            val messagetimeinmin = message.timestamp!! / 60
                            val messagetimeinhour = messagetimeinmin  / 60
                            val messagetimeinday =  messagetimeinhour /  24

                            val value  = 1



                            val timediff: Long = currentimeinday - messagetimeinday

                            if (timediff >= value){

                                viewHolder.itemView.inImage_datetextview.text =  currentmessage?.date
                                viewHolder.itemView.inImage_timetextview.text =  currentmessage?.time

                            }else{

                                viewHolder.itemView.inImage_timetextview.text =  currentmessage?.time

                            }

                            if (currentmessage?.seen == "true"){

                                Glide.with(context).load(R.drawable.seen_icon).into(viewHolder.itemView.inImage_seenicon)
                            }else{

                                Glide.with(context).load(R.drawable.notseen_icon).into(viewHolder.itemView.inImage_seenicon)
                            }






                        }

                    }


                }


            })







            viewHolder.itemView.Imageincard.setOnClickListener {

                val intent = Intent(context,MessImagePreviewActivity::class.java)
                intent.putExtra(IMAGE_KEY,message.message)
                context.startActivity(intent)


            }



            //delete message


            viewHolder.itemView.chatlog_inImagecard.setOnLongClickListener {


                builder.setMessage("Do you want to unsend this Image")
                    .setCancelable(true)
                    .setPositiveButton("Yes"){dialogInterface , it  ->




                        Log.d(TAG,"messageid:${message.messageid}, outuser:${outser.uid}, inuser:${inuser.uid}")

                        val dbin = firestore.collection("Messages").document(inuser.uid.toString())
                            .collection(outuser?.uid.toString()).document(message.messageid.toString())
                        dbin.update("deleted",true).addOnSuccessListener {


                            val dbout = firestore.collection("Messages").document(outser.uid.toString()).collection(inuser.uid.toString())
                                .document(message.messageid.toString())
                            dbout.update("deleted",true)
                        }






                        val dblatestmess  = firestore.collection("LatestMessages")
                            .document(inuser.uid.toString())
                            .collection("messsages")

                        dblatestmess.addSnapshotListener(object : EventListener<QuerySnapshot>{
                            override fun onEvent(
                                value: QuerySnapshot?,
                                error: FirebaseFirestoreException?
                            ) {

                                if (error!=null){


                                    Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()

                                }

                                for (dc:DocumentChange in value?.documentChanges!!){


                                    if (dc.type ==  DocumentChange.Type.ADDED){

                                        recentmessage = dc.document.toObject(DeliverChat::class.java)

                                        if (recentmessage!!.messageid == message.messageid){

                                            val updaterecentmess  =  firestore.collection("LatestMessages").document(inuser.uid.toString())
                                                .collection("messsages").document(outser.uid.toString())

                                            updaterecentmess.update("message","message was deleted")


                                            val updaterecentmessout = firestore.collection("LatestMessages").document(outser.uid.toString())
                                                .collection("messsages").document(inuser.uid.toString())

                                            updaterecentmessout.update("message","message was deleted")

                                        }


                                    }

                                }
                            }


                        })





                    }
                    .setNegativeButton("No"){dialogInterface, it ->

                        dialogInterface.cancel()

                    }
                    .show()



                true
            }

        }

        override fun getLayout(): Int {

            return R.layout.inimagechat
        }




    }
    class OutImagerecy(val context: Context, val message: DeliverChat, val outser: User, val inuser: User):Item<ViewHolder>(){

        private lateinit var firestore: FirebaseFirestore



        companion object{
            var recentmessage:DeliverChat?=null

        }

        override fun bind(viewHolder: ViewHolder, position: Int) {

            firestore =  FirebaseFirestore.getInstance()
            Glide.with(context).load(message.message).into(viewHolder.itemView.outImage_Image_view)
            Glide.with(context).load(outser.imageuri).into(viewHolder.itemView.outImage_imageview)
            viewHolder.itemView.outImage_timetextview.text = message.time
            val seen = message.seen
            val messageid = message.messageid.toString()


            viewHolder.itemView.outImage_username.text = outser.firstname

            val currentimestamp  = System.currentTimeMillis() / 1000
            val currenttimeinmin = currentimestamp /60
            val currenttimeinhour = currenttimeinmin / 60
            val currentimeinday  = currenttimeinhour /24

            // message time

            val messagetimeinmin = message.timestamp!! / 60
            val messagetimeinhour = messagetimeinmin  / 60
            val messagetimeinday =  messagetimeinhour /  24

            val value  = 1



            val timediff: Long = currentimeinday - messagetimeinday

            if (timediff >= value){

                viewHolder.itemView.outImage_datetextviwe.text =  message.date
                viewHolder.itemView.outImage_timetextview.text =  message.time

            }else{

                viewHolder.itemView.outImage_timetextview.text =  message.time

            }

            viewHolder.itemView.Imageoutcard.setOnClickListener {
                val intent = Intent(context,MessImagePreviewActivity::class.java)
                intent.putExtra(IMAGE_KEY,message.message)
                context.startActivity(intent)


            }


            if (seen == "false"){


                val dbseen = firestore.collection("Messages").document(outser.uid.toString()).collection(inuser.uid.toString()).document(messageid)
                dbseen.update("seen","true")

                val latestseen =  firestore.collection("LatestMessages").document(outser.uid.toString())
                    .collection("messsages").document(inuser.uid.toString())
                latestseen.update("seen","true")

            }


//            if (message.deleted == true){
//
//                viewHolder.itemView.outImage_Image_view.visibility  =View.GONE
//                viewHolder.itemView.OutImage_deletetext.visibility = View.VISIBLE
//                viewHolder.itemView.OutImage_deletetext.text = "${outser.firstname} deleted the image..."
//
//            }




            val lmess = firestore.collection("Messages").document(inuser.uid.toString())
                .collection(outuser?.uid.toString()).whereEqualTo("messageid",message.messageid.toString())


            lmess.addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {


                    for (dc:DocumentChange in value?.documentChanges!!){

                        if (dc.type == DocumentChange.Type.ADDED) {

                            recentmessage = dc.document.toObject(DeliverChat::class.java)


                            if (recentmessage?.deleted == true){

                                viewHolder.itemView.outImage_Image_view.visibility  =View.GONE
                                viewHolder.itemView.OutImage_deletetext.visibility = View.VISIBLE
                                viewHolder.itemView.OutImage_deletetext.text = "${outser.firstname} deleted the image..."

                            }





                        }
                        if (dc.type == DocumentChange.Type.MODIFIED){

                           recentmessage = dc.document.toObject(DeliverChat::class.java)






                            if (recentmessage?.deleted ==false){

                                viewHolder.itemView.outImage_Image_view.visibility  =View.GONE
                                viewHolder.itemView.OutImage_deletetext.visibility = View.VISIBLE
                                viewHolder.itemView.OutImage_deletetext.text = "${outser.firstname} deleted the image..."

                            }
                        }
                    }
                }


            })


        }

        override fun getLayout(): Int {
            return R.layout.outimagechat
        }


    }




    class inchatshareprofile(val context: Context, val message: DeliverChat, val inuser: User, val  outser: User):Item<ViewHolder>(){

        private lateinit var builder:AlertDialog.Builder
        private lateinit var firestore: FirebaseFirestore

        companion object{

            var recentmessage: DeliverChat?=null
            var currentmessage:DeliverChat?=null

        }

        override fun bind(viewHolder: ViewHolder, position: Int) {

            firestore = FirebaseFirestore.getInstance()

            builder = AlertDialog.Builder(context)

            Glide.with(context).load(message.shareuser?.imageuri).into(viewHolder.itemView.inchat_shareprofileimageview)
            viewHolder.itemView.inchat_shareprofilename.text = "${message.shareuser?.firstname} ${message.shareuser?.lastname}"


            val lmess = firestore.collection("Messages").document(inuser.uid.toString())
                .collection(outuser?.uid.toString()).whereEqualTo("messageid",message.messageid.toString())

            lmess.addSnapshotListener(object :EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    if (error!=null){
                        Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()

                    }

                    for (dc:DocumentChange in value?.documentChanges!!){

                        if (dc.type  == DocumentChange.Type.ADDED){

                          currentmessage = dc.document.toObject(DeliverChat::class.java)



                            val currentimestamp  = System.currentTimeMillis() / 1000
                            val currenttimeinmin = currentimestamp /60
                            val currenttimeinhour = currenttimeinmin / 60
                            val currentimeinday  = currenttimeinhour /24

                            // message time

                            val messagetimeinmin = message.timestamp!! / 60
                            val messagetimeinhour = messagetimeinmin  / 60
                            val messagetimeinday =  messagetimeinhour /  24

                            val valuee  = 1



                            val timediff: Long = currentimeinday - messagetimeinday

                            if (timediff >= valuee){

                                viewHolder.itemView.inchatshareprofile_datetextview.text =  currentmessage?.date
                                viewHolder.itemView.inchatshareprofile_timetextview.text =  currentmessage?.time

                            }else{

                                viewHolder.itemView.inchatshareprofile_timetextview.text =  currentmessage?.time

                            }

                            if (currentmessage?.seen == "true"){

                                Glide.with(context).load(R.drawable.seen_icon).into(viewHolder.itemView.inchatshareprofile_seen)
                            }else{

                                Glide.with(context).load(R.drawable.notseen_icon).into(viewHolder.itemView.inchatshareprofile_seen)
                            }


                        }
                        if (dc.type == DocumentChange.Type.MODIFIED){

                            currentmessage = dc.document.toObject(DeliverChat::class.java)



                            val currentimestamp  = System.currentTimeMillis() / 1000
                            val currenttimeinmin = currentimestamp /60
                            val currenttimeinhour = currenttimeinmin / 60
                            val currentimeinday  = currenttimeinhour /24

                            // message time

                            val messagetimeinmin = message.timestamp!! / 60
                            val messagetimeinhour = messagetimeinmin  / 60
                            val messagetimeinday =  messagetimeinhour /  24

                            val value  = 1



                            val timediff: Long = currentimeinday - messagetimeinday

                            if (timediff >= value){

                                viewHolder.itemView.inchatshareprofile_datetextview.text =  currentmessage?.date
                                viewHolder.itemView.inchatshareprofile_timetextview.text =  currentmessage?.time

                            }else{

                                viewHolder.itemView.inchatshareprofile_timetextview.text =  currentmessage?.time

                            }

                            if (currentmessage?.seen == "true"){

                                Glide.with(context).load(R.drawable.seen_icon).into(viewHolder.itemView.inchatshareprofile_seen)
                            }else{

                                Glide.with(context).load(R.drawable.notseen_icon).into(viewHolder.itemView.inchatshareprofile_seen)
                            }



                        }

                    }


                }


            })







            viewHolder.itemView.inchatshareprofile_card.setOnClickListener {

                val intent = Intent(context,UserProfileActivity::class.java)
                intent.putExtra(SearchActivity.USERK,message.shareuser)
                context.startActivity(intent)


            }



            //delete message


            viewHolder.itemView.inchatshareprofile.setOnLongClickListener {


                builder.setMessage("Do you want to unsend this Image")
                    .setCancelable(true)
                    .setPositiveButton("Yes"){dialogInterface , it  ->




                        Log.d(TAG,"messageid:${message.messageid}, outuser:${outser.uid}, inuser:${inuser.uid}")

                        val dbin = firestore.collection("Messages").document(inuser.uid.toString())
                            .collection(outuser?.uid.toString()).document(message.messageid.toString())
                        dbin.delete()

                        val dbout = firestore.collection("Messages").document(outser.uid.toString()).collection(inuser.uid.toString())
                            .document(message.messageid.toString())
                        dbout.delete()




                        val dblatestmess  = firestore.collection("LatestMessages")
                            .document(inuser.uid.toString())
                            .collection("messsages")

                        dblatestmess.addSnapshotListener(object : EventListener<QuerySnapshot>{
                            override fun onEvent(
                                value: QuerySnapshot?,
                                error: FirebaseFirestoreException?
                            ) {

                                if (error!=null){


                                    Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()

                                }

                                for (dc:DocumentChange in value?.documentChanges!!){


                                    if (dc.type ==  DocumentChange.Type.ADDED){

                                        recentmessage = dc.document.toObject(DeliverChat::class.java)

                                        if (recentmessage!!.messageid == message.messageid){

                                            val updaterecentmess  =  firestore.collection("LatestMessages").document(inuser.uid.toString())
                                                .collection("messsages").document(outser.uid.toString())

                                            updaterecentmess.update("message","message was deleted")


                                            val updaterecentmessout = firestore.collection("LatestMessages").document(outser.uid.toString())
                                                .collection("messsages").document(inuser.uid.toString())

                                            updaterecentmessout.update("message","message was deleted")

                                        }


                                    }

                                }
                            }


                        })





                    }
                    .setNegativeButton("No"){dialogInterface, it ->

                        dialogInterface.cancel()

                    }
                    .show()



                true
            }

        }

        override fun getLayout(): Int {

            return R.layout.inchat_shareprofile
        }




    }


    class outchatshareprofilerecy(val context: Context, val message: DeliverChat, val outser: User, val inuser: User):Item<ViewHolder>(){
        private lateinit var firestore: FirebaseFirestore

        override fun bind(viewHolder: ViewHolder, position: Int) {

            firestore =  FirebaseFirestore.getInstance()
            viewHolder.itemView.outchatshareprofile_sharename.text = "${message.shareuser?.firstname} ${message.shareuser?.lastname}"
            Glide.with(context).load(message.shareuser?.imageuri).into(viewHolder.itemView.outchatshareprofile_shareimageview)
            Glide.with(context).load(outser.imageuri).into(viewHolder.itemView.outchatshareprofile_imageview)
            viewHolder.itemView.outchatshareprofile_timetextview.text = message.time
            val seen = message.seen
            val messageid = message.messageid.toString()


            viewHolder.itemView.outchatshareprofile_username.text = outser.firstname

            val currentimestamp  = System.currentTimeMillis() / 1000
            val currenttimeinmin = currentimestamp /60
            val currenttimeinhour = currenttimeinmin / 60
            val currentimeinday  = currenttimeinhour /24

            // message time

            val messagetimeinmin = message.timestamp!! / 60
            val messagetimeinhour = messagetimeinmin  / 60
            val messagetimeinday =  messagetimeinhour /  24

            val value  = 1



            val timediff: Long = currentimeinday - messagetimeinday

            if (timediff >= value){

                viewHolder.itemView.outchatprofile_datetextviwe.text =  message.date
                viewHolder.itemView.outchatshareprofile_timetextview.text =  message.time

            }else{

                viewHolder.itemView.outchatshareprofile_timetextview.text =  message.time

            }

            viewHolder.itemView.outchatshareprofilecard.setOnClickListener {
                val intent = Intent(context,UserProfileActivity::class.java)
                intent.putExtra(SearchActivity.USERK,message.shareuser)
                context.startActivity(intent)


            }


            if (seen == "false"){


                val dbseen = firestore.collection("Messages").document(outser.uid.toString()).collection(inuser.uid.toString()).document(messageid)
                dbseen.update("seen","true")

                val latestseen =  firestore.collection("LatestMessages").document(outser.uid.toString())
                    .collection("messsages").document(inuser.uid.toString())
                latestseen.update("seen","true")


            }





//            viewHolder.itemView.outchatshareprofile_card.setOnLongClickListener {
//
//
//
//            val dblikeadd = firestore.collection("Messages").document(outser.uid.toString()).collection(inuser.uid.toString())
//                .document(message.messageid.toString())
//
//            dblikeadd.update("like",true).addOnSuccessListener {
//                val dblikeinadd = firestore.collection("Messages").document(inuser.uid.toString())
//                    .collection(outser.uid.toString())
//                    .document(message.messageid.toString())
//
//                dblikeinadd.update("like", true)
//                    .addOnSuccessListener {
//
//
//                        val latestmessi = firestore.collection("LatestMessages")
//                            .document(inuser.uid.toString())
//                            .collection("messsages")
//                            .document(message.messageid.toString())
//
//                        latestmessi.update("like", true)
//
//
//                        val latestmess0 = firestore.collection("LatestMessages")
//                            .document(inuser.uid.toString())
//                            .collection("messsages")
//                            .document(message.messageid.toString())
//
//                        latestmess0.update("like", true)
//
//
//                    }
//
//
//            }
//
//           true
//
//
//
//            }










        }

        override fun getLayout(): Int {
            return R.layout.outchat_shareprofile
        }


    }


    override fun onPause() {
        super.onPause()

        val currentuseid = FirebaseAuth.getInstance().uid.toString()

        val  userstatus = firestore.collection("UserInfo").document(currentuseid)
        userstatus.update("status","Offline")


        val lastseen  = firestore.collection("UserInfo").document(currentuseid)
        lastseen.update("timestamp",System.currentTimeMillis())




    }


    override fun onResume() {
        super.onResume()

        val currentuseid = FirebaseAuth.getInstance().uid.toString()

        val  userstatus = firestore.collection("UserInfo").document(currentuseid)
        userstatus.update("status","Online")



    }
}


