package puzzle.game;

import javafx.application.Application;
import puzzle.model.Position;
import puzzle.model.PuzzleModel;
import puzzle.solver.BreadthFirstSearch;

public class Main{
    public static void main(String[] args){
        BreadthFirstSearch<Position> bfs = new BreadthFirstSearch<>();
        bfs.solveAndPrintSolution(new PuzzleModel());
        Application.launch(PuzzleApplication.class, args);
    }
}
