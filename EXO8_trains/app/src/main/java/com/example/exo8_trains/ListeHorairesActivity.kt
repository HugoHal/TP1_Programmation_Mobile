package com.example.exo8_trains

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Trajet(
    val depart: String,
    val arrivee: String,
    val horaires: List<String>
)

class ListeHorairesActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste)

        // 1. Récupérer les villes envoyées par l'Intent
        val villeD = intent.getStringExtra("DEPART") ?: ""
        val villeA = intent.getStringExtra("ARRIVEE") ?: ""

        findViewById<TextView>(R.id.txtTrajet).text = "$villeD ➔ $villeA"

        // 2. Lire le JSON des trajets
        val horairesTrouves = chercherHoraires(villeD, villeA)

        // 3. Afficher dans la ListView
        val listView = findViewById<ListView>(R.id.listeTrains)
        if (horairesTrouves.isNotEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, horairesTrouves)
            listView.adapter = adapter
        } else {
            // Optionnel : Afficher un message si aucun train n'existe pour ce trajet
            Toast.makeText(this, "Aucun train trouvé pour ce trajet", Toast.LENGTH_LONG).show()
        }

        findViewById<Button>(R.id.btnRetour).setOnClickListener { finish() }
    }

    private fun chercherHoraires(dep: String, arr: String): List<String> {
        return try {
            // Lecture du fichier
            val jsonString = assets.open("trajets.json").bufferedReader().use { it.readText() }

            // Conversion en liste d'objets Trajet
            val typeListe = object : TypeToken<List<Trajet>>() {}.type
            val tousLesTrajets: List<Trajet> = Gson().fromJson(jsonString, typeListe)

            // Filtrage : On cherche le trajet exact
            val trajetMatch = tousLesTrajets.find {
                it.depart.equals(dep, ignoreCase = true) && it.arrivee.equals(arr, ignoreCase = true)
            }

            trajetMatch?.horaires ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}