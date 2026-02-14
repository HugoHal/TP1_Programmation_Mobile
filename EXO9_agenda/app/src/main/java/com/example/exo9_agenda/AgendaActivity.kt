package com.example.exo9_agenda

import EventDecorator
import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import java.util.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.content.Context
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

class AgendaActivity : Activity() {

    private val agenda = HashMap<String, MutableList<Evenement>>()

    private lateinit var selectedDate: String
    private lateinit var adapter: EventAdapter
    private val currentEvents = mutableListOf<Evenement>()
    private lateinit var calendarView: MaterialCalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)

        calendarView = findViewById(R.id.calendarView)
        val txtDate = findViewById<TextView>(R.id.txtDateSelectionnee)
        val listView = findViewById<ListView>(R.id.listEvents)
        val btnFab = findViewById<Button>(R.id.btnFab)

        // 1. Initialisation date
        val c = Calendar.getInstance()
        selectedDate = formateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

        // MODIF : Utilisation d'une string à format "Date : %1$s"
        txtDate.text = getString(R.string.date_format_label, selectedDate)

        adapter = EventAdapter(this, currentEvents)
        listView.adapter = adapter

        var today = CalendarDay.today()
        selectedDate = formateDate(today.year, today.month, today.day)

        // MODIF
        txtDate.text = getString(R.string.date_format_label, selectedDate)
        calendarView.setDateSelected(today, true)

        chargerDonnees()
        updateList()
        refreshCalendarDots()

        calendarView.setOnDateChangedListener { widget, date, selected ->
            selectedDate = formateDate(date.year, date.month - 1, date.day)
            // MODIF
            txtDate.text = getString(R.string.date_format_label, selectedDate)
            updateList()
        }

        btnFab.setOnClickListener {
            afficherBoiteDialogue()
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->

            val eventToDelete = currentEvents[position]

            // On demande confirmation à l'utilisateur
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_title)) // MODIF
                // MODIF : On injecte le nom de l'événement dans le message
                .setMessage(getString(R.string.delete_message, eventToDelete.nom))
                .setPositiveButton(getString(R.string.btn_yes)) { _, _ -> // MODIF

                    val eventsDuJour = agenda[selectedDate]
                    eventsDuJour?.remove(eventToDelete)

                    if (eventsDuJour != null && eventsDuJour.isEmpty()) {
                        agenda.remove(selectedDate)
                    }

                    sauvegarderDonnees()
                    updateList()
                    refreshCalendarDots()

                    // MODIF
                    Toast.makeText(this, getString(R.string.toast_deleted), Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(getString(R.string.btn_no), null) // MODIF
                .show()

            true
        }
    }

    private fun refreshCalendarDots() {
        calendarView.removeDecorators()
        val datesParCouleur = HashMap<Int, HashSet<CalendarDay>>()

        for ((dateString, events) in agenda) {
            if (events.isNotEmpty()) {
                val couleurPoint = events[0].couleur
                val parts = dateString.split("/")
                if (parts.size == 3) {
                    val day = parts[0].toInt()
                    val month = parts[1].toInt()
                    val year = parts[2].toInt()
                    val calendarDay = CalendarDay.from(year, month, day)
                    val list = datesParCouleur.getOrPut(couleurPoint) { HashSet() }
                    list.add(calendarDay)
                }
            }
        }

        for ((color, dates) in datesParCouleur) {
            calendarView.addDecorator(EventDecorator(color, dates))
        }
    }

    private fun afficherBoiteDialogue() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_event, null)

        val editNom = dialogView.findViewById<EditText>(R.id.dialogNom)
        val btnHeure = dialogView.findViewById<Button>(R.id.dialogHeure)
        val editLieu = dialogView.findViewById<EditText>(R.id.dialogLieu)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroupColor)

        var heureChoisie = "00:00"

        btnHeure.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(this, { _, hour, minute ->
                heureChoisie = String.format("%02d:%02d", hour, minute)
                // MODIF : Utilisation du format "Heure : %1$s"
                btnHeure.text = getString(R.string.time_format_label, heureChoisie)
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setNegativeButton(getString(R.string.btn_cancel), null) // MODIF
        builder.setPositiveButton(getString(R.string.btn_validate)) { _, _ -> // MODIF

            val nom = editNom.text.toString()
            val lieu = editLieu.text.toString()

            val couleur = when (radioGroup.checkedRadioButtonId) {
                R.id.radioRouge -> 0xFFB71C1C.toInt()
                R.id.radioBleu -> 0xFF0D47A1.toInt()
                R.id.radioVert -> 0xFF1B5E20.toInt()
                else -> Color.DKGRAY
            }

            if (nom.isNotEmpty()) {
                val nouvelEvenement = Evenement(nom, selectedDate, heureChoisie, lieu, couleur)
                ajouterEvenement(nouvelEvenement)
            } else {
                // MODIF
                Toast.makeText(this, getString(R.string.error_name_required), Toast.LENGTH_SHORT).show()
            }
        }
        builder.show()
    }

    // ... Le reste des fonctions (ajouterEvenement, updateList, etc.) ne contient pas de texte UI, donc inchangé ...

    private fun ajouterEvenement(ev: Evenement) {
        val list = agenda.getOrPut(selectedDate) { mutableListOf() }
        list.add(ev)
        list.sortBy { it.heure }
        sauvegarderDonnees()
        updateList()
        refreshCalendarDots()
    }

    private fun updateList() {
        currentEvents.clear()
        val events = agenda[selectedDate]
        if (events != null) {
            currentEvents.addAll(events)
        }
        adapter.notifyDataSetChanged()
    }

    private fun formateDate(year: Int, month: Int, day: Int): String {
        return String.format("%02d/%02d/%04d", day, month + 1, year)
    }

    private fun sauvegarderDonnees() {
        val sharedPreferences = getSharedPreferences("MonAgendaPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(agenda)
        editor.putString("CLE_AGENDA", json)
        editor.apply()
    }

    private fun chargerDonnees() {
        val sharedPreferences = getSharedPreferences("MonAgendaPrefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("CLE_AGENDA", null)
        if (json != null) {
            val gson = Gson()
            val type = object : TypeToken<HashMap<String, MutableList<Evenement>>>() {}.type
            val donneesChargees: HashMap<String, MutableList<Evenement>> = gson.fromJson(json, type)
            agenda.clear()
            agenda.putAll(donneesChargees)
        }
    }
}

// Data Class inchangée (toString n'est pas utilisé dans l'interface finale)
data class Evenement(
    val nom: String,
    val date: String,
    val heure: String,
    val lieu: String,
    val couleur: Int
)