package com.tsunaze.wiggle.views

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.tsunaze.wiggle.R
import com.tsunaze.wiggle.Wiggle
import com.tsunaze.wiggle.views.adapters.WiggleAdapter
import kotlinx.android.synthetic.main.wiggle_activity.*


class WiggleActivity : AppCompatActivity() {

    var adapter: WiggleAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        initWindow()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wiggle_activity)
        initViewPager()
        initClickListeners()
    }

    private fun initWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.requestFeature(Window.FEATURE_ACTION_BAR)
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun initImmersiveMode() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
    }

    override fun onResume() {
        super.onResume()
        initImmersiveMode()
    }

    private fun initViewPager() {
        val fragments = Wiggle.fragments
        if (fragments?.isNotEmpty() == true) {
            adapter = WiggleAdapter(supportFragmentManager, fragments)
            wigglePager.adapter = adapter
        }
    }

    private fun initClickListeners() {
        wiggleClose.bringToFront()
        wiggleClose.setOnClickListener {
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}