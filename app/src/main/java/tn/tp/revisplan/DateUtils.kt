package tn.tp.revisplan

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

// Dates are stored as epoch days built from the local calendar date,
// so the day never shifts around midnight because of the time zone.
object DateUtils {
    private const val MILLIS_PAR_JOUR = 86_400_000L
    private val UTC = TimeZone.getTimeZone("UTC")

    fun aujourdhui(): Long {
        val local = Calendar.getInstance()
        return versJour(
            local.get(Calendar.YEAR),
            local.get(Calendar.MONTH),
            local.get(Calendar.DAY_OF_MONTH)
        )
    }

    // month is 0-based, like Calendar and DatePickerDialog
    fun versJour(annee: Int, mois: Int, jourDuMois: Int): Long {
        val cal = Calendar.getInstance(UTC)
        cal.clear()
        cal.set(annee, mois, jourDuMois)
        return cal.timeInMillis / MILLIS_PAR_JOUR
    }

    fun versCalendar(jour: Long): Calendar {
        val cal = Calendar.getInstance(UTC)
        cal.timeInMillis = jour * MILLIS_PAR_JOUR
        return cal
    }

    fun formater(jour: Long): String = format("EEE d MMM yyyy").format(versCalendar(jour).time)

    fun formaterCourt(jour: Long): String = format("d MMM").format(versCalendar(jour).time)

    private fun format(pattern: String) = SimpleDateFormat(pattern, Locale.ENGLISH).apply { timeZone = UTC }
}
