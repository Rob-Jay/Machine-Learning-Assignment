/*
MURDO MACKENZIE - 16152522
IBRAHIM ALAYDI - 16165365
NATTHAWIPHA PROMLOI - 17198453
ROBERT WALSH - 17232783
*/

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.text.DecimalFormat;
import java.text.Format;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;

public class is16165365 {
    static ArrayList<Point> points = new ArrayList<Point>();
    //static ArrayList<Double> fitnessCostDistance;
    //static ArrayList<Double> fitnessCostEdge;
    static int[][] adjacencyMatrix;
    static int[] orderingDistance, numbers, orderingEdge;
    static int N, P, G;
    static int countNextPopulationD = 0;
    static int countNextPopulationE = 0;
    static int Cr, Mr, Pr;
    static int[][] currentPopulationD, currentPopulationE, nextPopulationD, nextPopulationE;
    static double chunk;
    
    public static void main( String[] args) throws Exception, FileNotFoundException, IOException {
        
        validateInput();
        readFile();
        currentPopulationD = new int[P][N + 1];
        currentPopulationE = new int[P][N + 1];
        nextPopulationD = new int[P][N + 1];
        nextPopulationE = new int[P][N + 1];
        generateOrdering();
        populateNumbers();
        GraphVisualisation gv1 = new GraphVisualisation("Total Distance Fuction");
        GraphVisualisation gv2 = new GraphVisualisation("Total Edge Fuction");
        gv1.adjacencyMatrix = adjacencyMatrix;
        gv2.adjacencyMatrix = adjacencyMatrix;
        for (int i = 0; i < G; i++) {
            //fitnessCostDistance = new ArrayList<>();
            //selection(currentPopulationD, "D");
            selection(currentPopulationE, "E");
            int current = currentPopulationD.length;
            boolean finish = false;
            while(!finish) {
                int Pr = (int)(Math.random() * 101);
                
                if(Cr >= Pr && !finish && current >= 2) {
                    crossOver(current, currentPopulationD, "D");
                    crossOver(current, currentPopulationE, "E");
                    current = current - 2;
                    if(current == 0) finish = true;
                }
                
                if(Cr <= Pr && Pr <= (Cr + Mr) && !finish) {
                    mutation(current, currentPopulationD, "D");
                    mutation(current, currentPopulationE, "E");
                    current = current - 1;
                    if(current == 0) finish = true;
                }
                
                if(Cr == Mr && !finish) {
                    if (Cr <= Pr) {
                        reproduction(current, currentPopulationD, "D");
                        reproduction(current, currentPopulationE, "E");
                        current = current - 1;
                        if(current == 0) finish = true;
                    }
                }
            }
            currentPopulationD = nextPopulationD;
            currentPopulationE = nextPopulationE;
            countNextPopulationD = 0;
            countNextPopulationE = 0;
            orderingDistance = getBestOrdering("D");
            orderingEdge = getBestOrdering("E");
            gv1.chunk = chunk;
            gv1.numberOfVertices = orderingDistance.length;
            gv1.ordering = orderingDistance;
            gv1.repaint();
            gv2.chunk = chunk;
            gv2.numberOfVertices = orderingEdge.length;
            gv2.ordering = orderingEdge;
            gv2.repaint();
        }
    }

    public static int[] getBestOrdering(String functionSwitch) {
        double fitness = 50.0;
        double currentFitness = 0.0;
        if(functionSwitch.equals("D")) {
            int[] ordering = nextPopulationD[0];
            for (int i = 0; i < nextPopulationD.length; i++) {
                currentFitness = calculateFitnessDistance(nextPopulationD[i]);
                if(currentFitness <= fitness) {
                    fitness = currentFitness;
                    ordering = nextPopulationD[i];
                }
            }
            printOrdering(ordering);
            System.out.println("\nDistance Fitness: " + fitness);
            return ordering;
        } else {
            int[] ordering = nextPopulationE[0];
            for (int i = 0; i < nextPopulationE.length; i++) {
                currentFitness = calculateFitnessEdge(nextPopulationE[i]);
                if(currentFitness <= fitness) {
                    fitness = currentFitness;
                    ordering = nextPopulationE[i];
                }
            }
            printOrdering(ordering);
            System.out.println("\nEdge Fitness: " + fitness);
            return ordering;
        }
    }

    //to remove the ordering from currentPopulation
    public static void removeOrdering(int length, int index, int[][] currentPopulation, String count) {
        int[][] temp = new int[length - 1][currentPopulation[0].length];
        if(length == 1 || length == 0) {
            currentPopulation = new int[0][0];
        } else if (length > 1) {
            boolean found = false;
            for(int i = 0; i < length; i++) {
                if(i == index && !found) {
                    if(i == length - 1) ;
                    else System.arraycopy(currentPopulation[i + 1], 0, temp[i], 0, currentPopulation[i + 1].length);
                    found = true;
                } else if(found){
                    System.arraycopy(currentPopulation[i], 0, temp[i - 1], 0, currentPopulation[i].length);
                } else {
                    System.arraycopy(currentPopulation[i], 0, temp[i], 0, currentPopulation[i].length);
                }
            }
        }
        if(count.equals("D")) currentPopulationD = temp;
        else currentPopulationE = temp;
    }

    public static void crossOver(int length, int[][] currentPopulation, String count) {
        int index1 = (int)(Math.random() * length);
        int[] order1 = currentPopulation[index1];
        removeOrdering(length, index1, currentPopulation, count);
        int index2 = (int)(Math.random() * length - 1);
        int[] order2 = currentPopulation[index2];
        removeOrdering(length - 1, index2, currentPopulation, count);
        int Cp = (int)(Math.random() * order1.length - 2) + 1;
        int[] temp = Arrays.copyOfRange(order1, 0, Cp);
        for(int i = 0; i < temp.length; i++) {
            order1[i] = order2[i];
            order2[i] = temp[i];
        }
        addToNextGeneration(manageDuplicates(order1), count);
        addToNextGeneration(manageDuplicates(order2), count);
    }

    public static void addToNextGeneration(int[] ordering, String count) {
        if(count.equals("D")) {
            nextPopulationD[countNextPopulationD] = ordering;
            countNextPopulationD++;
        } else {
            nextPopulationE[countNextPopulationE] = ordering;
            countNextPopulationE++;
        }
        
    }

    public static void populateNumbers() {
        numbers = new int[N + 1];
        for(int i = 0; i < N + 1; i++) {
            numbers[i] = i;
        }
    }

    //remove duplicates and put in missing numbers
    public static int[] manageDuplicates(int[] order) {
        String duplicatesString = "";
        String missingString = "";

        for (int i = 0; i < order.length; i++) {
            for (int j = i + 1; j < order.length; j++) {
                if (order[i] == order[j]) duplicatesString += order[i] + " ";
            }
        }
        String[] dupArr = duplicatesString.split(" ");
        int[] duplicates = new int[dupArr.length];
        for(int i = 0; i < dupArr.length; i++) {
            if(!dupArr[i].equals("")) duplicates[i] = Integer.parseInt(dupArr[i]);
        }

        for (int i = 0; i < order.length; i++) { 
            int j;  
            for (j = 0; j < numbers.length; j++) 
                if (numbers[i] == order[j]) break; 
            if (j == numbers.length) missingString += numbers[i] + " "; 
        }
        String[] missingArr = missingString.split(" ");
        int[] missing = new int[missingArr.length];
        for(int i = 0; i < missingArr.length; i++) {
            if(!missingArr[i].equals("")) missing[i] = Integer.parseInt(missingArr[i]);
        }

        for(int i = 0; i < order.length; i++) {
            for(int j = 0; j < duplicates.length; j++) {
                if(order[i] == duplicates[j]) {
                    order[i] = missing[j];
                    duplicates[j] = -1;
                    missing[j] = -1;
                }
            }
        }
        return order;
    }

    public static void mutation(int length, int[][] currentPopulation, String count) {
        int index = (int)(Math.random() * length);
        int[] order = currentPopulation[index];
        removeOrdering(length, index, currentPopulation, count);
        int m1 = (int)(Math.random() * order.length - 1);
        int m2 = (int)(Math.random() * order.length - 1);
        int temp = order[m1];
		order[m1] = order[m2];
        order[m2] = temp;
        //printOrdering(order);
        addToNextGeneration(order, count);
    }

    public static void reproduction(int length, int[][] currentPopulation, String count) {
        int index = (int)(Math.random() * length - 1);
        addToNextGeneration(currentPopulation[index], count);
        removeOrdering(length, index, currentPopulation, count);
    }

    public static void selection(int[][] currentPopulation, String count) {
        ArrayList<Double> fitnessCost = new ArrayList<>();
        ArrayList<Double> temp = new ArrayList<>();
        double fitness = 0.0;
        for (int i = 0; i < currentPopulation.length; i++) {
            int[] order = currentPopulation[i];
            calculateChunk(order);
            if(count.equals("D")) fitness = calculateFitnessDistance(order);
            else if(count.equals("E")) fitness = calculateFitnessEdge(order);
            fitnessCost.add(fitness);
            temp.add(fitness);
        }
        Collections.sort(fitnessCost);
        //System.out.println(fitnessCost);
        
        int[][] tempPopulation = new int[P][N + 1];
        for(int i = 0; i < currentPopulation.length; i++) {
            int location = temp.indexOf(fitnessCost.get(i));
            tempPopulation[i] = currentPopulation[location];
        }

        // Replacing the worst section with the best section
        //int[] order2 = tempPopulation[0];
        int j = 0;
        double selNum = currentPopulation.length * 2/3; 
        for(int i = 0; i < currentPopulation.length; i++) {
            if (i + 0.0 < selNum) {
                currentPopulation[i] = tempPopulation[i];
            } else if(i + 0.0 >= selNum) {
                currentPopulation[i] = tempPopulation[j];
                j++;
            }
        }
    }
    
    // Order the currentPopulation according to the best fitness cost (lowest number) to the worst fitness cost (higest number)
    // Divide the currentPopulation into 3 sections s1, s2, s3 from best orderings to worst ordering. 
    // From that replaces the worst section (s3) with the best orderings (s1)
    /* public static void selection() {
        ArrayList<Double> fitnessCostDistance = new ArrayList<>();
        ArrayList<Double> fitnessCostEdge =  new ArrayList<>();
        ArrayList<Double> tempD = new ArrayList<>();
        ArrayList<Double> tempE = new ArrayList<>();
        int[] orderD, orderE;
        for (int i = 0; i < currentPopulationD.length; i++) {
            orderD = currentPopulationD[i];
            orderE = currentPopulationE[i];
            double fitnessD = calculateFitnessDistance(orderD);
            double fitnessE = calculateFitnessEdge(orderE);
            fitnessCostDistance.add(fitnessD);
            fitnessCostEdge.add(fitnessE);
            tempD.add(fitnessD);
            tempE.add(fitnessE);
        }
        Collections.sort(fitnessCostDistance);
        System.out.println(fitnessCostDistance);
        Collections.sort(fitnessCostEdge);
        System.out.println(fitnessCostEdge);

        int[][] tempPopulationD = new int[P][N + 1];
        int[][] tempPopulationE = new int[P][N + 1];
        for(int i = 0; i < currentPopulationD.length; i++) {
            int locationD = tempD.indexOf(fitnessCostDistance.get(i));
            tempPopulationD[i] = currentPopulationD[locationD];
            int locationE = tempE.indexOf(fitnessCostEdge.get(i));
            tempPopulationE[i] = currentPopulationE[locationE];
        }

        // Replacing the worst section with the best section
        orderD = tempPopulationD[0];
        orderE = tempPopulationE[0];
        int j = 0;
        double selNum = currentPopulationD.length * 2/3; 
        for(int i = 0; i < currentPopulationD.length; i++) {
            if (i + 0.0 < selNum) {
                currentPopulationD[i] = tempPopulationD[i];
                currentPopulationE[i] = tempPopulationE[i];
            } else if(i + 0.0 >= selNum) {
                currentPopulationD[i] = tempPopulationD[j];
                currentPopulationE[i] = tempPopulationE[j];
                j++;
            }
        }
    } */

    public static void printPop(int[][] pop) {
        for (int[] is : pop) {
            printOrdering(is);
        }
    }
    
    public static void printOrdering(int[] order) {
        System.out.println();
        for(int i = 0; i < order.length; i++) {
            System.out.print(order[i] + " ");
        }
    }
    
    // Question 2 Inputs
    public static void validateInput() throws Exception {
        String pattern1 = "^[0-9]\\d*$";
        String pattern2 = "^[1-9]?[0-9]{1}$|^100$";
         String populationInput = JOptionPane.showInputDialog(null, "Population Size");
         String numberOfGenerations = JOptionPane.showInputDialog(null, "number of generations");
         String crossOverRate = JOptionPane.showInputDialog(null, "crossover rate");
         String mutationRate = JOptionPane.showInputDialog(null, "mutation rate");
        if (populationInput == null || numberOfGenerations == null || crossOverRate == null || mutationRate == null) {
            System.out.println("You canceled one of the options!");
            validateInput();
        }
        if (populationInput.matches(pattern1) && numberOfGenerations.matches(pattern1)
        && crossOverRate.matches(pattern1) && mutationRate.matches(pattern1)) {
            System.out.println(populationInput);
            System.out.println(numberOfGenerations);
            P = Integer.parseInt(populationInput);
            G = Integer.parseInt(numberOfGenerations);
            if (crossOverRate.matches(pattern2) && mutationRate.matches(pattern2)) {
                System.out.println(crossOverRate);
                System.out.println(mutationRate);
                int cR = Integer.parseInt(crossOverRate);
                int mR = Integer.parseInt(mutationRate);
                Cr = Integer.parseInt(crossOverRate);
                Mr = Integer.parseInt(mutationRate);
                int sum = cR + mR;
                if (sum > 100) {
                    System.out.println("sum of mutation rate and crossover rate must be below 100");
                    validateInput();
                } else {
                    JOptionPane.showMessageDialog(null, "the sum of cR and mR = " + sum);
                    
                }
                
            } else {
                JOptionPane.showMessageDialog(null, "crossover rate and mutation rate must be below 100");
                validateInput();
            }
        } else {
            JOptionPane.showMessageDialog(null, "one of the inputs is not a positive digit");
            validateInput();
        }
    }
    
    // Read input file and map coordinates into adjacencyMatrix 2D array
    public static void readFile() throws FileNotFoundException, IOException {
        // pass the path to the file as a parameter
        File file = new File("input.txt");
        Scanner sc = new Scanner(file);
        int highest = 0;
        while (sc.hasNextLine()) {
            String[] point = sc.nextLine().split(" ");
            int x = Integer.parseInt(point[0]);
            int y = Integer.parseInt(point[1]);
            if (x >= highest)
                highest = x;
            else if (y >= highest)
                highest = y;
            Point a = new Point(x + 0.0, y + 0.0);
            points.add(a);
        }
        sc.close();
        N = highest;
        // SIZE OF MATRIX
        adjacencyMatrix = new int[highest + 1][highest + 1];
        for (Point p : points) {
            adjacencyMatrix[(int)p.getY()][(int)p.getX()] = 1;
            adjacencyMatrix[(int)p.getX()][(int)p.getY()] = 1;
        }
        printMatrix(highest);
        points.clear();
    }
    
    // print the matrix
    public static void printMatrix( int highest) {
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
        System.out.println("\n");
    }
    
    
    // 8.4
    public static void generateOrdering() {
        String result = "";
        int max = N + 1;
        int randomValue;
        StringBuffer sb = new StringBuffer();
        ArrayList<String> ordering = new ArrayList<String>();
        ArrayList<String> populationOrdering = new ArrayList<String>();
        
        for (int j = 0; j < P; j++) {
            // create an ordering
            for (int i = 0; i != max; i++) {
                randomValue = (int) (Math.random() * max);
                
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
                populateCurrentOrdering(result, j);
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
    
    // Populate currentPopulation from order generating.
    public static void populateCurrentOrdering(String populationOrdering, int i) {
        String[] curPopulation = populationOrdering.split(" ");
        for(int j = 0; j <= N; j++) {
            currentPopulationD[i][j] = Integer.parseInt(curPopulation[j]);
            currentPopulationE[i][j] = Integer.parseInt(curPopulation[j]);
        }
    }

    // getting the distance of two points using pythagoras equation a^2 = b^2 + c^2
    public static void calculateXY(int[] ordering) {
        DecimalFormat df = new DecimalFormat("#.######");
        points = new ArrayList<Point>();
        for(int i = 0; i < ordering.length; i++) {
            double x = Double.parseDouble(df.format(Math.cos(i * chunk)));
            double y = Double.parseDouble(df.format(Math.sin(i * chunk)));
            points.add(new Point(x, y));
        }
    }

    // getting the distance of two points using pythagoras equation a^2 = b^2 + c^2
    public static double getDistance(Point p1, Point p2) {
        DecimalFormat df = new DecimalFormat("#.######");
        double ac = Math.abs(p2.getY() - p1.getY());
        double cb = Math.abs(p2.getX() - p1.getX());
        
        return Double.parseDouble(df.format(Math.hypot(ac, cb)));
    }

    public static void calculateChunk(int[] ordering) {
        chunk = (Math.PI * 2) / (double)(ordering.length);
    }

    // finding the index of x from the ordering
    public static int getPositionOf(int[] ordering, int x) {
        for(int i = 0; i < ordering.length; i++) {
            if(ordering[i] == x) return i;
        }
        return -1;
    }
    
    // Fitness calculted unsing the distance between two points as mentioned in the spec
    public static double calculateFitnessDistance(int[] ordering) {
        calculateXY(ordering);
        double sum = 0;
        for(int i = 0; i < adjacencyMatrix.length; i++) {
            for(int j = 0; j < adjacencyMatrix[i].length / 2; j++) {
                if(adjacencyMatrix[i][j] == 1) {
                    int index1 = getPositionOf(ordering, i);
                    int index2 = getPositionOf(ordering, j);
                    double distance = getDistance(points.get(index1), points.get(index2));
                    sum += distance;
                }
            }
        }
        return sum;
    }

    public static double calculateFitnessEdge(int[] ordering) {
        calculateXY(ordering);
        double minimumNodeDistance = 10;
        for(int i = 0; i < ordering.length; i++) {
            for(int j = i + 1; j < ordering.length; j++ ) {
                if(i == ordering.length - 1) j = 0;
                double length = getDistance(points.get(i), points.get(j));
                if(length <= minimumNodeDistance) {
                    minimumNodeDistance = length;
                }
            }
        }
        int possibleCrossingEdge = 0;
        for(int i = 0; i < adjacencyMatrix.length; i++) {
            for(int j = 0; j < adjacencyMatrix[i].length / 2; j++) {
                if(adjacencyMatrix[i][j] == 1) {
                    int index1 = getPositionOf(ordering, i);
                    int index2 = getPositionOf(ordering, j);
                    double distance = getDistance(points.get(index1), points.get(index2));
                    if(distance > minimumNodeDistance) possibleCrossingEdge++;
                }
            }
        }
        return possibleCrossingEdge;
    }
}

class Point {
    private  double x;
    private  double y;
    
    public Point( double x,  double y) {
        this.x = x;
        this.y = y;
    }
    
    public String toString() {
        return this.x + ":" + this.y;
    }
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
}

class GraphVisualisation extends JFrame {
    private String TITLE = "Graph Visualisation ";
    private int WIDTH = 360;
    private int HEIGHT = 360;
    int[][] adjacencyMatrix;
    int numberOfVertices;
    int[] ordering;
    double chunk;

    public GraphVisualisation(String title) {
        this.TITLE = this.TITLE + title;
        setTitle(TITLE);
        setSize(WIDTH, HEIGHT);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    public void paint( Graphics g) {
        super.paint(g);
        int radius = 100;
        int mov = 200;
        
        for (int i = 0; i < this.numberOfVertices; i++) {
            for (int j = i + 1; j < this.numberOfVertices; j++) {
                if (this.adjacencyMatrix[this.ordering[i]][this.ordering[j]] == 1) {
                    g.drawLine((int) (Math.cos(i * chunk) * radius) + mov,
                    (int) (Math.sin(i * chunk) * radius) + mov,
                    (int) (Math.cos(j * chunk) * radius) + mov,
                    (int) (Math.sin(j * chunk) * radius) + mov);
                }
            }
        }
    }
}