package com.youngminds.upmarkx

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.firestore.FirebaseFirestore
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import com.youngminds.upmarkx.Models.Pdfs
import com.youngminds.upmarkx.recyclerview.Pdfsrercy
import kotlinx.android.synthetic.main.activity_pdfpreview.*
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.File

class PdfpreviewActivity : AppCompatActivity() {

    companion object{

        var pdfs: Pdfs?=null
        var downloadid:Long = 0

        private var mRewardedAd: RewardedAd? = null
        val TAG = "PdfpreivewActivity"

    }

    private lateinit var firestore:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdfpreview)


        firestore = FirebaseFirestore.getInstance()

         pdfs = intent.getParcelableExtra(Pdfsrercy.PDF_KEY)

        supportActionBar?.title = pdfs?.name



        var adRequest = AdRequest.Builder().build()

        RewardedAd.load(this,"ca-app-pub-2181621433616722/3646045432", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.message)
                RewardedAd.load(this@PdfpreviewActivity,"ca-app-pub-2181621433616722/3646045432", adRequest, object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(TAG, adError.message)
                        mRewardedAd = null
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        Log.d(TAG, "Ad was loaded.")
                        mRewardedAd = rewardedAd
                    }
                })
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d(TAG, "Ad was loaded.")
                mRewardedAd = rewardedAd
            }
        })


        Handler().postDelayed({},5000)

        pdfs?.let { previewpdf(it) }

    }

    private fun previewpdf(Pdfs: Pdfs) {


        FileLoader.with(this)
            .load(Pdfs.pdflink,false)
            .fromDirectory("PDFFile", FileLoader.DIR_INTERNAL)
            .asFile(object : FileRequestListener<File> {
                override fun onLoad(request: FileLoadRequest?, response: FileResponse<File>?) {


                    val pdfFile = response?.body

                    pdfviewer.fromFile(pdfFile)
                        .password(null)
                        .defaultPage(0)
                        .enableSwipe(true)
                        .swipeHorizontal(true)
                        .enableDoubletap(true)
                        .onPageError{ page,t ->
                            Toast.makeText(this@PdfpreviewActivity,"error while opening the page $page",
                                Toast.LENGTH_LONG).show()
                        }
                        .onTap{false}
                        .onRender { nbPages, pageWidth, pageHeight ->
                            pdfviewer.fitToWidth()
                        }
                        .enableAnnotationRendering(true)
                        .invalidPageColor(Color.RED)
                        .load()

                }

                override fun onError(request: FileLoadRequest?, t: Throwable?) {
                    Toast.makeText(this@PdfpreviewActivity,"${t?.message}", Toast.LENGTH_LONG).show()
                }


            })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.nav_pdfpreview,menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.downloadbtn ->{

                downloadpdf()

            }


        }


        return super.onOptionsItemSelected(item)
    }

    private fun downloadpdf() {








        val  request = DownloadManager.Request(Uri.parse(pdfs?.pdflink?.trim()))
            .setTitle(pdfs?.name)
            .setDescription("${pdfs?.name} is dowloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setAllowedOverMetered(true)



        val  dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        downloadid = dm.enqueue(request)



        val br =  object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {

                val id:Long? = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1)

                if (id == downloadid){

                    if (mRewardedAd != null) {
                        mRewardedAd?.show(this@PdfpreviewActivity, OnUserEarnedRewardListener() {
                            fun onUserEarnedReward(rewardItem: RewardItem) {

                                Log.d(TAG, "User earned the reward.")
                            }
                        })
                    } else {
                        Log.d(TAG, "The rewarded ad wasn't ready yet.")
                    }


                    MotionToast.createToast(this@PdfpreviewActivity
                        ,"Pdf downloaded"
                        ,"${pdfs?.name} downloaded..."
                        , MotionToastStyle.SUCCESS
                        , MotionToast.GRAVITY_BOTTOM
                        , MotionToast.LONG_DURATION
                        , ResourcesCompat.getFont(this@PdfpreviewActivity,R.font.lato_bold))



                }
            }


        }


        registerReceiver(br, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))


    }





}