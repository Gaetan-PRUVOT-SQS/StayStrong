# Protocole de test QA — Stay Strong

| Champ | Valeur |
|-------|--------|
| **Projet** | Stay Strong (`fr.gaetan.pompes`) |
| **Version sous test** | 1.0 (debug) + évolutions récentes (reps max, historique, articles) |
| **Norme** | ISTQB Foundation — processus de test en 6 phases |
| **Niveau principal** | Tests système + acceptation (UAT) |
| **Auteur / exécutant** | Gaetan Pruvot |
| **Date** | 2026-07-30 |
| **Environnement cible** | Pixel 8 Pro (Android 17) — min SDK 26 |
| **Build** | `./gradlew :app:installDebug` (JDK 17) |

---

## 1. Objectifs et périmètre

### 1.1 Objectifs

1. Vérifier que les parcours d’entraînement fonctionnent de bout en bout hors ligne.
2. Confirmer la persistance de la progression (jours faits, agenda, export/import, reps max).
3. Valider les évolutions récentes (accueil, historique, séance, articles, sauvegarde).
4. Estimer le **risque résiduel** avant usage personnel / distribution.

### 1.2 Périmètre IN

| Domaine | Contenu |
|---------|---------|
| Navigation | Footer Entraînement / Articles, mode sombre, démarrage |
| Programmes | 5 exercices × 5 niveaux, jours, tests de passage |
| Séance | séries fixes, max, maintien, repos, pause, passer chrono, couleurs boutons |
| Séance complète | enchaînement des 5 exercices |
| Progression | marquer jour, Continuer, Reprendre, stats, streak |
| Agenda | calendrier, graphe 7 j, totaux, détail reps max |
| Export / import | JSON, rappel 14 j, toast sauvegarde |
| Articles | 4 articles (contenu + navigation) |
| UI système | notch / safe area, écran allumé en séance, vibration fin chrono |

### 1.3 Périmètre OUT

- Tests de performance / charge (usage mono-utilisateur)
- Sécurité réseau (app 100 % offline)
- Publication Play Store / signature release
- Accessibilité WCAG exhaustive
- Tests multi-appareils (1 device de référence pour ce cycle)
- Son (non implémenté)

### 1.4 Hypothèses

- App installée en debug, données initiales **vides** (ou export/import de jeu de données de test).
- L’exécutant connaît le circuit Tractions → Pompes → Squats → Killy → Gainage.
- Les tests unitaires Gradle (`:app:testDebugUnitTest`) ont déjà passé avant le cycle manuel.

---

## 2. Analyse de risques (likelihood × impact)

Échelle 1–5 pour **L** (vraisemblance) et **I** (impact). **Score = L × I**. Priorité de test proportionnelle au score.

| ID | Risque | L | I | Score | Zone impactée |
|----|--------|---|---|-------|----------------|
| R01 | Perte de progression (SharedPreferences / export) | 3 | 5 | **15** | ProgressionStore, export/import |
| R02 | Séance max sans saisie / mauvaise persistance des reps | 3 | 4 | **12** | SeanceController, SeanceScreen, agenda |
| R03 | Chrono repos / maintien faux (tick, pause, passer) | 3 | 4 | **12** | SeanceViewModel, UI séance |
| R04 | Séance complète : mauvais enchaînement / jour non marqué | 2 | 5 | **10** | MainActivity, modeComplet |
| R05 | Continuer / Reprendre pointe vers mauvais niveau-jour | 3 | 3 | **9** | ProgressionStore, ExercicesScreen |
| R06 | Stats / streak / graphe incorrects | 2 | 3 | **6** | HistoriqueStats, AgendaScreen |
| R07 | Test de passage débloque mal les niveaux | 2 | 4 | **8** | TestsPassage, NiveauxScreen |
| R08 | Import JSON corrompt ou ignore les données | 2 | 5 | **10** | parserExportJson |
| R09 | UI illisible (couleurs, mode sombre, consigne) | 2 | 2 | **4** | Theme, SeanceScreen |
| R10 | Crash au lancement / rotation / retour arrière | 2 | 4 | **8** | MainActivity, navigation manuelle |
| R11 | Articles manquants ou non ouvrables | 1 | 2 | **2** | ArticlesData |

**Ordre d’exécution manuel** : R01 → R02 → R03 → R04 → R08 → R05 → R07 → R10 → R06 → R09 → R11.

---

## 3. Approche de test

### 3.1 Niveaux

| Niveau | Moyen | Objectif |
|--------|-------|----------|
| **Composant** | `./gradlew :app:testDebugUnitTest` | Logique pure (séance, stats, progression mémoire, articles) |
| **Système** | Exécution manuelle sur device (ce protocole) | Parcours UI + persistance réelle |
| **Acceptation** | Scénarios UAT (section 5.3) | App utilisable pour une vraie semaine d’entraînement |

### 3.2 Types

- **Fonctionnels** : majorité des cas
- **Non-fonctionnels** : offline, lisibilité, vibration, keep-screen-on (échantillon)
- **Liés au changement** : confirmation des correctifs + **régression smoke** (section 5.4)

### 3.3 Techniques de conception

| Technique | Usage dans ce protocole |
|-----------|-------------------------|
| **Partitions d’équivalence** | Saisie reps (vide / 0 / valide / très grand) |
| **Valeurs limites** | Reps min programme, jour 1 / dernier jour, niveau 1 / 5 |
| **Tables de décision** | Série max vs fixe ; pause pendant maintien vs repos |
| **Transitions d’état** | SERIE → REPOS → SERIE → FIN ; PAUSE |
| **Cas d’utilisation** | Parcours « première séance », « Continuer », « export/import » |
| **Error guessing** | Double tap fin de série, arrêt en milieu, import collé partiel |

### 3.4 Critère de couverture (cible de ce cycle)

- 100 % des risques score ≥ 8 couverts par ≥ 1 cas prioritaire P1
- 100 % des parcours critiques UAT (PC-01 à PC-05) exécutés
- Smoke de régression verte après tout correctif

---

## 4. Conditions de test et traçabilité

| ID condition | Condition de test | Risque | Exigence / feature |
|--------------|-------------------|--------|--------------------|
| C01 | L’app démarre hors ligne | R10 | Technique offline |
| C02 | Navigation footer Entraînement ↔ Articles | R10 | Footer |
| C03 | Mode sombre bascule et persiste | R09 | ProgressionStore modeSombre |
| C04 | Choix exercice → niveaux → jours → détail | R05 | ProgrammeData |
| C05 | Lancement séance, séries fixes | R03 | Séance |
| C06 | Série max : saisie reps obligatoire | R02 | Saisie max |
| C07 | Repos 60 s, couleurs vertes, bouton jaune « Série suivante » | R03 R09 | UI séance |
| C08 | Maintien Killy/Gainage + pause + passer chrono | R03 | Chrono |
| C09 | Fin de jour : toast « Progression sauvegardée » + agenda | R01 | Sauvegarde |
| C10 | Bouton Continuer reprend bon exos/niv/jour | R05 | Accueil action |
| C11 | Mini-stats (séances / 7 j, streak) cohérentes | R06 | Accueil |
| C12 | Agenda : graphe 7 j, totaux, détail max | R06 | Historique |
| C13 | Séance complète 5 exercices | R04 | Séance complète |
| C14 | Test de passage débloque niveau suivant | R07 | TestsPassage |
| C15 | Export JSON + copie presse-papiers | R01 R08 | Export |
| C16 | Import JSON restaure sessions + positions | R01 R08 | Import |
| C17 | Rappel export après séances sans export | R01 | Rappel 14 j |
| C18 | Image + consigne visibles en séance | R09 | Séance UI |
| C19 | 4 articles lisibles | R11 | Articles |
| C20 | Vibration en fin de chrono | R03 | Vibration |

---

## 5. Cas de test

### Légende

| Priorité | Signification |
|----------|----------------|
| **P1** | Bloquant release / usage |
| **P2** | Important, corriger avant large usage |
| **P3** | Mineur / cosmétique |

| Résultat | Cocher à l’exécution |
|----------|----------------------|
| OK | Passé |
| KO | Échoué → ouvrir fiche défaut |
| B | Bloqué (prérequis manquant) |
| NA | Non applicable |

**Préconditions globales** (sauf mention contraire) :

1. App installée (`installDebug`), package `fr.gaetan.pompes`.
2. Données réinitialisées : désinstaller l’app **ou** import d’un JSON vide de test.
3. Téléphone en mode avion (vérifier offline).

---

### 5.1 Smoke (ordre d’entrée — 10 min)

| ID | Titre | Prio | Condition | Préconditions | Étapes | Résultat attendu | OK/KO |
|----|-------|------|-----------|---------------|--------|------------------|-------|
| SM-01 | Lancement | P1 | C01 | App installée | 1. Ouvrir Stay Strong | Écran démarrage ou entraînement, pas de crash | ☐ |
| SM-02 | Footer | P1 | C02 | Sur Entraînement | 1. Taper Articles 2. Taper Entraînement | Bascules correctes, contenu adapté | ☐ |
| SM-03 | Liste exercices | P1 | C04 | Footer Entraînement | 1. Scroller la liste | 5 cartes : Tractions, Pompes, Squats, Killy, Gainage | ☐ |
| SM-04 | Accès agenda | P2 | C12 | — | 1. Agenda & historique | Calendrier + zone stats visibles | ☐ |
| SM-05 | Accès articles | P2 | C19 | — | 1. Articles | 4 titres listés | ☐ |

---

### 5.2 Cas fonctionnels détaillés

#### A. Navigation & réglages

| ID | Titre | Prio | Cond. | Étapes | Résultat attendu | OK/KO |
|----|-------|------|-------|--------|------------------|-------|
| TC-NAV-01 | Mode sombre | P2 | C03 | 1. Activer 🌙 2. Tuer l’app 3. Relancer | Mode sombre conservé | ☐ |
| TC-NAV-02 | Mode clair | P3 | C03 | 1. Désactiver le switch | Retour palette crème | ☐ |

#### B. Parcours séance standard (Pompes N1 J1)

| ID | Titre | Prio | Cond. | Étapes | Résultat attendu | OK/KO |
|----|-------|------|-------|--------|------------------|-------|
| TC-SE-01 | Accès détail jour | P1 | C04 | 1. Pompes → Niv.1 → Jour 1 | Séries listées, repos 60 s, consigne forme | ☐ |
| TC-SE-02 | Image + consigne en séance | P1 | C18 | 1. Start | Image pompes + phrase de forme en haut | ☐ |
| TC-SE-03 | Série fixe | P1 | C05 | 1. Faire série 1 2. « Série terminée » | Bouton cuivre → passage repos | ☐ |
| TC-SE-04 | Repos en cours | P1 | C07 | 1. Observer repos | Label REPOS, chrono vert, barre verte, bouton « Passer le repos » vert | ☐ |
| TC-SE-05 | Série suivante jaune | P1 | C07 | 1. Laisser finir ou passer repos | Bouton « → Série suivante » **jaune** | ☐ |
| TC-SE-06 | Série max — refus vide | P1 | C06 | 1. Atteindre dernière série max 2. Valider sans nombre | Reste en série, pas de repos/fin | ☐ |
| TC-SE-07 | Série max — saisie valide | P1 | C06 | 1. Saisir 12 2. Valider | Fin de jour (ou repos si pas dernière) | ☐ |
| TC-SE-08 | Toast sauvegarde | P1 | C09 | 1. Terminer le jour | Toast « Progression sauvegardée » | ☐ |
| TC-SE-09 | Agenda après séance | P1 | C09 C12 | 1. Ouvrir Agenda 2. Jour d’aujourd’hui | Session Pompes N1 J1 ; si max : « Max : S5: 12 » | ☐ |
| TC-SE-10 | Arrêter en cours | P2 | C05 | 1. Pendant série 2. Arrêter | Retour exercices, jour **non** marqué si pas fini | ☐ |

#### C. Chrono maintien (Killy)

| ID | Titre | Prio | Cond. | Étapes | Résultat attendu | OK/KO |
|----|-------|------|-------|--------|------------------|-------|
| TC-CH-01 | Décompte maintien | P1 | C08 | 1. Killy N1 J1 Start | Chrono cuivre, barre de progression | ☐ |
| TC-CH-02 | Pause maintien | P1 | C08 | 1. Pause 2. Attendre 3 s 3. Reprendre | Temps figé puis reprend | ☐ |
| TC-CH-03 | Passer chrono maintien | P1 | C08 | 1. Passer le chrono | « Maintien terminé », vibration éventuelle | ☐ |
| TC-CH-04 | Pause repos | P1 | C08 | 1. Finir une série 2. Pause pendant repos | Chrono figé | ☐ |
| TC-CH-05 | Passer repos | P1 | C07 | 1. Passer le repos | Temps 0, bouton jaune série suivante | ☐ |
| TC-CH-06 | Écran allumé | P2 | C08 | 1. Laisser 30 s en séance | Écran ne s’éteint pas (timeout système normal désactivé) | ☐ |

#### D. Accueil & progression

| ID | Titre | Prio | Cond. | Étapes | Résultat attendu | OK/KO |
|----|-------|------|-------|--------|------------------|-------|
| TC-AC-01 | Continuer | P1 | C10 | 1. Après TC-SE-08 2. Continuer | Ouvre Pompes N1 détail **jour suggéré** (souvent J2) ou dernier jour mémorisé | ☐ |
| TC-AC-02 | Mini-stats | P2 | C11 | 1. Après ≥1 séance | Compteurs séances/7j et streak ≥ 1 si session aujourd’hui | ☐ |
| TC-AC-03 | Jour suggéré | P2 | C04 | 1. Pompes N1 liste jours | Jour suivant non fait mis en avant / accessible | ☐ |
| TC-AC-04 | Jours faits | P2 | C04 | 1. Liste jours après J1 fait | J1 marqué fait | ☐ |

#### E. Séance complète

| ID | Titre | Prio | Cond. | Étapes | Résultat attendu | OK/KO |
|----|-------|------|-------|--------|------------------|-------|
| TC-SC-01 | Setup | P1 | C13 | 1. Séance complète 2. N1 J1 3. Lancer | Démarre Tractions, barre « Exercice 1/5 » | ☐ |
| TC-SC-02 | Enchaînement | P1 | C13 | 1. Terminer Tractions (max avec saisie) | Enchaîne Pompes sans retour menu ; 2/5 | ☐ |
| TC-SC-03 | Fin complète | P1 | C13 | 1. Finir les 5 | Message « Séance complète terminée » | ☐ |
| TC-SC-04 | Reprendre complète | P2 | C10 | 1. Depuis accueil 2. Séance complète mémorisée | Prefill N/J corrects | ☐ |

*Note : TC-SC-02/03 longs — acceptable en smoke partiel (1er enchaînement seulement) si temps limité ; cocher B + note.*

#### F. Tests de passage

| ID | Titre | Prio | Cond. | Étapes | Résultat attendu | OK/KO |
|----|-------|------|-------|--------|------------------|-------|
| TC-TP-01 | Accès test | P2 | C14 | 1. Exercice avec test 2. Bouton test passage | Écran consigne + validation | ☐ |
| TC-TP-02 | Validation | P1 | C14 | 1. Valider le test | Niveau suivant débloqué dans la liste | ☐ |

#### G. Agenda & historique

| ID | Titre | Prio | Cond. | Étapes | Résultat attendu | OK/KO |
|----|-------|------|-------|--------|------------------|-------|
| TC-AG-01 | Streak affiché | P2 | C12 | 1. ≥1 séance aujourd’hui | « X jour(s) d’affilée » ≥ 1 | ☐ |
| TC-AG-02 | Graphe 7 j | P2 | C12 | 1. Observer barres | 7 colonnes ; barre du jour courant > 0 si séance | ☐ |
| TC-AG-03 | Total par exo | P2 | C12 | 1. Lire totaux | Pompes ≥ 1 après TC-SE | ☐ |
| TC-AG-04 | Jour sans séance | P3 | C12 | 1. Sélectionner un jour vide | Message « Aucun entraînement… » | ☐ |
| TC-AG-05 | Navigation mois | P3 | C12 | 1. Mois précédent / suivant | Calendrier change | ☐ |

#### H. Export / import / rappel

| ID | Titre | Prio | Cond. | Étapes | Résultat attendu | OK/KO |
|----|-------|------|-------|--------|------------------|-------|
| TC-EX-01 | Export | P1 | C15 | 1. Export 2. Coller ailleurs | JSON avec `"app": "Stay Strong"`, sessions | ☐ |
| TC-EX-02 | Toast export | P2 | C15 | 1. Export | Toast presse-papiers | ☐ |
| TC-EX-03 | Rappel export | P2 | C17 | 1. Après séance sans export (ou reset date export) | Bannière « Pense à exporter… » | ☐ |
| TC-EX-04 | Import valide | P1 | C16 | 1. Désinstaller ou vider 2. Import JSON exporté | Sessions + positions restaurées | ☐ |
| TC-EX-05 | Import invalide | P2 | C16 | 1. Coller `{invalid` 2. Importer | Toast « JSON invalide », données non écrasées | ☐ |
| TC-EX-06 | Round-trip reps | P1 | C06 C16 | 1. Export session avec Max 2. Import | `detailReps` visible agenda | ☐ |

#### I. Articles

| ID | Titre | Prio | Cond. | Étapes | Résultat attendu | OK/KO |
|----|-------|------|-------|--------|------------------|-------|
| TC-AR-01 | Liste | P2 | C19 | 1. Onglet Articles | 4 cartes (exercices, circuit, échauffement, récupération) | ☐ |
| TC-AR-02 | Lecture | P2 | C19 | 1. Ouvrir chaque article 2. Scroller 3. Retour | Blocs lisibles, retour liste OK | ☐ |

#### J. Partitions / limites — saisie reps

| ID | Titre | Prio | Cond. | Technique | Entrée | Résultat attendu | OK/KO |
|----|-------|------|-------|-----------|--------|------------------|-------|
| TC-BV-01 | Vide | P1 | C06 | EP invalide | (vide) | Pas de validation | ☐ |
| TC-BV-02 | Zéro | P1 | C06 | EP / BVA | `0` | Pas de validation | ☐ |
| TC-BV-03 | Min programme | P1 | C06 | BVA | ex. `5` si min 5 | Accepté | ☐ |
| TC-BV-04 | Sous le min (volontaire) | P2 | C06 | EP | min-1 | **Accepté** (on enregistre l’échec technique) — documenter le comportement réel | ☐ |
| TC-BV-05 | Grand nombre | P3 | C06 | EP | `999` | Accepté, affiché agenda | ☐ |
| TC-BV-06 | Lettres | P2 | C06 | EP invalide | tenter `ab` | Champ refuse non-chiffres | ☐ |

---

### 5.3 Cas d’acceptation (UAT)

| ID | Scénario utilisateur | Critères d’acceptation | OK/KO |
|----|----------------------|------------------------|-------|
| PC-01 | **Première séance** : je découvre l’app et finis Pompes N1 J1 | Je comprends où cliquer, je termine le jour, je vois la sauvegarde et l’agenda | ☐ |
| PC-02 | **Continuer le lendemain** : je rouvre l’app | En ≤ 2 taps j’arrive sur mon exercice / jour via Continuer | ☐ |
| PC-03 | **Circuit du jour** : séance complète N1 J1 | Les 5 exos s’enchaînent ; message de fin clair | ☐ |
| PC-04 | **Changer de téléphone** (simuler) : export puis import | Progression et reps max récupérées | ☐ |
| PC-05 | **S’informer** : lire échauffement + récupération | Contenu utile avant/après séance | ☐ |

---

### 5.4 Régression smoke (après chaque correctif)

Exécuter dans l’ordre : **SM-01 → SM-02 → TC-SE-03 → TC-SE-07 → TC-SE-08 → TC-AC-01 → TC-EX-01 → TC-AR-01**.

Durée cible : **≤ 15 min**.

---

### 5.5 Automatisation existante (composant)

À lancer avant le cycle manuel :

```bash
cd StayStrongApp
export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"  # adapter
./gradlew :app:testDebugUnitTest
```

| Suite | Couvre |
|-------|--------|
| `SeanceViewModelTest` | états séance, max + reps, chrono |
| `ProgressionStoreTest` | jours, export, rappel export, detailReps |
| `HistoriqueStatsTest` | streak, totaux, semaine |
| `ArticlesDataTest` | 4 articles |
| `ProgrammeDataTest` | structure programmes |
| `TestsPassageTest` | tests de passage |

**Résultat run** : ☐ OK (date : _____)  ☐ KO

---

## 6. Données de test

| Jeu | Description | Usage |
|-----|-------------|-------|
| D0 | App fraîche (désinstallée) | Smoke, première séance |
| D1 | 1 session Pompes N1 J1 + max `S5: 12` | Continuer, agenda, export |
| D2 | 3 dates consécutives (hier-2, hier, aujourd’hui) | Streak = 3 |
| D3 | JSON export de D1 | Import round-trip |
| D4 | JSON tronqué `{` | Import invalide |

**Réinit rapide** : `adb uninstall fr.gaetan.pompes` puis `./gradlew :app:installDebug`.

---

## 7. Environnement

| Élément | Valeur de référence |
|---------|---------------------|
| Device | Pixel 8 Pro |
| OS | Android 17 |
| Build | debug, `versionName 1.0` |
| JDK build | 17 |
| Réseau | Mode avion ON pour tests offline |
| Outils | adb, Gradle, presse-papiers |

Checklist entrée d’exécution :

- [ ] Device USB connecté (`adb devices`)
- [ ] APK debug installé (commit / date notés)
- [ ] Unit tests verts
- [ ] Jeu de données choisi (D0 / D1…)
- [ ] Protocole imprimé ou ouvert à côté

---

## 8. Critères d’entrée / de sortie

### Entrée (début d’exécution manuelle)

1. Build installable sans erreur de compile.
2. Tests unitaires verts.
3. Device disponible, protocole versionné.
4. Périmètre et risques lus par l’exécutant.

### Sortie (fin de cycle)

1. Tous les cas **P1** exécutés (pas de B non justifié).
2. Aucun défaut **sévérité S1/S2 ouvert** non accepté explicitement.
3. UAT PC-01, PC-02, PC-04 passés (PC-03 si temps).
4. Régression smoke verte.
5. Risque résiduel documenté (section 10).

---

## 9. Gestion des défauts

### 9.1 Sévérité (impact technique)

| Sévérité | Définition |
|----------|------------|
| **S1** Bloquant | Crash, perte de données, séance inutilisable |
| **S2** Majeur | Fonction principale incorrecte (mauvais jour, import KO, max non enregistré) |
| **S3** Moyen | Fonction secondaire / contournable |
| **S4** Mineur | Cosmétique, texte, alignement |

### 9.2 Priorité (urgence correction)

| Priorité | Définition |
|----------|------------|
| **P1** | Corriger avant tout usage |
| **P2** | Corriger avant distribution / commit « stable » |
| **P3** | Backlog |

### 9.3 Modèle de fiche défaut

```
ID          : DEF-XXX
Titre       :
Sévérité    : Sx
Priorité    : Py
Environnement : Pixel 8 Pro / Android 17 / build …
Préconditions :
Étapes      :
1.
2.
Résultat attendu :
Résultat obtenu :
Preuves     : (capture / logcat)
Risque lié  : R0x
Statut      : Ouvert | Corrigé | Retest OK | Reporté | Rejeté
```

### 9.4 Journal d’exécution (à remplir)

| Date | Build | Exécutant | Cas joués | OK | KO | B | Défauts ouverts |
|------|-------|-----------|-----------|----|----|---|-----------------|
| | | | | | | | |

---

## 10. Évaluation & recommandation de release

*À compléter en fin de cycle.*

| Indicateur | Cible | Mesure |
|------------|-------|--------|
| P1 passés | 100 % | __ / __ |
| P2 passés | ≥ 90 % | __ / __ |
| Défauts S1/S2 ouverts | 0 | __ |
| Unit tests | Vert | ☐ |
| UAT critiques | PC-01 PC-02 PC-04 OK | ☐ |

**Risques résiduels acceptés** :

- …

**Recommandation** :

- [ ] **GO** — usage perso / partage OK
- [ ] **GO avec réserves** — lister les réserves
- [ ] **NO-GO** — bloquants à corriger

**Signature exécutant** : _____________  **Date** : _____________

---

## 11. Ordre d’exécution recommandé (session 60–90 min)

| Phase | Durée | Cas |
|-------|-------|-----|
| 0. Auto | 5 min | `testDebugUnitTest` |
| 1. Smoke | 10 min | SM-01 → SM-05 |
| 2. Séance cœur | 25 min | TC-SE-01 → TC-SE-09, TC-BV-01 → TC-BV-03 |
| 3. Chrono | 10 min | TC-CH-01 → TC-CH-05 |
| 4. Accueil / agenda | 10 min | TC-AC-*, TC-AG-01 → TC-AG-03 |
| 5. Export | 10 min | TC-EX-01 → TC-EX-06 |
| 6. Articles + sombre | 5 min | TC-AR-*, TC-NAV-01 |
| 7. UAT express | 10 min | PC-01, PC-02, PC-04 |
| 8. Bilan | 5 min | Section 10 + fiches défaut |

*Séance complète (TC-SC) : session séparée 45–60 min si besoin.*

---

## 12. Annexes

### A. Commandes utiles

```bash
# SDK + JDK
export PATH="$HOME/Library/Android/sdk/platform-tools:$PATH"
export ANDROID_HOME="$HOME/Library/Android/sdk"
export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"

cd StayStrongApp
./gradlew :app:testDebugUnitTest
./gradlew :app:installDebug
adb shell am start -n fr.gaetan.pompes/.MainActivity
adb uninstall fr.gaetan.pompes
adb logcat | grep -i pompes
```

### B. Matrice risque → cas (extrait)

| Risque | Cas prioritaires |
|--------|------------------|
| R01 | TC-SE-08, TC-EX-01, TC-EX-04, TC-EX-06 |
| R02 | TC-SE-06, TC-SE-07, TC-BV-*, TC-EX-06 |
| R03 | TC-SE-03→05, TC-CH-* |
| R04 | TC-SC-01→03 |
| R05 | TC-AC-01, TC-AC-03 |
| R08 | TC-EX-04, TC-EX-05 |

### C. Références produit

- README racine du dépôt
- Code : `SeanceViewModel`, `ProgressionStore`, `HistoriqueStats`, `ArticlesData`, écrans `ui/`
- Tests auto : `app/src/test/java/fr/gaetan/pompes/`

---

*Document vivant : mettre à jour les cas à chaque feature majeure (principe ISTQB du « pesticide paradox »).*
