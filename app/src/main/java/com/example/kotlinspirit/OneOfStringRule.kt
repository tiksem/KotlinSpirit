package com.example.kotlinspirit

private const val NOT_FOUND = "not found"

class OneOfStringRule(
    strings: List<String>
) : StringRule() {
    private val tree = TernaryStringTree(strings)

    override fun createParseIterator(): ParseIterator<CharSequence> {
        return tree.getIterator()
    }
}

fun oneOf(vararg strings: String): OneOfStringRule {
    return OneOfStringRule(strings.toList())
}

fun oneOf(strings: List<String>): OneOfStringRule {
    return OneOfStringRule(strings)
}