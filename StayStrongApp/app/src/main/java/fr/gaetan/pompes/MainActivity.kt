package fr.gaetan.pompes

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.gaetan.pompes.ui.AccueilScreen
import fr.gaetan.pompes.ui.AgendaScreen
import fr.gaetan.pompes.ui.ArticleDetailScreen
import fr.gaetan.pompes.ui.ArticlesScreen
import fr.gaetan.pompes.ui.DemarrageScreen
import fr.gaetan.pompes.ui.DetailJourScreen
import fr.gaetan.pompes.ui.ExercicesScreen
import fr.gaetan.pompes.ui.FinScreen
import fr.gaetan.pompes.ui.FooterNav
import fr.gaetan.pompes.ui.NiveauxScreen
import fr.gaetan.pompes.ui.OngletFooter
import fr.gaetan.pompes.ui.SeanceCompleteSetupScreen
import fr.gaetan.pompes.ui.SeanceScreen
import fr.gaetan.pompes.ui.TestPassageScreen
import fr.gaetan.pompes.ui.theme.PompesTheme
import fr.gaetan.pompes.ui.theme.fondApp

enum class Ecran {
    DEMARRAGE,
    EXERCICES,
    ARTICLES,
    ARTICLE_DETAIL,
    AGENDA,
    SEANCE_COMPLETE_SETUP,
    NIVEAUX,
    TEST_PASSAGE,
    JOURS,
    DETAIL_JOUR,
    SEANCE,
    FIN
}

class MainActivity : ComponentActivity() {

    private val seanceViewModel: SeanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val progression = ProgressionStore(applicationContext)

        setContent {
            var modeSombre by remember { mutableStateOf(progression.getModeSombre()) }

            PompesTheme(modeSombre = modeSombre) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = fondApp
                ) {
                    var ecran by remember { mutableStateOf(Ecran.DEMARRAGE) }
                    var exerciceId by remember { mutableStateOf("tractions") }
                    var niveauCourant by remember { mutableStateOf(1) }
                    var jourDetail by remember { mutableStateOf(1) }
                    var tickProgression by remember { mutableStateOf(0) }
                    var jourFinMarque by remember { mutableStateOf(false) }
                    var articleId by remember { mutableStateOf("exercices") }

                    // footer visible sur les onglets principaux (+ détail article)
                    val afficherFooter = ecran == Ecran.EXERCICES ||
                        ecran == Ecran.ARTICLES ||
                        ecran == Ecran.ARTICLE_DETAIL
                    val ongletFooter = when (ecran) {
                        Ecran.ARTICLES, Ecran.ARTICLE_DETAIL -> OngletFooter.ARTICLES
                        else -> OngletFooter.ENTRAINEMENT
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .safeDrawingPadding()
                    ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {

                        // séance complète
                        var modeComplet by remember { mutableStateOf(false) }
                        var indexExoComplet by remember { mutableStateOf(0) }
                        var alerteComplete by remember { mutableStateOf<AlertePause?>(null) }
                        var enAttenteLancementComplet by remember {
                            mutableStateOf<Pair<Int, Int>?>(null)
                        }

                        // import JSON
                        var dialogueImport by remember { mutableStateOf(false) }
                        var texteImport by remember { mutableStateOf("") }

                        // préremplissage séance complète (Reprendre)
                        var prefillComplet by remember {
                            mutableStateOf<PositionSeanceComplete?>(null)
                        }

                        val seanceState by seanceViewModel.uiState.collectAsState()
                        val doitVibrer by seanceViewModel.vibration.collectAsState()

                        val joursFaits = remember(exerciceId, niveauCourant, tickProgression) {
                            progression.getJoursFaits(exerciceId, niveauCourant)
                        }
                        val jourSuggere = remember(exerciceId, niveauCourant, tickProgression) {
                            progression.getJourSuggere(exerciceId, niveauCourant)
                        }
                        val sessionsAgenda = remember(tickProgression) {
                            progression.getSessions()
                        }
                        val dernierePosition = remember(tickProgression) {
                            progression.getDernierePositionExo()
                        }
                        val derniereSeanceComplete = remember(tickProgression) {
                            progression.getDerniereSeanceComplete()
                        }

                        // écran allumé pendant séance
                        DisposableEffect(ecran) {
                            if (ecran == Ecran.SEANCE) {
                                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                            } else {
                                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                            }
                            onDispose {
                                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                            }
                        }

                        LaunchedEffect(doitVibrer) {
                            if (doitVibrer) {
                                vibrerTelephone()
                                seanceViewModel.vibrationConsommee()
                            }
                        }

                        LaunchedEffect(seanceState?.etat) {
                            val etatCourant = seanceState
                            if (etatCourant != null && etatCourant.etat == EtatSeance.FIN && !jourFinMarque) {
                                progression.marquerJourFait(
                                    etatCourant.exerciceId,
                                    etatCourant.niveauNumero,
                                    etatCourant.jourNumero
                                )
                                tickProgression = tickProgression + 1
                                jourFinMarque = true

                                if (modeComplet) {
                                    val total = ProgrammeData.exercices.size
                                    if (indexExoComplet < total - 1) {
                                        // enchaîne l'exercice suivant
                                        indexExoComplet = indexExoComplet + 1
                                        val suivant = ProgrammeData.exercices[indexExoComplet]
                                        exerciceId = suivant.id
                                        jourFinMarque = false
                                        seanceViewModel.demarrer(
                                            suivant.id,
                                            niveauCourant,
                                            jourDetail
                                        )
                                        ecran = Ecran.SEANCE
                                    } else {
                                        ecran = Ecran.FIN
                                    }
                                } else {
                                    ecran = Ecran.FIN
                                }
                            }
                        }

                        // dialogue import
                        if (dialogueImport) {
                            AlertDialog(
                                onDismissRequest = {
                                    dialogueImport = false
                                    texteImport = ""
                                },
                                title = { Text("Importer des données") },
                                text = {
                                    Column {
                                        Text(
                                            text = "Colle le JSON exporté (ou utilise Coller le presse-papiers)."
                                        )
                                        OutlinedTextField(
                                            value = texteImport,
                                            onValueChange = { texteImport = it },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(min = 120.dp, max = 220.dp)
                                                .padding(top = 12.dp)
                                                .verticalScroll(rememberScrollState()),
                                            placeholder = { Text("{\n  \"app\": \"Stay Strong\" ...") },
                                            maxLines = 12
                                        )
                                    }
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            val ok = progression.importerJson(texteImport.trim())
                                            if (ok) {
                                                tickProgression = tickProgression + 1
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Import réussi",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                dialogueImport = false
                                                texteImport = ""
                                            } else {
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "JSON invalide",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    ) {
                                        Text("Importer")
                                    }
                                },
                                dismissButton = {
                                    Column {
                                        TextButton(
                                            onClick = {
                                                val clip = getSystemService(CLIPBOARD_SERVICE)
                                                    as ClipboardManager
                                                val data = clip.primaryClip
                                                if (data != null && data.itemCount > 0) {
                                                    val t = data.getItemAt(0).coerceToText(
                                                        this@MainActivity
                                                    ).toString()
                                                    if (t.isNotBlank()) {
                                                        texteImport = t
                                                    }
                                                }
                                            }
                                        ) {
                                            Text("Coller")
                                        }
                                        TextButton(
                                            onClick = {
                                                dialogueImport = false
                                                texteImport = ""
                                            }
                                        ) {
                                            Text("Annuler")
                                        }
                                    }
                                }
                            )
                        }

                        // dialogue pause séance complète
                        if (alerteComplete != null && enAttenteLancementComplet != null) {
                            val alerte = alerteComplete!!
                            val params = enAttenteLancementComplet!!
                            AlertDialog(
                                onDismissRequest = {
                                    alerteComplete = null
                                    enAttenteLancementComplet = null
                                },
                                title = { Text("Rappel de pause") },
                                text = {
                                    Text(alerte.message + "\n\nTu peux quand même lancer la séance.")
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            alerteComplete = null
                                            enAttenteLancementComplet = null
                                            demarrerSeanceComplete(
                                                params.first,
                                                params.second,
                                                progression,
                                                seanceViewModel,
                                                onReady = { id, idx ->
                                                    exerciceId = id
                                                    indexExoComplet = idx
                                                    niveauCourant = params.first
                                                    jourDetail = params.second
                                                    modeComplet = true
                                                    jourFinMarque = false
                                                    ecran = Ecran.SEANCE
                                                }
                                            )
                                        }
                                    ) {
                                        Text("Lancer quand même")
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = {
                                            alerteComplete = null
                                            enAttenteLancementComplet = null
                                        }
                                    ) {
                                        Text("Annuler")
                                    }
                                }
                            )
                        }

                        when (ecran) {
                            Ecran.DEMARRAGE -> {
                                DemarrageScreen(
                                    onCommencer = { ecran = Ecran.EXERCICES }
                                )
                            }
                            Ecran.ARTICLES -> {
                                ArticlesScreen(
                                    onOuvrirArticle = { id ->
                                        articleId = id
                                        ecran = Ecran.ARTICLE_DETAIL
                                    }
                                )
                            }
                            Ecran.ARTICLE_DETAIL -> {
                                ArticleDetailScreen(
                                    articleId = articleId,
                                    onRetour = { ecran = Ecran.ARTICLES }
                                )
                            }
                            Ecran.EXERCICES -> {
                                ExercicesScreen(
                                    dernierePosition = dernierePosition,
                                    derniereSeanceComplete = derniereSeanceComplete,
                                    modeSombre = modeSombre,
                                    onChoisirExercice = { id ->
                                        exerciceId = id
                                        modeComplet = false
                                        ecran = Ecran.NIVEAUX
                                    },
                                    onReprendreExo = { pos ->
                                        exerciceId = pos.exerciceId
                                        niveauCourant = pos.niveau
                                        jourDetail = pos.jour
                                        modeComplet = false
                                        ecran = Ecran.DETAIL_JOUR
                                    },
                                    onReprendreSeanceComplete = { pos ->
                                        prefillComplet = pos
                                        ecran = Ecran.SEANCE_COMPLETE_SETUP
                                    },
                                    onOuvrirAgenda = {
                                        ecran = Ecran.AGENDA
                                    },
                                    onSeanceComplete = {
                                        prefillComplet = null
                                        ecran = Ecran.SEANCE_COMPLETE_SETUP
                                    },
                                    onExporter = {
                                        partagerExport(progression.exporterJson())
                                    },
                                    onImporter = {
                                        // préremplit avec le presse-papiers si possible
                                        val clip = getSystemService(CLIPBOARD_SERVICE)
                                            as ClipboardManager
                                        val data = clip.primaryClip
                                        if (data != null && data.itemCount > 0) {
                                            val t = data.getItemAt(0).coerceToText(
                                                this@MainActivity
                                            ).toString()
                                            if (t.contains("Stay Strong") || t.contains("sessions")) {
                                                texteImport = t
                                            }
                                        }
                                        dialogueImport = true
                                    },
                                    onToggleSombre = { actif ->
                                        modeSombre = actif
                                        progression.setModeSombre(actif)
                                    }
                                )
                            }
                            Ecran.AGENDA -> {
                                AgendaScreen(
                                    sessions = sessionsAgenda,
                                    onRetour = { ecran = Ecran.EXERCICES }
                                )
                            }
                            Ecran.SEANCE_COMPLETE_SETUP -> {
                                SeanceCompleteSetupScreen(
                                    niveauInitial = prefillComplet?.niveau ?: 1,
                                    jourInitial = prefillComplet?.jour ?: 1,
                                    onRetour = { ecran = Ecran.EXERCICES },
                                    onLancer = { niveau, jour ->
                                        val premier = ProgrammeData.exercices.first()
                                        val alerte = progression.verifierPause(premier.id, niveau)
                                        if (alerte != null) {
                                            alerteComplete = alerte
                                            enAttenteLancementComplet = Pair(niveau, jour)
                                        } else {
                                            demarrerSeanceComplete(
                                                niveau,
                                                jour,
                                                progression,
                                                seanceViewModel,
                                                onReady = { id, idx ->
                                                    exerciceId = id
                                                    indexExoComplet = idx
                                                    niveauCourant = niveau
                                                    jourDetail = jour
                                                    modeComplet = true
                                                    jourFinMarque = false
                                                    ecran = Ecran.SEANCE
                                                }
                                            )
                                        }
                                    }
                                )
                            }
                            Ecran.NIVEAUX -> {
                                val exercice = ProgrammeData.getExercice(exerciceId)
                                val debloque = progression.getNiveauDebloque(exerciceId)
                                NiveauxScreen(
                                    exercice = exercice,
                                    niveauDebloque = debloque,
                                    onRetour = { ecran = Ecran.EXERCICES },
                                    onChoisirNiveau = { numero ->
                                        niveauCourant = numero
                                        ecran = Ecran.JOURS
                                    },
                                    onTestPassage = { depuis ->
                                        niveauCourant = depuis
                                        ecran = Ecran.TEST_PASSAGE
                                    },
                                    joursFaitsParNiveau = { n ->
                                        progression.getJoursFaits(exerciceId, n)
                                    }
                                )
                            }
                            Ecran.TEST_PASSAGE -> {
                                val exercice = ProgrammeData.getExercice(exerciceId)
                                val test = TestsPassage.getTest(exerciceId, niveauCourant)
                                if (test != null) {
                                    TestPassageScreen(
                                        exerciceNom = exercice.nom,
                                        test = test,
                                        onRetour = { ecran = Ecran.NIVEAUX },
                                        onValide = {
                                            progression.validerTestPassage(
                                                exerciceId,
                                                niveauCourant
                                            )
                                            tickProgression = tickProgression + 1
                                            ecran = Ecran.NIVEAUX
                                        }
                                    )
                                }
                            }
                            Ecran.JOURS -> {
                                val niveau = ProgrammeData.getNiveau(exerciceId, niveauCourant)
                                AccueilScreen(
                                    niveau = niveau,
                                    jourSuggere = jourSuggere,
                                    joursFaits = joursFaits,
                                    onRetour = { ecran = Ecran.NIVEAUX },
                                    onVoirJour = { numero ->
                                        jourDetail = numero
                                        ecran = Ecran.DETAIL_JOUR
                                    }
                                )
                            }
                            Ecran.DETAIL_JOUR -> {
                                val jour = ProgrammeData.getJour(exerciceId, niveauCourant, jourDetail)
                                val alerte = progression.verifierPause(exerciceId, niveauCourant)
                                DetailJourScreen(
                                    jour = jour,
                                    alertePause = alerte,
                                    consigneForme = ConsignesForme.pour(exerciceId, niveauCourant),
                                    onRetour = { ecran = Ecran.JOURS },
                                    onStart = {
                                        modeComplet = false
                                        jourFinMarque = false
                                        // mémorise pour Reprendre
                                        progression.sauverPositionExo(
                                            exerciceId,
                                            niveauCourant,
                                            jourDetail
                                        )
                                        tickProgression = tickProgression + 1
                                        seanceViewModel.demarrer(
                                            exerciceId,
                                            niveauCourant,
                                            jourDetail
                                        )
                                        ecran = Ecran.SEANCE
                                    }
                                )
                            }
                            Ecran.SEANCE -> {
                                val state = seanceState
                                if (state != null && state.etat != EtatSeance.FIN) {
                                    SeanceScreen(
                                        state = state,
                                        indexExercice = if (modeComplet) indexExoComplet else 0,
                                        totalExercices = if (modeComplet) {
                                            ProgrammeData.exercices.size
                                        } else {
                                            0
                                        },
                                        onSerieTerminee = { seanceViewModel.serieTerminee() },
                                        onPause = { seanceViewModel.pause() },
                                        onReprendre = { seanceViewModel.reprendre() },
                                        onSerieSuivante = { seanceViewModel.serieSuivante() },
                                        onPasserChrono = { seanceViewModel.passerChrono() },
                                        onArreter = {
                                            seanceViewModel.arreter()
                                            modeComplet = false
                                            ecran = Ecran.EXERCICES
                                        }
                                    )
                                }
                            }
                            Ecran.FIN -> {
                                val state = seanceState
                                if (modeComplet && indexExoComplet >= ProgrammeData.exercices.size - 1) {
                                    FinScreen(
                                        titre = "Séance complète terminée !",
                                        sousTitre = "Les 5 exercices du jour ${state?.jourNumero ?: ""} sont faits.",
                                        texteBouton = "Retour aux exercices",
                                        onRetour = {
                                            seanceViewModel.arreter()
                                            modeComplet = false
                                            ecran = Ecran.EXERCICES
                                        }
                                    )
                                } else if (state != null) {
                                    val textePause = if (state.pauseJoursApres <= 1) {
                                        "Repos minimum avant le prochain jour : 1 jour."
                                    } else {
                                        "Repos minimum avant le prochain jour : ${state.pauseJoursApres} jours."
                                    }
                                    FinScreen(
                                        titre = "Jour ${state.jourNumero} terminé !",
                                        sousTitre = textePause,
                                        texteBouton = "Retour",
                                        onRetour = {
                                            seanceViewModel.arreter()
                                            ecran = Ecran.JOURS
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // barre du bas : Entraînement / Articles
                    if (afficherFooter) {
                        FooterNav(
                            ongletActif = ongletFooter,
                            onChoisir = { onglet ->
                                when (onglet) {
                                    OngletFooter.ENTRAINEMENT -> {
                                        ecran = Ecran.EXERCICES
                                    }
                                    OngletFooter.ARTICLES -> {
                                        ecran = Ecran.ARTICLES
                                    }
                                }
                            }
                        )
                    }
                    }
                }
            }
        }
    }

    private fun demarrerSeanceComplete(
        niveau: Int,
        jour: Int,
        progression: ProgressionStore,
        viewModel: SeanceViewModel,
        onReady: (exerciceId: String, index: Int) -> Unit
    ) {
        progression.sauverDerniereSeanceComplete(niveau, jour)
        val premier = ProgrammeData.exercices.first()
        // mémorise aussi la position du 1er exo
        progression.sauverPositionExo(premier.id, niveau, jour)
        viewModel.demarrer(premier.id, niveau, jour)
        onReady(premier.id, 0)
    }

    private fun partagerExport(json: String) {
        // copie aussi dans le presse-papiers pour coller facilement
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Stay Strong", json))
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/json"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Stay Strong — sauvegarde")
        intent.putExtra(Intent.EXTRA_TEXT, json)
        startActivity(Intent.createChooser(intent, "Exporter mes données"))
        Toast.makeText(this, "Copié dans le presse-papiers", Toast.LENGTH_SHORT).show()
    }

    private fun vibrerTelephone() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = getSystemService(VibratorManager::class.java)
            manager.defaultVibrator.vibrate(
                VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(400)
            }
        }
    }
}
