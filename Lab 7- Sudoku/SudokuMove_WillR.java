/**SudokuMove_WillR
 * Lab 7- Sudoku
 * @author Will Richard
 * Stores the basic variables of a Sudoku move, i.e. its location and its value.
 */

public class SudokuMove_WillR {
	
	private int row;
	private int col;
	private int value;
	
	//Default constructor- instantiates all varaibles to 0.
	public SudokuMove_WillR(){
		row = 0;
		col = 0;
		value = 0;
	}
	
	public SudokuMove_WillR(int row, int column, int value){
		this.row = row;
		this.col = column;
		this.value = value;
	}
	
	//getters
	public int getRow(){
		return row;
	}
	
	public int getCol(){
		return col;
	}
	
	public int getValue(){
		return value;
	}
	
	//.equals method- returns true if all variables are equal in both moves
	public boolean equals(Object o){
		boolean isEqual = true;
		SudokuMove_WillR move = (SudokuMove_WillR)o;
		if(this.row != move.row) isEqual = false;
		if(this.col != move.col) isEqual = false;
		if(this.value != move.value) isEqual = false;
		return isEqual;
	}
	
	//Basic toString, returns a string with the location and value
	public String toString(){
		return row + ", " + col + ": " + value;
	}
}
