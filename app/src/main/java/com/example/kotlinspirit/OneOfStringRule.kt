package com.example.kotlinspirit

class OneOfStringRule : RuleWithDefaultRepeat<CharSequence> {
    private val tree: TernarySearchTree
    private var beginSeek = -1

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
                stepCode = StepCode.COMPLETE
            )
        } else {
            createStepResult(
                seek = seek,
                stepCode = StepCode.ONE_OF_STRING_NOT_FOUND
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
                stepCode = StepCode.COMPLETE
            )
            result.data = string.subSequence(seek, r)
        } else {
            result.stepResult = createStepResult(
                seek = seek,
                stepCode = StepCode.ONE_OF_STRING_NOT_FOUND
            )
        }
    }

    override fun hasMatch(seek: Int, string: CharSequence): Boolean {
        return tree.hasMatch(seek, string)
    }

    override fun resetNoStep() {
        super.resetNoStep()
    }

    override fun resetStep() {
        tree.resetStep()
        beginSeek = -1
    }

    override fun getStepParserResult(string: CharSequence): CharSequence {
        return string.subSequence(beginSeek, tree.mayCompleteSeek)
    }

    override fun parseStep(seek: Int, string: CharSequence): Long {
        if (beginSeek < 0) {
            beginSeek = seek
        }

        return tree.parseStep(seek, string)
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

    override fun noParseStep(seek: Int, string: CharSequence): Long {
        TODO("Not yet implemented")
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