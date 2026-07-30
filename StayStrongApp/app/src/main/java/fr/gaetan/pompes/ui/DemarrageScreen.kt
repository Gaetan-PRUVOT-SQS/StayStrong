package fr.gaetan.pompes.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.gaetan.pompes.R
import fr.gaetan.pompes.ui.theme.AccentCuivre
import fr.gaetan.pompes.ui.theme.fondApp
import fr.gaetan.pompes.ui.theme.texteApp
import fr.gaetan.pompes.ui.theme.texteGrisApp
import fr.gaetan.pompes.ui.theme.onAccentApp

@Composable
fun DemarrageScreen(onCommencer: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondApp)
    ) {
        // photo en haut (design)
        Image(
            painter = painterResource(id = R.drawable.hero_fitness),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.1f)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f)
                .padding(horizontal = 28.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Stay Strong",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = texteApp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Tractions, Pompes, Squats, Killy, Gainage.\n60 s de repos · séance ~35-45 min.",
                fontSize = 15.sp,
                color = texteGrisApp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onCommencer,
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
                    text = "Commencer",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
