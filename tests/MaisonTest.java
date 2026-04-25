import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import modele.Generateur;
import modele.Maison;
import modele.TypeMaison;

class MaisonTest {

    private Maison m;

    @BeforeEach
    void setUp() {
        m = new Maison("M1", TypeMaison.NORMAL);
    }

    // ---------------- CONSTRUCTEURS ----------------

    @Test
    void testConstructeur() {
        assertEquals("M1", m.getNom());
        assertEquals(TypeMaison.NORMAL, m.getType());
        assertNull(m.getGenerateurAssocie());
    }

    @Test
    void testConstructeurCopie() {
        Maison copie = new Maison(m);

        assertEquals(m.getNom(), copie.getNom());
        assertEquals(m.getType(), copie.getType());
        assertNull(copie.getGenerateurAssocie());
    }

    // ---------------- GETTERS ----------------

    @Test
    void testGetConsommation() {
        assertEquals(20, m.getConsommation());
    }

    // ---------------- SETTERS ----------------

    @Test
    void testSetType() {
        m.setType(TypeMaison.FORTE);
        assertEquals(TypeMaison.FORTE, m.getType());
        assertEquals(40, m.getConsommation());
    }

    @Test
    void testSetGenerateurAssocie() {
        Generateur g = new Generateur("G1", 100);
        m.setGenerateurAssocie(g);

        assertEquals(g, m.getGenerateurAssocie());
    }

    // ---------------- TOSTRING ----------------

    @Test
    void testToStringSansGenerateur() {
        String s = m.toString();

        assertTrue(s.contains("M1"));
        assertTrue(s.contains("NORMAL"));
        assertTrue(s.contains("20"));
        assertTrue(s.contains("non connectée"));
    }

    @Test
    void testToStringAvecGenerateur() {
        Generateur g = new Generateur("G1", 100);
        m.setGenerateurAssocie(g);

        String s = m.toString();

        assertTrue(s.contains("G1"));
    }
}
