package tn.tp.revisplan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView

class ProgressionAdapter(context: Context, progressions: List<Progression>) :
    ArrayAdapter<Progression>(context, 0, progressions) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val vue = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_progression, parent, false)
        val p = getItem(position) ?: return vue

        vue.findViewById<TextView>(R.id.txtNomProgression).text = p.matiere.nom
        vue.findViewById<TextView>(R.id.txtPourcentage).text =
            context.getString(R.string.progress_percent, p.pourcentage)
        vue.findViewById<TextView>(R.id.txtExamenProgression).text =
            context.getString(R.string.progress_exam, DateUtils.formater(p.matiere.dateExamen))
        vue.findViewById<ProgressBar>(R.id.barreProgression).progress = p.pourcentage
        vue.findViewById<TextView>(R.id.txtHeuresProgression).text = context.getString(
            R.string.progress_hours, formaterHeures(p.heuresPrevues), formaterHeures(p.heuresFaites)
        )

        val txtRetard = vue.findViewById<TextView>(R.id.txtRetard)
        val examPasse = p.matiere.dateExamen <= DateUtils.aujourdhui()
        if (p.retard > 0.0) {
            val texte = if (examPasse) R.string.progress_delay_exam_passed else R.string.progress_delay
            txtRetard.text = context.getString(texte, formaterHeures(p.retard))
            txtRetard.setTextColor(context.getColor(R.color.missed))
        } else {
            txtRetard.setText(R.string.progress_on_track)
            txtRetard.setTextColor(context.getColor(R.color.done))
        }
        return vue
    }
}
