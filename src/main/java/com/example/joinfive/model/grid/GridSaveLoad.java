package com.example.joinfive.model.grid;

import java.io.*;
import java.util.List;
import java.util.Set;

public class GridSaveLoad {

    public static void saveGrid(Grid grid, String filename) throws IOException {
        FileOutputStream file = new FileOutputStream(filename);
        ObjectOutputStream out = new ObjectOutputStream(file);

        // Method for serialization of object
        out.writeObject(grid);

        out.close();
        file.close();

        System.out.println("Grid has been serialized");
    }

    public static Grid loadGrid(String filename) throws IOException, ClassNotFoundException {
        FileInputStream file = new FileInputStream(filename);
        ObjectInputStream in = new ObjectInputStream(file);

        Grid grid = (Grid) in.readObject();
        in.close();
        file.close();
        System.out.println("Grid has been deserialized");
        return grid;
    }
}
