package fr.gaetan.pompes

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests instrumentés du parcours (UI Automator).
 */
@RunWith(AndroidJUnit4::class)
class ParcoursUiTest {

    @Test
    fun packageApplicationCorrect() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("fr.gaetan.pompes", ctx.packageName)
    }

    @Test
    fun mainActivitySeLance() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            assertNotNull(activity)
            assertEquals("fr.gaetan.pompes.MainActivity", activity.javaClass.name)
        }
        scenario.close()
    }

    @Test
    fun demarrageVersExercicesAvecFooter() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        ActivityScenario.launch(MainActivity::class.java)

        val commencer = device.wait(Until.findObject(By.text("Commencer")), 8000)
        assertNotNull("Bouton Commencer introuvable", commencer)
        commencer.click()

        val exercices = device.wait(Until.findObject(By.text("Entraînement")), 8000)
        assertNotNull("Écran Entraînement introuvable", exercices)

        // footer avec les deux onglets
        assertTrue(
            "Footer manquant",
            device.hasObject(By.text("Articles"))
        )

        // au moins un des libellés de l'écran
        val ok = device.wait(
            Until.findObject(By.textContains("séance")),
            3000
        ) != null ||
            device.hasObject(By.textContains("Agenda")) ||
            device.hasObject(By.textContains("ordre")) ||
            device.hasObject(By.textContains("Ordre")) ||
            device.hasObject(By.textContains("Tractions")) ||
            device.hasObject(By.textContains("Pompes"))

        assertTrue("Contenu de l'écran Exercices introuvable", ok)
    }

    @Test
    fun switchFooterVersArticles() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        ActivityScenario.launch(MainActivity::class.java)

        device.wait(Until.findObject(By.text("Commencer")), 8000)?.click()
        // le footer a "Articles" — on clique le bas de l'écran
        val articlesFooter = device.wait(Until.findObject(By.text("Articles")), 8000)
        assertNotNull(articlesFooter)
        articlesFooter.click()
        Thread.sleep(500)

        assertTrue(
            "Liste articles introuvable",
            device.hasObject(By.textContains("exercice")) ||
                device.hasObject(By.textContains("circuit")) ||
                device.hasObject(By.textContains("Lire")) ||
                device.hasObject(By.textContains("Chaque"))
        )
    }

    @Test
    fun ouvrirAgenda() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        ActivityScenario.launch(MainActivity::class.java)

        device.wait(Until.findObject(By.text("Commencer")), 8000)?.click()
        Thread.sleep(500)

        // Agenda peut être hors écran : scroll
        for (i in 0..3) {
            if (device.hasObject(By.text("Agenda"))) break
            device.swipe(
                device.displayWidth / 2,
                device.displayHeight * 2 / 3,
                device.displayWidth / 2,
                device.displayHeight / 3,
                15
            )
            Thread.sleep(300)
        }

        val agendaBtn = device.findObject(By.text("Agenda"))
        assertNotNull("Bouton Agenda introuvable", agendaBtn)
        agendaBtn.click()
        Thread.sleep(1000)

        assertTrue(
            "Écran agenda non détecté",
            device.hasObject(By.text("Agenda")) ||
                device.hasObject(By.textContains("entraînement")) ||
                device.hasObject(By.textContains("mois")) ||
                device.hasObject(By.textContains("Série"))
        )
    }
}
