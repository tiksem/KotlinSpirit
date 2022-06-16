package com.example.kotlinspirit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import it.unimi.dsi.fastutil.chars.CharArrays

interface A {
    operator fun invoke(predicate: () -> Unit): AWithPredicate {
        return AWithPredicate(this, predicate)
    }

    operator fun plus(a: A): Sequence {
        return Sequence(this, a)
    }

    fun s(): String
}

class B : A {
    override fun s(): String {
        return "B"
    }
}

class AWithPredicate(
    private val a: A,
    private val predicate: () -> Unit
): A {
    override fun s(): String {
        return "AWithPredicate(${a.s()})"
    }

}

class Sequence(
    private val a: A,
    private val b: A
) : A {
    override fun s(): String {
        return a.s() + "+" + b.s()
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) }
}