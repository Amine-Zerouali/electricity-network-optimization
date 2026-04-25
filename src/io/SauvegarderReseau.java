package io;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import modele.Generateur;
import modele.Maison;
import modele.Reseau;

/**
 * Classe responsable de la sauvegarde du réseau dans un fichier texte.
 * <p>
 * Elle permet d'exporter l'état complet d'un objet {@link Reseau} vers un fichier texte,
 * en respectant le format d'entrée attendu par la classe de chargement
 * (syntaxe spécifique avec point final).
 */
public class SauvegarderReseau {
	/**
     * Constructeur privé pour empêcher l'instanciation de cette classe utilitaire.
     * <p>
     * Comme cette classe ne contient que des méthodes statiques,
     * elle n'est pas conçue pour être instanciée.
     */
    private SauvegarderReseau() {
        // Empêche l'instanciation
    }
	
	/**
     * Sauvegarde l'état actuel du réseau dans un fichier texte.
     * <p>
     * L'écriture se fait séquentiellement :
     * <ol>
     * <li>Définition des générateurs : {@code generateur(nom,capacité).}</li>
     * <li>Définition des maisons : {@code maison(nom,TYPE).}</li>
     * <li>Définition des connexions : {@code connexion(générateur,maison).}</li>
     * </ol>
     * Chaque instruction est terminée par un point et suivie d'un saut de ligne.
     *
     * @param cheminFichier Le chemin complet ou relatif du fichier de sortie (sera créé ou écrasé).
     * @param reseau        L'objet {@link Reseau} contenant les données à sauvegarder.
     * @throws IOException  Si une erreur d'entrée/sortie survient lors de l'ouverture ou de l'écriture du fichier.
     */
    public static void sauvegarder(String cheminFichier, Reseau reseau) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cheminFichier))) {
            
            // Écrire les générateurs
            for (Generateur g : reseau.getGenerateurs()) {
                // Format : generateur(nom,capacité).
                writer.write(String.format("generateur(%s,%.0f).", g.getNom(), g.getCapaciteMax()));
                writer.newLine();
            }
            
            // Écrire les maisons
            for (Maison m : reseau.getMaisons()) {
                // Format : maison(nom,TYPE).
                writer.write(String.format("maison(%s,%s).", m.getNom(), m.getType().name()));
                writer.newLine();
            }
            
            // Écrire les connexions
            for (Generateur g : reseau.getGenerateurs()) {
                for (Maison m : g.getMaisonsConnectees()) {
                    // Format : connexion(generateur,maison).
                    writer.write(String.format("connexion(%s,%s).", g.getNom(), m.getNom()));
                    writer.newLine();
                }
            }
        }
    }
}