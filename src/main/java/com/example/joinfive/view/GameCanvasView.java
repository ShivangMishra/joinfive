package com.example.joinfive.view;

import com.example.joinfive.util.GameObserver;
import com.example.joinfive.model.grid.Grid;
import com.example.joinfive.model.grid.Line;
import com.example.joinfive.model.grid.Point;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class GameCanvasView implements GameObserver {

    private Canvas canvas;

    private static final int CELL_WIDTH = 25;
    private static final int CELL_HEIGHT = 25;

    public static final int WIDTH = CELL_WIDTH * 50, HEIGHT = CELL_HEIGHT * 50;

    private final int offX = CELL_WIDTH;
    private final int offY = CELL_HEIGHT;

    public static final Theme LIGHT_THEME = new Theme(
            Color.valueOf("#AFD8F8"),
            Color.valueOf("#0C3547"),
            Color.WHITE,
            Color.valueOf("#0C3547"),
            Color.YELLOW
    );

    public static final Theme DARK_THEME = new Theme(
            Color.valueOf("#404258"),
            Color.valueOf("#C0A080"),
            Color.valueOf("#404258"),
            Color.valueOf("#D5BFBF"),
            Color.valueOf("#6D8299")
    );

    private Theme theme;

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public GameCanvasView(Canvas canvas) {
        this.canvas = canvas;
        canvas.setWidth(WIDTH);
        canvas.setHeight(HEIGHT);
    }

    public int getOffX() {
        return offX;
    }

    public int getOffY() {
        return offY;
    }

    public int getCellWidth() {
        return CELL_WIDTH;
    }

    public int getCellHeight() {
        return CELL_HEIGHT;
    }

    private void calcVars(Grid grid) {
//        int width = (int) canvas.getWidth();
//        int height = (int) canvas.getHeight();
//
//        int nBlocksX = grid.width() - 1;
//        int nBlocksY = grid.height() - 1;

//        cellWidth = width / (nBlocksX + 2); // 500/21 initially
//        cellHeight = height / (nBlocksY + 2); // 500/21 initially
//        cellWidth = 25;
//        cellHeight = 25;
//        offX = cellWidth;
//        offY = cellHeight;
    }


    public void calcDimensions(Grid grid) {
        int width = grid.width() * CELL_WIDTH;
        canvas.setWidth(width);
        int height = grid.height() * CELL_HEIGHT;
        canvas.setHeight(height);
    }

    public void update(Grid grid, List<Point> highlightPoints, List<Line> highlightLines) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        g.setLineWidth(2.0);
        g.setStroke(Color.valueOf("#34495E"));
        g.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());
        System.out.println(canvas.getWidth() + ", " + canvas.getHeight());
        g.setLineWidth(1);
//        g.setStroke(Color.valueOf("#AFD8F8"));
        g.setStroke(theme.bgLineColor());
        g.setLineWidth(1);
        for (double gridX = 0; gridX < grid.width(); gridX++) {
//            g.strokeRect(0,0,width,height);
            double x1 = snap(offX + gridX * CELL_WIDTH);
            double y1 = snap(offY);
            double x2 = snap(offX + gridX * CELL_WIDTH);
            double y2 = snap(offY + (grid.height() - 1) * CELL_HEIGHT);

            g.strokeLine(x1, y1, x2, y2);
//            drawLine(offX + 0, offY + gridY * cellHeight, offX + gridX * cellWidth, offY + height * cellHeight, g);
        }

        for (double gridY = 0; gridY < grid.height(); gridY++) {
            double x1 = snap(offX);
            double y1 = snap(offY + gridY * CELL_HEIGHT);
            double x2 = snap(offX + (grid.width() - 1) * CELL_WIDTH);
            double y2 = snap(offY + gridY * CELL_HEIGHT);
            g.strokeLine(x1, y1, x2, y2);
        }

//        g.setFill(Color.valueOf("#0C3547"));
        g.setFill(theme.lineColor());
        double radius = 5;
        g.setStroke(theme.lineColor());
        grid.lines().forEach(line -> drawLine(line, g));
        g.setFill(theme.pointColor());
        grid.points().forEach(point -> {
            g.fillOval(offX + CELL_WIDTH * point.x - radius, offY + CELL_HEIGHT * point.y - radius, 2 * radius, 2 * radius);
        });

        g.setStroke(theme.lineColor());
        grid.lines().forEach(line -> drawNumberedPoint(line.getNewPoint(), line.getNumber(), g));
//        g.setFill(Color.YELLOW);
        g.setFill(theme.highlightColor());
        highlightPoints.forEach(point -> {
            g.fillOval(offX + CELL_WIDTH * point.x - radius, offY + CELL_HEIGHT * point.y - radius, 2 * radius, 2 * radius);
        });

        g.setStroke(theme.highlightColor());
        highlightLines.forEach(line -> drawLine(line, g));

//        System.out.println("Drawing");
    }

    private double snap(double num) {
        return num + 0.5;
    }

    private void drawLine(Line line, GraphicsContext g) {
//        g.setStroke(Color.valueOf("#0C3547"));
        g.setLineWidth(2);
        Point p1 = line.points().get(0);
        Point p2 = line.points().get(line.points().size() - 1);

        g.strokeLine(offX + CELL_HEIGHT * p1.x, offY + CELL_HEIGHT * p1.y, offX + CELL_WIDTH * p2.x, offY + CELL_HEIGHT * p2.y);

//        g.setFill(Color.valueOf("#0C3547"));
    }

    private void drawNumberedPoint(Point p, int num, GraphicsContext g) {

        double radius = 10;
        double centerX = offX + CELL_HEIGHT * p.x - radius;
        double centerY = offY + CELL_HEIGHT * p.y - radius;
        double numX = centerX + radius * .65 * 1.0 / (num + "").length();
        double numY = centerY + radius * 1.3;
        g.setFill(theme.pointColor());
        g.fillOval(centerX, centerY, radius * 2, radius * 2);
        g.setLineWidth(1);
//        g.setStroke(Color.WHITE);
        g.setStroke(theme.numberColor());
        g.strokeText(num + "", numX, numY);
    }
}
