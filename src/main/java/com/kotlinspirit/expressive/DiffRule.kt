package com.kotlinspirit.expressive

import com.kotlinspirit.char.CharRule
import com.kotlinspirit.core.*
import com.kotlinspirit.core.createStepResult
import com.kotlinspirit.debug.DebugEngine
import com.kotlinspirit.debug.DebugRule
import com.kotlinspirit.repeat.RuleWithDefaultRepeat

private fun internalParse(seek: Int, string: CharSequence, main: Rule<*>, diff: Rule<*>): Long {
    val mainRes = main.parse(seek, string)
    return if (mainRes.getParseCode().isError()) {
        mainRes
    } else {
        if (diff.hasMatch(seek, string)) {
            return createStepResult(
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
    if (stepResult.getParseCode().isNotError()) {
        if (diff.hasMatch(seek, string)) {
            result.parseResult = createStepResult(
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
    override fun parse(seek: Int, string: CharSequence): Long {
        return internalParse(seek, string, main, diff)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<T>) {
        return internalParseWithResult(seek, string, result, main, diff)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return internalHasMatch(seek, string, main, diff)
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

    override fun isDynamic(): Boolean {
        return main.isDynamic() || diff.isDynamic()
    }

    override fun ignoreCallbacks(): DiffRuleWithDefaultRepeat<T> {
        return DiffRuleWithDefaultRepeat(main.ignoreCallbacks(), diff.ignoreCallbacks())
    }
}

class CharDiffRule(
    protected val main: Rule<Char>,
    protected val diff: Rule<*>,
    name: String? = null
) : CharRule(name) {
    override fun parse(seek: Int, string: CharSequence): Long {
        return internalParse(seek, string, main, diff)
    }

    override fun parseWithResult(seek: Int, string: CharSequence, result: ParseResult<Char>) {
        return internalParseWithResult(seek, string, result, main, diff)
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return internalHasMatch(seek, string, main, diff)
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

    override fun isDynamic(): Boolean {
        return main.isDynamic() || diff.isDynamic()
    }

    override fun ignoreCallbacks(): CharDiffRule {
        return CharDiffRule(main.ignoreCallbacks(), diff.ignoreCallbacks())
    }
}

