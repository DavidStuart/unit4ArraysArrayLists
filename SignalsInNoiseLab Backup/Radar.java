
/**
 * The model for radar scan and accumulator
 * 
 * @author @gcschmit
 * @version 19 July 2014
 */
public class Radar
{
    
    // stores whether each cell triggered detection for the current scan of the radar
    private boolean[][] currentScan;
    
    //Stores the last scan to check the change in positions
    private boolean[][] previousScan;
    // value of each cell is incremented for each scan in which that cell triggers detection 
    private int[][] accumulator;
    
    // location of the monster
    private int monsterLocationRow;
    private int monsterLocationCol;

    // probability that a cell will trigger a false detection (must be >= 0 and < 1)
    private double noiseFraction;
    
    // number of scans of the radar since construction
    private int numScans;
    
    //Change in monster's position
    private int dx;
    private int dy;
    
    private int turnNumber = 0;
    /**
     * Constructor for objects of class Radar
     * 
     * @param   rows    the number of rows in the radar grid
     * @param   cols    the number of columns in the radar grid
     */
    public Radar(int rows, int cols,int deltax, int deltay)
    {
        // initialize instance variables
        currentScan = new boolean[rows][cols]; // elements will be set to false
        accumulator = new int[rows][cols]; // elements will be set to 0
        previousScan = new boolean[rows][cols];
        dx = deltax;
        dy = deltay;
        // randomly set the location of the monster (can be explicity set through the
        //  setMonsterLocation method
        
        noiseFraction = 0.05;
        numScans= 0;
    }
    
    /**
     * Performs a scan of the radar. Noise is injected into the grid and the accumulator is updated.
     * 
     */
    public void scan()
    {
        turnNumber ++;
        // zero the current scan grid
        //previousScan = currentScan;
        for(int row = 0; row < currentScan.length; row++)
        {
            for(int col = 0; col < currentScan[0].length; col++)
            {
                previousScan[row][col] = currentScan[row][col];
                currentScan[row][col] = false;
            }
        }
        
        updateMonsterLocation(dx,dy);
        // detect the monster
        currentScan[monsterLocationRow][monsterLocationCol] = true;
        
        // inject noise into the grid
        injectNoise();
        
        int ldx = 1;
        int ldy = 1;
        int checkX = 0;
        int checkY = 0;
        
        // udpate the accumulator
        for(int row = 0; row < currentScan.length; row++)
        {
           for(int col = 0; col < currentScan[0].length; col++)
           {
               for(ldx = -5; ldx < 6; ldx++)
               {
                   for(ldy = -5; ldy < 6; ldy++)
                   {         
                       if(row + ldx <= 99 && row + ldx >= 0)
                        {
                            checkX = row + ldx;
                        }
                        else
                        {
                            checkX = row;
                        }
                       if(col + ldy <= 99 && col + ldy >= 0)
                       {
                           checkY = col + ldy;
                       }
                       else
                       {
                          checkY = col;
                       }
                       if(currentScan[checkX][checkY] == true && currentScan[checkX][checkY] == previousScan[row][col])
                       {
                           accumulator[checkX][checkY] = accumulator[row][col]+1;
                           //accumulator[row-1][col-1] = 0;
                       }
                       else
                       {
                           //accumulator[row][col] = 0;
                       }
                    }
                }
            }
        }
        // keep track of the total number of scans
        numScans++;
    }

    /**
     * Sets the location of the monster
     * 
     * @param   row     the row in which the monster is located
     * @param   col     the column in which the monster is located
     * @pre row and col must be within the bounds of the radar grid
     */
    public void setMonsterLocation(int row, int col)
    {
        // remember the row and col of the monster's location
        monsterLocationRow = row;
        monsterLocationCol = col;
        
        // update the radar grid to show that something was detected at the specified location
        currentScan[row][col] = true;
    }
    public void updateMonsterLocation(int dx, int dy)
    {
        if(monsterLocationRow+ dx <= 99 && monsterLocationRow + dx >= 0)
        {monsterLocationRow += dx;}
        
        if (monsterLocationCol+ dy <= 99 && monsterLocationCol + dy >= 0)
        {monsterLocationCol += dy;}
        
        currentScan[monsterLocationRow][monsterLocationCol] = true;
    }
    
     /**
     * Sets the probability that a given cell will generate a false detection
     * 
     * @param   fraction    the probability that a given cell will generate a flase detection expressed
     *                      as a fraction (must be >= 0 and < 1)
     */
    public void setNoiseFraction(double fraction)
    {
        noiseFraction = fraction;
    }
    
    /**
     * Returns true if the specified location in the radar grid triggered a detection.
     * 
     * @param   row     the row of the location to query for detection
     * @param   col     the column of the location to query for detection
     * @return true if the specified location in the radar grid triggered a detection
     */
    public boolean isDetected(int row, int col)
    {
        return currentScan[row][col];
    }
    
    /**
     * Returns the number of times that the specified location in the radar grid has triggered a
     *  detection since the constructor of the radar object.
     * 
     * @param   row     the row of the location to query for accumulated detections
     * @param   col     the column of the location to query for accumulated detections
     * @return the number of times that the specified location in the radar grid has
     *          triggered a detection since the constructor of the radar object
     */
    public int getAccumulatedDetection(int row, int col)
    {
        return accumulator[row][col];
    }
    
    /**
     * Returns the number of rows in the radar grid
     * 
     * @return the number of rows in the radar grid
     */
    public int getNumRows()
    {
        return currentScan.length;
    }
    
    /**
     * Returns the number of columns in the radar grid
     * 
     * @return the number of columns in the radar grid
     */
    public int getNumCols()
    {
        return currentScan[0].length;
    }
    
    /**
     * Returns the number of scans that have been performed since the radar object was constructed
     * 
     * @return the number of scans that have been performed since the radar object was constructed
     */
    public int getNumScans()
    {
        return numScans;
    }
    
    /**
     * Sets cells as falsely triggering detection based on the specified probability
     * 
     */
    private void injectNoise()
    {
        for(int row = 0; row < currentScan.length; row++)
        {
            for(int col = 0; col < currentScan[0].length; col++)
            {
                // each cell has the specified probablily of being a false positive
                if(Math.random() < noiseFraction)
                {
                    currentScan[row][col] = true;
                }
            }
        }
    }
    
}
