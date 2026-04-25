package modele;
 /**
 * Représente les catégories de consommation électrique d'une maison.
 * Chaque type (BASSE, NORMAL, FORTE) est associé à une valeur de consommation fixe en kW.
 */
public enum TypeMaison {
	/** Consommation basse, fixée à 10 kW. */
    BASSE(10),
    /** Consommation normale, fixée à 20 kW. */
    NORMAL(20),
    /** Consommation forte, fixée à 40 kW. */
    FORTE(40);
	
	/** La valeur de la consommation en kW. */
    private final double consommation;
    
    /**
     * Constructeur privé pour l'énumération.
     * @param consommation La consommation électrique en kW associée à ce type.
     */
    private TypeMaison(double consommation) {
        this.consommation = consommation;
    }

    /**
     * Retourne la valeur de la consommation électrique de ce type de maison.
     * @return La consommation en kW (10, 20 ou 40).
     */
    public double getConsommation() {
        return consommation;
    }
}
