package com.kotlinspirit.core

internal fun Int.parseCodeToString(): String {
    return when (this) {
        ParseCode.COMPLETE -> "COMPLETE"
        ParseCode.EOF -> "EOF"
        ParseCode.INVALID_INT -> "INVALID_INT"
        ParseCode.INVALID_UINT -> "INVALID_UINT"
        ParseCode.INVALID_LONG -> "INVALID_LONG"
        ParseCode.INVALID_ULONG -> "INVALID_ULONG"
        ParseCode.INT_OUT_OF_BOUNDS -> "INT_OUT_OF_BOUNDS"
        ParseCode.LONG_OUT_OF_BOUNDS -> "LONG_OUT_OF_BOUNDS"
        ParseCode.UINT_OUT_OF_BOUNDS -> "UINT_OUT_OF_BOUNDS"
        ParseCode.ULONG_OUT_OF_BOUNDS -> "ULONG_OUT_OF_BOUNDS"
        ParseCode.INVALID_BYTE -> "INVALID_BYTE"
        ParseCode.INVALID_UBYTE -> "INVALID_UBYTE"
        ParseCode.INVALID_SHORT -> "INVALID_SHORT"
        ParseCode.INVALID_USHORT -> "INVALID_USHORT"
        ParseCode.SHORT_OUT_OF_BOUNDS -> "SHORT_OUT_OF_BOUNDS"
        ParseCode.USHORT_OUT_OF_BOUNDS -> "USHORT_OUT_OF_BOUNDS"
        ParseCode.DIFF_FAILED -> "DIFF_FAILED"
        ParseCode.CHAR_PREDICATE_FAILED -> "CHAR_PREDICATE_FAILED"
        ParseCode.STRING_NOT_ENOUGH_DATA -> "STRING_NOT_ENOUGH_DATA"
        ParseCode.STRING_DOES_NOT_MATCH -> "STRING_NOT_ENOUGH_DATA"
        ParseCode.NO_FAILED -> "NO_FAILED"
        ParseCode.INVALID_DOUBLE -> "INVALID_DOUBLE"
        ParseCode.INVALID_FLOAT -> "INVALID_FLOAT"
        ParseCode.WHOLE_STRING_DOES_NOT_MATCH -> "WHOLE_STRING_DOES_NOT_MATCH"
        ParseCode.ONE_OF_STRING_NOT_FOUND -> "ONE_OF_STRING_NOT_FOUND"
        ParseCode.FAIL_PREDICATE -> "FAIL_PREDICATE"
        ParseCode.SUFFIX_EXPECTATION_FAILED -> "SUFFIX_EXPECTATION_FAILED"
        ParseCode.INVALID_BIG_INTEGER -> "INVALID_BIG_INTEGER"
        ParseCode.INVALID_BIG_DECIMAL -> "INVALID_BIG_DECIMAL"
        ParseCode.BIG_DECIMAL_EXPONENT_OVERFLOW -> "BIG_DECIMAL_EXPONENT_OVERFLOW"
        ParseCode.BYTE_OUT_OF_BOUNDS -> "BYTE_OUT_OF_BOUNDS"
        ParseCode.UBYTE_OUT_OF_BOUNDS -> "UBYTE_OUT_OF_BOUNDS"
        ParseCode.NO_EOF -> "NO_EOF"
        ParseCode.BOOLEAN_NO_MATCH -> "BOOLEAN_NO_MATCH"
        ParseCode.REGEX_NO_MATCH -> "REGEX_NO_MATCH"
        ParseCode.SPLIT_NOT_ENOUGH_DATA -> "SPLIT_NOT_ENOUGH_DATA"
        ParseCode.NOT_START_OF_LINE -> "NOT_START_OF_LINE"
        
        else -> if (this > ParseCode.PREFIX_NOT_SATISFIED) {
            "PREFIX_NOT_SATISFIED_" + (this - ParseCode.PREFIX_NOT_SATISFIED).parseCodeToString()
        } else {
            "UNKNOWN_ERROR"
        }
    }
}

@JvmInline
value class ParseSeekResult(
    private val value: Long
) {
    constructor(seek: Int, parseCode: Int = ParseCode.COMPLETE) :
            this(seek.toLong() shl 32 or (parseCode.toLong() and 0xFFFFFFFFL))

    val errorCode: Int
        get() {
            val stepCode = parseCode
            return if (parseCode != ParseCode.COMPLETE) {
                stepCode
            } else {
                -1
            }
        }

    val parseCodeString: String
        get() = parseCode.parseCodeToString()

    val parseCode: Int
        get() = value.toInt()

    val isError: Boolean
        get() = parseCode != ParseCode.COMPLETE

    val isComplete: Boolean
        get() = parseCode == ParseCode.COMPLETE

    val seek: Int
        get() = (value shr 32).toInt()
}