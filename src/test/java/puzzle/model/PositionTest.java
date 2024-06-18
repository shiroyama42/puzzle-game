package puzzle.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tester for the Position class
 */

class PositionTest {

    Position position;

    /**
     * Sets up the position before each test.
     */
    @BeforeEach
    void setUp() {
        position = new Position(0, 0);
    }

    /**
     * Tests the toString() method.
     */
    @Test
    void testToString(){
        assertEquals("(0, 0)", position.toString());
    }
}