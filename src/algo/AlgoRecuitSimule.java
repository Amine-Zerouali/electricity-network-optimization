package algo;

import java.util.List;
import java.util.Random;

import modele.Generateur;
import modele.Maison;
import modele.Reseau;

/**
 * Implémentation de l'algorithme du Recuit Simulé (Simulated Annealing).
 * <p>
 * Algorithme méta-heuristique qui accepte parfois des solutions moins bonnes pour explorer
 * le champ des possibles et éviter les minimums locaux.
 * La probabilité d'accepter une mauvaise solution diminue avec le temps (la "température").
 * </p>
 */
public class AlgoRecuitSimule implements Algorithme{
	/**
     * Constructeur par défaut.
     */
    public AlgoRecuitSimule() {
    }
	
	/**
	 * Optimise le réseau en acceptant parfois des solutions dégradantes.
	 * La probabilité d'acceptation décroît avec la température.
	 * 
	 * @param reseau Le réseau à optimiser.
	 * @param k Le nombre d'itérations à effectuer.
	 * @param lambda Le coefficient de pénalité des surcharges.
	 */
	@Override
    public void optimiser(Reseau reseau, int k, double lambda) {
		List<Maison> maisons = reseau.getMaisons();
        List<Generateur> generateurs = reseau.getGenerateurs();
        
        if (maisons.isEmpty() || generateurs.isEmpty()) {
        	return;
        }

        Random rand = new Random();
        double coutActuel = reseau.calculerCout(lambda);
        
        // Paramètres du recuit
        double temperature = 1000.0; // Température initiale
        double refroidissement = 0.99; // Vitesse de baisse de température

        for (int i = 0; i < k; i++) {
            // Tirage aléatoire
            Maison m = maisons.get(rand.nextInt(maisons.size()));
            Generateur gNouveau = generateurs.get(rand.nextInt(generateurs.size()));
            Generateur gAncien = m.getGenerateurAssocie();

            if (gAncien == gNouveau || gAncien == null) {
            	continue;
            }

            // Application du mouvement
            gAncien.retirerMaison(m);
            gNouveau.ajouterMaison(m);
            m.setGenerateurAssocie(gNouveau);

            double coutNouveau = reseau.calculerCout(lambda);
            double delta = coutNouveau - coutActuel;

            // Logique du Recuit Simulé
            // Condition d'acceptation :
            // - Soit c'est mieux (delta < 0)
            // - Soit c'est pire, mais on a de la "chance" (probabilité liée à la température)
            if (delta < 0 || Math.exp(-delta / temperature) > rand.nextDouble()) {
                // On accepte le changement
                coutActuel = coutNouveau;
            } else {
                // On refuse et on annule
                gNouveau.retirerMaison(m);
                gAncien.ajouterMaison(m);
                m.setGenerateurAssocie(gAncien);
            }

            // On refroidit le système à chaque tour
            temperature *= refroidissement;
        }
    }
}
