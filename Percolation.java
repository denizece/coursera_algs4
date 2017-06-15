/******************************************************************************
  *  Compilation:  javac Percolation.java
  *  Execution:    java Percolation
  * 
  *  Dependencies: edu.princeton.cs.algs4.WeightedQuickUnionUF 
  * 
  *  This class takes n (nxn grid size for percolation)
  *  Provide Percolation API:
  *    - Creates an n-by-n grid of sites (intially all blocked)
  *    - Open given sites (x,y)
  *    - Answer if system percolates on a given state
  *    - Provide methods isFull, isOpen, NumberOfSites
  *
  ******************************************************************************/
import edu.princeton.cs.algs4.WeightedQuickUnionUF;
public class Percolation {
    private boolean[][] grid; // table[i][j]=0 for blocked, 
    private int gridSz;
    private WeightedQuickUnionUF uf; // for percolates()
    private WeightedQuickUnionUF backwashEliminateUF;
    private int treeSize; // size of the uf tree
    private int openSites;
    // create n-by-n grid, with all sites blocked
    public Percolation(int n) {
        gridSz = n;
        treeSize = n * n + 2;
        openSites = 0;
        uf = new WeightedQuickUnionUF(treeSize); // add 2 nodes top and bottom
        backwashEliminateUF = new WeightedQuickUnionUF(treeSize - 1); // additional 1 node at top
        // no node at bottom
        grid = new boolean[gridSz][gridSz];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = false;
            }
        }
        // union bottom node with last column from grid
        // union up node with first column from grid
        for (int node = 0; node < n; node++) {
            uf.union(0, node+1);
            uf.union(treeSize - 1, treeSize - 2 - node); // treeSize-1 is the bottom,
            // start union from treeSize -2
            backwashEliminateUF.union(0, node + 1); // union top node to top row
        }
    }
    private void validateIndex(int i, int j) {
        if (i < 1 || i > gridSz || j < 1 || j > gridSz)
            throw new java.lang.IndexOutOfBoundsException();
    }
    private int xyTo1D(int x, int y) {
        int oneD = (x - 1) * gridSz + (y - 1) + 1; // + 1 from col(y) because of +1 node on top
        return oneD;
    }
    // is site (row, col) open?
    public boolean isOpen(int row, int col) {
        validateIndex(row, col);
        return grid[row - 1][col - 1];
    }
    
    // is site (row, col) full?
    public boolean isFull(int row, int col) {
        validateIndex(row, col);
        return isOpen(row, col) && backwashEliminateUF.connected(xyTo1D(row, col), 0);
    }
    // number of open sites
    public int numberOfOpenSites() {
        return openSites;
    }
    // open site (row, col) if it is not open already
    public void open(int row, int col) {
        validateIndex(row, col);
        if (isOpen(row, col))
            return;
        else {
            openSites++;
            int treeIndex = xyTo1D(row, col);
            grid[row -1][col -1] = true;
            if (row > 1 && isOpen(row - 1, col)) {
                uf.union(xyTo1D(row - 1, col), treeIndex);
                backwashEliminateUF.union(xyTo1D(row - 1, col), treeIndex);
            }
            if (col > 1 && isOpen(row, col -1)) {
                uf.union(xyTo1D(row, col -1), treeIndex);
                backwashEliminateUF.union(xyTo1D(row, col -1), treeIndex);
            }
            if (row < gridSz && isOpen(row + 1, col)) {
                uf.union(xyTo1D(row + 1, col), treeIndex);
                backwashEliminateUF.union(xyTo1D(row + 1, col), treeIndex);
            }
            if (col < gridSz && isOpen(row, col + 1)) {
                uf.union(xyTo1D(row, col + 1), treeIndex);
                backwashEliminateUF.union(xyTo1D(row, col + 1), treeIndex);
            }        
        }
    }
    private boolean connected(int x1, int y1, int x2, int y2) {
        validateIndex(x1, y1);
        validateIndex(x2, y2);
        return uf.connected(xyTo1D(x1, y1), xyTo1D(x2, y2));
    }
    // does the system percolate?
    public boolean percolates() {
        if (gridSz == 1) 
            return isOpen(1, 1); // corner case gridSz = 1
        return uf.connected(0, treeSize-1);
    }
    
    public  static void main(String[]args) {
        Percolation p = new Percolation(3);
        p.open(1, 1);
        System.out.println("------opened 1,1");
        System.out.println("------opened 1,2");
        p.open(1, 2);
        System.out.println("------is connected? (1, 1),(1, 2) "+p.connected(1, 1, 1, 2));
        System.out.println("------is open? (1, 2) "+p.isOpen(1, 2));
        boolean perc = p.percolates();
        System.out.println("------percolate? "+perc);
        p.open(2, 2);
        p.open(3, 2);
        System.out.println("------is connected?(1, 1)(3, 2) "+p.connected(1, 1, 3, 2));
        perc = p.percolates();
        System.out.println("------percolate? "+perc);
        System.out.println("------number of open sites? "+p.numberOfOpenSites());
        
    }
}