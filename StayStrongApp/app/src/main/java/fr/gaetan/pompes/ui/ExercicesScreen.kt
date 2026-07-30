package fr.gaetan.pompes.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gaetan.pompes.ExerciceProgramme
import fr.gaetan.pompes.PositionExo
import fr.gaetan.pompes.PositionSeanceComplete
import fr.gaetan.pompes.ProgrammeData
import fr.gaetan.pompes.R
import fr.gaetan.pompes.ui.theme.AccentCuivre
import fr.gaetan.pompes.ui.theme.fondApp
import fr.gaetan.pompes.ui.theme.texteApp
import fr.gaetan.pompes.ui.theme.texteGrisApp
import fr.gaetan.pompes.ui.theme.carteApp
import fr.gaetan.pompes.ui.theme.onAccentApp

@Composable
fun ExercicesScreen(
    dernierePosition: PositionExo?,
    derniereSeanceComplete: PositionSeanceComplete?,
    modeSombre: Boolean,
    onChoisirExercice: (String) -> Unit,
    onReprendreExo: (PositionExo) -> Unit,
    onReprendreSeanceComplete: (PositionSeanceComplete) -> Unit,
    onOuvrirAgenda: () -> Unit,
    onSeanceComplete: () -> Unit,
    onExporter: () -> Unit,
    onImporter: () -> Unit,
    onToggleSombre: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondApp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Entraînement",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = texteApp
            )
            // mode sombre
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (modeSombre) "🌙" else "☀️",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(end = 6.dp)
                )
                Switch(
                    checked = modeSombre,
                    onCheckedChange = onToggleSombre,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = carteApp,
                        checkedTrackColor = AccentCuivre,
                        uncheckedThumbColor = carteApp,
                        uncheckedTrackColor = texteGrisApp
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ordre conseillé : Tractions → Pompes → Squats → Killy → Gainage.",
            fontSize = 14.sp,
            color = texteGrisApp
        )
        Spacer(modifier = Modifier.height(18.dp))

        // Reprendre dernier exercice
        if (dernierePosition != null) {
            val nom = try {
                ProgrammeData.getExercice(dernierePosition.exerciceId).nom
            } catch (e: Exception) {
                dernierePosition.exerciceId
            }
            Button(
                onClick = { onReprendreExo(dernierePosition) },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentCuivre,
                    contentColor = onAccentApp
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "▶  Reprendre · $nom N${dernierePosition.niveau} J${dernierePosition.jour}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Reprendre séance complète
        if (derniereSeanceComplete != null) {
            Button(
                onClick = { onReprendreSeanceComplete(derniereSeanceComplete) },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = carteApp,
                    contentColor = texteApp
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🔄  Séance complète · N${derniereSeanceComplete.niveau} J${derniereSeanceComplete.jour}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        Button(
            onClick = onSeanceComplete,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (dernierePosition == null) AccentCuivre else carteApp,
                contentColor = if (dernierePosition == null) onAccentApp else texteApp
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "💪  Faire la séance complète",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onOuvrirAgenda,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = carteApp,
                contentColor = texteApp
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "📅  Agenda",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onExporter,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = carteApp,
                    contentColor = texteApp
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "📤 Export",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Button(
                onClick = onImporter,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = carteApp,
                    contentColor = texteApp
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "📥 Import",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        ProgrammeData.exercices.forEach { exercice ->
            CarteExercice(
                exercice = exercice,
                imageRes = imagePourExercice(exercice.id),
                onClick = { onChoisirExercice(exercice.id) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

private fun imagePourExercice(exerciceId: String): Int {
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
private fun CarteExercice(
    exercice: ExerciceProgramme,
    imageRes: Int,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = carteApp,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = exercice.nom,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = exercice.nom,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = texteApp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${exercice.niveaux.size} niveaux",
                        fontSize = 14.sp,
                        color = texteGrisApp
                    )
                }
                Text(
                    text = "›",
                    fontSize = 22.sp,
                    color = texteGrisApp
                )
            }
        }
    }
}
