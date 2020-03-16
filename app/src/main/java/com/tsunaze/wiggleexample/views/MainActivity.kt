package com.tsunaze.wiggleexample.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tsunaze.wiggle.Wiggle
import com.tsunaze.wiggleexample.R
import com.tsunaze.wiggleexample.views.fragments.TestAFragment
import com.tsunaze.wiggleexample.views.fragments.TestBFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Wiggle.fragments = mutableListOf(
            TestAFragment.newInstance(),
            TestBFragment.newInstance()
        )
        Wiggle.create(this)
    }

    override fun onResume() {
        super.onResume()
        Wiggle.resume(this)
    }

    override fun onStop() {
        super.onStop()
        Wiggle.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Wiggle.destroy()
    }
}
