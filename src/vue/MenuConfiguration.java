package vue;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import modele.Reseau;
import modele.ReseauException;
import modele.TypeMaison;

/**
 * Interface graphique pour la configuration manuelle d'un réseau électrique.
 * <p>
 * Permet de créer et configurer un réseau en ajoutant des générateurs, des maisons
 * et en gérant les connexions entre eux.
 * </p>
 * 
 * @see Reseau
 * @see MenuOperation
 */
public class MenuConfiguration extends BorderPane {
	/** Bouton pour ajouter un nouveau générateur au réseau */
    private Button ajoutgenerateur;
    
    /** Bouton pour ajouter une nouvelle maison au réseau */
    private Button ajoutmaison;
    
    /** Bouton pour créer une connexion entre un générateur et une maison */
    private Button ajoutconnexion;
    
    /** Bouton pour supprimer une connexion existante */
    private Button supprimerconnexion;
    
    /** Bouton pour finaliser la configuration et passer à l'optimisation */
    private Button finconfig;
    
    /** Bouton pour retourner au menu principal avec confirmation */
    private Button retour;
    
    /** Bouton de validation des formulaires de saisie */
    private Button validerFormulaire;
    
    /** Label affichant le titre principal de l'interface */
    private Label titre;
    
    /** Label pour les messages d'interaction généraux */
    private Label interaction;
    
    /** Label d'avertissement concernant la perte de données */
    private Label avertissement;
    
    /** Label affichant les messages d'erreur en rouge */
    private Label erreur = new Label("");
    
    /** Label affichant les messages de succès en vert */
    private Label succes = new Label("");
    
    /** Conteneur vertical central pour les éléments de l'interface */
    private VBox v;
    
    /** Barre horizontale supérieure contenant navigation et avertissements */
    private HBox topBar;
    
    /** Référence à la scène JavaFX actuelle pour la navigation */
    private Scene sceneActuelle;
    
    /** Instance du réseau électrique en cours de configuration */
    private Reseau reseau = new Reseau();

    
    /**
     * Constructeur de l'interface de configuration manuelle.
     * <p>
     * Initialise les composants graphiques et configure les gestionnaires d'événements.
     * Affiche une barre d'avertissement concernant la perte de données en cas de retour.
     * </p>
     */
    public MenuConfiguration() {
        // --- INITIALISATIONS ---
        
        // Barre du haut pour l'avertissement
        retour = new Button("⬅ Retour Menu Principal");
        retour.getStyleClass().add("button-rouge");
        
        avertissement = new Label("⚠ Attention : Quitter ce menu effacera le réseau en cours.");
        avertissement.setTextFill(Color.DARKRED);
        avertissement.setStyle("-fx-font-weight: bold; -fx-font-style: italic;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar = new HBox(15);
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setStyle("-fx-background-color: #f8d7da; -fx-border-color: #f5c6cb; -fx-border-width: 0 0 2 0;");
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getChildren().addAll(retour, spacer, avertissement);
        
        // Pour laisser la barre tout en haut, elle ne doit pas bouger :/
        this.setTop(topBar);

        // Les Boutons du Menu Central
        titre = new Label("Configuration du Réseau");
        titre.getStyleClass().add("titre-principal");
        
        ajoutgenerateur = new Button("Ajouter un générateur");
        ajoutgenerateur.getStyleClass().add("button-bleu");
        
        ajoutmaison = new Button("Ajouter une maison");
        ajoutmaison.getStyleClass().add("button-bleu");
        
        ajoutconnexion = new Button("Ajouter une connexion");
        ajoutconnexion.getStyleClass().add("button-vert");
        
        supprimerconnexion = new Button("Supprimer une connexion");
        supprimerconnexion.getStyleClass().add("button-rouge");
        
        finconfig = new Button("Valider la configuration");
        finconfig.getStyleClass().add("button-vert");
        
        // Labels d'info
        erreur.setTextFill(Color.RED);
        erreur.setStyle("-fx-font-weight: bold;");
        succes.setTextFill(Color.GREEN);
        succes.setStyle("-fx-font-weight: bold;");
        
        interaction = new Label(); // Pour les messages globaux en bas
        interaction.setPadding(new Insets(10));

        // Bouton valider des sous-menus
        validerFormulaire = new Button("Valider");
        validerFormulaire.getStyleClass().add("button-vert");
        
        
        v = new VBox();
        this.setCenter(v);
        this.setBottom(interaction);
        BorderPane.setAlignment(interaction, Pos.CENTER);

        this.MenuConfigurationManuellePrincipal();
        
        // --- ACTION DU BOUTON RETOUR ---
        retour.setOnAction(event -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Vous allez quitter la configuration");
            alert.setContentText("Le réseau actuel sera perdu. Voulez-vous vraiment retourner au menu principal ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                Accueil accueil = new Accueil();
                sceneActuelle = this.getScene();
                sceneActuelle.setRoot(accueil);
            }
        });
    }

    /**
     * Affiche le menu principal de configuration.
     * <p>
     * Configure les actions de tous les boutons principaux :
     * </p>
     * <ul>
     *   <li><b>Ajouter générateur :</b> Ouvre {@link #MenuAjoutGenerateur()}</li>
     *   <li><b>Ajouter maison :</b> Ouvre {@link #MenuAjoutMaison()}</li>
     *   <li><b>Ajouter connexion :</b> Ouvre {@link #MenuAjoutConnexion()}</li>
     *   <li><b>Supprimer connexion :</b> Ouvre {@link #MenuSupprimerConnexion()}</li>
     *   <li><b>Valider :</b> Vérifie le réseau et passe à {@link MenuOperation}</li>
     * </ul>
     */
    private void MenuConfigurationManuellePrincipal() {
        v.getChildren().clear(); 
        interaction.setText("");
        erreur.setText("");
        succes.setText("");

        v.getChildren().addAll(titre, ajoutgenerateur, ajoutmaison, ajoutconnexion, supprimerconnexion, finconfig);
        v.setAlignment(Pos.CENTER);
        v.setSpacing(15);
        v.setPadding(new Insets(20));

        // Actions
        ajoutgenerateur.setOnAction(event -> this.MenuAjoutGenerateur());
        ajoutmaison.setOnAction(event -> this.MenuAjoutMaison());
        ajoutconnexion.setOnAction(event -> this.MenuAjoutConnexion());
        supprimerconnexion.setOnAction(event -> this.MenuSupprimerConnexion());

        finconfig.setOnAction(event -> {
            try {
                reseau.verificationReseau();
                // Si OK :
                MenuOperation op = new MenuOperation(reseau, 10.0);
                Scene sceneActuelle = this.getScene();
                sceneActuelle.setRoot(op);
            } catch (ReseauException e) {
                interaction.setText("Erreur : " + e.getMessage());
                interaction.setTextFill(Color.RED);
            }
        });
    }

    /**
     * Affiche le formulaire d'ajout d'un générateur.
     * <p>
     * Permet de saisir le nom et la capacité d'un générateur.
     * Si le nom existe déjà, la capacité sera mise à jour.
     * </p>
     * 
     * @see Reseau#ajouterGenerateur(String, int)
     */
    private void MenuAjoutGenerateur() {
        v.getChildren().clear();
        succes.setText(""); 
        erreur.setText("");

        Label instruction = new Label("Ajout / Modification d'un Générateur");
        instruction.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        TextField champNom = new TextField();
        champNom.setPromptText("Nom (ex: G1)");
        champNom.setMinWidth(200);
        
        TextField champCapacite = new TextField();
        champCapacite.setPromptText("Capacité (ex: 60)");
        champCapacite.setMinWidth(200);
        
        VBox formulaire = new VBox(15);
        formulaire.setAlignment(Pos.CENTER);
        formulaire.setMaxWidth(350);
        formulaire.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5; -fx-padding: 20; -fx-background-color: #f9f9f9;");
        
        HBox nomGen = new HBox(10);
        nomGen.setAlignment(Pos.CENTER);
        Label labelNom = new Label("Nom :");
        labelNom.setMinWidth(100);
        nomGen.getChildren().addAll(labelNom, champNom);
        
        HBox capGen = new HBox(10); 
        capGen.setAlignment(Pos.CENTER);
        Label labelCap = new Label("Capacité (kW) :");
        labelCap.setMinWidth(100);
        capGen.getChildren().addAll(labelCap, champCapacite);
        
        Button retourLocal = new Button("Annuler");
        retourLocal.getStyleClass().add("button-rouge");
        retourLocal.setOnAction(e -> this.MenuConfigurationManuellePrincipal());
        
        HBox boutons = new HBox(10);
        boutons.setAlignment(Pos.CENTER);
        boutons.getChildren().addAll(retourLocal, validerFormulaire);
        
        formulaire.getChildren().addAll(instruction, nomGen, capGen, boutons, erreur, succes);
        v.getChildren().add(formulaire);

        validerFormulaire.setOnAction(event -> {
            erreur.setText(""); succes.setText("");
            try {
                String nom = champNom.getText();
                if(nom.isEmpty()) throw new IllegalArgumentException("Le nom est vide");
                int capacite = Integer.parseInt(champCapacite.getText());
                if(capacite < 0) throw new NumberFormatException();

                String resul = reseau.ajouterGenerateur(nom, capacite);
                succes.setText(resul);
                champNom.clear(); champCapacite.clear();
            } catch (NumberFormatException e) {
                erreur.setText("La capacité doit être un entier positif.");
            } catch (Exception e) {
                erreur.setText(e.getMessage());
            }
        });
    }

    /**
     * Affiche le formulaire d'ajout d'une maison.
     * <p>
     * Permet de saisir le nom et le type de consommation (BASSE, NORMAL, FORTE).
     * </p>
     * 
     * @see Reseau#ajouterMaison(String, TypeMaison)
     * @see TypeMaison
     */
    private void MenuAjoutMaison() {
        v.getChildren().clear();
        succes.setText(""); erreur.setText("");

        Label instruction = new Label("Ajout d'une Maison");
        instruction.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField champNom = new TextField();
        champNom.setPromptText("Nom (ex: M1)");
        champNom.setMinWidth(200);
        
        TextField champType = new TextField();
        champType.setPromptText("Type (BASSE, NORMAL, FORTE)");
        champType.setMinWidth(200);
        
        VBox formulaire = new VBox(15);
        formulaire.setAlignment(Pos.CENTER);
        formulaire.setMaxWidth(350);
        formulaire.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5; -fx-padding: 20; -fx-background-color: #f9f9f9;");
        
        HBox nomM = new HBox(10); 
        nomM.setAlignment(Pos.CENTER);
        Label labelNomM = new Label("Nom :");
        labelNomM.setMinWidth(80);
        nomM.getChildren().addAll(labelNomM, champNom);

        HBox typeM = new HBox(10); 
        typeM.setAlignment(Pos.CENTER);
        Label labelType = new Label("Type :");
        labelType.setMinWidth(80);
        typeM.getChildren().addAll(labelType, champType);
        
        Button retourLocal = new Button("Annuler");
        retourLocal.getStyleClass().add("button-rouge");
        retourLocal.setOnAction(e -> this.MenuConfigurationManuellePrincipal());
        
        HBox boutons = new HBox(10); boutons.setAlignment(Pos.CENTER);
        boutons.getChildren().addAll(retourLocal, validerFormulaire);
        
        formulaire.getChildren().addAll(instruction, nomM, typeM, boutons, erreur, succes);
        v.getChildren().add(formulaire);

        validerFormulaire.setOnAction(event -> {
            erreur.setText(""); succes.setText("");
            try {
                String nom = champNom.getText();
                if(nom.isEmpty()) throw new IllegalArgumentException("Nom vide");
                TypeMaison type = TypeMaison.valueOf(champType.getText().toUpperCase());
                
                String resul = reseau.ajouterMaison(nom, type);
                succes.setText(resul);
                champNom.clear(); champType.clear();
            } catch (IllegalArgumentException e) {
                erreur.setText("Type invalide (utilisez BASSE, NORMAL ou FORTE).");
            } catch (Exception e) {
                erreur.setText(e.getMessage());
            }
        });
    }

    /**
     * Affiche le menu d'ajout de connexion.
     * 
     * @see #setupConnexionMenu(String, boolean)
     */
    private void MenuAjoutConnexion() {
    	setupConnexionMenu("Nouvelle Connexion", true);
    }
    
    /**
     * Point d'entrée pour afficher le menu de suppression de connexion.
     * <p>
     * Redirige vers la méthode générique {@link #setupConnexionMenu(String, boolean)}
     * en mode suppression.
     * </p>
     * 
     * @see #setupConnexionMenu(String, boolean)
     */
    private void MenuSupprimerConnexion() {
    	setupConnexionMenu("Supprimer Connexion", false);
    }

    /**
     * Configure et affiche le formulaire de gestion des connexions.
     * <p>
     * Permet de créer ou supprimer des connexions entre un générateur et une maison.
     * Affiche l'état actuel du réseau pour faciliter la visualisation.
     * </p>
     * 
     * @param titreMenu Le titre à afficher dans le formulaire
     * @param modeAjout true pour créer une connexion, false pour la supprimer
     * 
     * @see Reseau#connecter(String, String)
     * @see Reseau#deconnecter(String, String)
     */
    private void setupConnexionMenu(String titreMenu, boolean modeAjout) {
        v.getChildren().clear();
        succes.setText(""); erreur.setText("");

        Label instruction = new Label(titreMenu);
        instruction.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField champ1 = new TextField();
        champ1.setPromptText("Nom (Générateur ou Maison)");
        champ1.setMinWidth(200);
        
        TextField champ2 = new TextField();
        champ2.setPromptText("Nom (Générateur ou Maison)");
        champ2.setMinWidth(200);
        
        TextArea affichageReseau = new TextArea(reseau.toString());
        affichageReseau.setEditable(false);
        affichageReseau.setPrefHeight(150);
        affichageReseau.setStyle("-fx-font-family: 'monospaced';");

        VBox formulaire = new VBox(15);
        formulaire.setAlignment(Pos.CENTER);
        formulaire.setMaxWidth(400);
        formulaire.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5; -fx-padding: 20; -fx-background-color: #f9f9f9;");
        
        HBox nomG = new HBox(10); 
        nomG.setAlignment(Pos.CENTER);
        Label label1 = new Label("Élément 1 :");
        label1.setMinWidth(80);
        nomG.getChildren().addAll(label1, champ1);

        HBox nomM = new HBox(10); 
        nomM.setAlignment(Pos.CENTER);
        Label label2 = new Label("Élément 2 :");
        label2.setMinWidth(80);
        nomM.getChildren().addAll(label2, champ2);
        
        Button retourLocal = new Button("Annuler");
        retourLocal.getStyleClass().add("button-rouge");
        retourLocal.setOnAction(e -> this.MenuConfigurationManuellePrincipal());
        
        HBox boutons = new HBox(10); boutons.setAlignment(Pos.CENTER);
        boutons.getChildren().addAll(retourLocal, validerFormulaire);

        formulaire.getChildren().addAll(instruction, nomG, nomM, boutons, erreur, succes, new Label("État actuel :"), affichageReseau);
        v.getChildren().add(formulaire);

        validerFormulaire.setOnAction(event -> {
            erreur.setText(""); succes.setText("");
            try {
                String s1 = champ1.getText();
                String s2 = champ2.getText();
                if(s1.isEmpty() || s2.isEmpty()) throw new IllegalArgumentException("Champs vides");

                String msgRetour = "";
                if(modeAjout) {
                    reseau.connecter(s1, s2);
                    msgRetour = "Connexion établie.";
                } else {
                    reseau.deconnecter(s1, s2);
                    msgRetour = "Déconnexion réussie.";
                }
                
                succes.setText(msgRetour);
                affichageReseau.setText(reseau.toString());
                champ1.clear(); champ2.clear();
            } catch (Exception e) {
                erreur.setText(e.getMessage());
            }
        });
    }
}