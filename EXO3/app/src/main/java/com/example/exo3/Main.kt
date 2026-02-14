package com.example.exo3

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // Cette fonction est appelée par le bouton via android:onClick="onValiderClick"
    fun onValiderClick(view: View) {

        // 1. Récupération des vues
        val editNom = findViewById<EditText>(R.id.editNom)
        val editPrenom = findViewById<EditText>(R.id.editPrenom)
        val editAge = findViewById<EditText>(R.id.editAge)
        val editTel = findViewById<EditText>(R.id.editTelephone)
        val editCompetences = findViewById<EditText>(R.id.editCompetences)

        // 2. Récupération des textes (avec .trim() pour enlever les espaces inutiles)
        val nom = editNom.text.toString().trim()
        val prenom = editPrenom.text.toString().trim()
        val ageStr = editAge.text.toString().trim()
        val tel = editTel.text.toString().trim()
        val competences = editCompetences.text.toString().trim()

        // 3. LOGIQUE DE VALIDATION
        var estValide = true

        // Vérification NOM (Vide ou trop court)
        if (nom.isEmpty()) {
            editNom.error = "Le nom est obligatoire"
            estValide = false
        } else if (nom.length < 2) {
            editNom.error = "Le nom est trop court"
            estValide = false
        }

        // Vérification PRÉNOM
        if (prenom.isEmpty()) {
            editPrenom.error = "Le prénom est obligatoire"
            estValide = false
        }

        // Vérification AGE (Vide ou incohérent)
        if (ageStr.isEmpty()) {
            editAge.error = "L'âge est requis"
            estValide = false
        } else {
            val ageInt = ageStr.toIntOrNull()
            if (ageInt == null || ageInt <= 0 || ageInt > 120) {
                editAge.error = "Âge invalide (1-120)"
                estValide = false
            }
        }

        // Vérification TÉLÉPHONE (Format français 10 chiffres)
        // Regex : Commence par 0, suivi d'un chiffre (1-9), puis 8 autres chiffres
        val phoneRegex = "^0[1-9][0-9]{8}$".toRegex()
        if (tel.isEmpty()) {
            editTel.error = "Téléphone requis"
            estValide = false
        } else if (!tel.matches(phoneRegex)) {
            editTel.error = "Numéro invalide (ex: 0612345678)"
            estValide = false
        }

        // Vérification COMPÉTENCES
        if (competences.isEmpty()) {
            editCompetences.error = "Veuillez saisir une compétence"
            estValide = false
        }

        // 4. SI TOUT EST VALIDE, ON LANCE LA SUITE
        if (estValide) {
            afficherConfirmation(nom, prenom, ageStr, tel, competences)
        } else {
            // Optionnel : Un petit Toast pour dire qu'il y a des erreurs
            Toast.makeText(this, "Veuillez corriger les erreurs", Toast.LENGTH_SHORT).show()
        }
    }

    // J'ai séparé la création du Dialog pour que le code soit plus propre
    private fun afficherConfirmation(nom: String, prenom: String, age: String, tel: String, competences: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmation")
        builder.setMessage("Êtes-vous sûr de vouloir valider ?")

        builder.setPositiveButton("Confirmer") { _, _ ->
            // Création de l'Intent pour changer d'activité
            val intent = Intent(this, AffichageActivity::class.java)

            // On passe les données validées
            intent.putExtra("EXTRA_NOM", nom)
            intent.putExtra("EXTRA_PRENOM", prenom)
            intent.putExtra("EXTRA_AGE", age)
            intent.putExtra("EXTRA_TEL", tel)
            intent.putExtra("EXTRA_COMP", competences)

            startActivity(intent)
        }

        builder.setNegativeButton("Annuler") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
}