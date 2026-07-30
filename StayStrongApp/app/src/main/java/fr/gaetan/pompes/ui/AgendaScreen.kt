package fr.gaetan.pompes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gaetan.pompes.ProgrammeData
import fr.gaetan.pompes.SessionAgenda
import fr.gaetan.pompes.calculerStats
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import fr.gaetan.pompes.ui.theme.AccentCuivre
import fr.gaetan.pompes.ui.theme.fondApp
import fr.gaetan.pompes.ui.theme.texteApp
import fr.gaetan.pompes.ui.theme.texteGrisApp
import fr.gaetan.pompes.ui.theme.carteApp
import fr.gaetan.pompes.ui.theme.secondaireApp
import fr.gaetan.pompes.ui.theme.onAccentApp

@Composable
fun AgendaScreen(
    sessions: List<SessionAgenda>,
    onRetour: () -> Unit
) {
    val aujourdhui = LocalDate.now()
    var moisCourant by remember { mutableStateOf(YearMonth.from(aujourdhui)) }
    var jourSelectionne by remember {
        mutableStateOf(aujourdhui.toString())
    }

    val datesEntrainement = remember(sessions) {
        sessions.map { it.date }.toSet()
    }

    val sessionsDuJour = remember(sessions, jourSelectionne) {
        sessions.filter { it.date == jourSelectionne }
    }

    val sessionsDuMois = remember(sessions, moisCourant) {
        val prefix = moisCourant.toString() // yyyy-MM
        sessions.filter { it.date.startsWith(prefix) }
    }

    val stats = remember(sessions) {
        calculerStats(sessions, aujourdhui)
    }

    val formatterMois = remember {
        DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondApp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // en-tête
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
                text = "Agenda",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = texteApp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tes jours d'entraînement",
            fontSize = 14.sp,
            color = texteGrisApp
        )
        Spacer(modifier = Modifier.height(16.dp))

        // stats enrichies
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = carteApp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Ce mois : ${stats.seancesCeMois} séance(s)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = texteApp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Série en cours : ${stats.streakJours} jour(s)",
                    fontSize = 15.sp,
                    color = texteApp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Dernier niveau par exercice",
                    fontSize = 13.sp,
                    color = texteGrisApp
                )
                Spacer(modifier = Modifier.height(6.dp))
                ProgrammeData.exercices.forEach { exo ->
                    val niv = stats.dernierNiveauParExo[exo.id] ?: 0
                    val texteNiv = if (niv <= 0) "—" else "Niv. $niv"
                    Text(
                        text = "${exo.nom} : $texteNiv",
                        fontSize = 14.sp,
                        color = texteApp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // navigation mois
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "‹",
                fontSize = 28.sp,
                color = texteApp,
                modifier = Modifier
                    .clickable {
                        moisCourant = moisCourant.minusMonths(1)
                    }
                    .padding(8.dp)
            )
            Text(
                text = moisCourant.atDay(1).format(formatterMois)
                    .replaceFirstChar { it.uppercase() },
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = texteApp
            )
            Text(
                text = "›",
                fontSize = 28.sp,
                color = texteApp,
                modifier = Modifier
                    .clickable {
                        moisCourant = moisCourant.plusMonths(1)
                    }
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // jours de la semaine
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("L", "M", "M", "J", "V", "S", "D").forEach { lettre ->
                Text(
                    text = lettre,
                    fontSize = 13.sp,
                    color = texteGrisApp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // grille du mois
        GrilleMois(
            mois = moisCourant,
            aujourdhui = aujourdhui,
            datesEntrainement = datesEntrainement,
            jourSelectionne = jourSelectionne,
            onChoisirJour = { date ->
                jourSelectionne = date
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // résumé mois
        Text(
            text = "${sessionsDuMois.size} entraînement(s) ce mois",
            fontSize = 14.sp,
            color = texteGrisApp
        )
        Spacer(modifier = Modifier.height(16.dp))

        // détail du jour sélectionné
        val dateAffichee = try {
            LocalDate.parse(jourSelectionne)
                .format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH))
        } catch (e: Exception) {
            jourSelectionne
        }
        Text(
            text = dateAffichee,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = texteApp
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (sessionsDuJour.isEmpty()) {
            Text(
                text = "Aucun entraînement ce jour-là.",
                fontSize = 14.sp,
                color = texteGrisApp
            )
        } else {
            sessionsDuJour.forEach { session ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = carteApp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = session.exerciceNom,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = texteApp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Niveau ${session.niveau} · Jour ${session.jourProgramme}",
                            fontSize = 14.sp,
                            color = texteGrisApp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // légende
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(AccentCuivre)
                    .padding(6.dp)
            )
            Spacer(modifier = Modifier.padding(horizontal = 6.dp))
            Text(
                text = "Jour d'entraînement",
                fontSize = 13.sp,
                color = texteGrisApp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun GrilleMois(
    mois: YearMonth,
    aujourdhui: LocalDate,
    datesEntrainement: Set<String>,
    jourSelectionne: String,
    onChoisirJour: (String) -> Unit
) {
    val premierJour = mois.atDay(1)
    // lundi = 1 → décalage 0
    val decalage = premierJour.dayOfWeek.value - 1
    val nbJours = mois.lengthOfMonth()
    // cases vides + jours
    val totalCases = decalage + nbJours
    val lignes = (totalCases + 6) / 7

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for (ligne in 0 until lignes) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (col in 0 until 7) {
                    val index = ligne * 7 + col
                    val numeroJour = index - decalage + 1
                    if (numeroJour < 1 || numeroJour > nbJours) {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val date = mois.atDay(numeroJour)
                        val dateTexte = date.toString()
                        val estEntrainement = datesEntrainement.contains(dateTexte)
                        val estAujourdhui = date == aujourdhui
                        val estSelectionne = dateTexte == jourSelectionne

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        estEntrainement -> AccentCuivre
                                        estSelectionne -> secondaireApp
                                        else -> fondApp
                                    }
                                )
                                .then(
                                    if (estAujourdhui && !estEntrainement) {
                                        Modifier.border(1.dp, texteApp, CircleShape)
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable { onChoisirJour(dateTexte) }
                        ) {
                            Text(
                                text = "$numeroJour",
                                fontSize = 14.sp,
                                fontWeight = if (estEntrainement || estAujourdhui) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                },
                                color = if (estEntrainement) {
                                    onAccentApp
                                } else {
                                    texteApp
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
