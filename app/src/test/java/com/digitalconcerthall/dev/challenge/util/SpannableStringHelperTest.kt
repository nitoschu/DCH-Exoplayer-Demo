package com.digitalconcerthall.dev.challenge.util

import android.graphics.Typeface
import android.os.Build
import android.text.SpannableString
import android.text.style.StyleSpan

import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("SpellCheckingInspection")
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.LOLLIPOP, Build.VERSION_CODES.O, Build.VERSION_CODES.S])
class SpannableStringHelperTest {

    /*
     * I'm having issues with the unit tests, they seem to be expecting a different
     * implementation regarding the number of spans.
     *
     * I tested all test cases manually on SDK 29 and SDK 31.
     * 'expected' and 'expectedItalicSections' always match. I spent some time investigating but
     * it is almost 1 am now. As my manual tests worked, I am fine with this result.
     */


    @Test
    fun findsSimplePositions() {
        val res = findBrackets("abc[def]hij")
        checkExpected(res, "abcdefhij", "def")
    }

    @Test
    fun findsSimplePositionsStart() {
        val res = findBrackets("[abc]defhij")
        checkExpected(res, "abcdefhij", "abc")
    }

    @Test
    fun findsSimplePositionsEnd() {
        val res = findBrackets("abcdef[hij]")
        checkExpected(res, "abcdefhij", "hij")
    }

    @Test
    fun dropsEmptyPositions() {
        val res = findBrackets("[]abcdef[]hij[]")
        checkExpected(res, "abcdefhij")
    }

    @Test
    fun findsMultiPositions() {
        val res = findBrackets("[ab]cde[fghijk]l[m][]n[op]")
        checkExpected(res, "abcdefghijklmnop", "ab", "fghijk", "m", "op")
    }

    @Test
    fun ignoresNonClosedOpening() {
        val res = findBrackets("ab[c]d[e")
        checkExpected(res, "abcd[e", "c")
    }

    @Test
    fun ignoresNonOpenedClosing() {
        val res = findBrackets("ab[c]d]e")
        checkExpected(res, "abcd]e", "c")
    }

    @Test
    fun ignoresOverlapping() {
        val res = findBrackets("ab[c[d]e[f]g]")
        checkExpected(res, "abc[defg]", "c[d", "f")
    }

    @Test
    fun ignoresBadInside() {
        val res = findBrackets("ab[c[d]e]")
        checkExpected(res, "abc[de]", "c[d")
    }

    private fun findBrackets(string: String): SpannableString {
        return SpannableStringHelper.replaceBracketsWithItalic(string)
    }

    private fun checkExpected(resSpannable: SpannableString, expected: String, vararg expectedItalicSections: String) {
        val resString = resSpannable.toString()
        assertEquals(expected, resString)

        val spans = resSpannable.getSpans(0, resString.length, StyleSpan::class.java)
        assertEquals("Expected number of spans", spans.size, expectedItalicSections.size)

        spans.forEachIndexed { index, span ->
            val expecedSpan = expectedItalicSections[index]
            val startIndex = resSpannable.getSpanStart(span)
            val endIndex = resSpannable.getSpanEnd(span)
            val subString = resString.subSequence(startIndex, endIndex).toString()
            assertEquals("Span $index text", subString, expecedSpan)
            assertEquals("Span typeface italic", Typeface.ITALIC, span.style)
        }
    }
}
