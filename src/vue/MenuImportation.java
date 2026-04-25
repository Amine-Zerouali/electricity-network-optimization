package vue;

import java.io.File;
import java.io.IOException;

import io.ChargerReseau;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import modele.Reseau;
import modele.ReseauException;

/**
 * Interface graphique pour importer un réseau électrique depuis un fichier texte.
 * <p>
 * Permet de sélectionner un fichier .txt contenant la définition du réseau,
 * de configurer le coefficient lambda, puis de passer au menu d'opérations.
 * </p>
 * 
 * @see ChargerReseau
 * @see MenuOperation
 */
public class MenuImportation extends BorderPane {
	/** Bouton pour retourner au menu principal */
    private Button retour;
    
    /** Bouton pour valider et charger le réseau */
    private Button valider;
    
    /** Bouton pour ouvrir l'explorateur de fichiers */
    private Button parcourir;
    
    /** Label du champ de fichier */
    private Label champchemin;
    
    /** Label du champ lambda */
    private Label champlambda;
    
    /** Label d'affichage des erreurs */
    private Label erreur;
    
    /** Conteneur pour le champ fichier */
    private FlowPane f1;
    
    /** Conteneur pour le champ lambda */
    private FlowPane f2;
    
    /** Champ de saisie du chemin de fichier */
    private TextField t1;
    
    /** Champ de saisie du coefficient lambda */
    private TextField t2;
    
    /** Conteneur vertical principal */
    private VBox v;
    
    /** Coefficient de pénalité (10.0 par défaut) */
    private double lambda = 10.0;
    
    /** Instance du réseau chargé */
    private Reseau reseau;

    /**
     * Constructeur de l'interface d'importation.
     * <p>
     * Initialise les composants graphiques et configure les actions :
     * </p>
     * <ul>
     *   <li><b>Parcourir :</b> Ouvre un FileChooser qui est filtré sur les .txt</li>
     *   <li><b>Retour :</b> Retourne au menu {@link Accueil}</li>
     *   <li><b>Valider :</b> Charge le réseau et le passe à {@link MenuOperation}</li>
     * </ul>
     * <p>
     * Valide que le fichier existe et que lambda est un réel positif avant le chargement.
     * </p>
     */
    public MenuImportation() {
        // --- INITIALISATIONS ---
        retour = new Button("Retour");
        retour.getStyleClass().add("button-rouge");
        
        valider = new Button("Valider l'importation");
        valider.getStyleClass().add("button-vert");
        
        parcourir = new Button("Parcourir...");
        parcourir.getStyleClass().add("button-bleu");
        
        champchemin = new Label("Fichier réseau (.txt) : ");
        champlambda = new Label("Lambda : ");
        
        erreur = new Label();
        erreur.getStyleClass().add("label-erreur");
        
        f1 = new FlowPane();
        f2 = new FlowPane();
        
        t1 = new TextField();
        t1.setPromptText("Chemin du fichier...");
        t1.setPrefWidth(300);
        
        t2 = new TextField();
        t2.setPromptText("10 par défaut");
        t2.setPrefWidth(100);
        
        v = new VBox();

        f1.getChildren().addAll(champchemin, t1, parcourir);
        f1.setAlignment(Pos.CENTER);
        f1.setHgap(10);

        f2.getChildren().addAll(champlambda, t2);
        f2.setAlignment(Pos.CENTER);
        f2.setHgap(10);

        v.getChildren().addAll(retour, f1, f2, valider, erreur);
        v.setAlignment(Pos.CENTER);
        v.setSpacing(20);

        // --- ACTIONS ---

        // Parcourir
        parcourir.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sélectionner le fichier réseau");
            
            // Pour que ça ouvre directement sur le répertoire du projet
            fileChooser.setInitialDirectory(new File("./"));
            
            // Montrer que les fichiers .txt
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Texte", "*.txt"));
            
            File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());
            
            if (selectedFile != null) {
                // On écrit le chemin absolu dans le champ de texte
                t1.setText(selectedFile.getAbsolutePath());
                erreur.setText(""); // On efface les anciennes erreurs
            }
        });

        // Retour
        retour.setOnAction(event -> {
            Accueil accueil = new Accueil();
            this.getScene().setRoot(accueil);
        });

        // Valider
        valider.setOnAction(event -> {
            erreur.setText("");
            String chemin = t1.getText();
            
            // Vérification que le champ n'est pas vide
            if (chemin.isEmpty()) {
                erreur.setText("Veuillez sélectionner un fichier.");
                return;
            }

            // Récupération du Lambda
            if (!t2.getText().isEmpty()) {
                try {
                    lambda = Double.parseDouble(t2.getText());
                    if (lambda < 0) {
                    	throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    erreur.setText("Lambda doit être un réel positif");
                    return;
                }
            } else {
                lambda = 10.0;
            }

            // Chargement du fichier
            reseau = new Reseau();
            try {
                ChargerReseau.charger(chemin, reseau);
                
                // Passage à la fenêtre d'opération avec le réseau chargé et lambda
                MenuOperation menuop = new MenuOperation(reseau, lambda); 
                this.getScene().setRoot(menuop);

            } catch (IOException e) {
                erreur.setText("Erreur fichier (Introuvable ?) : " + e.getMessage());
            } catch (ReseauException e) {
                erreur.setText("Erreur de format dans le fichier : " + e.getMessage());
            }
        });

        this.setCenter(v);
    }
}