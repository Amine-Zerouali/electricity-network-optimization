package algo;

import modele.Reseau;

/**
 * Énumération listant les différentes stratégies d'optimisation implémentées.
 * <p>
 * Cette énumération est utilisée par la classe {@link Optimiseur} et {@link TacheOptimisation} pour sélectionner dynamiquement l'algorithme à exécuter.
 * <p>
 * Pour ajouter un nouvel algorithme :
 * <ol>
 * <li>Ajouter une constante ici.</li>
 * <li>Implémenter la logique dans {@link Reseau}.</li>
 * <li>Ajouter le cas dans le switch de {@link TacheOptimisation}.</li>
 * </ol>
 */
public enum TypeAlgo {
	/** Algorithme de descente simple (Hill Climbing). */
    NAIF,
    /** Algorithme constructif basé sur le tri des consommations. */
    GLOUTON,
    /** Algorithme probabiliste inspiré de la métallurgie. */
    RECUIT,
    /** Algorithme de recherche locale itérée avec perturbation (Kick). */
    ILS,
    /** Algorithme de recherche avec acceptation tardive des solutions. */
    LAHC
}