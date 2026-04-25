import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.ChargerReseau;
import modele.Reseau;
import modele.ReseauException;
import modele.TypeMaison;

class ChargerReseauTest {

    private Reseau reseau;

    @BeforeEach
    void setUp() {
        reseau = new Reseau();
    }

    // ================== isAlphaNumeric ==================

    @Test
    void testIsAlphaNumericValide() {
        assertTrue(ChargerReseau.isAlphaNumeric("G1"));
        assertTrue(ChargerReseau.isAlphaNumeric("Maison123"));
        assertTrue(ChargerReseau.isAlphaNumeric(""));
    }

    @Test
    void testIsAlphaNumericInvalide() {
        assertFalse(ChargerReseau.isAlphaNumeric("G-1"));
        assertFalse(ChargerReseau.isAlphaNumeric("M@1"));
        assertFalse(ChargerReseau.isAlphaNumeric("M 1"));
    }

    // ================== parserGenerateur ==================

    @Test
    void testParserGenerateurValide() throws ReseauException {
        ChargerReseau.parserGenerateur(reseau, "generateur(G1,100)", 1);
        assertNotNull(reseau.chercherGenerateur("G1"));
    }

    @Test
    void testParserGenerateurMauvaisNombreArguments() {
        assertThrows(ReseauException.class, () ->
            ChargerReseau.parserGenerateur(reseau, "generateur(G1)", 1)
        );
        assertThrows(ReseauException.class, () ->
        ChargerReseau.parserGenerateur(reseau, "generateur(G1,60,19)", 1)
    );
    }

    @Test
    void testParserGenerateurNomInvalide() {
        assertThrows(ReseauException.class, () ->
            ChargerReseau.parserGenerateur(reseau, "generateur(G-1,100)", 1)
        );
    }

    @Test
    void testParserGenerateurCapaciteInvalide() {
        assertThrows(ReseauException.class, () ->
            ChargerReseau.parserGenerateur(reseau, "generateur(G1,abc)", 1)
        );
    }

    @Test
    void testParserGenerateurDoublonNom() throws ReseauException {
        reseau.ajouterMaison("G1", TypeMaison.NORMAL);

        assertThrows(ReseauException.class, () ->
            ChargerReseau.parserGenerateur(reseau, "generateur(G1,100)", 1)
        );
    }

    // ================== parserMaison ==================

    @Test
    void testParserMaisonValide() throws ReseauException {
        ChargerReseau.parserMaison(reseau, "maison(M1,NORMAL)", 1);
        assertNotNull(reseau.chercherMaison("M1"));
    }

    @Test
    void testParserMaisonMauvaisNombreArguments() {
        assertThrows(ReseauException.class, () ->
            ChargerReseau.parserMaison(reseau, "maison(M1)", 1)
        );
        assertThrows(ReseauException.class, () ->
        ChargerReseau.parserMaison(reseau, "maison(M1,FORTE, 798)", 1)
    );
    }

    @Test
    void testParserMaisonNomInvalide() {
        assertThrows(ReseauException.class, () ->
            ChargerReseau.parserMaison(reseau, "maison(M@1,NORMAL)", 1)
        );
    }

    @Test
    void testParserMaisonTypeInvalide() {
        assertThrows(ReseauException.class, () ->
            ChargerReseau.parserMaison(reseau, "maison(M1,INCONNU)", 1)
        );
    }

    @Test
    void testParserMaisonDoublonNom() throws ReseauException {
        reseau.ajouterGenerateur("M1", 100);

        assertThrows(ReseauException.class, () ->
            ChargerReseau.parserMaison(reseau, "maison(M1,NORMAL)", 1)
        );
    }

    // ================== parserConnexion ==================

    @Test
    void testParserConnexionValide() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterMaison("M1", TypeMaison.NORMAL);

        ChargerReseau.parserConnexion(reseau, "connexion(M1,G1)", 3);

        assertEquals("G1", reseau.chercherMaison("M1").getGenerateurAssocie().getNom());
    }

    @Test
    void testParserConnexionMauvaisNombreArguments() throws ReseauException {
    	reseau.ajouterMaison("M1", TypeMaison.NORMAL);
        reseau.ajouterGenerateur("G1", 100);
        
        assertThrows(ReseauException.class, () ->
            ChargerReseau.parserConnexion(reseau, "connexion(M1)", 1)
        );
        
        assertThrows(ReseauException.class, () ->
            ChargerReseau.parserConnexion(reseau, "connexion(M1)", 1)
        );
        assertThrows(ReseauException.class, () ->
        ChargerReseau.parserConnexion(reseau, "connexion(M1,G1,098)", 1)
    );
    }

    @Test
    void testParserConnexionMaisonInexistant() throws ReseauException {
        reseau.ajouterMaison("M1", TypeMaison.NORMAL);
        
        assertThrows(ReseauException.class, () ->
            ChargerReseau.parserConnexion(reseau, "connexion(M1,G1)", 1)
        );
    }

    @Test
    void testParserConnexionGenerateurInexistant() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        
        assertThrows(ReseauException.class, () ->
            ChargerReseau.parserConnexion(reseau, "connexion(M1,G1)", 1)
        );
    }
    
    @Test
    void testParserConnexionMaisonMaison() throws ReseauException {
        reseau.ajouterMaison("M1", TypeMaison.NORMAL);
        reseau.ajouterMaison("M2", TypeMaison.NORMAL);

        assertThrows(ReseauException.class, () ->
            ChargerReseau.parserConnexion(reseau, "connexion(M1,M2)", 1)
        );
    }

    @Test
    void testParserConnexionGenerateurGenerateur() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterGenerateur("G2", 100);

        assertThrows(ReseauException.class, () ->
            ChargerReseau.parserConnexion(reseau, "connexion(G1,G2)", 1)
        );
    }

    @Test
    void testParserConnexionMaisonDejaConnectee() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterGenerateur("G2", 100);
        reseau.ajouterMaison("M1", TypeMaison.NORMAL);

        reseau.connecter("M1", "G1");

        assertThrows(ReseauException.class, () ->
            ChargerReseau.parserConnexion(reseau, "connexion(M1,G2)", 4)
        );
    }

 // ================== charger (fichier) ==================

    @Test
    void testChargerLigneSansPoint() throws IOException {
        String path = "test_sans_point.txt";

        try (FileWriter fw = new FileWriter(path)) {
            fw.write("generateur(G1,100)\n");
        }

        assertThrows(ReseauException.class, () ->
            ChargerReseau.charger(path, reseau)
        );

        new java.io.File(path).delete();
    }

    @Test
    void testChargerMotCleInconnu() throws IOException {
        String path = "test_mot_cle.txt";

        try (FileWriter fw = new FileWriter(path)) {
            fw.write("inconnu(G1,100).\n");
        }

        assertThrows(ReseauException.class, () ->
            ChargerReseau.charger(path, reseau)
        );

        new java.io.File(path).delete();
    }

    @Test
    void testChargerOrdreIncorrect() throws IOException {
        String path = "test_ordre.txt";

        try (FileWriter fw = new FileWriter(path)) {
            fw.write("connexion(M1,G1).\n");
        }

        assertThrows(ReseauException.class, () ->
            ChargerReseau.charger(path, reseau)
        );

        new java.io.File(path).delete();
    }

    @Test
    void testChargerReseauInvalide() throws IOException {
        String path = "test_reseau_invalide.txt";

        try (FileWriter fw = new FileWriter(path)) {
            fw.write("generateur(G1,100).\n");
            fw.write("maison(M1,NORMAL).\n");
        }

        assertThrows(ReseauException.class, () ->
            ChargerReseau.charger(path, reseau)
        );

        new java.io.File(path).delete();
    }

    
    @Test
    void testOrdreGenerateurApresMaison() throws IOException {
        String path = "test_ordre_gen.txt";
        try (FileWriter fw = new FileWriter(path)) {
            fw.write("maison(M1,NORMAL).\n");
            fw.write("generateur(G1,100).\n"); // Interdit après une maison
        }

        assertThrows(ReseauException.class, () ->
            ChargerReseau.charger(path, reseau)
        );

        new java.io.File(path).delete();
    }
    
    @Test
    void testLigneMalFormeeParenthses() throws IOException {
        String path = "test_malforme.txt";
        try (FileWriter fw = new FileWriter(path)) {
            fw.write("generateur G1,100).\n"); 
        }

        assertThrows(ReseauException.class, () ->
            ChargerReseau.charger(path, reseau)
        );

        new java.io.File(path).delete();
    }
    
    @Test
    void testConstructeurPrive() throws Exception {
        java.lang.reflect.Constructor<ChargerReseau> constructor = ChargerReseau.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertNotNull(constructor.newInstance());
    }
}
