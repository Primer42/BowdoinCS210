/*Will Richard
 * 9/27/07
 * Lab 3- Pong
 * Ball_WillR.java
 * 
 * Ball_WillR takes care of the ball part of Pong.  It keeps track of the ball's location and its speed in the 
 * x and y directions, as well as handling various bouncing situations, both off of walls and paddles.
 */

import java.awt.geom.Ellipse2D;

public class Ball_WillR extends Ellipse2D.Double {
	
	//keeps track of the speed compnents of the ball
	private double speedX;
	private double speedY;
	
	//records the max speed of the ball- can be changed to adjust difficulty
	private final double MAXSPEED = 10;
	
	//keeps track of previous position of ball for erasing purposes
	private Ball_WillR previousBall; //= (Ball_WillR)this.clone();
	
	//keeps track of which way the ball is moving, so that it doesn't get stuck
	private boolean movingInPosYDir;
	private boolean movingInPosXDir;
	
	//constructs the ball
	public Ball_WillR(double locX, double locY, double radius){
		super(locX, locY, radius*2, radius*2);
		speedX = 4;
		movingInPosXDir = true;
		speedY = 4;
		movingInPosYDir = true;
	}
	
	//moves the ball the specified amount of spaces, after storing the old space for erasing
	protected void move(){
		previousBall = (Ball_WillR)this.clone();
		x += speedX;
		y += speedY;
	}
	
	//allows the ball to bounce off of the bottom or top boundry of the pay area and sets all necessary variables
	protected void bounceVert(){
		speedX = speedX;
		speedY = -speedY;
		movingInPosYDir = !movingInPosYDir;		
	}
	
	//allows the ball to bounce off of the right or left sides of the play area and sets all variables to necessary values
	protected void bounceSide(){
		speedX = -speedX;
		speedY = speedY;
		movingInPosXDir = !movingInPosXDir;
	}
	
	//lets the ball bounce off a paddle located at the bottom of the screen, based on speed values determined in paddle class
	protected void bouncePaddleBottom(double sX, double sY){
		if(movingInPosYDir){
			speedX = sX;
			speedY = sY;
			movingInPosYDir = false;
		}
	}
	
	//lets the ball bounce off a paddle located at the top of the screen, based on speed values determined in paddle class\
	protected void bouncePaddleTop(double sX, double sY){
		if(!movingInPosYDir){
			speedX = sX;
			speedY = sY;
			movingInPosYDir = true;
		}
	}
	//lets the ball bounce off a paddle located at the left of the screen, based on speed values determined in paddle class
	protected void bouncePaddleLeft(double sX, double sY){
		if(!movingInPosXDir){
			speedX = sX;
			speedY = sY;
			movingInPosXDir = true;
		}
	}
	
	//lets the ball bounce off a paddle located at the right of the screen, based on speed values determined in paddle class
	protected void bouncePaddleRight(double sX, double sY){
		if(movingInPosXDir){
			speedX = sX;
			speedY = sY;
			movingInPosXDir = false;
		}
	}
	
	//getters for all necessary variables in class
	protected Ball_WillR getPrevious(){
		return previousBall;
	}
	
	public double getRadius(){
		return width/2;
	}
	
	public double getSpeedX(){
		return speedX;
	}
	
	public double getSpeedY(){
		return speedY;
	}
	
	public double getMaxSpeed(){
		return MAXSPEED;
	}
}