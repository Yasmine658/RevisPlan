package tn.tp.revisplan

import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProgressionActivity : AppCompatActivity() {

    private lateinit var stockage: Stockage
    private lateinit var txtTotal: TextView
    private val progressions = mutableListOf<Progression>()
    private lateinit var adapter: ProgressionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progression)
        setTitle(R.string.title_progress)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        stockage = Stockage(this)

        txtTotal = findViewById(R.id.txtTotal)
        val liste = findViewById<ListView>(R.id.listeProgression)
        liste.emptyView = findViewById(R.id.txtAucuneProgression)
        adapter = ProgressionAdapter(this, progressions)
        liste.adapter = adapter
        charger()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun charger() {
        val aujourdhui = DateUtils.aujourdhui()
        val seances = if (stockage.planExiste()) stockage.mettreAJourPlan(aujourdhui) else emptyList()

        progressions.clear()
        stockage.chargerMatieres().sortedBy { it.dateExamen }.forEach {
            progressions += Progression.calculer(it, seances, aujourdhui)
        }
        adapter.notifyDataSetChanged()

        txtTotal.text = getString(
            R.string.progress_total,
            formaterHeures(progressions.sumOf { it.heuresFaites }),
            formaterHeures(progressions.sumOf { it.heuresPrevues })
        )
    }
}
