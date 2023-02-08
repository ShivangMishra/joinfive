package com.example.joinfive.model.ai.nrpa;

import com.example.joinfive.model.grid.Grid;
import com.example.joinfive.model.grid.Line;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;

public class NrpaState implements INrpaState<Grid, Line>{

    private final Grid grid;

    public NrpaState(Grid grid) {
        this.grid = grid;
    }

    @Override
    public double getScore() {
        return grid.lines().size();
    }

    @Override
    public List<Line> findAllLegalActions() {
        return grid.possibleLines();
    }

    @Override
    public INrpaState<Grid, Line> takeAction(Line line) {
        Grid newGrid = grid.copy();
        newGrid.addLine(line);
        return new NrpaState(newGrid);
    }

//    @Override
//    public Pair<Double, List<Line>> simulation(NrpaPolicy policy) {
//        List<Line> list = new LinkedList<>();
//
//
//        while(true) {
//            List<Line> possibleLines = grid.possibleLines();
//
//        }
//    }

    @Override
    public boolean isTerminalPosition() {
        return findAllLegalActions().size() == 0;
    }

    @Override
    public INrpaState<Grid, Line> copy() {
        return new NrpaState(grid.copy());
    }
}
