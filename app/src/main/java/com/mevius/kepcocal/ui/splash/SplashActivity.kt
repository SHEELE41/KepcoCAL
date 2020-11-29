package com.mevius.kepcocal.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.mevius.kepcocal.R
import com.mevius.kepcocal.ui.main.MainActivity

class SplashActivity: AppCompatActivity() {
    private val splashDisplayLength = 500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }, splashDisplayLength)
    }

    override fun onBackPressed() {
        // We don't want the splash screen to be interrupted
    }
}