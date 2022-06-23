package com.example.kotlinspirit

class OneOfStringRule : RuleWithDefaultRepeat<CharSequence> {
    private val tree: TernarySearchTree

    constructor(strings: List<CharSequence>) {
        tree = TernarySearchTree(strings)
    }

    private constructor(tree: TernarySearchTree) {
        this.tree = tree.clone()
    }

    override fun parse(seek: Int, string: CharSequence): Long {
        val result = tree.parse(seek, string)
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
        val r = tree.parse(seek, string)
        if (r >= 0) {
            result.stepResult = createStepResult(
                seek = r,
                parseCode = ParseCode.COMPLETE
            )
            result.data = string.subSequence(seek, r)
        } else {
            result.stepResult = createStepResult(
                seek = seek,
                parseCode = ParseCode.ONE_OF_STRING_NOT_FOUND
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return tree.hasMatch(seek, string)
    }

    override fun noParse(seek: Int, string: CharSequence): Int {
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
            -seek
        }
    }

    infix fun or(string: String): OneOfStringRule {
        return OneOfStringRule(listOf(string) + tree.strings)
    }

    infix fun or(anotherRule: ExactStringRule): OneOfStringRule {
        return OneOfStringRule(listOf(anotherRule.string) + tree.strings)
    }

    infix fun or(anotherRule: OneOfStringRule): OneOfStringRule {
        return OneOfStringRule(anotherRule.tree.strings + tree.strings)
    }

    override fun clone(): OneOfStringRule {
        return OneOfStringRule(tree)
    }
}