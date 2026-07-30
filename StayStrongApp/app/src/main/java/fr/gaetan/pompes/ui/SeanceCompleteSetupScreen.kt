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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gaetan.pompes.ProgrammeData
import fr.gaetan.pompes.ui.theme.AccentCuivre
import fr.gaetan.pompes.ui.theme.fondApp
import fr.gaetan.pompes.ui.theme.texteApp
import fr.gaetan.pompes.ui.theme.texteGrisApp
import fr.gaetan.pompes.ui.theme.onAccentApp

@Composable
fun SeanceCompleteSetupScreen(
    niveauInitial: Int = 1,
    jourInitial: Int = 1,
    onRetour: () -> Unit,
    onLancer: (niveau: Int, jour: Int) -> Unit
) {
    var niveau by remember(niveauInitial) { mutableStateOf(niveauInitial) }
    var jour by remember(jourInitial, niveauInitial) { mutableStateOf(jourInitial) }

    // jours communs à tous les exercices pour ce niveau
    val joursPossibles = remember(niveau) {
        joursCommuns(niveau)
    }
    LaunchedEffect(niveau, joursPossibles) {
        if (joursPossibles.isNotEmpty() && !joursPossibles.contains(jour)) {
            jour = joursPossibles.first()
        }
    }

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
                text = "Séance complète",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = texteApp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enchaîne les 5 exercices le même jour, avec 60 s de repos (~35-45 min).",
            fontSize = 14.sp,
            color = texteGrisApp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Ordre : Tractions → Pompes → Squats → Killy → Gainage",
            fontSize = 14.sp,
            color = texteGrisApp
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Niveau",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = texteApp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            for (n in 1..5) {
                FilterChip(
                    selected = niveau == n,
                    onClick = { niveau = n },
                    label = { Text("N$n") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Jour",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = texteApp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            joursPossibles.forEach { j ->
                FilterChip(
                    selected = jour == j,
                    onClick = { jour = j },
                    label = { Text("J$j") }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { onLancer(niveau, jour) },
            enabled = joursPossibles.isNotEmpty(),
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
                text = "Démarrer la séance",
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// jours présents dans tous les exercices pour un niveau donné
fun joursCommuns(niveau: Int): List<Int> {
    val listes = ProgrammeData.exercices.map { exo ->
        try {
            ProgrammeData.getNiveau(exo.id, niveau).jours.map { it.numero }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }
    if (listes.isEmpty()) {
        return emptyList()
    }
    var commun = listes.first()
    listes.drop(1).forEach { commun = commun.intersect(it) }
    return commun.sorted()
}
