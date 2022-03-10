## Traducteur du Langage des Signes


    Projet réalisé par Adrien DUBOIS, Erwan LE BLÉVEC, Vincent TCHOUMBA et Pierre CHECCHIN 
               dans le cadre du module PRO 3600 de l'école TÉLÉCOM SUDPARIS.

![Test](https://github.com/addbs/Sign_Translator/blob/master/src/main/resources/Logo_TSP.png)

### Pourquoi ?

Ce projet est né du regroupement de 4 élèves de 1ere année en école
d'ingénieur. Nous étions tous intéressés par le machine learning et
souhaitions en même temps réaliser quelque chose d'applicable dans la
vie courante. L'idée de créer une intelligence artificielle capable
d'interpréter une forme contenue dans une image et d'appliquer cela au
langage des signes correspondait tout à fait à nos critères, c'est
pourquoi nous avons décidé de lancer ce projet.

Le choix du langage Java est dû à l'envie que nous avions d'implémenter
l'algorithme dans une application, et la création d'interface graphique
est justement quelque chose d'aisé en Java. Pour éviter des potentiels
problèmes de compatibilité nous avons décidé de développer également
l'IA en Java, via la bibliothèque Deep Java Library (DJL) récemment mise
en ligne par Amazon (plus d'informations sur cette bibliothèque à
l'adresse suivante : [https://djl.ai](https://djl.ai)).

### Utilisation 

Le dossier "NeuralNetwork" contient l'ensemble des fichiers nécessaires
à l'exécution de l'application.

Une fois le programme téléchargé (démarche pour Eclipse) :

-   "Run Configuration"

-   "Maven Configuration"

-   "New Configuration"

-   "Run"

La page d'accueil devrait s'afficher. Bonne traduction !

### Avancée du projet 

Pour réaliser ce projet nous avons dû faire face à un certain nombre de
problématiques.

Ces dernières ont fait intervenir principalement trois aspects de la
programmation en Java :

-   Le premier est la gestion d'une interface graphique. Nous avons utilisé JavaFx pour développer nos interfaces. Ces interfaces
    couvrent l'ensemble du projet, c'est à dire la totalité des interactions entre l'utilisateur, son ordinateur et notre algorithme.

-   Le second fut la gestion des requêtes de l'utilisateur : sélection de l'image via une boîte de dialogue ou prise de photo directement 
    depuis la webcam de l'utilisateur - *fonctionnalité non aboutie pour le moment.*

-   Le dernier correspond au coeur du projet : le développement d'un IA via la bibliothèque DJL. Pour cela nous avons eu à :

    -   Générer un dataset traitable par la bibliothèque

    -   Choisir la configuration du réseau : MLP ou CNN ? (Ici il s'agit d'un CNN), quel type de blocs, quel nombre de couches, de
        neurones par couche, quelle initialisation pour les poids..

    -   Choisir la méthode d'entrainement : quelle fonction de perte, quelle fonction d'activation ? Sur quel volume de données entrainer l'algorithme ?

Ce projet fut également l'occasion de mettre en pratique les cours
dispensés dans le module **MAT 3502 - "Statistiques et Analyse de
données"** notamment concernant la partie *"Réduction de dimension"* avec
la mise en place d'algorithmes de filtrage et de segmentation d'images.
Ces algorithmes nous ont permis d'améliorer la précision de notre
algorithme tout en diminuant son temps d'entrainement, leur
implémentation fut grandement utile.

De plus nous nous sommes aussi intéressés à des questions d'ordres plus
éthique : quels sont les biais que nous insérons dans l'algorithme en
favorisant une hypothèse plutôt qu'une autre ? Comment palier cela ?
Pouvoir visualiser les erreurs de biais que réalisaient nos modèles à
nos débuts a été une expérience particulièrement enrichissante.

### Bilan 

Notre bilan est la création d'un modèle fonctionnel, capable de
reconnaître avec un taux de réussite de 70% un signe parmis ceux de
l'alphabet du langage des signes (moyenne calculée à
partir de tests réalisés sur 5 dataset "Tests" de 130 (5\*26) photos
chacun que l'algorithme n'avait jamais vu).

Merci d'avoir pris le temps de lire ce bref résumé portant sur la
réalisation de notre projet.  
En vous souhaitant une agréable découverte de cette application,

L'ensemble des membres du groupe.
