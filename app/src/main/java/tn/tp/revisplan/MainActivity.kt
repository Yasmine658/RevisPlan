package tn.tp.revisplan

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var stockage: Stockage
    private lateinit var editHeures: EditText
    private lateinit var listeMatieres: ListView
    private lateinit var txtAucuneMatiere: TextView
    private val matieres = mutableListOf<Matiere>()
    private lateinit var adapter: MatiereAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        stockage = Stockage(this)

        editHeures = findViewById(R.id.editHeuresParJour)
        listeMatieres = findViewById(R.id.listeMatieres)
        txtAucuneMatiere = findViewById(R.id.txtAucuneMatiere)

        adapter = MatiereAdapter(this, matieres)
        listeMatieres.adapter = adapter
        listeMatieres.emptyView = txtAucuneMatiere

        // after a rotation the EditText restores what the user was typing
        if (savedInstanceState == null) {
            editHeures.setText(formaterHeures(stockage.chargerHeuresParJour()))
        }

        findViewById<Button>(R.id.btnSauverHeures).setOnClickListener { sauverHeures() }
        findViewById<Button>(R.id.btnAjouterMatiere).setOnClickListener {
            startActivity(Intent(this, AjoutMatiereActivity::class.java))
        }

        findViewById<Button>(R.id.btnGenererPlan).setOnClickListener { genererPlan() }
        findViewById<Button>(R.id.btnProgression).setOnClickListener {
            startActivity(Intent(this, ProgressionActivity::class.java))
        }

        listeMatieres.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, AjoutMatiereActivity::class.java)
            intent.putExtra(AjoutMatiereActivity.EXTRA_MATIERE_ID, matieres[position].id)
            startActivity(intent)
        }
        listeMatieres.setOnItemLongClickListener { _, _, position, _ ->
            confirmerSuppression(matieres[position])
            true
        }
    }

    override fun onResume() {
        super.onResume()
        rafraichirListe()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_demo -> {
            confirmer(R.string.demo_title, R.string.demo_message) {
                DonneesDemo.charger(stockage)
                editHeures.setText(formaterHeures(stockage.chargerHeuresParJour()))
                Toast.makeText(this, R.string.demo_loaded, Toast.LENGTH_SHORT).show()
                rafraichirListe()
            }
            true
        }
        R.id.action_effacer -> {
            confirmer(R.string.clear_title, R.string.clear_message) {
                stockage.toutEffacer()
                editHeures.setText(formaterHeures(stockage.chargerHeuresParJour()))
                Toast.makeText(this, R.string.data_cleared, Toast.LENGTH_SHORT).show()
                rafraichirListe()
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun confirmer(titre: Int, message: Int, action: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(titre)
            .setMessage(message)
            .setPositiveButton(R.string.yes) { _, _ -> action() }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun rafraichirListe() {
        matieres.clear()
        matieres.addAll(stockage.chargerMatieres().sortedBy { it.dateExamen })
        adapter.notifyDataSetChanged()
    }

    // returns null if the field is not a valid number of hours
    private fun lireHeures(): Double? {
        val heures = editHeures.text.toString().trim().replace(',', '.').toDoubleOrNull()
        if (heures == null || heures < 0.5 || heures > 16 || (heures * 2) % 1.0 != 0.0) {
            Toast.makeText(this, R.string.error_hours, Toast.LENGTH_LONG).show()
            return null
        }
        return heures
    }

    private fun sauverHeures() {
        val heures = lireHeures() ?: return
        stockage.sauverHeuresParJour(heures)
        if (stockage.planExiste()) stockage.mettreAJourPlan()
        Toast.makeText(this, getString(R.string.hours_saved, formaterHeures(heures)), Toast.LENGTH_SHORT).show()
    }

    private fun genererPlan() {
        val heures = lireHeures() ?: return
        val aujourdhui = DateUtils.aujourdhui()
        if (stockage.chargerMatieres().none { it.dateExamen > aujourdhui }) {
            Toast.makeText(this, R.string.error_no_subject, Toast.LENGTH_LONG).show()
            return
        }
        stockage.sauverHeuresParJour(heures)
        val seances = stockage.mettreAJourPlan(aujourdhui)
        val aVenir = seances.count { it.jour >= aujourdhui && !it.faite }
        Toast.makeText(this, getString(R.string.plan_generated, aVenir), Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, PlanningActivity::class.java))
    }

    private fun confirmerSuppression(matiere: Matiere) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_subject_title)
            .setMessage(getString(R.string.delete_subject_message, matiere.nom))
            .setPositiveButton(R.string.delete) { _, _ ->
                stockage.sauverMatieres(stockage.chargerMatieres().filter { it.id != matiere.id })
                stockage.sauverSeances(stockage.chargerSeances().filter { it.matiereId != matiere.id })
                if (stockage.planExiste()) stockage.mettreAJourPlan()
                Toast.makeText(this, R.string.subject_deleted, Toast.LENGTH_SHORT).show()
                rafraichirListe()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
