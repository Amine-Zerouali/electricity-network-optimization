import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import algo.Optimiseur;
import modele.Reseau;
import modele.ReseauException;
import modele.TypeMaison;

/**
 * Tests unitaires de la classe Optimiseur.
 * 
 * Objectifs :
 * - Vérifier que l'exécution multi-thread ne plante pas
 * - Vérifier le contrat de retour (null ou réseau optimisé)
 * - Vérifier la cohérence des coûts
 */
public class OptimiseurTest {
	private Reseau r;
	/**
	 * Création d'un réseau simple pour les tests.
	 */
	@BeforeEach
	void init() throws ReseauException {
		r = new Reseau();

        r.ajouterGenerateur("G1", 100);
        r.ajouterGenerateur("G2", 80);

        r.ajouterMaison("M1", TypeMaison.BASSE);
        r.ajouterMaison("M2", TypeMaison.NORMAL);
        r.ajouterMaison("M3", TypeMaison.FORTE);

        r.connecter("M1", "G1");
        r.connecter("M2", "G1");
        r.connecter("M3", "G1");

    }

    /**
     * Test principal : lancerAlgos ne doit jamais lever d'exception.
     */
    @Test
    void testLancerAlgosSansException() {
        assertDoesNotThrow(() -> {
            Optimiseur.lancerAlgos(r, 50, 10);
        });
    }

    /**
     * Vérifie que le résultat est soit null, soit un réseau valide.
     */
    @Test
    void testRetourOpti() {
        Reseau resultat = Optimiseur.lancerAlgos(r, 50, 10);
        assertNotNull(resultat);
    }

    /**
     * Si une solution est trouvée, son coût doit être inférieur
     * ou égal au coût initial.
     */
    @Test
    void testCoutAmelioreOuEgal() {
        int lambda = 10;

        double coutInitial = r.calculerCout(lambda);
        Reseau resultat = Optimiseur.lancerAlgos(r, 50, lambda);

        double coutFinal = resultat.calculerCout(lambda);
        assertTrue(coutFinal <= coutInitial);
        
    }

    /**
     * Vérifie que si le reseau est deja optimisé, la méthode renvoie null.
     */
    @Test
    void testReseauDejaOpti() {
        int lambda = 10;
        r = Optimiseur.lancerAlgos(r, 50, lambda);
        r = Optimiseur.lancerAlgos(r, 50, lambda);
        assertNull(r);
    }

    /**
     * Test avec un nombre d'itérations minimal.
     */
    @Test
    void testAvecKMinimal() {
        Reseau resultat = Optimiseur.lancerAlgos(r, 1, 5);
        assertTrue(resultat == null || resultat instanceof Reseau);
    }
}
