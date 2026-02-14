package com.example.exo3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Button

class AffichageActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_affichage)

        val nom = intent.getStringExtra("EXTRA_NOM")
        val prenom = intent.getStringExtra("EXTRA_PRENOM")
        val age = intent.getStringExtra("EXTRA_AGE")
        val tel = intent.getStringExtra("EXTRA_TEL")
        val comp = intent.getStringExtra("EXTRA_COMP")

        findViewById<TextView>(R.id.resNom).text = nom
        findViewById<TextView>(R.id.resPrenom).text = prenom
        findViewById<TextView>(R.id.resAge).text = age
        findViewById<TextView>(R.id.resComp).text = comp
        findViewById<TextView>(R.id.resTel).text = tel

        findViewById<Button>(R.id.btnOK).setOnClickListener {
            val intent3 = Intent(this, AppelActivity::class.java)
            intent3.putExtra("EXTRA_TEL", tel)
            startActivity(intent3)
        }

        val btnRetour = findViewById<Button>(R.id.btnRetour)
        btnRetour.setOnClickListener {
            finish()
        }
    }
}