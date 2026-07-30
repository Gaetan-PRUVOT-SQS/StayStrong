package fr.gaetan.pompes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgrammeDataTest {

    @Test
    fun cinqExercicesPresentsDansLOrdreSeance() {
        assertEquals(5, ProgrammeData.exercices.size)
        // Tractions frais, puis pompes, squats, killy, gainage en dernier
        assertEquals("tractions", ProgrammeData.exercices[0].id)
        assertEquals("pompes", ProgrammeData.exercices[1].id)
        assertEquals("squats", ProgrammeData.exercices[2].id)
        assertEquals("killy", ProgrammeData.exercices[3].id)
        assertEquals("gainage", ProgrammeData.exercices[4].id)
    }

    @Test
    fun pompesCinqNiveaux() {
        val p = ProgrammeData.getExercice("pompes")
        assertEquals("Pompes", p.nom)
        assertEquals(5, p.niveaux.size)
    }

    @Test
    fun squatsCinqNiveaux() {
        val s = ProgrammeData.getExercice("squats")
        assertEquals("Squats", s.nom)
        assertEquals(5, s.niveaux.size)
    }

    @Test
    fun pompesNiveau1Jour1() {
        val j = ProgrammeData.getJour("pompes", 1, 1)
        assertEquals(60, j.reposSecondes)
        assertEquals(SerieFixe(5), j.series[0])
        assertEquals(SerieMax(5), j.series[4])
    }

    @Test
    fun squatsNiveau1Jour1() {
        val j = ProgrammeData.getJour("squats", 1, 1)
        assertEquals(60, j.reposSecondes)
        assertEquals(SerieFixe(12), j.series[0])
        assertEquals(SerieMax(12), j.series[4])
    }

    @Test
    fun squatsNiveau5ATroisJoursEtHuitSeries() {
        val n = ProgrammeData.getNiveau("squats", 5)
        assertEquals(3, n.jours.size)
        val j2 = ProgrammeData.getJour("squats", 5, 2)
        assertEquals(60, j2.reposSecondes)
        assertEquals(8, j2.series.size)
        assertEquals(SerieMax(62), j2.series[7])
    }

    @Test
    fun tractionsNiveau1SansMax() {
        val t = ProgrammeData.getExercice("tractions")
        assertEquals("Tractions", t.nom)
        assertEquals(5, t.niveaux.size)
        val j1 = ProgrammeData.getJour("tractions", 1, 1)
        assertEquals(60, j1.reposSecondes)
        assertEquals(SerieFixe(2), j1.series[0])
        assertEquals(SerieFixe(1), j1.series[4])
    }

    @Test
    fun tractionsNiveau5Jour6() {
        val j = ProgrammeData.getJour("tractions", 5, 6)
        assertEquals(60, j.reposSecondes)
        assertEquals(SerieFixe(12), j.series[0])
        assertEquals(SerieMax(12), j.series[4])
    }

    @Test
    fun chaqueExerciceADesNiveauxValides() {
        ProgrammeData.exercices.forEach { exercice ->
            assertTrue(exercice.niveaux.isNotEmpty())
            exercice.niveaux.forEach { niveau ->
                assertTrue(niveau.jours.isNotEmpty())
            }
        }
    }

    @Test
    fun reposToujours60Secondes() {
        ProgrammeData.exercices.forEach { exercice ->
            exercice.niveaux.forEach { niveau ->
                niveau.jours.forEach { jour ->
                    assertEquals(60, jour.reposSecondes)
                }
            }
        }
    }

    @Test
    fun gainageNiveau1Jour1ANeufSeries() {
        val j = ProgrammeData.getJour("gainage", 1, 1)
        assertEquals(9, j.series.size)
        assertEquals(SerieTemps(15, "Gainage facial"), j.series[0])
        assertEquals(SerieTemps(10, "Gainage latéral droit"), j.series[3])
    }

    @Test
    fun killyNiveau1Jour1EnSecondes() {
        val j = ProgrammeData.getJour("killy", 1, 1)
        assertEquals(5, j.series.size)
        assertEquals(SerieTemps(20), j.series[0])
        assertEquals(SerieTempsMax(20), j.series[4])
    }
}
