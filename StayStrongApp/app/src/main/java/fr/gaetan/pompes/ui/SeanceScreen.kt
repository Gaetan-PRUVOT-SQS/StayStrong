package fr.gaetan.pompes.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gaetan.pompes.EtatSeance
import fr.gaetan.pompes.R
import fr.gaetan.pompes.SeanceUiState
import kotlinx.coroutines.delay
import fr.gaetan.pompes.ui.theme.AccentCuivre
import fr.gaetan.pompes.ui.theme.SuccesVert
import fr.gaetan.pompes.ui.theme.fondApp
import fr.gaetan.pompes.ui.theme.texteApp
import fr.gaetan.pompes.ui.theme.texteGrisApp
import fr.gaetan.pompes.ui.theme.carteApp
import fr.gaetan.pompes.ui.theme.ligneApp
import fr.gaetan.pompes.ui.theme.onAccentApp

@Composable
fun SeanceScreen(
    state: SeanceUiState,
    consigneForme: String = "",
    // progression séance complète (optionnel)
    indexExercice: Int = 0,
    totalExercices: Int = 0,
    onSerieTerminee: (repsFaites: Int?) -> Unit,
    onPause: () -> Unit,
    onReprendre: () -> Unit,
    onSerieSuivante: () -> Unit,
    onPasserChrono: () -> Unit,
    onArreter: () -> Unit
) {
    // feedback court après validation d'une série
    var messageSerie by remember { mutableStateOf<String?>(null) }
    var derniereSerieVue by remember { mutableStateOf(-1) }
    // saisie des reps sur série max
    var texteReps by remember { mutableStateOf("") }

    LaunchedEffect(state.etat, state.indexSerie) {
        if (state.etat == EtatSeance.REPOS && state.indexSerie != derniereSerieVue) {
            val num = state.indexSerie + 1
            messageSerie = "✓  Série $num terminée !"
            derniereSerieVue = state.indexSerie
            delay(1600)
            messageSerie = null
        }
        // reset saisie à chaque nouvelle série
        if (state.etat == EtatSeance.SERIE) {
            texteReps = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondApp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${state.exerciceNom} · Niv. ${state.niveauNumero} · J${state.jourNumero}",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = texteApp
            )
            Text(
                text = "Arrêter",
                fontSize = 16.sp,
                color = texteApp,
                modifier = Modifier
                    .clickable { onArreter() }
                    .padding(8.dp)
            )
        }

        // barre séance complète
        if (totalExercices > 0) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Exercice ${indexExercice + 1} / $totalExercices",
                fontSize = 13.sp,
                color = texteGrisApp
            )
            Spacer(modifier = Modifier.height(6.dp))
            val prog = (indexExercice + 1).toFloat() / totalExercices.toFloat()
            LinearProgressIndicator(
                progress = { prog },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = AccentCuivre,
                trackColor = ligneApp,
                strokeCap = StrokeCap.Round
            )
        }

        // image + consigne de forme
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(carteApp)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageSeance(state.exerciceId)),
                contentDescription = state.exerciceNom,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (consigneForme.isNotEmpty()) {
                    consigneForme
                } else {
                    "Garde une bonne forme jusqu'à la fin de la série."
                },
                fontSize = 13.sp,
                color = texteApp,
                modifier = Modifier.weight(1f)
            )
        }

        // toast fin de série
        AnimatedVisibility(
            visible = messageSerie != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SuccesVert, RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = messageSerie ?: "",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = onAccentApp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "SÉRIE ${state.indexSerie + 1} / ${state.totalSeries}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = texteGrisApp,
                letterSpacing = 1.sp
            )
            Text(
                text = state.libelleCible,
                fontSize = if (state.estMaintien) 22.sp else 36.sp,
                fontWeight = FontWeight.Bold,
                color = texteApp,
                textAlign = TextAlign.Center
            )

            when (state.etat) {
                EtatSeance.SERIE -> {
                    if (state.estMaintien) {
                        if (state.maintienTermine) {
                            Text(
                                text = "MAINTIEN TERMINÉ",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = SuccesVert,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "0s",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = texteApp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            BoutonSerieTerminee(
                                state = state,
                                texteReps = texteReps,
                                onTexteReps = { texteReps = it },
                                onValider = onSerieTerminee
                            )
                        } else {
                            Text(
                                text = "MAINTIEN",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = texteGrisApp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${state.tempsRestant}s",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentCuivre
                            )
                            val total = if (state.maintienTotal > 0) {
                                state.maintienTotal.toFloat()
                            } else {
                                1f
                            }
                            LinearProgressIndicator(
                                progress = { state.tempsRestant.toFloat() / total },
                                modifier = Modifier
                                    .width(160.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = AccentCuivre,
                                trackColor = ligneApp,
                                strokeCap = StrokeCap.Round
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            secondaireAppArrondi(
                                texte = "Pause",
                                onClick = onPause
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            secondaireAppArrondi(
                                texte = "Passer le chrono",
                                onClick = onPasserChrono
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            BoutonSerieTerminee(
                                state = state,
                                texteReps = texteReps,
                                onTexteReps = { texteReps = it },
                                onValider = onSerieTerminee
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        BoutonSerieTerminee(
                            state = state,
                            texteReps = texteReps,
                            onTexteReps = { texteReps = it },
                            onValider = onSerieTerminee
                        )
                    }
                }
                EtatSeance.REPOS -> {
                    if (state.reposTermine || state.tempsRestant == 0) {
                        Text(
                            text = "REPOS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = texteGrisApp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Repos terminé",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccesVert
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        // bouton fin de repos = jaune
                        BoutonPrincipal(
                            texte = "→  Série suivante",
                            onClick = onSerieSuivante,
                            couleur = Color(0xFFE6A817)
                        )
                    } else {
                        Text(
                            text = "REPOS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = texteGrisApp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${state.tempsRestant}s",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccesVert
                        )
                        val total = if (state.reposTotal > 0) {
                            state.reposTotal.toFloat()
                        } else {
                            1f
                        }
                        LinearProgressIndicator(
                            progress = { state.tempsRestant.toFloat() / total },
                            modifier = Modifier
                                .width(160.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = SuccesVert,
                            trackColor = ligneApp,
                            strokeCap = StrokeCap.Round
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        secondaireAppArrondi(
                            texte = "Pause",
                            onClick = onPause
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        BoutonPrincipal(
                            texte = "⏭  Passer le repos",
                            onClick = onPasserChrono,
                            couleur = SuccesVert
                        )
                    }
                }
                EtatSeance.PAUSE -> {
                    Text(
                        text = "EN PAUSE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = texteGrisApp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${state.tempsRestant}s",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = texteApp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    secondaireAppArrondi(
                        texte = "▶  Reprendre",
                        onClick = onReprendre
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    secondaireAppArrondi(
                        texte = "Passer le chrono",
                        onClick = onPasserChrono
                    )
                }
                EtatSeance.FIN -> {
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BoutonSerieTerminee(
    state: SeanceUiState,
    texteReps: String,
    onTexteReps: (String) -> Unit,
    onValider: (Int?) -> Unit
) {
    if (state.estSerieMax) {
        Text(
            text = "Combien de reps as-tu faites ? (min. ${state.minimumMax})",
            fontSize = 14.sp,
            color = texteGrisApp,
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            value = texteReps,
            onValueChange = { nouveau ->
                // chiffres seulement
                if (nouveau.all { it.isDigit() } && nouveau.length <= 3) {
                    onTexteReps(nouveau)
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { Text("ex. 12") },
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .padding(vertical = 4.dp)
        )
        BoutonPrincipal(
            texte = "✓  Valider la série",
            onClick = {
                val n = texteReps.toIntOrNull()
                if (n != null && n > 0) {
                    onValider(n)
                }
            },
            couleur = AccentCuivre
        )
    } else {
        BoutonPrincipal(
            texte = "✓  Série terminée",
            onClick = { onValider(null) },
            couleur = AccentCuivre
        )
    }
}

private fun imageSeance(exerciceId: String): Int {
    return when (exerciceId) {
        "pompes" -> R.drawable.illu_pompes
        "squats" -> R.drawable.illu_squats
        "tractions" -> R.drawable.illu_tractions
        "gainage" -> R.drawable.illu_gainage
        "killy" -> R.drawable.illu_killy
        else -> R.drawable.illu_pompes
    }
}

@Composable
private fun BoutonPrincipal(
    texte: String,
    onClick: () -> Unit,
    // cuivre pendant l'exercice, vert pendant le repos
    couleur: Color = AccentCuivre
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = couleur,
            contentColor = onAccentApp
        ),
        modifier = Modifier
            .fillMaxWidth(0.78f)
            .heightIn(min = 52.dp)
    ) {
        Text(text = texte, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun secondaireAppArrondi(texte: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = carteApp,
            contentColor = texteApp
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth(0.55f)
            .heightIn(min = 48.dp)
    ) {
        Text(text = texte, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}
