package tn.tp.revisplan

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView

// A row is either a day header or a session
sealed class LignePlanning {
    data class Jour(val jour: Long, val totalHeures: Double) : LignePlanning()
    data class Session(val seance: Seance) : LignePlanning()
}

class SeanceAdapter(
    private val context: Context,
    private val lignes: List<LignePlanning>,
    private val nomsMatieres: Map<Long, String>
) : BaseAdapter() {

    var aujourdhui: Long = DateUtils.aujourdhui()

    override fun getCount() = lignes.size
    override fun getItem(position: Int) = lignes[position]
    override fun getItemId(position: Int) = position.toLong()
    override fun getViewTypeCount() = 2
    override fun getItemViewType(position: Int) = if (lignes[position] is LignePlanning.Jour) 0 else 1
    override fun isEnabled(position: Int) = lignes[position] is LignePlanning.Session

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return when (val ligne = lignes[position]) {
            is LignePlanning.Jour -> vueJour(ligne, convertView, parent)
            is LignePlanning.Session -> vueSeance(ligne.seance, convertView, parent)
        }
    }

    private fun vueJour(ligne: LignePlanning.Jour, convertView: View?, parent: ViewGroup): View {
        val vue = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_jour, parent, false)
        val date = DateUtils.formater(ligne.jour)
        vue.findViewById<TextView>(R.id.txtJour).text =
            if (ligne.jour == aujourdhui) context.getString(R.string.today_header, date) else date
        vue.findViewById<TextView>(R.id.txtTotalJour).text =
            context.getString(R.string.day_total, formaterHeures(ligne.totalHeures))
        return vue
    }

    private fun vueSeance(seance: Seance, convertView: View?, parent: ViewGroup): View {
        val vue = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_seance, parent, false)
        val txtMatiere = vue.findViewById<TextView>(R.id.txtMatiereSeance)
        val txtStatut = vue.findViewById<TextView>(R.id.txtStatutSeance)

        vue.findViewById<CheckBox>(R.id.checkFaite).isChecked = seance.faite
        vue.findViewById<TextView>(R.id.txtHeuresSeance).text =
            context.getString(R.string.session_hours, formaterHeures(seance.heures))
        txtMatiere.text = nomsMatieres[seance.matiereId] ?: context.getString(R.string.unknown_subject)

        val manquee = seance.estManquee(aujourdhui)
        txtMatiere.paintFlags = if (manquee) txtMatiere.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else txtMatiere.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

        val statut = when {
            seance.faite -> context.getString(R.string.status_done)
            manquee -> context.getString(R.string.status_missed)
            else -> context.getString(R.string.status_todo)
        }
        txtStatut.text = if (seance.rattrapage) "${context.getString(R.string.status_catch_up)} · $statut" else statut
        txtStatut.setTextColor(
            context.getColor(
                when {
                    seance.faite -> R.color.done
                    manquee -> R.color.missed
                    seance.rattrapage -> R.color.catch_up
                    else -> R.color.text_secondary
                }
            )
        )
        return vue
    }
}
