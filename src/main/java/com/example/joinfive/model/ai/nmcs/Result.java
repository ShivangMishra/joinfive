package com.example.joinfive.model.ai.nmcs;

import com.example.joinfive.model.grid.Line;

import java.util.List;

public class Result {
    private List<Line> lines;
    private double score;

    public Result(double score, List<Line> lines) {
        this.lines = lines;
        this.score = score;
    }

    public double getScore() {
        return this.score;
    }

    public List<Line> getLines() {
        return lines;
    }
}
