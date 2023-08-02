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
import com.youngminds.upmarkx.*
import com.youngminds.upmarkx.Models.Answer
import com.youngminds.upmarkx.Models.Comments
import com.youngminds.upmarkx.Models.Question
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.R
import com.youngminds.upmarkx.Utils.TimeStamp
import kotlinx.android.synthetic.main.communitychild_commentslayout.view.*
import kotlinx.android.synthetic.main.communitychild_report_bottomsheet.view.*
import kotlinx.android.synthetic.main.communitychildcard.view.*
import kotlinx.android.synthetic.main.communitychild_thaknslayout.view.*
import java.util.*

class Communitychildrecy(val context: Context, val  answer: Answer, val question: Question):Item<ViewHolder>() {


    private lateinit var firestore: FirebaseFirestore


    companion object{


        val adaptar = GroupAdapter<ViewHolder>()
        val commentadaptar = GroupAdapter<ViewHolder>()

        var user: User?=null
        var currentuser: User?=null
        val MORE_ANS = "MORE_ANS"
        val MORE_QUESTION = "MORE_QUESTION"
        val EDIT_ANSWER ="EDIT_ANSWER"
        var report:CommunityReport?=null
        var option:String?=null

    }

    override fun bind(viewHolder: ViewHolder, position: Int) {


        firestore  = FirebaseFirestore.getInstance()
        viewHolder.itemView.communitychild_answer.text =  answer.answer
        Glide.with(context).load(answer.answerimage).into( viewHolder.itemView.communitychild_imageview)


        viewHolder.itemView.communitychild_date.text = TimeStamp.getTimeAgo(answer.date!!)





        if (answer.edited  ==  "True"){

            viewHolder.itemView.communitychild_answertextview.text = "Answer (Edited)"
        }else{

            viewHolder.itemView.communitychild_answertextview.setText("Answer")

        }







        viewHolder.itemView.communitychild_option.setOnClickListener {


            val popmenu =  PopupMenu(context.applicationContext,it)

            popmenu.inflate(R.menu.nav_communitychild)
            popmenu.setOnMenuItemClickListener {

                when(it.itemId){

                    R.id.copy ->{

                        val clipboardmanager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clipData = ClipData.newPlainText("text",answer.answer)
                        clipboardmanager.setPrimaryClip(clipData)



                        Toast.makeText(context,"Text copied to Clipboard",Toast.LENGTH_SHORT).show()

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








        viewHolder.itemView.communitychild_edit.setOnClickListener {

            val  intent = Intent(context,EditAnswerActivity::class.java)
            intent.putExtra(EDIT_ANSWER,answer)
            context.startActivity(intent)


        }







        val currentueruid = FirebaseAuth.getInstance().uid.toString()

        if (currentueruid == answer.userid){

            viewHolder.itemView.communitychild_edit.visibility =View.VISIBLE

        }else{
            viewHolder.itemView.communitychild_edit.visibility =View.GONE
        }


        val dbuser = firestore.collection("UserInfo").whereEqualTo("uid",answer.userid)
        dbuser.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                   Toast.makeText(context,"error is coming while fetching the answers",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){


                        user = dc.document.toObject(User::class.java)


                        if (user?.verified =="true"){

                            viewHolder.itemView.communitychild_verified.visibility = View.VISIBLE
                        }else{
                            viewHolder.itemView.communitychild_verified.visibility = View.GONE

                        }


                        if (currentueruid == answer.userid){

                            viewHolder.itemView.communitychild_usernmae.text = "${user?.firstname} ${user?.lastname} (You)"

                        }else{

                            viewHolder.itemView.communitychild_usernmae.text = "${user?.firstname} ${user?.lastname}"
                        }

                        Glide.with(context).load(user?.imageuri).into(viewHolder.itemView.communitychild_userimage)

                        viewHolder.itemView.communitychild_class.setText(user?.standard)


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

        likestatus.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){


                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()
                }

                for (dc:DocumentChange in  value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){

                        viewHolder.itemView.communitychild_likedbtn.visibility = View.VISIBLE
                        viewHolder.itemView.communitychild_likebtn.visibility = View.GONE

                    }else{
                        viewHolder.itemView.communitychild_likedbtn.visibility = View.GONE
                        viewHolder.itemView.communitychild_likebtn.visibility = View.VISIBLE

                    }

                }


            }


        })






        val useruid = FirebaseAuth.getInstance().uid.toString()
        val currentuserdb = firestore.collection("UserInfo").whereEqualTo("uid",useruid)
        currentuserdb.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(context,"error is coming while fetching the answers",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){


                        currentuser = dc.document.toObject(User::class.java)




                    }

                }

            }


        })


        val likecountdba = firestore.collection("CommunityQuestions")
            .document(answer.questionid.toString())
            .collection("Answers")
            .document(answer.answerid.toString())
            .collection("Helped")

        likecountdba.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                var likecount = 0
                if (error!=null){


                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){



                        likecount++

                    }
                }


                viewHolder.itemView.communitychild_likecount.text = "$likecount Thanks"
            }


        })




        val dbreport = firestore.collection("UserInfo")
            .document(currentueruid)
            .collection("CommunityReported")
            .whereEqualTo("answerid",answer.answerid)

        dbreport.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){

                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()

                }

                for (dc:DocumentChange in value?.documentChanges!!){


                    if (dc.type == DocumentChange.Type.ADDED){

                        viewHolder.itemView.communitychild_report.visibility  = View.GONE
                        viewHolder.itemView.communitychild_reported.visibility = View.VISIBLE

                    }else{

                        viewHolder.itemView.communitychild_report.visibility  = View.VISIBLE
                        viewHolder.itemView.communitychild_reported.visibility = View.GONE


                    }

                }


            }


        })















        val currentuseruid =  FirebaseAuth.getInstance().uid.toString()

        viewHolder.itemView.communitychild_likebtn.setOnClickListener {





            val randomid = UUID.randomUUID()


            val dblike = firestore.collection("CommunityQuestions")
                .document(answer.questionid.toString())
                .collection("Answers")
                .document(answer.answerid.toString())
                .collection("Helped")
                .document(currentuseruid)



            dblike.set(currentuser!!).addOnSuccessListener {
                val db = firestore.collection("UserInfo").document(answer.userid.toString())
                    .collection("Likes").document(randomid.toString())


                db.set(currentuser!!)
            }





//            val dbleaderboard = firestore.collection("Leaderboard_Admin")
//                .document(useruid)
//
//            dbleaderboard.set(currentuser!!)







            val likestatusc = firestore.collection("CommunityQuestions")
                .document(answer.questionid.toString())
                .collection("Answers")
                .document(answer.answerid.toString())
                .collection("Helped")
                .whereEqualTo("uid",uid)

            likestatusc.addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    if (error!=null){


                        Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()
                    }

                    for (dc:DocumentChange in  value?.documentChanges!!){

                        if (dc.type == DocumentChange.Type.ADDED){

                            viewHolder.itemView.communitychild_likedbtn.visibility = View.VISIBLE
                            viewHolder.itemView.communitychild_likebtn.visibility = View.GONE

                        }else{
                            viewHolder.itemView.communitychild_likedbtn.visibility = View.GONE
                            viewHolder.itemView.communitychild_likebtn.visibility = View.VISIBLE

                    }


                }}


            })


            val likecountdb = firestore.collection("CommunityQuestions")
                .document(answer.questionid.toString())
                .collection("Answers")
                .document(answer.answerid.toString())
                .collection("Helped")

            likecountdb.addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    var likecount = 0
                    if (error!=null){


                        Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()

                    }

                    for (dc:DocumentChange in value?.documentChanges!!){

                        if (dc.type == DocumentChange.Type.ADDED){

                            likecount++

                        }
                    }


                    viewHolder.itemView.communitychild_likecount.text = "$likecount Thanks"
                }


            })


        }

        viewHolder.itemView.communitychild_showallans.setOnClickListener {

            val intent = Intent(context, MoreanswersActivity::class.java)
//            intent.putExtra(MORE_ANS,answer)
            intent.putExtra(MORE_QUESTION,question.questionid)
            context.startActivity(intent)


        }


        val savedbc  = firestore.collection("UserInfo")
            .document(currentueruid)
            .collection("SavedAnswers")
            .whereEqualTo("answerid",answer.answerid)

        savedbc.addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){

                    Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()

                }
                for (dc:DocumentChange in value?.documentChanges!!){

                    if (dc.type  == DocumentChange.Type.ADDED){

                        viewHolder.itemView.communitychild_save.visibility = View.GONE
                        viewHolder.itemView.communitychild_saved.visibility = View.VISIBLE

                    }

                }



            }


        })




        viewHolder.itemView.communitychild_save.setOnClickListener {

            val savedb  = firestore.collection("UserInfo")
                .document(currentueruid)
                .collection("SavedAnswers")
                .document(answer.answerid.toString())

            savedb.set(answer).addOnSuccessListener {

                Toast.makeText(context,"Answer Saved",Toast.LENGTH_SHORT).show()

                val savedbci  = firestore.collection("UserInfo")
                    .document(currentueruid)
                    .collection("SavedAnswers")
                    .whereEqualTo("answerid",answer.answerid)

                savedbci.addSnapshotListener(object :EventListener<QuerySnapshot>{
                    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                        if (error!=null){

                            Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()

                        }
                        for (dc:DocumentChange in value?.documentChanges!!){

                            if (dc.type  == DocumentChange.Type.ADDED) {

                                viewHolder.itemView.communitychild_save.visibility = View.GONE
                                viewHolder.itemView.communitychild_saved.visibility = View.VISIBLE

                            }else{
                                viewHolder.itemView.communitychild_save.visibility = View.VISIBLE
                                viewHolder.itemView.communitychild_saved.visibility = View.GONE


                            }
                            if(dc.type == DocumentChange.Type.REMOVED){

                                viewHolder.itemView.communitychild_save.visibility = View.VISIBLE
                                viewHolder.itemView.communitychild_saved.visibility = View.GONE
                            }

                        }



                    }


                })

            }


        }

        viewHolder.itemView.communitychild_saved.setOnClickListener {





            val unsaveddb  = firestore.collection("UserInfo")
                .document(currentueruid)
                .collection("SavedAnswers")
                .document(answer.answerid.toString())

            unsaveddb.delete().addOnSuccessListener {

                Toast.makeText(context,"Answer removed from Saved",Toast.LENGTH_SHORT).show()

                val savedbcd  = firestore.collection("UserInfo")
                    .document(currentueruid)
                    .collection("SavedAnswers")
                    .whereEqualTo("answerid",answer.answerid)

                savedbcd.addSnapshotListener(object :EventListener<QuerySnapshot>{
                    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                        if (error!=null){

                            Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()

                        }
                        for (dc:DocumentChange in value?.documentChanges!!){

                            if (dc.type  == DocumentChange.Type.ADDED) {

                                viewHolder.itemView.communitychild_save.visibility = View.GONE
                                viewHolder.itemView.communitychild_saved.visibility = View.VISIBLE

                            }else{


                                viewHolder.itemView.communitychild_save.visibility = View.VISIBLE
                                viewHolder.itemView.communitychild_saved.visibility = View.GONE
                            }
                            if (dc.type  == DocumentChange.Type.REMOVED){

                                viewHolder.itemView.communitychild_save.visibility = View.VISIBLE
                                viewHolder.itemView.communitychild_saved.visibility = View.GONE

                            }

                        }



                    }


                })

            }

        }





        viewHolder.itemView.communitychild_report.setOnClickListener {

      val bottomSheetDialog = BottomSheetDialog(context,R.style.BottomSheetDialogTheme_communitychild)
            val bottomSheeetView = LayoutInflater.from(context).inflate(R.layout.communitychild_report_bottomsheet,null) as LinearLayout?



            bottomSheeetView?.communitychild_bottomsheet_radiogroup?.setOnCheckedChangeListener{ radioGroup  , i ->

                option = i.toString()


                if (option!=null){


                    bottomSheeetView.communitychild_bottomsheet_sendbtn?.visibility = View.VISIBLE
                }else{
                    bottomSheeetView.communitychild_bottomsheet_sendbtn?.visibility = View.GONE
                }


            }




            bottomSheeetView?.communitychild_bottomsheet_sendbtn?.setOnClickListener {

                val id =  UUID.randomUUID().toString()

                report  = CommunityReport(id,currentueruid,answer.userid,answer.answerid,question.questionid,question.question,answer.answer,System.currentTimeMillis(),
                    option)

                val  dbreporti  = firestore.collection("UserInfo")
                    .document(answer.userid.toString())
                    .collection("CommunityReports")
                    .document(id)

                dbreporti.set(report!!).addOnSuccessListener {

                    val dbreportu = firestore.collection("UserInfo")
                        .document(currentueruid)
                        .collection("CommunityReported")
                        .document(id)

                    dbreportu.set(report!!).addOnSuccessListener {
                        bottomSheetDialog.dismiss()}

                }

            }

            bottomSheetDialog.setContentView(bottomSheeetView!!)
            bottomSheetDialog.show()

            }





        viewHolder.itemView.communitychild_likecount.setOnClickListener {


            adaptar.clear()


            val dialog = BottomSheetDialog(context)
            val view = LayoutInflater.from(context).inflate(R.layout.communitychild_thaknslayout,null)



            view.communitychild_bottomthanks_recyclerview.adapter = adaptar




            adaptar.setOnItemClickListener { item, view ->

                val user = item as discoverprecy

                val intent =Intent(context,UserProfileActivity::class.java)
                intent.putExtra(SearchActivity.USERK,user.user)
                context.startActivity(intent)

            }



            val db = firestore.collection("CommunityQuestions")
                .document(question.questionid.toString())
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






        viewHolder.itemView.communitychild_comment.setOnClickListener {


            commentadaptar.clear()




            val dialog = BottomSheetDialog(context)
            val view = LayoutInflater.from(context).inflate(R.layout.communitychild_commentslayout,null)



            view.communitychild_comment_backbtn.setOnClickListener {

                dialog.dismiss()



            }


            view.communitychild_comment_share.setOnClickListener {

                val shareintent = Intent(Intent.ACTION_SEND)
                shareintent.setType("text/plane")
                shareintent.putExtra(Intent.EXTRA_TEXT,answer.answer)
                context.startActivity(shareintent)

            }



            view.communitychild_commentrecyclerview.adapter = commentadaptar


            Glide.with(context).load(currentuser?.imageuri).into(view.communitychild_comment_imageview)


            val dbcommentview = firestore.collection("CommunityQuestions")
                .document(question.questionid.toString())
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

                        commentadaptar.add(communitychild_commentsrecy(context,comments))


                    }


                }


            }












            view.communitychild_comment_postbtn.setOnClickListener {


               val comment =  view.communitychild_comment_text.text.trim().toString()

                if (view.communitychild_comment_text.text.isEmpty()){

                    Toast.makeText(context,"Add a comment...",Toast.LENGTH_SHORT).show()
                   return@setOnClickListener

                }else{



                    val time = System.currentTimeMillis()
                    val commentid = UUID.randomUUID().toString()

                    val comments = Comments(commentid,comment,currentueruid,question.questionid,answer.answerid,question.question,answer.answer,time)

                    val dbcomment = firestore.collection("CommunityQuestions")
                        .document(question.questionid.toString())
                        .collection("Answers")
                        .document(answer.answerid.toString())
                        .collection("Comments")
                        .document(commentid)


                    dbcomment.set(comments).addOnSuccessListener {

                        view.communitychild_comment_text.text.clear()

                       Toast.makeText(context,"Comment Posted",Toast.LENGTH_SHORT).show()

                    }






                }












            }









            dialog.setContentView(view!!)
            dialog.show()



        }








    }



    override fun getLayout(): Int {

        return R.layout.communitychildcard

    }
}