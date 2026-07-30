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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gaetan.pompes.ExerciceProgramme
import fr.gaetan.pompes.NiveauProgramme
import fr.gaetan.pompes.TestsPassage
import fr.gaetan.pompes.ui.theme.AccentCuivre
import fr.gaetan.pompes.ui.theme.fondApp
import fr.gaetan.pompes.ui.theme.texteApp
import fr.gaetan.pompes.ui.theme.texteGrisApp
import fr.gaetan.pompes.ui.theme.carteApp
import fr.gaetan.pompes.ui.theme.secondaireApp
import fr.gaetan.pompes.ui.theme.onAccentApp

@Composable
fun NiveauxScreen(
    exercice: ExerciceProgramme,
    niveauDebloque: Int,
    onRetour: () -> Unit,
    onChoisirNiveau: (Int) -> Unit,
    onTestPassage: (depuisNiveau: Int) -> Unit,
    joursFaitsParNiveau: (Int) -> Set<Int>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondApp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRetour() }
                .padding(vertical = 8.dp)
        ) {
            Text("‹", fontSize = 28.sp, color = texteApp, modifier = Modifier.padding(end = 8.dp))
            Text(
                text = exercice.nom,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = texteApp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Niveaux débloqués jusqu'au $niveauDebloque",
            fontSize = 14.sp,
            color = texteGrisApp
        )
        Spacer(modifier = Modifier.height(20.dp))

        exercice.niveaux.forEach { niveau ->
            val debloque = niveau.numero <= niveauDebloque
            CarteNiveau(
                niveau = niveau,
                joursFaits = joursFaitsParNiveau(niveau.numero),
                debloque = debloque,
                onClick = {
                    if (debloque) {
                        onChoisirNiveau(niveau.numero)
                    }
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // bouton test de passage si un test existe pour le niveau max actuel
        val test = TestsPassage.getTest(exercice.id, niveauDebloque)
        if (test != null && niveauDebloque < 5) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onTestPassage(niveauDebloque) },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentCuivre,
                    contentColor = onAccentApp
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Test pour débloquer le niveau ${test.versNiveau}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun CarteNiveau(
    niveau: NiveauProgramme,
    joursFaits: Set<Int>,
    debloque: Boolean,
    onClick: () -> Unit
) {
    val total = niveau.jours.size
    val faits = joursFaits.size

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (debloque) carteApp else secondaireApp,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (debloque) Modifier.shadow(1.dp, RoundedCornerShape(18.dp))
                else Modifier
            )
            .clickable(enabled = debloque) { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (debloque) {
                        "Niveau ${niveau.numero}"
                    } else {
                        "Niveau ${niveau.numero}  ·  verrouillé"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (debloque) texteApp else texteGrisApp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Départ : ${niveau.libelleDepart}",
                    fontSize = 14.sp,
                    color = texteGrisApp
                )
                if (debloque) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$faits / $total jours complétés",
                        fontSize = 13.sp,
                        color = texteGrisApp
                    )
                }
            }
            Text(
                text = if (debloque) "›" else "🔒",
                fontSize = 18.sp,
                color = texteGrisApp
            )
        }
    }
}
