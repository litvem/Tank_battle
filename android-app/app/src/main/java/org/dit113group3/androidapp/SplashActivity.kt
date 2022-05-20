package org.dit113group3.androidapp

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    var tank: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        tank = findViewById<ImageView>(R.id.tank)
        val moveTank =
            AnimationUtils.loadAnimation(getApplicationContext(), R.anim.tank_start_animation)
        tank!!.animation = moveTank
        val intent = Intent(this, MainMenuActivity::class.java)
        moveTank.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                finish()
                startActivity(intent)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }
}