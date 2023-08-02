package com.youngminds.upmarkx

import android.os.Bundle
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.youngminds.upmarkx.Models.Videos
import kotlinx.android.synthetic.main.activity_videopreview.*


class VideopreviewActivity : YouTubeBaseActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videopreview)





      val video = intent.getParcelableExtra<Videos>(VideosActivity.YOUTUBE_KEY)



        video?.let { youtubeplayerintisilazing(it) }


    }

    private fun youtubeplayerintisilazing(videos: Videos) {


        youtubeplayer.initialize("AIzaSyA8Cfl0y7QxiWy2NLMz-dsR0TSrLeqPE8g",object :YouTubePlayer.OnInitializedListener{
            override fun onInitializationSuccess(
                provider: YouTubePlayer.Provider?,
                player: YouTubePlayer?,
                wasRestored: Boolean
            ) {



                if (player==null)return

                if (wasRestored)
                    player.play()
                else{
                    player.cueVideo(videos.videourl)
                    player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)

                }



            }

            override fun onInitializationFailure(
                p0: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {



            }


        })

    }
}