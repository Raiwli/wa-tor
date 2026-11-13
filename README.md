## Classes

- `model.Position` représente les coordonnées d'une case.
- `model.Animal` porte la position et le compteur de reproduction communs, et définit le contrat polymorphe d'action.
- `model.Tuna` implémente le déplacement et la reproduction des thons.
- `model.Shark` implémente la chasse, le déplacement, la reproduction et la famine des requins.
- `model.World` possède la grille, initialise les populations, fournit les voisins avec raccordement des bords et orchestre les tours.
- `view.WaTorApp` construit la fenêtre JavaFX, dessine la mer et le graphique, affiche les statistiques et gère les commandes clavier.

## Choix de conception

Le modèle ne dépend pas de JavaFX et ne référence aucune classe de la vue. Chaque animal porte son comportement dans `act`, appelé par le monde à partir d'une copie mélangée des animaux présents au début du tour : les nouveaux-nés attendent le tour suivant et un animal mangé ne peut pas agir. La grille est l'unique source de vérité pour l'occupation des cases ; les requêtes de populations utilisent les contributions polymorphes des animaux, et la chasse s'appuie sur leur réponse à l'interaction avec un requin. La vue ne fait qu'interroger le modèle et rendre son état.
