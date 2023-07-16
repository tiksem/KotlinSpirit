package com.kotlinspirit.core

object ParseCode {
    const val COMPLETE = 0
    const val EOF = 1
    const val INVALID_INT = 2
    const val INVALID_UINT = 3
    const val INVALID_LONG = 4
    const val INVALID_ULONG = 5
    const val INVALID_SHORT = 6
    const val INVALID_USHORT = 7
    const val INVALID_BYTE = 8
    const val INVALID_UBYTE = 9
    const val INT_OUT_OF_BOUNDS = 10
    const val UINT_OUT_OF_BOUNDS = 11
    const val SHORT_OUT_OF_BOUNDS = 12
    const val USHORT_OUT_OF_BOUNDS = 13
    const val LONG_OUT_OF_BOUNDS = 14
    const val ULONG_OUT_OF_BOUNDS = 15
    const val BYTE_OUT_OF_BOUNDS = 16
    const val UBYTE_OUT_OF_BOUNDS = 17
    const val DIFF_FAILED = 18
    const val CHAR_PREDICATE_FAILED = 19
    const val STRING_NOT_ENOUGH_DATA = 20
    const val STRING_DOES_NOT_MATCH = 21
    const val NO_FAILED = 22
    const val INVALID_DOUBLE = 23
    const val INVALID_FLOAT = 24
    const val WHOLE_STRING_DOES_NOT_MATCH = 25
    const val ONE_OF_STRING_NOT_FOUND = 26
    const val FAIL_PREDICATE = 27
    const val SUFFIX_EXPECTATION_FAILED = 28
    const val SPLIT_NOT_ENOUGH_DATA = 29
    const val INVALID_BIG_INTEGER = 30
    const val INVALID_BIG_DECIMAL = 31
    const val BIG_DECIMAL_EXPONENT_OVERFLOW = 32
    const val NO_EOF = 33
    const val BOOLEAN_NO_MATCH = 34
    const val REGEX_NO_MATCH = 35
    const val PREFIX_NOT_SATISFIED = 3000
}
