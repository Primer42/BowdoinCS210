// Board.java

import java.awt.*;
import java.util.*;


/**
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearning.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Intead,
 just represents the abtsract 2-d board.
  See Tetris-Architecture.html for an overview.
  
 This is the starter file version -- a few simple things are filled in already
  
 @author	Nick Parlante
 @version	1.0, Mar 1, 2001
*/

/*Just a note- my version of Piece has a color associated with either piece,
 * which is stored and backed up here much like the grid is in variables colors
 * and bColors.  No other changes have been made to Pieces that removes 
 * functionality from the class.
 */
public final class Board  {
	private int width;
	private int height;
	private boolean[][] grid;
	private Color[][] colors;	//allows for colored pieces
        private int maxHeight;
        private int widths[];
        private int heights[];
        private boolean committed;

        // backup data structures
        private boolean[][] bGrid;
        private Color[][] bColors;
        private int[] bWidths;
        private int[] bHeights;
        private int bMaxHeight;
        
        //stores the last piece that was placed and its location.
        //makes undo() much faster
        private Piece lastPiecePlaced;
        private int lastX;
        private int lastY;
        private int lastPointReachedInBody;

        private boolean DEBUG = true;
        private static final int HEIGHT = 2;
        
	
	
	/**
	 Creates an empty board of the given width and height
	 measured in blocks.  This is not quite done, you still need
         to initialize your backup data structures (and erase this comment!)
	*/
	public Board(int aWidth, int aHeight) {
		width = aWidth;
		height = aHeight + HEIGHT;
                
		//sets up the various variables and their backups
		grid = new boolean[width][height];
		bGrid = new boolean[width][height];
		colors = new Color[width][height];
		bColors = new Color[width][height];
                widths = new int[height];
                bWidths = new int[height];
                heights = new int[width];
                bHeights = new int[width];

                maxHeight = 0;
                bMaxHeight = 0;
                committed = true;
                for (int i = 0; i < width; i++) {
                    heights[i] = 0;
                    bHeights[i] = 0;
                    for (int j = 0; j < height; j++) {
                        grid[i][j] = false;
                        bGrid[i][j] = false;
                    }
                }
                for (int i = 0; i < height; i++) {
                    widths[i] = 0;
                    widths[i] = 0;
                }
                sanityCheck("end of constructor");
        }
	
	
	/**
	 Returns the width of the board in blocks.
	*/
	public int getWidth() {
		return width;
	}
	
	
	/**
	 Returns the height of the board in blocks.
	*/
	public int getHeight() {
		return height;
	}
	
	
	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.  Remember, this should be constant!
	*/
	public int getMaxHeight() {
		return maxHeight;
	}
	
	
	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	 
	 Takes a string to identify where this sanityCheck was called
	*/
	public void sanityCheck(String location) {
		if (DEBUG) {
			System.out.println(location);	//tells where this was called
			//print out the grid, to see what it looks like
			for(int y = height-1; y >= 0; y--){
				for(int x = 0; x < width; x++){
					if(grid[x][y] == true){System.out.print("1 ");}
					else{System.out.print("0 ");}
				}
				System.out.println("");	
			}
			
			//check heights
			for(int x = 0; x < width; x++){
				int colHeight = 0;
				for(int h = height-1; h >= 0; h--){
					if(grid[x][h] == true){
						colHeight = h+1;
						break;
					}
				}
				if(heights[x] != colHeight){
					throw new RuntimeException(
		"heights["+x+"] wrong- should be " + colHeight + ", is " + heights[x]);
				}
			}
			
			//check maxHeight
			int localMaxHeight = 0;
			for(int h = 0; h < heights.length; h++){
				if(heights[h] > localMaxHeight){localMaxHeight = heights[h];}
			}
			
			if(localMaxHeight != maxHeight)
				throw new RuntimeException(
		"maxHeight wrong- should be " + localMaxHeight + ", is " + maxHeight);
			
			//check widths
			for(int y = 0; y < width-1; y++){
				int rowWidth = 0;
				for(int c = width-1; c >= 0; c--){
					if(grid[c][y] == true){
						rowWidth = c +1;
						break;
					}
				}
				if(widths[y] != rowWidth){
					throw new RuntimeException(
		"widths["+y+"] wrong- should be " + rowWidth + ", is " + widths[y]);
				}
			}
		}
	}
	
	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(Piece piece, int x) {
		int[] skirt = piece.getSkirt();		//get the skirt
		int maxY = 0;	//the hightest height at which the piece will fall
		//go through the skirt
		for(int i = 0; i < skirt.length; i++){
			//if the height of this column of the skirt is higher than the maxY
			//it is the new drop height, so set maxY to this height.
			if( maxY < heights[x+i] - skirt[i]){
				maxY = heights[x+i] - skirt[i];
			}
		}
		//return the maxY, or the hightest point at which this piece will fall
		return maxY;
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
		return heights[x];
	}
	
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		return widths[y];
	}
	
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public final boolean getGrid(int x, int y) {
		//if the location is within the grid return the grid value
		if(x >= 0 && x < width && y >= 0 && y < height){
			return grid[x][y];
		}
		//if not, return true
		else{return true;}
	}
	
	/**Returns the color of the passes location */
	public Color getColor(int x, int y){
		return colors[x][y];
	}
	
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.
	 
	 <p>Error cases:
	 If part of the piece would fall out of bounds, the placement
	 does not change the board at all, and PLACE_OUT_BOUNDS is returned.
	 If the placement is "bad" --interfering with existing blocks in the grid --
	 then the placement is halted partially complete and PLACE_BAD is returned.
	 An undo() will remove the bad placement.
	*/
	
	/* LOGIC OF METHOD
	 * Firstly, sees if the board is commited.  If it is, continue with the
	 * method.  If not, return PLACE_BAD.
	 * Assuming the board is committed, the method makes sure the location
	 * given is in bounds- if not returns PLACE_OUT_BOUNDS.  It then stores the
	 * necessary information for undoing, and, since the board will no longer
	 * be committed, it sets committed to false.
	 * The method then tries to place the piece.  if at any time, it finds a
	 * location in the grid has been used, it returns PLACE_BAD.  Otherwise,
	 * it continues to place the piece.  At the point where PLACE_BAD is
	 * returned, lastPointReachedInBody is set to the appropriate index- when
	 * undo is called, it only undos up to this index.  If the piece is placed
	 * sucessfully, lastPointReachdInBody is set to the body's length, again
	 * so undo() undos all the points that have been called.
	 * Once this point is reached, heights, maxHeights, and widths are
	 * recalculated. Finally, it checks if and rows are filled.  If a row has
	 * been filled, it returns PLACE_ROW_FILLED.  If no row has been filled, 
	 * PLACE_OK is returned.
	 */
	public int place(Piece piece, int x, int y) {
		if(committed){
			Point[] body = piece.getBody();
			//check that the piece will be in bounds 
			//if not return PLACE_OUT_BOUNDS
			if(x > width || x+piece.getWidth() > width 
					|| y > height || y+piece.getHeight() > height
					|| x < 0 || y < 0){
				sanityCheck("place(), b4 PLACE_OUT_BOUNDS");
				return PLACE_OUT_BOUNDS;
			}
			
			//piece is in bounds, so set up backup data
			lastPiecePlaced = piece;
			lastX = x;
			lastY = y;
			//piece will now be placed, so board is no longer committed
			committed= false;
			
			//go through the piece body
			for(int i = 0; i < body.length; i++){
				//get the next X and Y value to be placed
				int newX = x + (int)body[i].getX();
				int newY = y + (int)body[i].getY();
				//if these locations are already used, store the last point 
				//that was placed for the undo, and then return PLACE_BAD
				if(grid[newX][newY] == true){
					lastPointReachedInBody = i;
					return PLACE_BAD;}
				//if the location has not been used, set the grid and color
				//array accordingly
				else{
					grid[newX][newY] = true;
					colors[newX][newY] = piece.getColor();
				}
			}
			//since all points were placed, set lastPoint to the body length
			//so undo goes through all the body points
			lastPointReachedInBody = body.length;
			
			//recompute height and maxHeight
			//only for the columns that were changed
			for(int i = 0; i < piece.getWidth(); i++){
				for(int h = height-1; h >= 0; h--){
					if(grid[x+i][h] == true){
						heights[x+i] = h+1;
						if(h+1 > maxHeight){
							maxHeight = h+1;
						}
						break;
					}
				}
			}
			
			//recompute the widths, again only for the columns changed 
			for(int i = 0; i < piece.getHeight(); i++){
				for(int w = width-1; w>=0; w--){
					if(grid[w][y+i] == true){
						widths[y+i] = w+1;
						break;
					}
				}
			}
			
			//check if any rows were filled- assume they are filled until 
			//proven they are not. Stops looking if the row was not filled.
			//again, only checking the rows that were changed
			for(int row = y; row < y + piece.getHeight(); row++){
				boolean rowFilled = true;	//tells if a row was filled
				for(int col = 0; rowFilled && col < width; col++){
					//go through each location in the rows that were changed
					//if any given square is false, a row was not filled
					if(!grid[col][row]){rowFilled = false;}
				}
				//if the row was filled, return PLACE_ROW_FILLED
				if(rowFilled) {
					sanityCheck("end of place(), b4 PLACE_ROW_FILLED"); 
					return PLACE_ROW_FILLED;
				}
			}
			
			//if row was not filled, then this point is reached, and it is a 
			//normal place.  return PLACE_OK
			sanityCheck("end of place, b4 PLACE_OK");
			return PLACE_OK;
		}
		//if the board is not committed, then return PLACE_BAD
		else{
			System.out.println("Place Bad- not committed");
			return PLACE_BAD;}
	}

	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns true if any row clearing happened.
	 
	 <p>Implementation: This is complicated.
	 Ideally, you want to copy each row down
	 to its correct location in one pass.
	 Note that more than one row may be filled.
	*/
	/* LOGIC BEHIND METHOD
	 * First, the method backs up everything.
	 * Then, the method goes through and calls checkAndClearRows on every valid
	 * row.  This method, described below, checks if the given row is filled, 
	 * and if so it clears it and moves everything down accordingly.  If a row
	 * has been cleared, this method then recalculates heights and maxHeight. 
	 * Finally, the method returns true or false accordingly.
	 */
	public boolean clearRows() {
		//only works if committed
		if(!committed){
			//cannot undo piece placed, so lastPiecePlaced set to null
			lastPiecePlaced = null;	
			//back up widths, heights, grid, colors, and maxHeight 
			System.arraycopy(widths, 0, bWidths, 0, widths.length);
			System.arraycopy(heights, 0, bHeights, 0, heights.length);
			for(int g = 0; g < grid.length; g++){
				System.arraycopy(grid[g], 0, bGrid[g], 0, grid[g].length);
				System.arraycopy(colors[g], 0, bColors[g], 0, colors[g].length);
			}
			bMaxHeight = maxHeight;
			
			//goes through and calls checkAndClearRow on each row.
			boolean rowCleared = false;
			for(int row = 0; row < maxHeight; row++){
				if(checkAndClearRow(row)){
					//needs to be decremented, since a row has been cleared
					//and now the next row to be looked at is one lower.
					row--;	
					//a row has now been cleared, so rowCleared == true.
					rowCleared = true;
				}
			}
			
			//if a row has been cleared, heights and maxHeight have
			//to be recalculated.
			if(rowCleared){
				int localMaxH = 0;
				for(int c = 0; c < width; c++){
					boolean newHeightFound = false;
					for(int h = heights[c]; h >=0 && !newHeightFound; h--){
						if(grid[c][h] == true){
							heights[c] = h+1;
							if(h+1 > localMaxH){localMaxH = h+1;}
							newHeightFound = true;
						}
					}
					if(!newHeightFound){heights[c] = 0;}
				}
				maxHeight = localMaxH;
			}
			sanityCheck("end of clearRows(), !committed, so removed row(s)");
			//if a row has been cleared, returns true, if not, returns false.
			return false;
		}
		//if not committed, return false
		else{
			sanityCheck("end of clearRows, committed, so no rows removed");
			return false;
		}
	}
	
	/* Checks if a row is filled, and if it is, clears that row and moves all
	 * other rows down accordingly.
	 * 
	 * Logic behind method.
	 * First, the method checks if the row has been filled.  If it is, the
	 * method clears is by copying every row above it down one row using the 
	 * copyRows method.  Finally, it returns if a row was filled and cleared.
	 */
	private boolean checkAndClearRow(int row){
		boolean rowFilled = true;	//will hold if the row has been filled
		for(int x = 0; x < width && rowFilled; x++){
			//if the row is not filled, grid[x][row] will be false, so
			//rowFilled should be false.
			rowFilled = grid[x][row];	
		}
		//If the row was filled, it goes through and copies all valid rows
		//above row down one slot using copyRow.
		if(rowFilled){
			for(int to = row; to < maxHeight; to++){
				copyRow(to, to + 1);
			}
		}
		return rowFilled;
	}
	
	/*Copies a the row from into row to's row.
	 * Goes through each column and moves it to the correct row, both for grid
	 * and colors.  Then, it moves with entry for widths down one row as well.
	 */
	private void copyRow(int to, int from){
		for(int x = 0; x < width; x++){
			grid[x][to] = grid[x][from];
			colors[x][to] = colors[x][from];
		}
		widths[to] = widths[from];
	}


	/**
	 If a place() happens, optionally followed by a clearRows(),
	 a subsequent undo() reverts the board to its state before
	 the place(). If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	/* LOGIC BEHIND METHOD
	 * This method is specialized to be most efficient when undoing after a
	 * call to place()- should be close to O(widths*height) after a call to 
	 * place- in other words, O(1).  After a call to clearRows(), it should be
	 * about the same as it is explained in the docs.
	 * The optimization occurs in how the grid is stored for a call to undo()
	 * after place().  Instead of storing all of the grid, this method just 
	 * stores the piece that was placed, and where it was placed.  If a piece
	 * was not placed in the last method call to place() or clearRows(), null 
	 * is stored instead of a piece.  
	 * Therefore, if a piece needs to be undoed, lastPiecePlaced will not be
	 * null, if not lastPiecePlaced will be null.  If lastPiecePlaced is null,
	 * then the method does the undo described in the doc.
	 * If not, just the points in the body are removed from the grid and colors
	 * arrays.  The last index reached in the body of the piece is also stored,
	 * so if the piece is not completely placed, only points that were placed
	 * get removed.  Finally, bWidths and bHeights are copied into widths
	 * and heights, repsecitivly.  They must be copied rather than swapped
	 * because place does not back them up, (there is not need really)
	 * and thus swapping them would result in incorrect information every 
	 * other call to undo().
	 */
	public void undo() {
		if(!committed){
			//if the last piecePlaced != null, that piece needs to be removed
			if(lastPiecePlaced != null){	
				Point[] body = lastPiecePlaced.getBody();
				//goes through the piece body until the index reached by place()
				//and removes those entries from grid and colors.
				for(int p = 0; p < lastPointReachedInBody; p++){
					grid[lastX + (int)body[p].getX()]
					     [lastY + (int)body[p].getY()] = false;
					colors[lastX + (int)body[p].getX()]
					       [lastY + (int)body[p].getY()] = null;
				}
				//copies bWidths and bHeights into widths and heights.
				System.arraycopy(bWidths, 0, widths, 0, bWidths.length);
				System.arraycopy(bHeights, 0, heights, 0, bHeights.length);
			}
			//if lastPiecePlaced was null, a complete undo must be done
			else{
				//all the arrays are swapped with their backups.
				int [] temp = widths;
				widths = bWidths;
				bWidths = temp;
				
				temp = heights;
				heights = bHeights;
				bHeights = temp;
				
				boolean[][] gTemp = grid;
				grid = bGrid;
				bGrid = gTemp;
				
				Color [][] cTemp = colors;
				colors = bColors;
				bColors = cTemp;
			}
			
			//maxHeight will always equal bMaxHeight after an undo.
			maxHeight = bMaxHeight;
			
			//board is now committed
			committed = true;
			sanityCheck("end of undo");
		}
	}
	
	
	/**
	 Puts the board in the committed state.
	 See the overview docs.
	*/
	/* LOGIC
	 * Commits the board by reverting copying widths and heights into their
	 * backups.  Also sets bMaxHeight to the current maxHeight to back it up. 
	 * Grid and colors do not need to be backed up here- they will be backed
	 * up when and if necessary in clearRows().
	 */
	public void commit() {
		if(!committed){
			committed = true;
			System.arraycopy(widths, 0, bWidths, 0, widths.length);
			System.arraycopy(heights, 0, bHeights, 0, heights.length);		
			bMaxHeight = maxHeight;
			sanityCheck("end of commit- commit finished, commit now = true");
		}
		else{sanityCheck("end of commit, commit didn't do anything");}
	}
}


