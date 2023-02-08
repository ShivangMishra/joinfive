package com.example.joinfive.model.ai;

import com.example.joinfive.model.grid.Grid;
import com.example.joinfive.model.grid.Line;

public interface JoinFiveAlgorithm {
    /**
     * Calculates a game move- A move is simply a line that is drawn on the given grid.
     *
     * @param grid the grid
     * @return
     */
    Line calcMove(Grid grid);

    String getName();
}
