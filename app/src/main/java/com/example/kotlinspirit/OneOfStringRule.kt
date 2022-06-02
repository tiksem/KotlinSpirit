package com.example.kotlinspirit

class OneOfStringRule(
    strings: List<String>
) : StringRule() {
    private val tree = TernaryStringTree(strings)

    override fun createParseIterator(): ParseIterator<CharSequence> {
        return tree.getIterator()
    }
}