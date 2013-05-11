import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.event.*;


/**
 Debugging client for the Piece class.
 The JPieceTest component draws all the rotations of a tetris piece.
 JPieceTest.main()  creates a frame  with one JPieceTest for each
 of the 7 standard tetris pieces.
 

*/
class JPieceTest extends JComponent {
	protected Piece root;	
	

	public JPieceTest(Piece piece, int width, int height) {
		super();
		
		setPreferredSize(new Dimension(width, height));

		root = piece;
	}

	/**
	 Draws the rotations from left to right.
	 Each piece goes in its own little box.
	*/
	public final int MAX_ROTATIONS = 4;
	public void paintComponent(Graphics g) {
            // get the dimensions of the rectangles
            int height = (int)(getSize().getHeight());
            int width = (int)(getSize().getWidth() / MAX_ROTATIONS);
            Piece finger = root;
            int i = 0;
            // loop through all of the rotations
            do {
                drawPiece(g, finger, new Rectangle(i * width, 0, width, height));
                finger = finger.nextRotation();
                i++;
            } while (!(root.equals(finger)));
	}
	
	/**
	 Draw the piece inside the given rectangle.
	*/
	private void drawPiece(Graphics g, Piece piece, Rectangle r) {
            // extract the dimensions into ints
            int height = r.height;
            int width = r.width;
            int bh = height / 5;
            int bw = width / 5;
            Point[] p = piece.getBody();
	    int[] skirt = piece.getSkirt();
            // draw each of the blocks
            for (int i = 0; i < p.length; i++) {
                if (p[i].y == skirt[p[i].x]) 
                    g.setColor(Color.yellow);
                else
                    g.setColor(Color.black);
                g.fillRect(r.x + bw * p[i].x + 1, r.y + height - bh * p[i].y - bh - 1, bw-2, bh-2);
            }
            g.setColor(Color.red);
            g.drawString("w:"+piece.getWidth()+"h:"+piece.getHeight(), r.x+1, height - 1);
	}	


	/**
	 Draws all the pieces by creating a JPieceTest for
	 each piece, and putting them all in a frame.
	*/
	static public void main(String[] args)
	
	{
		JFrame frame = new JFrame("Piece Tester");
		JComponent container = (JComponent)frame.getContentPane();
		
		// Put in a BoxLayout to make a vertical list
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		Piece[] pieces = Piece.getPieces();
		
		for (int i=0; i<pieces.length; i++) {
			JPieceTest test = new JPieceTest(pieces[i], 375, 75);
			container.add(test);
		}
		
		// Size the window and show it on screen
		frame.pack();
		frame.setVisible(true);
		
		// Quit on window close
		frame.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			}
		);
	}
}

