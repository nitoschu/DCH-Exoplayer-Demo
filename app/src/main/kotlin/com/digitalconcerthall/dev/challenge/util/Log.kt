package com.digitalconcerthall.dev.challenge.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Wrapper around Android Log that will print
 *
 * 'TAG [Thread Name][Class name][Method name:line number] message'
 */
@Suppress("unused")
object Log {

    /**
     * Flag to enable or disable logs
     *
     * Recommended you set this to `BuildConfig.DEBUG` in your class that extends Application
     */
    private var SHOW_LOGS = false

    var TAG = "DCH"

    /**
     * How deep to log stack traces - Default is 5
     */
    private var STACK_DEPTH = 5

    private const val DOT_CLASS = 5

    private val dateFormat = object: ThreadLocal<DateFormat>() {
        override fun initialValue(): DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY)
    }

    private val dateTimeFormat = object: ThreadLocal<DateFormat>() {
        override fun initialValue(): DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMANY)
    }

    fun formatLogDate(date: Date): String = getDateFormat().format(date)
    fun formatLogDateTime(date: Date): String = getDateTimeFormat().format(date)
    fun formatLogDateTime(ts: Long): String = formatLogDateTime(Date(ts))
    fun currentDate(): String = getDateTimeFormat().format(Date())
    fun currentDateTime(): String = getDateTimeFormat().format(Date())

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun getDateFormat(): DateFormat = dateFormat.get()!!

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun getDateTimeFormat(): DateFormat = dateTimeFormat.get()!!

    /**
     * Separator for the concatenation of the vaargs
     */
    @JvmStatic
    private var separator = " "

    fun setSeparator(separator: String) {
        Log.separator = separator
    }

    @JvmStatic
    fun v(vararg msg: Any) {
        try {
            if (shouldShowLogs()) {
                android.util.Log.v(TAG, getDetailedLog(formatString(*msg)))
            }
        } catch (ignore: RuntimeException) {
            logError(ignore)
        }

    }

    @JvmStatic
    fun i(vararg msg: Any) {
        try {
            if (shouldShowLogs()) {
                android.util.Log.i(TAG, getDetailedLog(formatString(*msg)))
            }
        } catch (ignore: RuntimeException) {
            logError(ignore)
        }

    }

    @JvmStatic
    fun d(vararg msg: Any) {
        try {
            if (shouldShowLogs()) {
                android.util.Log.d(TAG, getDetailedLog(formatString(*msg)))
            }
        } catch (ignore: RuntimeException) {
            logError(ignore)
        }

    }

    @JvmStatic
    fun w(vararg msg: Any) {
        try {
            if (shouldShowLogs()) {
                android.util.Log.w(TAG, getDetailedLog(formatString(*msg)))
            }
        } catch (ignore: RuntimeException) {
            logError(ignore)
        }

    }

    @JvmStatic
    fun e(vararg msg: Any) {
        try {
            if (shouldShowLogs()) {
                android.util.Log.e(TAG, getDetailedLog(formatString(*msg)))
            }
        } catch (ignore: RuntimeException) {
            logError(ignore)
        }

    }

    @JvmStatic
    fun w(t: Throwable, vararg msg: Any) {
        try {
            if (shouldShowLogs()) {
                android.util.Log.w(TAG, getDetailedLog(formatString(*msg)), t)
            }
        } catch (ignore: RuntimeException) {
            logError(ignore)
        }

    }

    @JvmStatic
    fun d(t: Throwable, vararg msg: Any) {
        try {
            if (shouldShowLogs()) {
                android.util.Log.d(TAG, getDetailedLog(formatString(*msg)), t)
            }
        } catch (ignore: RuntimeException) {
            logError(ignore)
        }

    }

    @JvmStatic
    fun e(t: Throwable, vararg msg: Any) {
        try {
            if (shouldShowLogs()) {
                android.util.Log.e(TAG, getDetailedLog(formatString(*msg)), t)
            }
        } catch (ignore: RuntimeException) {
            logError(ignore)
        }

    }

    @JvmStatic
    fun wtf(msg: String, t: Throwable) {
        wtf(t, msg)
    }

    @JvmStatic
    fun wtf(t: Throwable, vararg msg: Any) {
        try {
            if (shouldShowLogs()) {
                android.util.Log.wtf(TAG, getDetailedLog(formatString(*msg)), t)
            }
        } catch (ignore: RuntimeException) {
            logError(ignore)
        }

    }

    private fun getDetailedLog(msg: String, depth: Int = STACK_DEPTH): String {
        if (!shouldShowLogs()) {
            return msg
        }
        val currentThread = Thread.currentThread()
        val trace = currentThread.stackTrace[depth]
        val filename = trace.fileName
        val linkableSourcePosition = String.format("(%s.java:%d)", filename.substring(0, filename.length - DOT_CLASS), trace.lineNumber)
        val logPrefix = String.format("[%s][%s.%s] ", currentThread.name, linkableSourcePosition, trace.methodName)
        return logPrefix + msg
    }

    private fun logError(ignore: Throwable) {
        android.util.Log.e(TAG, "Error", ignore)
    }

    private fun formatString(vararg msg: Any): String {
        val stringBuilder = StringBuilder()
        for (o in msg) {
            stringBuilder.append(o.toString()).append(separator)
        }
        return stringBuilder.toString()
    }

    /**
     * Toggle if logs should be output or not. Recommended to bind against `BuildConfig.DEBUG`

     * @param showLogs `true` for showing logs, `false` to deactivate them
     */
    @JvmStatic
    fun setShowLogs(showLogs: Boolean) {
        SHOW_LOGS = showLogs
    }

    /**
     * Returns the active status of the log switch

     * @return `true` if logs are active, `false` if deactivated
     */
    @JvmStatic
    fun shouldShowLogs(): Boolean {
        return SHOW_LOGS
    }
}
