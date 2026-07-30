# Stay Strong

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)

Application Android **hors ligne** pour un circuit au poids de corps :

**Tractions → Pompes → Squats → Killy → Gainage**

Programmes par niveaux, chrono de repos, séance complète, agenda, articles, export/import.

<p align="center">
  <img src="assets/gifs/demo-parcours.gif" alt="Parcours de l'application Stay Strong" width="280"/>
</p>

<p align="center">
  <em>Aperçu du parcours : démarrage → entraînement → séance → agenda → articles</em>
</p>

---

## Captures d’écran

| Accueil | Entraînement | Articles |
|:---:|:---:|:---:|
| <img src="assets/screenshots/01-demarrage.png" width="200" alt="Écran de démarrage"/> | <img src="assets/screenshots/02-entrainement.png" width="200" alt="Écran entraînement"/> | <img src="assets/screenshots/articles-liste.png" width="200" alt="Écran Articles"/> |

| Niveaux | Jours | Détail du jour |
|:---:|:---:|:---:|
| <img src="assets/screenshots/05-niveaux.png" width="200" alt="Choix du niveau"/> | <img src="assets/screenshots/06-jours.png" width="200" alt="Liste des jours"/> | <img src="assets/screenshots/07-detail-jour.png" width="200" alt="Détail du jour"/> |

| Séance | Agenda | Séance complète |
|:---:|:---:|:---:|
| <img src="assets/screenshots/08-seance.png" width="200" alt="Séance en cours"/> | <img src="assets/screenshots/09-agenda.png" width="200" alt="Agenda"/> | <img src="assets/screenshots/10-seance-complete.png" width="200" alt="Séance complète"/> |

| Lecture d’article |
|:---:|
| <img src="assets/screenshots/articles-detail.png" width="220" alt="Détail d'un article"/> |

---

## Démos animées (GIF)

### Navigation footer Entraînement / Articles

<p align="center">
  <img src="assets/gifs/nav-footer-articles.gif" alt="Bascule footer Entraînement et Articles" width="260"/>
</p>

### Lancer une séance

<p align="center">
  <img src="assets/gifs/demo-seance.gif" alt="Démarrage d'une séance" width="260"/>
</p>

---

## Contenu du dépôt

| Dossier | Description |
|--------|-------------|
| `StayStrongApp/` | Projet Android (Kotlin + Jetpack Compose) |
| `assets/screenshots/` | Captures d’écran pour ce README |
| `assets/gifs/` | Animations GIF |
| `programmes/` | PDF sources des niveaux (5 exercices) |
| `articles/` | Guides PDF intégrés dans l’app |
| `design/` | Maquettes UI de référence |

---

## Fonctionnalités

### Navigation
- **Footer** : bascule **Entraînement** / **Articles**
- Mode **sombre** optionnel (soir)

### Entraînement
- 5 exercices, 5 niveaux chacun
- Jour suggéré, détail des séries, consigne de forme
- **Séance** : séries, maintien chrono (Killy / Gainage), repos 60 s
- **Pause / Reprendre**, **Passer le chrono**
- **Séance complète** : les 5 exercices le même jour
- **Reprendre** : dernier niveau/jour + dernière séance complète
- Tests de passage pour débloquer le niveau suivant
- **Agenda** + stats simples
- **Export / import** JSON

### Articles
1. *Ce que chaque exercice apporte*
2. *Pourquoi faire le circuit complet*

### Technique
- 100 % hors ligne
- SharedPreferences pour la progression
- Respect notch / barre de navigation
- Vibration en fin de chrono
- Écran allumé pendant la séance

---

## Prérequis

- macOS, Linux ou Windows
- **JDK 17** (ou le JBR d’Android Studio)
- **Android Studio** ou SDK Android
- Téléphone (débogage USB) ou émulateur
- Min SDK **26** (Android 8.0)

---

## Ouvrir et lancer

### Android Studio
1. **File → Open**
2. Choisir le dossier **`StayStrongApp/`**
3. Laisser Gradle synchroniser
4. Run sur appareil / émulateur

### Ligne de commande

```bash
cd StayStrongApp

# tests unitaires
./gradlew :app:testDebugUnitTest

# APK debug
./gradlew :app:assembleDebug

# installer sur le téléphone
./gradlew :app:installDebug

# ouvrir l’app
adb shell am start -n fr.gaetan.pompes/.MainActivity
```

Sous Windows : `gradlew.bat` à la place de `./gradlew`.

`local.properties` (chemin du SDK) est généré par Android Studio et **n’est pas versionné**.

APK debug typique :

```
StayStrongApp/app/build/outputs/apk/debug/app-debug.apk
```

---

## Identifiants

| Champ | Valeur |
|-------|--------|
| Package | `fr.gaetan.pompes` |
| Nom affiché | Stay Strong |
| Langage | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Min / target SDK | 26 / 35 |
| Activity | `MainActivity` |

---

## Structure du code (`StayStrongApp/`)

```
StayStrongApp/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/fr/gaetan/pompes/
│       │   │   ├── MainActivity.kt
│       │   │   ├── ProgrammeData.kt
│       │   │   ├── ProgressionStore.kt
│       │   │   ├── SeanceViewModel.kt
│       │   │   ├── ArticlesData.kt
│       │   │   ├── TestsPassage.kt
│       │   │   └── ui/          # écrans Compose + FooterNav + thème
│       │   └── res/
│       ├── test/                # tests unitaires
│       └── androidTest/         # tests UI
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew / gradlew.bat
```

### Rôles principaux
- **`ProgrammeData`** : niveaux, jours, séries, repos
- **`ProgressionStore`** : jours faits, agenda, export/import, Reprendre, mode sombre
- **`SeanceController` / `SeanceViewModel`** : logique de séance + chrono
- **`ArticlesData`** : contenu des articles
- **`MainActivity`** : navigation, footer, vibration
- **Écrans `ui/`** : affichage Compose

---

## Tests

```bash
cd StayStrongApp
./gradlew :app:testDebugUnitTest
```

---

## Utilisation

1. **Commencer** → footer **Entraînement**
2. Choisir un exercice (ou **Reprendre** / **séance complète**)
3. Niveau → jour → **Start**
4. Séries, repos (ou **Passer le chrono**), fin de jour
5. Consulter l’**Agenda** ou les **Articles** via le footer

---

## Limites (v1)

- Pas de saisie du nombre de reps sur les séries « max »
- Pas de son (vibration + texte)
- Pas de notification en arrière-plan
- Pas de cloud (export/import JSON local)
- Progression locale à l’appareil

---

## Modifier le programme

```
StayStrongApp/app/src/main/java/fr/gaetan/pompes/ProgrammeData.kt
```

```bash
cd StayStrongApp
./gradlew :app:testDebugUnitTest :app:installDebug
```

---

## Licence

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)

Copyright 2026 Gaetan Pruvot.

Distribué sous la licence **Apache License 2.0**. Voir le fichier [`LICENSE`](LICENSE).

Projet personnel d'entraînement : le code et les ressources du dépôt sont réutilisables selon les termes Apache 2.0.