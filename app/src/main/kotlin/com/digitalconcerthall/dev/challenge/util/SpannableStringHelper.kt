package com.digitalconcerthall.dev.challenge.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import com.digitalconcerthall.dev.challenge.util.SpannableStringHelper.findClosures
import com.digitalconcerthall.dev.challenge.util.SpannableStringHelper.removeBrackets
import com.digitalconcerthall.dev.challenge.util.SpannableStringHelper.setItalic

object SpannableStringHelper {

    private var pairs = mutableListOf<Pair<Int, Int>>()

    fun replaceBracketsWithItalic(textComplete: String): SpannableString {
        pairs.clear()
        val builder = SpannableStringBuilder(textComplete).also {
            it.findClosures()
            it.setItalic()
            it.removeBrackets()
        }

        return SpannableString.valueOf(builder)
    }

    private fun SpannableStringBuilder.findClosures() {
        var currentOpenIndex: Int? = null
        this.asIterable().forEachIndexed { index, char ->
            if (currentOpenIndex == null) {
                if (char == '[') currentOpenIndex = index
            } else if (char == ']') {
                pairs.add(Pair(currentOpenIndex!!, index))
                currentOpenIndex = null
            }
        }
    }

    private fun SpannableStringBuilder.setItalic() {
        pairs.forEach {
            setSpan(
                StyleSpan(Typeface.ITALIC),
                it.first,
                it.second,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }
    }

    private fun SpannableStringBuilder.removeBrackets() {
        var removedCharsCount = 0

        pairs.forEach {
            val opening = it.first - removedCharsCount
            delete(opening, opening +1)
            removedCharsCount += 1
            val closure = it.second - removedCharsCount
            delete(closure, closure +1)
            removedCharsCount += 1
        }
    }

}
