# Ajoutons un peu de Physique

## Contexte

Faisons en sorte que nos objets soient animés de façon plus réaliste.
Nous allons rendre un peu plus vivant notre scène en ajoutant quelques éléments de la physique du mouvement dont Newton
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

Si maintenant, nous souhaitons modéliser nos objets animés, nous devons créer un certain nombre d'attributs permettant
de représenter ces vecteurs et forces, ainsi que quelques attributs permettant d'identifier facilement les objets.

Voici une première proposition :

![Entity UML model](https://www.plantuml.com/plantuml/png/PSwn2W8n383XlKyH7y35iOZSukOck9SqUe4saIQASjoxsqCHn2tv-0aPp5FpMorXvIDLWggY0KioWxqu-nEc06kOUkUCCx1aUiIYSbcOytUKL2aUlV5xlQgniqg44w5hsonufwBOR_vWGgH2BVtLPsr85WzilltmBinXv4nGoKVDOI39_VaN "Entity UML model")

_fig. 2 - le modèle UML pour la class Entity_

Ce qui se traduira par le code suivant :

```java
public class Entity {
    private static long index = 0;
    private long id = index++;
    private String name = "entity_" + id;

    Vector2 position = new Vector2();
    Vector2 velocity = new Vector2();
    Vector2 acceleration = new Vector2();
    List<Vector2> forces = new ArrayList<>();

    public Entity(String name) {
        this.name = name;
    }
}
```

Notre class doit égelement proposer quelques accesseurs pour definir les différentes valeurs des attributs :


