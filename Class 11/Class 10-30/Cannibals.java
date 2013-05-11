/* 
   Cannibals.java
   
   Author:	Laura Toma		
   Description:	
*/


public class Cannibals  {


    public Cannibals()  {
	Queue stateQueue = new Queue();    // queue for breadth-first search
	
	//initial state of the world
	QueueNode current = new QueueNode(3, 0, 3, 0, 1, 0, "Start state\n");     
	
	//put initial state in the queue
	stateQueue.enqueue(current); 
	
	int numChecked = 0;

	while (!stateQueue.isEmpty()) {
	    current = (QueueNode)stateQueue.dequeue();
	    if(current != null){
	    	numChecked++;
	    	if (current.isDone()) break; //exit the loop

	    	//otherwise, keep testing..
	    	stateQueue.enqueue(moveTwoCannibals(current));
	    	stateQueue.enqueue(moveOneCannibal(current));
	    	stateQueue.enqueue(moveTwoMissionaries(current));
	    	stateQueue.enqueue(moveOneMissionary(current));
	    	stateQueue.enqueue(moveOneMissionaryAndOneCannibal(current));
	    
	    }
	}
	System.out.println(current.getPath() + "Cost = " + current.getCost());
	System.out.println("queue size = " + stateQueue.size());
	System.out.println("situations checked: " + numChecked);
    }
    
    private QueueNode moveTwoCannibals(QueueNode node){
    	if(node.getBoatSide() == 1){
    		QueueNode newNode = new QueueNode(node.getCannibals1()-2, node.getCannibals2()+2, node.getMissionaries1(), node.getMissionaries2(), 2, node.getCost()+1, node.getPath() + "2 cannibals moved to side 2\n");
    		if(newNode.isLegal()){return newNode;}
    	}
    	else{
    		QueueNode newNode = new QueueNode(node.getCannibals1()+2, node.getCannibals2()-2, node.getMissionaries1(), node.getMissionaries2(), 1, node.getCost()+1, node.getPath() + "2 cannibals moved to side 1\n");
    		if(newNode.isLegal()){return newNode;}
    	}
    	return null;
    }
    
    private QueueNode moveOneCannibal(QueueNode node){
    	if(node.getBoatSide() == 1){
    		QueueNode newNode = new QueueNode(node.getCannibals1()-1, node.getCannibals2()+1, node.getMissionaries1(), node.getMissionaries2(), 2, node.getCost()+1, node.getPath() + "1 cannibal moved to side 2\n");
    		if(newNode.isLegal()){return newNode;}
    	}
    	else{
    		QueueNode newNode = new QueueNode(node.getCannibals1()+1, node.getCannibals2()-1, node.getMissionaries1(), node.getMissionaries2(), 1, node.getCost()+1, node.getPath() + "1 cannibal moved to side 1\n");
    		if(newNode.isLegal()){return newNode;}
    	}
    	return null;
    }
    
    private QueueNode moveTwoMissionaries(QueueNode node){
    	if(node.getBoatSide() == 1){
    		QueueNode newNode = new QueueNode(node.getCannibals1(), node.getCannibals2(), node.getMissionaries1()-2, node.getMissionaries2()+2, 2, node.getCost()+1, node.getPath() + "2 missionaries moved to side 2\n");
    		if(newNode.isLegal()){return newNode;}
    	}
    	else{
    		QueueNode newNode = new QueueNode(node.getCannibals1(), node.getCannibals2(), node.getMissionaries1()+2, node.getMissionaries2()-2, 1, node.getCost()+1, node.getPath() + "2 missionaries moved to side 1\n");
    		if(newNode.isLegal()){return newNode;}
    	}
    	return null;
    }
    
    private QueueNode moveOneMissionary(QueueNode node){
    	if(node.getBoatSide() == 1){
    		QueueNode newNode = new QueueNode(node.getCannibals1(), node.getCannibals2(), node.getMissionaries1()-1, node.getMissionaries2()+1, 2, node.getCost()+1, node.getPath() + "1 missionary moved to side 2\n");
    		if(newNode.isLegal()){return newNode;}
    	}
    	else{
    		QueueNode newNode = new QueueNode(node.getCannibals1(), node.getCannibals2(), node.getMissionaries1()+1, node.getMissionaries2()-1, 1, node.getCost()+1, node.getPath() + "1 missionary moved to side 1\n");
    		if(newNode.isLegal()){return newNode;}
    	}
    	return null;
    }
    
    private QueueNode moveOneMissionaryAndOneCannibal(QueueNode node){
    	if(node.getBoatSide() == 1){
    		QueueNode newNode = new QueueNode(node.getCannibals1()-1, node.getCannibals2()+1, node.getMissionaries1()-1, node.getMissionaries2()+1, 2, node.getCost()+1, node.getPath() + "1 missionary & 1 cannibal moved to side 2\n");
    		if(newNode.isLegal()){return newNode;}
    	}
    	else{
    		QueueNode newNode = new QueueNode(node.getCannibals1()+1, node.getCannibals2()-1, node.getMissionaries1()+1, node.getMissionaries2()-1, 1, node.getCost()+1, node.getPath() + "1 missionary & 1 cannibal moved to side 1\n");
    		if(newNode.isLegal()){return newNode;}
    	}
    	return null;
    }

    
    // Main entry point
    static public void main(String[] args)  {
	new Cannibals();
    }
    
}
