package org.dit113group3.androidapp

import android.content.Intent
import android.graphics.Color
import android.widget.ImageButton
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess

class CreditsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)
        setupHyperlink()

        val exit = findViewById<ImageButton>(R.id.creditsExit)
        exit.setOnClickListener {
            val goBack = Intent (this, MainMenuActivity::class.java)
                startActivity(goBack)
                finish()
        }
    }

    private fun setupHyperlink() {
        val devBodyLink = findViewById<TextView>(R.id.devBody)
        devBodyLink.movementMethod = LinkMovementMethod.getInstance()
        devBodyLink.setLinkTextColor(Color.BLUE)

        val teacherBodyLink = findViewById<TextView>(R.id.teacherBody)
        teacherBodyLink.movementMethod = LinkMovementMethod.getInstance()
        teacherBodyLink.setLinkTextColor(Color.BLUE)

        val taBodyLink = findViewById<TextView>(R.id.taBody)
        taBodyLink.movementMethod = LinkMovementMethod.getInstance()
        taBodyLink.setLinkTextColor(Color.BLUE)
    }
}