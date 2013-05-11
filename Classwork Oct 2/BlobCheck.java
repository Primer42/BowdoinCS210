
/*Blobcheck. 
 * 
 * csci 210, oct 2007
 * This program counts the size of "blobs" in a 2-D array.  The
 * program uses a recursive function countBlob, which takes as
 * parameter a grid cell and computes and returns how many cell are
 * filled around it. The program calls thi sfunction for each cell in
 * the grid.
 */

public class BlobCheck {
    
    //constants to set the values for which the grid is filled or empty
    private static final int EMPTY = 0;
    private static final int FILLED = 1;
    private static final int COUNTED = -1; 

    //array to hold the grid of numbers
    private static final int grid[][] = 
    { {EMPTY,  EMPTY,  EMPTY,  FILLED, FILLED}, 
      {EMPTY,  EMPTY,  EMPTY,  FILLED, EMPTY}, 
      {FILLED, FILLED, EMPTY,  EMPTY,  EMPTY}, 
      {FILLED, FILLED, FILLED, EMPTY,  FILLED}, 
      {FILLED, FILLED, EMPTY,  EMPTY,  FILLED} 
    };
    
    //the dimensions of the grid
    private int nrows, ncols;


    // The constructor sets up for the blob counting.
    public BlobCheck(){

	//set up the grid 
	nrows = grid.length; 
	ncols=grid[0].length;
	printGrid(grid); 
       
	// call countBlob with all possible locations 
	System.out.println("Blob counts: ");
		for (int i=0; i<nrows; i++) {
	    	for (int j=0; j< ncols; j++) { 
		
	    		//reset the grid copy 
	    		for (int k=0; k< nrows; k++){
	    			for (int l=0; l<ncols; l++){ 
	    				if(grid[k][l]== COUNTED){
	    					grid[k][l] = FILLED;
	    				}
	    			}
	    		}

	    	System.out.print(countBlob(grid, i,j) + " ");
	    	} //for j 
	    	System.out.println(); 
		}//for i
    }
    
    void printGrid(int grid[][]) {
	System.out.println("The grid is: "); 
	for (int i=0; i< this.nrows; i++) {
	    for (int j=0; j< this.ncols; j++)
		System.out.print( grid[i][j] + " " );
	    System.out.println(); 
	}
    }
    
    
    /* Returns the size of the blob containing the grid cell specified
     * by the paramaters.  The function is called recursively because
     * each square that is filled must look at each square around it
     * to see if it is filled.  The base cases are: unfilled square, a
     * square with something other than the filled value, or location
     * out of bounds.
     */
    public int countBlob(int grid[][], int row, int col){
    	if(row<0 || col < 0 || row>=this.nrows || col >=this.ncols){return 0;}
    	
    	if(grid[row][col] == EMPTY){return 0;}
    	
    	if(grid[row][col] == FILLED){
    		grid[row][col] = COUNTED;
    		return 1+ countBlob(grid, row+1, col) + countBlob(grid,row +1, col+1) + countBlob(grid,row, col+1) + 
    			countBlob(grid,row-1, col+1) + countBlob(grid,row-1, col) + countBlob(grid,row-1, col-1) +
    			countBlob(grid,row, col-1) + countBlob(grid,row+1, col-1);
    	}
    	return 0; 
    }
    
    public static void main(String args[]) {
        new BlobCheck();
    }

}
