
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class SquareDoodle_WillR extends JFrame implements ComponentListener {
	
	public static final int WINDOW_SIZE = 400;
	public static final int THRESHOLD = 2;
	public static double p1_x, p1_y, p2_x, p2_y, p3_x, p3_y, p4_x, p4_y;
	
	public SquareDoodle_WillR() {
		super("Square Doodle- Will Richard");
		setSize(WINDOW_SIZE,WINDOW_SIZE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.addComponentListener(this);
		int length;
		if(getHeight() < getWidth()){
			length = getHeight();
		}
		else{
			length = getWidth();
		}
		p1_x = 20;
		p1_y = length - 20;
		p2_x = 20;
		p2_y = length/2;
		p3_x = length/2;
		p3_y = length/2;
		p4_x = length/2;
		p4_y = length - 20;
		
		setVisible(true);
	}
	
	public void componentHidden(ComponentEvent e){};
	public void componentMoved(ComponentEvent e){};
	public void componentShown(ComponentEvent e){}
	public void componentResized(ComponentEvent e){
		int length;
		if(getHeight() < getWidth()){
			length = getHeight();
		}
		else{
			length = getWidth();
		}
		p1_x = 20;
		p1_y = length - 20;
		p2_x = 20;
		p2_y = length/2;
		p3_x = length/2;
		p3_y = length/2;
		p4_x = length/2;
		p4_y = length - 20;
		repaint();
	}
	
	public Point getMiddle(Point2D p1, Point2D p2) {
		return new Point((int)(p1.getX() + p2.getX())/2, (int)(p1.getY() + p2.getY())/2); 
	}

	public void paint(Graphics g) {
		super.paint(g);
		sqdraw(new Point2D.Double(p1_x, p1_y),new Point2D.Double(p2_x, p2_y), new Point2D.Double(p3_x,p3_y), new Point2D.Double(p4_x, p4_y), 0); 
	}
	
	public void sqdraw(Point2D p1, Point2D p2, Point2D p3, Point2D p4, int itteration){
		
		if (p1.distance(p2) < THRESHOLD  &&  p1.distance(p3) <  THRESHOLD && p1.distance(p4) < THRESHOLD &&
				p2.distance(p1) < THRESHOLD  &&  p2.distance(p3) <  THRESHOLD && p2.distance(p4) < THRESHOLD &&
				p3.distance(p2) < THRESHOLD  &&  p3.distance(p1) <  THRESHOLD && p3.distance(p4) < THRESHOLD){
			return;
		}
		
		Graphics g = getGraphics(); 
		g.setColor(new Color(240 - (5* itteration), 248 - (10*itteration), 255));
		g.fillRect((int)p2.getX(), (int)p2.getY(), (int)p2.distance(p3), (int)p2.distance(p1));
		
		g.setColor(Color.black);
		g.drawLine((int)p1.getX(),(int)p1.getY(),(int)p2.getX(),(int)p2.getY()); 
		g.drawLine((int)p2.getX(),(int)p2.getY(),(int)p3.getX(),(int)p3.getY()); 
		g.drawLine((int)p3.getX(),(int)p3.getY(),(int)p4.getX(),(int)p4.getY());
		g.drawLine((int)p4.getX(),(int)p4.getY(),(int)p1.getX(),(int)p1.getY());
		
		Point m23 = getMiddle(p2, p3);
		double m23_2Length = p2.distance(m23);
		Point m34 = getMiddle(p3, p4);
		double m34_4Length = p4.distance(m34);
		
		sqdraw(p2, new Point2D.Double(p2.getX(), (p2.getY() - m23_2Length)), new Point2D.Double(m23.getX(), m23.getY() - m23_2Length), m23, itteration+ 1);
		sqdraw(p4, m34, new Point2D.Double(m34.getX() + m34_4Length, m34.getY()), new Point2D.Double(p4.getX() + m34_4Length, p4.getY()), itteration+ 1 );
	}
	
	public static void main(String[] args) {
		new SquareDoodle_WillR(); 			
	}	
}
