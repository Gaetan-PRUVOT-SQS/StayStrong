package fr.gaetan.pompes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gaetan.pompes.TestPassage
import fr.gaetan.pompes.ui.theme.AccentCuivre
import fr.gaetan.pompes.ui.theme.fondApp
import fr.gaetan.pompes.ui.theme.texteApp
import fr.gaetan.pompes.ui.theme.texteGrisApp
import fr.gaetan.pompes.ui.theme.onAccentApp

@Composable
fun TestPassageScreen(
    exerciceNom: String,
    test: TestPassage,
    onRetour: () -> Unit,
    onValide: () -> Unit
) {
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
            Text("‹", fontSize = 28.sp, color = texteApp, modifier = Modifier.padding(end = 8.dp))
            Text(
                text = "Test de passage",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = texteApp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = exerciceNom,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = texteApp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Niveau ${test.depuisNiveau} → Niveau ${test.versNiveau}",
            fontSize = 16.sp,
            color = texteGrisApp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Objectif",
            fontSize = 14.sp,
            color = texteGrisApp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = test.consigne,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = texteApp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Réalise le test en bonne forme. Si tu réussis, valide pour débloquer le niveau suivant.",
            fontSize = 14.sp,
            color = texteGrisApp
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onValide,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentCuivre,
                contentColor = onAccentApp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
        ) {
            Text(
                text = "J'ai réussi le test",
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onRetour,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = fondApp,
                contentColor = texteGrisApp
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pas encore, revenir")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
