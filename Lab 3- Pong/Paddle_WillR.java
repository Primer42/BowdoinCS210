/*Will Richard
 * 9/27/07
 * Lab 3- Pong
 * Paddle_WillR.java
 * 
 * Paddle_WillR is responsible for the paddle.  It keeps track of the paddle's absolute location and moving
 * that location, its location relative to the playing field, and when Pong1P_WillR tells it so, it takes care
 * of bouncing a ball off of it. 
 */

import java.awt.geom.Rectangle2D;

public class Paddle_WillR extends Rectangle2D.Double {
	
	//keeps track of the previous location and information of the paddle, so that it can be removed in
	//Pong's paint method
	private Paddle_WillR previousPaddle = (Paddle_WillR)this.clone();
	
	//default fields storing locations relative to playing feild
	private final String[] positions = {"BOTTOM", "TOP", "LEFT", "RIGHT"};
	
	//set to a number based upon which location relative to the play area is passed to the constructor 
	private int pos;
	
	public Paddle_WillR(double locX, double locY, double width, double height, String position){
		super(locX, locY, width, height);
		//figures out value of pos
		for(int i = 0; i < positions.length; i++){
			if(position.equalsIgnoreCase(positions[i])){
				pos = i;
				break;
			}
		}
	}
	
	//returns the previous paddle, so Pong's paint method can erase it
	protected Paddle_WillR getPrevious(){
		return previousPaddle;
	}
	
	//moves the paddle, based on it's midpoint- that way the mouse lines up with the paddle's midpoint rather than a corner
	protected void move(double mid){
		previousPaddle = (Paddle_WillR)this.clone();	//before moved, the old paddle is stored for erasing purposes
		if(pos<=1){										//if its on the bottom or top...
			setRect(mid-width/2, y, width, height);		//midpoint has a different meaning, using the width
		}
		else{											//as opposed to when its on the left or right
			setRect(mid-height/2, y, width, height);	//when the height is used
		}
	}
	
	//checks if a ball has hit the paddle, and if so, bounces it
	//uses a bouncing method more like that of real pong, giving the user more control over where the ball goes and how fast
	protected void checkBounce(Ball_WillR ball){						
		Rectangle2D.Double intersection = new Rectangle2D.Double();
		Rectangle2D.intersect(this, ball.getBounds2D(), intersection);	//makes a rectangle that is the intersection of the ball's bounds and the paddle
		if(pos<=1){														// if its on the top or bottom
			if(intersection.height>0 && intersection.width>0){			//and if the ball has intersected it, the intersection rectangle will have a positive height and width 
				double posOverall = intersection.getX() + ball.getRadius();	//finds where the ball hit overall in the x direction
				double posPaddle = posOverall-x;							//finds where that is relative to the paddle
				double distFromMid = Math.abs((posPaddle - width/2));		//finds the distance from the intersection point to the middle
				double distFromNearSide;									//finds the distance from the intersection point to the nearest edge
				if(posPaddle < width/2){
					distFromNearSide = Math.abs((x - posOverall));
					if(distFromMid > 0){
						distFromMid *= -1;
					}
				}
				else{
					distFromNearSide = Math.abs(posOverall - (x+width));
					if(distFromMid < 0){
						distFromMid *= -1;
					}
				}
				
				double percentFromNearSide = distFromNearSide / width;		//find the percent of the paddle the distances represent
				double percentFromMid = distFromMid / width;
				//based on where the paddle is relative to the play area, tells the ball to bounce
				//at speeds based on the percentages
				switch(pos){
					case 0:
						ball.bouncePaddleBottom(percentFromMid * ball.getMaxSpeed(), -(percentFromNearSide * ball.getMaxSpeed()));
						break;
						
					case 1:
						ball.bouncePaddleTop(percentFromMid * ball.getMaxSpeed(), percentFromNearSide * ball.getMaxSpeed());
						break;
				}
			}
		}
		//does the same thing as the top part, but for paddles on the sides of the play area
		else{
			if(intersection.width>0 && intersection.height>0){
				double posOverall = intersection.getY() + ball.getRadius();
				double posPaddle = posOverall-y;
				double distFromNearSide;
				double distFromMid = Math.abs((posPaddle - height/2));
				if(posPaddle < height/2){
					distFromNearSide = Math.abs((y - posOverall));
					if(distFromMid > 0){
						distFromMid *= -1;
					}
				}
				else{
					distFromNearSide = Math.abs(posOverall - (y+height));
					if(distFromMid < 0){
						distFromMid *= -1;
					}
				}
				
				if(ball.getSpeedX() > 0){
					distFromNearSide *= -1;
				}
				
				double percentFromNearSide = distFromNearSide / height;
				double percentFromMid = distFromMid / height;
				
				switch(pos){
				case 2:
					ball.bouncePaddleLeft(percentFromNearSide * ball.getMaxSpeed(), percentFromMid * ball.getMaxSpeed());
					break;
				case 3:
					ball.bouncePaddleRight(percentFromNearSide * ball.getMaxSpeed(), -(percentFromMid *ball.getMaxSpeed()));
					break;
				}
			}
		}
	}
}