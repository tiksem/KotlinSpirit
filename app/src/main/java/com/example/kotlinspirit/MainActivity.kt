package com.example.kotlinspirit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val numbers = int % ','
        val result = numbers.parseOrThrow("123,3443,443,1234,5454,232323")
        Log.d("yoyoyo", result.joinToString(","))
    }
}