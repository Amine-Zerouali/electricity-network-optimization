package algo;

import modele.Reseau;

/**
 * Interface définissant le contrat de base pour tous les algorithmes d'optimisation du réseau.
 * <p>
 * Chaque implémentation de cette interface propose une stratégie différente
 * pour réorganiser les connexions entre les maisons et les générateurs afin de minimiser le coût total.
 * </p>
 */
public interface Algorithme {
	/**
     * Exécute la stratégie d'optimisation sur un réseau donné.
     * 
     * @param reseau   L'objet {@link Reseau} à optimiser.
     * @param k        Le nombre d'itérations maximum (pour les algorithmes itératifs).
     * @param lambda   Le coefficient de pénalité appliqué aux surcharges dans le calcul du coût.
     */
	void optimiser(Reseau reseau, int k, double lambda);
}
