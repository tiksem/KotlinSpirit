package com.kotlinspirit

import com.kotlinspirit.core.Rules.char
import com.kotlinspirit.core.Rules.int
import com.kotlinspirit.rangeres.*
import com.kotlinspirit.replacer.Replace
import com.kotlinspirit.replacer.Replacer
import org.junit.Assert
import org.junit.Test

class ReplaceTest {
    @Test
    fun replaceAllIntegers() {
        val parser = int.compile()
        val result = parser.replaceAll(
            "Hello 3344543, How is your time 456.233 today 321", "eee"
        ).toString()
        Assert.assertEquals("Hello eee, How is your time eee.eee today eee", result)
    }

    @Test
    fun replaceAllNameSplits() {
        val name = char('A'..'Z') + +char('a'..'z')
        val split = name % ','
        val parser = split.compile()
        val result = parser.replaceAll(
            "Hello 3344543, How,Right,Hey is your time No 456.233 today 321", "eee"
        ).toString()
        Assert.assertEquals("eee 3344543, eee is your time eee 456.233 today 321", result)
    }

    @Test
    fun replaceFirstNameSplit() {
        val name = char('A'..'Z') + +char('a'..'z')
        val split = name % ','
        val parser = split.compile()
        val result = parser.replaceFirst(
            "3344543, How,Right,Hey is your time No 456.233 today 321", "eee"
        ).toString()
        Assert.assertEquals("3344543, eee is your time No 456.233 today 321", result)
    }

    @Test
    fun multiplyTwiceAllIntegers() {
        val parser = int.compile()
        val result = parser.replaceAll("Hello 2 yoyoyo 45 norm -64") {
            it * 2
        }
        Assert.assertEquals("Hello 4 yoyoyo 90 norm -128", result.toString())
    }

    @Test
    fun multiplyTwiceFirstInteger() {
        val parser = int.compile()
        val result = parser.replaceFirst("Hello 2 yoyoyo 45 norm -64") {
            it * 2
        }
        Assert.assertEquals("Hello 4 yoyoyo 45 norm -64", result.toString())
    }

    @Test
    fun multipleIntegersSplitAndReplaceNameAndLastNameWithInitials() {
        val replacer = Replacer {
            val nameRange = range()
            val lastNameRange = range()
            val intsResult = rangeResultList<Int>()

            val name = char('A'..'Z') + +char('a'..'z')
            val nameAndLastName = name.getRange(nameRange) + ' ' + name.getRange(lastNameRange)

            val ints = int.getRangeResult {
                intsResult.add(it)
            } % ','

            fun replaceName(name: CharSequence): CharSequence {
                return name[0].toString() + '.'
            }

            Replace(
                rule = nameAndLastName + ' ' + ints
            ) {
                replace(nameRange, ::replaceName)
                replace(lastNameRange, ::replaceName)
                replace(intsResult) {
                    it * 2
                }
            }
        }

        Assert.assertEquals(
            "I. A. 2,4,-10,12 Urvan Arven 12,12,323,3",
            replacer.replaceFirst("Ivan Abdulan 1,2,-5,6 Urvan Arven 12,12,323,3").toString()
        )

        Assert.assertEquals(
            "I. A. 2,4,-10,12 U. A. 24,24,646,6",
            replacer.replaceAll("Ivan Abdulan 1,2,-5,6 Urvan Arven 12,12,323,3").toString()
        )
    }
}