import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

public class WordCount {

	Hashtable<String, Integer> table;
	
	public WordCount(String fileName){
		//Sets up a scanner to read through a file for the numbers of the board 
        Scanner sc = null;
        try {
            sc = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
        	System.out.println("File not found!");
        	return;
        }
        
        table = new Hashtable<String, Integer>();
        
        while (sc.hasNext()){
        	String next = sc.next();
        	Integer hashValue = table.get(next);
        	if(hashValue != null){
        		hashValue = new Integer(hashValue.intValue() + 1);
        	}
        	else{
        		hashValue = new Integer(1);
        	}
        	table.put(next, hashValue);
        }
        
        
	}
	
	public void print(){
		Set<String> keySet = table.keySet();
		Iterator<String> tableIterator = keySet.iterator();
		Vector<String> sortedTable = new Vector(table.size());
		for(int i = 0; i < table.size(); i++){
			String next = tableIterator.next();
			sortedTable.add(BinarySearch(sortedTable, 0, i+1, next), next);
		}
		
		for(int i = 0; i < sortedTable.size(); i++){
			System.out.println(sortedTable.get(i)+": "+ table.get(sortedTable.get(i)));
		}
	}
	
	 public int BinarySearch(Vector s, int front, int back, String item){
	        // Check the middle spot
	        int middle = (front + back) / 2;
	        // base cases
	        if(((String)s.get(middle)).compareTo(item)==0)
	            return middle;
	        if(front >= back)
	            return middle;
	        // More searching necessary
	        if(((String)s.get(middle)).compareTo(item) > 0)
	            return BinarySearch(s, front, middle-1, item);
	        return BinarySearch(s, middle + 1, back, item);
	    }
	
	public static void main(String[] args){
		WordCount wordCount = new WordCount("/Users/Will/Downloads/hamlet1.txt");
		wordCount.print();
	}
}