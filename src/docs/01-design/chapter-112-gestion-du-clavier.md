# Documentation de la classe `InputListener`

La classe **`InputListener`** implémente les interfaces **`KeyListener`**, **`Serializable`** et **`GSystem`**. Elle est
conçue pour gérer les événements et interactions du clavier dans un environnement de jeu. Elle surveille les touches
pressées, relâchées et leurs interactions pour exécuter des actions spécifiques ou gérer des systèmes de jeu.

---

## Objectif de la classe

`InputListener` permet de :

1. **Gérer les entrées clavier** : surveiller les appuis et relâchements des touches.
2. **Déclencher des actions spécifiques** : telles que quitter le jeu, basculer en mode plein écran, activer/désactiver
   un mode de pause ou modifier les paramètres de débogage.
3. **S'intégrer dans le cycle de vie** et la gestion des dépendances au sein d'un système modulaire de jeu, grâce à
   l'implémentation de l'interface `GSystem`.

---

## Composition principale

### 1. Attributs

```java
private final GameInterface app;
public boolean[] keys = new boolean[1024];
```

- **`GameInterface app`** : Une instance de la classe principale du jeu, qui permet à l’écouteur d’interagir avec l’état
  et les commandes globales du jeu (exemple : mise en pause ou débogage).
- **`boolean[] keys`** : Un tableau de `boolean` qui conserve l’état de chaque touche (pressée ou non). Le tableau peut
  gérer jusqu’à 1024 touches, ce qui le rend compatible avec une grande variété d’entrées.

---

### 2. Constructeur

```java
public InputListener(GameInterface app) {
    this.app = app;
    debug(InputListener.class, "Start of processing");
}
```

Le constructeur initialise l'instance et associe l'interface de jeu fournie. Il enregistre également un message à des
fins de journalisation via la méthode `debug`.

---

### 3. Gestion des événements clavier via KeyListener

#### a. `keyPressed(KeyEvent e)`

Marque une touche comme pressée en passant la valeur correspondante dans le tableau `keys` à `true`.

**Exemple :**

```java
keys[e.

getKeyCode()]=true;
```

#### b. `keyReleased(KeyEvent e)`

Traite l’action associée à la relâche d’une touche spécifique en fonction de combinaisons ou de conditions.

**Exemples d’actions gérées :**

1. **Quitter le jeu**  
   Relâcher les touches `Q` ou `ECHAP` déclenche une demande de fermeture :
   ```java
   if (isKeyPressed(KeyEvent.VK_Q) || isKeyPressed(KeyEvent.VK_ESCAPE)) {
       app.requestExit();
   }
   ```

2. **Commandes spéciales pour développeur**
    - `CTRL + Z` réinitialise la scène actuelle.
    - `CTRL + D` bascule le mode débogage.
   ```java
   if (e.isControlDown()) {
       if (isKeyPressed(KeyEvent.VK_Z)) {
           scnMgr.getActiveScene().reset();
       }
       if (isKeyPressed(KeyEvent.VK_D)) {
           app.setDebug(app.getDebug() + 1 < 6 ? app.getDebug() + 1 : 0);
       }
   }
   ```

3. **Basculer le mode Pause**  
   Relâcher `P` ou `PAUSE` active/désactive le mode pause :
   ```java
   if (isKeyPressed(KeyEvent.VK_P) || isKeyPressed(KeyEvent.VK_PAUSE)) {
       app.setPause(app.isNotPaused());
   }
   ```

4. **Basculer en plein écran (mode fenêtré/plein écran)**  
   Relâcher `F11` :
   ```java
   if (isKeyPressed(KeyEvent.VK_F11)) {
       Renderer renderer = SystemManager.get(Renderer.class);
       renderer.switchFullScreenMode();
   }
   ```

5. **Réinitialiser l'état des touches**  
   Après traitement, la méthode met à jour l’état pour indiquer que la touche a été relâchée :
   ```java
   keys[e.getKeyCode()] = false;
   ```

#### c. `keyTyped(KeyEvent e)`

Méthode implémentée mais laissée vide (inutile dans ce contexte).

---

### 4. Méthodes du cycle de vie via GSystem

La classe implémente l’interface `GSystem`, ce qui l’intègre dans un système modulaire avec un cycle de vie bien défini.

| Méthodes              | Description                                                                                                                                                             |
|-----------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **`getDependencies`** | Retourne une liste de dépendances système nécessaires, comme `Config` et `Renderer`. Cela garantit qu'elles sont chargées avant que cette instance démarre.             |
| **`initialize`**      | Prépare l'instance pour le jeu (resté vide ici).                                                                                                                        |
| **`start`**           | Démarre l’écouteur (resté vide ici).                                                                                                                                    |
| **`process`**         | Gère les mises à jour par frame : <br> - Réinitialise les forces physiques via le moteur physique.<br> - Envoie les entrées clavier à la scène active et à ses entités. |
| **`stop`**            | Stoppe la gestion des entrées clavier (resté vide ici).                                                                                                                 |
| **`dispose`**         | Libère les ressources utilisées (resté vide ici).                                                                                                                       |

---

### Exemple d’implémentation de la méthode `process`

```java

@Override
public void process(GameInterface game, double elapsed, Map<String, Object> stats) {
    SceneManager sceneManager = SystemManager.get(SceneManager.class);
    Scene scene = sceneManager.getActiveScene();

    PhysicEngine physicEngine = SystemManager.get(PhysicEngine.class);
    if (physicEngine != null) {
        physicEngine.resetForces(scene);
    }

    scene.input(this);
    scene.getEntities().values()
            .forEach(e -> e.getBehaviors()
                    .forEach(b -> b.input(this, e)));
}
```

---

## Résumé

La classe **`InputListener`** offre les fonctionnalités suivantes :

1. **Gestion centralisée des entrées clavier**  
   Grâce au tableau `keys`, elle garde en mémoire et traite les événements de pression et de relâchement des touches.

2. **Déclenchement d'actions spécifiques**  
   Par exemple : quitter le jeu, basculer en mode plein écran, activer un mode pause, et utiliser des commandes pour
   développeurs.

3. **Intégration modulaire**  
   Avec l’interface `GSystem`, cette classe suit un cycle de vie clair et peut s’intégrer facilement avec d'autres
   systèmes (scènes, moteur de physique).

4. **Propagation des entrées vers les entités**  
   Les scènes et comportements des entités peuvent recevoir et réagir aux informations liées aux entrées clavier,
   permettant une interaction dynamique et évolutive dans l’environnement de jeu.

---

### **Conception robuste** :

Cette classe est extensible et parfaitement adaptée à des jeux nécessitant une gestion fiable et complexe des entrées
utilisateur au clavier.

### Récapitulatif des touches et des actions associées

| **Touche**           | **Résumé de l'action**                                                                        |
|----------------------|-----------------------------------------------------------------------------------------------|
| `Q` ou `ECHAP (ESC)` | Quitte le jeu en appelant la méthode `app.requestExit()`.                                     |
| `CTRL + Z`           | Réinitialise la scène active via la méthode `scnMgr.getActiveScene().reset()`.                |
| `CTRL + D`           | Modifie le mode de débogage : incrémente le niveau jusqu'à 5, puis revient à 0.               |
| `P` ou `PAUSE`       | Bascule le mode pause du jeu : met en pause ou reprend le jeu avec `app.setPause()`.          |
| `F11`                | Bascule entre les modes plein écran et fenêtré en appelant `renderer.switchFullScreenMode()`. |

---

### Notes supplémentaires :

- Les actions déclenchées par les touches impliquent souvent une vérification préalable avec la méthode
  `isKeyPressed(int keyCode)` pour s'assurer que la touche a bien été pressée.
- Les combinaisons avec `CTRL` ajoutent une couche de protection pour éviter que certaines actions (comme la
  réinitialisation de la scène) soient exécutées par accident.
- Toutes les autres touches pressées sont ajoutées à l’état (`keys`) correspondant jusqu’à ce qu’elles soient relâchées,
  moment où leur état est mis à `false`.
