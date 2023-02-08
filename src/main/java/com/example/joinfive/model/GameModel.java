package com.example.joinfive.model;

import com.example.joinfive.model.ai.AlgorithmObserver;
import com.example.joinfive.model.ai.JoinFiveAlgorithm;
import com.example.joinfive.model.ai.RandomSearchAlgorithm;
import com.example.joinfive.model.grid.*;
import com.example.joinfive.util.GameObserver;
import com.example.joinfive.util.ScoreEntry;
import com.example.joinfive.util.ScoreObserver;
import com.example.joinfive.util.ScoreSaveLoad;
import javafx.concurrent.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GameModel {

    private static final int GRID_WIDTH = 50, GRID_HEIGHT = 50;
    /**
     * The game grid
     */
    private Grid grid;

    /**
     * Is the user selecting a line from among the multiple possible lines for a selected point?
     */
    private boolean selectingLine;

    /**
     * Possible lines when the user clicks on a coordinate
     */
    private List<Line> possibleLines;

    /**
     * the game observers which get notified/updated when the game state changes
     */
    private final ArrayList<GameObserver> gameObservers;

    private final ArrayList<ScoreObserver> scoreObservers;

    private Thread computerThread;

    private boolean computerPlaying;
    private String playerName;
    private boolean hintVisible;

    private boolean gameOver;

    public boolean isGameOver() {
        System.out.println(gameOver);
        return gameOver;
    }

    /**
     * Default constructor
     */
    public GameModel() {
        gameObservers = new ArrayList<>();
        scoreObservers = new ArrayList<>();
        init();
    }

    /**
     * Update all the game observers and score observers with the current game state and score respectively
     */
    public void updateObservers() {
        List<Point> points = new ArrayList<>();
        for (Line line : possibleLines) {
            List<Point> otherPoints = new ArrayList<>();
            possibleLines.stream().filter(l -> l != line).toList()
                    .forEach(l -> otherPoints.addAll(l.points()));
            for (Point point : line.points()) {
                if (!otherPoints.contains(point)) {
                    points.add(point);
                    break;
                }
            }
        }
        List<Line> hintLines = new ArrayList<>();
        if (hintVisible) {
            hintLines.addAll(grid.possibleLines());
        }
        gameObservers.forEach(gameObserver -> gameObserver.update(grid, points, hintLines));
        scoreObservers.forEach(scoreObserver -> scoreObserver.updateScore(getScore()));
    }

    /**
     * Add a game observer
     *
     * @param gameObserver the game observer to be added
     */
    public void addGameObserver(GameObserver gameObserver) {
        gameObservers.add(gameObserver);
    }

    public void addScoreObserver(ScoreObserver scoreObserver) {
        scoreObservers.add(scoreObserver);
    }

    /**
     * Initialize the game model
     */
    public void init() {
        grid = new Grid(GRID_WIDTH, GRID_HEIGHT, Mode.FIVE_D);
        grid.init();
        gameOver = false;
        possibleLines = new ArrayList<>();
        updateObservers();
    }

    /**
     * Let the computer play from now until the end of the game
     *
     * @param algorithm the game AI algorithm
     */
    public void computerPlay(JoinFiveAlgorithm algorithm, AlgorithmObserver algoObs) {
        if (computerPlaying) {
            System.out.println("Already Running..");
            return;
        }
        playerName = algorithm.getName();
        computerPlaying = true;
        long startTime = System.currentTimeMillis();
        Task<Void> compTask = new Task<>() {
            @Override
            protected Void call() {
                while (computerPlaying) {
                    Line compLine = algorithm.calcMove(grid);
                    System.out.println("Got line");
                    if (compLine == null) {
                        gameOver();
                        break;
                    }
                    makeMove(compLine);
                }
                succeeded();
                return null;
            }

            @Override
            protected void succeeded() {
                algoObs.done();
            }
        };

        Task<Void> timerTask = new Task<>() {
            @Override
            protected Void call() {
                while (computerPlaying) {
                    updateMessage(null);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                return null;
            }

            @Override
            protected void updateMessage(String message) {
                super.updateMessage(message);
                algoObs.updateTimeElapsed(System.currentTimeMillis() - startTime);
            }
        };

        computerThread = new Thread(compTask);
        computerThread.start();
        new Thread(timerTask).start();
    }

    public void computerStop() {
        if (computerThread == null) {
            System.out.println("Not running");
            return;
        }
        computerPlaying = false;
        System.out.println("comp stopped");
    }

    /**
     * end the game
     */
    private void gameOver() {
        System.out.println("GAME OVER !");
        updateObservers();
        checkAndSaveBestGrid();
        checkAndSaveScore();
        gameOver = true;
        computerPlaying = false;
    }

    private void checkAndSaveScore() {
        if (playerName == null) {
            playerName = "No Name";
        }
        try {
            ScoreSaveLoad.saveScore(new ScoreEntry(playerName, grid.lines().size()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkAndSaveBestGrid() {
        Grid best = null;
        try {
            best = GridSaveLoad.loadGrid("best.grid");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Unable to load the best grid...");
        }

        if (best == null || best.lines().size() <= grid.lines().size()) {
            try {
                System.out.println("Saving best grid...");
                GridSaveLoad.saveGrid(grid, "best.grid");
                System.out.println("Grid saved!");
            } catch (IOException e) {
                System.out.println("Error saving the best grid");
            }
        }
    }

    public Grid getBestGrid() {
        Grid best = null;
        try {
            best = GridSaveLoad.loadGrid("best.grid");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Unable to load the best grid...");
        }
        return best;
    }

    public void handleHumanMove(int x, int y) {
        if (computerPlaying)
            return;
        if (selectingLine) {
            for (Line line : possibleLines) {
                for (Point point : line.points()) {
                    if (point.x == x && point.y == y) {
                        makeMove(line);
                        selectingLine = false;
                        checkGameOver();
                        return;
                    }
                }
            }
            System.out.println("Invalid Move");
            return;
        }
        List<Line> possibleLines = grid.possibleLines(x, y);
        if (possibleLines.size() == 0) {
            System.out.println("Invalid Move");
        } else if (possibleLines.size() == 1) {
            makeMove(possibleLines.get(0));
            checkGameOver();
        } else {
            selectingLine = true;
            System.out.println("More than one possible");
            this.possibleLines = possibleLines;
            updateObservers();
        }
        System.out.println(possibleLines);
//        possibleLines.forEach(grid::addLine);
    }

    /**
     * Check if the game is over
     */
    private void checkGameOver() {
        HashSet<Point> pointsSoFar = new HashSet<>();
        List<Line> possibleLines = new ArrayList<>();
        for (Point gridPoint : grid.points()) {
            for (Point point : grid.getNeighbors(gridPoint)) {
                if (pointsSoFar.contains(gridPoint)) continue;
                pointsSoFar.add(point);
                possibleLines.addAll(grid.possibleLines(point.x, point.y));
                if (possibleLines.size() > 0) {
                    gameOver = false;
                    return;
                }
            }
        }
        gameOver();
    }

    /**
     * Make a move on the grid, i.e., add a new line to the grid
     *
     * @param line the line to be drawn in this move
     */
    private void makeMove(Line line) {
        grid.addLine(line);
        possibleLines.clear();
        updateObservers();
    }

    /**
     * Set the game mode enum
     *
     * @param mode the game mode
     */
    public void setGameMode(Mode mode) {
        this.grid.setMode(mode);
    }

    public boolean isComputerPlaying() {
        return computerPlaying;
    }

    public int getScore() {
        return grid.lines().size();
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setHintVisible(boolean visibility) {
        hintVisible = visibility;
        updateObservers();
    }

    public boolean isHintVisible() {
        return hintVisible;
    }

    public void undoMove() {
        if (grid.lines().size() < 1) return;
        grid.undoLine();
        checkGameOver();
        updateObservers();
    }
}
