package com.youngminds.upmarkx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.youngminds.upmarkx.Models.Chapters
import com.youngminds.upmarkx.Models.News
import com.youngminds.upmarkx.Models.Pdfs
import com.youngminds.upmarkx.Models.Videos
import java.util.*

class UploadmaterialActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore



    companion object{



        var viedos = arrayListOf<Videos>()
        var newscard = arrayListOf<News>()



    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uploadmaterial)



        firestore = FirebaseFirestore.getInstance()







//        news()




        settingmaterial()



//
//        chapters.addAll(0, listOf(Chapters("RealNumbers","Real Numbers", "1","0","4")))
//        chapters.addAll(1, listOf(Chapters("Poly","Polu Numbers", "2","0","4")))












        viedos.add(0,
            Videos("https://firebasestorage.googleapis.com/v0/b/upmark-a72c4.appspot.com/o/ChaptersDocuments%2Fvideosthumbnail%2FClass10Maths%2Fvd10%20realnumbers.png?alt=media&token=f9e1a0a3-45be-45fa-b250-62e86efa0466"
            ,"Real Numbers L-1 | Euclid's Division Lemma | CBSE Class 10 Maths Chapter 1 | Umang 2021 | Vedantu"
            ,"6Ei_Km-KWyU&ab_channel=Vedantu9%2610"))




        viedos.add(1,
            Videos("https://firebasestorage.googleapis.com/v0/b/upmark-a72c4.appspot.com/o/ChaptersDocuments%2Fvideosthumbnail%2FClass10Maths%2Fdr10%20realnumbers.png?alt=media&token=4fdfda81-d852-457a-9abe-8181f88af657",
                "2021-22 Real Numbers | Class 10 Maths Chapter 1 | Full Chapter | Number System | Rational Numbers",
                "7v4YPaBtoPA&ab_channel=DearSir")
        )

        viedos.add(2,
            Videos("https://firebasestorage.googleapis.com/v0/b/upmark-a72c4.appspot.com/o/ChaptersDocuments%2Fvideosthumbnail%2FClass10Maths%2Fpd10%20realnumbers.png?alt=media&token=0e2f5b2e-a498-4ae8-89c3-ae0a831a68ae",
                "Real Number Class 10 Term 1 One Shot | Class 10 Maths | NCERT Covered",
                "Qjeih7dXmQ8&ab_channel=PadhleTenthies")
        )


//
//        for ( i in viedos){
//
//
//
//            val db = firestore.collection("Class10")
//                .document("Maths")
//                .collection("Chapters")
//                .document("RealNumbers")
//                .collection("Videos")
//                .document(i.tittle.toString())
//
//            db.set(i)
//        }
//
//






    }

    private fun newsmaterial() {

        newscard.add(News("",""))

    }

    private fun news() {

        val uid = UUID.randomUUID()
        val heading = "Students to get board results even if they skip one of the two term exams, CBSE clarifies"
        val body = "CBSE has said that if a student is not able to take all the exams and fails to appear for one or two papers because of Covid-19, then they may receive the results under a special scheme. The Central Board of Secondary Education (CBSE) announced Monday that students who fail to appear for both Term-1 and Term-2 examinations will not be allowed to sit for compartment exams this year and have to repeat the class.\n" +
                "\n" +
                "In a live webinar ahead of the CBSE term-2 exam from April 26, Sanyam Bhardwaj, the board’s controller of examinations, said that students who were marked absent in term-1 exams and do not appear for term-2 exams will not be allowed to take the compartment exams because “all class 12 compartment exams are conducted on the same day.” For class 10, the compartment exams are conducted in time period of 7 days."
        val news = News(uid.toString(),
            "https://firebasestorage.googleapis.com/v0/b/upmark-a72c4.appspot.com/o/NewsImages%2Fnews1.webp?alt=media&token=6e731c35-a0ac-4c7e-bff2-98b4128b3df6", heading, body,
        "https://firebasestorage.googleapis.com/v0/b/upmark-a72c4.appspot.com/o/NewsImages%2Fnews2.jpeg?alt=media&token=87a9f00b-c62d-4924-bda2-144c733cb762", System.currentTimeMillis(), keywords(heading.toLowerCase()))



        val db = firestore.collection("News")
            .document(uid.toString())

        db.set(news).addOnSuccessListener { Log.e("NewsActivity","uploaded") }

    }


    private fun keywords(news:String):List<String>{

        val keywords = mutableListOf<String>()
        for ( i in 0 until news.length){

            for (j in (i+1)..news.length){

                keywords.add(news.slice(i until j))

            }

        }
//        Toast.makeText(this,"${keywords[0]}, ${keywords[1]}, ${keywords[2]}, ${keywords[3]}",Toast.LENGTH_SHORT).show()

        return keywords



    }


    private fun settingpdfs() {

        var pdfs = arrayListOf<Pdfs>()




        pdfs.add(Pdfs("Real Numbers NCERT"
            ,"Real Numbers NCERT"
            ,"Real numbers ncert book explanation.."
            ,"https://firebasestorage.googleapis.com/v0/b/upmark-a72c4.appspot.com/o/ChaptersDocuments%2F10Maths%2FReal%20Numbers%2Frealnumbersncert.pdf?alt=media&token=746402b2-01a5-4c1b-94bc-4eae7f8676d7"))


        pdfs.add(Pdfs("Real Numbers Notes"
            ,"Real Numbers Notes"
            ,"Real numbers notes..."
            ," https://firebasestorage.googleapis.com/v0/b/upmark-a72c4.appspot.com/o/ChaptersDocuments%2F10Maths%2FReal%20Numbers%2Frealnumbersnotes.pdf?alt=media&token=33169106-aa17-4aac-bf63-f9c72f4979c6"))



        for (pdf in pdfs){


            val db = firestore.collection("Class10")
                .document("Maths")
                .collection("Chapters")
                .document("Real Numbers")
                .collection("Pdfs")
                .document(pdf.name.toString())


            db.set(pdf)


        }

    }

    private fun settingmaterial() {


        var videos = arrayListOf<Videos>()





        videos.add(Videos("https://firebasestorage.googleapis.com/v0/b/upmark-a72c4.appspot.com/o/ChaptersDocuments%2F10Maths%2FProbability%2FScreenshot%202022-07-09%20161804.jpg?alt=media&token=5831dc72-3be0-469f-9788-6c8f6e8fe405"
            ,"PROBABILITY in One Shot - Class 10th Board Exam"
            ,"2h7hkpJI1sw"))

        videos.add(Videos("https://firebasestorage.googleapis.com/v0/b/upmark-a72c4.appspot.com/o/ChaptersDocuments%2F10Maths%2FProbability%2FScreenshot%202022-07-09%20161822.jpg?alt=media&token=b1234537-5023-4260-9e3f-cbd30ef6aa1d"
            ,"Probability | Probability class 10,9 | Class 10th Maths Chapter 15 |Full Chapter|Concept|Explanation"
            ,"QIhAO5ZZK5I"))

        videos.add(Videos("https://firebasestorage.googleapis.com/v0/b/upmark-a72c4.appspot.com/o/ChaptersDocuments%2F10Maths%2FProbability%2FScreenshot%202022-07-09%20161842.jpg?alt=media&token=8d589ea5-24fb-4ad0-986f-4811b9350f4d"
            ,"PROBABILITY - CLASS 10 || ONE SHOT - LIVE SESSION || NCERT FULL CHAPTER 15"
            ,"SHrbK8i5IKc"))

        videos.add(Videos("https://firebasestorage.googleapis.com/v0/b/upmark-a72c4.appspot.com/o/ChaptersDocuments%2F10Maths%2FProbability%2FScreenshot%202022-07-09%20161856.jpg?alt=media&token=d398b867-c3a5-46ea-96d4-124aaea53d5f"
            ,"Probability in One Shot | CBSE Class 10 Maths Chapter 15 | NCERT Solutions | Vedantu Class 9 and 10"
            ,"7wzasSUOV9w"))

        videos.add(Videos("https://firebasestorage.googleapis.com/v0/b/upmark-a72c4.appspot.com/o/ChaptersDocuments%2F10Maths%2FProbability%2FScreenshot%202022-07-09%20161909.jpg?alt=media&token=e7dfe375-7980-4233-9a0a-e83b39356636"
            ,"Probability Class 10 Quick Revision | Probability Concepts | Mathematics of Chance"
            ,"OmejHh2GJWs"))




        for (video in videos){


            val db = firestore.collection("Class10")
                .document("Maths")
                .collection("Chapters")
                .document("Probability")
                .collection("Videos")
                .document(video.tittle.toString())


                db.set(video)


        }

    }

    private fun setingchapters() {


        var chapters = arrayListOf<Chapters>()

//        chapters.add(Chapters("Light-Reflection and Refraction","Light-Reflection and Refraction",1,"4","5"))
//        chapters.add(Chapters("The Human Eye And The Colourful World","The Human Eye And The Colourful World",2,"4","5"))
//        chapters.add(Chapters("Electricity","Electricity",3,"4","5"))
//        chapters.add(Chapters("Magnetic Effects Of Electric Current","Magnetic Effects Of Electric Current",4,"4","5"))
//        chapters.add(Chapters("Arithmetic Progressions","Arithmetic Progressions",5,"4","5"))
//        chapters.add(Chapters("Triangles","Triangles",6,"4","5"))
//        chapters.add(Chapters("Coordinate Geometry","Coordinate Geometry",7,"4","5"))
        chapters.add(Chapters("Introduction to Trigonometry","Introduction to Trigonometry",8,"4","5"))
//        chapters.add(Chapters("Some Applications of Trigonometry","Some Applications of Trigonometry",9,"4","5"))
//        chapters.add(Chapters("Circles","Circles",10,"4","5"))
//        chapters.add(Chapters("Constructions","Constructions",11,"4","5"))
//        chapters.add(Chapters("Areas Related to Circle","Areas Related to Circle",12,"4","5"))
//        chapters.add(Chapters("Surface Areas and Volumes","Surface Areas and Volumes",13,"4","5"))
//        chapters.add(Chapters("Statistics","Statistics",14,"4","5"))
//        chapters.add(Chapters("Probability","Probability",15,"4","5"))



        for (chaper in chapters){


            val db = firestore.collection("Class10")
                .document("Maths")
                .collection("Chapters")
                .document(chaper.documentid.toString())

            db.set(chaper)


        }








    }


}