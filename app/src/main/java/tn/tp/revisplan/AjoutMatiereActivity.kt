package tn.tp.revisplan

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AjoutMatiereActivity : AppCompatActivity() {

    private lateinit var stockage: Stockage
    private lateinit var editNom: EditText
    private lateinit var btnDate: Button
    private lateinit var ratingDifficulte: RatingBar
    private lateinit var editCoefficient: EditText

    private var matiereEditee: Matiere? = null
    private var dateExamen: Long = AUCUNE_DATE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajout_matiere)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        stockage = Stockage(this)

        editNom = findViewById(R.id.editNom)
        btnDate = findViewById(R.id.btnDateExamen)
        ratingDifficulte = findViewById(R.id.ratingDifficulte)
        editCoefficient = findViewById(R.id.editCoefficient)

        val id = intent.getLongExtra(EXTRA_MATIERE_ID, -1L)
        matiereEditee = stockage.chargerMatieres().find { it.id == id }
        setTitle(if (matiereEditee == null) R.string.title_add_subject else R.string.title_edit_subject)

        if (savedInstanceState != null) {
            dateExamen = savedInstanceState.getLong(CLE_DATE, AUCUNE_DATE)
        } else {
            matiereEditee?.let {
                editNom.setText(it.nom)
                dateExamen = it.dateExamen
                ratingDifficulte.rating = it.difficulte.toFloat()
                editCoefficient.setText(it.coefficient.toString())
            }
        }
        afficherDate()

        btnDate.setOnClickListener { choisirDate() }
        findViewById<Button>(R.id.btnSauverMatiere).setOnClickListener { sauver() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(CLE_DATE, dateExamen)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun afficherDate() {
        btnDate.text = if (dateExamen == AUCUNE_DATE) getString(R.string.pick_date)
        else DateUtils.formater(dateExamen)
    }

    private fun choisirDate() {
        val depart = if (dateExamen == AUCUNE_DATE) DateUtils.aujourdhui() + 7 else dateExamen
        val cal = DateUtils.versCalendar(depart)
        val dialog = DatePickerDialog(
            this,
            { _, annee, mois, jour ->
                dateExamen = DateUtils.versJour(annee, mois, jour)
                afficherDate()
            },
            cal.get(java.util.Calendar.YEAR),
            cal.get(java.util.Calendar.MONTH),
            cal.get(java.util.Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    private fun sauver() {
        val nom = editNom.text.toString().trim()
        val difficulte = ratingDifficulte.rating.toInt()
        val coefficient = editCoefficient.text.toString().trim().toIntOrNull()
        val matieres = stockage.chargerMatieres()

        val erreur = when {
            nom.isEmpty() -> R.string.error_name
            matieres.any { it.nom.equals(nom, ignoreCase = true) && it.id != matiereEditee?.id } ->
                R.string.error_name_exists
            dateExamen == AUCUNE_DATE -> R.string.error_date
            dateExamen <= DateUtils.aujourdhui() -> R.string.error_date_past
            difficulte !in Matiere.DIFFICULTE_MIN..Matiere.DIFFICULTE_MAX -> R.string.error_difficulty
            coefficient == null || coefficient !in Matiere.COEFFICIENT_MIN..Matiere.COEFFICIENT_MAX ->
                R.string.error_coefficient
            else -> null
        }
        if (erreur != null) {
            Toast.makeText(this, erreur, Toast.LENGTH_SHORT).show()
            return
        }

        val id = matiereEditee?.id ?: stockage.prochainIdMatiere()
        val matiere = Matiere(id, nom, dateExamen, difficulte, coefficient!!)
        val index = matieres.indexOfFirst { it.id == id }
        if (index >= 0) matieres[index] = matiere else matieres.add(matiere)
        stockage.sauverMatieres(matieres)

        Toast.makeText(this, R.string.subject_saved, Toast.LENGTH_SHORT).show()
        finish()
    }

    companion object {
        const val EXTRA_MATIERE_ID = "tn.tp.revisplan.MATIERE_ID"
        private const val CLE_DATE = "date_examen"
        private const val AUCUNE_DATE = Long.MIN_VALUE
    }
}
