package com.digitalconcerthall.dev.challenge.video

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.digitalconcerthall.dev.challenge.content.Video
import com.digitalconcerthall.dev.challenge.util.Log
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Renderer
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.audio.AudioCapabilities
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.audio.DefaultAudioSink
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.metadata.MetadataOutput
import com.google.android.exoplayer2.metadata.MetadataRenderer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.text.TextOutput
import com.google.android.exoplayer2.text.TextRenderer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.MediaCodecVideoRenderer
import com.google.android.exoplayer2.video.VideoRendererEventListener

class DCHVideoPlayer(
    context: Context,
    shouldAutoPlay: Boolean,
    statusListener: StatusListener
) {

    companion object {

        fun exoPlayerState(state: Int): String {
            return when (state) {
                Player.STATE_IDLE -> "STATE_IDLE"
                Player.STATE_BUFFERING -> "STATE_BUFFERING"
                Player.STATE_READY -> "STATE_READY"
                Player.STATE_ENDED -> "STATE_ENDED"
                else -> "STATE_UNKNOWN[$state]"
            }
        }

        const val RENDERER_TYPE_VIDEO = 0
        const val RENDERER_TYPE_AUDIO = 1
    }

    private val handler: Handler = Handler(Looper.getMainLooper())

    private val mediaDataSourceFactory: DataSource.Factory
    private val bandwidthMeter = DefaultBandwidthMeter.Builder(context).build()

    private val trackSelector = DefaultTrackSelector(context)
    private val eventLogger = EventLogger()

    private val userAgent = Util.getUserAgent(context, "DigitalConcertHall")

    val exoPlayer: ExoPlayer

    init {
        mediaDataSourceFactory = buildDataSourceFactory(context)

        exoPlayer = ExoPlayer.Builder(context, DCHRendererFactory(context))
            .setTrackSelector(trackSelector)
            .setLoadControl(DefaultLoadControl())
            .setBandwidthMeter(bandwidthMeter).build()
        exoPlayer.playWhenReady = shouldAutoPlay
        exoPlayer.addListener(statusListener)
        exoPlayer.addListener(eventLogger)
        exoPlayer.addAnalyticsListener(eventLogger)
        exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
    }

    fun preparePlayer(videoUri: String) {
        Log.d("Start player with URL: $videoUri")
        val mediaSource = buildMediaSource(videoUri)

        exoPlayer.setMediaSource(mediaSource, true)
        exoPlayer.prepare()
    }

    fun releasePlayer() {
        exoPlayer.stop()
        exoPlayer.release()
    }

    private fun buildMediaSource(videoUri: String): MediaSource {
        val uri = Uri.parse(videoUri)

        val factory = when (val type = Util.inferContentType(uri)) {
            C.CONTENT_TYPE_SS -> {
                Log.d("DCH", "Building media source for video type: CONTENT_TYPE_SS")
                SsMediaSource.Factory(mediaDataSourceFactory)
            }
            C.CONTENT_TYPE_DASH -> {
                Log.d("DCH", "Building media source for video type: CONTENT_TYPE_DASH")
                DashMediaSource.Factory(mediaDataSourceFactory)
            }
            C.CONTENT_TYPE_HLS -> {
                Log.d("DCH", "Building media source for video type: CONTENT_TYPE_HLS")
                HlsMediaSource.Factory(mediaDataSourceFactory)
            }
            C.CONTENT_TYPE_OTHER -> {
                Log.d("DCH", "Building media source for video type: CONTENT_TYPE_OTHER")
                ProgressiveMediaSource.Factory(mediaDataSourceFactory)
            }
            else -> {
                throw IllegalStateException("Unsupported type: $type")
            }
        }

        val mediaItem = MediaItem.Builder().setUri(uri).build()
        val mediaSource = factory.createMediaSource(mediaItem)
        mediaSource.addEventListener(handler, eventLogger)

        return mediaSource
    }

    private fun buildDataSourceFactory(context: Context): DataSource.Factory {
        val httpDataSourceFactory = BaseHttpsDataSourceFactory(userAgent, bandwidthMeter)
        return DefaultDataSource.Factory(context, httpDataSourceFactory)
    }

    abstract class StatusListener : Player.Listener

    private class DCHRendererFactory constructor(
        private val context: Context,
        private val useTextRenderer: Boolean = false,
        private val useMetaDataRenderer: Boolean = false,
        private val allowedVideoJoiningTimeMs: Long = DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS
    ) : RenderersFactory {
        companion object {
            private const val DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS = 5000L
            private const val MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY = 50
        }

        override fun createRenderers(
            eventHandler: Handler,
            videoRendererEventListener: VideoRendererEventListener,
            audioRendererEventListener: AudioRendererEventListener,
            textRendererOutput: TextOutput,
            metadataRendererOutput: MetadataOutput
        ): Array<Renderer> {
            val rendererList = ArrayList<Renderer>()
            val audioSink = DefaultAudioSink.Builder()
                .setAudioCapabilities(AudioCapabilities.getCapabilities(context))
                .setAudioProcessors(emptyArray())
                .build()
            val videoRenderer = MediaCodecVideoRenderer(
                context,
                MediaCodecSelector.DEFAULT,
                allowedVideoJoiningTimeMs,
                false,
                eventHandler,
                videoRendererEventListener,
                MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY
            )
            val audioRenderer = MediaCodecAudioRenderer(
                context,
                MediaCodecSelector.DEFAULT,
                true,
                eventHandler,
                audioRendererEventListener,
                audioSink
            )
            rendererList.add(RENDERER_TYPE_VIDEO, videoRenderer)
            rendererList.add(RENDERER_TYPE_AUDIO, audioRenderer)

            if (useTextRenderer) {
                rendererList.add(TextRenderer(textRendererOutput, eventHandler.looper))
            }
            if (useMetaDataRenderer) {
                rendererList.add(MetadataRenderer(metadataRendererOutput, eventHandler.looper))
            }
            return rendererList.toTypedArray()
        }
    }
}

