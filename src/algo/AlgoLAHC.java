package algo;

import java.util.List;
import java.util.Random;

import modele.Generateur;
import modele.Maison;
import modele.Reseau;

/**
 * Implémentation de l'algorithme Late Acceptance Hill Climbing (LAHC).
 * <p>
 * Variante de l'algorithme Naïf qui va utiliser un historique des coûts passés.
 * Une solution est acceptée si elle est meilleure que le coût 
 * enregistré il y a un certain nombre d'itérations, permettant une convergence
 * plus robuste.
 * </p>
 */
public class AlgoLAHC implements Algorithme{
	/**
     * Constructeur par défaut.
     */
    public AlgoLAHC() {
    }
	
	/**
	 * Optimise le réseau en acceptant les solutions meilleures que l'historique récent.
	 * 
	 * @param reseau Le réseau à optimiser.
	 * @param k Le nombre d'itérations à effectuer.
	 * @param lambda Le coefficient de pénalité des surcharges.
	 */
	@Override
    public void optimiser(Reseau reseau, int k, double lambda) {
		List<Maison> maisons = reseau.getMaisons();
        List<Generateur> generateurs = reseau.getGenerateurs();
        
        // Longueur de l'historique
        // Plus c'est grand, plus l'algo explore. Plus c'est petit, plus il converge vite.
        int L = 500; 
        
        double[] historiqueCouts = new double[L];
        double coutActuel = reseau.calculerCout(lambda);
        
        // On remplit l'historique avec le coût initial
        for(int i = 0; i < L; i++) {
        	historiqueCouts[i] = coutActuel;
        }
        
        Random rand = new Random();
        
        for (int i = 0; i < k; i++) {
            Maison m = maisons.get(rand.nextInt(maisons.size()));
            Generateur gDest = generateurs.get(rand.nextInt(generateurs.size()));
            Generateur gSource = m.getGenerateurAssocie();
            
            if (gSource == gDest || gSource == null) {
            	continue;
            }
            
            gSource.retirerMaison(m);
            gDest.ajouterMaison(m);
            m.setGenerateurAssocie(gDest);
            
            double coutNouveau = reseau.calculerCout(lambda);
            
            // On récupère le coût qu'on avait il y a L tours
            int indexHistorique = i % L;
            double coutReference = historiqueCouts[indexHistorique];
            
            // Condition d'acceptation :
            // Est-ce que c'est mieux que maintenant ou il y a L tours ?
            if (coutNouveau <= coutReference || coutNouveau <= coutActuel) {
                // On accepte la solution
                coutActuel = coutNouveau;
                // On met à jour l'historique avec le nouveau coût qui a été accepté
                historiqueCouts[indexHistorique] = coutActuel;
            } else {
                // On refuse cette solution
                gDest.retirerMaison(m);
                gSource.ajouterMaison(m);
                m.setGenerateurAssocie(gSource);
                // On doit quand même à jour l'historique avec le coût actuel (qui n'aura pas changé du coup)
                historiqueCouts[indexHistorique] = coutActuel;
            }
        }
    }
}
