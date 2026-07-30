package fr.gaetan.pompes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class EtatSeance {
    SERIE,
    REPOS,
    PAUSE,
    FIN
}

data class SeanceUiState(
    val etat: EtatSeance,
    val exerciceId: String,
    val exerciceNom: String,
    val niveauNumero: Int,
    val jourNumero: Int,
    val indexSerie: Int,
    val totalSeries: Int,
    val libelleCible: String,
    val tempsRestant: Int,
    val reposTotal: Int,
    val reposTermine: Boolean,
    val pauseJoursApres: Int,
    // chrono de maintien (gainage / killy)
    val estMaintien: Boolean,
    val maintienTotal: Int,
    val maintienTermine: Boolean,
    // série "max" : on demande les reps faites
    val estSerieMax: Boolean,
    val minimumMax: Int
)

// Logique pure testable sans Android
class SeanceController {

    var state: SeanceUiState? = null
        private set

    private var vibrationEnAttente = false
    private var jour: JourProgramme? = null
    // ex. "S5: 14" pour l'agenda
    private val repsMaxSaisies = mutableListOf<String>()

    fun demarrer(exerciceId: String, niveauNumero: Int, jourNumero: Int) {
        val exercice = ProgrammeData.getExercice(exerciceId)
        val j = ProgrammeData.getJour(exerciceId, niveauNumero, jourNumero)
        jour = j
        vibrationEnAttente = false
        repsMaxSaisies.clear()
        val serie0 = j.series[0]
        val maintien = dureeMaintien(serie0)
        state = SeanceUiState(
            etat = EtatSeance.SERIE,
            exerciceId = exerciceId,
            exerciceNom = exercice.nom,
            niveauNumero = niveauNumero,
            jourNumero = j.numero,
            indexSerie = 0,
            totalSeries = j.series.size,
            libelleCible = libelle(serie0),
            tempsRestant = maintien,
            reposTotal = j.reposSecondes,
            reposTermine = false,
            pauseJoursApres = j.pauseJoursApres,
            estMaintien = maintien > 0,
            maintienTotal = maintien,
            maintienTermine = false,
            estSerieMax = serie0 is SerieMax,
            minimumMax = minimumSerie(serie0)
        )
    }

    // repsFaites : obligatoire pour une série max (sinon ignoré)
    fun serieTerminee(repsFaites: Int? = null) {
        val s = state ?: return
        val j = jour ?: return
        if (s.etat != EtatSeance.SERIE) {
            return
        }
        // mémorise les reps sur une série max
        if (s.estSerieMax) {
            if (repsFaites == null || repsFaites <= 0) {
                return
            }
            repsMaxSaisies.add("S${s.indexSerie + 1}: $repsFaites")
        }
        if (s.indexSerie >= j.series.size - 1) {
            state = s.copy(etat = EtatSeance.FIN)
            return
        }
        state = s.copy(
            etat = EtatSeance.REPOS,
            tempsRestant = j.reposSecondes,
            reposTotal = j.reposSecondes,
            reposTermine = false,
            estMaintien = false,
            maintienTotal = 0,
            maintienTermine = false,
            estSerieMax = false,
            minimumMax = 0
        )
        vibrationEnAttente = false
    }

    fun getDetailReps(): String {
        return repsMaxSaisies.joinToString(", ")
    }

    fun pause() {
        val s = state ?: return
        // pause pendant repos OU pendant maintien en cours
        if (s.etat == EtatSeance.REPOS && s.tempsRestant > 0) {
            state = s.copy(etat = EtatSeance.PAUSE)
        } else if (s.etat == EtatSeance.SERIE && s.estMaintien && s.tempsRestant > 0 && !s.maintienTermine) {
            state = s.copy(etat = EtatSeance.PAUSE)
        }
    }

    fun reprendre() {
        val s = state ?: return
        if (s.etat != EtatSeance.PAUSE) {
            return
        }
        // reprendre maintien ou repos
        if (s.estMaintien && !s.maintienTermine) {
            state = s.copy(etat = EtatSeance.SERIE)
        } else {
            state = s.copy(etat = EtatSeance.REPOS)
        }
    }

    fun serieSuivante() {
        val s = state ?: return
        val j = jour ?: return
        if (s.etat == EtatSeance.REPOS && s.tempsRestant == 0) {
            val nouvelIndex = s.indexSerie + 1
            val serie = j.series[nouvelIndex]
            val maintien = dureeMaintien(serie)
            state = s.copy(
                etat = EtatSeance.SERIE,
                indexSerie = nouvelIndex,
                libelleCible = libelle(serie),
                tempsRestant = maintien,
                reposTermine = false,
                estMaintien = maintien > 0,
                maintienTotal = maintien,
                maintienTermine = false,
                estSerieMax = serie is SerieMax,
                minimumMax = minimumSerie(serie)
            )
            vibrationEnAttente = false
        }
    }

    // saute le chrono en cours (repos ou maintien)
    fun passerChrono() {
        val s = state ?: return
        // repos en cours (ou en pause pendant le repos)
        if (s.etat == EtatSeance.REPOS && s.tempsRestant > 0) {
            state = s.copy(tempsRestant = 0, reposTermine = true)
            vibrationEnAttente = true
            return
        }
        if (s.etat == EtatSeance.PAUSE && !s.estMaintien && s.tempsRestant > 0) {
            state = s.copy(
                etat = EtatSeance.REPOS,
                tempsRestant = 0,
                reposTermine = true
            )
            vibrationEnAttente = true
            return
        }
        // maintien en cours (ou en pause pendant le maintien)
        if (s.etat == EtatSeance.SERIE && s.estMaintien && !s.maintienTermine && s.tempsRestant > 0) {
            state = s.copy(tempsRestant = 0, maintienTermine = true)
            vibrationEnAttente = true
            return
        }
        if (s.etat == EtatSeance.PAUSE && s.estMaintien && !s.maintienTermine && s.tempsRestant > 0) {
            state = s.copy(
                etat = EtatSeance.SERIE,
                tempsRestant = 0,
                maintienTermine = true
            )
            vibrationEnAttente = true
        }
    }

    fun tick() {
        val s = state ?: return
        if (s.etat == EtatSeance.REPOS) {
            if (s.tempsRestant <= 0) {
                return
            }
            val nouveau = s.tempsRestant - 1
            if (nouveau == 0) {
                state = s.copy(tempsRestant = 0, reposTermine = true)
                vibrationEnAttente = true
            } else {
                state = s.copy(tempsRestant = nouveau)
            }
            return
        }
        // décompte maintien en série
        if (s.etat == EtatSeance.SERIE && s.estMaintien && !s.maintienTermine) {
            if (s.tempsRestant <= 0) {
                return
            }
            val nouveau = s.tempsRestant - 1
            if (nouveau == 0) {
                state = s.copy(tempsRestant = 0, maintienTermine = true)
                vibrationEnAttente = true
            } else {
                state = s.copy(tempsRestant = nouveau)
            }
        }
    }

    fun arreter() {
        state = null
        jour = null
        vibrationEnAttente = false
        repsMaxSaisies.clear()
    }

    fun consommerVibration(): Boolean {
        if (vibrationEnAttente) {
            vibrationEnAttente = false
            return true
        }
        return false
    }

    private fun dureeMaintien(serie: TypeSerie): Int {
        return when (serie) {
            is SerieTemps -> serie.secondes
            is SerieTempsMax -> serie.minimumSecondes
            else -> 0
        }
    }

    private fun minimumSerie(serie: TypeSerie): Int {
        return when (serie) {
            is SerieMax -> serie.minimum
            is SerieTempsMax -> serie.minimumSecondes
            else -> 0
        }
    }

    private fun libelle(serie: TypeSerie): String {
        return when (serie) {
            is SerieFixe -> "${serie.reps} répétitions"
            is SerieMax -> "max (minimum ${serie.minimum})"
            is SerieTemps -> {
                if (serie.nom.isEmpty()) {
                    "${serie.secondes} s"
                } else {
                    "${serie.nom}\n${serie.secondes} s"
                }
            }
            is SerieTempsMax -> {
                if (serie.nom.isEmpty()) {
                    "max (minimum ${serie.minimumSecondes} s)"
                } else {
                    "${serie.nom}\nmax (min. ${serie.minimumSecondes} s)"
                }
            }
        }
    }
}

class SeanceViewModel : ViewModel() {

    private val controller = SeanceController()
    private val _uiState = MutableStateFlow<SeanceUiState?>(null)
    val uiState: StateFlow<SeanceUiState?> = _uiState.asStateFlow()

    private val _vibration = MutableStateFlow(false)
    val vibration: StateFlow<Boolean> = _vibration.asStateFlow()

    private var jobChrono: Job? = null

    fun demarrer(exerciceId: String, niveauNumero: Int, jourNumero: Int) {
        jobChrono?.cancel()
        controller.demarrer(exerciceId, niveauNumero, jourNumero)
        publier()
        lancerChrono()
    }

    fun serieTerminee(repsFaites: Int? = null) {
        controller.serieTerminee(repsFaites)
        publier()
    }

    fun getDetailReps(): String {
        return controller.getDetailReps()
    }

    fun pause() {
        controller.pause()
        publier()
    }

    fun reprendre() {
        controller.reprendre()
        publier()
    }

    fun serieSuivante() {
        controller.serieSuivante()
        publier()
    }

    fun passerChrono() {
        controller.passerChrono()
        publier()
    }

    fun arreter() {
        jobChrono?.cancel()
        controller.arreter()
        publier()
    }

    fun vibrationConsommee() {
        _vibration.value = false
    }

    private fun publier() {
        _uiState.value = controller.state
        if (controller.consommerVibration()) {
            _vibration.value = true
        }
    }

    private fun lancerChrono() {
        jobChrono?.cancel()
        jobChrono = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                controller.tick()
                publier()
            }
        }
    }
}
