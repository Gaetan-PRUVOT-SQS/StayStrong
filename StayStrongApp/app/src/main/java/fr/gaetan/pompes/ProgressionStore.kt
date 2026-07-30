package fr.gaetan.pompes

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class SessionAgenda(
    val date: String,
    val exerciceId: String,
    val exerciceNom: String,
    val niveau: Int,
    val jourProgramme: Int
)

data class AlertePause(
    val message: String,
    val joursRestants: Long
)

// dernière position mémorisée pour un exercice
data class PositionExo(
    val exerciceId: String,
    val niveau: Int,
    val jour: Int
)

// dernière séance complète (niveau + jour communs)
data class PositionSeanceComplete(
    val niveau: Int,
    val jour: Int
)

interface ProgressionRepository {
    fun getJoursFaits(exerciceId: String, niveau: Int): Set<Int>
    fun marquerJourFait(exerciceId: String, niveau: Int, numeroJour: Int)
    fun getJourSuggere(exerciceId: String, niveau: Int): Int
    fun getSessions(): List<SessionAgenda>
    fun getDatesEntrainement(): Set<String>
    fun verifierPause(exerciceId: String, niveau: Int): AlertePause?
    fun getNiveauDebloque(exerciceId: String): Int
    fun validerTestPassage(exerciceId: String, depuisNiveau: Int): Boolean
    fun exporterJson(): String
    fun importerJson(json: String): Boolean

    // reprendre
    fun sauverPositionExo(exerciceId: String, niveau: Int, jour: Int)
    fun getPositionExo(exerciceId: String): PositionExo?
    fun getDernierePositionExo(): PositionExo?
    fun sauverDerniereSeanceComplete(niveau: Int, jour: Int)
    fun getDerniereSeanceComplete(): PositionSeanceComplete?

    // mode sombre
    fun getModeSombre(): Boolean
    fun setModeSombre(actif: Boolean)
}

class ProgressionMemoire : ProgressionRepository {
    private val faits = mutableMapOf<String, MutableSet<Int>>()
    private val sessions = mutableListOf<SessionAgenda>()
    private val niveauxDebloques = mutableMapOf<String, Int>()
    private val positions = mutableMapOf<String, PositionExo>()
    private var derniereSeance: PositionSeanceComplete? = null
    private var dernierExoId: String? = null
    private var modeSombre = false
    var dateTest: String = LocalDate.now().toString()

    private fun cle(exerciceId: String, niveau: Int): String {
        return "${exerciceId}_n$niveau"
    }

    override fun getJoursFaits(exerciceId: String, niveau: Int): Set<Int> {
        return faits[cle(exerciceId, niveau)]?.toSet() ?: emptySet()
    }

    override fun marquerJourFait(exerciceId: String, niveau: Int, numeroJour: Int) {
        val set = faits.getOrPut(cle(exerciceId, niveau)) { mutableSetOf() }
        set.add(numeroJour)
        val nom = ProgrammeData.getExercice(exerciceId).nom
        sessions.add(
            SessionAgenda(
                date = dateTest,
                exerciceId = exerciceId,
                exerciceNom = nom,
                niveau = niveau,
                jourProgramme = numeroJour
            )
        )
        // mémorise aussi la position
        sauverPositionExo(exerciceId, niveau, numeroJour)
    }

    override fun getJourSuggere(exerciceId: String, niveau: Int): Int {
        val maxJours = ProgrammeData.nombreJours(exerciceId, niveau)
        val faitsNiveau = getJoursFaits(exerciceId, niveau)
        if (faitsNiveau.isEmpty()) {
            return 1
        }
        val max = faitsNiveau.maxOrNull() ?: 0
        if (max >= maxJours) {
            return maxJours
        }
        return max + 1
    }

    override fun getSessions(): List<SessionAgenda> = sessions.toList()

    override fun getDatesEntrainement(): Set<String> = sessions.map { it.date }.toSet()

    override fun verifierPause(exerciceId: String, niveau: Int): AlertePause? {
        return calculerAlertePause(getSessions(), exerciceId, niveau, LocalDate.parse(dateTest))
    }

    override fun getNiveauDebloque(exerciceId: String): Int {
        return niveauxDebloques[exerciceId] ?: 1
    }

    override fun validerTestPassage(exerciceId: String, depuisNiveau: Int): Boolean {
        val test = TestsPassage.getTest(exerciceId, depuisNiveau) ?: return false
        val actuel = getNiveauDebloque(exerciceId)
        if (depuisNiveau > actuel) {
            return false
        }
        if (test.versNiveau > actuel) {
            niveauxDebloques[exerciceId] = test.versNiveau
        }
        return true
    }

    override fun exporterJson(): String {
        return construireExportJson(
            getSessions(),
            niveauxDebloques.toMap(),
            faitsExport(),
            positions.toMap(),
            derniereSeance
        )
    }

    private fun faitsExport(): Map<String, Set<Int>> {
        return faits.mapValues { it.value.toSet() }
    }

    override fun importerJson(json: String): Boolean {
        return try {
            val data = parserExportJson(json)
            sessions.clear()
            sessions.addAll(data.sessions)
            niveauxDebloques.clear()
            niveauxDebloques.putAll(data.niveauxDebloques)
            faits.clear()
            data.joursFaits.forEach { (k, v) ->
                faits[k] = v.toMutableSet()
            }
            positions.clear()
            positions.putAll(data.positionsExo)
            if (data.positionsExo.isNotEmpty()) {
                // dernier exo = le plus récent dans la map (ordre d'import)
                dernierExoId = data.positionsExo.keys.last()
            }
            derniereSeance = data.derniereSeanceComplete
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun sauverPositionExo(exerciceId: String, niveau: Int, jour: Int) {
        positions[exerciceId] = PositionExo(exerciceId, niveau, jour)
        dernierExoId = exerciceId
    }

    override fun getPositionExo(exerciceId: String): PositionExo? {
        return positions[exerciceId]
    }

    override fun getDernierePositionExo(): PositionExo? {
        val id = dernierExoId ?: return null
        return positions[id]
    }

    override fun sauverDerniereSeanceComplete(niveau: Int, jour: Int) {
        derniereSeance = PositionSeanceComplete(niveau, jour)
    }

    override fun getDerniereSeanceComplete(): PositionSeanceComplete? {
        return derniereSeance
    }

    override fun getModeSombre(): Boolean = modeSombre

    override fun setModeSombre(actif: Boolean) {
        modeSombre = actif
    }
}

class ProgressionStore(context: Context) : ProgressionRepository {

    private val prefs = context.getSharedPreferences("progression", Context.MODE_PRIVATE)

    private fun cle(exerciceId: String, niveau: Int): String {
        return "jours_faits_${exerciceId}_n$niveau"
    }

    override fun getJoursFaits(exerciceId: String, niveau: Int): Set<Int> {
        val texte = prefs.getString(cle(exerciceId, niveau), "") ?: ""
        if (texte.isEmpty()) {
            return emptySet()
        }
        return texte.split(",").map { it.toInt() }.toSet()
    }

    override fun marquerJourFait(exerciceId: String, niveau: Int, numeroJour: Int) {
        val set = getJoursFaits(exerciceId, niveau).toMutableSet()
        set.add(numeroJour)
        prefs.edit().putString(cle(exerciceId, niveau), set.sorted().joinToString(",")).apply()

        val nom = ProgrammeData.getExercice(exerciceId).nom
        val date = LocalDate.now().toString()
        val ligne = "$date|$exerciceId|$nom|$niveau|$numeroJour"
        val existant = prefs.getString("agenda_sessions", "") ?: ""
        val nouveau = if (existant.isEmpty()) ligne else existant + "\n" + ligne
        prefs.edit().putString("agenda_sessions", nouveau).apply()

        // mémorise la position pour "Reprendre"
        sauverPositionExo(exerciceId, niveau, numeroJour)
    }

    override fun getJourSuggere(exerciceId: String, niveau: Int): Int {
        val maxJours = ProgrammeData.nombreJours(exerciceId, niveau)
        val faits = getJoursFaits(exerciceId, niveau)
        if (faits.isEmpty()) return 1
        val max = faits.maxOrNull() ?: 0
        if (max >= maxJours) return maxJours
        return max + 1
    }

    override fun getSessions(): List<SessionAgenda> {
        val texte = prefs.getString("agenda_sessions", "") ?: ""
        if (texte.isEmpty()) return emptyList()
        val liste = mutableListOf<SessionAgenda>()
        texte.split("\n").forEach { ligne ->
            val parts = ligne.split("|")
            if (parts.size == 5) {
                liste.add(
                    SessionAgenda(
                        date = parts[0],
                        exerciceId = parts[1],
                        exerciceNom = parts[2],
                        niveau = parts[3].toInt(),
                        jourProgramme = parts[4].toInt()
                    )
                )
            }
        }
        return liste
    }

    override fun getDatesEntrainement(): Set<String> = getSessions().map { it.date }.toSet()

    override fun verifierPause(exerciceId: String, niveau: Int): AlertePause? {
        return calculerAlertePause(getSessions(), exerciceId, niveau, LocalDate.now())
    }

    override fun getNiveauDebloque(exerciceId: String): Int {
        return prefs.getInt("niveau_max_$exerciceId", 1)
    }

    override fun validerTestPassage(exerciceId: String, depuisNiveau: Int): Boolean {
        val test = TestsPassage.getTest(exerciceId, depuisNiveau) ?: return false
        val actuel = getNiveauDebloque(exerciceId)
        if (depuisNiveau > actuel) return false
        if (test.versNiveau > actuel) {
            prefs.edit().putInt("niveau_max_$exerciceId", test.versNiveau).apply()
        }
        return true
    }

    override fun exporterJson(): String {
        val niveaux = mutableMapOf<String, Int>()
        val joursFaits = mutableMapOf<String, Set<Int>>()
        val positions = mutableMapOf<String, PositionExo>()
        ProgrammeData.exercices.forEach { exo ->
            niveaux[exo.id] = getNiveauDebloque(exo.id)
            exo.niveaux.forEach { n ->
                val faits = getJoursFaits(exo.id, n.numero)
                if (faits.isNotEmpty()) {
                    joursFaits["${exo.id}_n${n.numero}"] = faits
                }
            }
            val pos = getPositionExo(exo.id)
            if (pos != null) {
                positions[exo.id] = pos
            }
        }
        return construireExportJson(
            getSessions(),
            niveaux,
            joursFaits,
            positions,
            getDerniereSeanceComplete()
        )
    }

    override fun importerJson(json: String): Boolean {
        return try {
            val data = parserExportJson(json)
            val lignes = data.sessions.joinToString("\n") {
                "${it.date}|${it.exerciceId}|${it.exerciceNom}|${it.niveau}|${it.jourProgramme}"
            }
            val editor = prefs.edit()
            editor.putString("agenda_sessions", lignes)
            data.niveauxDebloques.forEach { (id, niv) ->
                editor.putInt("niveau_max_$id", niv)
            }
            data.joursFaits.forEach { (cleFaits, set) ->
                // cle export : pompes_n1 → clé prefs : jours_faits_pompes_n1
                editor.putString("jours_faits_$cleFaits", set.sorted().joinToString(","))
            }
            data.positionsExo.forEach { (id, pos) ->
                editor.putString("pos_$id", "${pos.niveau}|${pos.jour}")
            }
            val sc = data.derniereSeanceComplete
            if (sc != null) {
                editor.putString("pos_seance_complete", "${sc.niveau}|${sc.jour}")
            }
            // dernier exo pour Reprendre global
            if (data.positionsExo.isNotEmpty()) {
                editor.putString("dernier_exo_id", data.positionsExo.keys.last())
            }
            editor.apply()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun sauverPositionExo(exerciceId: String, niveau: Int, jour: Int) {
        prefs.edit()
            .putString("pos_$exerciceId", "$niveau|$jour")
            .putString("dernier_exo_id", exerciceId)
            .apply()
    }

    override fun getPositionExo(exerciceId: String): PositionExo? {
        val texte = prefs.getString("pos_$exerciceId", null) ?: return null
        val parts = texte.split("|")
        if (parts.size != 2) return null
        return try {
            PositionExo(
                exerciceId = exerciceId,
                niveau = parts[0].toInt(),
                jour = parts[1].toInt()
            )
        } catch (e: Exception) {
            null
        }
    }

    override fun getDernierePositionExo(): PositionExo? {
        val id = prefs.getString("dernier_exo_id", null) ?: return null
        return getPositionExo(id)
    }

    override fun sauverDerniereSeanceComplete(niveau: Int, jour: Int) {
        prefs.edit().putString("pos_seance_complete", "$niveau|$jour").apply()
    }

    override fun getDerniereSeanceComplete(): PositionSeanceComplete? {
        val texte = prefs.getString("pos_seance_complete", null) ?: return null
        val parts = texte.split("|")
        if (parts.size != 2) return null
        return try {
            PositionSeanceComplete(niveau = parts[0].toInt(), jour = parts[1].toInt())
        } catch (e: Exception) {
            null
        }
    }

    override fun getModeSombre(): Boolean {
        return prefs.getBoolean("mode_sombre", false)
    }

    override fun setModeSombre(actif: Boolean) {
        prefs.edit().putBoolean("mode_sombre", actif).apply()
    }
}

data class ExportData(
    val sessions: List<SessionAgenda>,
    val niveauxDebloques: Map<String, Int>,
    val joursFaits: Map<String, Set<Int>>,
    val positionsExo: Map<String, PositionExo> = emptyMap(),
    val derniereSeanceComplete: PositionSeanceComplete? = null
)

fun construireExportJson(
    sessions: List<SessionAgenda>,
    niveauxDebloques: Map<String, Int>,
    joursFaits: Map<String, Set<Int>>,
    positionsExo: Map<String, PositionExo> = emptyMap(),
    derniereSeanceComplete: PositionSeanceComplete? = null
): String {
    val root = JSONObject()
    root.put("app", "Stay Strong")
    root.put("version", 2)
    root.put("exporteLe", LocalDate.now().toString())

    val arr = JSONArray()
    sessions.forEach { s ->
        val o = JSONObject()
        o.put("date", s.date)
        o.put("exerciceId", s.exerciceId)
        o.put("exerciceNom", s.exerciceNom)
        o.put("niveau", s.niveau)
        o.put("jourProgramme", s.jourProgramme)
        arr.put(o)
    }
    root.put("sessions", arr)

    val niv = JSONObject()
    niveauxDebloques.forEach { (k, v) -> niv.put(k, v) }
    root.put("niveauxDebloques", niv)

    val jf = JSONObject()
    joursFaits.forEach { (k, set) ->
        val a = JSONArray()
        set.sorted().forEach { a.put(it) }
        jf.put(k, a)
    }
    root.put("joursFaits", jf)

    // positions pour reprendre
    val pos = JSONObject()
    positionsExo.forEach { (id, p) ->
        val o = JSONObject()
        o.put("niveau", p.niveau)
        o.put("jour", p.jour)
        pos.put(id, o)
    }
    root.put("positionsExo", pos)

    if (derniereSeanceComplete != null) {
        val sc = JSONObject()
        sc.put("niveau", derniereSeanceComplete.niveau)
        sc.put("jour", derniereSeanceComplete.jour)
        root.put("derniereSeanceComplete", sc)
    }

    return root.toString(2)
}

fun parserExportJson(json: String): ExportData {
    val root = JSONObject(json)
    val sessions = mutableListOf<SessionAgenda>()
    val arr = root.getJSONArray("sessions")
    for (i in 0 until arr.length()) {
        val o = arr.getJSONObject(i)
        sessions.add(
            SessionAgenda(
                date = o.getString("date"),
                exerciceId = o.getString("exerciceId"),
                exerciceNom = o.getString("exerciceNom"),
                niveau = o.getInt("niveau"),
                jourProgramme = o.getInt("jourProgramme")
            )
        )
    }
    val niveaux = mutableMapOf<String, Int>()
    val nivObj = root.optJSONObject("niveauxDebloques")
    if (nivObj != null) {
        nivObj.keys().forEach { k ->
            niveaux[k] = nivObj.getInt(k)
        }
    }
    val jours = mutableMapOf<String, Set<Int>>()
    val jf = root.optJSONObject("joursFaits")
    if (jf != null) {
        jf.keys().forEach { k ->
            val a = jf.getJSONArray(k)
            val set = mutableSetOf<Int>()
            for (i in 0 until a.length()) {
                set.add(a.getInt(i))
            }
            jours[k] = set
        }
    }
    val positions = mutableMapOf<String, PositionExo>()
    val posObj = root.optJSONObject("positionsExo")
    if (posObj != null) {
        posObj.keys().forEach { id ->
            val o = posObj.getJSONObject(id)
            positions[id] = PositionExo(
                exerciceId = id,
                niveau = o.getInt("niveau"),
                jour = o.getInt("jour")
            )
        }
    }
    var seanceComplete: PositionSeanceComplete? = null
    val scObj = root.optJSONObject("derniereSeanceComplete")
    if (scObj != null) {
        seanceComplete = PositionSeanceComplete(
            niveau = scObj.getInt("niveau"),
            jour = scObj.getInt("jour")
        )
    }
    return ExportData(sessions, niveaux, jours, positions, seanceComplete)
}

fun calculerAlertePause(
    sessions: List<SessionAgenda>,
    exerciceId: String,
    niveau: Int,
    aujourdhui: LocalDate
): AlertePause? {
    val dernieres = sessions.filter {
        it.exerciceId == exerciceId && it.niveau == niveau
    }
    if (dernieres.isEmpty()) return null
    val derniere = dernieres.maxByOrNull { it.date } ?: return null
    val jourProg = try {
        ProgrammeData.getJour(exerciceId, niveau, derniere.jourProgramme)
    } catch (e: Exception) {
        return null
    }
    val pauseMin = jourProg.pauseJoursApres
    if (pauseMin <= 0) return null
    val dateDerniere = try {
        LocalDate.parse(derniere.date)
    } catch (e: Exception) {
        return null
    }
    val joursEcoules = ChronoUnit.DAYS.between(dateDerniere, aujourdhui)
    if (joursEcoules >= pauseMin) return null
    val restants = pauseMin - joursEcoules
    val texte = if (restants <= 1) {
        "Pause conseillée : encore 1 jour minimum avant la prochaine séance (${derniere.exerciceNom} niv. $niveau)."
    } else {
        "Pause conseillée : encore $restants jours minimum avant la prochaine séance (${derniere.exerciceNom} niv. $niveau)."
    }
    return AlertePause(message = texte, joursRestants = restants)
}
