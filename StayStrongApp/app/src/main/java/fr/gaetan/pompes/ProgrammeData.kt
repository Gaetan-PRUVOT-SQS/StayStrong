package fr.gaetan.pompes

// Type de série : reps, max, ou maintien en secondes
sealed class TypeSerie

data class SerieFixe(val reps: Int) : TypeSerie()

data class SerieMax(val minimum: Int) : TypeSerie()

// Maintien chronométré (gainage, Killy…)
data class SerieTemps(val secondes: Int, val nom: String = "") : TypeSerie()

// Maintien max avec minimum en secondes
data class SerieTempsMax(val minimumSecondes: Int, val nom: String = "") : TypeSerie()

data class JourProgramme(
    val numero: Int,
    val reposSecondes: Int,
    val series: List<TypeSerie>,
    val pauseJoursApres: Int
)

data class NiveauProgramme(
    val numero: Int,
    val libelleDepart: String,
    val jours: List<JourProgramme>
)

// Un exercice = Pompes, Squats, etc.
data class ExerciceProgramme(
    val id: String,
    val nom: String,
    val niveaux: List<NiveauProgramme>
)

// Données en dur depuis Pompes/, Squats/, Tractions/, Gainage/, Killy/
object ProgrammeData {

    // Ordre de séance (60 s de repos partout) :
    // 1 Tractions (frais) → 2 Pompes → 3 Squats → 4 Killy → 5 Gainage (fin)
    val exercices: List<ExerciceProgramme> = listOf(
        ExerciceProgramme(
            id = "tractions",
            nom = "Tractions",
            niveaux = niveauxTractions()
        ),
        ExerciceProgramme(
            id = "pompes",
            nom = "Pompes",
            niveaux = niveauxPompes()
        ),
        ExerciceProgramme(
            id = "squats",
            nom = "Squats",
            niveaux = niveauxSquats()
        ),
        ExerciceProgramme(
            id = "killy",
            nom = "Killy",
            niveaux = niveauxKilly()
        ),
        ExerciceProgramme(
            id = "gainage",
            nom = "Gainage",
            niveaux = niveauxGainage()
        )
    )

    fun getExercice(id: String): ExerciceProgramme {
        return exercices.first { it.id == id }
    }

    fun getNiveau(exerciceId: String, niveau: Int): NiveauProgramme {
        return getExercice(exerciceId).niveaux.first { it.numero == niveau }
    }

    fun getJour(exerciceId: String, niveau: Int, jour: Int): JourProgramme {
        return getNiveau(exerciceId, niveau).jours.first { it.numero == jour }
    }

    fun nombreJours(exerciceId: String, niveau: Int): Int {
        return getNiveau(exerciceId, niveau).jours.size
    }

    // --- Pompes (dossier Pompes/) ---
    private fun niveauxPompes(): List<NiveauProgramme> {
        return listOf(
            NiveauProgramme(
                numero = 1,
                libelleDepart = "6-10 pompes",
                jours = listOf(
                    jour(1, 60, listOf(f(5), f(6), f(4), f(4), m(5)), 1),
                    jour(2, 60, listOf(f(6), f(7), f(6), f(6), m(7)), 1),
                    jour(3, 60, listOf(f(8), f(10), f(7), f(7), m(10)), 2),
                    jour(4, 60, listOf(f(9), f(11), f(8), f(8), m(11)), 1),
                    jour(5, 60, listOf(f(10), f(12), f(9), f(9), m(13)), 1),
                    jour(6, 60, listOf(f(12), f(13), f(10), f(10), m(15)), 2)
                )
            ),
            NiveauProgramme(
                numero = 2,
                libelleDepart = "11-20 pompes",
                jours = listOf(
                    jour(1, 60, listOf(f(8), f(9), f(7), f(7), m(8)), 1),
                    jour(2, 60, listOf(f(9), f(10), f(8), f(8), m(10)), 1),
                    jour(3, 60, listOf(f(11), f(13), f(9), f(9), m(13)), 2),
                    jour(4, 60, listOf(f(12), f(14), f(10), f(10), m(15)), 1),
                    jour(5, 60, listOf(f(13), f(15), f(11), f(11), m(17)), 1),
                    jour(6, 60, listOf(f(14), f(16), f(13), f(13), m(19)), 2)
                )
            ),
            NiveauProgramme(
                numero = 3,
                libelleDepart = "21-25 pompes",
                jours = listOf(
                    jour(1, 60, listOf(f(12), f(17), f(13), f(13), m(17)), 1),
                    jour(2, 60, listOf(f(14), f(19), f(14), f(14), m(19)), 1),
                    jour(3, 60, listOf(f(16), f(21), f(15), f(15), m(21)), 2),
                    jour(4, 60, listOf(f(18), f(22), f(16), f(16), m(21)), 1),
                    jour(5, 60, listOf(f(20), f(25), f(20), f(20), m(23)), 1),
                    jour(6, 60, listOf(f(23), f(28), f(22), f(22), m(25)), 2)
                )
            ),
            NiveauProgramme(
                numero = 4,
                libelleDepart = "26-30 pompes",
                jours = listOf(
                    jour(1, 60, listOf(f(14), f(18), f(14), f(14), m(20)), 1),
                    jour(2, 60, listOf(f(20), f(25), f(15), f(15), m(23)), 1),
                    jour(3, 60, listOf(f(20), f(27), f(18), f(18), m(25)), 2),
                    jour(4, 60, listOf(f(21), f(25), f(21), f(21), m(27)), 1),
                    jour(5, 60, listOf(f(25), f(29), f(25), f(25), m(30)), 1),
                    jour(6, 60, listOf(f(29), f(33), f(29), f(29), m(33)), 2)
                )
            ),
            NiveauProgramme(
                numero = 5,
                libelleDepart = "31-35 pompes",
                jours = listOf(
                    jour(1, 60, listOf(f(17), f(19), f(15), f(15), m(20)), 1),
                    jour(2, 60, listOf(f(10), f(10), f(13), f(13), f(10), f(10), f(9), m(25)), 1),
                    jour(3, 60, listOf(f(13), f(13), f(15), f(15), f(12), f(12), f(10), m(30)), 2)
                )
            )
        )
    }

    // --- Squats (dossier Squats/) ---
    private fun niveauxSquats(): List<NiveauProgramme> {
        return listOf(
            NiveauProgramme(
                numero = 1,
                libelleDepart = "Niveau 1",
                jours = listOf(
                    jour(1, 60, listOf(f(12), f(15), f(10), f(10), m(12)), 1),
                    jour(2, 60, listOf(f(15), f(18), f(15), f(15), m(18)), 1),
                    jour(3, 60, listOf(f(20), f(25), f(18), f(18), m(25)), 2),
                    jour(4, 60, listOf(f(22), f(28), f(20), f(20), m(28)), 1),
                    jour(5, 60, listOf(f(25), f(30), f(22), f(22), m(32)), 1),
                    jour(6, 60, listOf(f(30), f(33), f(25), f(25), m(38)), 2)
                )
            ),
            NiveauProgramme(
                numero = 2,
                libelleDepart = "Niveau 2",
                jours = listOf(
                    jour(1, 60, listOf(f(20), f(22), f(18), f(18), m(20)), 1),
                    jour(2, 60, listOf(f(22), f(25), f(20), f(20), m(25)), 1),
                    jour(3, 60, listOf(f(28), f(32), f(22), f(22), m(32)), 2),
                    jour(4, 60, listOf(f(30), f(35), f(25), f(25), m(38)), 1),
                    jour(5, 60, listOf(f(32), f(38), f(28), f(28), m(42)), 1),
                    jour(6, 60, listOf(f(35), f(40), f(32), f(32), m(48)), 2)
                )
            ),
            NiveauProgramme(
                numero = 3,
                libelleDepart = "Niveau 3",
                jours = listOf(
                    jour(1, 60, listOf(f(30), f(42), f(32), f(32), m(42)), 1),
                    jour(2, 60, listOf(f(35), f(48), f(35), f(35), m(48)), 1),
                    jour(3, 60, listOf(f(40), f(52), f(38), f(38), m(52)), 2),
                    jour(4, 60, listOf(f(45), f(55), f(40), f(40), m(55)), 1),
                    jour(5, 60, listOf(f(50), f(62), f(50), f(50), m(62)), 1),
                    jour(6, 60, listOf(f(58), f(70), f(55), f(55), m(70)), 2)
                )
            ),
            NiveauProgramme(
                numero = 4,
                libelleDepart = "Niveau 4",
                jours = listOf(
                    jour(1, 60, listOf(f(35), f(45), f(35), f(35), m(50)), 1),
                    jour(2, 60, listOf(f(45), f(55), f(40), f(40), m(55)), 1),
                    jour(3, 60, listOf(f(50), f(65), f(45), f(45), m(62)), 2),
                    jour(4, 60, listOf(f(55), f(70), f(50), f(50), m(68)), 1),
                    jour(5, 60, listOf(f(62), f(72), f(60), f(60), m(75)), 1),
                    jour(6, 60, listOf(f(72), f(82), f(70), f(70), m(82)), 2)
                )
            ),
            NiveauProgramme(
                numero = 5,
                libelleDepart = "Niveau 5",
                jours = listOf(
                    jour(1, 60, listOf(f(42), f(48), f(38), f(38), m(50)), 1),
                    jour(2, 60, listOf(f(25), f(25), f(32), f(32), f(25), f(25), f(22), m(62)), 1),
                    jour(3, 60, listOf(f(32), f(32), f(38), f(38), f(30), f(30), f(25), m(75)), 2)
                )
            )
        )
    }

    // --- Tractions (dossier Tractions/) ---
    private fun niveauxTractions(): List<NiveauProgramme> {
        return listOf(
            NiveauProgramme(
                numero = 1,
                libelleDepart = "1 à 3 tractions",
                jours = listOf(
                    jour(1, 60, listOf(f(2), f(1), f(1), f(1), f(1)), 1),
                    jour(2, 60, listOf(f(2), f(2), f(1), f(1), f(1)), 1),
                    jour(3, 60, listOf(f(2), f(2), f(2), f(1), f(1)), 2),
                    jour(4, 60, listOf(f(2), f(2), f(2), f(2), f(1)), 1),
                    jour(5, 60, listOf(f(3), f(2), f(2), f(2), f(1)), 1),
                    jour(6, 60, listOf(f(3), f(3), f(2), f(2), f(2)), 2)
                )
            ),
            NiveauProgramme(
                numero = 2,
                libelleDepart = "4 à 6 tractions",
                jours = listOf(
                    jour(1, 60, listOf(f(3), f(3), f(2), f(2), f(2)), 1),
                    jour(2, 60, listOf(f(3), f(3), f(3), f(2), f(2)), 1),
                    jour(3, 60, listOf(f(4), f(3), f(3), f(2), f(2)), 2),
                    jour(4, 60, listOf(f(4), f(3), f(3), f(3), f(3)), 1),
                    jour(5, 60, listOf(f(4), f(4), f(3), f(3), f(3)), 1),
                    jour(6, 60, listOf(f(5), f(4), f(4), f(3), f(3)), 2)
                )
            ),
            NiveauProgramme(
                numero = 3,
                libelleDepart = "7 à 10 tractions",
                jours = listOf(
                    jour(1, 60, listOf(f(5), f(4), f(4), f(3), m(4)), 1),
                    jour(2, 60, listOf(f(5), f(5), f(4), f(4), m(5)), 1),
                    jour(3, 60, listOf(f(6), f(5), f(4), f(4), m(5)), 2),
                    jour(4, 60, listOf(f(6), f(5), f(5), f(4), m(6)), 1),
                    jour(5, 60, listOf(f(6), f(6), f(5), f(5), m(6)), 1),
                    jour(6, 60, listOf(f(7), f(6), f(5), f(5), m(7)), 2)
                )
            ),
            NiveauProgramme(
                numero = 4,
                libelleDepart = "11 à 15 tractions",
                jours = listOf(
                    jour(1, 60, listOf(f(7), f(6), f(5), f(5), m(6)), 1),
                    jour(2, 60, listOf(f(7), f(7), f(6), f(5), m(7)), 1),
                    jour(3, 60, listOf(f(8), f(7), f(6), f(6), m(7)), 2),
                    jour(4, 60, listOf(f(8), f(7), f(7), f(6), m(8)), 1),
                    jour(5, 60, listOf(f(9), f(8), f(7), f(6), m(8)), 1),
                    jour(6, 60, listOf(f(9), f(8), f(8), f(7), m(9)), 2)
                )
            ),
            NiveauProgramme(
                numero = 5,
                libelleDepart = "16 tractions et plus",
                jours = listOf(
                    jour(1, 60, listOf(f(10), f(9), f(8), f(7), m(9)), 1),
                    jour(2, 60, listOf(f(10), f(9), f(9), f(8), m(10)), 1),
                    jour(3, 60, listOf(f(11), f(10), f(9), f(8), m(10)), 2),
                    jour(4, 60, listOf(f(11), f(10), f(10), f(9), m(11)), 1),
                    jour(5, 60, listOf(f(12), f(11), f(10), f(9), m(11)), 1),
                    jour(6, 60, listOf(f(12), f(11), f(11), f(10), m(12)), 2)
                )
            )
        )
    }

    // --- Gainage (dossier Gainage/) — chaque ligne "n x t s" = n séries de t secondes ---
    private fun niveauxGainage(): List<NiveauProgramme> {
        // repos 60 s partout (règle app)
        return listOf(
            NiveauProgramme(
                numero = 1,
                libelleDepart = "moins de 30 s facial",
                jours = listOf(
                    jourGainage(1, 1, 3, 15, 3, 10, 3, 10, "Gainage facial"),
                    jourGainage(2, 1, 3, 18, 3, 12, 3, 12, "Gainage facial"),
                    jourGainage(3, 2, 3, 20, 3, 15, 3, 15, "Gainage facial"),
                    jourGainage(4, 1, 3, 22, 3, 15, 3, 15, "Gainage facial"),
                    jourGainage(5, 1, 3, 25, 3, 18, 3, 18, "Gainage facial"),
                    jourGainage(6, 2, 3, 30, 3, 20, 3, 20, "Gainage facial")
                )
            ),
            NiveauProgramme(
                numero = 2,
                libelleDepart = "30 à 60 s facial",
                jours = listOf(
                    jourGainage(1, 1, 3, 30, 3, 20, 3, 20, "Gainage facial"),
                    jourGainage(2, 1, 3, 35, 3, 22, 3, 22, "Gainage facial"),
                    jourGainage(3, 2, 3, 40, 3, 25, 3, 25, "Gainage facial"),
                    jourGainage(4, 1, 3, 45, 3, 28, 3, 28, "Gainage facial"),
                    jourGainage(5, 1, 3, 50, 3, 30, 3, 30, "Gainage facial"),
                    jourGainage(6, 2, 3, 60, 3, 35, 3, 35, "Gainage facial")
                )
            ),
            NiveauProgramme(
                numero = 3,
                libelleDepart = "60 à 90 s facial",
                jours = listOf(
                    jourGainage(1, 1, 4, 40, 3, 30, 3, 30, "Gainage facial"),
                    jourGainage(2, 1, 4, 45, 3, 33, 3, 33, "Gainage facial"),
                    jourGainage(3, 2, 4, 50, 3, 35, 3, 35, "Gainage facial"),
                    jourGainage(4, 1, 4, 55, 3, 40, 3, 40, "Gainage facial"),
                    jourGainage(5, 1, 4, 60, 3, 45, 3, 45, "Gainage facial"),
                    jourGainage(6, 2, 4, 70, 3, 50, 3, 50, "Gainage facial")
                )
            ),
            NiveauProgramme(
                numero = 4,
                libelleDepart = "90 à 120 s facial",
                jours = listOf(
                    jourGainage(1, 1, 4, 60, 3, 45, 3, 45, "Gainage facial"),
                    jourGainage(2, 1, 4, 65, 3, 48, 3, 48, "Gainage facial"),
                    jourGainage(3, 2, 4, 70, 3, 50, 3, 50, "Gainage facial"),
                    jourGainage(4, 1, 4, 80, 3, 55, 3, 55, "Gainage facial"),
                    jourGainage(5, 1, 4, 90, 3, 60, 3, 60, "Gainage facial"),
                    jourGainage(6, 2, 4, 100, 3, 70, 3, 70, "Gainage facial")
                )
            ),
            NiveauProgramme(
                numero = 5,
                libelleDepart = "plus de 120 s facial",
                jours = listOf(
                    jourGainage(1, 1, 4, 45, 3, 30, 3, 30, "Facial pieds surélevés"),
                    jourGainage(2, 1, 4, 50, 3, 33, 3, 33, "Facial pieds surélevés"),
                    jourGainage(3, 2, 4, 55, 3, 35, 3, 35, "Facial pieds surélevés"),
                    jourGainage(4, 1, 4, 60, 3, 40, 3, 40, "Facial pieds surélevés"),
                    jourGainage(5, 1, 4, 70, 3, 45, 3, 45, "Facial pieds surélevés"),
                    jourGainage(6, 2, 4, 80, 3, 50, 3, 50, "Facial pieds surélevés")
                )
            )
        )
    }

    // --- Killy (dossier Killy/) — maintien en secondes, série 5 = max ---
    private fun niveauxKilly(): List<NiveauProgramme> {
        return listOf(
            NiveauProgramme(
                numero = 1,
                libelleDepart = "maintien moins de 45 s",
                jours = listOf(
                    jour(1, 60, listOf(t(20), t(20), t(15), t(15), tm(20)), 1),
                    jour(2, 60, listOf(t(25), t(20), t(20), t(15), tm(25)), 1),
                    jour(3, 60, listOf(t(25), t(25), t(20), t(20), tm(25)), 2),
                    jour(4, 60, listOf(t(30), t(25), t(20), t(20), tm(30)), 1),
                    jour(5, 60, listOf(t(30), t(30), t(25), t(20), tm(30)), 1),
                    jour(6, 60, listOf(t(35), t(30), t(25), t(25), tm(35)), 2)
                )
            ),
            NiveauProgramme(
                numero = 2,
                libelleDepart = "maintien 45 à 90 s",
                jours = listOf(
                    jour(1, 60, listOf(t(40), t(35), t(30), t(30), tm(40)), 1),
                    jour(2, 60, listOf(t(45), t(40), t(35), t(30), tm(45)), 1),
                    jour(3, 60, listOf(t(50), t(45), t(35), t(35), tm(50)), 2),
                    jour(4, 60, listOf(t(55), t(45), t(40), t(40), tm(55)), 1),
                    jour(5, 60, listOf(t(60), t(50), t(45), t(40), tm(60)), 1),
                    jour(6, 60, listOf(t(70), t(55), t(50), t(45), tm(70)), 2)
                )
            ),
            NiveauProgramme(
                numero = 3,
                libelleDepart = "maintien 90 à 150 s",
                jours = listOf(
                    jour(1, 60, listOf(t(60), t(55), t(45), t(45), tm(60)), 1),
                    jour(2, 60, listOf(t(70), t(60), t(50), t(45), tm(70)), 1),
                    jour(3, 60, listOf(t(75), t(65), t(55), t(50), tm(75)), 2),
                    jour(4, 60, listOf(t(85), t(70), t(60), t(55), tm(85)), 1),
                    jour(5, 60, listOf(t(90), t(75), t(65), t(60), tm(90)), 1),
                    jour(6, 60, listOf(t(100), t(85), t(70), t(65), tm(100)), 2)
                )
            ),
            NiveauProgramme(
                numero = 4,
                libelleDepart = "maintien 150 à 210 s",
                jours = listOf(
                    jour(1, 60, listOf(t(100), t(85), t(75), t(70), tm(100)), 1),
                    jour(2, 60, listOf(t(110), t(90), t(80), t(70), tm(110)), 1),
                    jour(3, 60, listOf(t(120), t(100), t(85), t(75), tm(120)), 2),
                    jour(4, 60, listOf(t(130), t(105), t(90), t(80), tm(130)), 1),
                    jour(5, 60, listOf(t(140), t(115), t(95), t(85), tm(140)), 1),
                    jour(6, 60, listOf(t(150), t(120), t(100), t(90), tm(150)), 2)
                )
            ),
            NiveauProgramme(
                numero = 5,
                libelleDepart = "maintien plus de 210 s",
                jours = listOf(
                    jour(1, 60, listOf(t(140), t(120), t(100), t(90), tm(140)), 1),
                    jour(2, 60, listOf(t(150), t(125), t(105), t(95), tm(150)), 1),
                    jour(3, 60, listOf(t(160), t(135), t(110), t(100), tm(160)), 2),
                    jour(4, 60, listOf(t(170), t(140), t(120), t(105), tm(170)), 1),
                    jour(5, 60, listOf(t(180), t(150), t(125), t(110), tm(180)), 1),
                    jour(6, 60, listOf(t(190), t(160), t(130), t(115), tm(190)), 2)
                )
            )
        )
    }

    private fun f(reps: Int) = SerieFixe(reps)
    private fun m(minimum: Int) = SerieMax(minimum)
    private fun t(secondes: Int) = SerieTemps(secondes)
    private fun tm(minimumSecondes: Int) = SerieTempsMax(minimumSecondes)

    // construit les séries gainage : facial + latéral d + latéral g
    private fun jourGainage(
        numero: Int,
        pause: Int,
        nbFacial: Int,
        secFacial: Int,
        nbLatD: Int,
        secLatD: Int,
        nbLatG: Int,
        secLatG: Int,
        nomFacial: String
    ): JourProgramme {
        val series = mutableListOf<TypeSerie>()
        repeat(nbFacial) {
            series.add(SerieTemps(secFacial, nomFacial))
        }
        repeat(nbLatD) {
            series.add(SerieTemps(secLatD, "Gainage latéral droit"))
        }
        repeat(nbLatG) {
            series.add(SerieTemps(secLatG, "Gainage latéral gauche"))
        }
        return JourProgramme(
            numero = numero,
            reposSecondes = 60,
            series = series,
            pauseJoursApres = pause
        )
    }

    private fun jour(
        numero: Int,
        repos: Int,
        series: List<TypeSerie>,
        pause: Int
    ): JourProgramme {
        return JourProgramme(
            numero = numero,
            reposSecondes = repos,
            series = series,
            pauseJoursApres = pause
        )
    }
}
