package com.example.exo8_trains

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : Activity() {
    private lateinit var editDepart: AutoCompleteTextView
    private lateinit var editArrivee: AutoCompleteTextView
    private lateinit var listeVilles: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainpage)


        editDepart = findViewById(R.id.editDepart) // Garde tes IDs actuels du XML
        editArrivee = findViewById(R.id.editArrivee)
        val btnValider = findViewById<Button>(R.id.btnValider)

        try {

            val villesJson = assets.open("villes.json").bufferedReader().use { it.readText() }
            val typeListe = object : TypeToken<List<String>>() {}.type
            listeVilles = Gson().fromJson(villesJson, typeListe)

            val adapter = CityAdapter(this, listeVilles)

            editDepart.setAdapter(adapter)
            editArrivee.setAdapter(adapter)

        } catch (e: Exception) {
            e.printStackTrace()
            listeVilles = emptyList()
        }


        btnValider.setOnClickListener {
            onValiderClick()
        }
    }

    private fun onValiderClick() {
        val villeD = editDepart.text.toString()
        val villeA = editArrivee.text.toString()

        // On vérifie si les villes saisies existent dans notre liste JSON
        if (villeD in listeVilles && villeA in listeVilles) {
            val intent = Intent(this, ListeHorairesActivity::class.java)
            intent.putExtra("DEPART", villeD)
            intent.putExtra("ARRIVEE", villeA)
            startActivity(intent)
        } else {
            android.app.AlertDialog.Builder(this)
                .setTitle("Erreur de saisie")
                .setMessage("Veuillez sélectionner des villes valides dans la liste.")
                .setPositiveButton("OK", null)
                .show()
        }
    }
}