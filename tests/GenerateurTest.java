import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import modele.Generateur;
import modele.Maison;
import modele.TypeMaison;

class GenerateurTest {

    private Generateur g;
    private Maison m1;
    private Maison m2;

    @BeforeEach
    void setUp() {
        g = new Generateur("G1", 100);
        m1 = new Maison("M1", TypeMaison.NORMAL); // 20 kW
        m2 = new Maison("M2", TypeMaison.FORTE);  // 40 kW
    }

    // ---------------- CONSTRUCTEURS ----------------

    @Test
    void testConstructeur() {
        assertEquals("G1", g.getNom());
        assertEquals(100, g.getCapaciteMax());
        assertEquals(0, g.getChargeActuelle());
        assertTrue(g.getMaisonsConnectees().isEmpty());
    }

    @Test
    void testConstructeurCopie() {
        g.ajouterMaison(m1);
        Generateur copie = new Generateur(g);

        assertEquals("G1", copie.getNom());
        assertEquals(100, copie.getCapaciteMax());
        assertEquals(0, copie.getChargeActuelle());
        assertTrue(copie.getMaisonsConnectees().isEmpty());
    }

    // ---------------- AJOUT / RETRAIT ----------------

    @Test
    void testAjouterMaison() {
        g.ajouterMaison(m1);

        assertEquals(1, g.getMaisonsConnectees().size());
        assertEquals(20, g.getChargeActuelle());
        assertTrue(g.getMaisonsConnectees().contains(m1));
    }

    @Test
    void testAjouterMaisonDeuxFois() {
        g.ajouterMaison(m1);
        g.ajouterMaison(m1);

        assertEquals(1, g.getMaisonsConnectees().size());
        assertEquals(20, g.getChargeActuelle());
    }

    @Test
    void testRetirerMaison() {
        g.ajouterMaison(m1);
        g.retirerMaison(m1);

        assertEquals(0, g.getChargeActuelle());
        assertTrue(g.getMaisonsConnectees().isEmpty());
    }

    @Test
    void testRetirerMaisonAbsente() {
        g.retirerMaison(m1);

        assertEquals(0, g.getChargeActuelle());
        assertTrue(g.getMaisonsConnectees().isEmpty());
    }

    // ---------------- CHARGE & TAUX ----------------

    @Test
    void testChargeAvecPlusieursMaisons() {
        g.ajouterMaison(m1); // 20
        g.ajouterMaison(m2); // +40

        assertEquals(60, g.getChargeActuelle());
    }

    @Test
    void testTauxUtilisation() {
        g.ajouterMaison(m1);
        g.ajouterMaison(m2);

        assertEquals(0.6, g.getTauxUtilisation(), 0.0001);
    }
    
    @Test
    void testTauxUtilisationCapaciteZero() {
        Generateur gZero = new Generateur("G_Zero", 0);
        assertEquals(0.0, gZero.getTauxUtilisation());
    }

    // ---------------- SETTERS ----------------

    @Test
    void testSetCapaciteMax() {
        g.setCapaciteMax(200);
        assertEquals(200, g.getCapaciteMax());
    }

    // ---------------- TOSTRING ----------------

    @Test
    void testToStringSansMaison() {
        String s = g.toString();

        assertTrue(s.contains("G1"));
        assertTrue(s.contains("Aucune maison connectée"));
    }

    @Test
    void testToStringAvecMaisons() {
        g.ajouterMaison(m1);
        g.ajouterMaison(m2);

        String s = g.toString();

        assertTrue(s.contains("M1"));
        assertTrue(s.contains("M2"));
    }
}
