package tn.tp.revisplan

import java.util.Locale

// 1.0 -> "1", 1.5 -> "1.5"
fun formaterHeures(heures: Double): String =
    if (heures % 1.0 == 0.0) heures.toLong().toString()
    else String.format(Locale.US, "%.1f", heures)
