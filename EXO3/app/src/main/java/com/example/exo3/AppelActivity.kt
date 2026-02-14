package com.example.exo3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Button

class AppelActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appel)

        val numero = intent.getStringExtra("EXTRA_TEL")
        val txtNumero = findViewById<TextView>(R.id.txtNumero)
        txtNumero.text = numero

        findViewById<Button>(R.id.btnAppeler).setOnClickListener {
            // Création de l'Intent Implicite
            val intentAppel = Intent(Intent.ACTION_DIAL)
            // On précise le numéro au format URI
            intentAppel.data = android.net.Uri.parse("tel:$numero")
            startActivity(intentAppel)
        }
    }
}