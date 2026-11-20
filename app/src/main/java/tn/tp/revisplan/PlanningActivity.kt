package tn.tp.revisplan

import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PlanningActivity : AppCompatActivity() {

    private lateinit var stockage: Stockage
    private lateinit var listeSeances: ListView
    private lateinit var txtResume: TextView
    private val lignes = mutableListOf<LignePlanning>()
    private val nomsMatieres = mutableMapOf<Long, String>()
    private lateinit var adapter: SeanceAdapter
    private var premierAffichage = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planning)
        setTitle(R.string.title_planning)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        stockage = Stockage(this)

        listeSeances = findViewById(R.id.listeSeances)
        txtResume = findViewById(R.id.txtResumePlanning)
        listeSeances.emptyView = findViewById(R.id.txtAucunPlan)
        adapter = SeanceAdapter(this, lignes, nomsMatieres)
        listeSeances.adapter = adapter

        // load now so the ListView can restore its scroll position after a rotation
        charger()
        if (savedInstanceState == null) defilerVersAujourdhui()
    }

    override fun onResume() {
        super.onResume()
        // recalculate when coming back, the day may have changed
        if (!premierAffichage) charger()
        premierAffichage = false
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun charger() {
        val aujourdhui = DateUtils.aujourdhui()
        val seances = if (stockage.planExiste()) stockage.mettreAJourPlan(aujourdhui) else emptyList()

        nomsMatieres.clear()
        stockage.chargerMatieres().forEach { nomsMatieres[it.id] = it.nom }

        lignes.clear()
        seances.groupBy { it.jour }.toSortedMap().forEach { (jour, duJour) ->
            lignes += LignePlanning.Jour(jour, duJour.sumOf { it.heures })
            duJour.forEach { lignes += LignePlanning.Session(it) }
        }
        adapter.aujourdhui = aujourdhui
        adapter.notifyDataSetChanged()
        txtResume.text = getString(R.string.planning_summary, formaterHeures(stockage.chargerHeuresParJour()))
    }

    private fun defilerVersAujourdhui() {
        val aujourdhui = DateUtils.aujourdhui()
        val index = lignes.indexOfFirst { it is LignePlanning.Jour && it.jour >= aujourdhui }
        if (index > 0) listeSeances.setSelection(index)
    }
}
