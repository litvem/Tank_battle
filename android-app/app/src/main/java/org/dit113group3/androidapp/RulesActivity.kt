package org.dit113group3.androidapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import kotlin.system.exitProcess

class RulesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)

        val exit = findViewById<ImageButton>(R.id.exit)
        exit.setOnClickListener {
            val goBack = Intent (this, MainMenuActivity::class.java)
            startActivity(goBack)
            finish()
        }
    }
}