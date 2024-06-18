package gameresults;

import gameresult.OnePlayerGameResult;
import gameresult.manager.json.JsonOnePlayerGameResultManager;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.tinylog.Logger;
import puzzle.game.PuzzleController;
import util.DurationUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class GameResultController {
    private static final int MAX_NUMBER_OF_ROWES = 10;

    @FXML
    private TableView<OnePlayerGameResult> tableView;

    @FXML
    private TableColumn<OnePlayerGameResult, String> playerName;

    @FXML
    private TableColumn<OnePlayerGameResult, Integer> stepCount;

    @FXML
    private TableColumn<OnePlayerGameResult, String> durationSec;

    @FXML
    private TableColumn<OnePlayerGameResult, String> createdTime;

    @FXML
    private void initialize() throws IOException {
        playerName.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        stepCount.setCellValueFactory(new PropertyValueFactory<>("numberOfMoves"));
        durationSec.setCellValueFactory(
                data -> {
                    var duration = data.getValue().getDuration();
                    return new ReadOnlyStringWrapper(DurationUtil.formatDuration(duration));
                }
        );
        createdTime.setCellValueFactory(
                data -> {
                    var date = data.getValue().getCreated();
                    var formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
                    return new ReadOnlyStringWrapper(formatter.format(date));
                }
        );
        ObservableList<OnePlayerGameResult> observableList = FXCollections.observableArrayList();
        observableList.addAll(new JsonOnePlayerGameResultManager(Path.of("gameresult.json"))
                .getBestByNumberOfMoves(MAX_NUMBER_OF_ROWES));
        tableView.setItems(observableList);
    }

    @FXML
    private void backToLogin(ActionEvent event) throws IOException {
        Logger.info("Back to login screen button pressed.");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.setTitle("Login screen");
        stage.show();
    }
}
