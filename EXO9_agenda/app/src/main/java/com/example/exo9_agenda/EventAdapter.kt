package com.example.exo9_agenda

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView

class EventAdapter(context: Context, private val events: List<Evenement>) :
    ArrayAdapter<Evenement>(context, 0, events) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // 1. Récupération de la vue (recyclage ou création)
        val itemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_event, parent, false)

        // 2. Récupération de l'événement actuel
        val currentEvent = getItem(position)

        // 3. Récupération des composants du layout item_event.xml
        val txtNom = itemView.findViewById<TextView>(R.id.itemNom)
        val txtHeure = itemView.findViewById<TextView>(R.id.itemHeure)
        val txtLieu = itemView.findViewById<TextView>(R.id.itemLieu)
        val container = itemView.findViewById<LinearLayout>(R.id.containerBulle)

        // 4. Remplissage des données
        currentEvent?.let { evt ->
            txtNom.text = evt.nom
            txtHeure.text = evt.heure
            txtLieu.text = evt.lieu

            // Application de la couleur choisie sur le fond
            container.setBackgroundColor(evt.couleur)
        }

        return itemView
    }
}