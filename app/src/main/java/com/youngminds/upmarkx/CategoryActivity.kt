package com.youngminds.upmarkx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import kotlinx.android.synthetic.main.activity_category.*

class CategoryActivity : AppCompatActivity() {


    companion object{

        val SUBJECT_KEY  = "SUBJECT_KEY"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)




        category_backbtn.setOnClickListener {

            finish()
        }

        category_sciencecard.setOnClickListener {

            if (category_scienceexpandable.visibility == View.GONE) {

                TransitionManager.beginDelayedTransition(category_sciencecard, AutoTransition())
                category_scienceexpandable.visibility = View.VISIBLE


            } else {
                TransitionManager.beginDelayedTransition(category_sciencecard, AutoTransition())
                category_scienceexpandable.visibility = View.GONE



            }

        }

            category_socialcard.setOnClickListener {


                if (category_socialexpandable.visibility == View.GONE){

                    TransitionManager.beginDelayedTransition(category_socialcard,AutoTransition())
                    category_socialexpandable.visibility =View.VISIBLE


                }else{
                    TransitionManager.beginDelayedTransition(category_socialcard,AutoTransition())
                    category_socialexpandable.visibility =View.GONE







            }


        }





        category_physicscard.setOnClickListener {


            val intent = Intent(this,ChaptersActivity::class.java)
            intent.putExtra(SUBJECT_KEY,"Physics")
            startActivity(intent)


        }

        category_biologycard.setOnClickListener {


            val intent = Intent(this,ChaptersActivity::class.java)
            intent.putExtra(SUBJECT_KEY,"Biology")
            startActivity(intent)


        }


        category_chemistrycard.setOnClickListener {


            val intent = Intent(this,ChaptersActivity::class.java)
            intent.putExtra(SUBJECT_KEY,"Chemistry")
            startActivity(intent)


        }


        category_historycard.setOnClickListener {


            val intent = Intent(this,ChaptersActivity::class.java)
            intent.putExtra(SUBJECT_KEY,"History")
            startActivity(intent)


        }


        category_geographycard.setOnClickListener {


            val intent = Intent(this,ChaptersActivity::class.java)
            intent.putExtra(SUBJECT_KEY,"Geography")
            startActivity(intent)


        }


        category_politicalcard.setOnClickListener {


            val intent = Intent(this,ChaptersActivity::class.java)
            intent.putExtra(SUBJECT_KEY,"PoliticalScience")
            startActivity(intent)


        }



        category_economicscard.setOnClickListener {


            val intent = Intent(this,ChaptersActivity::class.java)
            intent.putExtra(SUBJECT_KEY,"Economics")
            startActivity(intent)


        }


        category_englihcard.setOnClickListener {


            val intent = Intent(this,ChaptersActivity::class.java)
            intent.putExtra(SUBJECT_KEY,"English")
            startActivity(intent)


        }


        category_mathscard.setOnClickListener {


            val intent = Intent(this,ChaptersActivity::class.java)
            intent.putExtra(SUBJECT_KEY,"Maths")
            startActivity(intent)


        }


        category_hindicard.setOnClickListener {


            val intent = Intent(this,ChaptersActivity::class.java)
            intent.putExtra(SUBJECT_KEY,"Hindi")
            startActivity(intent)


        }



    }
}