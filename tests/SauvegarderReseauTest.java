import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.SauvegarderReseau;
import modele.Reseau;
import modele.ReseauException;
import modele.TypeMaison;

public class SauvegarderReseauTest {

    private Reseau reseau;
    private final String fichierTest = "test_sauvegarde.txt";

    @BeforeEach
    void setUp() throws ReseauException {
        reseau = new Reseau();
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterMaison("M1", TypeMaison.NORMAL);
        reseau.ajouterMaison("M2", TypeMaison.BASSE);
        reseau.connecter("G1", "M1");
        reseau.connecter("G1", "M2");
    }

    @AfterEach
    void cleanUp() {
        File f = new File(fichierTest);
        if (f.exists()) {
            f.delete();
        }
    }

    /**
     * Test : le fichier est bien créé après la sauvegarde.
     */
    @Test
    void testCreationFichier() throws IOException {
        SauvegarderReseau.sauvegarder(fichierTest, reseau);

        File f = new File(fichierTest);
        assertTrue(f.exists());
    }

    /**
     * Test : le générateur est correctement écrit dans le fichier.
     */
    @Test
    void testEcritureGenerateur() throws IOException {
        SauvegarderReseau.sauvegarder(fichierTest, reseau);
        boolean trouve = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(fichierTest))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                if (ligne.equals("generateur(G1,100).")) {
                    trouve = true;
                    break;
                }
            }
        }
        assertTrue(trouve, "La ligne du générateur n'a pas été trouvée exactement comme prévu");
    }

    /**
     * Test : les maisons sont correctement écrites.
     */
    @Test
    void testEcritureMaisons() throws IOException {
        SauvegarderReseau.sauvegarder(fichierTest, reseau);

        boolean maisonM1 = false;
        boolean maisonM2 = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(fichierTest))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                if (ligne.equals("maison(M1,NORMAL).")) maisonM1 = true;
                if (ligne.equals("maison(M2,BASSE).")) maisonM2 = true;
            }
        }

        assertTrue(maisonM1);
        assertTrue(maisonM2);
    }

    /**
     * Test : les connexions sont correctement écrites.
     */
    @Test
    void testEcritureConnexions() throws IOException {
        SauvegarderReseau.sauvegarder(fichierTest, reseau);

        boolean connexionM1 = false;
        boolean connexionM2 = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(fichierTest))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                if (ligne.equals("connexion(G1,M1).")) connexionM1 = true;
                if (ligne.equals("connexion(G1,M2).")) connexionM2 = true;
            }
        }

        assertTrue(connexionM1);
        assertTrue(connexionM2);
    }
}
