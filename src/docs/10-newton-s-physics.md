# Ajoutons un peu de Physique

Dans un jeu 2D, certes le gameplay est important, mais l'aspect simulation de la réalité l'est tout autant.

Aussi, apporter un peu de réalité physique peut largement apporter au gameplay, mais assi permettre quelques nouveaux
effets dans celui-ci, comme influencer des objects quand ils pénètrent une zone particulière de la zone de jeux, pour
leur faire subir, du vent, une contrainte magnétique, etc ...

Votre seule limite, alors, sera votre imagination sur comment modifier les paramètre de la simulation pour apporter de
nouveau gameplay, comme on a pu l'observer dans les jeux Mario ces dernières années.

## Contexte

Faisons en sorte que nos objets soient animés de façon un plus réaliste.
Rendons plus vivante notre scène en ajoutant quelques éléments de la physique du mouvement dont Newton
et sa pomme ont été les premiers contributeurs.

![Portrait de Sir Isaac  Newton, 1689](https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/Portrait_of_Sir_Isaac_Newton%2C_1689.jpg/199px-Portrait_of_Sir_Isaac_Newton%2C_1689.jpg "Portrait de Sir Isaac  Newton, 1689, image référence wikipedia")

_fig. 1 - Portrait de Sir Isaac Newton, 1689_

Voici résumé les 3 lois de Newton :

1. **Première loi de Newton** : Tout objet reste au repos ou en mouvement rectiligne uniforme sauf si une force nette
   agit sur lui.
2. **Deuxième loi de Newton** : La force appliquée sur un objet est proportionnelle à son accélération, et inversement
   proportionnelle à sa masse.
3. **Troisième loi de Newton** : Pour chaque action, il y a une réaction égale et opposée.

Voyons comment passer de ces belles lois à un peu de math, et à du code !

## Un peu de théorie

Dans un monde en deux dimensions, la vitesse ($\vec{v}$) d'un objet peut être exprimée en fonction de l'accélération (
$\vec{a}$) et du temps ($t$) à l'aide de la formule suivante :

$\vec{V} = \vec{V_0} + \vec{a} * t$

où :

- ($\vec{V_0}$) est la vitesse initiale de l'objet,
- ($\vec{a}$) est le vecteur d'accélération,
- ($t$) est le temps écoulé.

- Cette formule indique que la vitesse à un moment donné est égale à la vitesse initiale plus la variation de vitesse
  causée par l'accélération sur une période de temps. En deux dimensions, les vecteurs peuvent être décomposés en
  composantes (x) et (y) pour chaque variable.

Dans un monde en deux dimensions, l'accélération (\vec{a}) d'un objet peut être calculée à partir des forces appliquées
en utilisant la deuxième loi de Newton, qui s'exprime par la formule suivante :

$\vec{F}_{\text{résultante}} = m * \vec{a}$

où :

- ($\vec{F}_{\text{résultante}}$) est la force résultante agissant sur l'objet,
- ($m$) est la masse de l'objet,
- ($\vec{a}$) est l'accélération de l'objet.

Pour calculer l'accélération, vous pouvez suivre ces étapes :

1. Déterminer les forces appliquées : Identifiez toutes les forces agissant sur l'objet. Cela peut inclure des forces
   telles que la gravité, la friction, la tension, etc.
2. Calculer la force résultante : Additionnez toutes les forces vectoriellement. Si vous avez des
   forces ($\vec{F_1}$), ($
   \vec{F_2}$), etc., la force résultante est donnée par :

$\vec{F}_{\text{résultante}} = \vec{F_1} + \vec{F_2} + ... + \vec{F_n}  $

3. Appliquer la deuxième loi de Newton : Une fois que vous avez la force résultante, vous pouvez calculer l'accélération
   en
   réarrangeant la formule :

$ \vec{a} = \vec{F}_{\text{résultante}} / m$

Cette formule vous donnera l'accélération ($\vec{a}$) de l'objet en fonction des forces appliquées et de sa masse ($m$).

La position résultante pour l'objet en mouvement sera alors :

$\vec{P} = \vec{P_0} + 0.5 * \vec{V} * t$

où :

- $\vec{P_0}$ est la position précédente connue
- $\vec{V}$ est la vitesse résultante
- $t$ le temps écoulé depuis le précédent calcul.

## Un peu de code Java

Nous allons voir étape par étape, classe par classe, une solution possible d'implémentation d'un moteur de physique qui
remplira le rôle principal d'animation et de coordination des mouvements des entités au sein d'une scene.

Nous commencerons par modéliser un objet censé bouger sur notre écran de jeu, puis, point par point, nous
construirons notre moteur physique, en implémentant fonction a près fonction l'ensemble des operations nécessaires pour
obtenir une simulation suffisament précise pour l'emploi dans un jeu de plateforme 2D.

### Une Entité

Si maintenant, nous souhaitons modéliser nos objets animés, nous devons créer un certain nombre d'attributs permettant
de représenter ces vecteurs et forces, ainsi que quelques attributs permettant d'identifier facilement les objets.

Voici une première proposition :

![Entity UML model](https://www.plantuml.com/plantuml/png/PSwn2W8n383XlKyH7y35iOZSukOck9SqUe4saIQASjoxsqCHn2tv-0aPp5FpMorXvIDLWggY0KioWxqu-nEc06kOUkUCCx1aUiIYSbcOytUKL2aUlV5xlQgniqg44w5hsonufwBOR_vWGgH2BVtLPsr85WzilltmBinXv4nGoKVDOI39_VaN "Entity UML model")

_fig. 2 - le modèle UML pour la class Entity_

Ce qui se traduira par le code suivant :

```java
public class Entity extends Rectangle2D {
    private static long index = 0;
    private long id = index++;
    private String name = "entity_" + id;

    private Vector2 position = new Vector2();
    private Vector2 velocity = new Vector2();
    private Vector2 acceleration = new Vector2();
    private List<Vector2> forces = new ArrayList<>();

    public Entity(String name) {
        this.name = name;
    }

    public void update(double time) {
        setRect(position.x, position.y, width, height);
        // TODO
    }
}
```

Cette classe Entity hérite de la classe [
Rectangle2D](https://docs.oracle.com/en/java/javase/23/docs/api/java.desktop/java/awt/geom/Rectangle2D.html "The Rectangle2D in the JDK documentation")
du JDK, ce afin de faciliter l'implémentation à venir de certains
contrôles et comparaison. pour que cela fonctionne, nous utiliserons une méthode update qui synchronisera la position
du Rectangle2D
avec celle issue du `Vector2` position.

Notre classe devra également proposer quelques accesseurs pour définir les différentes valeurs des
attributs. Nous ne les aborderons pas ici, je vous invite à aller voir le code source.
Cependant, il est à noter que nous proposerons une implémentation
dite [Fluent Interface](https://en.wikipedia.org/wiki/Fluent_interface "Definition of a Fluent API from wkipedia")
permettant la création facile d'entité, passant par le principe
de [Method Cascading](https://en.wikipedia.org/wiki/Method_cascading "Method Cascading on Wikipedia").

Nous avons la base de nos entités.

Afin de satisfaire la seconde loi, nous ajouterons également la masse, et bien sûr, ses accesseurs:

```java
public class Entity {
    //...
    List<Vector2> forces = new ArrayList<>();
    private double mass = 1.0;
    //...
}
```

> **IMPORTANT** Afin d'éviter tout futur problème de calcul lié à la possible division par zéro, nous prenons la valeur
> 1.0 par défaut.

Nous pourrons ajouter d'autres attributs plus tard via la notion de "matériel" pour jouer sur les paramètres de friction
et d'élasticité de nos entités.

Regardons d'un peu plus près maintenant l'implementation du moteur physic qui sera en charge des calculs.

### Le service PhysicEngine

Ce que nous savons à travers les lois de Newton, c'est que le mouvement de notre Entité sera dirigé par les forces qui
lui seront appliquées et du temps écoulé.

Commençons par calculer l'accélération résultante de ces forces :

```java
public class PhysicEngine {

    public PhysicEngine() {

    }

    public void update(Entity e, elapsed time) {
        // Calculons la somme des forces appliquées pour obtenir l'accélération résultante
        e.setAcceleration(e.getAcceleration().addAll(e.getForces()).divide(e.getMass()));

        // La vélocité et le résultat l'effet de l'accélération en fonction du temps écoulé 
        e.setVelocity(e.getVelocity().add(e.getAcceleration().multiply(time)));

        // la position résultante est calculée en fonction de la vitesse et du temps écoulé.
        e.setPosition(e.getPosition().add(e.getVelocity().multiply(0.5).multiply(time)));

        // on supprime toutes les forces appliquées en attendant le prochain cycle dans la boucle de jeu.
        e.getForces().clear();
    }

}
```

Et pour l'appliquer à l'ensemble des entités actives de la `Scene`:

```java
public class PhysicEngine {
    //...

    public void update(Scene s, elapsed time) {
        scene.getEntities().values().stream()
                .filter(Entity::isActive)
                .forEach(e -> {
                    // apply Physic rules
                    update(e, time);
                    // update the position in inherited Rectangle2D from Entity.
                    e.update(time);
                });
    }
}
```

Ce code peut être décrit sommairement via UML avec ce diagramme d'activités :

![Calculs dans le moteur physique pour l'ensemble des entités d'une scene.](http://www.plantuml.com/plantuml/png/VOwnoeGm48JxFCMM2lulOF7Z5NAjS71zY8Eta6p4h8LlVs9IU1nd4vZCV3lJ9RMBhi6Rkmtu-wVXjILslKmiQ6cTHwke7Ww2XfG3QdDEq4uSPaiJj1TbPQIgDZx6cL2q8Vg0VjKS_DRaccycsoqbwCqvU2nMESfryWaVtIwkKqDCN6xbtxDVrkLPaD5q-xC6_mO0 "Calculs dans le moteur physique pour l'ensemble des entités d'une scene")

_fig. 3 - Calculs dans le moteur physique pour l'ensemble des entités d'une scene._

Nous avons le fondement de notre moteur de calcul. Il est temps de mettre quelques contraintes, afin de garder les
entités dans un espace visible, et dans des limites de vitesse et d'accélération contrôlées.

### Les limites liées au jeu

Dans l'absolue, la proposition d'implémentation pourrait suffire, mais dans la réalité, la fenêtre par laquelle nous
regardons notre espace de jeu est limitée.

Ce sera notre première limite à définir : garder les entités de notre scene dans l'espace du monde de notre jeu.

![Notre Entité soumise à un ensemble de forces et limitée dans l'espace](https://docs.google.com/drawings/d/e/2PACX-1vS1mK0tLz4VBBNbMNIJxtHGTymADBu7emdwWDRA5RIwxEnJQ0DcOFqP4uCc7lFwj77qbLl3Ntm9tzbO/pub?w=549&h=362 "Notre Entité soumise à un ensemble de forces et limitée dans l'espace")

_fig. 2 - Notre Entité soumise à un ensemble de forces et limitée dans l'espace_

Nous allons donc passer par un autre objet qui sera attaché à notre scene, et qui définira cette limite.

### La classe World

Notre nouvel object sera défini par une class World, permettant dans un premier temps de définir la zone de jeu dans
laquelle les entités de la scène évolueront.

```java
import java.awt.geom.Rectangle2D;

public class World {
    private Rectangle2D playArea;

    public World() {
        playArea = new Rectangle2D.Double(0, 0, 320, 200);
    }
}
```

Par défaut, et pour a nouveau éviter des erreurs de calcul ou tout problème de valeur nulle, nous initialisons la zone
de jeu fin définir une zone minimum de 320 par 200.

> **NOTE** La taille de cette zone de jeu correspond à la taille minimum par défaut
> de la fenêtre d'affichage de notre jeu.

Nous pouvons donc faire évoluer notre moteur physique en lui ajoutant une méthode permettant de contenir toute entité
dans la zone de jeu :

```java
public class PhysicEngine {
    //...

    public void update(Scene s, elapsed time) {
        scene.getEntities().values().stream()
                .filter(Entity::isActive)
                .forEach(e -> {
                    //...
                    keepEntityInWorld(scene.getWorld(), e);
                });
    }

    public void keepEntityInWorld(World w, Entity e) {
        if (!world.getPlayArea().contains(e)) {
            if (!w.contains(e) || w.intersects(e)) {
                if (e.x < w.x) {
                    e.x = w.x;
                }
                if (e.x + e.width > w.width) {
                    e.x = w.width - e.width;
                }
                if (e.y < w.y) {
                    e.y = w.y;
                }
                if (e.y > w.height - e.height) {
                    e.y = w.height - e.height;
                }
            }
        }
    }
}
```

Dans ce code, nous pouvons constater que nous profitons des capacités héritées de `Rectangle2D` ici, pour une première
comparaison afin de détecter si l'instance de notre `Entity` est contenue par l'objet `World`.
Si ce n'est pas le cas, nous repositionnons l'instance `Entity` dans la limite de l'espace de jeu du monde.

![Les limite du monde imposées à une instance d'Entity](https://docs.google.com/drawings/d/e/2PACX-1vQjqb-Ky6hG_zGtFcszvp3bHUp3GyqN-DD6DeM17_c2wmNDRize_2nnOXs_3ckV-c0f0zVxhgviRGgi/pub?w=504&h=351 "Les limite du monde imposées à une instance d'Entity")

_fig. 4 - Les limite du monde imposées à une instance d'Entity_

Nous avons ainsi corrigé la position de notre entité, mais les vitesses sur les deux axes sont toujours actives.
Il est préférable, pour des facilités de calculs, de les ramener à zéro sur l'axe où se produit la collision avec la
zone de jeu:

```java
public class PhysicEngine {
    //...

    public void keepEntityInWorld(World w, Entity e) {
        if (!world.getPlayArea().contains(e)) {
            if (!w.contains(e) || w.intersects(e)) {
                if (e.x < w.x) {
                    e.x = w.x;
                    e.getVelocity().setX(0.0);
                }
                if (e.x + e.width > w.width) {
                    e.x = w.width - e.width;
                    e.getVelocity().setX(0.0);
                }
                if (e.y < w.y) {
                    e.y = w.y;
                    e.getVelocity().setY(0.0);
                }
                if (e.y > w.height - e.height) {
                    e.y = w.height - e.height;
                    e.getVelocity().setY(0.0);
                }
            }
        }
    }
}
```

Voilà un moteur de physique permettant le movement des entités d'une scène dans un espace limité et contrôlé.
Nous pouvons apporter un peu plus de réalisme en introduisant d'autres composantes dans le calcul.

### l'effet Material

Afin de simuler au mieux les comportements de nos objets en movement, nous nous proposons d'ajouter de nouvelles notions
liées à la physique du mouvement, à savoir la friction pour appliquer une resistance sur les délacements en contact avec
une surface, ainsi qu'une elasticité qui permettra de calcul le rebond lors de collision.

La classe `Material` sera notre object de définion des valeurs, et une instance de celle-ci sera ajouté à la classe
`Entity` en tant qu'attribut `material`

```java
public class Material {
    private String name = "default";
    private double density = 1.0;
    private double elasticity = 1.0;
    private double friction = 1.0;

    public Material(String name, double d, double e, double f) {
        this.name = name;
        this.density = d;
        this.elasticity = e;
        this.friction = f;
    }
}
```

Une petite amélioration permettra d'affecter bien plus rapidement un Material: la définition d'une liste de Materiaux
par défaut.

| Name          | Density | Elasticity | Friction |
|---------------|---------|------------|----------|
| Default       | 1.0     | 1.0        | 1.0      |
| Wood          | 1.1     | 0.3        | 0.7      |
| Glass         | 1.3     | 0.5        | 1.0      |
| Ice           | 1.1     | 0.4        | 1.0      |
| Water         | 1.0     | 0.4        | 0.3      |
| Boucning ball | 1.0     | 0.999      | 1.0      |

Matériaux qui seront implémentés par l'intermédiaire de variables finales dans la classe :

```java
public class Material {
    public final Material DEFAULT = new Material("default", 1.0, 1.0, 1.0);
    public final Material BOUNCING_BALL = new Material("default", 1.1, 0.999, 1.0);
    //...
}
```

Occupons-nous maintenant des calculs dans le moteur physique. Nous devons, afin de savoir quand appliquer la friction,
si l'Entity est en contact avec autre chose.
Dans notre premier exemple, le seul contact que nous pouvons détecter est celui avec le bord de la zone de jeux. Aussi,
modifons Entity avec l'ajout d'un flag `contact`
et ajoutons le code nécessaire.

```java
public class Entity extends Rectangle2D {
    //...
    private boolean contact = false;

    //...
    public boolean getContact() {
        return this.contact;
    }

    public Entity setContact(boolean c) {
        this.contact = c;
        return this;
    }
}
```

Appliquons dans un premier temps le facteur d'élasticité afin de calculer la nouvelle vitesse
suite à une collision :

```java
public class PhysicEngine {
    //...

    public void keepEntityInWorld(World w, Entity e) {
        e.setContact(false);
        if (!world.getPlayArea().contains(e)) {
            if (!w.contains(e) || w.intersects(e)) {
                Material m = e.getMaterial();
                if (e.x < w.x) {
                    e.getPosition().setX(0.0);
                    e.getVelocity().setX(e.getVelocity().getX() * -m.getElasticity());
                    e.setContact(true);
                }
                if (e.x + e.width > w.width) {
                    e.getPosition().setX(w.width - e.width);
                    e.getVelocity().setX(e.getVelocity().getX() * -m.getElasticity());
                    e.setContact(true);
                }
                if (e.y < w.y) {
                    e.getPosition().setY(w.y);
                    e.getVelocity().setY(e.getVelocity().getY() * -m.getElasticity());
                    e.setContact(true);
                }
                if (e.y > w.height - e.height) {
                    e.getPosition().setY(w.height - e.height);
                    e.getVelocity().setY(e.getVelocity().getY() * -m.getElasticity());
                    e.setContact(true);
                }
            }
        }
    }
}
```

Ensuite, si le contact est persistant, appliquons le facteur de friction dans le calcul de la vitesse :

```java
public class PhysicEngine {

    public PhysicEngine() {

    }

    public void update(Entity e, elapsed time) {
        // Calculons la somme des forces appliquées pour obtenir l'accélération résultante
        e.setAcceleration(e.getAcceleration().addAll(e.getForces()).divide(e.getMass()));

        // La vélocité et le résultat l'effet de l'accélération en fonction du temps écoulé 
        e.setVelocity(e.getVelocity().add(e.getAcceleration()
                .multiply(time)
                .multiply(e.getContact() ? e.getMaterial().getFriction() : 1.0);

        // la position résultante est calculée en fonction de la vitesse et du temps écoulé.
        e.setPosition(e.getPosition().add(e.getVelocity().multiply(0.5).multiply(time)));

        // on supprime toutes les forces appliquées en attendant le prochain cycle dans la boucle de jeu.
        e.getForces().clear();
    }

}
```

Les autres facteurs issus de la classe Material seront utilisés ultérieurement dans d'autres fonctions.

Mais nous pouvons continuer d'améliorer notre moteur en proposant d'autres possibilités. Nous pouvons ajouter quelques
éléments de simulation comme les effets que sont le vent, le courant de l'eau, le magnétisme.
Nous allons donc ajouter de nouvelles capacités à notre class World pour définir des zones d'interaction dans notre zone
de jeu.

### Les WorldArea

La class World telle qu'elle existe ne définit qu'une chose, la taille de la zone de jeu. Nous allons lui adjoindre ne
nouveaux attributs pour étendre ses effets sur les entitiés d'une scène.

Imaginons une Scene d'automne, où le vent souffle, et l'eau de la rivère est soumise à un fort courant.

Nous allons matérialiser ces zones de vent et de courant dans la classe World à travers la definition de la nouvelle
classe `WorldArea`.

![Définissons une zone de vent et une zone de courant](https://docs.google.com/drawings/d/e/2PACX-1vS30djuxifNQtcIpwzM10mNhWFVtmLuOmsx8TZiZpzFrLqVh-sRZ2U8onN4fLyEOXS12D1vvzeD_O-M/pub?w=429&h=289 "Définissons une zone de vent et une zone de courant")

_fig. 5 - Définissons une zone de vent et une zone de courant_

Nous pouvons maintenant définir ce qu'est une `WorldArea`, une zone d'influence pour toute Entity qui sera contenue par
celle-ci.

Cet objet partage des caractérisques avec l'`Entity` :  une position, une taille, une ou plusieurs forces qui peuvent
lui être appliquées, elle peut aussi contenir un `Material` définissant des attributs physique comme la friction et la
densité, il parait judicieux de la faire hériter de la class `Entity` :

```java
public class WorldArea extends Entity {
    public WorldArea(String name) {
        super(name);
    }
}
```

Si nous mettons en place une mécanique d'héritage en place, les fonctions de fluent interface offerte par `Entity`
deviennent alors problématiques, car la création d'une WorldArea via les setters "fluent" retournera une `Entity` et non
une WorldArea.

Aussi il est nécessaire de modifier un peu notre Entity pour permettre de paramétrer la nature de l'objet de retour des
setters:

```java
// <1>
public class Entity<T> extends Node<T> {
    //...
    List<Point2D> forces = new ArrayList<>();

    //...
    public T setPosition(double x, double y) {
        this.position.setX(x);
        this.position.setY(y);
        super.setRect(x, y, width, height);
        // <2>
        return (T) this;
    }

    public T setPosition(Vector2 p) {
        this.position = p;
        super.setRect(p.x, p.y, width, height);
        // <3>
        return (T) this;
    }
    //...
}
```

Nous pouvons voir que l'object retourné en `<2>` et `<3>` est le parametre T défini en `<1>`.

Notre classe Entity reçoit maintenant un paramètre, la classe cible, permettant une instanciation correcte de nos
`WorkdArea`.

```java
public class WorldArea extends Entity<WorldArea> {
    public WorldArea(String name) {
        super(name);
    }
}
```

#### Modifions l'objet World

Nous allons définir la liste de zones d'influence dans l'object exstant. ajoutons donc une liste à cette effet:

```java
public class World {
    //...
    private List<WorldArea> areas = new ArrayList<>();
    //../

    public World add(WorldArea wa) {
        this.areas.add(wa);
        return this;
    }

    public List<WorldArea> getWorldAreas() {
        return this.areas;
    }
}
```

Nous pouvons maintenant facilement ajouter des zones d'influence sur notre monde lors de la creation de la scene (voir
chapitre précédent pour la Scene) :

```java
public class SceneDemo {
    public void create() {
        World world = new World();
        world.add(
                new WorldArea("water")
                        .setPosition(0, 280)
                        .setSize(320, 40)
                        .addForce(new Vector2(0, 0.2)));
    }
}
```

#### Appliquons les effets

Il est maintenant temps de procéder au calcul des effets de ces zones sur nos Entity dans le moteur physique.

Pour chaque entité de la scene, nous devons vérifier pour chaque zone sir celle-ci est en collision avec l'entité. Si
oui, on applique les forces de ladite zone sur l'entité AVANT de lancer les calculs physique pour l'entité.

```java
public class PhysicEngine {
    //...

    public void update(Scene s, elapsed time) {
        scene.getEntities().values().stream()
                .filter(Entity::isActive)
                .forEach(e -> {
                    // apply World constraints
                    applyWorldConstraints(s.getWorld(), e, time);
                    // apply Physic rules
                    update(e, time);
                    //...
                    keepEntityInWorld(scene.getWorld(), e);
                    // update the position in inherited Rectangle2D from Entity.
                    e.update(time);
                });
    }

    public void applyWorldConstraints(World w, Entity e, double time) {
        w.getWorldAreas().filter(wa -> wa.contains(e) || e.intersects(wa)).forEach(wa -> {
            e.getForces().addAll(wa.getForces());
        });
    }
}
```

Ainsi, lorsque qu'un objet `Entity` pénétrera dans une zone définie par un objet `WorldArea`, toutes les forces décrites
dans celui-ci seront appliquée à l'entité contenue.

![Effets de zone d'influence sur les Entités](https://docs.google.com/drawings/d/e/2PACX-1vTQCHLtU7yDIyKQJYRFcvWZJP-EMApQ0PZeKrxPK5I6iEoHyaD5f_ejpuy1Qv82Z6JzOGvJffnJGBOY/pub?w=429&h=289 "Effets de zone d'influence sur les Entités")

_fig. 6 - Effets de zone d'influence sur les Entités_

Les entités sur l'image ci-dessus subissent les forces comme suit :

- l'entité **E1** est soumise au vent de la WorldArea "**wind**",
- l'entité **E2** est quant à elle soumise à l'influence de l'object WOrldArea "Effets de zone d'influence sur les
  Entités",
- alors que l'objet **E3** est lui soumis à l'influence des 2 zones que sont "**water**" et "**wind**".