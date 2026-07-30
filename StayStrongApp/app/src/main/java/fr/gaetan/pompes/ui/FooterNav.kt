package fr.gaetan.pompes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gaetan.pompes.ui.theme.AccentCuivre
import fr.gaetan.pompes.ui.theme.carteApp
import fr.gaetan.pompes.ui.theme.ligneApp
import fr.gaetan.pompes.ui.theme.texteApp
import fr.gaetan.pompes.ui.theme.texteGrisApp

enum class OngletFooter {
    ENTRAINEMENT,
    ARTICLES
}

@Composable
fun FooterNav(
    ongletActif: OngletFooter,
    onChoisir: (OngletFooter) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(color = ligneApp, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(carteApp)
                .padding(top = 8.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ItemFooter(
                emoji = "🏋️",
                libelle = "Entraînement",
                actif = ongletActif == OngletFooter.ENTRAINEMENT,
                onClick = { onChoisir(OngletFooter.ENTRAINEMENT) },
                modifier = Modifier.weight(1f)
            )
            ItemFooter(
                emoji = "📚",
                libelle = "Articles",
                actif = ongletActif == OngletFooter.ARTICLES,
                onClick = { onChoisir(OngletFooter.ARTICLES) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ItemFooter(
    emoji: String,
    libelle: String,
    actif: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 22.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = libelle,
            fontSize = 12.sp,
            fontWeight = if (actif) FontWeight.SemiBold else FontWeight.Normal,
            color = if (actif) AccentCuivre else texteGrisApp
        )
        Spacer(modifier = Modifier.height(4.dp))
        // petit trait sous l'onglet actif
        Spacer(
            modifier = Modifier
                .height(3.dp)
                .fillMaxWidth(0.35f)
                .background(if (actif) AccentCuivre else carteApp)
        )
    }
}
