import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.*;

/**Sudoku_WillR.java
 * Lab 7- Sudoku
 * @author Will Richard
 * 
 * This class solves a game of Sudoku.  The SudokuBoard class gets a board from 
 * a .txt file, where numbers given at the beginning of the game are given, and 
 * blank spaces are marked as a 0.  This class creates a visual representation 
 * of that board.  It displays the given numbers in one color, then begins to 
 * solve the board, displaying each new number in a different color.  It solves
 * the board using a slightly modified depth-first search.  It gets a valid move
 * from the SudokuBoard_WillR class, displays the move, and stores it in a
 * stack.  If the SudokuBoard_WillR cannot return a valid move, the most recent
 * entry into the stack is removed, and the board calculates a new valid move 
 * that has not been tried yet.  
 */

public class Sudoku_WillR extends JFrame implements ChangeListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SudokuBoard_WillR board;	//the board- holds values of the baord
	private Stack<SudokuMove_WillR> moves;	//moves that have been done
	
	//A slider to adjust the speed of the game, along with its associated values
	private JSlider slider;
	private static final int MIN_DELAY = 0;
	private static final int MAX_DELAY = 200;
	private static final int INIT_DELAY = 25;
	private int delay;
	
	//assuming a square board, this is the length of one side
	private static final int WINDOW_DIMENTION = 650;
	//Only part of the window will be used to display the square baord, and this
	//variable will hold the length of the side of that area.
	private int contentPaneDimention;
	
	//Colors and fonts for the board display.
	protected static final Color background = Color.gray;
	protected static final Color originalNumbers = Color.black;
	protected static final Color foundNumbers = Color.red;
	protected Font font;
	
	public Sudoku_WillR(String boardFileName){
		//sets up the window
		super("Lab 7- Sudoku");
		setSize(WINDOW_DIMENTION, WINDOW_DIMENTION);
		setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Sets up the board and move stack
        board = new SudokuBoard_WillR(boardFileName);
        moves = new Stack<SudokuMove_WillR>();
        
        //setting up the slider to adjust game speed.
        delay = INIT_DELAY;
        slider = new JSlider(JSlider.VERTICAL,MIN_DELAY,MAX_DELAY,INIT_DELAY);
        Font sliderFont = new Font("Serif", Font.ITALIC, 15);
        slider.setFont(sliderFont);
        slider.addChangeListener(this);
        
        slider.setMajorTickSpacing(20);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        
        //Temporarily set the window visible, so we can get the length of the
        //side of the area to display the board
        setVisible(true);
        //We want the area for the board to be slightly smaller than the area
        //of the content pane, to allow space for the slider.
        contentPaneDimention = getContentPane().getHeight() -  40;
        setVisible(false);
        
        //set up the font for the numbers
        font = new Font("numberFont", Font.PLAIN, contentPaneDimention / 10);
		
        //JLabels are placed at each location where a number needs to be placed
        //this sets up these JLabels
		for(int r = 0; r < board.getSideLength(); r++){
			for(int c = 0; c < board.getSideLength(); c++){
				JLabel label = new JLabel();
				label.setLocation(contentPaneDimention * c/board.getSideLength(),
						contentPaneDimention* r/board.getSideLength());
				label.setSize(new Dimension(
						contentPaneDimention / board.getSideLength(), 
						contentPaneDimention / board.getSideLength()));
				label.setFont(font);
				getContentPane().add(label);
			}
		}
		//An error was occurring with the last JLabel added to the content pane
		//This additional JLabel ensures that no data will be lost.
		getContentPane().add(new JLabel());
				
        getContentPane().add(slider, BorderLayout.EAST);
		
		setVisible(true);
		//method sets up the initial state of the board
		setOriginalState();
	}
	
	/*Sets up the initial state of the board.
	 * Goes through the board and finds any non-0 value- in other words any 
	 * value given at the beginning of the game.  It then sets up the correct
	 * label with the correct value and color, and repaints the board to show 
	 * this original state. 
	 */
	
	private void setOriginalState(){
		for(int r = 0; r < board.getSideLength(); r++){
			for(int c = 0; c < board.getSideLength(); c++){
				if(board.getSquareValue(r, c) != 0){
					JLabel label = getLabel(r, c);

					label.setForeground(originalNumbers);
					label.setText(""+board.getSquareValue(r, c));
				}
			}
		}
		repaint();
	}
	
	/* This method returns the label that represents the number at location
	 * row,col.
	 * The content pane has an array of components stored in it, and includes 
	 * accesser methods for those components.  This method uses those methods to
	 * get the JLabel at row, col.
	 */
	protected JLabel getLabel(int row, int col){
		return (JLabel) getContentPane().getComponentAt(
				contentPaneDimention * col/board.getSideLength(), 
				contentPaneDimention* row/board.getSideLength());
	}

	/* This method paints the graphics in the window.
	 * It paints the JFrame itself, including all the objects that have been 
	 * added to the JFrame or its Content Pane.  It then prints borders for the
	 * game board. 
	 */
	public void paint(Graphics g){
		//Paint the JFrame and all objects within it.
		super.paint(g);
		
		//get the graphics for the content pane, and make it into a Graphics2D
		Graphics contentGraphics = getContentPane().getGraphics();
		Graphics2D g2d = (Graphics2D) contentGraphics;
		
		//Paint major border-lines
		g2d.setColor(Color.black);
		g2d.setStroke(new BasicStroke(10));
		g2d.drawLine(0, 5, contentPaneDimention, 5);
		g2d.drawLine(0, contentPaneDimention * 1/3, 
				contentPaneDimention, contentPaneDimention * 1/3);
		g2d.drawLine(0, contentPaneDimention * 2/3, 
				contentPaneDimention, contentPaneDimention * 2/3);
		g2d.drawLine(0, contentPaneDimention, 
				contentPaneDimention, contentPaneDimention);
		g2d.drawLine(0, 0, 0, contentPaneDimention);
		g2d.drawLine(contentPaneDimention * 1/3, 0, 
				contentPaneDimention * 1/3, contentPaneDimention);
		g2d.drawLine(contentPaneDimention * 2/3, 0, 
				contentPaneDimention * 2/3, contentPaneDimention);
		g2d.drawLine(contentPaneDimention, 0, 
				contentPaneDimention, contentPaneDimention);
		
		//Paints minor border-lines
		g2d.setStroke(new BasicStroke(2));
		g2d.drawLine(0, contentPaneDimention * 1/9, 
				contentPaneDimention, contentPaneDimention * 1/9);
		g2d.drawLine(0, contentPaneDimention * 2/9, 
				contentPaneDimention, contentPaneDimention * 2/9);
		g2d.drawLine(0, contentPaneDimention * 4/9, 
				contentPaneDimention, contentPaneDimention * 4/9);
		g2d.drawLine(0, contentPaneDimention * 5/9, 
				contentPaneDimention, contentPaneDimention * 5/9);
		g2d.drawLine(0, contentPaneDimention * 7/9, 
				contentPaneDimention, contentPaneDimention * 7/9);
		g2d.drawLine(0, contentPaneDimention * 8/9, 
				contentPaneDimention, contentPaneDimention * 8/9);
		g2d.drawLine(contentPaneDimention * 1/9, 0, 
				contentPaneDimention * 1/9, contentPaneDimention);
		g2d.drawLine(contentPaneDimention * 2/9, 0, 
				contentPaneDimention * 2/9, contentPaneDimention);
		g2d.drawLine(contentPaneDimention * 4/9, 0, 
				contentPaneDimention * 4/9, contentPaneDimention);
		g2d.drawLine(contentPaneDimention * 5/9, 0, 
				contentPaneDimention * 5/9, contentPaneDimention);
		g2d.drawLine(contentPaneDimention * 7/9, 0, 
				contentPaneDimention * 7/9, contentPaneDimention);
		g2d.drawLine(contentPaneDimention * 8/9, 0, 
				contentPaneDimention * 8/9, contentPaneDimention);
	}		
	
	/*Solves the Sudoku game
	 * Does this using a modified depth-first search.  The method gets a valid
	 * move from the board using the nextMove method, and pushes that on the 
	 * Stack, moves.  
	 * If that move is not a valid move, it undo's the last move
	 * by popping it from the stack, and then setting the int startNumber to 
	 * a number 1 greater than that of the move that was just popped.  This tells 
	 * the board to try to put in a number greater than the one just tried, and
	 * this it does not return moves it has returned previously.
	 * If the move given by the board is a valid move, it pushes it on the stack
	 * and then calls the drawNew method on that move.  It then resets the
	 * start number, so all possible moves are considered.
	 * Finally, at the end of both of these loops, the current time in
	 * milliseconds is stored so that there can be a delay between moves.  The
	 * delay is set by the slider.  As long as the difference between the
	 * current time in milliseconds and the stored time is less than the 
	 * dictated delay, no next Move is considered.
	 */
	private void solve(){
		long t = System.currentTimeMillis();	//instantiate t- the time
		int startNumber = 1;	//the start number- will change to avoid repeats
		
		//method gets a first move, pushes it on the stack, and draws it
		SudokuMove_WillR firstMove = board.nextMove(startNumber); 
		moves.push(firstMove);
		drawNew(firstMove);
		
		while(!board.isDone()){		//while the board isn't solved
			while (System.currentTimeMillis() - t < delay) {}	//adds the delay
			
			//gets a new move
			SudokuMove_WillR nextMove = board.nextMove(startNumber);
			//if it isn't a valid move
			if((nextMove.equals(new SudokuMove_WillR(0,0,0)))){
				if(!moves.isEmpty()) {	//and there are still moves in the stack
					//undo the last move by popping it
					SudokuMove_WillR badMove = moves.pop();
					clear(badMove);		//and clear it
					//reste the start number to avoid repeats
					startNumber = badMove.getValue()+1;
				}
				//if there are no more moves in the stack, then the board
				//cannot be solved, so this breaks the !board.isDone() loop
				else{break;}
			}
			//if it is a valid move
			else{
				//add that move to the stack, draw it, and reset the start #
				moves.push(nextMove);
				drawNew(nextMove);
				startNumber = 1;
			}
			//get the current time so as to allow for delay
			t = System.currentTimeMillis();			
		}
	}
	
	//Responds to the slider's position, and sets the delay between each move
	//accordingly
	public void stateChanged(ChangeEvent e) {
	    JSlider source = (JSlider)e.getSource();
	    if (!source.getValueIsAdjusting()) {
	    	delay = (int)source.getValue();
	    }
	}
	
	/*draws a new move, one that is given during the solving process
	 * First, this method sets the board with the new value.
	 * It then gets the label in the GUI that represents this move, changes
	 * that label's text to reflect the change, and repaints the board.
	 */
	protected void drawNew(SudokuMove_WillR move){
		//set the board correctly
		board.setSquare(move.getRow(), move.getCol(), move.getValue());
		
		//get the label representing the move, and change it accordingly
		JLabel label = getLabel(move.getRow(),move.getCol());
		label.setForeground(foundNumbers);
		label.setText("" + move.getValue());
		repaint();
	}
	
	/*undos a move that was given by the solve method
	 * Does so by setting the board to the default value, 0.
	 * The resets the appropriate label to an empty string to reflect the lack
	 * of value in this square.  Finally, it repaints the board.
	 */
	protected void clear(SudokuMove_WillR move){
		//set the location on the board to 0
		board.setSquare(move.getRow(), move.getCol(), 0);
		
		//reset the right label
		JLabel label = getLabel(move.getRow(), move.getCol());
		label.setText("");
		repaint();
	}
	
	//main class
	public static void main(String[] args){
		Sudoku_WillR s = new Sudoku_WillR(
				//"/Users/Will/Documents/workspace/Comp 210/Lab 7- Sudoku/" +
				"./Sudoku.txt");
		s.solve();
	}
}