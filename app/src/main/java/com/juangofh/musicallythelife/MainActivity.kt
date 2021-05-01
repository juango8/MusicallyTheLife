package com.juangofh.musicallythelife

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        Thread.sleep(2000)
        setTheme(R.style.Theme_MusicallyTheLife)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}