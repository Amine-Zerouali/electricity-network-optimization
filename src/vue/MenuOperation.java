package vue;

import java.io.File;
import java.io.IOException;

import algo.Optimiseur;
import io.SauvegarderReseau;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser; // Pour la sauvegarde moderne
import modele.Reseau;

/**
 * Interface graphique pour l'optimisation et la sauvegarde d'un réseau électrique.
 * <p>
 * Permet de lancer l'optimisation automatique du réseau, visualiser les résultats,
 * et sauvegarder la solution optimisée dans un fichier.
 * </p>
 * 
 * @see Optimiseur
 * @see SauvegarderReseau
 */
public class MenuOperation extends BorderPane {
	/** Label du titre principal */
    private Label titre;
    
    /** Label affichant le coût actuel du réseau */
    private Label infoCout;
    
    /** Zone de texte affichant l'état complet du réseau */
    private TextArea affichageReseau;
    
    /** Bouton pour lancer l'optimisation automatique */
    private Button res;
    
    /** Bouton pour sauvegarder le réseau dans un fichier */
    private Button save;
    
    /** Bouton pour quitter l'application */
    private Button fin;
    
    /** Conteneur vertical central */
    private VBox centreVBox;
    
    /** Conteneur horizontal pour les boutons */
    private HBox boutonsBox;
    
    /** Instance du réseau électrique */
    private Reseau reseau;

    /**
     * Constructeur principal de l'interface d'opérations.
     * <p>
     * Initialise l'interface avec le réseau chargé et configure les actions :
     * </p>
     * <ul>
     *   <li><b>Optimisation :</b> Lance {@link Optimiseur#lancerAlgos} et met à jour l'affichage</li>
     *   <li><b>Sauvegarde :</b> Ouvre un FileChooser pour enregistrer le réseau au format .txt</li>
     *   <li><b>Quitter :</b> Ferme l'application</li>
     * </ul>
     * 
     * @param reseau Le réseau électrique à optimiser
     * @param lambda Le coefficient de pénalité pour le calcul du coût
     */
    public MenuOperation(Reseau reseau, double lambda) {
    	this.reseau = reseau;
    	
        // --- INITIALISATIONS ---
        titre = new Label("OPÉRATIONS SUR LE RÉSEAU");
        titre.getStyleClass().add("titre-principal");

        // Affichage du coût
        infoCout = new Label("Coût actuel : " + String.format("%.4f", reseau.calculerCout(lambda)));
        infoCout.setStyle("-fx-font-size: 14px;");

        // Zone de texte pour voir le réseau (Générateurs, Maisons, Connexions)
        affichageReseau = new TextArea(reseau.toString());
        affichageReseau.setEditable(false);
        affichageReseau.setPrefHeight(200);
        affichageReseau.setFont(javafx.scene.text.Font.font("Monospaced", 12));
        
        // Boutons
        res = new Button("Lancer l'Optimisation Auto");
        save = new Button("Sauvegarder la solution...");
        fin = new Button("Quitter");

        // Couleurs
        res.getStyleClass().add("button-vert");
        save.getStyleClass().add("button-bleu");
        fin.getStyleClass().add("button-rouge");

        boutonsBox = new HBox(15, res, save, fin);
        boutonsBox.setAlignment(Pos.CENTER);

        centreVBox = new VBox(15);
        centreVBox.setPadding(new Insets(20));
        centreVBox.getChildren().addAll(titre, infoCout, affichageReseau, boutonsBox);
        centreVBox.setAlignment(Pos.CENTER);
        
        this.setCenter(centreVBox);

        // --- ACTIONS ---

        // Résolution Automatique
        res.setOnAction(event -> {
            infoCout.setText("Calcul en cours... (Veuillez patienter)");
            
            double coutAvant = this.reseau.calculerCout(lambda);
            
            // On passe le réseau actuel à l'optimiseur
            Reseau meilleur = Optimiseur.lancerAlgos(this.reseau, 100000, lambda);
            
            if (meilleur != null) {
                // Une amélioration a été trouvée
                double nouveauCout = meilleur.calculerCout(lambda);
                this.reseau = meilleur;
                
                // Mise à jour de l'interface
                infoCout.setText("Optimisation terminée ! Nouveau coût : " + String.format("%.4f", nouveauCout));
                infoCout.setTextFill(Color.GREEN);
                affichageReseau.setText(meilleur.toString());
            } else {
                // Aucune amélioration (null retourné)
                infoCout.setText("Aucune amélioration trouvée. Coût : " + String.format("%.4f", coutAvant));
                infoCout.setTextFill(Color.ORANGE);
            }
        });

        save.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sauvegarder le réseau");
            fileChooser.setInitialFileName("solution_optimisee.txt");
            
            // Toujours pour se mettre dans le dossier du projet
            fileChooser.setInitialDirectory(new File("./"));
            
            // Filtre pour avoir que des fichiers en .txt
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Texte", "*.txt"));
            
            // Oouvre la boîte de dialogue "Enregistrer sous"
            File file = fileChooser.showSaveDialog(this.getScene().getWindow());
            
            if (file != null) {
                try {
                    SauvegarderReseau.sauvegarder(file.getAbsolutePath(), this.reseau);
                    
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Sauvegarde");
                    alert.setHeaderText(null);
                    alert.setContentText("Fichier sauvegardé avec succès :\n" + file.getName());
                    alert.showAndWait();
                    
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Impossible de sauvegarder");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
        });

        // Fin
        fin.setOnAction(event -> {
             System.exit(0);
        });
    }
    
    /**
     * Constructeur simplifié avec lambda par défaut.
     * <p>
     * Appelle le constructeur principal avec lambda = 10.0.
     * </p>
     * 
     * @param reseau Le réseau électrique à optimiser
     */
    public MenuOperation(Reseau reseau) {
        this(reseau, 10.0);
    }
}