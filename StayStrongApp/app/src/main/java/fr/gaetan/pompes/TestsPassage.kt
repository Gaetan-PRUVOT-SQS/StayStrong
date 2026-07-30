package fr.gaetan.pompes

// Test pour passer du niveau N au niveau N+1
data class TestPassage(
    val exerciceId: String,
    val depuisNiveau: Int,
    val versNiveau: Int,
    val consigne: String
)

object TestsPassage {

    private val tests = listOf(
        // Tractions
        TestPassage("tractions", 1, 2, "4 tractions strictes d'affilée"),
        TestPassage("tractions", 2, 3, "7 tractions strictes d'affilée"),
        TestPassage("tractions", 3, 4, "11 tractions strictes d'affilée"),
        TestPassage("tractions", 4, 5, "16 tractions strictes d'affilée"),
        // Pompes (seuils de départ des niveaux)
        TestPassage("pompes", 1, 2, "11 pompes d'affilée en bonne forme"),
        TestPassage("pompes", 2, 3, "21 pompes d'affilée en bonne forme"),
        TestPassage("pompes", 3, 4, "26 pompes d'affilée en bonne forme"),
        TestPassage("pompes", 4, 5, "31 pompes d'affilée en bonne forme"),
        // Squats
        TestPassage("squats", 1, 2, "Terminer le jour 6 du niveau 1, puis 25 squats d'affilée"),
        TestPassage("squats", 2, 3, "35 squats d'affilée en bonne forme"),
        TestPassage("squats", 3, 4, "45 squats d'affilée en bonne forme"),
        TestPassage("squats", 4, 5, "55 squats d'affilée en bonne forme"),
        // Killy
        TestPassage("killy", 1, 2, "45 s de maintien Killy (genoux à 90°)"),
        TestPassage("killy", 2, 3, "90 s de maintien Killy"),
        TestPassage("killy", 3, 4, "150 s de maintien Killy"),
        TestPassage("killy", 4, 5, "210 s de maintien Killy"),
        // Gainage
        TestPassage("gainage", 1, 2, "30 s de gainage facial en position correcte"),
        TestPassage("gainage", 2, 3, "60 s de gainage facial en position correcte"),
        TestPassage("gainage", 3, 4, "90 s de gainage facial en position correcte"),
        TestPassage("gainage", 4, 5, "120 s de gainage facial en position correcte")
    )

    fun getTest(exerciceId: String, depuisNiveau: Int): TestPassage? {
        return tests.firstOrNull {
            it.exerciceId == exerciceId && it.depuisNiveau == depuisNiveau
        }
    }
}

// Consignes courtes par exercice (détail du jour)
object ConsignesForme {

    fun pour(exerciceId: String, niveau: Int): String {
        return when (exerciceId) {
            "tractions" ->
                "Tirage contrôlé, pas d'élan. Arrête 1 rep avant la rupture technique."
            "pompes" ->
                "Corps gainé, poitrine vers le sol. Amplitude complète sans creuser le dos."
            "squats" ->
                "Pieds à plat, genoux dans l'axe des pieds. Descends contrôlé, remonte en poussant."
            "killy" ->
                "Dos et tête plaqués au mur, cuisses horizontales, genoux à 90°, pieds à plat, sans appui des mains. Arrête dès que la position se dégrade."
            "gainage" -> {
                if (niveau >= 5) {
                    "Facial pieds surélevés : dos neutre, bassin rétroversé, pas de creux lombaire. Arrête dès que la position se dégrade."
                } else {
                    "Dos neutre, bassin rétroversé, pas de creux lombaire. Le temps est un plafond, pas un objectif en mauvaise posture."
                }
            }
            else -> ""
        }
    }
}
