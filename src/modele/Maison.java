package modele;
 /**
 * Représente un consommateur d'électricité (maison) dans le réseau.
 * <p>
 * Chaque maison a un nom, un type de consommation (BASSE, NORMAL, FORTE)
 * et maintient une référence unique vers le {@link Generateur}
 * auquel elle est connectée.
 */
public class Maison {
	/** Le nom unique de la maison (ex: "M1"). */
	private String nom;
	/** Le type de consommation (BASSE, NORMAL, FORTE). */
    private TypeMaison type;
    /** Référence vers le générateur qui alimente cette maison.
     * Sa valeur est {@code null} si la maison n'est connectée à aucun générateur.
     */
    private Generateur generateurAssocie;
    
    /**
     * Construit une nouvelle maison.
     * Par défaut, la maison n'est connectée à aucun générateur.
     *
     * @param nom Le nom de la maison.
     * @param type Le type de consommation (BASSE, NORMAL, ou FORTE).
     */
    public Maison(String nom, TypeMaison type) {
        this.nom = nom;
        this.type = type;
        this.generateurAssocie = null; // Non connectée à la création
    }
    
    /**
     * Constructeur de copie.
     * <p>
     * Crée une nouvelle instance de maison avec les mêmes caractéristiques fixes que la maison passée en paramètre.
     * <p>
     * La nouvelle maison est initialisée comme non connectée (le générateur associé est forcé à {@code null}), même si la maison source était connectée.
     * <p>
     * Cela permet à la méthode {@link Reseau#copier()} de reconstruire proprement
     * les liens entre les nouveaux objets, sans pointer vers les anciens générateurs.
     *
     * @param autre La maison source à copier.
     */
    public Maison(Maison autre) {
        this.nom = autre.nom;
        this.type = autre.type;
        this.generateurAssocie = null; // On reconnectera plus tard
    }

    
    // --- GETTERS ---
    
    /**
	 * Récupère le nom de cette maison.
	 *
	 * @return Le nom de la maison.
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * Récupère le type de consommation de cette maison.
	 *
	 * @return L'objet {@link TypeMaison} associé à cette maison.
	 */
	public TypeMaison getType() {
		return type;
	}

	/**
	 * Récupère le générateur auquel cette maison est connectée.
	 *
	 * @return Le {@link Generateur} auquel la maison est associée, 
     * ou {@code null} si elle n'est pas connectée.
	 */
	public Generateur getGenerateurAssocie() {
		return generateurAssocie;
	}
	
	/**
	 * Calcule et retourne la consommation électrique de la maison en kW.
	 * Cette valeur est déterminée par son {@link TypeMaison}.
	 *
	 * @return La consommation en kW (ex: 10.0, 20.0 ou 40.0).
	 */
	public double getConsommation() {
		return type.getConsommation();
	}
	
	
	// --- SETTERS ---

	/**
	 * Met à jour le type de consommation de la maison.
     * <p>
     * Note: Cette méthode est utilisée par {@code Reseau.ajouterMaison}
     * pour mettre à jour une maison existante.
     *
	 * @param type Le nouveau {@link TypeMaison} pour cette maison.
	 */
	public void setType(TypeMaison type) {
		this.type = type;
	}

	/**
	 * Assigne un générateur à cette maison.
     * <p>
     * Note: Cette méthode est le "côté maison" de la connexion,
     * gérée par les méthodes {@code connecter} et {@code modifierConnexion} 
     * de la classe {@link Reseau}.
     *
	 * @param generateurAssocie Le {@link Generateur} à associer.
	 */
	public void setGenerateurAssocie(Generateur generateurAssocie) {
		this.generateurAssocie = generateurAssocie;
	}
	
	
	/**
	 * Fournit une représentation textuelle de l'état de la maison.
     * Utile principalement pour le débogage.
     *
	 * @return Une chaîne de caractères décrivant la maison et sa connexion.
	 */
	@Override
	public String toString() {
        String g = (generateurAssocie == null) ? "non connectée" : generateurAssocie.getNom();
        return String.format("Maison : %s (Type : %s | Consommation : %.0f kW) --> Générateur : %s", nom, type, getConsommation(), g);
    }
}
