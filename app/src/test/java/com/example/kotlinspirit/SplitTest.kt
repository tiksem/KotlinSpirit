package com.example.kotlinspirit

import com.example.kotlinspirit.Rules.int
import org.junit.Assert
import org.junit.Test

class SplitTest {
    @Test
    fun splitHelloWorld() {
        val r = Rules.str('a'..'z', 'A'..'Z') % " "
        val e = r.parseOrThrow("hello world")
        Assert.assertArrayEquals(e.toTypedArray(), arrayOf("hello", "world"))
    }

    @Test
    fun splitHelloWorld2() {
        var mark: Char = Char.MIN_VALUE
        var array = arrayOf<CharSequence>()
        val r = (Rules.str('a'..'z', 'A'..'Z') % " ").on {
            array = it.toTypedArray()
        } + Rules.char('!', '?').on {
            mark = it
        }
        r.parseOrThrow("Hello world!")
        Assert.assertArrayEquals(array, arrayOf("Hello", "world"))
        Assert.assertEquals(mark, '!')
    }

    @Test
    fun splitExactInt() {
        val rule = int.split(',', 3)
        val res = rule.parseOrThrow("34,-23,45")
        Assert.assertArrayEquals(res.toTypedArray(), arrayOf(34, -23, 45))
    }

    @Test
    fun splitInt() {
        val rule = int % ','
        val res = rule.parseOrThrow("34,-23,45,345")
        Assert.assertArrayEquals(res.toTypedArray(), arrayOf(34, -23, 45, 345))
    }

    @Test
    fun splitIntDividerString() {
        val rule = int % "aabibu"
        val res = rule.parseOrThrow("34aabibu-23aabibu45aabibu345")
        Assert.assertArrayEquals(res.toTypedArray(), arrayOf(34, -23, 45, 345))
    }

    @Test
    fun splitIntDividerStringRange() {
        val rule = int.split("aabibu", 4..6)
        val res = rule.parseOrThrow("34aabibu-23aabibu45aabibu345")
        Assert.assertArrayEquals(res.toTypedArray(), arrayOf(34, -23, 45, 345))
    }

    @Test
    fun splitIntDividerCharRange() {
        val rule = int.split("a", 4..6)
        val res = rule.parseOrThrow("34a-23a45a345")
        Assert.assertArrayEquals(res.toTypedArray(), arrayOf(34, -23, 45, 345))
    }

    @Test
    fun splitComplexDivider() {
        val rule = int % !int
        val res = rule.parseOrThrow("1,,,,,-12443|0435663546-+-")
        Assert.assertArrayEquals(res.toTypedArray(), arrayOf(1, -12443, 435663546))
    }
}