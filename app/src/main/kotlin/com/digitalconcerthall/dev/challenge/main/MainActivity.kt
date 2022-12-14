package com.digitalconcerthall.dev.challenge.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.digitalconcerthall.dev.challenge.R
import com.digitalconcerthall.dev.challenge.content.*
import com.digitalconcerthall.dev.challenge.util.AndroidUtils
import com.digitalconcerthall.dev.challenge.util.Log
import com.digitalconcerthall.dev.challenge.video.DCHVideoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.gson.Gson
import java.io.InputStreamReader
import javax.inject.Inject

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var gson: Gson

    private lateinit var videos: List<Video>

    private var player: DCHVideoPlayer? = null
    private var status: PlayerStatus = PlayerStatus.Idle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as ChallengeApplication).component.inject(this)

        setContentView(R.layout.activity_main)

        loadVideoList()
    }

    private fun loadVideoList() {
        val inputStream = resources.openRawResource(R.raw.data)
        videos = gson.fromJson(InputStreamReader(inputStream), VideoJson::class.java).videos
        Log.d("Parsed ${videos.size} videos")

        val spans = if (AndroidUtils.appWidthDp(this) >= 360) 2 else 2
        val adapter = VideoListRecycleAdapter(this)
        val layoutManager = GridLayoutManager(this, spans, GridLayoutManager.VERTICAL, false)
        videoList.layoutManager = layoutManager
        videoList.adapter = adapter
        val videoItems = videos.toVideoItems()
        adapter.add(videoItems)
        videoItems.forEachIndexed { index, videoItem ->
            videoItem.loadThumbnailAsync(videos[index].thumb) {
                adapter.replace(index, videoItem)
            }
        }
    }

    fun playVideo(videoUri: String) {
        status = PlayerStatus.Preparing
        val playerInstance = DCHVideoPlayer(this, true, statusListener)
        playerInstance.preparePlayer(videoUri)
        playerView.player = playerInstance.exoPlayer
        player = playerInstance
        playerNoVideoText.visibility = View.GONE
    }

    fun finishPlayback() {
        player?.releasePlayer()
        player = null
        status = PlayerStatus.Idle
        playerNoVideoText.visibility = View.VISIBLE
    }

    fun clearPlaylist() = player?.exoPlayer?.clearMediaItems()

    // This allows videos to be added to the playlist multiple times,
    // because I think that's better UX.
    fun addVideoToPlaylist(videoUri: String) =
        player?.exoPlayer?.addMediaItem(MediaItem.fromUri(videoUri))

    private val statusListener = object : DCHVideoPlayer.StatusListener() {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val playWhenReady = player?.exoPlayer?.playWhenReady
            val state = DCHVideoPlayer.exoPlayerState(playbackState)
            Log.d("Player state changed: play=$playWhenReady, state=$state, status=$status")

            when (playbackState) {
                Player.STATE_IDLE -> {
                    if (status != PlayerStatus.Idle && status != PlayerStatus.Preparing) {
                        finishPlayback()
                    }
                }
                Player.STATE_BUFFERING -> {
                    status = PlayerStatus.Playing
                }
                Player.STATE_READY -> {
                    status = PlayerStatus.Playing
                }
                Player.STATE_ENDED -> {
                    finishPlayback()
                }
            }

            super.onPlaybackStateChanged(playbackState)
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            finishPlayback()
            Log.e(error, "Video playback error")
        }
    }

    enum class PlayerStatus {
        Idle,
        Preparing,
        Playing
    }
}
