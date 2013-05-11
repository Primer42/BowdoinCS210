/*Will Richard
 * 10/5/07
 * Lab 4- Breakout
 * Brick_WillR.java
 * A brick in the breakout game.  Basically a rectangle, 
 * with the extra additions of setting its height and width to 0 when "hit"
 * Also has a color, allowing for later extensions/sub classes with different colored bricks 
 * that can have new effects on game play
 */

import java.awt.geom.Rectangle2D;
import java.awt.*;

public class Brick_WillR extends Rectangle2D.Double {
	
	private static double brickWidth = 50;		//sets initial values for width
	private static double brickHeight = 10;		//and height
	private Color brickColor;					//allows for different colored bricks
	
	//constructors
	public Brick_WillR(double locX, double locY){
		super (locX, locY, brickWidth, brickHeight);	//sets up the brick at the correct location and corect width and height
		brickColor = Color.black;						//sets up brick with default color- other constructors can be made in sub classes that override this color choice
	}
	
	//same as previous, but with color choice
	public Brick_WillR(double locX, double locY, Color c){	
		super(locX, locY, brickWidth, brickHeight);
		brickColor = c;
	}
	
	//sets the static variables of brickWidth and brickHeight
	public static void setBrickDimentions(double w, double h){
		brickWidth = w;
		brickHeight = h;
	}
	
	//allows the brick to be hit
	public void hit(){
		setRect(x,y,0,0);	//resets the rectangle to be at same place, but with width and height of 0- doesn't really matter where brick actually is as it cannot be hit with dimensions of 0
	}
	
	//returns whether or not the brick has been hit
	public boolean hasBeenHit(){
		return getWidth() == 0 && getHeight() == 0;	//width and height should not be 0 unless the brick has been hit
	}
	
	//getters
	public Color getColor(){
		return brickColor;
	}
}
