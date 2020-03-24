
// useful link https://github.com/knazir/SimpleGA
import java.util.*;

import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;

public class machineLearningProject {
    static ArrayList<Point> points;
    static int[][] adjacencyMatrix;
    static int numberOfVertices;
    static int[] ordering;
    static double chunk;
    public static String pattern1 = "^[1-9]\\d*$";
    public static String pattern2 = "^[1-9]?[0-9]{1}$|^100$";

    public static void main(String[] args) throws FileNotFoundException, IOException {

        String PopulationInput = JOptionPane.showInputDialog(null, "Population Size");
        String numberOfGenerations = JOptionPane.showInputDialog(null, "number of generations");
        String crossOverRate = JOptionPane.showInputDialog(null, "crossover rate");
        String mutationRate = JOptionPane.showInputDialog(null, "mutation rate");
        if (PopulationInput.matches(pattern1) && numberOfGenerations.matches(pattern1)
                && crossOverRate.matches(pattern1) && mutationRate.matches(pattern1)) {
            System.out.println(PopulationInput);
            System.out.println(numberOfGenerations);
            if (crossOverRate.matches(pattern2) && mutationRate.matches(pattern2)) {
                System.out.println(crossOverRate);
                System.out.println(mutationRate);
                int cR = Integer.parseInt(crossOverRate);
                int mR = Integer.parseInt(mutationRate);
                int sum = cR + mR;
                if (sum > 100) {
                    System.out.println("please input lower numbers for mutation rate and crossover rate");
                } else {
                    System.out.println("the sum of cR and mR = " + sum);
                }

            }
        } else {
            JOptionPane.showMessageDialog(null, "one of the inputs is not a positive digit");
        }
        // readline();

    }

    public static void readFile() throws FileNotFoundException, IOException {
        // pass the path to the file as a parameter
        File file = new File("input.txt");
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String[] point = sc.nextLine().split(" ");
            Point a = new Point(Double.parseDouble(point[0]), Double.parseDouble(point[1]));
            points.add(a);
        }
        sc.close();
    }

}

class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return this.x + ":" + this.y;
    }
}

class GraphVisualisation extends JFrame {
    private static final String TITLE = "Graph Visualisation";
    private static final int WIDTH = 960;
    private static final int HEIGHT = 960;
    private int[][] adjacencyMatrix;
    private final int numberOfVertices;
    private final int[] ordering;
    private final double chunk;

    public GraphVisualisation(final int[][] adjacencyMatrix, final int[] ordering, final int numberOfVertices) {
        this.adjacencyMatrix = adjacencyMatrix;
        this.ordering = ordering;
        this.numberOfVertices = numberOfVertices;
        this.chunk = (Math.PI * 2) / ((double) numberOfVertices);
        setTitle(TITLE);
        setSize(WIDTH, HEIGHT);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void paint(Graphics g) {
        super.paint(g);

        int radius = 100;
        int mov = 200;

        for (int i = 0; i < this.numberOfVertices; i++) {
            for (int j = i + 1; j < this.numberOfVertices; j++) {
                if (this.adjacencyMatrix[this.ordering[i]][this.ordering[j]] == 1) {
                    g.drawLine((int) (Math.cos(i * chunk) * radius) + mov, (int) (Math.sin(i * chunk) * radius) + mov,
                            (int) (Math.cos(j * chunk) * radius) + mov, (int) (Math.sin(j * chunk) * radius) + mov);
                }
            }
        }
    }
}
