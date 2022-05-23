package org.dit113group3.androidapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import kotlin.system.exitProcess

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val play = findViewById<ImageButton>(R.id.play)
        play.setOnClickListener {
            val Intent = Intent(this, MainActivity::class.java)
            startActivity(Intent)
        }

        val rules = findViewById<ImageButton>(R.id.rules)
        rules.setOnClickListener {
            val intent = Intent(this, RulesActivity::class.java)
            startActivity(intent)
        }

        val credits = findViewById<ImageButton>(R.id.credits)
        credits.setOnClickListener {
            val Intent = Intent(this, CreditsActivity::class.java)
            startActivity(Intent)
        }

        val exit = findViewById<ImageButton>(R.id.exit)
        exit.setOnClickListener {
            val eBuilder = AlertDialog.Builder(this)
            eBuilder.setTitle("Exit")
            eBuilder.setIcon(R.drawable.ic_action_name)
            eBuilder.setMessage("Are you sure you want to exit the game ?")
            eBuilder.setPositiveButton("EXIT") { Dialog, which ->
                finish()
                exitProcess(0)
            }

            eBuilder.setNegativeButton("CANCEL") { dialog, which ->
            }
            val createBuild = eBuilder.create()
            createBuild.show()
        }
    }
}