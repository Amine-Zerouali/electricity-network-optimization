package algo;

import java.util.List;
import java.util.Random;

import modele.Generateur;
import modele.Maison;
import modele.Reseau;

/**
 * Implémentation de l'algorithme Iterated Local Search (ILS).
 * <p>
 * Cette stratégie alterne entre des phases d'optimisation locale (via l'algorithme Naïf)
 * et de "perturbation" (le Kick). La perturbation déplace aléatoirement des 
 * maisons pour forcer l'algorithme à explorer de nouvelles zones du réseau.
 * </p>
 */
public class AlgoILS implements Algorithme {
	/**
     * Constructeur par défaut.
     */
    public AlgoILS() {
    }
    
	/**
	 * Optimise le réseau par recherche locale itérée avec perturbations.
	 * Alterne entre optimisation locale et perturbations du réseau.
	 * 
	 * @param reseau Le réseau à optimiser.
	 * @param k Le nombre total d'itérations (réparti entre cycles).
	 * @param lambda Le coefficient de pénalité des surcharges.
	 */
	@Override
    public void optimiser(Reseau reseau, int k, double lambda) {
		List<Maison> maisons = reseau.getMaisons();
        
        // Sauvegarde su meilleur réseau trouvé jusque maintenant
        Reseau meilleurGlobal = reseau.copier();
        double coutMeilleurGlobal = reseau.calculerCout(lambda);
        
        // Le nombre de cycles de perturbation + réparation
        int nbCycles = k / 100; 
        
        // On optimise localement
        reseau.optimiserNaif(1000, lambda); // Appel du Naïf existant
        
        for (int i = 0; i < nbCycles; i++) {
            
            // Perturbation du réseau créé (The Kick)
            // On fait X mouvements aléatoires sans regarder le coût
            // Pour sortir du minimum local et d'avoir plus de chances de trouver le minimum global
            int forceDuKick = Math.max(2, maisons.size() / 10); // On fait selon la taille du réseau
            perturberReseau(reseau, forceDuKick); 
            
            // On va essayer de réparer après la perturbation pour peut-être trouvé un nouveau minimum local
            reseau.optimiserNaif(500, lambda); 
            
            double coutActuel = reseau.calculerCout(lambda);
            
            if (coutActuel < coutMeilleurGlobal) {
                coutMeilleurGlobal = coutActuel;
                meilleurGlobal = reseau.copier();
            } else {
            	Reseau copieDeSauvegarde = meilleurGlobal.copier();
                
                reseau.getGenerateurs().clear();
                reseau.getGenerateurs().addAll(copieDeSauvegarde.getGenerateurs());
                
                reseau.getMaisons().clear();
                reseau.getMaisons().addAll(copieDeSauvegarde.getMaisons());
            }
        }
    }

	/**
	 * Perturbe le réseau en déplaçant N maisons aléatoirement (phase du "Kick").
	 * 
	 * @param reseau Le réseau à perturber.
	 * @param nbMouvements Le nombre de déplacements aléatoires à effectuer.
	 */
    private void perturberReseau(Reseau reseau, int nbMouvements) {
    	List<Maison> maisons = reseau.getMaisons();
        List<Generateur> generateurs = reseau.getGenerateurs();
        
        Random rand = new Random();
        for(int i = 0; i < nbMouvements; i++) {
            if(maisons.isEmpty() || generateurs.isEmpty()) {
            	break;
            }
            
            Maison m = maisons.get(rand.nextInt(maisons.size()));
            Generateur g = generateurs.get(rand.nextInt(generateurs.size()));
            
            if(m.getGenerateurAssocie() != null) {
            	m.getGenerateurAssocie().retirerMaison(m);
            }
            
            g.ajouterMaison(m);
            m.setGenerateurAssocie(g);
        }
    }
}
