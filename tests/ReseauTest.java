import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import modele.Reseau;
import modele.ReseauException;
import modele.TypeMaison;

public class ReseauTest {

    private Reseau reseau;

    @BeforeEach
    void setUp() {
        reseau = new Reseau();
    }

    // ---------------- CONSTRUCTEUR / GETTERS ----------------

    @Test
    void testReseauInitialementVide() {
        assertTrue(reseau.getGenerateurs().isEmpty());
        assertTrue(reseau.getMaisons().isEmpty());
    }

    // ---------------- AJOUT GENERATEUR ----------------

    @Test
    void testAjouterGenerateur() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        assertNotNull(reseau.chercherGenerateur("G1"));
        assertEquals(100, reseau.chercherGenerateur("G1").getCapaciteMax());
    }

    @Test
    void testMettreAJourGenerateur() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterGenerateur("G1", 200);
        assertEquals(200, reseau.chercherGenerateur("G1").getCapaciteMax());
    }

    // ---------------- AJOUT MAISON ----------------

    @Test
    void testAjouterMaison() throws ReseauException {
        reseau.ajouterMaison("M1", TypeMaison.BASSE);
        assertNotNull(reseau.chercherMaison("M1"));
        assertEquals(TypeMaison.BASSE, reseau.chercherMaison("M1").getType());
    }

    @Test
    void testMettreAJourMaison() throws ReseauException {
        reseau.ajouterMaison("M1", TypeMaison.BASSE);
        reseau.ajouterMaison("M1", TypeMaison.FORTE);

        assertEquals(TypeMaison.FORTE, reseau.chercherMaison("M1").getType());
    }

    // ---------------- CONNEXION / DECONNEXION ----------------

    @Test
    void testConnexionMaisonGenerateur() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterMaison("M1", TypeMaison.NORMAL);
        reseau.connecter("M1", "G1");

        assertEquals(reseau.chercherGenerateur("G1"),
                     reseau.chercherMaison("M1").getGenerateurAssocie());
    }

    @Test
    void testDeconnexionMaisonGenerateur() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterMaison("M1", TypeMaison.NORMAL);
        reseau.connecter("M1", "G1");

        reseau.deconnecter("M1", "G1");

        assertNull(reseau.chercherMaison("M1").getGenerateurAssocie());
        assertEquals(0, reseau.chercherGenerateur("G1").getChargeActuelle());
    }

    // ---------------- VERIFICATIONS ----------------

    @Test
    void testVerificationReseauOK() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterMaison("M1", TypeMaison.BASSE);
        reseau.connecter("M1", "G1");

        assertDoesNotThrow(() -> reseau.verificationReseau());
    }

    @Test
    void testVerificationCapaciteInsuffisante() throws ReseauException {
        reseau.ajouterGenerateur("G1", 10);
        reseau.ajouterMaison("M1", TypeMaison.FORTE);
        reseau.connecter("M1", "G1");

        assertThrows(ReseauException.class, () -> reseau.verificationReseau());
    }
    
    @Test
    void testVerificationMaisonNonConnecte() throws ReseauException {
        reseau.ajouterGenerateur("G1", 10);
        reseau.ajouterMaison("M1", TypeMaison.FORTE);

        assertThrows(ReseauException.class, () -> reseau.verificationReseau());
    }
    
    @Test
    void testVerificationAucuneMaison() throws ReseauException {
        reseau.ajouterGenerateur("G1", 10);

        assertFalse(reseau.verifierNonVide());
    }
    
    @Test
    void testVerificationAucunGenerateur() throws ReseauException {
        reseau.ajouterMaison("M1", TypeMaison.FORTE);

        assertFalse(reseau.verifierNonVide());
    }

    // ---------------- CALCULS ----------------

    @Test
    void testCalculDispersionVide() {
        assertEquals(0, reseau.calculerDispersion());
    }

    @Test
    void testCalculSurcharge() throws ReseauException {
        reseau.ajouterGenerateur("G1", 10);
        reseau.ajouterMaison("M1", TypeMaison.FORTE);
        reseau.connecter("M1", "G1");

        assertTrue(reseau.calculerSurcharge() > 0);
    }

    @Test
    void testCalculCout() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterMaison("M1", TypeMaison.NORMAL);
        reseau.connecter("M1", "G1");

        double cout = reseau.calculerCout(5);
        assertTrue(cout >= 0);
    }

    // ---------------- COPIE PROFONDE ----------------

    @Test
    void testCopieReseauIndependante() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterMaison("M1", TypeMaison.BASSE);
        reseau.connecter("M1", "G1");

        Reseau copie = reseau.copier();

        copie.deconnecter("M1", "G1");

        assertNotNull(reseau.chercherMaison("M1").getGenerateurAssocie());
        assertNull(copie.chercherMaison("M1").getGenerateurAssocie());
    }

    // ---------------- OPTIMISATIONS ----------------

    @Test
    void testOptimiserReseauNaif() throws ReseauException {
        reseau.ajouterGenerateur("G1", 50);
        reseau.ajouterGenerateur("G2", 50);
        reseau.ajouterMaison("M1", TypeMaison.NORMAL);
        reseau.ajouterMaison("M2", TypeMaison.NORMAL);

        reseau.connecter("M1", "G1");
        reseau.connecter("M2", "G2");

        reseau.optimiserNaif(10, 5);

        assertTrue(reseau.calculerCout(5) >= 0);
        assertNotNull(reseau.chercherMaison("M1").getGenerateurAssocie());
        assertNotNull(reseau.chercherMaison("M2").getGenerateurAssocie());
    }

    @Test
    void testOptimiserReseauGlouton() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterGenerateur("G2", 100);
        reseau.ajouterMaison("M1", TypeMaison.FORTE);
        reseau.ajouterMaison("M2", TypeMaison.BASSE);

        reseau.optimiserReseauGlouton(10);

        assertTrue(reseau.calculerCout(5) >= 0);
        assertNotNull(reseau.chercherMaison("M1").getGenerateurAssocie());
        assertNotNull(reseau.chercherMaison("M2").getGenerateurAssocie());
    }

    @Test
    void testOptimiserILS() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterGenerateur("G2", 100);
        reseau.ajouterMaison("M1", TypeMaison.NORMAL);
        reseau.ajouterMaison("M2", TypeMaison.NORMAL);

        reseau.connecter("M1", "G1");
        reseau.connecter("M2", "G2");

        reseau.optimiserILS(200, 5);

        assertTrue(reseau.calculerCout(5) >= 0);
        assertNotNull(reseau.chercherMaison("M1").getGenerateurAssocie());
        assertNotNull(reseau.chercherMaison("M2").getGenerateurAssocie());
    }

    @Test
    void testOptimiserLAHC() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterGenerateur("G2", 100);
        reseau.ajouterMaison("M1", TypeMaison.NORMAL);
        reseau.ajouterMaison("M2", TypeMaison.NORMAL);

        reseau.connecter("M1", "G1");
        reseau.connecter("M2", "G2");

        reseau.optimiserLAHC(100, 5);

        assertTrue(reseau.calculerCout(5) >= 0);
        assertNotNull(reseau.chercherMaison("M1").getGenerateurAssocie());
        assertNotNull(reseau.chercherMaison("M2").getGenerateurAssocie());
    }

    /* =====================================================
     * CONNEXION – MAISON OU GENERATEUR INTROUVABLE
     * ===================================================== */

    @Test
    void testConnexionMaisonIntrouvable() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);

        assertThrows(ReseauException.class, () -> {
            reseau.connecter("M_INCONNUE", "G1");
        });
    }

    @Test
    void testConnexionGenerateurIntrouvable() throws ReseauException {
        reseau.ajouterMaison("M1", TypeMaison.BASSE);

        assertThrows(ReseauException.class, () -> {
            reseau.connecter("M1", "G_INCONNU");
        });
    }

    /* =====================================================
     * CONNEXION – GENERATEUR DEJA CONNECTE A LA MAISON
     * ===================================================== */

    @Test
    void testConnexionMaisonDejaConnecteeMemeGenerateur() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterMaison("M1", TypeMaison.NORMAL);

        reseau.connecter("M1", "G1");
        
        assertThrows(ReseauException.class, () -> {
            reseau.connecter("M1", "G1");
        });
    }

    /* =====================================================
     * CONNEXION – MAISON DEJA CONNECTEE A UN AUTRE GENERATEUR
     * ===================================================== */

    @Test
    void testConnexionMaisonDejaConnecteeAutreGenerateur() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterGenerateur("G2", 100);
        reseau.ajouterMaison("M1", TypeMaison.NORMAL);

        reseau.connecter("M1", "G1");

        assertThrows(ReseauException.class, () -> {
            reseau.connecter("M1", "G2");
        });
    }

    /* =====================================================
     * DECONNEXION – MAISON OU GENERATEUR INTROUVABLE
     * ===================================================== */

    @Test
    void testDeconnexionMaisonIntrouvable() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterMaison("M1", TypeMaison.FORTE);
        reseau.connecter("M1", "G1");
        
        assertThrows(ReseauException.class, () -> {
            reseau.deconnecter("M_INCONNUE", "G1");
        });
    }

    @Test
    void testDeconnexionGenerateurIntrouvable() throws ReseauException {
    	reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterMaison("M1", TypeMaison.FORTE);
        reseau.connecter("M1", "G1");
        
        assertThrows(ReseauException.class, () -> {
            reseau.deconnecter("M1", "G_INCONNU");
        });
    }
	    
	    
    /* =====================================================
     * TESTS ADDITIONNELS
     * ===================================================== */
    
    /**
     * Test : Erreur si on ajoute un générateur avec un nom déjà pris par une maison.
     */
    @Test
    void testAjoutGenerateurNomPrisParMaison() throws ReseauException {
        reseau.ajouterMaison("M1", TypeMaison.BASSE);
        
        assertThrows(ReseauException.class, () -> {
            reseau.ajouterGenerateur("M1", 100);
        });
    }

    /**
     * Test : Erreur si on ajoute une maison avec un nom déjà pris par un générateur.
     */
    @Test
    void testAjoutMaisonNomPrisParGenerateur() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        
        assertThrows(ReseauException.class, () -> {
            reseau.ajouterMaison("G1", TypeMaison.BASSE);
        });
    }

    /**
     * Test : Mise à jour d'une maison DÉJÀ connectée.
     * Vérifie que la charge du générateur s'actualise (Retrait ancienne -> Ajout nouvelle).
     */
    @Test
    void testMiseAJourMaisonConnectee() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterMaison("M1", TypeMaison.BASSE); // Consomme 10
        reseau.connecter("M1", "G1");
        
        // Vérif avant
        assertEquals(10.0, reseau.chercherGenerateur("G1").getChargeActuelle());

        // Mise à jour : BASSE (10) -> FORTE (40)
        reseau.ajouterMaison("M1", TypeMaison.FORTE); 

        // Le générateur doit avoir vu sa charge passer de 10 à 40
        assertEquals(40.0, reseau.chercherGenerateur("G1").getChargeActuelle());
        assertEquals(TypeMaison.FORTE, reseau.chercherMaison("M1").getType());
    }

    /**
     * Test : Connexion avec ordre inversé (Générateur, Maison).
     * Couvre les "if" dans connecterChercherMaison/Generateur.
     */
    @Test
    void testConnexionOrdreInverse() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterMaison("M1", TypeMaison.NORMAL);
        
        // On donne (G, M) au lieu de (M, G)
        reseau.connecter("G1", "M1"); 
        
        assertNotNull(reseau.chercherMaison("M1").getGenerateurAssocie());
    }
    
    /**
     * Test : Déconnexion avec ordre inversé.
     */
    @Test
    void testDeconnexionOrdreInverse() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterMaison("M1", TypeMaison.NORMAL);
        reseau.connecter("M1", "G1");
        
        // On déconnecte en donnant (G, M)
        reseau.deconnecter("G1", "M1");
        
        assertNull(reseau.chercherMaison("M1").getGenerateurAssocie());
    }

    /**
     * Test : Déconnexion d'une maison connectée ailleurs (Erreur).
     * Couvre le "else if (ancienG != null)" de la méthode deconnecter.
     */
    @Test
    void testDeconnexionMaisonConnecteeAilleurs() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterGenerateur("G2", 100);
        reseau.ajouterMaison("M1", TypeMaison.NORMAL);
        
        reseau.connecter("M1", "G1"); // Connectée à G1
        
        // On essaie de la déconnecter de G2
        assertThrows(ReseauException.class, () -> {
            reseau.deconnecter("M1", "G2");
        });
    }

    /**
     * Test : Déconnexion d'une maison qui n'est connectée à rien (Erreur).
     * Couvre le dernier "else" de la méthode deconnecter.
     */
    @Test
    void testDeconnexionMaisonNonConnectee() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterMaison("M1", TypeMaison.NORMAL);
        // Pas de connexion
        
        assertThrows(ReseauException.class, () -> {
            reseau.deconnecter("M1", "G1");
        });
    }

    /**
     * Test : Glouton sur réseau déjà connecté.
     * Couvre la boucle de nettoyage au début de "optimiserReseauGlouton".
     */
    @Test
    void testOptimiserGloutonAvecConnexionsExistantes() throws ReseauException {
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterMaison("M1", TypeMaison.NORMAL);
        reseau.connecter("M1", "G1"); // Déjà connectée

        // L'algo doit d'abord déconnecter M1 avant de recalculer
        reseau.optimiserReseauGlouton(10); 
        
        assertNotNull(reseau.chercherMaison("M1").getGenerateurAssocie());
    }

    /**
     * Test : Affichage toString complet.
     * Couvre les branches d'affichage (Générateurs, Maisons connectées ou non).
     */
    @Test
    void testToStringComplet() throws ReseauException {
        // Cas 1 : Réseau vide
        assertNotNull(reseau.toString());

        // Cas 2 : Réseau avec générateurs, connexions et orphelins
        reseau.ajouterGenerateur("G1", 100);
        reseau.ajouterMaison("M1_Connectee", TypeMaison.BASSE);
        reseau.ajouterMaison("M2_Orpheline", TypeMaison.FORTE);
        
        reseau.connecter("M1_Connectee", "G1");

        String affichage = reseau.toString();
        assertNotNull(affichage);
        // On vérifie que le texte contient des mots clés attendus
        assertTrue(affichage.contains("G1"));
        assertTrue(affichage.contains("M1_Connectee"));
        assertTrue(affichage.contains("M2_Orpheline")); // Vérifie l'affichage des non-connectées
    }

}
