package com.lukaszgalinski.gamefuture

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.splash_screen.*
import java.util.*

private const val SPLASH_SCREEN_DEFAULT_TIME = 3000L
private val screenChangeHandler = Handler()
private var timeLeft: Long = 0
private var startTime: Long = 0
private var TIME_LEFT_LABEL = "splashTime"
class SplashScreenActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        timeLeft = savedInstanceState?.getLong(TIME_LEFT_LABEL) ?: SPLASH_SCREEN_DEFAULT_TIME
        setShadowOnTextButton(splash_skip)
    }

    private fun setShadowOnTextButton(button: Button){
        button.setOnClickListener { activityClean() }
    }

    private fun changeScreenTask(timePeriod: Long) {
        screenChangeHandler.postDelayed({
            activityClean()
        }, timePeriod)
    }

    private fun activityClean(){
        finish()
        startActivity(Intent(this, MainMenuActivity::class.java))
        screenChangeHandler.removeCallbacksAndMessages(null)
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