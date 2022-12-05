package com.digitalconcerthall.dev.challenge.main

import android.app.Application
import com.digitalconcerthall.dev.challenge.dagger.DaggerAppComponent
import com.digitalconcerthall.dev.challenge.dagger.AppComponent
import com.digitalconcerthall.dev.challenge.BuildConfig
import com.digitalconcerthall.dev.challenge.dagger.AppModule
import com.digitalconcerthall.dev.challenge.util.Log
import java.util.logging.Level
import java.util.logging.Logger

class ChallengeApplication : Application() {

    val component: AppComponent by lazy {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        component.inject(this)

        initLogging()
        Log.d("Application set up")
    }

    private fun initLogging() {
        Log.setShowLogs(BuildConfig.DEBUG)
        adjustCoreLogging()
    }

    private fun adjustCoreLogging() {
        @Suppress("ConstantConditionIf")
        val defaultLogLevel = if (BuildConfig.DEBUG) getDebugLogLevel() else Level.WARNING
        val rootLogger = Logger.getLogger("")
        rootLogger.level = if (android.util.Log.isLoggable(Log.TAG, android.util.Log.DEBUG))
            Level.FINE
        else
            defaultLogLevel
    }

    private fun getDebugLogLevel(): Level {
        return if (android.util.Log.isLoggable(Log.TAG, android.util.Log.DEBUG))
            Level.FINE
        else
            Level.INFO
    }
}

