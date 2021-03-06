
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
    private boolean[][][] scanHistory;
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
    
    //Final Monster Velocity Variables
    private int monsterX;
    private int monsterY;
    
    //keep track of what frame is currently running
    private int turnNumber = 0;
    
    //Tracks if the velocity has been output yet
    private boolean outputReported = false;
    
    //2D array used to check against current frame for possible monsters
    private boolean [][] isPossibleMonster;
    
    /**
     * Constructor for objects of class Radar
     * 
     * @param   rows    the number of rows in the radar grid
     * @param   cols    the number of columns in the radar grid
     * @param   deltax  the change in x position of the monster(input by user)
     * @param   deltay  the change in y position of the monster(input by user)
     */
    public Radar(int rows, int cols,int deltax, int deltay)
    {
        // initialize instance variables
        currentScan = new boolean[rows][cols]; // elements will be set to false
        accumulator = new int[rows][cols]; // elements will be set to 0
        scanHistory = new boolean[100][rows][cols];
        isPossibleMonster = new boolean[11][11];
        dx = deltax;
        dy = deltay;
        
        //Final Velocity to be output
        monsterX = -100;
        monsterY = -100;
        // randomly set the location of the monster (can be explicity set through the
        //  setMonsterLocation method
        
        noiseFraction = 0.05;
        numScans= 0;
    }
    
    /**
     * Performs a scan of the radar. Noise is injected into the grid and the accumulator is updated.
     * 
     * @for loop   accumulator   goes through the grid checking each cell then comparing the changes around that cell to
     *                                  previous scans to find patterns and detect the monster.
     * 
     * 
     * 
     * 
     * 
     */
    public void scan()
    {
        // zero the current scan grid
        for(int row = 0; row < currentScan.length; row++)
        {
            for(int col = 0; col < currentScan[0].length; col++)
            {
                currentScan[row][col] = false;
            }
        }
        

        updateMonsterLocation(dx,dy);
        // detect the monster
        currentScan[monsterLocationRow][monsterLocationCol] = true;
        
        // inject noise into the grid
        injectNoise();
        
        
        int checkX = 0;
        int checkY = 0;
        
        //local change in x and y used to check changes around the cell currently being checked
        int ldx = 0;
        int ldy = 0;
        
        
        int numMonsters = 0;
        boolean setAsMonster = false;
        
        // add the current scan to the history
        for(int row = 0; row < currentScan.length; row++)
        {
            for(int col = 0; col < currentScan[0].length; col++)
            {
                scanHistory[turnNumber][row][col] = currentScan[row][col];
            }
        }
        
        // udpate the accumulator
        for(int row = 0; row < currentScan.length; row++)
        {
            for(int col = 0; col < currentScan[0].length; col++)
            {
                // set the isPossibleMonster array to true values
                for(ldx = 0; ldx < 11; ldx++)
                {
                    for(ldy = 0; ldy < 11; ldy++)
                    {
                        isPossibleMonster[ldx][ldy]=true;
                    }
                }
                for(int i = 0; i <= turnNumber; i++)
                {
                    for(ldx = 0; ldx < 11; ldx++)
                    {
                        for(ldy = 0; ldy < 11; ldy++)
                        {
                            if(row - (turnNumber-i)*(ldx-5) <= 99 && row - (turnNumber-i)*(ldx-5) >= 0)
                            {
                                checkX = row - (turnNumber-i)*(ldx-5);
                            }
                            else
                            {
                                checkX = row;
                            }
                            if(col - (turnNumber-i)*(ldy-5) <= 99 && col - (turnNumber-i)*(ldy-5) >= 0)
                            {
                                checkY = col - (turnNumber-i)*(ldy-5);
                            }
                             else
                            {
                                checkY = col;
                            }
                            if(scanHistory[i][checkX][checkY] == false)
                            {
                                isPossibleMonster[ldx][ldy] = false;
                            }
                         }
                    }
                }
                setAsMonster = false;
                for(ldx = 0; ldx < 11; ldx++)
                {
                    for(ldy = 0; ldy < 11; ldy++)
                    {
                        if(isPossibleMonster[ldx][ldy] == true)
                        {
                            setAsMonster = true;
                            monsterX = ldx - 5;
                            monsterY = ldy - 5;
                        }
                    }
                }
                if(setAsMonster == true)
                {
                    accumulator[row][col] = turnNumber+1;
                    numMonsters++;
                }
                else
                {
                    accumulator[row][col] = 0;
                }
            }
        }
        
        if(numMonsters == 1 && turnNumber > 0 && outputReported == false) {
            System.out.println("Monster found in turn: " + turnNumber + " Monster Movement: " + monsterX + ", " + monsterY);
            outputReported = true;
        }
        
        // keep track of the total number of scans
        numScans++;
        turnNumber++;
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
    /**
     * Upadates the monster's location on the grid(called in scan method)
     * 
     * @param   dx   the user input change in x each scan
     * @param   dy   the user input change in y each scan
     */
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
