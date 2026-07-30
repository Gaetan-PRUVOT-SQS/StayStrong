package fr.gaetan.pompes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ArticlesDataTest {

    @Test
    fun quatreArticlesPresents() {
        assertEquals(4, ArticlesData.articles.size)
        val ids = ArticlesData.articles.map { it.id }.toSet()
        assertTrue(ids.contains("exercices"))
        assertTrue(ids.contains("circuit"))
        assertTrue(ids.contains("echauffement"))
        assertTrue(ids.contains("recuperation"))
    }

    @Test
    fun articleExercicesADesBlocsIllustres() {
        val a = ArticlesData.getArticle("exercices")
        assertTrue(a.blocs.size >= 5)
        val avecImage = a.blocs.filter { it.exerciceId != null }
        assertTrue(avecImage.size >= 4)
        assertTrue(a.titre.contains("exercice", ignoreCase = true))
    }

    @Test
    fun articleCircuitParleDeLOrdre() {
        val a = ArticlesData.getArticle("circuit")
        val textes = a.blocs.joinToString(" ") { it.texte }
        assertTrue(textes.contains("tractions") || textes.contains("Tractions") || textes.lowercase().contains("tractions"))
        assertTrue(a.blocs.any { it.titre.contains("ordre", ignoreCase = true) })
    }
}
