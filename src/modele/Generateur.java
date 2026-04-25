package modele;
import java.util.ArrayList;
import java.util.List;

 /**
 * Représente une source de production d'électricité (générateur) dans le réseau.
 * Cette classe gère sa capacité maximale (Cg), la liste des maisons qu'elle alimente,
 * et maintient un cache de sa charge actuelle (Lg) pour des calculs de coût efficaces.
 */
public class Generateur {
	/** Le nom unique du générateur (ex: "G1"). */
    private String nom;
    /** La capacité de production maximale en kW (Cg). */
    private double capaciteMax;
    /** La charge actuelle consommée en kW (Lg).
     * C'est une valeur mise en cache, mise à jour par ajouterMaison() et retirerMaison().
     */
    private double chargeActuelle;
    /** La liste des objets Maison que ce générateur alimente. */
    private List<Maison> maisonsConnectees;

    /**
     * Construit un nouveau générateur.
     * La charge actuelle est initialisée à 0 et la liste des maisons est vide.
     * @param nom Le nom du générateur.
     * @param capaciteMax La capacité de production maximale en kW.
     */
    public Generateur(String nom, double capaciteMax) {
        this.nom = nom;
        this.capaciteMax = capaciteMax;
        this.maisonsConnectees = new ArrayList<>();
    }
    
    /**
     * Constructeur de copie.
     * <p>
     * Crée un nouveau générateur avec les mêmes caractéristiques fixes que le générateur passé en paramètre.
     * <p>
     * Ce constructeur ne copie pas les connexions existantes.
     * Le nouveau générateur est initialisé "à vide" :
     * <ul>
     * <li>La charge actuelle est réinitialisée à 0.</li>
     * <li>La liste des maisons connectées est initialisée vide.</li>
     * </ul>
     * Ce comportement est conçu pour permettre une reconstruction propre des liens lors de la copie profonde du réseau.
     *
     * @param autre Le générateur modèle à copier.
     */
    public Generateur(Generateur autre) {
        this.nom = autre.nom;
        this.capaciteMax = autre.capaciteMax;
        this.chargeActuelle = 0;
        this.maisonsConnectees = new ArrayList<>();
    }

    /**
     * Connecte une maison à ce générateur.
     * <p>
     * Ajoute la maison à la liste {@code maisonsConnectees} et met à jour le cache
     * {@code chargeActuelle} en ajoutant la consommation de la maison.
     * Ne fait rien si la maison est déjà dans la liste.
     * @param m La maison à connecter.
     */
    public void ajouterMaison(Maison m) {
        if (!maisonsConnectees.contains(m)) {
            maisonsConnectees.add(m);
            // Met à jour la charge
            chargeActuelle += m.getConsommation();
        }
    }

    /**
     * Déconnecte une maison de ce générateur.
     * <p>
     * Retire la maison de la liste {@code maisonsConnectees} et met à jour le cache
     * {@code chargeActuelle} en soustrayant la consommation de la maison.
     * Ne fait rien si la maison n'était pas connectée à ce générateur.
     * @param m La maison à déconnecter.
     */
    public void retirerMaison(Maison m) {
    	// .remove() retourne true si l'élément était présent
    	if (maisonsConnectees.remove(m))
    		// Met à jour la charge
    	    chargeActuelle -= m.getConsommation();
    }
    
    /**
     * Calcule le taux d'utilisation de ce générateur (ug = Lg / Cg).
     * 
     * @return Le taux d'utilisation.
     * Retourne 0 si la capacité maximale est 0.
     */
    public double getTauxUtilisation() {
    	// Utilisation d'un opérateur ternaire pour éviter la division par zéro
    	return capaciteMax == 0 ? 0 : chargeActuelle / capaciteMax;
    }

    
    // --- GETTERS ---
    
    /**
     * Retourne le nom de ce générateur.
     *
     * @return Le nom du générateur.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Récupère la capacité maximale de ce générateur.
     *
     * @return La capacité maximale en kW (Cg).
     */
    public double getCapaciteMax() {
        return capaciteMax;
    }

    /**
     * Récupère la liste des maisons actuellement connectées à ce générateur.
     *
     * @return La liste des objets {@link Maison}.
     */
    public List<Maison> getMaisonsConnectees() {
        return maisonsConnectees;
    }
    
    /**
     * Récupère la charge actuelle (en cache) de ce générateur.
     *
     * @return La charge actuelle en kW (Lg), basée sur le cache.
     */
    public double getChargeActuelle() {
        return chargeActuelle;
    }

    
    // --- SETTERS ---
    
    /**
     * Met à jour la capacité maximale du générateur.
     * <p>
     * Note: Cette méthode est utilisée par {@code Reseau.ajouterGenerateur}
     * pour mettre à jour un générateur existant.
     * @param capaciteMax La nouvelle capacité maximale en kW.
     */
    public void setCapaciteMax(double capaciteMax) {
        this.capaciteMax = capaciteMax;
    }

    
    /**
     * Fournit une représentation textuelle de l'état du générateur.
     * Utile principalement pour le débogage.
     * @return Une chaîne de caractères décrivant le générateur et les maisons connectées.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("Générateur : %s (Capacité : %.2f kW) | Charge actuelle: %.2f kW | Taux: %.4f%n", nom, capaciteMax, getChargeActuelle(), getTauxUtilisation()));
        if (maisonsConnectees.isEmpty()) {
            sb.append(" | Aucune maison connectée.\n");
        } else {
            sb.append("\nMaisons connectées : ");
            for (int i = 0; i < maisonsConnectees.size(); i++) {
                sb.append(maisonsConnectees.get(i).getNom());
                if (i < maisonsConnectees.size() - 1)
                	sb.append(", ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
