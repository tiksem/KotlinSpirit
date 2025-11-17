package com.kotlinspirit.expressive

import com.kotlinspirit.char.CharRule
import com.kotlinspirit.core.*
import com.kotlinspirit.core.ParseSeekResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

private fun internalParse(seek: Int, string: CharSequence, main: Rule<*>, diff: Rule<*>): ParseSeekResult {
    val mainRes = main.parse(seek, string)
    return if (mainRes.isError) {
        mainRes
    } else {
        if (diff.hasMatch(seek, string)) {
            return ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.DIFF_FAILED
            )
        } else {
            mainRes
        }
    }
}

private fun <T : Any> internalParseWithResult(
    seek: Int, string: CharSequence, result: ParseResult<T>, main: Rule<T>, diff: Rule<*>
) {
    main.parseWithResult(seek, string, result)
    val stepResult = result.parseResult
    if (stepResult.isComplete) {
        if (diff.hasMatch(seek, string)) {
            result.parseResult = ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.DIFF_FAILED
            )
            return
        }
    }
}

private fun internalHasMatch(
    seek: Int, string: CharSequence, main: Rule<*>, diff: Rule<*>
): Boolean {
    return main.hasMatch(seek, string) && !diff.hasMatch(seek, string)
}

private fun internalReverseParse(seek: Int, string: CharSequence, main: Rule<*>, diff: Rule<*>): ParseSeekResult {
    val mainRes = main.reverseParse(seek, string)
    return if (mainRes.isError) {
        mainRes
    } else {
        if (diff.hasMatch(mainRes.seek + 1, string)) {
            return ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.DIFF_FAILED
            )
        } else {
            mainRes
        }
    }
}

private fun internalReverseHasMatch(
    seek: Int, string: CharSequence, main: Rule<*>, diff: Rule<*>
): Boolean {
    return internalReverseParse(seek, string, main, diff).isComplete
}

private fun <T : Any> internalReverseParseWithResult(
    seek: Int, string: CharSequence, result: ParseResult<T>, main: Rule<T>, diff: Rule<*>
) {
    main.reverseParseWithResult(seek, string, result)
    val stepResult = result.parseResult
    if (stepResult.isComplete) {
        if (diff.hasMatch(result.endSeek + 1, string)) {
            result.parseResult = ParseSeekResult(
                seek = seek,
                parseCode = ParseCode.DIFF_FAILED
            )
            return
        }
    }
}

private fun generateDebugName(main: Rule<*>, diff: Rule<*>): String {
    val mainName = main.wrappedName
    val diffName = diff.wrappedName
    return "$mainName-$diffName"
}

class DiffRuleWithDefaultRepeat<T : Any>(
    protected val main: Rule<T>,
    protected val diff: Rule<*>,
    name: String? = null
) : RuleWithDefaultRepeat<T>(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        return internalParse(seek, string, main, diff)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        return internalParseWithResult(seek, string, result, main, diff)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return internalHasMatch(seek, string, main, diff)
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        return internalReverseParse(seek, string, main, diff)
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        internalParseWithResult(seek, string, result, main, diff)
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return internalReverseHasMatch(seek, string, main, diff)
    }

    override fun clone(): DiffRuleWithDefaultRepeat<T> {
        return DiffRuleWithDefaultRepeat(main.clone(), diff.clone(), name)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = true

    override fun debug(engine: DebugEngine): DebugRule<T> {
        val rule = DiffRuleWithDefaultRepeat(main.debug(engine), diff.debug(engine), name)
        return DebugRule(rule = rule, engine = engine)
    }

    override fun name(name: String): Rule<T> {
        return DiffRuleWithDefaultRepeat(main, diff, name)
    }

    override val defaultDebugName: String
        get() = generateDebugName(main, diff)

    override fun isThreadSafe(): Boolean {
        return main.isThreadSafe() && diff.isThreadSafe()
    }
}

class CharDiffRule(
    protected val main: Rule<Char>,
    protected val diff: Rule<*>,
    name: String? = null
) : CharRule(name) {
    override fun parse(seek: Int, string: CharSequence): ParseSeekResult {
        return internalParse(seek, string, main, diff)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        return internalParseWithResult(seek, string, result, main, diff)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return internalHasMatch(seek, string, main, diff)
    }

    override fun reverseParse(seek: Int, string: CharSequence): ParseSeekResult {
        return internalReverseParse(seek, string, main, diff)
    }

    override fun reverseParseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        internalParseWithResult(seek, string, result, main, diff)
    }

    override fun reverseHasMatch(seek: Int, string: CharSequence): Boolean {
        return internalReverseHasMatch(seek, string, main, diff)
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = true

    override fun clone(): CharDiffRule {
        return CharDiffRule(main.clone(), diff.clone(), name)
    }

    override fun debug(engine: DebugEngine): DebugRule<Char> {
        return DebugRule(
            rule = CharDiffRule(main.debug(engine), diff.debug(engine), name),
            engine = engine
        )
    }

    override fun name(name: String): CharDiffRule {
        return CharDiffRule(main, diff, name)
    }

    override val defaultDebugName: String
        get() = generateDebugName(main, diff)

    override fun isThreadSafe(): Boolean {
        return main.isThreadSafe() && diff.isThreadSafe()
    }
}

