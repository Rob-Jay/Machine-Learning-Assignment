
// useful link https://github.com/knazir/SimpleGA
import java.util.*;

import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;

public class machineLearningProject {
    static ArrayList<Point> points = new ArrayList<Point>();
    static int[][] adjacencyMatrix;
    static int numberOfVertices;
    static int[] ordering;
    static double chunk;
    public static String pattern1 = "^[0-9]\\d*$";
    public static String pattern2 = "^[1-9]?[0-9]{1}$|^100$";

    public static void main(String[] args) throws Exception, FileNotFoundException, IOException {

      validateInput();
      //readFile();

    }
    public static void validateInput()throws Exception{
        String populationInput = JOptionPane.showInputDialog(null, "Population Size");
        String numberOfGenerations = JOptionPane.showInputDialog(null, "number of generations");
        String crossOverRate = JOptionPane.showInputDialog(null, "crossover rate");
        String mutationRate = JOptionPane.showInputDialog(null, "mutation rate");
        if(populationInput==null || numberOfGenerations==null || crossOverRate==null || mutationRate==null){
            System.out.println("You canceled one of the options!");            
            validateInput();
        }
        if (populationInput.matches(pattern1) && numberOfGenerations.matches(pattern1)
                && crossOverRate.matches(pattern1) && mutationRate.matches(pattern1)) {
            System.out.println(populationInput);
            System.out.println(numberOfGenerations);
            if (crossOverRate.matches(pattern2) && mutationRate.matches(pattern2)) {
                System.out.println(crossOverRate);
                System.out.println(mutationRate);
                int cR = Integer.parseInt(crossOverRate);
                int mR = Integer.parseInt(mutationRate);
                int sum = cR + mR;
                if (sum > 100) {
                    System.out.println("sum of mutation rate and crossover rate must be below 100"); 
                    validateInput();
                } else {
                    JOptionPane.showMessageDialog(null, "the sum of cR and mR = " + sum);
                   
                }

            }
        }
         else {
            JOptionPane.showMessageDialog(null, "one of the inputs is not a positive digit");
            validateInput();
        }
    }

    //Read input file and map coordinates into adjacencyMatrix 2D array
    public static void readFile() throws FileNotFoundException, IOException {
        // pass the path to the file as a parameter 
        File file = new File("input.txt"); 
        Scanner sc = new Scanner(file);
        int highest = 0;
        while (sc.hasNextLine()) {
            String[] point = sc.nextLine().split(" ");
            int x = Integer.parseInt(point[0]);
            int y = Integer.parseInt(point[1]);
            if (x >= highest) highest = x;
            else if (y >= highest) highest = y;
            Point a = new Point(x, y);
            points.add(a);
        } 
        sc.close();
        
        adjacencyMatrix = new int[highest+1][highest+1];
        for (Point p : points) {
            adjacencyMatrix[p.getX()][p.getY()] = 1;
        }
        printMatrix(highest);
    }

    //print the matrix
    public static void printMatrix(int highest) {
        System.out.print("Adjacency Matrix\n  ");
        for (int i = 0; i <= highest; i++) {
            System.out.printf("%3d", i);
        }
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            System.out.printf("\n%2d", i);
            for (int j = 0; j < adjacencyMatrix[0].length; j++) {
                System.out.printf("%3d", adjacencyMatrix[i][j]);
            }
        }
    }
}


 // 8.4 generated orderings by storing values in an arraylist and converting to string to compare
 public static void generateOrdering(int n, int p) {
    String result = "";
    int randomValue;
    StringBuffer sb = new StringBuffer();
    ArrayList<String> ordering = new ArrayList<String>();
    ArrayList<String> populationOrdering = new ArrayList<String>();

    for (int j = 0; j < p; j++) {
        // create an ordering
        for (int i = 0; i != n; i++) {
            randomValue = (int) (Math.random() * n + 1);
            result = result + String.valueOf(randomValue);
            if (!ordering.contains(result)) {
                ordering.add(result);
                result = "";
            } else {
                i = i - 1;
            }
            result = "";
        }

        // insert ordering into orderings
        for (String s : ordering) {
            sb.append(s + " ");
        }
        result = sb.toString();
        if (!populationOrdering.contains(result)) {
            populationOrdering.add(result);
            System.out.println("Here is a population \t" + result);
            result = "";
        } else {
            j = j - 1;
            result = "";
        }
        ordering.clear();
        sb.delete(0, sb.length());
    }
}

class Point {
    private int x;
	private int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public String toString() {
		return this.x + ":" + this.y;
    }
    
    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
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
