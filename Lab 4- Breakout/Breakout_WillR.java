/*Will Richard
 * 10/5/07
 * Lab 4- Breakout
 * Breakout_WillR.java
 * 
 * Breakout_WillR takes care of keeping track of the bricks, ball and paddle in 
 * the playing field, as well as collisions between these objects, and the 
 * boundaries of the play feild.
 * Also renders all objects that need rendering.
 */

import javax.swing.*;
import javax.swing.Timer;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;

@SuppressWarnings("serial")
public class Breakout_WillR extends JFrame implements MouseMotionListener, ActionListener {
	
	private Vector<Col_WillR> brickFeild;				//vector of Col_WillR's
	private javax.swing.Timer timer;
	private Ball_WillR ball;
	private Paddle_WillR paddle;
	private Graphics g;
	
	private final int FRAME_WIDTH = 600;				//window width
	private final int FRAME_HEIGHT = 550;				//window height
	private final int MOVE = 20;						//delay between timer firings 
	private final Line2D.Double TOP_BOUND = new Line2D.Double(0, 21, FRAME_WIDTH, 21);							//top boundary
	private final Line2D.Double RIGHT_BOUND = new Line2D.Double (FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT);	//right boundary
	private final Line2D.Double LEFT_BOUND = new Line2D.Double (0, 0, 0, FRAME_HEIGHT);							//left boundary
	private final Line2D.Double BOTTOM_BOUND = new Line2D.Double(0, FRAME_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT);	//bottom boundary
	private final Color BACKGROUND = Color.gray;		//background color
	
	private final double COL_WIDTH = 75;				//the width of each Col_WillR of bricks
	private final double BRICK_HEIGHT = 10;				//height of each Brick_WillR
	private final double SPACE_BETWEEN_COLS = 10;		//space left empty between Col_WillR's
	private final int NUM_COLS = (int) ((FRAME_WIDTH)/(COL_WIDTH + SPACE_BETWEEN_COLS));	//determines max number of columns that could fit in the window
	
	private final int MIN_NUM_BRICKS_PER_COL = 4;		//the min number of bricks allowed in each column
	private int currNumBricksPerCol;					//keeps track of the # of bricks in each column for this level
	private int level;									//counter to keep track of what level the player is on
	
	private final double BALL_LOC_X = 10;					//the ball's starting X location
	private final double BALL_LOC_Y = FRAME_HEIGHT *2/3;	//the ball's starting Y location, putting it at a good place based upon the window size
	private final double BALL_RADIUS = 10;					//the ball's radius
	
	private int currColIndex;								//keeps track of which column the ball is under, and could hit
	
	//constructor
	public Breakout_WillR(){
		super("Lab 4- Breakout");						//sets up window title bar
		setSize(FRAME_WIDTH, FRAME_HEIGHT);				//sets the size based upon constants above
		setResizable(false);							//makes it so the user cannot change the window size- nicer looking
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	//exits the JVM on window close
        setBackground(BACKGROUND); 						//keep track of background color for erasing purposes
        
        addMouseMotionListener(this);					//adds the mouse motion listener, so that the mouse can be heard
        
        //setting up the timer
		timer = new Timer(MOVE, this);
		timer.setInitialDelay(1000);
		timer.setCoalesce(true);
		timer.start();
		
		//sets up the initial condiditons for the game
		level = 0;										//start on level 0- COUNTING LEVELS IN 0 BASED NUMBER SYSTEM
		brickFeild = new Vector<Col_WillR>(NUM_COLS);	//instantiates up the brickfeild, filled with Col_WillR's
		currNumBricksPerCol = MIN_NUM_BRICKS_PER_COL;	//sets the current number of bricks per column to the min number of bricks per column
		fillBrickFeild(true);							//sets up each Col_WillR in brickfeild, true meaning it is the first time we are doing this
		
		paddle = new Paddle_WillR(200, 490, 100, 10, "BOTTOM");		//sets up paddle at bottom of screen- could be top or either side
		ball = new Ball_WillR(BALL_LOC_X, BALL_LOC_Y, BALL_RADIUS);	//sets up the ball
		
		repaint();		//paints it all for the first time
		
		setVisible(true);	//set frame visible
	}
	
	//fills the brickFeild with the necessary number of columns
	private void fillBrickFeild(boolean firstTime){
		//uses previous brickfeilds to determine correct size, so if this is the first call to fillBrickFeild, this part should not be entered
		if(!firstTime){	
			//this next if statements makes sure that the columns stay at a constant # of bricks after a certain point
			if((brickFeild.firstElement().getColHeight() > (FRAME_HEIGHT * (7/12)))){	//figures out if the current columns in the brickfeild are too long
				//if they are not, figure it out by adding the level number to the min number of bricks
				currNumBricksPerCol = MIN_NUM_BRICKS_PER_COL + level;
			}
			else{
				//if the columns are too long, keep the number of bricks per column constant 
				currNumBricksPerCol = currNumBricksPerCol;
			}
		}
		
		brickFeild.clear();			//clears the old brickfeild 
		for(int i = 0; i < NUM_COLS; i++){		//sets up the new brickfeild
			Col_WillR col = new Col_WillR( ((COL_WIDTH+SPACE_BETWEEN_COLS)*i)+10, COL_WIDTH, BRICK_HEIGHT, currNumBricksPerCol);
			brickFeild.add(col);
		}
	}
	
	//moves the paddle based upon the mouse position
	public void mouseMoved(MouseEvent e){
		paddle.move(e.getX());		//uses x pos of mouse to move paddle that much
		//this makes it so the paddle cannot be moved off of the screen by comparing the paddle's position relative
		//to the borders of the playing field, and if the paddle has started to move off, this section moves it back on 
		if(LEFT_BOUND.relativeCCW(paddle.getX(), paddle.getY()) <=0){
			paddle.move(LEFT_BOUND.getX1() + (paddle.getWidth()/2) );
		}
		if(RIGHT_BOUND.relativeCCW(paddle.getX()+paddle.getWidth(), paddle.getY()) >=0){
			paddle.move(RIGHT_BOUND.getX1()- (paddle.getWidth()/2) );
		}
		repaint();	//re-render the window
	}
	
	//this makes it so that if the user decides to click and drag the mouse, the game still works
	public void mouseDragged(MouseEvent e){
		mouseMoved(e);
	}
	
	//gets called by timer ever MOVE milliseconds- takes care of moving ball and gameplay
	public void actionPerformed(ActionEvent e){
		ball.move();				//first ball is moved
		checkBorders(ball);			//then we check if the ball has crossed a border
		if(ball.getY() > brickFeild.firstElement().getColHeight()){	//if the ball is under where the bricks are...
			paddle.checkBounce(ball);	//then we check if the ball has hit the paddle
			checkGameOver();			//even if it has hit the paddle, if the game is over, the game is over
		}
		else{						//if the ball is where the bricks are...
			updateCurrCol(ball);	//figure out which column the ball is under
			checkBricksVert(ball);	//checks if the ball is hitting a brick at the top or bottom of the column
			checkBricksHor(ball);	//checks if the ball is hitting a brick in an adjacent column on its side
			checkNextLevel();		//check if all bricks have been hit, and if the next level should be started
		}
		repaint();					//repaint the screen, assuming the checkGameOver hasn't ended the game
	}
	
	//check whether or not the ball has hit a side of the playing feild
	private void checkBorders(Ball_WillR ball){
		//if the ball has hit the top boundary, bounce it in a vertical manner
		if(TOP_BOUND.intersects(ball.getBounds2D())){
			ball.bounceVert();
		}
		//if the ball has hit a side boundary, bounce it in a horezontal manner
		if(RIGHT_BOUND.intersects(ball.getBounds2D()) || LEFT_BOUND.intersects(ball.getBounds2D())){
			ball.bounceSide();
		}
	}
	
	//checks whether the game is over, based upon whether or not the ball has hit the bottom boundary
	private void checkGameOver(){
		//if the ball has hit the boundary
		if(BOTTOM_BOUND.intersects(ball.getBounds2D())){
			//pop up a message telling the player the game is over, and exit the JVM when OK is pressed
			JOptionPane.showMessageDialog(this,
				    "Game Over :(",
				    "Game Over :(",
				    JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
	//updates which column the ball is under- used to determine which bricks the ball can hit
	private void updateCurrCol(Ball_WillR ball){
		double distanceFromBallToCol=0;			//the distance from the ball to the column- initially set to 0
		double ballLoc = ball.getCenterX();		//gets the ball's center X coordinate
		double colLoc=0;						//the X location of the Column
		int i;									//a counter/ the index of the current column
		for(i = 0; i < brickFeild.size()-1; i++){
			colLoc = (brickFeild.get(i)).getX();									//sets up colLoc
			distanceFromBallToCol = ballLoc - colLoc;								//calulated the distance from the ball to the column
			if(distanceFromBallToCol >0 && distanceFromBallToCol < COL_WIDTH){		//if the ball is between the 2 ends of the column, this is the current column		
				break;
			}
		}
		currColIndex = i;		//this should be the index in brickFeild that is the current column
	}
	
	//checks if the top or bottom brick in the current column have been hit
	private void checkBricksVert(Ball_WillR ball){
		Col_WillR currCol = brickFeild.get(currColIndex);			//gets the current column based on the currColIndex
		Vector<Brick_WillR> bricks = currCol.getCurrVertBricks();	//gets the top and bottom bricks in this column
		for(int i = 0; i< bricks.size(); i++){						//checks if the ball has hit either of these bricks
			Brick_WillR currBrick = bricks.get(i);
			if(currBrick.intersects(ball.getBounds2D())){			//if it has hit the brick, erase the brick
				eraseBrick(currBrick);
				if(Math.abs(ball.getCenterY() - currBrick.getCenterY()) < currBrick.getHeight()){	//if it hits the brick on its side...
					ball.bounceSide();								//bounce in a sideways manner
				}
				else{
					ball.bounceVert();								//else, bounce in a vertical manner
				}
			}
		}
	}
	
	//checks if any of the bricks in either of the adjacent columns have been hit
	private void checkBricksHor(Ball_WillR ball){
		Col_WillR currCol;								//sets up the variables for the current column
		Vector<Brick_WillR> bricks;						//and current set of bricks
		if(currColIndex > 0){							//if it is not the furthest left, check the column one to the left
			currCol = brickFeild.get(currColIndex-1);
		}
		else{											//otherwise check this column, because it is furthest left,
			currCol = brickFeild.get(currColIndex);		// and that way an out of bounds exception will not occur
		}
		
		bricks = currCol.getCurrHorBricks();			//get the bricks that could be hit from the side from the column
		if(bricks != null){								//make sure there are bricks in the vector
			for(int i = 0; i<bricks.size(); i++){		//go through the vector
				Brick_WillR currBrick = bricks.get(i);	
				if(currBrick.intersects(ball.getBounds2D())){	//if a given brick intersects the ball
					eraseBrick(currBrick);						//erase it, using the eraseBrick method
					ball.bounceSide();							//then bounce the ball in a horezontal manner
					return;										//ball has bounced, so return
				}
			}
		}
		
		if(currColIndex < brickFeild.size()-1){				//again, checking if this column is the furthest right
			currCol = brickFeild.get(currColIndex+1);		//and setting the column accordingly as to not get an out of bounds exception
		}
		else{
			currCol = brickFeild.get(currColIndex);
		}
		bricks = currCol.getCurrHorBricks();
		if(bricks != null){									//again, go through the bricks and check if the ball has hit them 
			for(int i = 0; i<bricks.size(); i++){
				Brick_WillR currBrick = bricks.get(i);
				if(currBrick.intersects(ball.getBounds2D())){	//and react accordingly
					eraseBrick(currBrick);
					ball.bounceSide();
					return;
				}	
			}
		}
	}
	
	//takes care of erasing the brick
	private void eraseBrick(Brick_WillR brick){			
		g = getGraphics();						
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(BACKGROUND);								//set color to background to erase the brick
		g2d.fill(brick);										//erase the brick
		(brickFeild.get(currColIndex)).removeBrick(brick);		//run the removeBrick method to remove the brick correctly from the col- DIFFERENT FROM remove()
	}
	
	//checks if all the bricks have been hit, and if so starts the next level
	private void checkNextLevel(){
		for(int i = 0; i < brickFeild.size(); i++){		//goes through all the columns
			if(!brickFeild.get(i).allHit()){			//and sees if all of the bricks in each column have been
				return;									//if one column has one brick left, then not all have been hit, to return w/o starting next level
			}
		}
		
		//if the method hasn't returned, next level should be started
		
		//remove visual and shadow of the ball
		g = getGraphics();								
		g.setColor(BACKGROUND);
		((Graphics2D) g).fill(ball);
		((Graphics2D) g).fill(ball.getPrevious());
		
		ball = new Ball_WillR(BALL_LOC_X, BALL_LOC_Y, BALL_RADIUS);		//resets the ball to the beginning location
		level++;														//increments the level
		fillBrickFeild(false);											//refills the brick feild, this time with a false to signify that this is not the first brickFeild fill
		timer.restart();												//restarts the time- allows for the initial delay to occur again
	}
	
	//paints the screen with all necessary conditions and aspects
	public void paint(Graphics g){
		g = getGraphics();
		Graphics2D g2d = (Graphics2D) g;
		
		//erases previous shadows of moving parts
		g2d.setColor(BACKGROUND);
		g2d.fill(ball.getPrevious());
		g2d.fill(paddle.getPrevious());
		
		//draw the new positions of moving parts
		g2d.setColor(Color.black);
		g2d.fill(ball);
		g2d.fill(paddle);
		
		//draw the brickfeild
		for(int j = 0; j < brickFeild.size(); j++){
			Col_WillR col = brickFeild.get(j);
			for(int i = 0; i < col.size(); i++){
				Brick_WillR brick = col.get(i);
				g2d.setColor(brick.getColor());
				g2d.fill(brick);
			}
		}
	}
	
	//main method to make running easier
	public static void main(String[] args){
		new Breakout_WillR();
	}

}