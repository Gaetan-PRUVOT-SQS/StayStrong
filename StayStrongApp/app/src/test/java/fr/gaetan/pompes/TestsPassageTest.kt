package fr.gaetan.pompes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TestsPassageTest {

    @Test
    fun testTractionsNiveau1() {
        val t = TestsPassage.getTest("tractions", 1)!!
        assertEquals(2, t.versNiveau)
        assertTrue(t.consigne.contains("4"))
    }

    @Test
    fun validerDebloqueNiveau() {
        val store = ProgressionMemoire()
        assertEquals(1, store.getNiveauDebloque("tractions"))
        assertTrue(store.validerTestPassage("tractions", 1))
        assertEquals(2, store.getNiveauDebloque("tractions"))
    }

    @Test
    fun exportImportJson() {
        val store = ProgressionMemoire()
        store.dateTest = "2026-07-30"
        store.marquerJourFait("pompes", 1, 2)
        store.validerTestPassage("pompes", 1)
        val json = store.exporterJson()
        assertTrue(json.contains("Stay Strong"))
        assertTrue(json.contains("pompes"))

        val store2 = ProgressionMemoire()
        assertTrue(store2.importerJson(json))
        assertEquals(1, store2.getSessions().size)
        assertEquals(2, store2.getNiveauDebloque("pompes"))
        // marquerJourFait mémorise aussi la position
        val pos = store2.getPositionExo("pompes")
        assertEquals(1, pos!!.niveau)
        assertEquals(2, pos.jour)
    }

    @Test
    fun consigneKillyPresente() {
        val c = ConsignesForme.pour("killy", 1)
        assertTrue(c.contains("mur"))
        assertFalse(c.isEmpty())
    }
}
