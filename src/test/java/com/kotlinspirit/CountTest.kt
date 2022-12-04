package com.kotlinspirit

import com.kotlinspirit.core.Rules.int
import com.kotlinspirit.ext.count
import org.junit.Assert
import org.junit.Test

class CountTest {
    @Test
    fun countZero() {
        Assert.assertEquals(0, "sddsdsds ajsfdhhjds asdjfhgashjd sdhfshjd".count(int))
    }

    @Test
    fun count1() {
        Assert.assertEquals(1, "0sddsdsds ajsfdhhjds asdjfhgashjd sdhfshjd".count(int))
    }

    @Test
    fun count2() {
        Assert.assertEquals(2, "0sddsdsds ajsfdhhjds asdjfhgashjd 0343434 sdhfshjd".count(int))
    }

    @Test
    fun countMany() {
        Assert.assertEquals(3, "0sddsdsds 3434 ajsfdhhjds asdjfhgashjd sdhfshjd-4545".count(int))
    }
}