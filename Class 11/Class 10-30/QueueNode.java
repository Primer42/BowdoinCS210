/*
  This class is designed to help solve the missionaries and cannibals
  problem.  You can make nodes to put in the queue, and check whether
  the nodes are legal, or whether you've hit the finishing condition.

  Adapted by Laura Toma, from Eric Chown.
*/



public class QueueNode {
    private int cannibals1;       // cannibals on side 1, etc.
    private int cannibals2;
    private int missionaries1;
    private int missionaries2;
    private int boatside;         // side boat is on
    private int pathcost;         // cost of current path
    private String path;          // collection of moves made
    
    
    public QueueNode(int c1, int c2, int m1, int m2, int bs, int pc, String p) {
	cannibals1 = c1;
	missionaries1 = m1;
	boatside = bs;
	cannibals2 = c2;
	missionaries2 = m2;
	pathcost = pc;
	path = p;
    }
    
    public int getCannibals1() { return cannibals1;}
    
    public int getCannibals2() { return cannibals2;}
	
    public int getMissionaries1() { return missionaries1;}
    
    public int getMissionaries2() { return missionaries2;}
    
    public int getCost() { return pathcost;}
    
    public int getBoatSide() { return boatside;}
    
    public String getPath() { return path;}
    
    // useful for debugging this prints out a state
    public String output() {
	String outstring = "Side 1 has "+cannibals1+" cannibals and "
	    +missionaries1+" missionaries" +
	    "\nSide 2 has "+cannibals2+" cannibals and "
	    +missionaries2+" missionaries" +
	    "\nThe boat is on side "+boatside+" path cost is "+pathcost + "\n";
	return outstring;
    }
    
    public String toString() {
	return output();
    }
    
    // checks to see if everyone has made it accross
    public boolean isDone() {
	if ((cannibals2 == 3) && (missionaries2 == 3) && (boatside == 2)) return true;
	return false;
    }
    

    // checks for legal configurations
    public boolean isLegal() {
	// make sure those cannibals don't eat the missionaries!
	if ((cannibals1 > missionaries1) && (missionaries1 > 0)) return false;
	if ((cannibals2 > missionaries2) && (missionaries2 > 0)) return false;
	// don't do magic with the numbers
	if (cannibals1 < 0) return false;
	if (cannibals2 < 0) return false;
	if (cannibals1 > 3) return false;
	if (cannibals2 > 3) return false;
	if (missionaries1 < 0) return false;
	if (missionaries2 < 0) return false;
	if (missionaries1 > 3) return false;
	if (missionaries2 > 3) return false;
	return true;
    }	
}