/*Will Richard
 * 10/5/07
 * Lab 4- Breakout
 * Col_WillR.java
 * Col_WillR is a vector of Brick_WillR's, with some other enhancements that keeps track of some locations,
 * column height, and if all the bricks have been hit.  Also returns certain bricks, depending on their position
 * and relivance to searching for collisions
 */

import java.util.Vector;

@SuppressWarnings("serial")
public class Col_WillR extends Vector<Brick_WillR>{
	
	private final double SPACE_BETWEEN_BRICKS = 10;			//default space between bricks in the column
	private double XLoc;									//the x location of the column
	private int bricksHit;									//counter that keeps track of how many bricks have been hit
	private double colHeight;								//the height that the column takes up
	
	public Col_WillR(double colXLoc, double width, double brickHeight, int numBricks){
		
		XLoc = colXLoc;											//instantiates the XLoc
		
		Brick_WillR.setBrickDimentions(width, brickHeight);		//makes sure the brick dimensions are correct
				
		double currY = 21;	//current vert locaiton of the brick being placed- needs to be 21 so that bricks do not get hidden behind title bar of window
		bricksHit = 0;		//instantiates the number of bricks hit
		colHeight = (numBricks * (brickHeight + SPACE_BETWEEN_BRICKS) + currY);		//figures out the column height, based upon all of its parts
		
		//goes through and adds the correct number of bricks at the correct spaces to the correct locations 
		for(int i = 0; i < numBricks; i++){
			add(new Brick_WillR(colXLoc, currY));
			currY += (brickHeight +  SPACE_BETWEEN_BRICKS);
		}
	}
	
	//returns the top and bottom bricks in the column
	protected Vector<Brick_WillR> getCurrVertBricks(){
		Vector<Brick_WillR> bricks  = new Vector<Brick_WillR>();	//eventually will be returned
		int i;			//counter used to determine correct brick
		
		i = size()-1;						//start with bottom location
		while(i>=0){						//while the i is still valid
			if(!(get(i)).hasBeenHit()){		//see if each brick has been hit
				bricks.add(get(i));			//if it hasn't, add it and break the loop
				break;
			}
			else{
				i--;						//otherwise, move up one brick 
			}
		}
		
		i = 0;								//now start with top
		while (i<size()){					//do the same thing, but moving down
			if(!(get(i)).hasBeenHit() && get(i) != bricks.get(0)){		//same check, but also check the same brick as previously has been reached
				bricks.add(get(i));
				break;
			}
			else{
				i++;
			}
		}
		return bricks;
	}
	
	//returns all bricks that could be hit from the side in this column
	protected Vector<Brick_WillR> getCurrHorBricks(){
		Vector<Brick_WillR> bricks = new Vector<Brick_WillR>();
		for(int i = 0; i<size(); i++){		//go through the column
			if(!(get(i)).hasBeenHit()){		//see if each brick has been hit
				bricks.add(get(i));			//if it hasn't, add to return vector
			}
		}
		return bricks;						//return the vector
	}
	
	//"hits" a brick
	protected void removeBrick(Brick_WillR brick){
		brick.hit();			//lets the brick know it has been hit
		bricksHit ++;			//add one to the counter of bricks that have been hit
	}
	
	//returns whether or not all bricks in this column have been hit or not
	protected boolean allHit(){
		return bricksHit == size();		//if the counter of bricks that have been hit equals the size, all bricks have been hit
	}
	
	//getters
	protected double getX(){
		return XLoc;
	}
	
	protected double getColHeight(){
		return colHeight;
	}
}