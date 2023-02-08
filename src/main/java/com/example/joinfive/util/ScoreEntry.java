package com.example.joinfive.util;

public class ScoreEntry {
    public String getUsername() {
        return username;
    }

    public double getScore() {
        return score;
    }

    private String username;
    private double score;
    public ScoreEntry(String username, double score) {
        this.score = score;
        this.username = username;
    }
}
