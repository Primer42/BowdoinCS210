import java.util.Stack; 
import java.util.LinkedList; 


public class Maze {

	final static char C=' ', X='x', S='s', E='e', V='.';

	final static int START_I = 1, START_J = 1; 
	final static int END_I = 2, END_J = 9; 
	private static char[][] maze = {
		{X, X, X, X, X, X, X, X, X, X}, 
		{X, S, C, C, C, C, C, C, C, X},
		{X, C, C, C, X, C, X, X, C, E},
		{X, C, X, X, X, C, X, X, C, X}, 
		{X, C, C, C, C, X, X, X, C, X},
		{X, X, X, X, C, X, X, X, C, X},
		{X, X, X, X, C, X, C, C, C, X},
		{X, X, C, X, C, X, X, C, C, X}, 
		{X, X, C, C, C, C, C, C, C, X},  
		{X, X, X, X, X, X, C, X, X, X}
	};

	public int size() {
		return maze.length;
	}

	public void print() {
		for (int i=0; i<size(); i++) {
			for (int j=0; j<size(); j++) {
				System.out.print(maze[i][j]);
				System.out.print(' ');
			}
			System.out.println();
		}
	}

	public void mark(int i, int j) {
		assert(i>= 0 && i< size() && j>-0 && j< size()); 
		maze[i][j] = V;
	}

	public boolean isMarked(int i, int j) {
		assert(i>= 0 && i< size() && j>-0 && j< size()); 
		return (maze[i][j] == V);
	}
	
	public boolean isClear(int i, int j) {
		assert(isInMaze(i,j)); 
		return (maze[i][j] != X && maze[i][j] != V);
	}
	public boolean isClear(MazePos pos) {
		return isClear(pos.i(), pos.j());
	}

	//true if cell is within maze 
	public boolean isInMaze(int i, int j) {
		if (i >= 0 && i<size() && j>= 0 && j<size()) return true; 
		else return false;
	}

	//true if cell is within maze 
	public boolean isInMaze(MazePos pos) {
		return isInMaze(pos.i(), pos.j());
	}

	public boolean isFinal( int i, int j) {
		return (i== Maze.END_I && j == Maze.END_J);
	}
	public boolean isFinal(MazePos pos) {
		return isFinal(pos.i(), pos.j());
	}

	public char[][] clone() {
		
		char[][] mazeCopy = new char[size()][size()]; 
		for (int i=0; i< size(); i++) 
			for (int j=0; j<size(); j++)
				mazeCopy[i][j] = maze[i][j];
		return mazeCopy; 
	}

	public void restore(char[][] savedMaze) {
		for (int i=0; i< size(); i++) 
			for (int j=0; j<size(); j++)
				maze[i][j] = savedMaze[i][j];
	}
	
	
	public static void main(String[] args) {
		
		Maze maze = new Maze();
		maze.print();
		
		System.out.println("\n\nFind a path using a stack: ");
		maze.solveStack();

		System.out.println("\n\nFind a path using a queue: ");
		maze.solveQueue();

		System.out.println("\n\nFind a path recursively: ");
		maze.solveRec();
		
	}


	//THE GOAL IS TO FIND A PATH FROM START TO END 

	//**************************************************
	//this solution uses a stack to keep track of possible
	//states/positions to explore; it marks the maze to remember the
	//positions that it's already explored.
	public void solveStack() {
		
		//save the maze
		char[][] savedMaze = clone(); 

		//declare the locations stack 
		Stack<MazePos> candidates = new Stack<MazePos>(); 
		MazePos st = new MazePos(START_I, START_J);
		candidates.push(st); 

		while (!candidates.empty()) {

			//pop the next state to explore
			st = candidates.pop(); 

			System.out.print("current state: ");
			st.print();

			if (isFinal(st)) //found the solution 
				break; //exit the loop

			//mark this location as visited 
			mark(st.i(), st.j());
			
			//add its neighbors that are clear and within maze
			MazePos next; 
			next = st.north(); 
			if (isInMaze(next) && isClear(next))  candidates.push(next); 
			next = st.east(); 
			if (isInMaze(next)  && isClear(next)) candidates.push(next); 
			next = st.south(); 
			if (isInMaze(next)  && isClear(next)) candidates.push(next); 
			next = st.west(); 
			if (isInMaze(next)  && isClear(next)) candidates.push(next); 
		}
		
		//if we are here, then either st is final, or not
		if (isFinal(st)) 	System.out.println("Got it!");
		else 	System.out.println("No solution, you're stuck in the maze");
		print();

		//restore the maze
		restore(savedMaze);
	}



	
	//**************************************************
	//this solution uses a QUEUE to keep track of possible
	//states/positions to explore; it marks the maze to remember the
	//positions that it's already explored.
	public void solveQueue() {
		
		//save the maze
		char[][] savedMaze = clone(); 

		//declare the locations stack 
		LinkedList<MazePos> candidates = new LinkedList<MazePos>(); 
		MazePos st = new MazePos(START_I, START_J);
		candidates.add(st); 

		while (!candidates.isEmpty()) {

			//pop the next state to explore
			st = candidates.remove(); 

			System.out.print("current state: ");
			st.print();

			if (isFinal(st)) //found the solution 
				break; //exit the loop

			//mark this location as visited 
			mark(st.i(), st.j());
			
			//add its neighbors that are clear and within maze
			MazePos next; 
			next = st.north(); 
			if (isInMaze(next) && isClear(next))  candidates.add(next); 
			next = st.east(); 
			if (isInMaze(next)  && isClear(next)) candidates.add(next); 
			next = st.south(); 
			if (isInMaze(next)  && isClear(next)) candidates.add(next); 
			next = st.west(); 
			if (isInMaze(next)  && isClear(next)) candidates.add(next); 
		}
		
		//if we are here, then either st is final, or not
		if (isFinal(st)) 	System.out.println("Got it!");
		else 	System.out.println("No solution, you're stuck in the maze");
		print();

		//restore the maze
		restore(savedMaze);
	}




	//**************************************************
	//solve using recursion
	public void solveRec() {

		char[][] savedMaze = clone();

		if (solve(new MazePos(START_I, START_J))) 
			System.out.println("Got it: "); 
		else System.out.println("You're stuck in the maze."); 
		print(); 

		restore(savedMaze);
	}

	//works by marking the maze
	public boolean solve(MazePos pos) {
		//base case
		if (!isInMaze(pos)) return false;  //outside boundaries
		if (isFinal(pos)) return true;
		if (!isClear(pos)) return false; 
		
		//otherwise, go into recursion 
		//first mark position, to avoid recursive loops 
		mark(pos.i(), pos.j());
		
		
		//first try north
		if (solve(pos.north())) return true; 
		//if not, try east 
		if (solve(pos.east())) return true; 
		//if not, try south
		if (solve(pos.south())) return true; 
		//finally, west 
		return solve(pos.west()); 	
	}
	
   
};



	

class MazePos {
	int i, j; 
	
	public MazePos(int i, int j) {
		this.i = i; 
		this.j = j;
	};
	public int i() { return i;}

	public int j() { return j;}

	public void print() {
		System.out.println("(" + i + "," + j + ")");
	}
	public MazePos north() {
		return new MazePos(i-1, j);
	}
	public MazePos south() {
		return new MazePos(i+1, j);
	}
	public MazePos east() {
		return new MazePos(i, j+1);
	}
	public MazePos west() {
		return new MazePos(i, j-1);
	}


};