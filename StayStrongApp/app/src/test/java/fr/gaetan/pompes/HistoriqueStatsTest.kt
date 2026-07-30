package fr.gaetan.pompes

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class HistoriqueStatsTest {

    @Test
    fun streakTroisJours() {
        val auj = LocalDate.of(2026, 7, 30)
        val sessions = listOf(
            SessionAgenda("2026-07-30", "pompes", "Pompes", 1, 1),
            SessionAgenda("2026-07-29", "squats", "Squats", 1, 1),
            SessionAgenda("2026-07-28", "tractions", "Tractions", 1, 1)
        )
        val stats = calculerStats(sessions, auj)
        assertEquals(3, stats.streakJours)
        assertEquals(3, stats.seancesCeMois)
        assertEquals(3, stats.seancesCetteSemaine)
        assertEquals(7, stats.joursSemaine.size)
    }

    @Test
    fun dernierNiveauParExo() {
        val sessions = listOf(
            SessionAgenda("2026-07-20", "pompes", "Pompes", 1, 1),
            SessionAgenda("2026-07-21", "pompes", "Pompes", 3, 2),
            SessionAgenda("2026-07-22", "killy", "Killy", 2, 1)
        )
        val stats = calculerStats(sessions, LocalDate.of(2026, 7, 30))
        assertEquals(3, stats.dernierNiveauParExo["pompes"])
        assertEquals(2, stats.dernierNiveauParExo["killy"])
        assertEquals(0, stats.dernierNiveauParExo["squats"])
        assertEquals(2, stats.totalParExo["pompes"])
        assertEquals(1, stats.totalParExo["killy"])
        assertEquals(0, stats.totalParExo["squats"])
    }
}
