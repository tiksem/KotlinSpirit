package com.kotlinspirit

import com.kotlinspirit.core.Rule
import com.kotlinspirit.core.Rules
import com.kotlinspirit.core.Rules.bigDecimal
import com.kotlinspirit.core.Rules.double
import com.kotlinspirit.core.Rules.float
import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal

class ReverseFloatTest {
    private fun nameHavingFloatNumberBeforeIt(prefixRule: Rule<*>) {
        val name = Rules.char('A'..'Z') + +Rules.char('a'..'z')
        fun testWith(str: String, success: Boolean, expectedString: String? = null) {
            val r = name.requiresPrefix(prefixRule.failIf {
                val expected = (expectedString ?: str.replace("Albert", "")).let { s ->
                    when (prefixRule) {
                        float -> s.toFloatOrNull()
                        double -> s.toDoubleOrNull()
                        bigDecimal -> try {
                            BigDecimal(s)
                        } catch (e: Exception) {
                            null
                        }
                        else -> throw IllegalStateException("")
                    }
                }
                it != expected
            })
            val p = r.compile()
            Assert.assertEquals(
                if (success) "Albert" else null,
                p.findFirst(str)?.toString()
            )
        }

        testWith("232332Albert", success = true)
        testWith("+---Albert", success = false)
        testWith("+Albert", success = false)
        testWith("-Albert", success = false)
        testWith("-4434Albert", success = true)
        testWith("Albert", success = false)
        testWith(
            "43434343.4343243523645728368723645782364578236457823654e4344343434343-4344343Albert",
            success = true,
            expectedString = "-4344343"
        )
        testWith(
            ".4343e-43Albert",
            success = true
        )
        testWith(
            ".4343e43Albert",
            success = true
        )
        testWith(
            "4343e43Albert",
            success = true
        )
        testWith(
            "+4343e43Albert",
            success = true
        )
        testWith(
            "-4343e43Albert",
            success = true
        )
        testWith(
            "-4343.00+e43Albert",
            success = false
        )
        testWith(
            "-4343.00e-43Albert",
            success = true
        )
        testWith(
            ".3Albert",
            success = true
        )
        testWith(
            ".3e345Albert",
            success = true
        )
    }

    @Test
    fun test() {
        nameHavingFloatNumberBeforeIt(float)
        nameHavingFloatNumberBeforeIt(double)
        nameHavingFloatNumberBeforeIt(bigDecimal)
    }
}