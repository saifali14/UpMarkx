package com.youngminds.upmarkx.recyclerview

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.youngminds.upmarkx.CommunityReport
import com.youngminds.upmarkx.EditAnswerActivity
import com.youngminds.upmarkx.Models.Answer
import com.youngminds.upmarkx.Models.Comments
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.R
import com.youngminds.upmarkx.Utils.TimeStamp
import kotlinx.android.synthetic.main.communitychild_report_bottomsheet.view.*
import kotlinx.android.synthetic.main.communitychild_thaknslayout.view.*
import kotlinx.android.synthetic.main.moreans_commentlayout.view.*
import kotlinx.android.synthetic.main.moreanscard.view.*
import java.util.*

class savedrecy(val context: Context, val answer: Answer):Item<ViewHolder>() {


    private lateinit var firestore: FirebaseFirestore

    companion object{

        var user: User?=null
        var report:CommunityReport?=null
        var option:String?=null
        var currentuser:User?=null
        val commentadaptar = GroupAdapter<ViewHolder>()
        val adaptar = GroupAdapter<ViewHolder>()

    }


    override fun bind(viewHolder: ViewHolder, position: Int) {


        firestore = FirebaseFirestore.getInstance()


        viewHolder.itemView.moreanscard_answer.text = answer.answer
        Glide.with(context).load(answer.answerimage).into(viewHolder.itemView.moreanscard_imageview)
        viewHolder.itemView.moreanscard_date.text = TimeStamp.getTimeAgo(answer.date!!)







        val currentuseruid = FirebaseAuth.getInstance().uid.toString()


        val dbcurrentuser = firestore.collection("UserInfo").whereEqualTo("uid",currentuseruid)

        dbcurrentuser.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(context,"Something went wrong", Toast.LENGTH_SHORT).show()

                }

                for (dc: DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){


                        Moreansrecy.currentuser = dc.document.toObject(User::class.java)



                    }


                }

            }


        })


        val  currentuid = FirebaseAuth.getInstance().uid.toString()

        if (currentuid == answer.userid){
            viewHolder.itemView.moreans_edit.visibility = View.VISIBLE

        }else{


            viewHolder.itemView.moreans_edit.visibility = View.GONE
        }





        viewHolder.itemView.moreans_edit.setOnClickListener {

            val  intent = Intent(context, EditAnswerActivity::class.java)
            intent.putExtra(Communitychildrecy.EDIT_ANSWER,answer)
            context.startActivity(intent)




        }



        viewHolder.itemView.moreans_option.setOnClickListener {


            val popmenu =  PopupMenu(context.applicationContext,it)

            popmenu.inflate(R.menu.nav_communitychild)
            popmenu.setOnMenuItemClickListener {

                when(it.itemId){

                    R.id.copy ->{

                        val clipboardmanager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clipData = ClipData.newPlainText("text",answer.answer)
                        clipboardmanager.setPrimaryClip(clipData)



                        Toast.makeText(context,"Text copied to Clipboard", Toast.LENGTH_SHORT).show()

                        true

                    }

                    R.id.sharetext ->{

                        val shareintent = Intent(Intent.ACTION_SEND)
                        shareintent.setType("text/plane")
                        shareintent.putExtra(Intent.EXTRA_TEXT,answer.answer)
                        context.startActivity(shareintent)

                        true
                    }



                    else -> true


                }

            }
            popmenu.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popmenu)
            menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                .invoke(menu,true)


        }



        val currentueruid = FirebaseAuth.getInstance().uid.toString()

        if (currentueruid == answer.userid){

            viewHolder.itemView.moreans_edit.visibility = View.VISIBLE

        }else{
            viewHolder.itemView.moreans_edit.visibility = View.GONE
        }







        val dbuser = firestore.collection("UserInfo").whereEqualTo("uid",answer.userid)
        dbuser.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(context,"something went wrong", Toast.LENGTH_SHORT).show()


                }

                for (dc: DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){

                        user = dc.document.toObject(User::class.java)

                        Glide.with(context).load(user?.imageuri).into(viewHolder.itemView.moreanscard_userimage)

                        if (user?.verified =="true"){

                            viewHolder.itemView.moreans_verified.visibility = View.VISIBLE
                        }else{
                            viewHolder.itemView.moreans_verified.visibility = View.GONE

                        }

                        if (currentuid == answer.userid){

                            viewHolder.itemView.moreanscard_usernmae.text = "${user?.firstname} ${user?.lastname} (You)"

                        }else{
                            viewHolder.itemView.moreanscard_usernmae.text = "${user?.firstname} ${user?.lastname}"

                        }

                        viewHolder.itemView.moreans_class.setText(user?.standard)



                    }

                }



            }


        })





        val dbreport = firestore.collection("UserInfo")
            .document(currentueruid)
            .collection("CommunityReported")
            .whereEqualTo("answerid",answer.answerid)

        dbreport.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(context,"Something went wrong", Toast.LENGTH_SHORT).show()

                }

                for (dc: DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){

                        viewHolder.itemView.moreans_report.visibility  = View.GONE
                        viewHolder.itemView.moreans_reported.visibility = View.VISIBLE

                    }else{

                        viewHolder.itemView.moreans_report.visibility  = View.VISIBLE
                        viewHolder.itemView.moreans_reported.visibility = View.GONE


                    }

                }


            }


        })



        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()


        val likestatus = firestore.collection("CommunityQuestions")
            .document(answer.questionid.toString())
            .collection("Answers")
            .document(answer.answerid.toString())
            .collection("Helped")
            .whereEqualTo("uid",uid)

        likestatus.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){


                    Toast.makeText(context,"Something went wrong", Toast.LENGTH_SHORT).show()
                }

                for (dc: DocumentChange in  value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        viewHolder.itemView.moreans_likedbtn.visibility = View.VISIBLE
                        viewHolder.itemView.moreanscard_likebtn.visibility = View.GONE

                    }else{
                        viewHolder.itemView.moreans_likedbtn.visibility = View.GONE
                        viewHolder.itemView.moreanscard_likebtn.visibility = View.VISIBLE

                    }

                }


            }


        })



        val likecountdba = firestore.collection("CommunityQuestions")
            .document(answer.questionid.toString())
            .collection("Answers")
            .document(answer.answerid.toString())
            .collection("Helped")

        likecountdba.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                var likecount = 0
                if (error!=null){


                    Toast.makeText(context,"Something went wrong", Toast.LENGTH_SHORT).show()

                }

                for (dc: DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){



                        likecount++

                    }
                }


                viewHolder.itemView.moreanscard_likecount.text = "$likecount Thanks"
            }


        })


        val savedbc  = firestore.collection("UserInfo")
            .document(currentueruid)
            .collection("SavedAnswers")
            .whereEqualTo("answerid",answer.answerid)

        savedbc.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(context,"Something went wrong", Toast.LENGTH_SHORT).show()

                }
                for (dc: DocumentChange in value?.documentChanges!!){

                    if (dc.type  == DocumentChange.Type.ADDED){

                        viewHolder.itemView.moreans_save.visibility = View.GONE
                        viewHolder.itemView.moreans_saved.visibility = View.VISIBLE

                    }

                }



            }


        })







        viewHolder.itemView.moreanscard_likebtn.setOnClickListener {


            val dblike = firestore.collection("CommunityQuestions")
                .document(answer.questionid.toString())
                .collection("Answers")
                .document(answer.answerid.toString())
                .collection("Helped")
                .document(currentuseruid)



            currentuser?.let { it1 ->
                dblike.set(it1).addOnSuccessListener {

                    val db = firestore.collection("UserInfo").document(answer.userid.toString())
                        .collection("Likes").document(currentuseruid)


                    db.set(currentuser!!)

                }
            }





            val likestatusi = firestore.collection("CommunityQuestions")
                .document(answer.questionid.toString())
                .collection("Answers")
                .document(answer.answerid.toString())
                .collection("Helped")
                .whereEqualTo("uid", uid)

            likestatusi.addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    if (error != null) {


                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {

                        if (dc.type == DocumentChange.Type.ADDED) {
                            viewHolder.itemView.moreans_likedbtn.visibility = View.VISIBLE
                            viewHolder.itemView.moreanscard_likebtn.visibility = View.GONE

                        } else {
                            viewHolder.itemView.moreans_likedbtn.visibility = View.GONE
                            viewHolder.itemView.moreanscard_likebtn.visibility = View.VISIBLE

                        }

                    }


                }


            })

            val likecountdb = firestore.collection("CommunityQuestions")
                .document(answer.questionid.toString())
                .collection("Answers")
                .document(answer.answerid.toString())
                .collection("Helped")

            likecountdb.addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    var likecount = 0
                    if (error != null) {


                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()

                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {

                        if (dc.type == DocumentChange.Type.ADDED) {


                            likecount++

                        }
                    }


                    viewHolder.itemView.moreanscard_likecount.text = "$likecount Thanks"
                }


            })


        }


        viewHolder.itemView.moreans_save.setOnClickListener {


            val savedb  = firestore.collection("UserInfo")
                .document(currentueruid)
                .collection("SavedAnswers")
                .document(answer.answerid.toString())

            savedb.set(answer).addOnSuccessListener {

                Toast.makeText(context,"Answer Saved", Toast.LENGTH_SHORT).show()

                val savedbci  = firestore.collection("UserInfo")
                    .document(currentueruid)
                    .collection("SavedAnswers")
                    .whereEqualTo("answerid",answer.answerid)

                savedbci.addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                        if (error!=null){

                            Toast.makeText(context,"Something went wrong", Toast.LENGTH_SHORT).show()

                        }
                        for (dc: DocumentChange in value?.documentChanges!!){

                            if (dc.type  == DocumentChange.Type.ADDED) {

                                viewHolder.itemView.moreans_save.visibility = View.GONE
                                viewHolder.itemView.moreans_saved.visibility = View.VISIBLE

                            }else{
                                viewHolder.itemView.moreans_save.visibility = View.VISIBLE
                                viewHolder.itemView.moreans_saved.visibility = View.GONE


                            }
                            if(dc.type == DocumentChange.Type.REMOVED){

                                viewHolder.itemView.moreans_save.visibility = View.VISIBLE
                                viewHolder.itemView.moreans_saved.visibility = View.GONE
                            }

                        }



                    }


                })

            }

        }



        viewHolder.itemView.moreans_saved.setOnClickListener{





            val unsaveddb  = firestore.collection("UserInfo")
                .document(currentueruid)
                .collection("SavedAnswers")
                .document(answer.answerid.toString())

            unsaveddb.delete().addOnSuccessListener {

                Toast.makeText(context,"Answer removed from Saved", Toast.LENGTH_SHORT).show()

                val savedbcd  = firestore.collection("UserInfo")
                    .document(currentueruid)
                    .collection("SavedAnswers")
                    .whereEqualTo("answerid",answer.answerid)

                savedbcd.addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                        if (error!=null){

                            Toast.makeText(context,"Something went wrong", Toast.LENGTH_SHORT).show()

                        }
                        for (dc: DocumentChange in value?.documentChanges!!){

                            if (dc.type  == DocumentChange.Type.ADDED) {

                                viewHolder.itemView.moreans_save.visibility = View.GONE
                                viewHolder.itemView.moreans_saved.visibility = View.VISIBLE

                            }else{


                                viewHolder.itemView.moreans_save.visibility = View.VISIBLE
                                viewHolder.itemView.moreans_saved.visibility = View.GONE
                            }
                            if (dc.type  == DocumentChange.Type.REMOVED){

                                viewHolder.itemView.moreans_save.visibility = View.VISIBLE
                                viewHolder.itemView.moreans_saved.visibility = View.GONE

                            }

                        }



                    }


                })

            }

        }



        viewHolder.itemView.moreans_report.setOnClickListener {

            val bottomSheetDialog = BottomSheetDialog(context,R.style.BottomSheetDialogTheme_communitychild)
            val bottomSheeetView = LayoutInflater.from(context).inflate(R.layout.communitychild_report_bottomsheet,null) as LinearLayout?



            bottomSheeetView?.communitychild_bottomsheet_radiogroup?.setOnCheckedChangeListener{ radioGroup  , i ->

                Communitychildrecy.option = i.toString()


                if (Communitychildrecy.option !=null){


                    bottomSheeetView.communitychild_bottomsheet_sendbtn?.visibility = View.VISIBLE
                }else{
                    bottomSheeetView.communitychild_bottomsheet_sendbtn?.visibility = View.GONE
                }


            }




            bottomSheeetView?.communitychild_bottomsheet_sendbtn?.setOnClickListener {

                val id =  UUID.randomUUID().toString()

                Moreansrecy.report = CommunityReport(id,currentueruid,answer.userid,answer.answerid,answer.questionid,answer.question,answer.answer,System.currentTimeMillis(),
                    Moreansrecy.option
                )

                val  dbreporti  = firestore.collection("UserInfo")
                    .document(answer.userid.toString())
                    .collection("CommunityReports")
                    .document(id)

                dbreporti.set(Moreansrecy.report!!).addOnSuccessListener {

                    val dbreportu = firestore.collection("UserInfo")
                        .document(currentueruid)
                        .collection("CommunityReported")
                        .document(id)

                    dbreportu.set(Moreansrecy.report!!).addOnSuccessListener {
                        bottomSheetDialog.dismiss()}

                }

            }

            bottomSheetDialog.setContentView(bottomSheeetView!!)
            bottomSheetDialog.show()

        }

        viewHolder.itemView.moreanscard_comment.setOnClickListener {

           commentadaptar.clear()


            val dialog = BottomSheetDialog(context)
            val view = LayoutInflater.from(context).inflate(R.layout.moreans_commentlayout,null)



            view.moreans_comment_backbtn.setOnClickListener {

                dialog.dismiss()



            }



            view.moreans_commentrecyclerview.adapter = commentadaptar


            Glide.with(context).load(Moreansrecy.currentuser?.imageuri).into(view.moreans_comment_imageview)


            val dbcommentview = firestore.collection("CommunityQuestions")
                .document(answer.questionid.toString())
                .collection("Answers")
                .document(answer.answerid.toString())
                .collection("Comments")


            dbcommentview.addSnapshotListener { value, error ->
                if (error!=null){

                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()
                }


                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){


                        val comments = dc.document.toObject(Comments::class.java)

                       commentadaptar.add(moreans_commentsrecy(context,comments))


                    }


                }


            }




            view.moreans_comment_share.setOnClickListener {

                val shareintent = Intent(Intent.ACTION_SEND)
                shareintent.setType("text/plane")
                shareintent.putExtra(Intent.EXTRA_TEXT,answer.answer)
                context.startActivity(shareintent)

            }











            view.moreans_comment_postbtn.setOnClickListener {



                val comment =  view.moreans_comment_text.text.trim().toString()

                if (view.moreans_comment_text.text.isEmpty()){

                    Toast.makeText(context,"Add a comment...",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener

                }else{



                    val time = System.currentTimeMillis()
                    val commentid = UUID.randomUUID().toString()

                    val comments = Comments(commentid,comment,currentueruid,answer.questionid,answer.answerid,answer.question,answer.answer,time)

                    val dbcomment = firestore.collection("CommunityQuestions")
                        .document(answer.questionid.toString())
                        .collection("Answers")
                        .document(answer.answerid.toString())
                        .collection("Comments")
                        .document(commentid)


                    dbcomment.set(comments).addOnSuccessListener {

                        view.moreans_comment_text.text.clear()

                        Toast.makeText(context,"Comment Posted",Toast.LENGTH_SHORT).show()

                    }






                }












            }









            dialog.setContentView(view!!)
            dialog.show()



        }



        viewHolder.itemView.moreanscard_likecount.setOnClickListener {


            adaptar.clear()


            val dialog = BottomSheetDialog(context)
            val view = LayoutInflater.from(context).inflate(R.layout.communitychild_thaknslayout,null)



            view.communitychild_bottomthanks_recyclerview.adapter = adaptar






            val db = firestore.collection("CommunityQuestions")
                .document(answer.questionid.toString())
                .collection("Answers")
                .document(answer.answerid.toString())
                .collection("Helped")


            db.addSnapshotListener { value, error ->


                if (error!=null){

                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()

                }


                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){


                        val users = dc.document.toObject(User::class.java)


                      adaptar.add(discoverprecy(context,users))




                    }

                }


            }







            dialog.setContentView(view!!)
            dialog.show()


        }






    }

    override fun getLayout(): Int {



        return R.layout.moreanscard
    }
}