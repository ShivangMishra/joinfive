package com.example.joinfive.model.ai.nmcs;

import com.example.joinfive.model.ai.JoinFiveAlgorithm;
import com.example.joinfive.model.grid.Grid;
import com.example.joinfive.model.grid.Line;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * NMCS algorithm - searches through the state tree for the best possible move
 */
public class NmcsAlgorithm implements JoinFiveAlgorithm {

    @Override
    public Line calcMove(Grid grid) {

        int level = 2;
        List<Line> lines;
        final long maxRunningTimeMs = 2 * 1000;
        final long endTimeMs = System.currentTimeMillis() + maxRunningTimeMs;
        Result result = search(grid, level, () -> {

            System.out.println(System.currentTimeMillis() > endTimeMs);
            return System.currentTimeMillis() > endTimeMs;
        });
        //System.out.println("NMCS" + result.getValue().size());
        lines = result.getLines();

//        return lines.get(current++);
        return lines.size() > 0 ? lines.get(0) : null;
    }

    @Override
    public String getName() {
        return "NMCS";
    }

    private Result search(Grid grid, int depth, final Supplier<Boolean> isCanceled) {
        if (depth < 1)
            return grid.rollout();

        Result globalBestResult = new Result(grid.lines().size(), new LinkedList<>());

        List<Line> visited = new LinkedList<>();

        while (!grid.possibleLines().isEmpty() && !isCanceled.get()) {

            Result curBestResult = new Result(0.0, new LinkedList<>());
            Line curBestLine = null;

            List<Line> possibleLines = grid.possibleLines();
            for (Line line : possibleLines) {
                Result rolloutResult = search(grid.child(line), depth - 1, isCanceled);
                if (curBestResult.getScore() < rolloutResult.getScore()) {
                    curBestLine = line;
                    curBestResult = rolloutResult;
                }
            }

            if (curBestResult.getScore() < globalBestResult.getScore()) {
                curBestLine = globalBestResult.getLines().get(visited.size());
                visited.add(curBestLine);
            } else {
                visited.add(curBestLine);
                globalBestResult = curBestResult;
                globalBestResult.getLines().addAll(0, visited);
            }

            grid = grid.child(curBestLine);
        }
        return globalBestResult;
    }
}
