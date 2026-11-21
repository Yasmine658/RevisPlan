package tn.tp.revisplan

import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * Builds the study plan day by day, in 30 min slots.
 * Each day: catch-up hours first, then the remaining slots are split with
 *   poids = difficulte * coefficient / joursRestants
 * so hard subjects and close exams get more time.
 */
class PlanificateurRevision(heuresParJour: Double) {

    private val creneauxParJour = (heuresParJour / CRENEAU).roundToInt().coerceAtLeast(0)

    // Keeps past and done sessions, rebuilds every undone session from today on.
    fun recalculer(matieres: List<Matiere>, seances: List<Seance>, aujourdhui: Long): List<Seance> {
        val ids = matieres.map { it.id }.toSet()
        val gardees = seances.filter { it.matiereId in ids && (it.jour < aujourdhui || it.faite) }
        val actives = matieres.filter { it.dateExamen > aujourdhui }
        if (actives.isEmpty() || creneauxParJour == 0) return trier(gardees)

        val aRattraper = actives.associate {
            it.id to versCreneaux(heuresARattraper(it.id, gardees, aujourdhui))
        }.toMutableMap()
        var prochainId = (seances.maxOfOrNull { it.id } ?: 0L) + 1
        val nouvelles = mutableListOf<Seance>()

        for (jour in aujourdhui until actives.maxOf { it.dateExamen }) {
            val faitesDuJour = gardees.filter { it.jour == jour && it.faite }
            var libres = creneauxParJour -
                versCreneaux(faitesDuJour.filter { it.rattrapage }.sumOf { it.heures })
            // no session on or after the exam day
            val duJour = actives.filter { it.dateExamen > jour }

            // missed hours are spread evenly over the days left before the exam
            for (matiere in duJour.sortedBy { it.dateExamen }) {
                val reste = aRattraper[matiere.id] ?: 0
                if (reste == 0 || libres <= 0) continue
                if (faitesDuJour.any { it.matiereId == matiere.id && it.rattrapage }) continue
                val joursRestants = (matiere.dateExamen - jour).toInt()
                val n = minOf((reste + joursRestants - 1) / joursRestants, libres)
                aRattraper[matiere.id] = reste - n
                libres -= n
                nouvelles += Seance(prochainId++, matiere.id, jour, n * CRENEAU, rattrapage = true)
            }

            // split the whole day, a session already done keeps its place
            // so the other sessions of the day don't move
            val parts = repartir(libres, duJour.map { poids(it, jour) })
            duJour.forEachIndexed { i, matiere ->
                val dejaFaite = faitesDuJour.any { it.matiereId == matiere.id && !it.rattrapage }
                if (parts[i] > 0 && !dejaFaite) {
                    nouvelles += Seance(prochainId++, matiere.id, jour, parts[i] * CRENEAU)
                }
            }
        }
        return trier(gardees + nouvelles)
    }

    companion object {
        const val CRENEAU = 0.5

        fun poids(matiere: Matiere, jour: Long): Double {
            val joursRestants = (matiere.dateExamen - jour).coerceAtLeast(1)
            return matiere.difficulte.toDouble() * matiere.coefficient / joursRestants
        }

        // missed regular hours minus the catch-up hours already done
        fun heuresARattraper(matiereId: Long, seances: List<Seance>, aujourdhui: Long): Double {
            val manquees = seances.filter {
                it.matiereId == matiereId && !it.rattrapage && it.estManquee(aujourdhui)
            }.sumOf { it.heures }
            val rattrapees = seances.filter {
                it.matiereId == matiereId && it.rattrapage && it.faite
            }.sumOf { it.heures }
            return (manquees - rattrapees).coerceAtLeast(0.0)
        }

        fun heuresPrevues(matiereId: Long, seances: List<Seance>): Double =
            seances.filter { it.matiereId == matiereId && !it.rattrapage }.sumOf { it.heures }

        fun heuresFaites(matiereId: Long, seances: List<Seance>): Double =
            seances.filter { it.matiereId == matiereId && it.faite }.sumOf { it.heures }

        // largest remainder method, so the parts always add up to total
        fun repartir(total: Int, poids: List<Double>): IntArray {
            val parts = IntArray(poids.size)
            val somme = poids.sum()
            if (total <= 0 || somme <= 0.0) return parts
            val exacts = poids.map { it / somme * total }
            exacts.forEachIndexed { i, x -> parts[i] = floor(x).toInt() }
            val reste = total - parts.sum()
            exacts.indices.sortedByDescending { exacts[it] - parts[it] }
                .take(reste)
                .forEach { parts[it]++ }
            return parts
        }

        private fun versCreneaux(heures: Double) = (heures / CRENEAU).roundToInt()

        private fun trier(seances: List<Seance>) =
            seances.sortedWith(compareBy({ it.jour }, { it.rattrapage }, { it.matiereId }))
    }
}
