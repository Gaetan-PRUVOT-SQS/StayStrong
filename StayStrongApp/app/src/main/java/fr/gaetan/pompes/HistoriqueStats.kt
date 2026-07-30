package fr.gaetan.pompes

import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

data class StatsHistorique(
    val seancesCeMois: Int,
    val streakJours: Int,
    val dernierNiveauParExo: Map<String, Int>
)

// Calcule total mois, streak (jours consécutifs avec au moins 1 séance), dernier niveau par exo
fun calculerStats(
    sessions: List<SessionAgenda>,
    aujourdhui: LocalDate = LocalDate.now()
): StatsHistorique {
    val mois = YearMonth.from(aujourdhui).toString()
    val seancesCeMois = sessions.count { it.date.startsWith(mois) }

    val dates = sessions.map { it.date }.toSet().sortedDescending()
    val streak = calculerStreak(dates, aujourdhui)

    val derniers = mutableMapOf<String, Int>()
    ProgrammeData.exercices.forEach { exo ->
        val maxNiv = sessions
            .filter { it.exerciceId == exo.id }
            .maxOfOrNull { it.niveau } ?: 0
        derniers[exo.id] = maxNiv
    }

    return StatsHistorique(
        seancesCeMois = seancesCeMois,
        streakJours = streak,
        dernierNiveauParExo = derniers
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
