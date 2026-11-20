package tn.tp.revisplan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MatiereAdapter(context: Context, matieres: List<Matiere>) :
    ArrayAdapter<Matiere>(context, 0, matieres) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val vue = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_matiere, parent, false)
        val matiere = getItem(position) ?: return vue
        val joursRestants = matiere.dateExamen - DateUtils.aujourdhui()

        vue.findViewById<TextView>(R.id.txtNomMatiere).text = matiere.nom
        vue.findViewById<TextView>(R.id.txtDetailsMatiere).text = context.getString(
            R.string.subject_details,
            DateUtils.formater(matiere.dateExamen),
            matiere.difficulte,
            matiere.coefficient
        )
        vue.findViewById<TextView>(R.id.txtJoursRestants).text = when {
            joursRestants <= 0 -> context.getString(R.string.exam_passed)
            joursRestants == 1L -> context.getString(R.string.exam_tomorrow)
            else -> context.getString(R.string.days_left, joursRestants.toInt())
        }
        return vue
    }
}
