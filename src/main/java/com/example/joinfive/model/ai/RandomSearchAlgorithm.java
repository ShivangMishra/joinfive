package com.example.joinfive.model.ai;

import com.example.joinfive.model.grid.Grid;
import com.example.joinfive.model.grid.Line;

import java.util.List;

/**
 * Picks a random move from all the possible moves
 */
public class RandomSearchAlgorithm implements JoinFiveAlgorithm {
    @Override
    public Line calcMove(Grid grid) {
        List<Line> possibleLines = grid.possibleLines();
        if (possibleLines.isEmpty()) return null;
        else return possibleLines.get((int) (Math.random() * possibleLines.size()));
    }

    @Override
    public String getName() {
        return "Random Search";
    }
}
