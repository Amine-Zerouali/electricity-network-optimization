package algo;

import java.util.List;
import java.util.Random;

import modele.Generateur;
import modele.Maison;
import modele.Reseau;

/**
 * Implémentation de l'algorithme Naïf (Hill Climbing).
 * <p>
 * Cette stratégie va effectuer des modifications aléatoires et ne les conserve que si
 * elles améliorent le coût global du réseau. C'est une méthode très rapide
 * mais reste bloquée dans des minimums locaux.
 * </p>
 */
public class AlgoNaif implements Algorithme{
	/**
     * Constructeur par défaut.
     */
    public AlgoNaif() {
    }
	
	/**
	 * Optimise le réseau par descente de gradient stochastique.
	 * Accepte uniquement les solutions améliorantes.
	 * 
	 * @param reseau Le réseau à optimiser.
	 * @param k Le nombre de tentatives de déplacement.
	 * @param lambda Le coefficient de pénalité des surcharges.
	 */
	@Override
    public void optimiser(Reseau reseau, int k, double lambda) {
		List<Maison> maisons = reseau.getMaisons();
        List<Generateur> generateurs = reseau.getGenerateurs();
        
        if (maisons.isEmpty() || generateurs.isEmpty()){
        	return;
        }

        Random rand = new Random();
        double coutActuel = reseau.calculerCout(lambda);

        for (int i = 0; i < k; i++) {
            // Tirage aléatoire
            Maison m = maisons.get(rand.nextInt(maisons.size()));
            Generateur gNouveau = generateurs.get(rand.nextInt(generateurs.size()));
            Generateur gAncien = m.getGenerateurAssocie();

            // Si on tombe sur le même générateur, on passe
            if (gAncien == gNouveau || gAncien == null) {
            	continue;
            }

            // Application du mouvement
            gAncien.retirerMaison(m);
            gNouveau.ajouterMaison(m);
            m.setGenerateurAssocie(gNouveau);

            double coutNouveau = reseau.calculerCout(lambda);

            // Vérification de si c'est mieux
            if (coutNouveau < coutActuel) {
                // Si oui, on garde le changement
                coutActuel = coutNouveau;
            } else {
                // Si non, on annule
                gNouveau.retirerMaison(m);
                gAncien.ajouterMaison(m);
                m.setGenerateurAssocie(gAncien);
            }
        }
    }
}
