package io;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import modele.Generateur;
import modele.Maison;
import modele.Reseau;
import modele.ReseauException;
import modele.TypeMaison;


/**
 * Classe utilitaire responsable du chargement et de l'analyse des fichiers de configuration.
 * <p>
 * Elle lit un fichier texte ligne par ligne pour construire l'objet {@link Reseau}.
 * Le format du fichier doit respecter strictement la syntaxe suivante :
 * <ul>
 * <li>Chaque instruction doit se terminer par un point ('.').</li>
 * <li>Les instructions doivent apparaître dans l'ordre : générateurs, puis maisons, puis connexions.</li>
 * <li>Formats attendus :
 *     <ul>
 *     <li>{@code generateur(nom,capacité).}</li>
 *     <li>{@code maison(nom,TYPE).}</li>
 *     <li>{@code connexion(nom1,nom2).}</li>
 *     </ul>
 * </li>
 * </ul>
 * La classe gère également la vérification des contraintes (unicité des noms, existence des objets connectés, ...).
 */
public class ChargerReseau {
	// Constantes afin de définir l'ordre qui est attendu dans la lecture du fichier
	/** Etape de la création des générateurs. */
	private static final int ETAPE_GENERATEUR = 0;
	/** Etape de la création des maisons. */
	private static final int ETAPE_MAISON = 1;
	/** Etape de la liaison entre générateurs et maisons. */
	private static final int ETAPE_CONNEXION = 2;
	
	/**
     * Constructeur privé pour empêcher l'instanciation de cette classe utilitaire.
     * <p>
     * Comme cette classe ne contient que des méthodes statiques,
     * elle n'est pas conçue pour être instanciée.
     */
    private ChargerReseau() {
        // Empêche l'instanciation
    }
	
	/**
     * Charge un réseau électrique à partir d'un fichier texte.
     * <p>
     * Cette méthode ouvre le fichier, lit chaque ligne, vérifie la syntaxe, et délègue l'analyse à des méthodes spécifiques selon le mot-clé rencontré.
     * <p>
     * À la fin du chargement, une vérification globale de la validité du réseau est effectuée via {@link Reseau#verificationReseau()}.
     *
     * @param cheminFichier Le chemin absolu ou relatif du fichier à lire.
     * @param reseau        L'objet {@link Reseau} vide (ou existant) à remplir.
     * @throws IOException      En cas de problème de lecture du fichier (fichier introuvable, erreur disque...).
     * @throws ReseauException  En cas d'erreur de syntaxe ou de sémantique dans le fichier (ligne mal formée, doublon, type inconnu, ordre incorrect...).
     * L'exception contient le numéro de la ligne fautive.
     */
    public static void charger(String cheminFichier, Reseau reseau) throws IOException, ReseauException{
        try(BufferedReader br = new BufferedReader(new FileReader(cheminFichier))){
            String ligne = null;
            int nline = 0;
            int etape = ETAPE_GENERATEUR;
            
            while((ligne = br.readLine()) != null) {
            	nline++;
            	ligne = ligne.trim();
            	
            	if (ligne.isEmpty()) continue;
            	
            	if(!ligne.endsWith(".")) {
            		throw new ReseauException("La ligne doit terminer par un point", nline); //Peut être +1 les nline, je verrai
            	}
            	
            	
            	ligne = ligne.substring(0, ligne.length() - 1);
            	
            	
            	if(ligne.startsWith("generateur")) {
            		if(etape > ETAPE_GENERATEUR) {
            			throw new ReseauException("Les générateurs doivent tous être définis en premier", nline);
            		}
            		etape = ETAPE_GENERATEUR;
            		parserGenerateur(reseau, ligne, nline);
            	}else if(ligne.startsWith("maison")) {
            		if(etape == ETAPE_GENERATEUR) {
            			etape = ETAPE_MAISON;
            		}
            		if(etape > ETAPE_MAISON) {
            			throw new ReseauException("Les maisons doivent tous être définis en premier", nline);
            		}
            		parserMaison(reseau, ligne, nline);
            	}else if(ligne.startsWith("connexion")) {
            		if(etape < ETAPE_CONNEXION) {
            			etape = ETAPE_CONNEXION;
            		}
            		parserConnexion(reseau, ligne, nline);
            	}else {
            		throw new ReseauException("Erreur, Entrée incorrect, doit commencer par generateur, maison ou connexion", nline);
            	}
            }
            
            reseau.verificationReseau();
        }
    }
    
    /**
     * Analyse une ligne de déclaration de générateur.
     * Format attendu : {@code generateur(nom, capacité)}
     *
     * @param reseau Le réseau à mettre à jour.
     * @param ligne  La ligne de texte à analyser (sans le point final).
     * @param nline  Le numéro de la ligne (en cas d'erreur).
     * @throws ReseauException Si le format est incorrect, si le nom est invalide/doublon ou si la capacité n'est pas un nombre.
     */
    public static void parserGenerateur(Reseau reseau, String ligne, int nline) throws ReseauException{
    	String[] args = splitString(ligne, nline, "generateur");
    	if(args.length != 2) {
    		throw new ReseauException("generateur attend 2 arguments (nom, capacité)", nline);
    	}
    	
    	String nom = args[0].trim();
    	
    	if(!isAlphaNumeric(nom)) {
    		throw new ReseauException("Le nom \"" + nom + "\" contient des caractères invalides", nline);
    	}
    	
    	try {
    		double capacite = Double.parseDouble(args[1].trim());
    		
    		if (reseau.chercherGenerateur(nom) != null || reseau.chercherMaison(nom) != null) {
                throw new ReseauException("L'identifiant '" + nom + "' est déjà utilisé.", nline);
            }
    		
    		reseau.ajouterGenerateur(nom, capacite);
    	}catch(NumberFormatException e) {
    		throw new ReseauException("La capacité doit être un nombre valide", nline);
    	}
    }
    
    /**
     * Analyse une ligne de déclaration de maison.
     * Format attendu : {@code maison(nom, TYPE)}
     *
     * @param reseau Le réseau à mettre à jour.
     * @param ligne  La ligne de texte.
     * @param nline  Le numéro de ligne.
     * @throws ReseauException Si le format est incorrect, le nom invalide/doublon ou le type inconnu (hors BASSE, NORMAL, FORTE).
     */
    public static void parserMaison(Reseau reseau, String ligne, int nline) throws ReseauException{
    	String[] args = splitString(ligne, nline, "maison");
    	if(args.length != 2) {
    		throw new ReseauException("maison attend 2 arguments (nom, type)", nline);
    	}
    	
    	String nom = args[0].trim();
    	
    	if(!isAlphaNumeric(nom)) {
    		throw new ReseauException("Le nom \"" + nom + "\" contient des caractères invalides", nline);
    	}
    	
    	try {
    		TypeMaison type = TypeMaison.valueOf(args[1].trim().toUpperCase());
    		
    		if (reseau.chercherGenerateur(nom) != null || reseau.chercherMaison(nom) != null) {
                throw new ReseauException("L'identifiant \"" + nom + "\" est déjà utilisé.", nline);
            }
    		
    		reseau.ajouterMaison(nom, type);
    	}catch(IllegalArgumentException e) {
    		throw new ReseauException("Type de maison inconnu (BASSE, NORMAL, FORTE)", nline);
    	}
    }
    
    /**
     * Analyse une ligne de déclaration de connexion.
     * Format attendu : {@code connexion(nom1, nom2)}
     * <p>
     * Vérifie que les deux éléments existent déjà et qu'il s'agit bien d'une paire (Maison, Générateur).
     *
     * @param reseau Le réseau à mettre à jour.
     * @param ligne  La ligne de texte.
     * @param nline  Le numéro de ligne.
     * @throws ReseauException Si un élément n'existe pas, si la connexion n'est pas entre une maison et un générateur, ou si la maison est déjà connectée.
     */
    public static void parserConnexion(Reseau reseau, String ligne, int nline) throws ReseauException{
    	String[] args = splitString(ligne, nline, "connexion");
    	if(args.length != 2) {
    		throw new ReseauException("connexion attend 2 arguments (arg1, arg2)", nline);
    	}
    	
    	String nom1 = args[0].trim();
    	String nom2 = args[1].trim();
    	
    	boolean existe1 = (reseau.chercherGenerateur(nom1) != null || reseau.chercherMaison(nom1) != null);
        boolean existe2 = (reseau.chercherGenerateur(nom2) != null || reseau.chercherMaison(nom2) != null);
        
        if (!existe1) {
        	throw new ReseauException("L'élément '" + nom1 + "' n'est pas défini.", nline);
        }
        if (!existe2) {
        	throw new ReseauException("L'élément '" + nom2 + "' n'est pas défini.", nline);
        }
        
        Maison m = reseau.chercherMaison(nom1);
        Generateur g = reseau.chercherGenerateur(nom2);
        
        if (m == null && g == null) {
            m = reseau.chercherMaison(nom2);
            g = reseau.chercherGenerateur(nom1);
        }

        if (m == null || g == null) {
        	throw new ReseauException("Une connexion doit relier une Maison et un Générateur.", nline);
        }

        if (m.getGenerateurAssocie() != null) {
        	throw new ReseauException("La maison '" + m.getNom() + "' est déjà connectée à un générateur.", nline);
        }

        reseau.connecter(nom1, nom2);
    }
    
    /**
     * Méthode utilitaire pour extraire les arguments entre parenthèses.
     * <p>
     * Vérifie que la ligne commence par {@code motCle(} et finit par {@code )},
     * puis découpe le contenu par la virgule.
     *
     * @param ligne   La ligne à découper.
     * @param nline   Le numéro de ligne pour l'erreur.
     * @param keyWord Le mot clé attendu au début (ex: "generateur").
     * @return Un tableau de String contenant les arguments séparés par des virgules.
     * @throws ReseauException Si le format des parenthèses est incorrect.
     */
    private static String[] splitString(String ligne, int nline, String keyWord) throws ReseauException{
    	if(!ligne.startsWith(keyWord + "(") || !ligne.endsWith(")")) {
    		throw new ReseauException("Format invalide", nline);
    	}
    	return ligne.substring(keyWord.length() + 1, ligne.length() - 1).split(",");
    }
    
    /**
     * Vérifie si une chaîne ne contient que des caractères alphanumériques (lettres et chiffres).
     *
     * @param s La chaîne à vérifier.
     * @return {@code true} si la chaîne est valide, {@code false} sinon.
     */
    public static boolean isAlphaNumeric(String s){
        String pattern= "^[a-zA-Z0-9]*$"; // Regex, merci THL :D
        return s != null && s.matches(pattern);
    }
    
    
}