import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import algo.TypeAlgo;

public class TypeAlgoTest {

    /**
     * Test : vérifie que toutes les valeurs attendues sont présentes.
     */
	@Test
	void testValeursExistantes() {
	    TypeAlgo[] algos = TypeAlgo.values();

	    assertEquals(5, algos.length,
	            "Le nombre d'algorithmes doit être de 5");

	    // Vérifie que tous les algos attendus sont présents (peu importe l'ordre)
	    List<TypeAlgo> listeAlgos = Arrays.asList(algos);
	    assertTrue(listeAlgos.contains(TypeAlgo.NAIF));
	    assertTrue(listeAlgos.contains(TypeAlgo.RECUIT));
	    assertTrue(listeAlgos.contains(TypeAlgo.GLOUTON));
	    assertTrue(listeAlgos.contains(TypeAlgo.ILS));
	    assertTrue(listeAlgos.contains(TypeAlgo.LAHC));
	}

    /**
     * Test : vérifie que valueOf fonctionne pour chaque algorithme.
     */
    @Test
    void testValueOf() {
        assertEquals(TypeAlgo.NAIF, TypeAlgo.valueOf("NAIF"));
        assertEquals(TypeAlgo.RECUIT, TypeAlgo.valueOf("RECUIT"));
        assertEquals(TypeAlgo.GLOUTON, TypeAlgo.valueOf("GLOUTON"));
        assertEquals(TypeAlgo.ILS, TypeAlgo.valueOf("ILS"));
        assertEquals(TypeAlgo.LAHC, TypeAlgo.valueOf("LAHC"));
    }

    /**
     * Test : valueOf doit lever une exception pour une valeur inconnue.
     */
    @Test
    void testValueOfInvalide() {
        assertThrows(IllegalArgumentException.class, () -> {
            TypeAlgo.valueOf("inconnu");
        });
    }


}
