package com.example.joinfive.util;

import com.example.joinfive.model.grid.Point;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ScoreSaveLoad {
    private static final String scorePath = "history.csv";

    public static List<ScoreEntry> loadScores() throws IOException {
        File file = new File(scorePath);
        List<ScoreEntry> scoresList;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            scoresList = new ArrayList<>();
            String line;
            br.readLine(); // to skip the header line
            while ((line = br.readLine()) != null) {
                line = line.trim();
                int x = line.indexOf(',');
                String name = line.substring(0, x);
                double score = Double.parseDouble(line.substring(x + 1));
                ScoreEntry scoreEntry = new ScoreEntry(name, score);
                scoresList.add(scoreEntry);
            }
        }
        return scoresList;
    }

    public static void saveScore(ScoreEntry scoreEntry) throws IOException {
        try (BufferedWriter out = new BufferedWriter(
                new FileWriter(scorePath, true))) {
            out.write(scoreEntry.getUsername() + "," + scoreEntry.getScore() + "\n");
        }
    }

    public static List<Double> loadScores(String playerName) throws IOException {
        List<ScoreEntry> scoreEntries = loadScores();
        List<Double> scores = new LinkedList<>();

        for(ScoreEntry scoreEntry : scoreEntries) {
            System.out.println(playerName + scoreEntry.getUsername());
            if(scoreEntry.getUsername().equalsIgnoreCase(playerName)) {
                System.out.println("TRUEEE");
                scores.add(scoreEntry.getScore());
            }
        }

        return scores;
    }
}
