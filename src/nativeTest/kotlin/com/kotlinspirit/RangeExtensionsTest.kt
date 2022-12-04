package com.kotlinspirit

import com.kotlinspirit.ext.minus
import com.kotlinspirit.ext.plus
import org.junit.Assert
import org.junit.Test

class RangeExtensionsTest {
    @Test
    fun testMinus() {
        assertArrayEquals((('3'..'5') - ('3'..'5')).toTypedArray(), arrayOf())
        assertArrayEquals((('3'..'5') - ('2'..'6')).toTypedArray(), arrayOf())
        assertArrayEquals((('3'..'5') - ('3'..'6')).toTypedArray(), arrayOf())
        assertArrayEquals((('3'..'5') - ('2'..'5')).toTypedArray(), arrayOf())
        assertArrayEquals((('1'..'9') - ('3'..'5')).toTypedArray(), arrayOf('1'..'2', '6'..'9'))
        assertArrayEquals((('2'..'9') - ('0'..'5')).toTypedArray(), arrayOf('6'..'9'))
        assertArrayEquals((('2'..'9') - ('2'..'5')).toTypedArray(), arrayOf('6'..'9'))
        assertArrayEquals((('0'..'8') - ('7'..'9')).toTypedArray(), arrayOf('0'..'6'))
        assertArrayEquals((('0'..'8') - ('7'..'8')).toTypedArray(), arrayOf('0'..'6'))
    }

    @Test
    fun testPlus() {
        assertArrayEquals((('3'..'5') + ('3'..'5')).toTypedArray(), arrayOf('3'..'5'))
        assertArrayEquals((('1'..'5') + ('2'..'4')).toTypedArray(), arrayOf('1'..'5'))
        assertArrayEquals((('1'..'5') + ('1'..'4')).toTypedArray(), arrayOf('1'..'5'))
        assertArrayEquals((('1'..'5') + ('2'..'5')).toTypedArray(), arrayOf('1'..'5'))
        assertArrayEquals((('1'..'5') + ('6'..'7')).toTypedArray(), arrayOf('1'..'7'))
        assertArrayEquals((('1'..'5') + ('3'..'7')).toTypedArray(), arrayOf('1'..'7'))
        assertArrayEquals((('3'..'7') + ('1'..'5')).toTypedArray(), arrayOf('1'..'7'))
        assertArrayEquals((('6'..'7') + ('1'..'5')).toTypedArray(), arrayOf('1'..'7'))
        assertArrayEquals((('1'..'2') + ('4'..'5')).toTypedArray(), arrayOf('1'..'2', '4'..'5'))
        assertArrayEquals((('4'..'5') + ('1'..'2')).toTypedArray(), arrayOf('4'..'5', '1'..'2'))
    }

    @Test
    fun test1() {
        assertArrayEquals((('a'..'f') - ('z'..'z')).toTypedArray(), arrayOf('a'..'f'))
    }
}