package com.example.kotlinspirit

private const val NOT_FOUND = "not found"

class OneOfStringRule(
    strings: List<String>
) : Rule<String> {
    private val tree = TernaryStringTree(strings)

    override fun parse(state: ParseState) {
        state.startParseToken()
        val node = tree.search(state)
        if (node == null) {
            state.errorReason = NOT_FOUND
        }
    }

    override fun getResult(state: ParseState): String {
        return state.getToken()
    }
}

fun oneOf(vararg strings: String): OneOfStringRule {
    return OneOfStringRule(strings.toList())
}

fun oneOf(strings: List<String>): OneOfStringRule {
    return OneOfStringRule(strings)
}