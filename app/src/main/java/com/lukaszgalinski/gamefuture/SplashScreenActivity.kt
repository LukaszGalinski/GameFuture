package com.lukaszgalinski.gamefuture

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import java.util.*

private const val SPLASH_SCREEN_DEFAULT_TIME = 5000L
private val screenChangeHandler = Handler()
private var timeLeft: Long = 0
private var startTime: Long = 0
private var TIME_LEFT_LABEL = "splashTime"
class SplashScreenActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        timeLeft = savedInstanceState?.getLong(TIME_LEFT_LABEL) ?: SPLASH_SCREEN_DEFAULT_TIME
    }

    private fun changeScreenTask(timePeriod: Long) {
        screenChangeHandler.postDelayed({
            finish()
            startActivity(Intent(this, MainMenuActivity::class.java))
        }, timePeriod)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        screenChangeHandler.removeCallbacksAndMessages(null)
        val timeEnd = Calendar.getInstance().timeInMillis
        timeLeft -= (timeEnd - startTime)
        outState.putLong(TIME_LEFT_LABEL, timeLeft)
    }

    override fun onResume() {
        super.onResume()
        startTime = Calendar.getInstance().timeInMillis
        changeScreenTask(timeLeft)
    }
}