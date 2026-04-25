package app;
import java.util.List;

import io.ChargerReseau;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import modele.Reseau;
import vue.Accueil;
import vue.MenuOperation;

/**
 * Point d'entrée principal de l'application graphique JavaFX.
 * <p>
 * Cette classe gère le lancement de l'interface utilisateur et supporte deux modes :
 * <ul>
 * <li><b>Mode interactif :</b> Lance l'écran d'accueil sans arguments.</li>
 * <li><b>Mode direct :</b> Charge automatiquement un réseau depuis un fichier
 *     et accède directement au menu d'opérations.</li>
 * </ul>
 * 
 * @see Accueil
 * @see MenuOperation
 * @see ChargerReseau
 */
public class Interface extends Application {
	/**
	 * Constructeur par défaut de la classe Interface.
	 * Initialise une nouvelle instance de l'application.
	 */
	public Interface() {
	    super();
	}

	/**
     * Initialise et affiche la fenêtre principale de l'application.
     * <p>
     * Cette méthode analyse les arguments de ligne de commande et détermine
     * la vue initiale à afficher :
     * </p>
     * <ul>
     * <li>Sans argument : affiche {@link Accueil}</li>
     * <li>Avec fichier : charge le réseau et affiche {@link MenuOperation}</li>
     * <li>En cas d'erreur : affiche {@link Accueil} avec un message d'erreur</li>
     * </ul>
     * 
     * @param stage La fenêtre principale fournie par JavaFX.
     */
    @Override
    public void start(Stage stage) {
        stage.setTitle("Optimisation du Réseau Électrique");
        
        // Récupération des arguments du main
        List<String> args = getParameters().getRaw();
        BorderPane root;

        // S'il y a des arguments
        if (!args.isEmpty()) {
            if (args.size() > 2) {
                System.err.println("Trop d'arguments. Entrée attendue : java Interface <fichier_reseau> [valeur_lambda]");
                root = new Accueil();
            } else {
                try {
                    String cheminFichier = args.get(0);
                    double lambda = 10.0; // Valeur par défaut qu'on donne

                    // On vérifie si ya un lambda
                    if (args.size() == 2) {
                        try {
                            lambda = Double.parseDouble(args.get(1));
                            if (lambda <= 0) {
                            	throw new NumberFormatException();
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Le paramètre lambda doit être un double positif. Utilisation de 10.0.");
                            lambda = 10.0;
                        }
                    }

                    // Tentative de charger le réseau
                    Reseau reseau = new Reseau();
                    ChargerReseau.charger(cheminFichier, reseau);
                    
                    // Si aucune erreur, on peut directement aller au menu des opérations
                    root = new MenuOperation(reseau, lambda);
                    System.out.println("Réseau chargé avec succès. Passage au menu des opérations.");

                } catch (Exception e) {
                    System.err.println("Erreur de chargement auto : " + e.getMessage());
                    // S'il y a un problème, on va à l'accueil
                    root = new Accueil();
                }
            }
        } else {
            // Lancement sans arguments
            root = new Accueil();
        }
        
        // Configuration de la scène
        Scene scene = new Scene(root, 600, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    /**
     * Point d'entrée de l'application.
     * <p>
     * Démarre l'application JavaFX en appelant {@link Application#launch(String...)}.
     * Les arguments sont transmis à la méthode {@link #start(Stage)}.
     * </p>
     * 
     * @param args Arguments de ligne de commande (fichier réseau et lambda (optionnel)).
     */
    public static void main(String[] args) {
        launch(args);
    }
}