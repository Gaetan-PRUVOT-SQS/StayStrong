package fr.gaetan.pompes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gaetan.pompes.AlertePause
import fr.gaetan.pompes.JourProgramme
import fr.gaetan.pompes.SerieFixe
import fr.gaetan.pompes.SerieMax
import fr.gaetan.pompes.SerieTemps
import fr.gaetan.pompes.SerieTempsMax
import fr.gaetan.pompes.TypeSerie
import fr.gaetan.pompes.ui.theme.AccentCuivre
import fr.gaetan.pompes.ui.theme.fondApp
import fr.gaetan.pompes.ui.theme.texteApp
import fr.gaetan.pompes.ui.theme.texteGrisApp
import fr.gaetan.pompes.ui.theme.secondaireApp
import fr.gaetan.pompes.ui.theme.onAccentApp

@Composable
fun DetailJourScreen(
    jour: JourProgramme,
    alertePause: AlertePause?,
    consigneForme: String,
    onRetour: () -> Unit,
    onStart: () -> Unit
) {
    var montrerAlerte by remember { mutableStateOf(false) }

    if (montrerAlerte && alertePause != null) {
        AlertDialog(
            onDismissRequest = { montrerAlerte = false },
            title = { Text("Rappel de pause") },
            text = { Text(alertePause.message + "\n\nTu peux quand même lancer la séance.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        montrerAlerte = false
                        onStart()
                    }
                ) {
                    Text("Lancer quand même")
                }
            },
            dismissButton = {
                TextButton(onClick = { montrerAlerte = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondApp)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRetour() }
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "‹",
                fontSize = 28.sp,
                color = texteApp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "Jour ${jour.numero}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = texteApp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Repos entre les séries : ${jour.reposSecondes} secondes",
            fontSize = 15.sp,
            color = texteGrisApp
        )
        Spacer(modifier = Modifier.height(6.dp))
        val textePause = if (jour.pauseJoursApres <= 1) {
            "Pause minimum avant le jour suivant : 1 jour"
        } else {
            "Pause minimum avant le jour suivant : ${jour.pauseJoursApres} jours"
        }
        Text(
            text = textePause,
            fontSize = 15.sp,
            color = texteGrisApp
        )

        if (alertePause != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = alertePause.message,
                fontSize = 14.sp,
                color = texteApp,
                fontWeight = FontWeight.Medium
            )
        }

        if (consigneForme.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Forme",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = texteApp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = consigneForme,
                fontSize = 14.sp,
                color = texteGrisApp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Séries",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = texteApp
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            jour.series.forEachIndexed { index, serie ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(secondaireApp, RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Série ${index + 1}",
                        fontSize = 16.sp,
                        color = texteApp
                    )
                    Text(
                        text = libelleSerieDetail(serie),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = texteApp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (alertePause != null) {
                    montrerAlerte = true
                } else {
                    onStart()
                }
            },
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentCuivre,
                contentColor = onAccentApp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 54.dp)
        ) {
            Text(
                text = "Start",
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

private fun libelleSerieDetail(serie: TypeSerie): String {
    return when (serie) {
        is SerieFixe -> "${serie.reps} répétitions"
        is SerieMax -> "max (min. ${serie.minimum})"
        is SerieTemps -> {
            if (serie.nom.isEmpty()) {
                "${serie.secondes} s"
            } else {
                "${serie.nom} · ${serie.secondes} s"
            }
        }
        is SerieTempsMax -> {
            if (serie.nom.isEmpty()) {
                "max (min. ${serie.minimumSecondes} s)"
            } else {
                "${serie.nom} · max (min. ${serie.minimumSecondes} s)"
            }
        }
    }
}
