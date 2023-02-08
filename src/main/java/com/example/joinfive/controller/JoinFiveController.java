package com.example.joinfive.controller;

import com.example.joinfive.model.GameModel;
import com.example.joinfive.model.ai.AlgorithmFactory;
import com.example.joinfive.model.ai.AlgorithmObserver;
import com.example.joinfive.model.ai.JoinFiveAlgorithm;
import com.example.joinfive.model.grid.Grid;
import com.example.joinfive.model.grid.Mode;
import com.example.joinfive.util.ScoreEntry;
import com.example.joinfive.util.ScoreSaveLoad;
import com.example.joinfive.view.GameCanvasView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static com.example.joinfive.util.TimeUtil.toStringRepresentation;

public class JoinFiveController {
    @FXML
    private TextField usernameField;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private ComboBox<String> playerOptions;

    @FXML
    private ComboBox<String> algorithmOptions;

    @FXML
    private ComboBox<String> gameModeOptions;

    @FXML
    private ComboBox<String> themeOptions;

    @FXML
    private Button simulateButton;

    @FXML
    private Button hintButton;

    @FXML
    private Canvas gameCanvas;
    //    private Grid grid;
    private GameCanvasView canvasView;
    private GameModel gameModel;

    @FXML
    private void displayHistory() {
        Dialog dialog = new Dialog();
        TableView historyTable = new TableView();
        TableColumn nameColumn = new TableColumn("Name");
        TableColumn scoreColumn = new TableColumn("Score");
        nameColumn.setCellValueFactory(
                new PropertyValueFactory<ScoreEntry, String>("username"));
        scoreColumn.setCellValueFactory(
                new PropertyValueFactory<ScoreEntry, Double>("score"));
        historyTable.getColumns().addAll(nameColumn, scoreColumn);
        dialog.getDialogPane().setContent(historyTable);
        try {
            List<ScoreEntry> history = ScoreSaveLoad.loadScores();
            for (ScoreEntry scoreEntry : history) {
                historyTable.getItems().add(scoreEntry);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().setStyle(themeOptions.getParent().getScene().getRoot().getStyle());
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.show();
    }

    @FXML
    private void displayBestGrid() {
        System.out.println("Best Grid");
        Grid bestGrid = gameModel.getBestGrid();

        if (bestGrid == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Join Five: Best Grid");
            alert.setContentText("Failed to load the best grid!");
            alert.showAndWait();
            return;
        }
        Dialog dialog = new Dialog();
        dialog.getDialogPane().setStyle(themeOptions.getParent().getScene().getRoot().getStyle());
        Canvas canvas = new Canvas();
        GameCanvasView gcv = new GameCanvasView(canvas);
        gcv.setTheme(canvasView.getTheme());
        BorderPane bp = new BorderPane();
        Label bestScoreLabel = new Label("Score : " + bestGrid.lines().size());
        bestScoreLabel.setFont(new Font("Arial", 30));
        bp.setTop(bestScoreLabel);
        bp.setCenter(canvas);
        dialog.getDialogPane().setContent(bp);
        ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        gcv.update(bestGrid, new LinkedList<>(), new LinkedList<>());
        dialog.show();
    }

    @FXML
    private void canvasMousePressed(MouseEvent me) {
        System.out.printf("(%f,%f)", me.getX(), me.getY());
        double tolerance = 0.2;
        double tempX = (me.getX() - canvasView.getOffX()) / canvasView.getCellWidth();
        double tempY = (me.getY() - canvasView.getOffY()) / canvasView.getCellHeight();
        double frX = tempX - (int) tempX;
        double frY = tempY - (int) tempY;
        if ((frX > tolerance && frX < 1 - tolerance) || (frY > tolerance && frY < 1 - tolerance)) {
            System.out.println("INVALID");
            return;
        }
        int gridX = (int) Math.round(tempX);
        int gridY = (int) Math.round(tempY);
        if (!gameModel.isComputerPlaying() && gameModel.getPlayerName() == null) {
            gameModel.setPlayerName(usernameField.getText().trim());
        }
        gameModel.handleHumanMove(gridX, gridY);
    }

    public void start() {
        canvasView = new GameCanvasView(gameCanvas);
        gameCanvas.setFocusTraversable(true);
        gameModel.addGameObserver(canvasView);
        gameModel.addScoreObserver(score -> Platform.runLater(() -> scoreLabel.setText((gameModel.isGameOver() ? "Game Over: " : "Score: ") + score)));
//        hintButton.getScene().getWindow().setWidth(gameCanvas.getWidth() + 20);
//        hintButton.getScene().getWindow().setHeight(gameCanvas.getHeight() + 20);
        setupOptions();
        reset();
    }

    private void setupOptions() {

        themeOptions.getItems().addAll("Light", "Dark");
        themeOptions.getSelectionModel().select("Dark");
        themeOptions.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (oldValue.equals(newValue)) return;
            if (newValue.equalsIgnoreCase("Dark")) {
                canvasView.setTheme(GameCanvasView.DARK_THEME);
                themeOptions.getParent().getScene().getRoot().setStyle("-fx-base:black");
            } else if (newValue.equalsIgnoreCase("Light")) {
                canvasView.setTheme(GameCanvasView.LIGHT_THEME);
                themeOptions.getParent().getScene().getRoot().setStyle("");
            }
            gameModel.updateObservers();
        });
        canvasView.setTheme(GameCanvasView.DARK_THEME);
        themeOptions.getParent().getScene().getRoot().setStyle("-fx-base:black");

        playerOptions.getItems().removeAll(playerOptions.getItems());
        playerOptions.getItems().addAll("Human", "Computer");
        playerOptions.getSelectionModel().select("Human");

        algorithmOptions.getItems().removeAll(algorithmOptions.getItems());
        algorithmOptions.getItems().addAll(AlgorithmFactory.availableAlgorithmNames());
        algorithmOptions.getSelectionModel().select(0);

        gameModeOptions.getItems().removeAll(gameModeOptions.getItems());
        gameModeOptions.getItems().addAll("5T", "5D");
        gameModeOptions.getSelectionModel().select("5D");
        algorithmOptions.setDisable(true);
        simulateButton.setDisable(true);
    }

    @FXML
    private void playerChanged() {
        algorithmOptions.setDisable(!playerOptions.getSelectionModel().getSelectedItem().equals("Computer"));
        simulateButton.setDisable(!playerOptions.getSelectionModel().getSelectedItem().equals("Computer"));
    }

    @FXML
    private void reset() {
        gameModel.init();
        Mode mode = gameModeOptions.getSelectionModel().getSelectedItem().equalsIgnoreCase("5T") ? Mode.FIVE_T : Mode.FIVE_D;
        gameModel.setGameMode(mode);
    }

    @FXML
    private void simulate() {
        if (gameModel.isComputerPlaying()) {
            gameModel.computerStop();
            System.out.println("Simulation Stopped");
            simulateButton.setText("Simulate");
            return;
        }

        simulateButton.setText("Stop");
        playerOptions.setDisable(true);
        algorithmOptions.setDisable(true);
        gameModeOptions.setDisable(true);
        String algorithmName = algorithmOptions.getSelectionModel().getSelectedItem().trim();
        JoinFiveAlgorithm algorithm = AlgorithmFactory.createAlgorithm(algorithmName);
        assert algorithm != null;
        gameModel.computerPlay(algorithm,
                new AlgorithmObserver() {
                    @Override
                    public void done() {
                        simulationStopped();
                    }

                    @Override
                    public void updateTimeElapsed(long timeElapsed) {
                        Platform.runLater(() -> {
                            timeLabel.setText("Time Elapsed: " + toStringRepresentation(timeElapsed));
                        });
                    }
                });
    }

    @FXML
    private void toggleHint() {
        gameModel.setHintVisible(!gameModel.isHintVisible());
        if (gameModel.isHintVisible())
            hintButton.setText("Hide Hint");
        else hintButton.setText("Show Hint");
    }

    @FXML
    private void showCharts() {
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Run number");
        yAxis.setLabel("Score");
        //creating the chart
        final LineChart<Number, Number> lineChart =
                new LineChart<>(xAxis, yAxis);


        final StringConverter<Number> converter = new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return object.doubleValue() == object.intValue()
                        ? object.intValue() + ""
                        : "";
            }

            @Override
            public Number fromString(String string) {
                return 0;
            }
        };
        xAxis.setTickLabelFormatter(converter);

        lineChart.setTitle("Algorithms Performance");
        //defining a series
        XYChart.Series series = new XYChart.Series();

        lineChart.getData().add(series);

        BorderPane bp = new BorderPane();
        HBox topBox = new HBox();
        ComboBox<String> options = new ComboBox<>();
        options.getItems().addAll(AlgorithmFactory.availableAlgorithmNames());
        options.getSelectionModel().select(0);
        series.setName(options.getSelectionModel().getSelectedItem());

        //populating the series with data
        try {
            List<Double> scores = ScoreSaveLoad
                    .loadScores(options.getSelectionModel().getSelectedItem());
//                lineChart.getData().clear();

            for (int i = 0, scoresSize = scores.size(); i < scoresSize; i++) {
                double score = scores.get(i);
                series.getData().add(new XYChart.Data(i, score));
            }
//                lineChart.getData().add(series);
            series.setName(options.getSelectionModel().getSelectedItem());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        options.getSelectionModel().selectedItemProperty().addListener((allValues, oldValue, newValue) -> {
//            if (oldValue.equals(newValue)) return;
            // quick patch for inconsistency in algorithm names
            try {
                List<Double> scores = ScoreSaveLoad.loadScores(newValue);
                System.out.println(scores);
//                lineChart.getData().clear();
                series.getData().clear();
                for (int i = 0, scoresSize = scores.size(); i < scoresSize; i++) {
                    double score = scores.get(i);
                    series.getData().add(new XYChart.Data(i, score));
                }
//                lineChart.getData().add(series);
                series.setName(options.getSelectionModel().getSelectedItem());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        topBox.getChildren().add(options);
        bp.setTop(topBox);
        bp.setCenter(lineChart);
        Dialog dialog = new Dialog();
        dialog.getDialogPane().setStyle(themeOptions.getParent().getScene().getRoot().getStyle());
        ButtonType buttonTypeOk = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().setContent(bp);
        dialog.showAndWait();
    }

    @FXML
    private void undoMove() {
        gameModel.undoMove();
    }

    private void simulationStopped() {
        Platform.runLater(() -> {
            simulateButton.setText("Simulate");
            scoreLabel.setText("Game Over: " + gameModel.getScore());
        });
        playerOptions.setDisable(false);
        algorithmOptions.setDisable(false);
        gameModeOptions.setDisable(false);
        String username = usernameField.getText().trim();
        username = username.isBlank() ? null : username;
        gameModel.setPlayerName(username);
    }

    @FXML
    private void gameModeChanged() {
        Mode mode = gameModeOptions.getSelectionModel().getSelectedItem().equalsIgnoreCase("5T") ? Mode.FIVE_T : Mode.FIVE_D;
        gameModel.setGameMode(mode);
    }

    public void setModel(GameModel gameModel) {
        this.gameModel = gameModel;
    }
}