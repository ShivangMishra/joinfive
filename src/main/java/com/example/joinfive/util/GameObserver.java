package com.example.joinfive.util;

import com.example.joinfive.model.grid.Grid;
import com.example.joinfive.model.grid.Line;
import com.example.joinfive.model.grid.Point;

import java.util.List;

public interface GameObserver {
    void update(Grid grid, List<Point> highlightPoints, List<Line> highlightLines);
}
