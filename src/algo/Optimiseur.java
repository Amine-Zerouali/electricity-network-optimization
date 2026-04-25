package algo;
import java.util.ArrayList;
import java.util.List;

import modele.Reseau;

/**
 * Classe utilitaire pour gérer l'exécution multi-thread de plusieurs algorithmes d'optimisation.
 * <p>
 * Son rôle est de déterminer parmi les différentes stratégies, celle qui produit le meilleur résultat pour
 * une configuration donnée.
 */
public class Optimiseur {
	/**
     * Constructeur privé pour empêcher l'instanciation de cette classe utilitaire.
     */
    private Optimiseur() {
        // Empêche l'instanciation
    }
	
	/**
     * Lance les optimisations en parallèle et retourne la meilleure solution trouvée.
     * <p>
     * Le fonctionnement est le suivant :
     * <ol>
     * <li>Toutes les stratégies définies dans {@link TypeAlgo} sont exécutées en parallèle.</li>
     * <li>Pour chaque stratégie, un nouveau {@link Thread} est créé, associé à une {@link TacheOptimisation}.</li>
     * <li>Tous les threads sont démarrés simultanément via {@code start()}.</li>
     * <li>Le thread principal attend la fin de l'exécution de tous les threads via la méthode {@code join()}.</li>
     * <li>Une fois tous les calculs terminés, les coûts sont comparés et le meilleur réseau est renvoyé.</li>
     * </ol>
     *
     * @param reseauOriginal Le réseau initial à optimiser.
     * @param k              Le nombre d'itérations pour les algorithmes itératifs.
     * @param lambda         Le coefficient de pénalité pour le calcul du coût.
     * @return L'objet {@link Reseau} correspondant à la meilleure solution trouvée parmi tous les concurrents.
     */
    public static Reseau lancerAlgos(Reseau reseauOriginal, int k, double lambda) {
    	
    	double coutInitial = reseauOriginal.calculerCout(lambda);

        List<TacheOptimisation> taches = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        // Création et démarrage des threads
        for (TypeAlgo type : TypeAlgo.values()) {
            TacheOptimisation tache = new TacheOptimisation(reseauOriginal.copier(), k, lambda, type);
            Thread t = new Thread(tache, "Thread-" + type);
            
            taches.add(tache);
            threads.add(t);
            
            t.start();
        }

        // Attente de la fin de tous les threads
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        
        // Recherche du meilleur réseau
        Reseau meilleurReseauTrouve = null;
        double meilleurCoutTrouve = Double.MAX_VALUE;

        for (int i = 0; i < taches.size(); i++) {
            Reseau r = taches.get(i).getReseauResultat();
            double cout = r.calculerCout(lambda);

            if (cout < meilleurCoutTrouve) {
            	meilleurCoutTrouve = cout;
            	meilleurReseauTrouve = r;
            }
        }
        
        if (meilleurReseauTrouve != null && meilleurCoutTrouve < coutInitial) {
            return meilleurReseauTrouve;
        } else {
            return null;
        }
    }
}