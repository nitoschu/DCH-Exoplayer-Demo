package com.digitalconcerthall.dev.challenge.util

import android.content.Context

@Suppress("MemberVisibilityCanBePrivate", "unused")
object AndroidUtils {

    fun dpToPx(dp: Int, context: Context): Int = (dp * density(context)).toInt()
    fun pxToDp(px: Int, context: Context): Int = (px / density(context)).toInt()
    fun pxToDp(px: Float, context: Context): Float = (px / density(context))

    private fun deviceDPI(c: Context): Int {
        return c.resources.displayMetrics.densityDpi
    }

    fun appWidthDp(c: Context): Int {
        val metrics = c.resources.displayMetrics
        return pxToDp(metrics.widthPixels, c)
    }

    fun appHeightDp(c: Context): Int {
        val metrics = c.resources.displayMetrics
        return pxToDp(metrics.heightPixels, c)
    }

    fun density(context: Context): Float {
        return context.resources.displayMetrics.density
    }
}
