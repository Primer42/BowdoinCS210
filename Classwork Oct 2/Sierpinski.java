/* 
 * Recursion example: Sierpinski triangle
 * 
 * Laura Toma
 * oct 2007 
 */
import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class Sierpinski extends JFrame {
	public static final int WINDOW_SIZE = 500; 
	public static final int THRESHOLD=3;
	public static int P1_x, P1_y, P2_x, P2_y, P3_x, P3_y;

	public Sierpinski() {
		super("Sierpinski"); 
		setSize(WINDOW_SIZE, WINDOW_SIZE);
		
		//set initial triangle corners. Should reset on window resize, etc.
		P1_x = (int)getSize().getWidth()/2;; 
		P1_y = 20; 
		P2_x = 20; 
		P2_y = (int)getSize().getHeight() - 20;
		P3_x = (int)getSize().getWidth() - 20; 
		P3_y = (int)getSize().getHeight() - 20;

		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public Point getMiddle(Point p1, Point p2) {
		return new Point((int)(p1.getX() + p2.getX())/2, (int)(p1.getY() + p2.getY())/2); 
	}

	public void paint(Graphics g) {
		super.paint(g);
		sdraw(new Point(P1_x, P1_y),new Point(P2_x, P2_y), new Point(P3_x,P3_y)); 
	}

	public void sdraw(Point p1, Point p2, Point p3) {
		System.out.println("drawing:" + p1 + p2 + p3);
		//termination condition
		if (p1.distance(p2) < THRESHOLD  &&  p1.distance(p3) <  THRESHOLD && 
			p2.distance(p3) < THRESHOLD)  return; 

		//else 
		//draw the current triangle
		Graphics g = getGraphics(); 
		g.drawLine((int)p1.getX(),(int)p1.getY(),(int)p2.getX(),(int)p2.getY()); 
		g.drawLine((int)p2.getX(),(int)p2.getY(),(int)p3.getX(),(int)p3.getY()); 
		g.drawLine((int)p3.getX(),(int)p3.getY(),(int)p1.getX(),(int)p1.getY()); 

		//recursively draw the 3 smaller corner triangles
		Point m12 = getMiddle(p1, p2);
		Point m23 = getMiddle(p2, p3);
		Point m31 = getMiddle(p3, p1);
		sdraw(p1, m12, m31);
		sdraw(p2, m23, m12); 
		sdraw(p3, m31, m23);
	}

	public static void main(String[] args) {
		new Sierpinski(); 			
	}
}
