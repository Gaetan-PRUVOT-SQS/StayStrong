package fr.gaetan.pompes.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
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
import fr.gaetan.pompes.Article
import fr.gaetan.pompes.ArticlesData
import fr.gaetan.pompes.BlocArticle
import fr.gaetan.pompes.R
import fr.gaetan.pompes.ui.theme.AccentCuivre
import fr.gaetan.pompes.ui.theme.carteApp
import fr.gaetan.pompes.ui.theme.fondApp
import fr.gaetan.pompes.ui.theme.secondaireApp
import fr.gaetan.pompes.ui.theme.texteApp
import fr.gaetan.pompes.ui.theme.texteGrisApp

@Composable
fun ArticlesScreen(
    onOuvrirArticle: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondApp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Articles",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = texteApp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Lire pour mieux s'entraîner — contenu illustré des guides Stay Strong.",
            fontSize = 14.sp,
            color = texteGrisApp
        )
        Spacer(modifier = Modifier.height(20.dp))

        ArticlesData.articles.forEach { article ->
            CarteArticleListe(
                article = article,
                onClick = { onOuvrirArticle(article.id) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CarteArticleListe(
    article: Article,
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
            // bandeau illustré avec les 5 exo ou icône
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(secondaireApp)
            ) {
                if (article.id == "exercices") {
                    // mosaïque des 5 images
                    Row(modifier = Modifier.fillMaxSize()) {
                        listOf(
                            R.drawable.illu_tractions,
                            R.drawable.illu_pompes,
                            R.drawable.illu_squats,
                            R.drawable.illu_killy,
                            R.drawable.illu_gainage
                        ).forEach { res ->
                            Image(
                                painter = painterResource(id = res),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                            )
                        }
                    }
                } else {
                    // circuit : image hero + emoji
                    Image(
                        painter = painterResource(id = R.drawable.hero_fitness),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AccentCuivre.copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = article.emoji, fontSize = 42.sp)
                    }
                }
            }
            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                Text(
                    text = article.emoji + "  " + article.titre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = texteApp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = article.sousTitre,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = AccentCuivre
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = article.resume,
                    fontSize = 14.sp,
                    color = texteGrisApp,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Lire l'article  ›",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AccentCuivre
                )
            }
        }
    }
}

@Composable
fun ArticleDetailScreen(
    articleId: String,
    onRetour: () -> Unit
) {
    val article = ArticlesData.getArticle(articleId)

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
                text = "Articles",
                fontSize = 16.sp,
                color = texteGrisApp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = article.emoji + "  " + article.titre,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = texteApp,
            lineHeight = 30.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = article.sousTitre,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = AccentCuivre
        )
        Spacer(modifier = Modifier.height(20.dp))

        article.blocs.forEach { bloc ->
            BlocArticleVue(bloc = bloc)
            Spacer(modifier = Modifier.height(18.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun BlocArticleVue(bloc: BlocArticle) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = carteApp,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // image d'illustration si l'exo est lié
            val imageRes = imagePourExercice(bloc.exerciceId)
            if (imageRes != null) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = bloc.titre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
            }
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (bloc.emoji.isNotEmpty()) {
                        Text(text = bloc.emoji, fontSize = 20.sp)
                    }
                    Text(
                        text = bloc.titre,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = texteApp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = bloc.texte,
                    fontSize = 15.sp,
                    color = texteApp,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

private fun imagePourExercice(exerciceId: String?): Int? {
    if (exerciceId == null) return null
    return when (exerciceId) {
        "pompes" -> R.drawable.illu_pompes
        "squats" -> R.drawable.illu_squats
        "tractions" -> R.drawable.illu_tractions
        "gainage" -> R.drawable.illu_gainage
        "killy" -> R.drawable.illu_killy
        else -> null
    }
}
