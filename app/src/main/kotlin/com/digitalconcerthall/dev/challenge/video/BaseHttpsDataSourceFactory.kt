package com.digitalconcerthall.dev.challenge.video

import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource.BaseFactory
import com.google.android.exoplayer2.upstream.HttpDataSource.Factory
import com.google.android.exoplayer2.upstream.TransferListener

/** A [Factory] that produces [DefaultHttpDataSource] instances.  */
class BaseHttpsDataSourceFactory(
    private val userAgent: String,
    private val transferListener: TransferListener,
    private val connectTimeoutMillis: Int = DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
    private val readTimeoutMillis: Int = DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
    private val allowCrossProtocolRedirects: Boolean = false
) : BaseFactory() {

    override fun createDataSourceInternal(defaultRequestProperties: HttpDataSource.RequestProperties):
            DefaultHttpDataSource {

        val dataSource = DefaultHttpDataSource.Factory()
            .setUserAgent(userAgent)
            .setConnectTimeoutMs(connectTimeoutMillis)
            .setReadTimeoutMs(readTimeoutMillis)
            .setAllowCrossProtocolRedirects(allowCrossProtocolRedirects)
            .setDefaultRequestProperties(defaultRequestProperties.snapshot)
            .setTransferListener(transferListener)
            .createDataSource()
        dataSource.addTransferListener(transferListener)
        return dataSource
    }
}
