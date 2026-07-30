package fr.gaetan.pompes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Palette A — crème + cuivre (clair)
val FondCrema = Color(0xFFF7F4F0)
val TexteNoir = Color(0xFF1A1A1A)
val TexteGris = Color(0xFF6B6B6B)
val AccentCuivre = Color(0xFFC45C26)
val BoutonSecondaire = Color(0xFFEDE8E2)
val BoutonDoux = BoutonSecondaire
val BoutonBlanc = Color(0xFFFFFFFF)
val LigneProgression = Color(0xFFD8D0C8)
val SuccesVert = Color(0xFF2F6B4F)
val AlerteCuivre = Color(0xFFA65D2E)

// Palette sombre (soir)
private val FondSombre = Color(0xFF1C1917)
private val TexteClair = Color(0xFFF5F0EA)
private val TexteGrisSombre = Color(0xFFA8A09A)
private val SurfaceSombre = Color(0xFF2A2522)
private val SecondaireSombre = Color(0xFF3A342F)
private val LigneSombre = Color(0xFF4A433C)

// couleurs actives selon le mode (clair / sombre)
data class AppColors(
    val fond: Color,
    val texte: Color,
    val texteGris: Color,
    val carte: Color,
    val secondaire: Color,
    val ligne: Color,
    val onAccent: Color
)

private val CouleursClair = AppColors(
    fond = FondCrema,
    texte = TexteNoir,
    texteGris = TexteGris,
    carte = BoutonBlanc,
    secondaire = BoutonSecondaire,
    ligne = LigneProgression,
    onAccent = BoutonBlanc
)

private val CouleursSombre = AppColors(
    fond = FondSombre,
    texte = TexteClair,
    texteGris = TexteGrisSombre,
    carte = SurfaceSombre,
    secondaire = SecondaireSombre,
    ligne = LigneSombre,
    onAccent = BoutonBlanc
)

val LocalAppColors = staticCompositionLocalOf { CouleursClair }

// raccourcis pour l'UI
val fondApp: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.fond

val texteApp: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.texte

val texteGrisApp: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.texteGris

val carteApp: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.carte

val secondaireApp: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.secondaire

val ligneApp: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.ligne

val onAccentApp: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.onAccent

@Composable
fun PompesTheme(
    modeSombre: Boolean = false,
    content: @Composable () -> Unit
) {
    val appColors = if (modeSombre) CouleursSombre else CouleursClair
    val scheme = if (modeSombre) {
        darkColorScheme(
            primary = AccentCuivre,
            onPrimary = BoutonBlanc,
            secondary = SecondaireSombre,
            onSecondary = TexteClair,
            background = FondSombre,
            onBackground = TexteClair,
            surface = SurfaceSombre,
            onSurface = TexteClair,
            tertiary = SuccesVert,
            error = AlerteCuivre
        )
    } else {
        lightColorScheme(
            primary = AccentCuivre,
            onPrimary = BoutonBlanc,
            secondary = BoutonSecondaire,
            onSecondary = TexteNoir,
            background = FondCrema,
            onBackground = TexteNoir,
            surface = BoutonBlanc,
            onSurface = TexteNoir,
            tertiary = SuccesVert,
            error = AlerteCuivre
        )
    }
    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = scheme,
            content = content
        )
    }
}
