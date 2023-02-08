package com.example.joinfive.model.grid;

import java.util.HashSet;
import java.util.Set;

/**
 * A Grid Point is a Point that can be locked in various directions
 */
public class GridPoint extends Point {
    private final Set<Direction> lockedDirections;

    public boolean isLocked(Direction direction) {
        return lockedDirections.contains(direction);
    }

    public void lock(Direction direction) {
        lockedDirections.add(direction);
    }

    public void unlock(Direction direction) {
        lockedDirections.remove(direction);
    }

    public GridPoint(int x, int y) {
        super(x, y);
        lockedDirections = new HashSet<>();
    }

    public GridPoint copy() {
        GridPoint copy = new GridPoint(x, y);
        copy.lockedDirections.addAll(lockedDirections);
        return copy;
    }
}
