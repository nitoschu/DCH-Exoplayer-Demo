package com.digitalconcerthall.dev.challenge.video

import com.digitalconcerthall.dev.challenge.util.Log
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.MediaSourceEventListener
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.EventLogger

/** Logs events from [Player] and other core components using [Log].  */
internal class EventLogger() : Player.Listener,
    MediaSourceEventListener,
    CacheDataSource.EventListener, BandwidthMeter.EventListener, AnalyticsListener,
    EventLogger("EventLogger") {

    // CacheDataSource.EventListener
    override fun onCachedBytesRead(cacheSizeBytes: Long, cachedBytesRead: Long) {
        Log.d("Cache size: $cacheSizeBytes bytes, read: $cachedBytesRead bytes")
    }

    override fun onCacheIgnored(reason: Int) {
        Log.d("Cache ignored, reason: $reason")
    }

    // BandwidthMeter.EventListener
    override fun onBandwidthSample(elapsedMs: Int, bytesTransferred: Long, bitrateEstimate: Long) {
        Log.d("onBandwidthSample($elapsedMs, $bytesTransferred, $bitrateEstimate)")
    }
}
