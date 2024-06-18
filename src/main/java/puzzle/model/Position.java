package puzzle.model;

/**
 * Reprezents the position.
 * @param row - the row of the board.
 * @param col - the col of the board.
 */

public record Position(int row, int col) {
    @Override
    public String toString() {
        return String.format("(%d, %d)", row, col);
    }
}
