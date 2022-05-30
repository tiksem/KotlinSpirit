package com.example.kotlinspirit

data class ParseResult<T>(
    val seek: Int,
    val code: Int,
    val result: T?
) {
    val hasError
        get() = code.isError()

    companion object {
        fun <T> result(seek: Int, result: T): ParseResult<T> {
            return ParseResult(
                seek = seek,
                result = result,
                code = StepCode.COMPLETE
            )
        }

        fun <T> error(seek: Int, errorCode: Int): ParseResult<T> {
            return ParseResult(
                seek = seek,
                result = null,
                code = errorCode
            )
        }
    }
}