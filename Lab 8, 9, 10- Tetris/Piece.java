// Piece.java

import java.awt.*;
import java.util.*;

/**
 An immutable representation of a tetris piece in a particular rotation.
 Each piece is defined by the blocks that make up its body.
 See the Tetris-Architecture.html for an overview.
 
 This is the starter file version -- a few simple things are filled in already
 
 @author	Nick Parlante
 @author        Eric Chown
 @version	1.1, April 12, 2005
*/
public final class Piece {
/*
 Implementation notes:
 -The starter code does out a few simple things for you
 -Store the body as a Point[] array
 -Do not assume there are 4 points in the body -- use array.length
 to keep the code general
*/
	private Point[] body;    // The body of the piece, each point is a coordinate specifying a block
	private int[] skirt;     // Each element specifies how high the piece will land in the corresponding column
	private int width;       // The width of the piece for the current rotation
	private int height;      // The height of the piece for the current rotation
	private Piece next;	 // The "next" rotation - note this is how a "piece" is really a list
	private Piece previous;
	
	private Color color;
	
	static private Piece[] pieces;	// singleton array of first rotations
	
	
	
	
	/**
	 Defines a new piece given the Points that make up its body.
	 Makes its own copy of the array and the Point inside it.
	 Does not set up the rotations.
	 
	 This constructor is PRIVATE -- if a client
	 wants a piece object, they must use Piece.getPieces().  getPieces() will therefore make
	 all of the calls to the constructor.
         
         As with all constructors, your variables should be initialized here.  This
         means you'll need to calculate width and height as well as setting up the
         skirt (doing these things once in the constructor means you don't have to do
         them on the fly during game play).  The one exception to this is the "next"
         variable.  You'll want to set that in the pieceRow method.
	*/
	private Piece(Point[] points) {
		body = points;
		color = Color.black;
		
		//calculate width & height
		//width and height are going to be equal to the max X value and Y 
		//value, so these max values are found first
		int maxX = 0;
		int maxY = 0;
		for(int i = 0; i < body.length; i++){
			if((int)body[i].getX() > maxX){
				maxX = (int)body[i].getX(); 
			}
			if((int)body[i].getY() > maxY){
				maxY = (int)body[i].getY(); 
			}
		}
		//width and height should be 1-based, so 1 is added to the max values
		width = maxX+1;
		height = maxY+1;
		
		/* calculate the skirt
		 * Skirt will be the same length as the width by its definition.
		 * The value stored in the skirt for each given column, or x value,
		 * will be the minimum y value for that column.  To find this, the 
		 * method goes through the skirt, each slot in the skirt representing
		 * an x value.  The method finds an initial, arbitrary y value that is
		 * in the correct column.  It then continues going through the body,
		 * looking for points in the correct column but with smaller y values
		 * then the found value, thus when it finishes, it will have found the
		 * smallest y value in that column.  It then stores this min value
		 * in the skirt array.  It continues doing this until all columns have 
		 * been checked, which also means the skirt will be full.
		 */
		skirt = new int[width];
		for(int x = 0; x < skirt.length; x++){
			int minY=0;
			//get an initial minY value
			int i = 0; 	//counter for for-loops
			for(; i < body.length; i++){
				//if the point has the correct x value, sore its y value
				if(body[i].getX() == x) {
					minY = (int)body[i].getY();
					//initial value has been found, break this loop and start
					//looking for smaller values.
					break;
				}
			}
			//see if there and any y vales smaller than minY
			for(;i < body.length; i++){
				//if the point has the correct x value and a smaller y value
				//than the inital value, store this points y value as the min
				if(body[i].getX() == x && body[i].getY() < minY) 
					minY = (int)body[i].getY();
			}
			//at this point, the minimum y value has been found, so store it.
			skirt[x] = minY;
		}
	}
	
	private Piece(Point[] points, Color c){
		this(points);
		color = c;
	}

    /**
     Returns the width of the piece measured in blocks.
    */
	public int getWidth() {
		return(width);
	}
	
    /**
     Returns the height of the piece measured in blocks.
    */
	public int getHeight() {
		return(height);
	}

    /**
     Returns a pointer to the piece's body. The caller
     should not modify this array.
    */
	public Point[] getBody() {
		return(body);
	}
	
    /**
     Returns a pointer to the piece's skirt. For each x value
     across the piece, the skirt gives the lowest y value in the body.
     This useful for computing where the piece will land.
     The caller should not modify this array.
    */
	public int[] getSkirt() {
		return(skirt);
	}


	/**
	 Returns a piece that is 90 degrees counter-clockwise
	 rotated from the receiver.
	 
	 <p>Implementation:
	 The Piece class pre-computes all the rotations once.
	 This method just hops from one pre-computed rotation
	 to the next in constant time.
	*/	
	public Piece nextRotation() {
		return next;
	}
	
	public Piece previousRotation() {
		return previous;
	}
	
	public Color getColor(){
		return color;
	}
	
	
	/**
	 Returns true if two pieces are the same --
	 their bodies contain the same points.
	 Interestingly, this is not the same as having exactly the
	 same body arrays, since the points may not be
	 in the same order in the bodies. Used internally to detect
	 if two rotations are effectively the same.
	 
	 This method checks if a given point in this Pieces is in the other Piece's
	 body.  If it is, then it looks for the next point in it's body.  If not,
	 it stops looking and the pieces are determined to be not equal.
	*/
	public boolean equals(Piece other) {
		//stores if the pieces are equal, as far as we know
		boolean areEqual = true;
		Point[] thisBody = this.getBody();
		Point[] otherBody = other.getBody();
		//goes through this body
		for(int i = 0; i < thisBody.length && areEqual; i++){
			boolean found = false;
			//looks for a given point in the other body- stops if the point 
			//has been found
			for(int j = 0; j < otherBody.length && !found; j++){
				//if the 2 points are .equals, the point has been found
				found = thisBody[i].equals(otherBody[j]);
			}
			//if the point was found, the pieces are still  equal, as far as we
			//know.  If it was not found, the pieces are definately not equal.
			areEqual = found;
		}
		return areEqual;
	}

    /**
    *  This is where most of your work will be done.  pieceRow takes the initial
    *  rotation of a piece and should create all of the rest of the rotations in
    *  a kind of circular list.  Essentially you get
    *  the body array of the initial configuration of the Piece, you need to calculate
    *  the other body arrays.
    *  
    *  Logic:
    *  Uses makesRotation to make and store the rotation for the starter.  Then,
    *  while that rotation isn't the same as the starter, it continues to
    *  makeRotations for each new piece.  Once you get a rotation that equals
    *  the starter, no more rotations are made, the final assignments of next
    *  and previous are made, and the starter is returned.
    */
    public static Piece pieceRow(Piece starter) {
    	
    	Piece nextPiece = starter.makeRotation();
    	
    	while(!nextPiece.equals(starter)){
    		nextPiece = nextPiece.makeRotation();
    	}
    	
    	/*at this point, nextPiece.equals(starter) == true.  Therefore, the
    	 * starter's previous should be nextPiece.previous, and 
    	 * nextPiece.previous's next should be starter.
    	 */
    	starter.previous = nextPiece.previous;
    	nextPiece.previous.next = starter;
    	return starter;
    }
    
    /* 	This method makes, and returns, the rotation of this piece.  Used as a
     * 	helper for pieceRow.  This also takes care of correctly assigning next
     * and previous.
     * 
     * 	Logic:
     *  Uses a linear transformation to rotate the piece.  For each point (x,y)
     *  the rotation ends up being (-y,x).  The problem with this is linear
     *  transformations use a 1- based number system.  This means that before 
     *  and after the transformation is done, the coordinates of a given point
     *  must be put into a 1-based form.  The other problem is that the piece
     *  is now in the -x region of space, so after the transformation is done,
     *  the point farthest over in the x direction is found, and the absolute 
     *  value of its x coordinate is added to each point, thereby moving the 
     *  piece into the +x quadrant and keeping the bottom left corner (0,0). 
     *  
     *  This method works by creating a new body for the starter, doing what is
     *  described above.  Then, while the rotations is not the same as the 
     *  starter (as determined by the .equals method) it keeps making rotations 
     *  using the strategy above.  Once the rotated pieces is .equals to the 
     *  starter, that piece's next variable is set to the starter, and the 
     *  starter is returned.
     *  
     *  This method also does clockwise rotations, and stores those Pieces in 
     *  the variable previous.  When a rotation is made, the unrotated Piece
     *  is stored in the rotation's previous variable.
     *  
     *  Once all this is done, the new, rotated piece is returned.
     */
    
    private Piece makeRotation(){
    	Point[] nextBody = new Point[getBody().length];
    	for(int i = 0; i < getBody().length; i++){
    		Point oldPoint = getBody()[i];
    		//make the starter body's points 1-based
    		oldPoint.translate(1,1);
    		//determine new, rotated point and add it to newBody
    		Point newPoint = new Point((int)(-oldPoint.getY()),
    				(int)(oldPoint.getX()));
    		//make newPoint and starterPoint 0 based again
    		newPoint.translate(-1, -1);
    		oldPoint.translate(-1, -1);
    		//add newPoint to newBody
    		nextBody[i] = newPoint;
    	}
    	//move all the points in newBody over into quadrant 1
    	//find most negative x value
    	int minX = (int) nextBody[0].getX();
    	for(int i = 1; i < nextBody.length; i++){
    		if((int)nextBody[i].getX() < minX){
    			minX = (int) nextBody[i].getX();
    		}
    	}
    	//make that min value positive
    	int xToAdd = Math.abs(minX);
    	//add that value to the x values of all of the points
    	for(int i = 0; i < nextBody.length; i++){
    		nextBody[i].translate(xToAdd, 0);
    	}
    	
    	Piece nextPiece = new Piece(nextBody, color);
    	this.next = nextPiece;
    	nextPiece.previous = this;
    	return nextPiece;
    }

	
	/**
	 Returns an array containing the first rotation of
	 each of the 7 standard tetris pieces.
	 The next (counterclockwise) rotation can be obtained
	 from each piece with the {@link #nextRotation()} message.
	 In this way, the client can iterate through all the rotations
	 until eventually getting back to the first rotation.
	*/
	public static Piece[] getPieces() {
        // Makes seven calls to pieceRow for each of the seven standard Tetris pieces.  
		// Places the results of each call into an array and returns the array.
		pieces = new Piece[] {
			pieceRow(new Piece(parsePoints("0 0	0 1	0 2	0 3"), Color.red)),	// 0
			pieceRow(new Piece(parsePoints("0 0	0 1	0 2	1 0"), Color.green)),	// 1
			pieceRow(new Piece(parsePoints("0 0	1 0	1 1	1 2"), Color.blue)),	// 2
			pieceRow(new Piece(parsePoints("0 0	1 0	1 1	2 1"), Color.yellow)),	// 3
			pieceRow(new Piece(parsePoints("0 1	1 1	1 0	2 0"), Color.orange)),	// 4
			pieceRow(new Piece(parsePoints("0 0	0 1	1 0	1 1"), Color.cyan)),	// 5
			pieceRow(new Piece(parsePoints("0 0	1 0	1 1	2 0"), Color.magenta)),	// 6
		};
		return pieces;
	}
	

	/**
	 Given a string of x,y pairs ("0 0	0 1	0 2	1 0"), parses
	 the points into a Point[] array.
	 (Provided code)
	*/
	private static Point[] parsePoints(String string) {
	    // could use Arraylist here, but use vector so works on Java 1.1
		Vector points = new Vector();
		StringTokenizer tok = new StringTokenizer(string);
		try {
			while(tok.hasMoreTokens()) {
				int x = Integer.parseInt(tok.nextToken());
				int y = Integer.parseInt(tok.nextToken());
				
				points.addElement(new Point(x, y));
			}
		}
		catch (NumberFormatException e) {
			throw new RuntimeException("Could not parse x,y string:" + string);	// cheap way to do assert
		}
		
		// Make an array out of the Vector
		Point[] array = new Point[points.size()];
		points.copyInto(array);
		return(array);
	}
}
