package vue;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Écran d'accueil principal de l'application d'optimisation de réseau électrique.
 * <p>
 * Cette classe représente le point d'entrée de l'interface utilisateur et permet
 * à l'utilisateur de choisir entre les deux modes de configuration du réseau :
 * </p>
 * <ul>
 *   <li><b>Importation depuis un fichier :</b> Charger un réseau préconfiguré au format .txt</li>
 *   <li><b>Configuration manuelle :</b> Créer son propre réseau de zéro via l'interface graphique</li>
 * </ul>
 * 
 * <p>
 * L'interface utilise des feuilles de style CSS personnalisées pour harmoniser
 * l'apparence des boutons et du titre avec le reste de l'application.
 * </p>
 * 
 * <h2>Structure de l'écran :</h2>
 * <ul>
 *   <li><b>Haut :</b> Titre "MENU PRINCIPAL"</li>
 *   <li><b>Centre :</b> Deux boutons d'action alignés verticalement</li>
 * </ul>
 * 
 * @see MenuImportation
 * @see MenuConfiguration
 */
public class Accueil extends BorderPane {
	// LIEN ULTRA UTILE POUR LA GUI :
	// https://github.com/openjdk/jfx/blob/master/modules/javafx.controls/src/main/resources/com/sun/javafx/scene/control/skin/modena/modena.css
	
	/**
     * Label affichant le titre du menu principal.
     */
    private Label menu;
    
    /**
     * Bouton permettant d'accéder à l'interface d'importation de fichier.
     */
    private Button importer;
    
    /**
     * Bouton permettant d'accéder à l'interface de configuration manuelle.
     */
    private Button configmanuelle;
    
    /**
     * Conteneur vertical regroupant les boutons.
     */
    private VBox v;
    
    /**
     * Constructeur de la classe <code>Accueil</code>.
     * Initialise les composants graphiques, applique les styles CSS et 
     * définit les gestionnaires d'événements pour la navigation.
     * * <p>Actions de navigation :</p>
     * <ul>
     * <li>Le bouton "importer" redirige vers {@link MenuImportation}.</li>
     * <li>Le bouton "configmanuelle" redirige vers {@link MenuConfiguration}.</li>
     * </ul>
     */
    public Accueil(){
    	// Initialisation du titre
        menu = new Label("MENU PRINCIPAL");
        menu.getStyleClass().add("titre-principal");
        
        // Initialisation du bouton d'importation
        importer = new Button("Importer un réseau (Fichier)");
        importer.getStyleClass().add("button-bleu");
        importer.setMinWidth(200);
        
        // Initialisation du bouton de configuration manuelle
        configmanuelle = new Button("Configuration manuelle");
        configmanuelle.getStyleClass().add("button-bleu");
        configmanuelle.setMinWidth(200);
        
        // Organisation des boutons dans une VBox
        v = new VBox();
        v.getChildren().addAll(importer, configmanuelle);
        v.setAlignment(Pos.CENTER);
        v.setSpacing(20);
        
        // --- Actions des boutons ---
        
        importer.setOnAction(event -> {
            MenuImportation vueConfig1 = new MenuImportation();
            Scene sceneActuelle = this.getScene();
            sceneActuelle.setRoot(vueConfig1);
        });
        
        configmanuelle.setOnAction(event -> {
            MenuConfiguration vueConfig2 = new MenuConfiguration();
            Scene sceneActuelle = this.getScene();
            sceneActuelle.setRoot(vueConfig2);
        });
        
        // On aligne le titre au centre en haut
        BorderPane.setAlignment(menu, Pos.CENTER);
        BorderPane.setMargin(menu, new javafx.geometry.Insets(20, 0, 0, 0));
        
        this.setTop(menu);
        this.setCenter(v);
    }
}