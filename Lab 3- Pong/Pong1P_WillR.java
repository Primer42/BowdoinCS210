/*Will Richard
 * 9/27/07
 * Lab 3- Pong
 * Pong1P_WillR.java
 * 
 * This class takes care of running the game- it sets up the playing field, is responsible for telling each object
 * in the play area to move or bounce, and checks whether or not the game is over. 
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.*;

@SuppressWarnings("serial")
public class Pong1P_WillR extends JFrame implements MouseMotionListener, ActionListener {
	
	Graphics g;
	
	private javax.swing.Timer timer;
	private final int MOVE = 15;
	
	private Paddle_WillR paddle;	//the paddle for this game
	private Ball_WillR ball;		//the ball for this game
	
	// the boundries for the play area, top bottom left and right
	private final Line2D.Double TOP_BOUND = new Line2D.Double(0, 21, 400, 21);
	private final Line2D.Double RIGHT_BOUND = new Line2D.Double (400, 0, 400, 520);
	private final Line2D.Double LEFT_BOUND = new Line2D.Double (0, 0, 0, 520);
	private final Line2D.Double BOTTOM_BOUND = new Line2D.Double(0, 520, 400, 520);
	
	public Pong1P_WillR(){
		super("Lab 3- Pong 1 Player");
		setSize(400, 520);
		setResizable(false);							//makes it so the user cannot change the window size- nicer looking
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.gray); 
        
        addMouseMotionListener(this);
		
        //setting up the timer
		timer = new Timer(MOVE, this);
		timer.setInitialDelay(1000);
		timer.setCoalesce(true);
		timer.start();
		
		paddle = new Paddle_WillR(200, 490, 100, 10, "BOTTOM");		//sets up paddle at bottom of screen- could be top or either side
		ball = new Ball_WillR(300, 100, 15);						//sets up the ball
		
		repaint();		//paints it all for the first time
		
		setVisible(true);
	}
	
	//when the timer gets tripped, each MOVE ms, this gets called
	public void actionPerformed(ActionEvent e){
		ball.move();				//first ball is moved
		checkBorders(ball);			//then we check if the ball has crossed a border
		paddle.checkBounce(ball);	//then we check if the ball has hit the paddle
		checkGameOver();			//even if it has hit the paddle, if the game is over, the game is over
		repaint();					//if game isn't over, we repaint the screen
	}
	
	public void mouseMoved(MouseEvent e){
		paddle.move(e.getX());		//moves the paddle based upon the mouse position
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
	
	//this checks whether or not the ball pass has intersected a border- the parameter allows for multiple balls easily
	private void checkBorders(Ball_WillR ball){
		if(TOP_BOUND.intersects(ball.getBounds2D())){
			ball.bounceVert();
		}
		if(RIGHT_BOUND.intersects(ball.getBounds2D()) || LEFT_BOUND.intersects(ball.getBounds2D())){
			ball.bounceSide();
		}
	}
	
	//checks if the game is over, based upon whether any of the balls have touched the bottom bound
	//and brings up a window telling the user that they are done
	//might need to be changed depending on rules with multiple balls
	private void checkGameOver(){
		if(BOTTOM_BOUND.intersects(ball.getBounds2D())){
			JOptionPane.showMessageDialog(this,
				    "Game Over :(",
				    "Game Over :(",
				    JOptionPane.ERROR_MESSAGE);
			System.exit(0);

		}
	}
	
	//takes care of rendering the window, first by removing the shadow of the previous shape, then drawing a new one
	public void paint(Graphics g){
		g = getGraphics();
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setColor(Color.gray);
		g2d.fill(ball.getPrevious());
		g2d.fill(paddle.getPrevious());
		
		g2d.setColor(Color.black);
		g2d.draw(TOP_BOUND);
		g2d.fill(ball);
		g2d.fill(paddle);
	}
	
	//makes the program easily runnable
	public static void main(String[] args){
		new Pong1P_WillR();
	}
}