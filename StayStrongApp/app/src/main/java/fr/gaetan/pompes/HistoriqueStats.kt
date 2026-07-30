package fr.gaetan.pompes

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

// un jour de la barre "7 derniers jours"
data class JourGraphique(
    val date: String,
    val libelle: String,
    val nbSeances: Int
)

data class StatsHistorique(
    val seancesCeMois: Int,
    val seancesCetteSemaine: Int,
    val streakJours: Int,
    val dernierNiveauParExo: Map<String, Int>,
    val totalParExo: Map<String, Int>,
    val joursSemaine: List<JourGraphique>
)

// Calcule mois, semaine, streak, totaux par exo, barre 7 jours
fun calculerStats(
    sessions: List<SessionAgenda>,
    aujourdhui: LocalDate = LocalDate.now()
): StatsHistorique {
    val mois = YearMonth.from(aujourdhui).toString()
    val seancesCeMois = sessions.count { it.date.startsWith(mois) }

    val debutSemaine = aujourdhui.minusDays(6)
    val seancesCetteSemaine = sessions.count {
        try {
            val d = LocalDate.parse(it.date)
            !d.isBefore(debutSemaine) && !d.isAfter(aujourdhui)
        } catch (e: Exception) {
            false
        }
    }

    val dates = sessions.map { it.date }.toSet().sortedDescending()
    val streak = calculerStreak(dates, aujourdhui)

    val derniers = mutableMapOf<String, Int>()
    val totaux = mutableMapOf<String, Int>()
    ProgrammeData.exercices.forEach { exo ->
        val pourExo = sessions.filter { it.exerciceId == exo.id }
        val maxNiv = pourExo.maxOfOrNull { it.niveau } ?: 0
        derniers[exo.id] = maxNiv
        totaux[exo.id] = pourExo.size
    }

    // 7 derniers jours (du plus ancien au plus récent)
    val joursSemaine = mutableListOf<JourGraphique>()
    var i = 6
    while (i >= 0) {
        val jour = aujourdhui.minusDays(i.toLong())
        val cle = jour.toString()
        val nb = sessions.count { it.date == cle }
        val lettre = jour.dayOfWeek
            .getDisplayName(TextStyle.NARROW, Locale.FRENCH)
            .uppercase(Locale.FRENCH)
        joursSemaine.add(
            JourGraphique(
                date = cle,
                libelle = lettre,
                nbSeances = nb
            )
        )
        i = i - 1
    }

    return StatsHistorique(
        seancesCeMois = seancesCeMois,
        seancesCetteSemaine = seancesCetteSemaine,
        streakJours = streak,
        dernierNiveauParExo = derniers,
        totalParExo = totaux,
        joursSemaine = joursSemaine
    )
}

fun calculerStreak(datesDesc: List<String>, aujourdhui: LocalDate): Int {
    if (datesDesc.isEmpty()) {
        return 0
    }
    val set = datesDesc.toSet()
    // la série peut partir d'aujourd'hui ou d'hier
    var curseur = aujourdhui
    if (!set.contains(curseur.toString())) {
        curseur = aujourdhui.minusDays(1)
        if (!set.contains(curseur.toString())) {
            return 0
        }
    }
    var streak = 0
    while (set.contains(curseur.toString())) {
        streak++
        curseur = curseur.minusDays(1)
    }
    return streak
}
