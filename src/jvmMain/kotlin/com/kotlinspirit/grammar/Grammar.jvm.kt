package com.kotlinspirit.grammar

import kotlin.RuntimeException

actual fun <T : Any> Grammar<T>.platformClone(): Grammar<T> {
    try {
        val constructor = javaClass.declaredConstructors[0]
        constructor.isAccessible = true
        return if (constructor.parameterCount == 0) {
            constructor.newInstance()
        } else {
            constructor.newInstance(
                *javaClass.declaredFields.sliceArray(0 until constructor.parameterCount).map {
                    it.isAccessible = true
                    it.get(this)
                }.toTypedArray()
            )
        } as Grammar<T>
    } catch (e: Exception) {
        throw RuntimeException("""
                Failed to clone grammar,  
                This might be caused by creating anonymous Grammar instance, capturing local variables.
                Try to avoid using anonymous Grammar class,  
                that captures local variables outside,  
                this might not be supported yet, instead create a separate Grammar class, 
                and pass them to constructor.
                
                Example:
                Not supported yet:
                
                fun grammarFactory(localVar1: Int, localVar2): Rule<*> {}
                    return object : Grammar<Int>() {
                        override val result: Int = 0
                        override fun defineRule(): Rule<Int> = /* */
                    }.toRule()
                }
                
                Supported:
                
                class MyGrammar(localVar1: Int, localVar2: Int) : Grammar<Int>() {
                    override val result: Int = 0
                    override fun defineRule(): Rule<Int> = /* */
                }
                
                fun grammarFactory(localVar1: Int, localVar2): Rule<Int>
                    return MyGrammar(localVar1, localVar2).toRule()
                    
                If this error was caused by other reasons, please report an issue.
                
                Alternatively, you can override clone() method in your 
                Grammar class and fix the issue for your own use case.
            """.trimIndent(), e)
    }
}