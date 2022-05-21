package com.example.kotlinspirit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val identifier = char(
            ranges = arrayOf('a'..'z', 'A'..'Z'),
            chars = charArrayOf('_')
        ) + str(
            ranges = arrayOf('a'..'z', 'A'..'Z', '0'..'9'),
            chars = charArrayOf('_')
        )[1..Int.MAX_VALUE]

        val email = identifier + '@' + identifier + '.' + identifier
        Log.d("yoyoyo", email.match("_semyo23423ntikhonenko@gmail.com").toString())
    }
}