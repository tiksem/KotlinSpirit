package com.example.kotlinspirit

import com.example.kotlinspirit.ext.minus
import com.example.kotlinspirit.ext.plus
import org.junit.Assert
import org.junit.Test

class RangeExtensionsTest {
    @Test
    fun testMinus() {
        Assert.assertArrayEquals((('3'..'5') - ('3'..'5')).toTypedArray(), arrayOf())
        Assert.assertArrayEquals((('3'..'5') - ('2'..'6')).toTypedArray(), arrayOf())
        Assert.assertArrayEquals((('3'..'5') - ('3'..'6')).toTypedArray(), arrayOf())
        Assert.assertArrayEquals((('3'..'5') - ('2'..'5')).toTypedArray(), arrayOf())
        Assert.assertArrayEquals((('1'..'9') - ('3'..'5')).toTypedArray(), arrayOf('1'..'2', '6'..'9'))
        Assert.assertArrayEquals((('2'..'9') - ('0'..'5')).toTypedArray(), arrayOf('6'..'9'))
        Assert.assertArrayEquals((('2'..'9') - ('2'..'5')).toTypedArray(), arrayOf('6'..'9'))
        Assert.assertArrayEquals((('0'..'8') - ('7'..'9')).toTypedArray(), arrayOf('0'..'6'))
        Assert.assertArrayEquals((('0'..'8') - ('7'..'8')).toTypedArray(), arrayOf('0'..'6'))
    }

    @Test
    fun testPlus() {
        Assert.assertArrayEquals((('3'..'5') + ('3'..'5')).toTypedArray(), arrayOf('3'..'5'))
        Assert.assertArrayEquals((('1'..'5') + ('2'..'4')).toTypedArray(), arrayOf('1'..'5'))
        Assert.assertArrayEquals((('1'..'5') + ('1'..'4')).toTypedArray(), arrayOf('1'..'5'))
        Assert.assertArrayEquals((('1'..'5') + ('2'..'5')).toTypedArray(), arrayOf('1'..'5'))
        Assert.assertArrayEquals((('1'..'5') + ('6'..'7')).toTypedArray(), arrayOf('1'..'7'))
        Assert.assertArrayEquals((('1'..'5') + ('3'..'7')).toTypedArray(), arrayOf('1'..'7'))
        Assert.assertArrayEquals((('3'..'7') + ('1'..'5')).toTypedArray(), arrayOf('1'..'7'))
        Assert.assertArrayEquals((('6'..'7') + ('1'..'5')).toTypedArray(), arrayOf('1'..'7'))
        Assert.assertArrayEquals((('1'..'2') + ('4'..'5')).toTypedArray(), arrayOf('1'..'2', '4'..'5'))
        Assert.assertArrayEquals((('4'..'5') + ('1'..'2')).toTypedArray(), arrayOf('4'..'5', '1'..'2'))
    }
}