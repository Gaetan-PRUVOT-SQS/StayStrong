package fr.gaetan.pompes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressionStoreTest {

    @Test
    fun sansHistoriqueSuggereJour1() {
        val store = ProgressionMemoire()
        assertEquals(1, store.getJourSuggere("pompes", 1))
        assertTrue(store.getJoursFaits("pompes", 1).isEmpty())
    }

    @Test
    fun progressionSepareeParExercice() {
        val store = ProgressionMemoire()
        store.marquerJourFait("pompes", 1, 3)
        store.marquerJourFait("squats", 1, 1)
        assertEquals(4, store.getJourSuggere("pompes", 1))
        assertEquals(2, store.getJourSuggere("squats", 1))
    }

    @Test
    fun progressionSepareeParNiveau() {
        val store = ProgressionMemoire()
        store.marquerJourFait("squats", 1, 6)
        store.marquerJourFait("squats", 2, 1)
        assertEquals(6, store.getJourSuggere("squats", 1))
        assertEquals(2, store.getJourSuggere("squats", 2))
    }

    @Test
    fun squatsNiveau5PlafonneATroisJours() {
        val store = ProgressionMemoire()
        store.marquerJourFait("squats", 5, 3)
        assertEquals(3, store.getJourSuggere("squats", 5))
    }

    @Test
    fun agendaEnregistreLesSeances() {
        val store = ProgressionMemoire()
        store.dateTest = "2026-07-30"
        store.marquerJourFait("pompes", 1, 1)
        store.marquerJourFait("tractions", 2, 3)
        val sessions = store.getSessions()
        assertEquals(2, sessions.size)
        assertEquals(setOf("2026-07-30"), store.getDatesEntrainement())
        assertEquals("Pompes", sessions[0].exerciceNom)
        assertEquals(1, sessions[0].jourProgramme)
        assertEquals("Tractions", sessions[1].exerciceNom)
    }

    @Test
    fun alertePauseSiTropTot() {
        val store = ProgressionMemoire()
        // jour 1 pompes a 1 jour de pause min
        store.dateTest = "2026-07-30"
        store.marquerJourFait("pompes", 1, 1)
        store.dateTest = "2026-07-30" // même jour
        val alerte = store.verifierPause("pompes", 1)
        assertTrue(alerte != null)
        assertTrue(alerte!!.joursRestants >= 1)
    }

    @Test
    fun pasDAlerteApresPause() {
        val store = ProgressionMemoire()
        store.dateTest = "2026-07-28"
        store.marquerJourFait("pompes", 1, 1)
        store.dateTest = "2026-07-30"
        val alerte = store.verifierPause("pompes", 1)
        assertNull(alerte)
    }

    @Test
    fun mémorisePositionExoEtSeanceComplete() {
        val store = ProgressionMemoire()
        store.sauverPositionExo("pompes", 2, 4)
        store.sauverDerniereSeanceComplete(3, 2)
        val pos = store.getPositionExo("pompes")
        assertTrue(pos != null)
        assertEquals(2, pos!!.niveau)
        assertEquals(4, pos.jour)
        val derniere = store.getDernierePositionExo()
        assertEquals("pompes", derniere!!.exerciceId)
        val sc = store.getDerniereSeanceComplete()
        assertEquals(3, sc!!.niveau)
        assertEquals(2, sc.jour)
    }

    @Test
    fun exportImportConservePositions() {
        val store = ProgressionMemoire()
        store.dateTest = "2026-07-30"
        store.marquerJourFait("tractions", 1, 2)
        store.sauverDerniereSeanceComplete(1, 3)
        val json = store.exporterJson()
        val store2 = ProgressionMemoire()
        assertTrue(store2.importerJson(json))
        val pos = store2.getPositionExo("tractions")
        assertEquals(1, pos!!.niveau)
        assertEquals(2, pos.jour)
        assertEquals(3, store2.getDerniereSeanceComplete()!!.jour)
    }

    @Test
    fun modeSombreParDefautFaux() {
        val store = ProgressionMemoire()
        assertTrue(!store.getModeSombre())
        store.setModeSombre(true)
        assertTrue(store.getModeSombre())
    }
}
