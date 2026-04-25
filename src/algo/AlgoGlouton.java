package algo;

import java.util.ArrayList;
import java.util.List;

import modele.Generateur;
import modele.Maison;
import modele.Reseau;

/**
 * Implémentation de l'algorithme Glouton (Greedy Algorithm).
 * <p>
 * Cet algorithme est constructif : il déconnecte tout le réseau
 * et reconnecte chaque maison une par une au meilleur générateur disponible à l'instant T.
 * Il va traiter les maisons par ordre de consommation décroissante pour s'assurer d'une 
 * répartition efficace dès le début.
 * </p>
 */
public class AlgoGlouton implements Algorithme{
	/**
     * Constructeur par défaut.
     */
    public AlgoGlouton() {
    }
	
	/**
	 * Optimise le réseau en reconnectant chaque maison au générateur minimisant le coût.
	 * Les maisons sont traitées par ordre décroissant de consommation.
	 * 
	 * @param reseau Le réseau à optimiser.
	 * @param k Non utilisé (algorithme constructif).
	 * @param lambda Le coefficient de pénalité des surcharges.
	 */
	@Override
    public void optimiser(Reseau reseau, int k, double lambda) {
		List<Maison> maisons = reseau.getMaisons();
        List<Generateur> generateurs = reseau.getGenerateurs();
		
        // On déconnecte toutes les maisons pour repartir de zéro
        for (Maison m : maisons) {
            if (m.getGenerateurAssocie() != null) {
                m.getGenerateurAssocie().retirerMaison(m);
                m.setGenerateurAssocie(null);
            }
        }

        // On trie les maisons par consommation décroissante
        List<Maison> maisonsTriees = new ArrayList<>(maisons);
        maisonsTriees.sort((m1, m2) -> Double.compare(m2.getConsommation(), m1.getConsommation()));

        // Pour chaque maison, on cherche le Meilleur Générateur (Best Fit)
        for (Maison m : maisonsTriees) {
            Generateur meilleurGen = null;
            double meilleurCoutAjout = Double.MAX_VALUE;

            for (Generateur g : generateurs) {
                // On teste virtuellement l'ajout
                g.ajouterMaison(m);
                // On regarde combien ça coûte
                double cout = reseau.calculerCout(lambda);
                
                // Si c'est le meilleur choix jusqu'ici, on le retient
                if (cout < meilleurCoutAjout) {
                    meilleurCoutAjout = cout;
                    meilleurGen = g;
                }
                
                // On retire pour tester le suivant
                g.retirerMaison(m);
            }

            // On valide le meilleur choix pour cette maison
            if (meilleurGen != null) {
                meilleurGen.ajouterMaison(m);
                m.setGenerateurAssocie(meilleurGen);
            }
        }
    }
}
