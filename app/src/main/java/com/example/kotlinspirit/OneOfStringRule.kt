package com.example.kotlinspirit

open class OneOfStringRule internal constructor(private val strings: List<CharSequence>) :
    RuleWithDefaultRepeat<CharSequence>() {

    private var tree: TernarySearchTree? = null

    internal constructor(strings: List<CharSequence>, tree: TernarySearchTree?) : this(strings) {
        this.tree = tree
    }

    private fun getTree(): TernarySearchTree {
        return tree ?: TernarySearchTree(strings).also {
            tree = it
        }
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        val result = getTree().parse(seek, string)
        return if (result >= 0) {
            createStepResult(
                seek = result,
                parseCode = ParseCode.COMPLETE
            )
        } else {
            createStepResult(
                seek = seek,
                parseCode = ParseCode.ONE_OF_STRING_NOT_FOUND
            )
        }
    }

    override fun parseWithResult(
        seek: Int,
        string: CharSequence,
        result: ParseResult<CharSequence>
    ) {
        val r = getTree().parse(seek, string)
        if (r >= 0) {
            result.parseResult = createStepResult(
                seek = r,
                parseCode = ParseCode.COMPLETE
            )
            result.data = string.subSequence(seek, r)
        } else {
            result.parseResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.ONE_OF_STRING_NOT_FOUND
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return getTree().hasMatch(seek, string)
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
        val tree = getTree()
        var i = seek
        val length = string.length
        while (i < length) {
            if (!tree.hasMatch(i, string)) {
                i++
            } else {
                break
            }
        }

        return if (i != seek) {
            i
        } else {
            -seek - 1
        }
    }

    infix fun or(string: String): OneOfStringRule {
        return OneOfStringRule(listOf(string) + strings)
    }

    infix fun or(anotherRule: ExactStringRule): OneOfStringRule {
        return OneOfStringRule(listOf(anotherRule.string) + strings)
    }

    infix fun or(anotherRule: OneOfStringRule): OneOfStringRule {
        return OneOfStringRule(anotherRule.strings + strings)
    }

    override fun clone(): OneOfStringRule {
        return this
    }

    override val debugNameShouldBeWrapped: Boolean
        get() = false

    private fun generateDebugName(): String {
        return strings.joinToString("|") {
            it.toString().replace("|", "`|`")
        }
    }

    override fun debug(name: String?): OneOfStringRule {
        return DebugOneOfStringRule(name ?: generateDebugName(), strings, tree)
    }
}

private class DebugOneOfStringRule(
    override val name: String,
    strings: List<CharSequence>,
    tree: TernarySearchTree?
) : OneOfStringRule(strings, tree), DebugRule {
    override fun parse(seek: Int, string: CharSequence): Long {
        DebugEngine.ruleParseStarted(this, seek)
        return super.parse(seek, string).also {
            DebugEngine.ruleParseEnded(this, it)
        }
    }

    override fun parseWithResult(
        seek: Int, string: CharSequence, result: ParseResult<CharSequence>
    ) {
        DebugEngine.ruleParseStarted(this, seek)
        super.parseWithResult(seek, string, result)
        DebugEngine.ruleParseEnded(this, result.parseResult)
    }
}