/******************************************************************************
  *  Author:       Deniz Ece Aktan
  *  Compilation:  javac PercolationStats.java
  *  Execution:    java PercolationStats int gridSz int testNum
  * 
  *  Dependencies: Percolation.java,
  *                edu.princeton.cs.algs4.StdRandom
  *                edu.princeton.cs.algs4.StdStats 
  * 
  *  This program takes n (nxn grid size for percolation) 
  *  and t (number of trials)
  *  from commandline
  *
  *    - Creates an n-by-n grid of sites (intially all blocked)
  *    - Choose a site uniformly at random among all blocked sites
  *    - Open this site
  *    - Repeat until system percolates and calculate treshold for each trial
  *    - Print mean, std and 95% confidence interval
  *
  ******************************************************************************/
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
public class PercolationStats {
    private int gridSz;
    private int testNum;
    private double[]percTholds;
    // perform trials independent experiments on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0)
            throw new java.lang.IllegalArgumentException();
        gridSz = n;
        testNum = trials;
        percTholds = new double[testNum];
        
        for (int i = 0; i < testNum; i++) {
            Percolation perc = new Percolation(gridSz);
            while (!perc.percolates()) {
                int row = StdRandom.uniform(1, gridSz + 1);
                int col = StdRandom.uniform(1, gridSz + 1);
                perc.open(row, col);
            }
            percTholds[i] = (double) perc.numberOfOpenSites() / (double) (gridSz * gridSz);
        }
    }
    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(percTholds);
    }
    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(percTholds);
    }
    // low  endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - (1.96 * stddev() / Math.sqrt(testNum));
    }
    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + (1.96 * stddev() / Math.sqrt(testNum));
    }                      
    public static void main(String[] args)
    {
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);
        PercolationStats ps = new PercolationStats(n, t);
        System.out.println("mean                    = " + ps.mean());
        System.out.println("stddev                  = " + ps.stddev());
        System.out.println("95% confidence interval = [ "+ ps.confidenceLo() + ", "+ ps.confidenceHi() +"]");
    }
}