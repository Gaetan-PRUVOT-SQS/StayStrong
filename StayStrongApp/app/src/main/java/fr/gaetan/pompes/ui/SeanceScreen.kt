package fr.gaetan.pompes.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gaetan.pompes.EtatSeance
import fr.gaetan.pompes.SeanceUiState
import kotlinx.coroutines.delay
import fr.gaetan.pompes.ui.theme.AccentCuivre
import fr.gaetan.pompes.ui.theme.SuccesVert
import fr.gaetan.pompes.ui.theme.fondApp
import fr.gaetan.pompes.ui.theme.texteApp
import fr.gaetan.pompes.ui.theme.texteGrisApp
import fr.gaetan.pompes.ui.theme.carteApp
import fr.gaetan.pompes.ui.theme.secondaireApp
import fr.gaetan.pompes.ui.theme.ligneApp
import fr.gaetan.pompes.ui.theme.onAccentApp

@Composable
fun SeanceScreen(
    state: SeanceUiState,
    // progression séance complète (optionnel)
    indexExercice: Int = 0,
    totalExercices: Int = 0,
    onSerieTerminee: () -> Unit,
    onPause: () -> Unit,
    onReprendre: () -> Unit,
    onSerieSuivante: () -> Unit,
    onPasserChrono: () -> Unit,
    onArreter: () -> Unit
) {
    // feedback court après validation d'une série
    var messageSerie by remember { mutableStateOf<String?>(null) }
    var derniereSerieVue by remember { mutableStateOf(-1) }

    LaunchedEffect(state.etat, state.indexSerie) {
        if (state.etat == EtatSeance.REPOS && state.indexSerie != derniereSerieVue) {
            val num = state.indexSerie + 1
            messageSerie = "✓  Série $num terminée !"
            derniereSerieVue = state.indexSerie
            delay(1600)
            messageSerie = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondApp)
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

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                                BoutonPrincipal(
                                    texte = "✓  Série terminée",
                                    onClick = onSerieTerminee
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
                                BoutonPrincipal(
                                    texte = "✓  Série terminée",
                                    onClick = onSerieTerminee
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.height(16.dp))
                            BoutonPrincipal(
                                texte = "✓  Série terminée",
                                onClick = onSerieTerminee
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
                            BoutonPrincipal(
                                texte = "→  Série suivante",
                                onClick = onSerieSuivante
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
                                color = texteApp
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
                                color = texteApp,
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
                                onClick = onPasserChrono
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
            }
        }
    }
}

@Composable
private fun BoutonPrincipal(texte: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AccentCuivre,
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
