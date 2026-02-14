package com.example.exo8_trains

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filter.FilterResults

class CityAdapter(context: Context, private val allCities: List<String>) :
    ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, allCities) {

    private var filteredCities: List<String> = allCities

    override fun getCount(): Int = filteredCities.size
    override fun getItem(position: Int): String? = filteredCities[position]

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase() ?: ""

                // Ici, on définit notre algorithme de proximité
                val suggestionList = if (query.isEmpty()) {
                    allCities
                } else {
                    allCities.filter { city ->
                        val cityName = city.lowercase()
                        // 1. Contient la séquence (plus souple que startsWith)
                        cityName.contains(query) ||
                                // 2. Bonus : Distance de Levenshtein (optionnel)
                                levenshteinDistance(cityName, query) <= 1
                    }
                }

                return FilterResults().apply {
                    values = suggestionList
                    count = suggestionList.size
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredCities = results?.values as? List<String> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }

    // Algorithme simple de proximité de caractères
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = kotlin.comparisons.minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost)
            }
        }
        return dp[s1.length][s2.length]
    }
}