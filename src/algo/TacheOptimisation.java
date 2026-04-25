package algo;
import modele.Reseau;

/**
 * Représente une unité de travail d'optimisation destinée à être exécutée dans un thread séparé.
 * <p>
 * Cette classe implémente {@link Runnable}, permettant ainsi de lancer plusieurs algorithmes
 * simultanément sans bloquer l'interface utilisateur. Elle travaille sur une copie indépendante
 * du réseau pour garantir l'absence d'effets de bord entre les threads.
 * </p>
 */
public class TacheOptimisation implements Runnable {
	/** La copie de travail du réseau sur laquelle l'algorithme va s'exécuter. */
    private Reseau reseauCopie;
    /** Le nombre d'itérations pour les algorithmes itératifs. */
    private int k;
    /** Le coefficient de pénalité pour le calcul du coût. */
    private double lambda;
    /** Le type d'algorithme à utiliser pour cette tâche spécifique. */
    private TypeAlgo typeAlgo;
    
    /**
     * Construit une nouvelle tâche d'optimisation.
     * 
     * @param reseau   La copie du réseau à optimiser.
     * @param k        Le nombre d'itérations maximum.
     * @param lambda   Le coefficient lambda.
     * @param typeAlgo La stratégie d'algorithme à appliquer ({@link TypeAlgo}).
     */
    public TacheOptimisation(Reseau reseau, int k, double lambda, TypeAlgo typeAlgo) {
        this.reseauCopie = reseau;
        this.k = k;
        this.lambda = lambda;
        this.typeAlgo = typeAlgo;
    }
    
    /**
     * Exécute l'algorithme d'optimisation selon le type défini.
     */
    @Override
    public void run() {
        switch (typeAlgo) {
            case RECUIT:
            	reseauCopie.optimiserRecuitSimule(k, lambda);
                break;
            case NAIF:
            	reseauCopie.optimiserNaif(k, lambda);
                break;
            case GLOUTON:
            	reseauCopie.optimiserReseauGlouton(lambda);
                break;
            case ILS:
            	reseauCopie.optimiserILS(k, lambda);
                break;
            case LAHC:
            	reseauCopie.optimiserLAHC(k, lambda);
                break;
        }
    }

    /**
     * Récupère le réseau après l'exécution de l'algorithme.
     *
     * @return L'objet {@link Reseau} après optimisation.
     */
    public Reseau getReseauResultat() {
        return reseauCopie;
    }
    
    /**
     * Retourne le type d'algorithme utilisé par cette tâche.
     * 
     * @return La valeur {@link TypeAlgo} associée.
     */
    public TypeAlgo getTypeAlgo() {
        return this.typeAlgo;
    }
}