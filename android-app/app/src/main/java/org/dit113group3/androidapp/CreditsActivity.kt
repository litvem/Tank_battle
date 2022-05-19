package org.dit113group3.androidapp

import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CreditsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)
        setupHyperlink()
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