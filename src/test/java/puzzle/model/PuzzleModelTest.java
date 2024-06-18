package puzzle.model;

import javafx.beans.property.ReadOnlyObjectWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import puzzle.TwoPhaseMoveState;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tester for the PuzzleModel class
 */
class PuzzleModelTest {

    Position position1 = new Position(0, 0); // PIECE
    Position position2 = new Position(0, 2); // EMPTY
    Position position3 = new Position(0, 4); // COIN
    Position position4 = new Position(2, 2); // WALL
    Position position5 = new Position(7, 7); // FINISH
    PuzzleModel model = new PuzzleModel();

    Set<Position> legalMoves = new HashSet<>();

    /**
     * A method which adds 2 new legal moves to the Set
     * @param set a {@code Set<Position>} type {@code HashSet<>()}
     * @return the set that contains the legal moves
     */
    public Set<Position> legalMovesAdder(Set<Position> set){
        legalMoves.add(position2);
        legalMoves.add(new Position(2, 0));
        return legalMoves;
    }

    /**
     * Tests the {@code getSquare} method
     * It checks if the declares positions are truly those {@code Square} types written in the {@code assertEquals}
     */
    @Test
    void getSquare() {
        assertEquals(model.getSquare(position1), Square.PIECE);
        assertEquals(model.getSquare(position2), Square.NONE);
        assertEquals(model.getSquare(position3), Square.COIN);
        assertEquals(model.getSquare(position4), Square.WALL);
        assertEquals(model.getSquare(position5), Square.FINISH);
    }

    /**
     * Tests the {@code isOnBoard} method
     * Tests positions that are on the border, somewhere in the middle of the board
     * Also tests out of index numbers.
     */
    @Test
    void isOnBoard() {
        assertTrue(model.isOnBoard(position1));
        assertTrue(model.isOnBoard(position4));
        assertFalse(model.isOnBoard(new Position(8, 8)));
        assertFalse(model.isOnBoard(new Position(-1, -1)));
    }

    /**
     * Test for the {@code isFinish()} method
     * Tests if the declared positions are walls
     */
    @Test
    void isWall() {
        assertTrue(model.isWall(position4));
        assertFalse(model.isWall(position1));
    }

    /**
     * Test for the {@code isFinish()} method
     * Tests if the declared positions are finishes
     */
    @Test
    void isFinish() {
        assertTrue(model.isFinish(position5));
        assertFalse(model.isFinish(position1));
    }

    /**
     * Test for the {@code isCoin()} method
     * Tests if the declared positions are coins
     */
    @Test
    void isCoin() {
        assertTrue(model.isCoin(position3));
        assertFalse(model.isCoin(position1));
    }

    /**
     * Test for the {@code isPiece()} method
     * Tests if the declared positions are pieces
     */
    @Test
    void isPiece(){
        assertTrue(model.isPiece(position1));
        assertFalse(model.isPiece(position2));
    }

    /**
     * Test for the {@code isLegalStepDistance()} method
     * Tests if two positions' distances are legal step distances
     */
    @Test
    void isLegalStepDistance() {
        assertTrue(model.isLegalStepDistance(position1, position2));
        assertFalse(model.isLegalStepDistance(position1, position3));
    }


    /**
     * Test for the {@code isLegalMove()} method
     * Tests if it's legal to move from one declared position to another
     */
    @Test
    void isLegalMove() {
        assertTrue(model.isLegalMove(position2));
        assertFalse(model.isLegalMove(position3));
        assertFalse(model.isLegalMove(position4));
    }

    /**
     * Test for the {@code toString()} method
     * Tests if the gives string is equal to the model's {@code toString()}'s string
     */
    @Test
    void testToString() {
        assertEquals("3 0 0 0 1 0 0 0 \n" +
                "0 0 1 0 0 0 1 0 \n" +
                "0 0 2 0 0 0 0 2 \n" +
                "0 0 1 0 1 0 0 0 \n" +
                "1 2 0 1 0 0 0 1 \n" +
                "0 0 0 1 0 2 1 0 \n" +
                "0 0 1 0 0 0 0 1 \n" +
                "1 0 0 2 0 0 0 4 \n", model.toString());
    }

    /**
     * Test for the {@code isSolved()} method
     * Tests if the model in the original state is solved
     */
    @Test
    void isSolved() {
        assertFalse(model.isSolved());
    }

    /**
     * Test for the {@code getLegalMoves()} method
     * Tests if the model in the original state gives the same legal moves as the {@code legalMoves} set
     */
    @Test
    void getLegalMoves() {
        assertEquals(legalMovesAdder(legalMoves), model.getLegalMoves());
    }

    /**
     * Test for the {@code clone()} method
     * Tests if the model's clone is the equal to the model
     * Tests if the model's clone is the same as the model
     */
    @Test
    void testClone() {
        var clone = model.clone();
        assertFalse(clone.equals(model));
        assertNotSame(clone, model);
    }

    /**
     * Test for the {@code equals()} method
     */
    @Test
    void testEquals() {
        assertTrue(model.equals(model));
        var clone = model.clone();
        clone.makeMove(position2);
        assertFalse(clone.equals(model));

        assertFalse(model.equals(null));
        assertFalse(model.equals("aaaaaaa"));
    }

    /**
     * Test for the {@code hashCode()} method
     */
    @Test
    void testHashCode() {
        assertTrue(model.hashCode() == model.hashCode());
        assertTrue(model.hashCode() == model.clone().hashCode());
    }
}