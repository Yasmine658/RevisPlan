package tn.tp.revisplan

import android.content.Context
import org.json.JSONArray
import org.json.JSONException

class Stockage(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("revisplan_data", Context.MODE_PRIVATE)

    fun chargerMatieres(): MutableList<Matiere> =
        lireListe(CLE_MATIERES) { Matiere.fromJson(it) }

    fun sauverMatieres(matieres: List<Matiere>) {
        val array = JSONArray()
        matieres.forEach { array.put(it.toJson()) }
        prefs.edit().putString(CLE_MATIERES, array.toString()).apply()
    }

    fun chargerSeances(): MutableList<Seance> =
        lireListe(CLE_SEANCES) { Seance.fromJson(it) }

    fun sauverSeances(seances: List<Seance>) {
        val array = JSONArray()
        seances.forEach { array.put(it.toJson()) }
        prefs.edit().putString(CLE_SEANCES, array.toString()).apply()
    }

    fun chargerHeuresParJour(): Double =
        prefs.getFloat(CLE_HEURES, HEURES_PAR_DEFAUT.toFloat()).toDouble()

    fun sauverHeuresParJour(heures: Double) {
        prefs.edit().putFloat(CLE_HEURES, heures.toFloat()).apply()
    }

    fun prochainIdMatiere(): Long {
        val id = prefs.getLong(CLE_PROCHAIN_ID, 1L)
        prefs.edit().putLong(CLE_PROCHAIN_ID, id + 1).apply()
        return id
    }

    fun toutEffacer() {
        prefs.edit().clear().apply()
    }

    // corrupted data gives an empty list instead of a crash
    private fun <T> lireListe(cle: String, lire: (org.json.JSONObject) -> T): MutableList<T> {
        val texte = prefs.getString(cle, null) ?: return mutableListOf()
        return try {
            val array = JSONArray(texte)
            MutableList(array.length()) { lire(array.getJSONObject(it)) }
        } catch (e: JSONException) {
            mutableListOf()
        }
    }

    companion object {
        const val HEURES_PAR_DEFAUT = 4.0
        private const val CLE_MATIERES = "matieres"
        private const val CLE_SEANCES = "seances"
        private const val CLE_HEURES = "heures_par_jour"
        private const val CLE_PROCHAIN_ID = "prochain_id_matiere"
    }
}
