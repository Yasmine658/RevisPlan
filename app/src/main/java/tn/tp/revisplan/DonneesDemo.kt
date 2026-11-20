package tn.tp.revisplan

object DonneesDemo {

    private const val JOURS_PASSES = 3

    // Fake subjects + a plan started 3 days ago, with a few missed sessions
    fun charger(stockage: Stockage) {
        val aujourdhui = DateUtils.aujourdhui()
        stockage.toutEffacer()
        val heuresParJour = 5.0
        stockage.sauverHeuresParJour(heuresParJour)

        val matieres = listOf(
            Matiere(stockage.prochainIdMatiere(), "Mathematics", aujourdhui + 4, 5, 4),
            Matiere(stockage.prochainIdMatiere(), "Programming", aujourdhui + 6, 3, 3),
            Matiere(stockage.prochainIdMatiere(), "Physics", aujourdhui + 9, 4, 3),
            Matiere(stockage.prochainIdMatiere(), "English", aujourdhui + 12, 2, 2),
            Matiere(stockage.prochainIdMatiere(), "History", aujourdhui + 16, 3, 1)
        )
        stockage.sauverMatieres(matieres)

        val debut = aujourdhui - JOURS_PASSES
        val planificateur = PlanificateurRevision(heuresParJour)
        val maths = matieres[0].id
        val physique = matieres[2].id

        // past sessions are done, except maths 2 days ago and physics yesterday
        val seances = planificateur.recalculer(matieres, emptyList(), debut).map {
            val manquee = (it.matiereId == maths && it.jour == aujourdhui - 2) ||
                (it.matiereId == physique && it.jour == aujourdhui - 1)
            if (it.jour < aujourdhui && !manquee) it.copy(faite = true) else it
        }
        stockage.sauverSeances(seances)
        stockage.mettreAJourPlan(aujourdhui)
    }
}
