package modele;

import io.ChargerReseau;

/**
 * Exception personnalisée pour gérer les erreurs spécifiques au réseau électrique.
 * <p>
 * Elle est principalement levée par {@link ChargerReseau} lors de la lecture
 * d'un fichier pour signaler :
 * <ul>
 * <li>Des erreurs de syntaxe (format incorrect, point manquant...).</li>
 * <li>Des erreurs de logique (doublons de noms, référence à un objet inexistant...).</li>
 * </ul>
 * Elle permet d'associer un numéro de ligne à l'erreur pour faciliter la correction par l'utilisateur.
 */
public class ReseauException extends Exception {
	/** Identifiant de version pour la sérialisation. */
	private static final long serialVersionUID = 1L;
	
	/**
     * Construit une nouvelle exception avec le message d'erreur spécifié.
     *
     * @param message La description de l'erreur.
     */
    public ReseauException(String message) {
        super(message);
    }
    
    /**
     * Construit une nouvelle exception en incluant le numéro de ligne où l'erreur est survenue.
     * <p>
     * Le message final sera automatiquement formaté sous la forme :
     * {@code "Erreur ligne [X] : [message]"}
     *
     * @param message La description de l'erreur.
     * @param ligne   Le numéro de la ligne dans le fichier texte où l'erreur a été détectée.
     */
    public ReseauException(String message, int ligne) {
        super("Erreur ligne " + ligne + " : " + message);
    }
}