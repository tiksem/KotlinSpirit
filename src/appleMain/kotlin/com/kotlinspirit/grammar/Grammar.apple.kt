package com.kotlinspirit.grammar

actual fun <T : Any> Grammar<T>.platformClone(): Grammar<T> {
    throw UnsupportedOperationException(
        """
        Auto clone is not supported on Apple platforms.  
        You have to override clone() method in your Grammar subclass.
        """.trimIndent()
    )
}