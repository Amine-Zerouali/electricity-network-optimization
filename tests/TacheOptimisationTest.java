import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import algo.TacheOptimisation;
import algo.TypeAlgo;
import modele.Reseau;
import modele.ReseauException;
import modele.TypeMaison;

public class TacheOptimisationTest {

    private Reseau reseauInitial;

    @BeforeEach
    void setUp() throws ReseauException {
        reseauInitial = new Reseau();
        reseauInitial.ajouterGenerateur("G1", 100);
        reseauInitial.ajouterGenerateur("G2", 100);
        reseauInitial.ajouterMaison("M1", TypeMaison.NORMAL);
        reseauInitial.ajouterMaison("M2", TypeMaison.FORTE);
        reseauInitial.connecter("G1", "M1");
        reseauInitial.connecter("G2", "M2");
    }

    /**
     * Test : la tâche retourne bien un réseau non nul après exécution.
     */
    @Test
    void testRunRetourneReseau() throws InterruptedException {
        TacheOptimisation tache = new TacheOptimisation(
                reseauInitial.copier(), 100, 10, TypeAlgo.NAIF);

        Thread t = new Thread(tache);
        t.start();
        t.join();

        assertNotNull(tache.getReseauResultat());
    }

    /**
     * Test : l'algorithme NAIF s'exécute sans exception.
     */
    @Test
    void testExecutionNaif() throws InterruptedException {
        TacheOptimisation tache = new TacheOptimisation(
                reseauInitial.copier(), 100, 10, TypeAlgo.NAIF);

        Thread t = new Thread(tache);
        assertDoesNotThrow(() -> {
            t.start();
            t.join();
        });
    }

    /**
     * Test : l'algorithme GLOUTON s'exécute sans exception.
     */
    @Test
    void testExecutionGlouton() throws InterruptedException {
        TacheOptimisation tache = new TacheOptimisation(
                reseauInitial.copier(), 50, 10, TypeAlgo.GLOUTON);

        Thread t = new Thread(tache);
        assertDoesNotThrow(() -> {
            t.start();
            t.join();
        });
    }

    /**
     * Test : l'algorithme RECUIT s'exécute sans exception.
     */
    @Test
    void testExecutionRecuit() throws InterruptedException {
        TacheOptimisation tache = new TacheOptimisation(
                reseauInitial.copier(), 50, 10, TypeAlgo.RECUIT);

        Thread t = new Thread(tache);
        assertDoesNotThrow(() -> {
            t.start();
            t.join();
        });
    }
    
    /**
     * Test : l'algorithme ILS s'exécute sans exception.
     */
    @Test
    void testExecutionILS() throws InterruptedException {
        TacheOptimisation tache = new TacheOptimisation(
                reseauInitial.copier(), 50, 10, TypeAlgo.ILS);

        Thread t = new Thread(tache);
        assertDoesNotThrow(() -> {
            t.start();
            t.join();
        });
    }
    
    /**
     * Test : l'algorithme LAHC s'exécute sans exception.
     */
    @Test
    void testExecutionLAHC() throws InterruptedException {
        TacheOptimisation tache = new TacheOptimisation(
                reseauInitial.copier(), 50, 10, TypeAlgo.LAHC);

        Thread t = new Thread(tache);
        assertDoesNotThrow(() -> {
            t.start();
            t.join();
        });
    }

    /**
     * Test : le type d'algorithme est correctement stocké.
     */
    @Test
    void testGetTypeAlgo() {
        TacheOptimisation tache = new TacheOptimisation(
                reseauInitial.copier(), 10, 5, TypeAlgo.ILS);

        assertEquals(TypeAlgo.ILS, tache.getTypeAlgo());
    }

    /**
     * Test : le coût après exécution est calculable (cohérence du réseau).
     */
    @Test
    void testCoutCalculableApresRun() throws InterruptedException {
        TacheOptimisation tache = new TacheOptimisation(
                reseauInitial.copier(), 100, 10, TypeAlgo.NAIF);

        Thread t = new Thread(tache);
        t.start();
        t.join();

        double cout = tache.getReseauResultat().calculerCout(10);

        assertTrue(cout >= 0);
    }
}
