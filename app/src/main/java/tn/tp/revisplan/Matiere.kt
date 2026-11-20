package tn.tp.revisplan

import org.json.JSONObject

data class Matiere(
    val id: Long,
    val nom: String,
    val dateExamen: Long, // epoch day
    val difficulte: Int,  // 1..5
    val coefficient: Int
) {
    fun toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("nom", nom)
        .put("dateExamen", dateExamen)
        .put("difficulte", difficulte)
        .put("coefficient", coefficient)

    companion object {
        const val DIFFICULTE_MIN = 1
        const val DIFFICULTE_MAX = 5
        const val COEFFICIENT_MIN = 1
        const val COEFFICIENT_MAX = 10

        fun fromJson(json: JSONObject) = Matiere(
            id = json.getLong("id"),
            nom = json.getString("nom"),
            dateExamen = json.getLong("dateExamen"),
            difficulte = json.getInt("difficulte").coerceIn(DIFFICULTE_MIN, DIFFICULTE_MAX),
            coefficient = json.getInt("coefficient").coerceIn(COEFFICIENT_MIN, COEFFICIENT_MAX)
        )
    }
}
