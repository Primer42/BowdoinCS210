import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SamuraiSudokuBoard_WillR extends SudokuBoard_WillR{
	
	private final int boardSideLength = 21;

	public SamuraiSudokuBoard_WillR(String fileName) {
		super(fileName);
		File f = new File(fileName);
        Scanner sc = null;
        try {
            sc = new Scanner(f);
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
            System.exit(0);
        }        
               
        board = new int[boardSideLength][boardSideLength];
        
        for(int r = 0; r < boardSideLength; r++){
    		for(int c = 0; c < boardSideLength; c++){
    			if (sc.hasNextInt()) {
    				board[r][c] = sc.nextInt();
    			}
        	}
        }
        
	}
}
