package tn.tp.revisplan

data class Progression(
    val matiere: Matiere,
    val heuresPrevues: Double,
    val heuresFaites: Double,
    val retard: Double
) {
    val pourcentage: Int
        get() = if (heuresPrevues <= 0.0) 0
        else (heuresFaites * 100 / heuresPrevues).toInt().coerceIn(0, 100)

    companion object {
        fun calculer(matiere: Matiere, seances: List<Seance>, aujourdhui: Long) = Progression(
            matiere,
            PlanificateurRevision.heuresPrevues(matiere.id, seances),
            PlanificateurRevision.heuresFaites(matiere.id, seances),
            PlanificateurRevision.heuresARattraper(matiere.id, seances, aujourdhui)
        )
    }
}
