package com.example.kotlinspirit

class OneOfStringRule internal constructor(private val strings: List<CharSequence>) :
    RuleWithDefaultRepeat<CharSequence>() {

    private var tree: TernarySearchTree? = null

    private constructor(strings: List<CharSequence>, tree: TernarySearchTree?) : this(strings) {
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
        var i = seek
        val length = string.length
        while (i < length) {
            if (!getTree().hasMatch(i, string)) {
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
        return OneOfStringRule(strings, tree)
    }
}