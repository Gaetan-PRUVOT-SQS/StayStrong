package fr.gaetan.pompes

// Contenu des deux articles (PDF racine), structuré pour l'app

data class BlocArticle(
    val titre: String,
    val texte: String,
    // image d'exercice existante (optionnel)
    val exerciceId: String? = null,
    // petit pictogramme texte
    val emoji: String = ""
)

data class Article(
    val id: String,
    val titre: String,
    val sousTitre: String,
    val emoji: String,
    val resume: String,
    val blocs: List<BlocArticle>
)

object ArticlesData {

    val articles: List<Article> = listOf(
        Article(
            id = "exercices",
            titre = "Ce que chaque exercice apporte",
            sousTitre = "Cinq exercices, cinq fonctions",
            emoji = "💪",
            resume = "Aucun ne remplace les autres. Voici le rôle de chaque mouvement du circuit.",
            blocs = listOf(
                BlocArticle(
                    titre = "Introduction",
                    texte = "Cinq exercices au poids de corps, cinq fonctions distinctes. Aucun ne remplace les autres.",
                    emoji = "📖"
                ),
                BlocArticle(
                    titre = "Les pompes",
                    texte = "Les pompes couvrent la poussée horizontale : pectoraux, deltoïdes antérieurs, triceps. Leur intérêt dépasse ces muscles. Maintenir le corps rigide entre les mains et les pieds impose au tronc un travail de gainage permanent, ce qui en fait un exercice global plutôt qu'un exercice de pectoraux.\n\nElles se chargent sans matériel : mains surélevées pour alléger, pieds surélevés, écartement modifié, tempo ralenti, appui sur une seule main pour durcir. La progression ne bute donc jamais sur l'équipement, seulement sur la technique.\n\nUne étude de cohorte publiée en 2019 sur des pompiers américains a associé la capacité à réaliser plus de quarante pompes à une incidence d'événements cardiovasculaires nettement plus faible sur dix ans. La corrélation ne prouve pas que les pompes protègent le cœur, mais elle confirme que ce test résume bien la condition physique générale.",
                    exerciceId = "pompes",
                    emoji = "🙌"
                ),
                BlocArticle(
                    titre = "Les tractions",
                    texte = "Les tractions sont le seul exercice de cette liste qui charge réellement le dos : grand dorsal, trapèzes inférieurs, rhomboïdes, biceps et avant-bras. Elles compensent directement la posture d'écran, où les épaules s'enroulent vers l'avant et les muscles interscapulaires s'affaiblissent.\n\nElles développent aussi la force de préhension. La force de serrage est l'un des marqueurs les mieux corrélés à la mortalité toutes causes dans les études épidémiologiques, plus fiable que la pression artérielle systolique dans certaines cohortes.\n\nC'est le mouvement le plus lent à progresser du lot, et celui qui demande le plus de patience. C'est aussi pour cette raison qu'il mérite d'être placé en début de séance.",
                    exerciceId = "tractions",
                    emoji = "🧗"
                ),
                BlocArticle(
                    titre = "Les squats",
                    texte = "Les squats sollicitent la plus grande masse musculaire du corps : quadriceps, fessiers, ischio-jambiers. Cette masse est le principal réservoir métabolique de l'organisme, ce qui rend l'exercice utile bien au-delà de la performance sportive.\n\nLe transfert vers la vie quotidienne est immédiat : se lever d'une chaise, monter un escalier, porter une charge au sol reposent sur le même schéma moteur. La descente complète entretient par ailleurs la mobilité de hanche et de cheville, qui se perd vite en position assise prolongée.\n\nAu poids de corps, le volume nécessaire est élevé — plusieurs centaines de répétitions par séance aux niveaux avancés. Cette caractéristique en fait autant un exercice cardio-respiratoire qu'un exercice de force.",
                    exerciceId = "squats",
                    emoji = "🦵"
                ),
                BlocArticle(
                    titre = "Le Killy",
                    texte = "Le Killy, chaise isométrique dos au mur genoux à 90°, travaille ce que le squat ne travaille pas : l'endurance de force en position tenue. Le squat entraîne le mouvement, le Killy entraîne la tenue. Les deux qualités sont distinctes et se transfèrent mal l'une à l'autre.\n\nL'absence de mouvement supprime l'impact articulaire, ce qui explique son usage courant en rééducation du genou, notamment après lésion ligamentaire, où la charge peut être dosée finement sans cisaillement.\n\nIl a enfin une dimension mentale que les autres exercices n'ont pas. Tenir au-delà de deux minutes relève autant de la tolérance à l'inconfort que de la capacité musculaire. C'est la raison pour laquelle il figure dans les tests de sélection militaires et de sports d'hiver.",
                    exerciceId = "killy",
                    emoji = "🪑"
                ),
                BlocArticle(
                    titre = "Le gainage facial et latéral",
                    texte = "Le rôle du tronc n'est pas de produire du mouvement mais d'en empêcher. Le gainage facial entraîne la résistance à l'extension lombaire, le gainage latéral la résistance à la flexion de côté. Cette fonction anti-mouvement est exactement celle que le tronc remplit en traction, en pompe et en squat.\n\nLe latéral mérite une attention particulière : il révèle et corrige les asymétries gauche-droite, très fréquentes et invisibles sur les exercices bilatéraux. Un écart marqué entre les deux côtés est un signal à traiter avant qu'il ne se traduise par une compensation ailleurs.\n\nSon coût articulaire est nul, sa récupération rapide. C'est pourquoi il peut figurer dans toutes les séances sans compromettre la progression du reste.",
                    exerciceId = "gainage",
                    emoji = "🧱"
                ),
                BlocArticle(
                    titre = "Ce que l'ensemble couvre… et ce qu'il ne couvre pas",
                    texte = "En résumé :\n• Poussée horizontale → Pompes\n• Tirage vertical → Tractions\n• Extension des jambes → Squats\n• Endurance isométrique → Killy\n• Stabilité du tronc → Gainage facial et latéral\n\nTrois familles de mouvement restent absentes. Le tirage horizontal, que les tractions ne remplacent pas et qui protège l'épaule en équilibrant la poussée des pompes. La charnière de hanche, c'est-à-dire le schéma du soulevé de terre, sur lequel le squat ne transfère que partiellement. Et le cardio prolongé : ces cinq exercices élèvent la fréquence cardiaque mais ne construisent pas d'endurance aérobie au sens strict.\n\nUn tirage horizontal sous barre basse et vingt à trente minutes de course ou de vélo deux fois par semaine suffisent à combler ces manques.",
                    emoji = "⚖️"
                ),
                BlocArticle(
                    titre = "Note",
                    texte = "Les bénéfices décrits ici supposent une exécution correcte et une progression régulière. En cas de douleur articulaire persistante ou d'antécédent de blessure, l'avis d'un professionnel de santé prime sur tout programme écrit.",
                    emoji = "⚠️"
                )
            )
        ),
        Article(
            id = "circuit",
            titre = "Pourquoi faire le circuit complet",
            sousTitre = "L'enchaînement des cinq exercices",
            emoji = "🔄",
            resume = "Ce que l'enchaînement apporte de plus que les cinq exercices pris séparément.",
            blocs = listOf(
                BlocArticle(
                    titre = "Introduction",
                    texte = "Ce que l'enchaînement des cinq exercices apporte de plus que les cinq exercices pris séparément.",
                    emoji = "📖"
                ),
                BlocArticle(
                    titre = "L'équilibre, pas l'accumulation",
                    texte = "Le principal bénéfice du circuit ne vient pas du volume total mais de sa répartition. Les pompes poussent, les tractions tirent, dans un rapport de charge équivalent. Cet équilibre entre les deux faces de l'épaule est la meilleure protection connue contre les conflits sous-acromiaux, dont la cause la plus fréquente est justement un excès de poussée sans tirage compensatoire.\n\nLa même logique joue verticalement. Un programme haut du corps seul crée un déséquilibre de masse et de puissance avec les jambes ; un programme bas du corps seul laisse une faiblesse de tirage qui se paie en posture. Le circuit rend ce déséquilibre structurellement impossible : on ne peut pas sauter la moitié du travail sans que ça se voie dans les fiches.",
                    emoji = "⚖️"
                ),
                BlocArticle(
                    titre = "Un tronc entraîné dans son vrai rôle",
                    texte = "Isolé, le gainage est un exercice abstrait. Placé en fin de circuit, il prend son sens : on vient de demander au tronc de stabiliser une traction, une pompe et un squat, et on termine en entraînant directement cette fonction. La séance construit ainsi le transfert de force entre le haut et le bas du corps, qui est ce qui rend une force utilisable en dehors de la salle.\n\nLe Killy joue un rôle comparable pour le genou. Le squat entraîne le mouvement, le Killy la tenue sous contrainte ; l'articulation est renforcée dans les deux régimes plutôt que dans un seul.",
                    exerciceId = "gainage",
                    emoji = "🧱"
                ),
                BlocArticle(
                    titre = "La densité fait le travail cardio",
                    texte = "Avec soixante secondes de repos, l'enchaînement ne laisse jamais la fréquence cardiaque redescendre complètement. La séance devient mixte : assez lourde par série pour stimuler la force, assez dense dans son ensemble pour solliciter le système cardio-respiratoire. Aucun des cinq exercices ne produit cet effet isolément.\n\nC'est ce qui permet de traiter en une seule séance ce qui demanderait sinon deux entraînements distincts, force d'un côté et endurance de l'autre.",
                    emoji = "❤️"
                ),
                BlocArticle(
                    titre = "Une séance courte, sans matériel, tenable dans la durée",
                    texte = "Le circuit complet tient en trente-cinq à quarante-cinq minutes, temps de repos inclus. Il ne demande qu'une barre de traction et un mur. Cette absence de friction — pas de trajet, pas d'abonnement, pas d'attente sur une machine — est le facteur le mieux corrélé à l'assiduité réelle sur plusieurs mois.\n\nUn seul système de niveaux gouverne les cinq exercices, avec le même format de fiche et les mêmes jours de pause. Il n'y a qu'un rythme à retenir et qu'un calendrier à tenir, ce qui compte davantage, sur un an, que l'optimalité théorique d'un programme plus compliqué.",
                    emoji = "⏱"
                ),
                BlocArticle(
                    titre = "Des repères de progression mesurables",
                    texte = "Chaque exercice se termine par un test chiffré. Ces cinq nombres constituent un bilan de condition physique plus complet que n'importe lequel d'entre eux pris seul, et plusieurs sont des marqueurs de santé documentés : la capacité en pompes est associée au risque cardiovasculaire, la force de préhension développée par les tractions à la mortalité toutes causes.\n\n• Tractions — force de tirage et préhension\n• Pompes — force de poussée et condition générale\n• Squats — force et endurance des jambes\n• Killy — endurance isométrique, tolérance à l'effort\n• Gainage — stabilité du tronc, symétrie gauche-droite",
                    emoji = "📊"
                ),
                BlocArticle(
                    titre = "L'ordre compte",
                    texte = "Le bénéfice du circuit disparaît si l'ordre est arbitraire.\n\n1. Les tractions d'abord, quand le système nerveux est frais.\n2. Les pompes ensuite, car des triceps fatigués dégradent le tirage.\n3. Les squats après le haut du corps, pour que l'essoufflement ne plombe pas les séries de force.\n4. Le Killy juste derrière, sur la même chaîne musculaire.\n5. Le gainage en dernier, parce qu'un tronc fatigué compromet la posture de tout ce qui précède, avec un risque lombaire réel.\n\nLes jours de pause des cinq programmes doivent également tomber ensemble. Étalés sur des jours différents, ils ne laissent plus aucune journée de récupération complète et perdent leur fonction.",
                    emoji = "📋"
                ),
                BlocArticle(
                    titre = "Ce que le circuit ne règle pas",
                    texte = "Trois manques subsistent. Le tirage horizontal, que les tractions ne remplacent pas. La charnière de hanche, sur laquelle le squat ne transfère que partiellement. Le cardio prolongé, que la densité de la séance ne remplace pas au-delà de quelques minutes d'effort continu.\n\nIl existe aussi un plafond. Au poids de corps, la progression finit par buter : au-delà, il faut du lest ou des variantes unilatérales, ce que les niveaux les plus élevés indiquent déjà.",
                    emoji = "🚧"
                ),
                BlocArticle(
                    titre = "Note",
                    texte = "Ces bénéfices supposent une exécution correcte et une progression régulière. En cas de douleur articulaire persistante ou d'antécédent de blessure, l'avis d'un professionnel de santé prime sur tout programme écrit.",
                    emoji = "⚠️"
                )
            )
        )
    )

    fun getArticle(id: String): Article {
        return articles.first { it.id == id }
    }
}
