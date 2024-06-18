package login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.tinylog.Logger;
import puzzle.game.PuzzleController;
import puzzle.model.PuzzleModel;

import java.io.IOException;

/**
 * Controller class for the login screen.
 */
public class LoginController {

    @FXML
    private TextField nameField;

    /**
     * Initializes the login screen.
     * Sets the {@code nameField} with the system's username.
     */
    @FXML
    private void initialize(){
        nameField.setText(System.getProperty("user.name"));
    }

    /**
     * Handles the switch to the game scene.
     * @param event the action event.
     * @throws IOException if loading the game scene fails.
     */
    @FXML
    private void switchScene(ActionEvent event) throws IOException{
        Logger.info("Play button clicked and player name added: {}", nameField.getText());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));
        Parent root = loader.load();
        PuzzleController controller = loader.getController();
        controller.setName(nameField.getText());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.setTitle("Puzzle game");
        stage.show();
    }

    /**
     * Handles the switch to the Game results screen.
     * @param event the action event.
     * @throws IOException if laoding the game fails.
     */
    @FXML
    private void switchToGameResults(ActionEvent event) throws IOException{
        Logger.info("Game results button clicked");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameresult.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.setTitle("Game results");
        stage.show();
    }
}
