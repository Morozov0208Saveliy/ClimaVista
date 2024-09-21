package com.example.climavista.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.climavista.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            setContentView(R.layout.activity_main)

        }
    }
}
