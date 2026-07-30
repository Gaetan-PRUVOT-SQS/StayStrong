package fr.gaetan.pompes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SeanceViewModelTest {

    @Test
    fun demarrerPompesMetSerie1() {
        val c = SeanceController()
        c.demarrer("pompes", 1, 1)
        val s = c.state!!
        assertEquals(EtatSeance.SERIE, s.etat)
        assertEquals("pompes", s.exerciceId)
        assertEquals(false, s.estMaintien)
        assertEquals("5 répétitions", s.libelleCible)
    }

    @Test
    fun maintienKillyDemarreAvecChrono() {
        val c = SeanceController()
        c.demarrer("killy", 1, 1)
        val s = c.state!!
        assertTrue(s.estMaintien)
        assertEquals(20, s.tempsRestant)
        assertEquals(20, s.maintienTotal)
        assertFalse(s.maintienTermine)
    }

    @Test
    fun maintienVibreAZero() {
        val c = SeanceController()
        c.demarrer("killy", 1, 1)
        repeat(20) { c.tick() }
        assertTrue(c.state!!.maintienTermine)
        assertEquals(0, c.state!!.tempsRestant)
        assertTrue(c.consommerVibration())
    }

    @Test
    fun pausePendantMaintien() {
        val c = SeanceController()
        c.demarrer("killy", 1, 1)
        c.tick()
        c.pause()
        assertEquals(EtatSeance.PAUSE, c.state!!.etat)
        val t = c.state!!.tempsRestant
        c.tick()
        assertEquals(t, c.state!!.tempsRestant)
        c.reprendre()
        assertEquals(EtatSeance.SERIE, c.state!!.etat)
    }

    @Test
    fun serieTermineePasseEnRepos() {
        val c = SeanceController()
        c.demarrer("pompes", 1, 1)
        c.serieTerminee()
        assertEquals(EtatSeance.REPOS, c.state!!.etat)
        assertEquals(60, c.state!!.tempsRestant)
    }

    @Test
    fun cinquiemeSerieTermineLaSeance() {
        val c = SeanceController()
        c.demarrer("pompes", 1, 1)
        repeat(4) {
            c.serieTerminee()
            repeat(c.state!!.tempsRestant) { c.tick() }
            c.serieSuivante()
        }
        // dernière série = max → on saisit les reps
        c.serieTerminee(10)
        assertEquals(EtatSeance.FIN, c.state!!.etat)
    }

    @Test
    fun arreterEffaceLaSeance() {
        val c = SeanceController()
        c.demarrer("squats", 2, 1)
        c.arreter()
        assertNull(c.state)
    }

    @Test
    fun passerChronoTermineLeRepos() {
        val c = SeanceController()
        c.demarrer("pompes", 1, 1)
        c.serieTerminee()
        assertEquals(EtatSeance.REPOS, c.state!!.etat)
        assertTrue(c.state!!.tempsRestant > 0)
        c.passerChrono()
        assertEquals(0, c.state!!.tempsRestant)
        assertTrue(c.state!!.reposTermine)
        assertTrue(c.consommerVibration())
        // on peut enchaîner la série suivante
        c.serieSuivante()
        assertEquals(EtatSeance.SERIE, c.state!!.etat)
        assertEquals(1, c.state!!.indexSerie)
    }

    @Test
    fun passerChronoTermineLeMaintien() {
        val c = SeanceController()
        c.demarrer("killy", 1, 1)
        assertTrue(c.state!!.estMaintien)
        assertFalse(c.state!!.maintienTermine)
        c.passerChrono()
        assertEquals(0, c.state!!.tempsRestant)
        assertTrue(c.state!!.maintienTermine)
    }

    @Test
    fun passerChronoPendantPauseRepos() {
        val c = SeanceController()
        c.demarrer("pompes", 1, 1)
        c.serieTerminee()
        c.pause()
        assertEquals(EtatSeance.PAUSE, c.state!!.etat)
        c.passerChrono()
        assertEquals(EtatSeance.REPOS, c.state!!.etat)
        assertTrue(c.state!!.reposTermine)
        assertEquals(0, c.state!!.tempsRestant)
    }

    @Test
    fun serieMaxExigeLesReps() {
        val c = SeanceController()
        c.demarrer("pompes", 1, 1)
        // séries 1-4 fixes, série 5 = max
        repeat(4) {
            c.serieTerminee()
            repeat(c.state!!.tempsRestant) { c.tick() }
            c.serieSuivante()
        }
        assertTrue(c.state!!.estSerieMax)
        // sans reps : ne valide pas
        c.serieTerminee(null)
        assertEquals(EtatSeance.SERIE, c.state!!.etat)
        c.serieTerminee(12)
        assertEquals(EtatSeance.FIN, c.state!!.etat)
        assertEquals("S5: 12", c.getDetailReps())
    }
}
