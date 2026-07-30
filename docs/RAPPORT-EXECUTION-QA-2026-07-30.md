# Rapport d’exécution QA — Stay Strong

| Champ | Valeur |
|-------|--------|
| Date | 2026-07-30 |
| Device | Pixel 8 Pro (Android 17) |
| Build | debug `fr.gaetan.pompes` |
| Protocole | `docs/PROTOCOLE-QA-ISTQB.md` |
| JDK | 17 |

---

## 1. Tests unitaires (composant)

**Commande :** `./gradlew :app:cleanTestDebugUnitTest :app:testDebugUnitTest --rerun-tasks`

| Suite | Tests | Échecs |
|-------|------:|-------:|
| HistoriqueStatsTest | 2 | 0 |
| ProgrammeDataTest | 12 | 0 |
| ArticlesDataTest | 3 | 0 |
| TestsPassageTest | 4 | 0 |
| SeanceViewModelTest | 11 | 0 |
| ProgressionStoreTest | 12 | 0 |
| **Total** | **44** | **0** |

**Résultat : PASS (100 %)**

Couverture logique : séance (max + reps), progression, export/import, streak, 4 articles, programmes.

---

## 2. Tests instrumentés (système UI Automator)

**Commande :** `./gradlew :app:connectedDebugAndroidTest`

| Test | Résultat | Note |
|------|----------|------|
| packageApplicationCorrect | OK | |
| mainActivitySeLance | OK | |
| demarrageVersExercicesAvecFooter | OK | |
| switchFooterVersArticles | OK | |
| ouvrirAgenda | OK* | *Corrigé pendant le cycle : `By.text("Agenda")` → `textContains("Agenda")` (libellé « Agenda & historique ») |

**1er run :** 4 OK / 1 KO (`ouvrirAgenda` — libellé obsolète)  
**2e run (après fix `ParcoursUiTest.kt`) :** **5/5 PASS**

---

## 3. Smoke système adb (échantillon protocole)

| ID protocole | Résultat | Preuve |
|--------------|----------|--------|
| SM-01 Lancement | OK | pid process |
| SM-01b Commencer | OK | dump UI |
| SM-02 Footer Articles | OK | signaux contenu = 5 |
| SM-02b Retour Entraînement | OK | |
| SM-03 Liste 5 exercices | OK | Tractions…Gainage |
| SM-04 Agenda | OK | stats + calendrier |
| TC-AR-01 Articles | OK | 4 signaux titres |
| TC-AC-01 Continuer | OK | ouvre détail jour |
| TC-SE-01 Détail jour | OK | séries + forme |
| TC-SE-02 Consigne en séance | OK | texte forme affiché |
| TC-SE-03 Séance lancée | OK | SÉRIE 1/5 + bouton cuivre |
| TC-SE-10 Arrêter | OK | retour hors séance |
| R10 Pas de crash FATAL | OK | logcat filtré |

**Non exécutés en auto (longs / manuels restants) :** séance complète 5 exos, saisie max + agenda detailReps, export/import round-trip UI, chrono killy bout en bout, UAT PC-03, BV reps.

---

## 4. Défauts trouvés

| ID | Titre | Sév. | Prio | Statut |
|----|-------|------|------|--------|
| DEF-001 | Test instrumenté `ouvrirAgenda` cassé après renommage bouton | S3 | P2 | **Corrigé** (`ParcoursUiTest` → `textContains("Agenda")`) |

Aucun défaut produit S1/S2 ouvert sur le build testé.

---

## 5. Synthèse couverture

| Niveau | Exécuté | Passé | Échoué |
|--------|--------|------:|-------:|
| Unitaire | 44 | 44 | 0 |
| Instrumenté | 5 | 5 | 0 |
| Smoke adb (échantillon) | 13 | 13 | 0 |

---

## 6. Risque résiduel & recommandation

**Risques encore non couverts dynamiquement sur device :**

- R02 saisie reps max + persistance agenda (logique unitaire OK, UI non jouée jusqu’à la 5e série)
- R04 séance complète 5 exercices (trop long pour ce cycle)
- R08 import JSON via dialogue UI
- R03 chrono maintien bout-en-bout + vibration (unitaires OK)

**Recommandation : GO avec réserves**

- Usage personnel / dogfood OK.
- Avant large partage : jouer manuellement TC-SE-06/07 (max), TC-EX-04 (import), TC-SC-01 (début séance complète).

---

## 7. Artefacts

- Protocole : `docs/PROTOCOLE-QA-ISTQB.md`
- Captures smoke : `/tmp/staystrong-qa/*.png`
- Rapport unitaires : `StayStrongApp/app/build/reports/tests/testDebugUnitTest/`
- Rapport instrumentés : `StayStrongApp/app/build/reports/androidTests/connected/debug/`
