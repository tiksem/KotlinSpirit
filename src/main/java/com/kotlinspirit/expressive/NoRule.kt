package com.kotlinspirit.expressive

import com.kotlinspirit.char.CharRule
import com.kotlinspirit.core.*
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import kotlin.math.max
import kotlin.math.min

/**
 * Matches one character, if it doesn't match the original rule.
 * If we are at the end of input, and the original rule doesn't match EOF, it outputs '\0' as a result
 */
class NoRule(
    private val rule: Rule<*>,
    name: String? = null
) : CharRule(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        val rResult = rule.parse(seek, string)
        val parseCode = rResult.parseCode
        return when (parseCode) {
            ParseCode.COMPLETE -> ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.NO_FAILED
            )
            else -> ParseSeekResult(min(seek + 1, string.length))
        }
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        val rResult = rule.parse(seek, string)
        val parseCode = rResult.parseCode
        when (parseCode) {
            ParseCode.COMPLETE -> {
                result.parseResult = ParseSeekResult(
                    seek = seek,
                    parseCode = ParseCode.NO_FAILED
                )
                result.data = null
            }
            else -> {
                val isEof = seek == string.length
                result.parseResult = ParseSeekResult(if (isEof) seek else seek + 1)
                result.data = if (isEof) 0.toChar() else string[seek]
            }
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return rule.parse(seek, string).isError
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        val rResult = rule.reverseParse(seek, string)
        val parseCode = rResult.parseCode
        return when (parseCode) {
            ParseCode.COMPLETE -> ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.NO_FAILED
            )
            else -> ParseSeekResult(max(seek - 1, -1))
        }
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        val rResult = rule.reverseParse(seek, string)
        val parseCode = rResult.parseCode
        when (parseCode) {
            ParseCode.COMPLETE -> {
                result.parseResult = ParseSeekResult(
                    seek = seek,
                    parseCode = ParseCode.NO_FAILED
                )
                result.data = null
            }
            else -> {
                val isEof = seek == -1
                result.parseResult = ParseSeekResult(if (isEof) seek else seek - 1)
                result.data = if (isEof) 0.toChar() else string[seek]
            }
        }
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        TODO("Not yet implemented")
    }

    override fun clone(): NoRule {
        return NoRule(rule.clone(), name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    override val defaultDebugName: String
        get() = "!${rule.wrappedName}"

    override fun debug(engine: DebugEngine): DebugRule<Char> {
        return DebugRule(
            rule = NoRule(rule.debug(engine), name),
            engine = engine
        )
    }

    override fun name(name: String): NoRule {
        return NoRule(rule, name)
    }

    override fun isThreadSafe(): Boolean {
        return rule.isThreadSafe()
    }
}