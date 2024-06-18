package puzzle.model;

import javafx.beans.property.*;
import puzzle.*;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Reprezents the model and state of the puzzle.
 */
public class PuzzleModel implements State<Position>{

    /**
     * The size of the board.
     */
    public static final int BOARD_SIZE = 8;

    /**
     * The starting step size which the piece can take.
     */
    private int STEP_SIZE = 2;

    /**
     * An object wrapper which the puzzle and its square types are stored in.
     */
    private ReadOnlyObjectWrapper<Square>[][] board = new ReadOnlyObjectWrapper[BOARD_SIZE][BOARD_SIZE];
    /**
     * Initializes game over.
     */
    private ReadOnlyBooleanWrapper gameOver;
    /**
     * Counts the number of moves taken.
     */
    private ReadOnlyIntegerWrapper numberOfMoves;

    /**
     * Saves the current local date.
     */
    public LocalDateTime startTime = LocalDateTime.now();

    /**
     * Saves the position where the piece moved from.
     */
    private Position fromPosition = new Position(0, 0);

    /**
     * Creates a {@link PuzzleModel} object that is the original initial state of the puzzle.
     */
    public PuzzleModel() {
        for (var i = 0; i < BOARD_SIZE; i++) {
            for (var j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = new ReadOnlyObjectWrapper<Square>(
                        switch (i) {
                            case 0 -> switch (j) {
                                case 0 -> Square.PIECE;
                                case 4 -> Square.COIN;
                                default -> Square.NONE;
                            };
                            case 1 -> switch (j) {
                                case 2 -> Square.COIN;
                                case 6 -> Square.COIN;
                                default -> Square.NONE;
                            };
                            case 2 -> switch (j) {
                                case 2 -> Square.WALL;
                                case 7 -> Square.WALL;
                                default -> Square.NONE;
                            };
                            case 3 -> switch (j) {
                                case 2 -> Square.COIN;
                                case 4 -> Square.COIN;
                                default -> Square.NONE;
                            };
                            case 4 -> switch (j) {
                                case 0 -> Square.COIN;
                                case 1 -> Square.WALL;
                                case 3 -> Square.COIN;
                                case 7 -> Square.COIN;
                                default -> Square.NONE;
                            };
                            case 5 -> switch (j) {
                                case 3 -> Square.COIN;
                                case 5 -> Square.WALL;
                                case 6 -> Square.COIN;
                                default -> Square.NONE;
                            };
                            case 6 -> switch (j) {
                                case 2 -> Square.COIN;
                                case 7 -> Square.COIN;
                                default -> Square.NONE;
                            };
                            case 7 -> switch (j) {
                                case 0 -> Square.COIN;
                                case 3 -> Square.WALL;
                                case 7 -> Square.FINISH;
                                default -> Square.NONE;
                            };
                            default -> Square.NONE;
                        }
                );
            }
        }
        numberOfMoves = new ReadOnlyIntegerWrapper(0);
        gameOver = new ReadOnlyBooleanWrapper();
    }

    /**
     * Creates a copy of the board in its original state.
     * @return returns a {@link ReadOnlyObjectWrapper} type object which is the copy of the original state of the board.
     */
    public ReadOnlyObjectWrapper<Square>[][] makeBoardCopy(){
        ReadOnlyObjectWrapper<Square>[][] copyBoard = new ReadOnlyObjectWrapper[BOARD_SIZE][BOARD_SIZE];
        for (var i = 0; i < BOARD_SIZE; i++) {
            for (var j = 0; j < BOARD_SIZE; j++) {
                copyBoard[i][j] = new ReadOnlyObjectWrapper<Square>(
                        switch (i) {
                            case 0 -> switch (j) {
                                case 4 -> Square.COIN;
                                default -> Square.NONE;
                            };
                            case 1 -> switch (j) {
                                case 2 -> Square.COIN;
                                case 6 -> Square.COIN;
                                default -> Square.NONE;
                            };
                            case 2 -> switch (j) {
                                case 2 -> Square.WALL;
                                case 7 -> Square.WALL;
                                default -> Square.NONE;
                            };
                            case 3 -> switch (j) {
                                case 2 -> Square.COIN;
                                case 4 -> Square.COIN;
                                default -> Square.NONE;
                            };
                            case 4 -> switch (j) {
                                case 0 -> Square.COIN;
                                case 1 -> Square.WALL;
                                case 3 -> Square.COIN;
                                case 7 -> Square.COIN;
                                default -> Square.NONE;
                            };
                            case 5 -> switch (j) {
                                case 3 -> Square.COIN;
                                case 5 -> Square.WALL;
                                case 6 -> Square.COIN;
                                default -> Square.NONE;
                            };
                            case 6 -> switch (j) {
                                case 2 -> Square.COIN;
                                case 7 -> Square.COIN;
                                default -> Square.NONE;
                            };
                            case 7 -> switch (j) {
                                case 0 -> Square.COIN;
                                case 3 -> Square.WALL;
                                case 7 -> Square.FINISH;
                                default -> Square.NONE;
                            };
                            default -> Square.NONE;
                        }
                );
            }
        }
        return copyBoard;
    }

    /**
     * Returns the square property of the {@code board}.
     * @param i the row of the board.
     * @param j the col of the board.
     * @return the property of the property of the board's specified row and column.
     */
    public ReadOnlyObjectProperty<Square> squareProperty(int i, int j) {
        return board[i][j].getReadOnlyProperty();
    }

    /**
     * A getter which returns which type of {@code Square} is on this board position.
     * @param position a Position type object whose Square type will be gotten.
     * @return the Square type of the board's specified position.
     */
    public Square getSquare(Position position){
        return board[position.row()][position.col()].get();
    }

    /**
     * Sets the board's specified position's Square type.
     * @param position - a Position type object whose Square type will be changed.
     * @param square - a Square type object which the board's specified position will be set to.
     */
    public void setSquare(Position position, Square square){
        board[position.row()][position.col()].set(square);
    }


    /**
     * Make a move by updating the board state and the position of the piece.
     * If the piece moves to a coin, the step size is changed from 2 to 3 and vice versa.
     * If the piece moves to the finish, the game is marked as game over.
     * The number of moves is incremented with each move.
     * @param position the new position to move the piece to.
     */
    @Override
    public void makeMove(Position position) {
        ReadOnlyObjectWrapper<Square>[][] boardCopy = makeBoardCopy();
        if(isCoin(position)){
            STEP_SIZE = (STEP_SIZE == 2) ? 3 : 2;
        }
        if(isFinish(position)){
            gameOver.set(true);
        }
        if(boardCopy[fromPosition.row()][fromPosition.col()].get() == Square.COIN){
            setSquare(position, Square.PIECE);
            setSquare(fromPosition, Square.COIN);
            fromPosition = position;
        }else{
            setSquare(position, Square.PIECE);
            setSquare(fromPosition, Square.NONE);
            fromPosition = position;
        }
        numberOfMoves.set(numberOfMoves.get() + 1);
    }

    /**
     * Checks if the selected position is on the board.
     * @param position the selected position.
     * @return true if the selected position is on the board, false otherwise.
     */
    public boolean isOnBoard(Position position){
        return 0 <= position.row() && position.row() < BOARD_SIZE && 0 <= position.col() && position.col() < BOARD_SIZE;
    }

    /**
     * Checks if the selected position is a wall.
     * @param position the selected position.
     * @return true if the selected position is a wall, false otherwise.
     */
    public boolean isWall(Position position){
        return getSquare(position) == Square.WALL;
    }

    /**
     * Checks if the selected position is the finish.
     * @param position the selected position.
     * @return true if the selected position is the finish, false otherwise.
     */
    public boolean isFinish(Position position){
        return getSquare(position) == Square.FINISH;
    }

    /**
     * Checks if the selected position is a coin.
     * @param position the selected position.
     * @return true if the selected position is a coin, false otherwise.
     */
    public boolean isCoin(Position position){
        return getSquare(position) == Square.COIN;
    }

    /**
     * Checks if the selected position is the piece.
     * @param position the selected position.
     * @return true if the selected position is the piece, false otherwise.
     */
    public boolean isPiece(Position position){
        return getSquare(position) == Square.PIECE;
    }

    /**
     * Checks if the move is according to a legal step distance.
     * @param from the selected from position.
     * @param to the selected to position.
     * @return true if the step distance between the selected two positions are equal to the {@code STEP_SIZE} false otherwise.
     */
    public boolean isLegalStepDistance(Position from, Position to){
        var x = Math.abs(to.row() - from.row());
        var y = Math.abs(to.col() - from.col());
        return (x == STEP_SIZE && y == 0) || (x == 0 && y == STEP_SIZE );
    }


    /**
     * Checks if the move to the selected position is legal.
     * A move is legal if the position is on the board, the step distance is valid and the selected piece
     * is not a wall.
     * @param position the selected position.
     * @return true if the move is legal, false otherwise.
     */
    @Override
    public boolean isLegalMove(Position position) {
        return isOnBoard(position)
                && isLegalStepDistance(fromPosition, position)
                && !isWall(position);
    }


    /**
     * Makes a string from the board's square type's ordinal numbers.
     * @return a {@link StringBuilder} type object.
     */
    @Override
    public String toString() {
        var sb = new StringBuilder();
        for(var i = 0; i < BOARD_SIZE; i++){
            for(var j = 0; j < BOARD_SIZE; j++){
                sb.append(board[i][j].get().ordinal()).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * @return numberOfMoves wrapper's property.
     */
    public ReadOnlyIntegerProperty numberOfMovesProperty(){
        return numberOfMoves.getReadOnlyProperty();
    }

    /**
     * @return the gameOver wrapper's property.
     */
    public ReadOnlyBooleanProperty gameOverProperty(){
        return gameOver.getReadOnlyProperty();
    }

    /**
     * @return gameOver value.
     */
    @Override
    public boolean isSolved(){
        return gameOver.get();
    }


    /**
     * Gets all the legal moves from the current position of the piece.
     * @return a set of positions representing all legal moves.
     */
    @Override
    public Set<Position> getLegalMoves() {
        Set<Position> moves = new HashSet<>();
        Position positionOfPiece = null;

        for(var i = 0; i < BOARD_SIZE; i++){
            for (var j = 0; j < BOARD_SIZE; j++){
                if(isPiece(new Position(i, j))){
                    positionOfPiece = new Position(i, j);
                    break;
                }
            }
        }

        int[][] directions = {
                {STEP_SIZE, 0}, {-STEP_SIZE, 0},
                {0, STEP_SIZE}, {0, -STEP_SIZE}
        };

        for(var direction : directions){
            Position newPosition = new Position(
                    positionOfPiece.row() + direction[0],
                    positionOfPiece.col() + direction[1]);
            if(isLegalMove(newPosition)){
                moves.add(newPosition);
            }
        }
        return moves;
    }

    /**
     * Creates a deep copy of the current state of the puzzle model.
     * @return a new instance of {@link PuzzleModel} with the same state as the current model.
     */
    @Override
    public State<Position> clone() {
        PuzzleModel copy;
        try{
            copy = (PuzzleModel) super.clone();
        }catch (CloneNotSupportedException e){
            throw new AssertionError();
        }
        copy.numberOfMoves = new ReadOnlyIntegerWrapper(numberOfMoves.get());
        copy.gameOver = new ReadOnlyBooleanWrapper(gameOver.get());
        copy.board = new ReadOnlyObjectWrapper[BOARD_SIZE][BOARD_SIZE];
        for (var i = 0; i < BOARD_SIZE; i++){
            for (var j = 0; j < BOARD_SIZE; j++){
                copy.board[i][j] = new ReadOnlyObjectWrapper<>(board[i][j].get());
            }
        }
        return copy;
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }
        if(!(o instanceof PuzzleModel)){
            return false;
        }
        PuzzleModel that = (PuzzleModel) o;
        if(numberOfMoves.get() != that.numberOfMoves.get()){
            return false;
        }
        if(!Objects.equals(gameOver, that.gameOver)){
            return false;
        }
        for(var i = 0; i < BOARD_SIZE; i++){
            for (var j = 0; j < BOARD_SIZE; j++){
                if (board[i][j].get() != that.board[i][j].get()){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode(){
        var result =  Objects.hash(numberOfMoves.get(), gameOver.get());
        for(var i = 0; i < BOARD_SIZE; i++){
            for(var j = 0; j < BOARD_SIZE; j++){
                result = 31 * result + board[i][j].get().hashCode();
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String playerName = "ben";
        var model = new PuzzleModel();
        System.out.println(model);

    }
}
