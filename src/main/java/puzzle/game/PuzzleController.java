package puzzle.game;

import gameresult.OnePlayerGameResult;
import gameresult.manager.GameResultManager;
import gameresult.manager.OnePlayerGameResultManager;
import gameresult.manager.json.JsonOnePlayerGameResultManager;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import login.LoginController;
import lombok.SneakyThrows;
import puzzle.model.Position;
import puzzle.model.PuzzleModel;
import puzzle.model.Square;
import puzzle.util.TwoPhaseMoveSelector;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLData;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Stack;


import static puzzle.util.TwoPhaseMoveSelector.Phase.READY_TO_MOVE;

import org.tinylog.Logger;
import util.javafx.EnumImageStorage;
import util.javafx.ImageStorage;

/**
 * Controller class for the puzzle game.
 */
public class PuzzleController {

    @FXML
    private GridPane board;

    @FXML
    private TextField movesNumberField;

    @FXML
    private Text text;

    private final StringProperty name = new SimpleStringProperty();

    /**
     * Returns the player's name.
     * @return the player's name.
     */
    public String getName() {
        return name.get();
    }

    /**
     * Sets the player's name.
     * @param name the player's name.
     */
    public void setName(String name) {
        this.name.set(name);
    }

    private PuzzleModel model = new PuzzleModel();

    private final IntegerProperty numberOfMoves = new SimpleIntegerProperty(0);

    private final LocalDateTime startTime = LocalDateTime.now();

    /**
     * Initializes the controller and the game board.
     */
    @FXML
    private void initialize() throws IOException {
        for(var i = 0; i < board.getRowCount(); i++){
            for(var j = 0; j < board.getColumnCount(); j++){
                var square = createSquare(i, j);
                board.add(square, j, i);
            }
        }
        //selector.phasesProperty().addListener(this::showSelectionPhaseChange);
        model.gameOverProperty().addListener(this::handleGameOver);
        movesNumberField.textProperty().bind(model.numberOfMovesProperty().asString());
        text.textProperty().bind(Bindings.concat(name));
    }

    /**
     * Binds the {@code numberOfMoves} to the {@code movesNumberField}.
     */
    private void bindNumberOfMoves(){
        movesNumberField.textProperty().bind(numberOfMoves.asString());
    }

    /**
     * Creates a square on the game board.
     * @param i the row index.
     * @param j the column index.
     * @return the created StackPane representing a square.
     */
    private StackPane createSquare(int i, int j){
        var square = new StackPane();
        square.getStyleClass().add("square");
        var circle = new Circle(20);
        var coins = new Circle(10);
        var wall = new Rectangle(63, 63);

        circle.fillProperty().bind(createSquareBinding(model.squareProperty(i, j)));
        coins.fillProperty().bind(createSquareBindingCoin(model.squareProperty(i, j)));
        wall.fillProperty().bind(createSquareBindingWall(model.squareProperty(i, j)));

        square.getChildren().add(circle);
        square.getChildren().add(coins);
        square.getChildren().add(wall);
        square.setOnMouseClicked(this::handleMouseClick);
        return square;
    }

    /**
     * Handles the mouse click on the game board.
     * @param event the {@code MouseEvent}.
     */
    @FXML
    private void handleMouseClick(MouseEvent event){
        var square = (StackPane) event.getSource();
        var row = GridPane.getRowIndex(square);
        var col = GridPane.getColumnIndex(square);
        Logger.info("Click on square ({},{})", row, col);
        Position position = new Position(row, col);
        if(model.isLegalMove(position)){
            model.makeMove(position);
            Logger.info("Made move to ({},{})", row, col);
        }
        else{
            Logger.info("Invalid selection");
        }
    }

    /**
     * Handles the game over event.
     * @param observableValue observable value.
     * @param oldVal the old value.
     * @param newVal the new value.
     */
    @FXML
    private void handleGameOver(ObservableValue observableValue, boolean oldVal, boolean newVal){
        if(newVal) {
            Logger.info("The piece is on the finish square!");
            Logger.info("Puzzle solved in {} seconds!", ChronoUnit.SECONDS.between(model.startTime, LocalDateTime.now()));
            try {
                Path path = Path.of("gameresult.json");
                if (!Files.exists(path)){
                    Files.createFile(path);
                    Files.writeString(path, "[]");

                }
                GameResultManager manager = new JsonOnePlayerGameResultManager(Path.of("gameresult.json"));
                manager.add(createGameResult());
                Logger.info("Added game result to JSON file.");
            } catch (IOException e) {
                Logger.error("Failed to save game result: {}", e.getMessage());
            }
            Platform.runLater(this::showGameOverAlertAndExit);
        }
    }

    /**
     * Switches to game result screen by clicking the button.
     * @throws IOException if the {@code gameresult.fxml} cannot be loaded.
     */
    @FXML
    private void switchToGameResult() throws IOException{
        Logger.info("Switching to game result scene.");
        Parent root = FXMLLoader.load(getClass().getResource("/gameresult.fxml"));
        Stage stage = (Stage) board.getScene().getWindow();
        stage.setTitle("Game Results");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Creates a game result object that contains the details of the player's performance.
     * This method gathers the player's name, whether the puzzle of solved, the number of moves,
     * the duration of the game and the time when the object is created.
     * @return a {@link OnePlayerGameResult} object containing the game result details.
     */
    private OnePlayerGameResult createGameResult(){
        return OnePlayerGameResult.builder()
                .playerName(getName())
                .solved(true)
                .numberOfMoves((Integer) model.numberOfMovesProperty().get() + 1)
                .duration(Duration.ofSeconds(ChronoUnit.SECONDS.between(startTime, LocalDateTime.now())))
                .created(ZonedDateTime.now())
                .build();
    }

    /**
     * Shows a game over alert.
     * Shows 2 buttons.
     * Exit button: exits the application.
     * Game results button: switches to game result screen.
     */
    private void showGameOverAlertAndExit(){
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Winner!");
        alert.setContentText("The piece reached the finish!");

        ButtonType exitButton = new ButtonType("Exit");
        ButtonType resultsButton = new ButtonType("Game results");
        alert.getButtonTypes().setAll(exitButton, resultsButton);
        alert.showAndWait().ifPresent(button -> {
            if (button == exitButton){
                Logger.info("Clicked on exit, exiting...");
                Platform.exit();
            }else if(button == resultsButton){
                try {
                    switchToGameResult();
                }catch (IOException e){
                    Logger.error("Failed to switch to game results: {}", e.getMessage());
                }
            }
        });
    }

    /**
     * Creates a binding for the square's color property.
     * @param squareProperty the square property.
     * @return the object binding for the square's color.
     */
    private ObjectBinding<Paint> createSquareBinding(ReadOnlyObjectProperty<Square> squareProperty){
        return new ObjectBinding<Paint>() {
            {
                super.bind(squareProperty);
            }
            @Override
            protected Paint computeValue() {
                return switch (squareProperty.get()){
                    case NONE -> Color.TRANSPARENT;
                    case COIN -> Color.TRANSPARENT;
                    case WALL -> Color.TRANSPARENT;
                    case FINISH -> Color.BLUE;
                    case PIECE -> Color.GRAY;
                };
            }
        };
    }

    /**
     * Creates a binding for the square's color property.
     * @param squareProperty the square property.
     * @return the object binding for the coin's color.
     */
    private ObjectBinding<Paint> createSquareBindingCoin(ReadOnlyObjectProperty<Square> squareProperty) {
        return new ObjectBinding<Paint>() {
            {
                super.bind(squareProperty);
            }

            @Override
            protected Paint computeValue() {
                return switch (squareProperty.get()) {
                    case NONE -> Color.TRANSPARENT;
                    case COIN -> Color.ORANGE;
                    case WALL -> Color.TRANSPARENT;
                    case FINISH -> Color.TRANSPARENT;
                    case PIECE -> Color.TRANSPARENT;
                };
            }
        };
    }

    /**
     * Creates a binding for the square's color property.
     * @param squareProperty the square property.
     * @return the object binding for the wall's color.
     */
    private ObjectBinding<Paint> createSquareBindingWall(ReadOnlyObjectProperty<Square> squareProperty) {
        return new ObjectBinding<Paint>() {
            {
                super.bind(squareProperty);
            }

            @Override
            protected Paint computeValue() {
                return switch (squareProperty.get()) {
                    case NONE -> Color.TRANSPARENT;
                    case COIN -> Color.TRANSPARENT;
                    case WALL -> Color.BLACK;
                    case FINISH -> Color.TRANSPARENT;
                    case PIECE -> Color.TRANSPARENT;
                };
            }
        };
    }
}
