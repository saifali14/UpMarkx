package com.youngminds.upmarkx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.youngminds.upmarkx.Models.User
import com.youngminds.upmarkx.fragments.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object{

        val TAG = "MainActivity"

        val TEXT = "TEXT"


    }


    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
//    private val mainHomeFragment = MainHomeFragment()
//    private val newsFragment = NewsFragment()
    private val askDoubtFragment = AskDoubtFragment()
    private val meProfileFragment = MeProfileFragment()
    private val doubtsFragment = DoubtsFragment()



    private lateinit var toggle: ActionBarDrawerToggle


    private lateinit var firestore:FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        firestore = FirebaseFirestore.getInstance()


        MobileAds.initialize(this) {}

        supportActionBar?.title =""








        bottomnav_main.add(MeowBottomNavigation.Model(0,R.drawable.bottom_home))
        bottomnav_main.add(MeowBottomNavigation.Model(1,R.drawable.search_icon22))
        bottomnav_main.add(MeowBottomNavigation.Model(2,R.drawable.askquestionicon))
        bottomnav_main.add(MeowBottomNavigation.Model(3, R.drawable.doubt_icon))
        bottomnav_main.add(MeowBottomNavigation.Model(4,R.drawable.profile_icon))



        bottomnav_main.setOnClickMenuListener {

            when(it.id){


                0 ->{
                    replacefragment(homeFragment)
                }

                1 ->{
                    replacefragment(searchFragment)
                }
                2 ->{ replacefragment(askDoubtFragment)}


                3-> { replacefragment(doubtsFragment)}

                4->{
                    replacefragment(meProfileFragment)
                }


            }
        }

        bottomnav_main.show(0 )



//        toggle = ActionBarDrawerToggle(this,drawerlayout,R.string.open,R.string.close)
//        drawerlayout.addDrawerListener(toggle)
//        toggle.syncState()



















//
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//
//        main_drawerlayout.setOnClickListener {
//
//
//            drawerlayout.openDrawer(GravityCompat.START,true)
//
//        }
//
//
//        main_messagebtn.setOnClickListener {
//
//            val intent = Intent(this,LatestMessageseActivity::class.java)
//            startActivity(intent)
//
//        }
//
//        nav_view.setNavigationItemSelectedListener {
//
//            when(it.itemId){
//
//                R.id.nav_subjects ->{
//
//                    val  intent = Intent(this,CategoryActivity::class.java)
//                    startActivity(intent)
//                    drawerlayout.closeDrawer(GravityCompat.START,false)
//
//
//
//
//                }
//
//                R.id.nav_messages ->{
//                    val  intent = Intent(this,LatestMessageseActivity::class.java)
//                    startActivity(intent)
//                    drawerlayout.closeDrawer(GravityCompat.START,false)
//
//
//                }
//
//                R.id.nav_community ->{
//                    val  intent = Intent(this,CommunityActivity::class.java)
//                    startActivity(intent)
//                    drawerlayout.closeDrawer(GravityCompat.START,false)
//
//
//                }
//
//                R.id.nav_news ->{
//                    val  intent = Intent(this,NewsActivity::class.java)
//                    startActivity(intent)
//                    drawerlayout.closeDrawer(GravityCompat.START,false)
//
//                }
//
//                R.id.nav_profile -> {Toast.makeText(this,"profile clicked",Toast.LENGTH_SHORT).show()
//                }
//
//                R.id.nav_leaderboard ->{
//                    val intent = Intent(this,LeaderboardActivity::class.java)
//                    startActivity(intent)
//                    drawerlayout.closeDrawer(GravityCompat.START,false)
//
//                }
//
//
//
//
//
//            }
//            true
//        }



//        home_searchview.addTextChangedListener(object : TextWatcher{
//            var text = 0
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//             Log.e(TAG,s.toString())
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                home_searchview.editableText.clear()
//
//
//                text++
//                if (text == 1){
//
//                    text = 0
//                    val intent  = Intent(this@MainActivity,SearchActivity::class.java)
//
////                intent.putExtra(TEXT,s.toString())
//                    startActivity(intent)
//
//                }
//
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                Log.e(TAG,s.toString())
//
//            }
//
//
//        })


//
//        newscard.setOnClickListener {
//
//            val intent = Intent(this,NewsActivity::class.java)
//            startActivity(intent)
//
//        }
//
//        allsubjectscard.setOnClickListener {
//
//            val intent = Intent(this,CategoryActivity::class.java)
//            startActivity(intent)
//
//        }
//
//        communitycard.setOnClickListener {
//
//
//            val intent = Intent(this,CommunityActivity::class.java)
//            startActivity(intent)
//
//
//        }
//
//        leaderboad_card.setOnClickListener {
//
//            val intent = Intent(this,LeaderboardActivity::class.java)
//            startActivity(intent)
//
//        }


//
//        retreiveuser()



        replacefragment(homeFragment)

//        bottomnav_main.setOnNavigationItemSelectedListener {
//
//            when(it.itemId){
//
//
//                R.id.bottomnav_home ->{ replacefragment(homeFragment)
//
//                }
//
//                R.id.bottomnav_search ->{ replacefragment(searchFragment) }
//
//                R.id.bottomnav_community ->{ replacefragment(newsFragment)}
//
//
//
//
//
//            }
//            true
//        }





    }



    private fun replacefragment(fragment:Fragment){


        if (fragment !=null){


            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.main_framelayout,fragment)
                .addToBackStack(null)
            transaction.commit()

        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.nav_menup,menu)

        return super.onCreateOptionsMenu(menu)


    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {



        if (toggle.onOptionsItemSelected(item)){



            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun retreiveuser() {


        val currrentueruid = FirebaseAuth.getInstance().uid.toString()
        val db  = firestore.collection("UserInfo").whereEqualTo("uid",currrentueruid)
        db.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {


                if (error!=null){


                    Toast.makeText(this@MainActivity,"Network issue is there",Toast.LENGTH_SHORT).show()

                }


                for (dc:DocumentChange in value?.documentChanges!!){

                    if(dc.type == DocumentChange.Type.ADDED){

                      val  user = dc.document.toObject(User::class.java)
//
//                        home_username.text = user.firstname
//                        Glide.with(applicationContext).load(user.imageuri).into(home_imageview)
//                        Glide.with(applicationContext).load(user.imageuri).into(navheader_imageview)
//                        navheader_class.text = user.standard
//                        navheader_nameview.text = user.firstname
//                        navheader_email.text = user.email

                    }

                }


            }


        })



    }











}