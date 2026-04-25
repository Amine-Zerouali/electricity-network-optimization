import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import modele.TypeMaison;

public class TypeMaisonTest {

    /**
     * Test 1 : Vérifie la méthode générée values().
     * Cela couvre implicitement la création de l'enum (les constructeurs).
     */
    @Test
    void testValeursEnum() {
        TypeMaison[] types = TypeMaison.values();

        assertEquals(3, types.length, "Il doit y avoir exactement 3 types de maison");

        // Vérifie que tous les éléments sont présents dans l'ordre
        assertArrayEquals(
            new TypeMaison[] { TypeMaison.BASSE, TypeMaison.NORMAL, TypeMaison.FORTE },
            types
        );
    }

    /**
     * Test 2 : Vérifie la méthode getConsommation() (Getter).
     * Couvre le champ 'consommation' et son retour.
     */
    @Test
    void testConsommation() {
        assertEquals(10.0, TypeMaison.BASSE.getConsommation());
        assertEquals(20.0, TypeMaison.NORMAL.getConsommation());
        assertEquals(40.0, TypeMaison.FORTE.getConsommation());
    }

    /**
     * Test 3 : Vérifie la méthode générée valueOf() avec des valeurs correctes.
     * Couvre la recherche par nom.
     */
    @Test
    void testValueOf() {
        assertEquals(TypeMaison.BASSE, TypeMaison.valueOf("BASSE"));
        assertEquals(TypeMaison.NORMAL, TypeMaison.valueOf("NORMAL"));
        assertEquals(TypeMaison.FORTE, TypeMaison.valueOf("FORTE"));
    }

    /**
     * Test 4 : Vérifie la robustesse (Gestion d'erreur).
     * Vérifie que valueOf() plante bien si on lui donne n'importe quoi.
     */
    @Test
    void testValueOfInvalide() {
        assertThrows(IllegalArgumentException.class, () -> {
            TypeMaison.valueOf("inconnu");
        });
    }
}
