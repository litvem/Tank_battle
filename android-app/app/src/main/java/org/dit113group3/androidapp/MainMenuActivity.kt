package org.dit113group3.androidapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.exit
import kotlinx.android.synthetic.main.dialog_view_main_menu.view.*
import kotlin.system.exitProcess

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val play = findViewById<Button>(R.id.playButton)
        play.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val rules = findViewById<Button>(R.id.rulesButton)
        rules.setOnClickListener {
            val intent = Intent(this, RulesActivity::class.java)
            startActivity(intent)
            finish()
        }

        val credits = findViewById<Button>(R.id.creditsButton)
        credits.setOnClickListener {
            val intent = Intent(this, CreditsActivity::class.java)
            startActivity(intent)
            finish()
        }

        exit.setOnClickListener {
            val view = View.inflate(this, R.layout.dialog_view_main_menu, null)

            val builder = AlertDialog.Builder(this)
            builder.setView(view)

            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            view.mainMenuNo.setOnClickListener {
                dialog.dismiss()
            }

            view.mainMenuYes.setOnClickListener {
                finish()
                exitProcess(0)
            }
        }
    }
}
