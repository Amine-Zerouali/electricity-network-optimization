import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import modele.ReseauException;

/**
 * Tests unitaires de la classe ReseauException.
 */
public class ReseauExceptionTest {

    /**
     * Test du constructeur avec message simple.
     */
    @Test
    void testConstructeurMessageSimple() {
        ReseauException ex = new ReseauException("Erreur simple");

        assertEquals("Erreur simple", ex.getMessage());
    }

    /**
     * Test du constructeur avec message + numéro de ligne.
     */
    @Test
    void testConstructeurAvecLigne() {
        ReseauException ex = new ReseauException("Syntaxe invalide", 12);

        assertEquals("Erreur ligne 12 : Syntaxe invalide", ex.getMessage());
    }

    /**
     * Vérifie que ReseauException est bien une Exception.
     */
    @Test
    void testHeritageException() {
        ReseauException ex = new ReseauException("Test");

        assertTrue(ex instanceof Exception);
    }

    /**
     * Vérifie que l'exception peut être levée et attrapée.
     */
    @Test
    void testLeverEtAttraperException() {
        try {
            throw new ReseauException("Erreur levée");
        } catch (ReseauException e) {
            assertEquals("Erreur levée", e.getMessage());
        }
    }
}
