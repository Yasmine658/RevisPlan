package tn.tp.revisplan

import org.json.JSONObject

data class Seance(
    val id: Long,
    val matiereId: Long,
    val jour: Long, // epoch day
    val heures: Double,
    val faite: Boolean = false,
    val rattrapage: Boolean = false // catch-up session for missed hours
) {
    fun estManquee(aujourdhui: Long) = !faite && jour < aujourdhui

    fun toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("matiereId", matiereId)
        .put("jour", jour)
        .put("heures", heures)
        .put("faite", faite)
        .put("rattrapage", rattrapage)

    companion object {
        fun fromJson(json: JSONObject) = Seance(
            id = json.getLong("id"),
            matiereId = json.getLong("matiereId"),
            jour = json.getLong("jour"),
            heures = json.getDouble("heures"),
            faite = json.optBoolean("faite", false),
            rattrapage = json.optBoolean("rattrapage", false)
        )
    }
}
