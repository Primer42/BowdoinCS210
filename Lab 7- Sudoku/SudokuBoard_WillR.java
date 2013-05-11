import java.io.*;
import java.util.*;

/* SudokuBoard_WillR
 * Lab 7- Sudoku
 * @author Will Richard
 * 
 * This class takes care of holding a Sudoku board.  It stores the values of the
 * board in a row major 2D array.  It is also figures out what is the next valid
 * move, and has getters to get any given value.
 * 
 * Takes a file address in the construtor, which should link to a .txt file
 * representing the board with 0's representing blank spaces and " " between
 * each number
 */
public class SudokuBoard_WillR {
	
	//assumes square board
	protected final int boardSideLength = 9;
	protected int[][] board;
	
	//makes a board with the maxiumum possible number of numbers in file while 
	//still having a square board
	public SudokuBoard_WillR(String fileName){
		//Sets up a scanner to read through a file for the numbers of the board 
		File f = new File(fileName);
        Scanner sc = null;
        try {
            sc = new Scanner(f);
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
            System.exit(0);
        }        
        
        //Sets up the 2D Array
        board = new int[boardSideLength][boardSideLength];
        
        //Fills the board with the ints from the file.
        for(int r = 0; r < boardSideLength; r++){
    		for(int c = 0; c < boardSideLength; c++){
    			if (sc.hasNextInt()) {
    				board[r][c] = sc.nextInt();
    			}
        	}
        } 
	}
	
	//Sets a given value of the square
	public void setSquare(int r, int c, int value){
		board[r][c] = value;
	}
	
	//Gets the value of a certain square
	public int getSquareValue(int r, int c){
		return board[r][c];
	}
	
	//Gets the length of the side of the board, assuming a square board
	public int getSideLength(){
		return boardSideLength;
	}
	
	/*Figures out what the next valid move is.
	 * Goes through the board and finds a blank square.  Once it finds a blank
	 * square, it goes through starting at the startNumber and see if that 
	 * number or a number greater than it could legally fit in that square by 
	 * making a new SudokuMove_WillR and then calling moveOK on that move.  If 
	 * a valid move is found, then it returns that move. If no valid move is 
	 * found, or if no blank squares are found, it returns a blank move where 
	 * all values in it are 0.
	 */
	protected SudokuMove_WillR nextMove(int startNumber){
		for(int r = 0; r < board.length; r++){
			for(int c = 0; c < board[r].length; c++){
				if(board[r][c] == 0){
					for(int i = startNumber; i <= 9; i++){
						if(moveOk(new SudokuMove_WillR(r, c, i))){
							return new SudokuMove_WillR(r, c, i);
						}
					}
					return new SudokuMove_WillR(0,0,0);
				}
			}
		}
		return new SudokuMove_WillR(0,0,0);
	}
	
	/*Checks is a given move is legal.
	 * Needs to check if the value in the move is duplicated in the same row,
	 * column or 3x3 square as the move.
	 * Starts by going through the column, checking for the value.
	 * Then it goes through the row, checking for the value.
	 * Finally, it goes through the 3x3 square by getting an array of ints that
	 * has all the values in the correct 3x3 square from the getContainingBox
	 * method.
	 */
	private boolean moveOk (SudokuMove_WillR move){
		//Goes through the column
		int r = 0;
		int c = move.getCol();
		boolean moveOk = true;
		for(; r < boardSideLength && moveOk; r++){
			if(board[r][c] == move.getValue()) moveOk = false;
		}
		
		//goes through the row
		r = move.getRow();
		c = 0;
		for(; c < boardSideLength && moveOk; c++){
			if(board[r][c] == move.getValue()) moveOk = false;
		}
		
		//get the box, then go through the box
		int[] box = getContainingBox(move);
		for(int i= 0; i < box.length && moveOk; i++){
			if(box[i] == move.getValue()) moveOk = false;
		}
		
		return moveOk;
	}
	
	/*Gets the value of the same box as the passed move.
	 * Depending on which position in the square the move is, it gets the 
	 * correct rows and columns that make up this square.  It then goes through 
	 * those locations and stores those values in an array of ints, the returns
	 * that array.
	 */
	private int[] getContainingBox(SudokuMove_WillR move){
		int[] rows = new int[3];
		int[] cols = new int[3];
		int[] boxValues = new int[9];
		int r = move.getRow();
		int c = move.getCol();
		
		/*r%3 will give the location row-wise in the square- will be 0 if it is
		*in the top row, r%3 = 1 if in middle row in square, r%3 = 2 if in 
		*bottom row of square.  Fills the array of ints rows accordingly based
		on the answer to r%3.*/
		switch(r%3) {
		case 0:
			rows[0] = r;
			rows[1] = r+1;
			rows[2] = r+2;
			break;
		case 1:
			rows[0] = r-1;
			rows[1] = r;
			rows[2] = r +1;
			break;
		case 2:
			rows[0] = r-2;
			rows[1] = r-1;
			rows[2] = r;
			break;
		}
		
		//Does the exact same thing as the previous switch statement, but with 
		//columns and c instead of rows and r.
		switch(c%3) {
		case 0: 
			cols[0] = c;
			cols[1] = c+1;
			cols[2] = c+2;
			break;
		case 1:
			cols[0] = c-1;
			cols[1] = c;
			cols[2] = c +1;
			break;
		case 2:
			cols[0] = c-2;
			cols[1] = c-1;
			cols[2] = c;
			break;
		}
		//goes through those rows and columns and stores their values in the
		//array boxValues, then returns boxValues.
		int k = 0;
		for(int i = 0; i < rows.length; i++){
			for(int j = 0; j < cols.length; j++){
				if(k < boxValues.length){
					boxValues[k] = board[rows[i]][cols[j]];
					k++;
				}
			}
		}	
		
		return boxValues;
	}
	
	/*This method determines if the board is complete.
	 * If there is still a blank square, the method returns false, 
	 * otherwise returns true.
	 */
	protected boolean isDone(){
		boolean isDone = true;
		for(int r = 0; r < boardSideLength && isDone; r++){
			for(int c = 0; c < boardSideLength && isDone; c++){
				if(getSquareValue(r, c) == 0){
					isDone = false;
				}
			}
		}
		return isDone;
	}
}