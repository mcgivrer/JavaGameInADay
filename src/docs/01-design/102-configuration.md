# Configuration

Ce court châpitre fera le tour des besoins en configuration que nous avons et comment nous pourrons fournir
un service permettant de gérer les entrées et les consommer.

## Contexte

La configuration sera servie via un fichier java ".properties".
Les différents services et traitement internes auront le propre section dans le fichier.

Quelques règles :

1. Chaque clé de configuration dans le fichier aura son nom préfixé apr `app.`.
2. Le nom de section doit suivre le préfixe `app.`, exemple `app.physic` définira toutes les clés pour le moteur
   physique.
3. Pas plus de QUATRE suffixes dans le nommage, après le nom de la section correspondant au service.

## Les sections

### Rendering

La section rendering fournira la taille de la fenêtre d'affichage ainsi que la résolution du buffer de rendu interne.
Ces 2 entrées seront au format `[width]x[height]`

- app.render.window.size = 640x400
- app.render.buffer.size = 640x400

### Physic

La section physic permettra de définir la zone de jeu ansi que la gravité

- app.physic.world.play.area.size = 1080x800
- app.physic.world.gravity = (0,-0.981)

### Scene

La section Scene définit la liste des implementations de `Scene` pour le jeu ainsi que la scene à activer par défaut au
démarrage du jeu.

- app.scene.list = play:com.snapgames.demo.scenes.PlayScene,
- app.scene.default = play