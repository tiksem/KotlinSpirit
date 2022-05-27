package com.example.kotlinspirit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val name = str('a'..'z')
        val rule = (name - str("eblo")) % "eblo"
        val result = rule.parseOrThrow("privetebloprivet")
        Log.d("yoyoyo", result.joinToString(","))
    }
}