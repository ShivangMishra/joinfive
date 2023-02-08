package com.example.joinfive.model.grid;

import java.io.Serializable;
import java.util.*;

/**
 * This is not a geometrical line. It encapsulates a game move
 */
public class Line implements Serializable    {
    private final List<Point> points;

    /**
     * the newest point among the 5 points that made this point
     */
    private Point newPoint;

    /**
     * line number
     */
    private int number;

    /**
     * Direction of this line
     */
    private Direction direction;
    private int hash;

    public Line() {
        points = new ArrayList<>();
    }

    public Line(Collection<Point> points, Direction direction) {
        this.points = new ArrayList<>();
        this.points.addAll(points);
        this.direction = direction;
    }

    public Line(Collection<Point> points, Point newPoint, Direction direction, int number) {
        this.points = new ArrayList<>();
        this.points.addAll(points);
        this.newPoint = newPoint;
        this.direction = direction;
        this.number = number;
    }

    public Line(Collection<Point> points) {
        this.points = new ArrayList<>();
        this.points.addAll(points);
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public String toString() {
        return newPoint + "-" + direction + ": " + points.toString();
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public List<Point> points() {
        return Collections.unmodifiableList(points);
    }

    public void setNewPoint(Point newPoint) {
        this.newPoint = newPoint;
    }

    public Point getNewPoint() {
        return newPoint;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Line copy() {
        return new Line(points, newPoint, direction, number);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return number == line.number && points.equals(line.points) && newPoint.equals(line.newPoint) && direction == line.direction;
    }

    @Override
    public int hashCode() {
        System.out.println("hashing");
//        if (hash == 0) {
//            long bits = 7L;
//            bits = 31L * bits + points.get(0).hashCode();
//            bits = 31L * bits + direction.hashCode();
//            hash = (int) (bits ^ (bits >> 32));
//            System.out.println("Calculated hash" + hash);
//        }
//        return hash;
        return Objects.hash(points.get(0), direction);
    }
}
