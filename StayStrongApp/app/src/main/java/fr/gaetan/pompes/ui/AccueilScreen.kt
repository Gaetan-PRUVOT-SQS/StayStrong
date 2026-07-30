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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gaetan.pompes.NiveauProgramme
import fr.gaetan.pompes.ui.theme.fondApp
import fr.gaetan.pompes.ui.theme.texteApp
import fr.gaetan.pompes.ui.theme.texteGrisApp
import fr.gaetan.pompes.ui.theme.carteApp
import fr.gaetan.pompes.ui.theme.secondaireApp

@Composable
fun AccueilScreen(
    niveau: NiveauProgramme,
    jourSuggere: Int,
    joursFaits: Set<Int>,
    onRetour: () -> Unit,
    onVoirJour: (Int) -> Unit
) {
    val nbFaits = joursFaits.size
    val totalJours = niveau.jours.size

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
            Text(
                text = "‹",
                fontSize = 28.sp,
                color = texteApp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "Niveau ${niveau.numero}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = texteApp
            )
        }

        Text(
            text = "Départ : ${niveau.libelleDepart}",
            fontSize = 14.sp,
            color = texteGrisApp
        )
        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = carteApp,
            shadowElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onVoirJour(jourSuggere) }
        ) {
            Text(
                text = "Jour suggéré : Jour $jourSuggere",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = texteApp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "$nbFaits / $totalJours jours complétés",
            fontSize = 14.sp,
            color = texteGrisApp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "Tous les jours",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = texteApp
        )
        Spacer(modifier = Modifier.height(12.dp))

        niveau.jours.forEach { jour ->
            val numero = jour.numero
            val fait = joursFaits.contains(numero)
            val titre = if (fait) {
                "Jour $numero  ·  terminé"
            } else {
                "Jour $numero"
            }
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = if (numero == jourSuggere) carteApp else secondaireApp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .then(
                        if (numero == jourSuggere) {
                            Modifier.shadow(2.dp, RoundedCornerShape(18.dp))
                        } else {
                            Modifier
                        }
                    )
                    .clickable { onVoirJour(numero) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = titre,
                        fontSize = 16.sp,
                        color = texteApp
                    )
                    Text(
                        text = if (fait) "✓" else "›",
                        fontSize = 18.sp,
                        color = texteGrisApp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
