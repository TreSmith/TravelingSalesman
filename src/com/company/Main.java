package com.company;
import java.lang.management.*;
import java.util.*;

public class Main {

    static long MAXTIME = 120000000000L;
    
    public static void main(String[] args) {
	// write your code here
        int maxCost = 100;

        int[][] Graph = GenerateRandomCostMatrix(5, maxCost);
        printGraph(Graph);
        greedyAlgorithm(Graph, maxCost);
        bruteForceAlgorithm(Graph);
        
        timeTest(100);
        
    }
    
    //=====================
    //TSP Algorithms
    //=====================

   public static int bruteForceAlgorithm(int[][] CostMatrix){
        int vertices = CostMatrix[0].length, cost;
        int[][] paths = new int[factorial(vertices-1)][vertices+1];
        int[] pathCost = new int[factorial(vertices-1)];

        paths = getAllPossiblePaths(vertices);  //Generate a list of all possible cities to travel to
        int minCost = 2147483647;               //Set to a large number so the minCost can only go down from there
        int minCostLoc = 0;                     //Location of the lowest cost path
        int i=0;
        while(i < factorial(vertices-1)) {  //Goes through the list of all possible paths and one by one checks the cost
            cost = 0;

            for(int x=0; x<vertices; x++) {
                cost += CostMatrix[paths[i][x]][paths[i][x+1]];
            }
            pathCost[i] = cost;
            if(pathCost[i] < minCost)          //calculate the lowest cost
                minCostLoc = i;
            i++;
        }

        //System.out.print(pathCost[minCostLoc]);
        return pathCost[minCostLoc];
    }

    /**https://www.geeksforgeeks.org/travelling-salesman-problem-greedy-approach/ Helped with this algorithm**/
   public static int greedyAlgorithm(int[][] CostMatrix, int maxCost){
        int cost=0, counter=0;
        List<Integer> visitedRoutes = new ArrayList<>();

        visitedRoutes.add(0);   //City 0 is the first one you are at
        int[] path = new int[CostMatrix.length];

        int x=0, y=0;           //Used to traverse the array
        int minPath = maxCost;  //Set the minPath equal to maxCost so it can only go down from there
        while(x < CostMatrix.length && y < CostMatrix[x].length) {


            if(counter >= CostMatrix[x].length - 1) {   //at the end of the graph
                break;
            }
            //Check if the city is unvisited and if the cost is less then the current path cost then update the cost
            if(x != y && !(visitedRoutes.contains(y))) {
                if (CostMatrix[x][y] < minPath) {
                    minPath = CostMatrix[x][y];
                    path[counter] = y + 1;
                }
            }
            y++;

            if(y == CostMatrix[x].length) {
                cost = cost + minPath;
                minPath = maxCost;
                visitedRoutes.add(path[counter]-1);
                y = 0;
                x = path[counter] - 1;
                counter++;
            }
            if(x < 0)
                x = 0;
        }

        x = path[counter - 1] - 1;
        if(x<0)
            x=0;

        for(y=0; y < CostMatrix.length; y++) {
            if((x != y) && CostMatrix[x][y] < minPath) {
                minPath = CostMatrix[x][y];
                path[counter] = y + 1;
            }
        }
        cost += minPath;

        //System.out.printf("Minimum cost is %d\n", cost);
        return cost;
    }

    //=====================
    //Test Input Generation
    //=====================

    public static int[][] GenerateRandomCostMatrix (int vertices, int maxCost) {
        int[][] CostMatrix = new int[vertices][vertices];

        for(int x=0; x<vertices; x++)
        {
            CostMatrix[x][x] = 0;
            for(int y=x+1; y<vertices; y++){
                CostMatrix[x][y] = randomNum(1,maxCost);
                CostMatrix[y][x] = CostMatrix[x][y];
            }
        }
        return CostMatrix;
    }
    
    //=========================================
    //TimeTests
    //=========================================
    
    public static void timeTest(int maxCost) {
       long bTime = 0, gTime = 0, beforeTime, afterTime;
       long[] bTimes = new long[100], gTimes = new long[100];
       boolean timeoutBrute = false, timeoutGreed = false;
       double geDouble, beDouble, gDouble, bDouble;


       System.out.printf("%-10s %25s %10s %10s %25s %10s %10s\n", "N", "Brute Time", "B Double", "B Expect Dbl", "Greed Time", "G Double", "G Expect Dbl");
       for(int x=2; x<100; x++) {
           if(x%2==0)
                System.out.printf("%-10d", x);

           if(!timeoutBrute && x<=10) {
               beforeTime = getCpuTime();
               bruteForceAlgorithm(GenerateRandomCostMatrix(x, maxCost));
               afterTime = getCpuTime();

               bTime = afterTime - beforeTime;
               bTimes[x] = bTime;

               if(x%2==0 && x!=2){
                   bDouble = (double)bTimes[x]/bTimes[x/2];             //Doubling
                    beDouble = (double)factorial(x)/factorial(x/2); //Expected doubling

                   System.out.printf("%25d %10.3f %10.3f ", bTime, bDouble, beDouble);
               }
           }
           else
               timeoutBrute = true;
           if (timeoutBrute && x%2==0) {
               System.out.printf("%48s", " ");
           }

           if(bTime >= MAXTIME)
               timeoutBrute = true;

           
           if(!timeoutGreed) {
               beforeTime = getCpuTime();
               greedyAlgorithm(GenerateRandomCostMatrix(x, maxCost), maxCost);
               afterTime = getCpuTime();

               gTime = afterTime - beforeTime;
               gTimes[x] = gTime;

               if (x % 2 == 0 && x != 2) {
                   gDouble = (double) gTimes[x] / gTimes[x / 2];
                   int x2 = x / 2;
                   geDouble = ((x * x * Math.log(x)) / (x2 * x2 * Math.log(x2)));

                   System.out.printf("%25d %10.3f %10.3f", gTime, gDouble, geDouble);

               }
           }
           if(gTime >= MAXTIME)
               timeoutGreed = true;

           if(x%2==0)
                System.out.print("\n");

       }
       
       
    }
    
    //=========================================
    //Utilities
    //=========================================

    public static void printGraph(int[][] graph){
        for(int x=0; x<graph[0].length; x++) {
            for(int y=0; y<graph[0].length; y++) {
                System.out.printf("%d ", graph[x][y]);
            }
            System.out.print("\n");
        }
    }

    public static int factorial(int N) {
        int factorial=1;
        for(int x=1; x<=N; x++)
            factorial = factorial*x;
        return factorial;
    }

    public static int randomNum(int min, int max){
        return (int) (Math.random() * ((max + 1) - min + 1)) + min;
    }

    public static boolean printIf(int x) {
        return (x < 100 && x >= 10 && x % 10 == 0) || (x < 1000 && x >= 100 && x % 100 == 0) || (x < 10000 && x >= 1000 & x % 1000 == 0) || (x<100000 && x>=10000 &&x%10000==0);
    }

    public static String getSecondType (long time) {
        if((time/1000000000) > 60)
            return "m ";
        if(time >= 1000000000)
            return "s ";
        else if(time >= 1000000)
            return "µs";
        else if(time >= 1000)
            return "ms";
        else
            return "ns";
    }

    public static long convertNanoSeconds (long time) {
        long convertedTime=0;

        if(time >= 1000000000) {
            convertedTime = time / 1000000000;
            if(convertedTime>=60)   //Check if its a minute or more
                convertedTime = convertedTime/60;
        }
        else if(time >= 1000000)
            convertedTime = time / 1000000;
        else if(time >= 1000)
            convertedTime = time / 1000;
        else
            convertedTime = time;

        return convertedTime;
    }

    /**This website is where this code was inspired by I changed it to better fit the current algorithm**/
    //https://www.baeldung.com/java-array-permutations
    public static int[][] getAllPossiblePaths(int N) {
        int[][] list = new int[factorial((N-1))][N+1];
        int[] indexes = new int[N];
        int[] elements = new int[N-1];
        for (int i = 0; i < N; i++) {
            indexes[i] = 0;
            if(i!=0)
            {
                elements[i-1] = i;
            }

        }
        copyInto(elements, list[0]);

        int i = 0, count=1;
        while (i < N-1) {
            list[count-1][0] = 0;

            if (indexes[i] < i) {
                swap(elements, i % 2 == 0 ?  0: indexes[i], i);
                copyInto(elements, list[count]);
                indexes[i]++;
                count++;
                i = 0;
            }
            else {
                indexes[i] = 0;
                i++;
            }
        }
        return list;
    }

    private static void swap(int[] input, int a, int b) {
        int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }


    private static void copyInto(int[] A, int[] B){
        B[0] = 0;
        if (B.length - 1 - 1 >= 0) System.arraycopy(A, 0, B, 1, B.length - 1 - 1);
        B[B.length-1] = 0;
    }

    /* Get CPU time in nanoseconds since the program(thread) started. */
    /** from: http://nadeausoftware.com/articles/2008/03/java_tip_how_get_cpu_and_user_time_benchmarking#TimingasinglethreadedtaskusingCPUsystemandusertime **/
    public static long getCpuTime( ) {

        ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
        return bean.isCurrentThreadCpuTimeSupported( ) ?
                bean.getCurrentThreadCpuTime( ) : 0L;

    }
}
