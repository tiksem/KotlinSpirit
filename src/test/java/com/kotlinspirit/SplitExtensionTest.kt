package com.kotlinspirit

import com.kotlinspirit.core.Rules.double
import com.kotlinspirit.core.Rules.int
import com.kotlinspirit.ext.split
import org.junit.Test

class SplitExtensionTest {
    val rule = int + "/" + double

    @Test
    fun emptyString() {
        Asserts.listStringEquals(listOf(""), "".split(rule))
    }

    @Test
    fun stringWith1Value() {
        Asserts.listStringEquals(listOf("1"), "1".split(rule))
    }

    @Test
    fun stringWithSeveralValues() {
        Asserts.listStringEquals(listOf("", "-", "---", ""), "-34/5.8--23/0----12345/-45".split(rule))
    }

    @Test
    fun stringWithSeveralValues2() {
        Asserts.listStringEquals(listOf("*", "**", "***", "****"), "*-34/5.8**23/0***-12345/-45****".split(rule))
    }
}