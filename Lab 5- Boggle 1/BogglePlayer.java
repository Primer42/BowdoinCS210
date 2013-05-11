import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.io.*;

/**
   This program implements code that allows the user to play a game of
    boggle, the famous fun fabulous board game.  However to display
    the results of the BogglePlayer class and game, Boggle GUI must be
    implemented as a graphical interface and to interpret the results
    of BoglePlayer.  The results are stored in useful data structures,
    such to make for easy retrieval and implementation.  The game
    works for any sized board and for any minimum word length.  The
    computer then gets all valid words from the board and displays
    them, thus thoroughly trouncing the human opponent.  The program
    checks the input the user puts in, and checks the board and all
    the words in enable1.txt (the dictionary) and gives the user
    points for that word.  In this fashion a Boggle Game is played.

 *
 * BogglePlayer implements methods that will be used by {@link
 * BoggleGUI} to play an interactive game of Boggle.  The code will
 * work for boards of any size, and for any minimum sized word length.
 * @author Michael Green
 * @author Paul Kube
 * @author Eric Chown
 * @author Laura Toma
 *
 * Copyright (C) 2002 Michael Green <mtgreen@cs.ucsd.edu>
 * Copyright (C) 2002 Paul Kube <kube@cs.ucsd.edu>
 * Copyright (C) 2003/5 Eric Chown <echown@bowdoin.edu>
 * Copyright (C) 2004 Pat Costello
 */



public class BogglePlayer {
    
    
    /**
     * Builds a data structure for use as a dictionary.  Given a Set of
     * words to use in a lexicon for the game, this method will
     * extract the words, sort them, and put them into a suitable
     * data structure such that they can be quickly searched.
     * @param wordlist the collection of words to use in the lexicon
     * (potentially unordered)
     * @return void
     * @see    BoggleGUI
     */
    
    //Constants - only used if call doesn't give values for these things
    private static final int MIN_WORD_DEFAULT = 4;
    private static final int ROWS_DEFAULT = 4;
    private static final int COLS_DEFAULT = 4;
    private static final int TILES_DEFAULT = (ROWS_DEFAULT*COLS_DEFAULT);
    private static final int UNUSED = 0;	//constant to mark that square has NOT been used
    private static final int USED = 1;		//constant to mark that square has been used
    
  
    //vars
    private Vector<String> lexicon; //Stores lexicon
    private String board[][];  //Stores board
    private int boardMarker[][];	//Stores used squares of board
    private boolean boardSet;		//Stores whether or not board has been set
    private static Stack<String> usedWords;		//Stack of used words

    
    private int rows, cols;   //Size of board
    private int tiles; //Total number of tile on board
    private int minWordLength; //Minimum size for a valid word
    
    
    //Constructor for default BogglePlayer with default values
    public BogglePlayer() {
	minWordLength = MIN_WORD_DEFAULT;
	rows = ROWS_DEFAULT;
	cols = COLS_DEFAULT;
	tiles = TILES_DEFAULT;
	
	lexicon = new Vector<String>();
	board = new String[cols][rows];
	boardSet = false;
	boardMarker = new int[cols][rows];
	usedWords = new Stack<String>();
    }
  
  
    /*Constructor for when user or BoggleGUI wants to state minimum
     * word-size and board size.
     */
    public BogglePlayer(int minLength, int r, int c)  {
	minWordLength = minLength;
	rows = r;
	cols = c;
	tiles = (rows*cols);
	
	lexicon = new Vector<String>();
	board = new String[cols][rows];
	boardSet = false;
	boardMarker = new int[cols][rows];
	usedWords = new Stack<String>();
    }
    
  
    /* Method to get lexicon from file; lexicon is sorted and only has
     * valid length words.
     * @param wordList   A list of legal words in Boggle
     * @see BoggleGUI
     */
  public void buildLexicon(Set wordList) {
      Iterator i;
      String newWord;
      i = wordList.iterator();
      
      //read in each word to newWord until there are no words left
      while(i.hasNext() == true) { 
	  newWord = (String)i.next();
	  
	  //only add word to lexicon if it is long enough
	  if (newWord.length() >= minWordLength)
	      lexicon.addElement(newWord);
      }   
      
      quickSort(lexicon, 0, lexicon.size() - 1);
  }
    
  
    /**
     * Method to create the board given an array of letters.  This
     * method is passed an array of strings.  Each element of the
     * array corresponds to one cube in the boggle board.  This method
     * converts the array into a suitable data structure to enable
     * efficient searching.
     * Also sets up the boardMarker with default values.
     * @param letterArray   the letters that make up the board
     * @return void
     * @see    BoggleGUI
     */
    public void setBoard(String[] letterArray) {
    	for(int j = 0; j < rows; j++){
    		for(int i = 0; i < cols; i++){
    			board[i][j] = letterArray[i + j*cols];
    			boardMarker[i][j] = UNUSED;
    		}
    	}
    	boardSet = true;
    	usedWords = new Stack<String>();
    }
    
  
    /**
     * Method to retrieve all legal words on the board.  This method
     * returns all of the words in the board that are in the lexicon
     * and are at least the minimum length size.
     * @param minimumWordLength  the minimum size of a legal word
     * @return a Vector of strings, each representing a word on the board
     * @see BoggleGUI
     */
    /*Logic behind this method
     * 
     * If the board has been set, this method starts to look for all the valid words, otherwise it returns null.
     * It starts by creating a list to store the valid words.
     * It then goes through each of the squares in the board, and does all of the following.
     * Firstly, it gets a list of the prefixes for the given square using the get prefixes method.
     * It then goes through those prefixes and searches the lexicon using the BinarySearchForLexicon method for the
     * words in the lexicon that start with that prefix.
     * If a valid section of the lexicon is returned, the method goes and looks at each of the words in the lexicon.
     * If a given word has not been used already, and the word is found on the board according to checkForWord,
     * the word is added to the list of valid words.
     * Once all the prefixes in all the squares have been checked, the method returns the list of words in the form
     * of a vector.
     */
    
    public Vector<String> getAllValidWords(int minimumWordLength) { 
    	if(boardSet){
    		LinkedList<String> validWords = new LinkedList<String>();		//list that will eventually hold the valid words to be returned
    		for(int c = 0; c < cols; c++){					//going through
    			for(int r = 0; r < rows; r++){				//all the squares in the board
    				LinkedList <String> prefixes = getPrefixes(c, r);		//get the prefixes at the given square
    				for(int prefixIndex = 0; prefixIndex < prefixes.size(); prefixIndex++){		//going through all of the prefixes
    					int[] sectionOfLexiconToSearch = BinarySearchLexiconForPrefix(0, lexicon.size(), prefixes.get(prefixIndex));				//searches the lexcion for words starting with the given prefix
    					if(sectionOfLexiconToSearch != null){
    						for(int lexIndex = sectionOfLexiconToSearch[0]; lexIndex <= sectionOfLexiconToSearch[1]; lexIndex++){			//goes through the correct sections of the lexion with words starting with the right prefix
    							if(usedWords.search(lexicon.get(lexIndex))<0 && checkForWord(c,r, lexicon.get(lexIndex)) != null){		//if the word being looked at has not been used, and is on the board according to checkForWord
    								validWords.addFirst(lexicon.get(lexIndex));			//add the word to the validWordist	
    							}
    							resetBoardMarker();			//to assure that all the squares are used correctly, the board marker is reset
    						}
    					}
    				}
    			}
    		}
    		return new Vector<String>(validWords);		//once all squares have been checked thouroughly, retun the words found
    	}
    	else{	//if the board has not been set
    		return null;
    	}
    }
    /* Method that gets the prefixes surrounding a given square on the board.
     * takes a location on the board, and goes through each neighbor creating a prefix and adding it to a list.
     * It then returns that list of prefixes
     */
    private LinkedList<String> getPrefixes(int locC, int locR){
    	LinkedList<String> prefixes = new LinkedList<String>();		//list that will hold the prefixes and be returned
    	String firstLetter = board[locC][locR];		//get the string at this square
    	//going through each valid neighbor, creates a prefix by adding the neighbor's string to that of this square,
    	//then adding that prefix to the prefix list
       	if(locC != cols-1){prefixes.add((firstLetter + board[locC+1][locR]).toLowerCase());}
     	if(locC != cols-1 && locR != rows-1){prefixes.add((firstLetter + board[locC+1][locR+1]).toLowerCase());}
       	if(locR != rows-1){prefixes.add((firstLetter + board[locC][locR+1]).toLowerCase());}
     	if(locC != 0 && locR != rows-1){prefixes.add((firstLetter + board[locC-1][locR+1]).toLowerCase());}
      	if(locC != 0){prefixes.add((firstLetter + board[locC-1][locR]).toLowerCase());}
       	if(locC != 0 && locR != 0){prefixes.add((firstLetter + board[locC-1][locR-1]).toLowerCase());}
       	if(locR != 0){prefixes.add((firstLetter + board[locC][locR-1]).toLowerCase());}
       	if(locC != cols-1 && locR != 0){prefixes.add((firstLetter + board[locC+1][locR-1]).toLowerCase());}
       	
       	return prefixes;		//return the list of prefixes
   }
    
    
    
    /**
     * This method checks if a word is in the lexicon specified by buildLexicon.
     * @param wordToCheck the word to be checked
     * @return true when the word is in the lexicon, false otherwise
     * @see BoggleGUI
     */
    public boolean isInLexicon(String wordToCheck) {
	boolean result;
	result = BinarySearch(lexicon, 0, lexicon.size() - 1, wordToCheck);  
	return result;
    }
  
  
    /**
     * Method to check whether or not a word in on the board.  This
     * method checks if the given word can be found on the board using
     * a legal connected path (i.e. consecutive letters are adjacent,
     * no cube on the board is used twice)
     * @param wordToCheck the word to be checked
     * @return a Vector of the locations of the letters.  If not found, returns null
     * @see BoggleGUI
    */
    
    /* Logic behind method
     * 
     * First thing, the method makes sure the word is a legal word, in that it has a length > 0
     * 
     * next, if the board has been set, as stored in the boolean boardSet, it starts to check for the word.
     * If the board has not been set, it returns null
     * 
     * First, for conformity sake, the letter is put into lower case, and it trimmed.
     * The first letter is stored, and then the method goes through the board looking for squares with a first
     * letter that matches that of the wordToCheck.
     * If the method finds such a square, it runs checkForWord on that square, which recursively looks for a word
     * starting at a given location, and returns a linkedList of the letter locations, which is stored in resultList
     * in this method.
     * If a checkForWord sucessfully returns a list of locations, isOnBoard stops searching through the board, adds
     * the word to a running list of words that have already been found, and retuns that list in the form of a vector.
     * If that list is not returned sucessfully, isOnBoard returns null.
     */
    public Vector<Integer> isOnBoard(String wordToCheck) {
    	if(wordToCheck.length() <= 0){	//if the word has a length <= 0, the word is not valid, and this returns null
    		return null;
    	}
    	if(boardSet){						//if the board has been set
    		wordToCheck = wordToCheck.toLowerCase();	//set the word to lower case
        	wordToCheck = wordToCheck.trim();			//trim spaces off the front and back
        	String firstLetter = "" + wordToCheck.charAt(0);	//get the first letter in the word, and store it
        	LinkedList<Integer> resultList = null;				//set up the list to hold the results
        	for(int locC = 0; locC < cols && resultList == null; locC++){	//go through the columns, stopping if the result list has been filled with valid locations
        		for(int locR = 0; locR < rows  && resultList == null; locR++){	//go through the rows, stopping at the same points
       				if(((""+board[locC][locR].charAt(0))).equalsIgnoreCase(firstLetter)){		//if the first letter in the square equals the first letter
       					resultList = checkForWord(locC, locR, wordToCheck);		//check that location for the word, using checkForWord, and store the result in resultList
       					resetBoardMarker();				//makes sure the boardMarker, storing used squares, has been completely reset
       				}//close if statment checking if first letter of square = first letter of word
       			}//close loop looking through rows
        	}//close loop looking through cols
        	if(resultList != null){				//if the resultList has locations
        		usedWords.add(wordToCheck);		//add the word to a list of used words
        		return new Vector<Integer>(resultList);		//return the list of locations as a vector
        	}
        	else{					//if the result list does not have locations
        		return null;		//return null
        	}
    	}//closes if boardSet
    	else{					//if the board has not been set
    		return null;		//return null
    	}
    }
    
    /**
     * Helper method to check whether or not a word in on the board starting from a given location.
     * This method checks if the given word can be found on the board using
     * a legal connected path (i.e. consecutive letters are adjacent,
     * no cube on the board is used twice) recursively
     * @param locC which column is being checked
     * @param locR which row is being checked
     * @param wordToCheck the word to be checked
     * @return a LinkedList<Integer> of the locations of the letters.  If not found, returns null
     * @see isOnBoard
    */
    
    /*LOGIC BEHIND METHOD
     * First, this method gets the string at the current square and sets it to lower case, for consistancy
     * 
     * Next, the method sets up a temporary board marker with the status of boardMarker when the method is called
     * (the values of boardMarker will change as the method runs)
     * 
     * Then, if the current square has been used, method returns null.
     * If, begins checking for the word.
     * 
     * If the word starts with with current square's string, it starts checking, but if not the method returns null.
     * The method starts checking by marking this square as used, 
     * and stores its location in an Integer in a 0-15 fashion
     * Then it sets up a list of locations, and adds this location to that list.
     * If the current string's length equals that of the square's string, all of the word has been found, so
     * the method restores the boardMarker and returns this square's location.
     * If not, the letters that have not yet been found are stored in a string, and the method goes through the surrounding
     * squares, looking for that shortened word using checkForWord and stores the result in a list.
     * if no result is found, the board marker is reset, and the method continues working.
     * If a result is found, the method stops checking, adds the current location to those results, and returns that longer list.
     */
    
    private LinkedList<Integer> checkForWord(int locC, int locR, String wordToCheck){
    	String currentString = board[locC][locR].toLowerCase();		//gets the string at this location, and stores it
    	
    	int[][] tempBoardMarker = new int[boardMarker.length][boardMarker[0].length];	//sets up a temp board marker
    	for(int i = 0; i < boardMarker.length; i++){
    		for(int j= 0; j < boardMarker[i].length; j++){
    			tempBoardMarker[i][j] = boardMarker[i][j];		//fills that temp board marker with the current boardMarker's values
    		}
    	}
    	
    	if(boardMarker[locC][locR] == USED){	//if the location has been used, return null
    		return null;
    	}
    	else{			//if the location is unused...
    		if(wordToCheck.startsWith(currentString)){				//see if the word starts with this location's string
    			boardMarker[locC][locR] = USED;						//mark this square (with a correct string) as used
        		Integer currentLoc = new Integer(locC + 4*locR);	//store this location in a 0-15 fashion
        		LinkedList<Integer> currentResult = new LinkedList<Integer>();	//set up a list of results
        		currentResult.add(currentLoc);						//and add the current location to that list
        		if(currentString.length() == wordToCheck.length()){		//if the length of the word matches that of the square, the word has been found
					boardMarker[locC][locR] = tempBoardMarker[locC][locR];	//so remove the marking
        			return currentResult;								//and return this squares location
        		}
        		else{	//if the word has not been completely found
        			wordToCheck = wordToCheck.substring(currentString.length());	//remove the part that has been found
        			LinkedList<Integer> resultToAdd = null;							//a temp list to store potential results
        			for(int switchNumber = 1; switchNumber <= 8; switchNumber ++){	//go through the various neighbors of this square
        				int currC = -1;		//locations of the various neighbors
        				int currR = -1;		//defaulted to -1 
        				
        				switch(switchNumber){				//desides which neighbor to check this itteration of the for loop
        				case 1:	if(locC != cols-1){currC = locC +1; currR = locR;} break;
        				case 2: if(locC != cols-1 && locR != rows-1){currC = locC+1; currR =  locR+1;} break;
        				case 3: if(locR != rows-1){currC = locC; currR = locR+1;} break;
        				case 4: if(locC != 0 && locR != rows-1){currC = locC-1; currR = locR+1;} break;
        				case 5: if(locC != 0){currC = locC-1; currR = locR;} break;
        				case 6: if(locC != 0 && locR != 0){currC = locC-1; currR = locR-1;} break;
        				case 7: if(locR != 0){currC = locC; currR = locR-1;} break;
        				case 8: if(locC != cols-1 && locR != 0){currC = locC+1; currR = locR-1;} break;}
        				
        				if(currC >= 0 && currR >=0){			//if that locaton is a valid location AKA not -1
        					resultToAdd = checkForWord(currC, currR, wordToCheck);	//see if there is a result at that location
        					if(resultToAdd == null){			//if there is not
        						boardMarker[currC][currR] = tempBoardMarker[currC][currR];	//reset the board marker for that location
        					}
        					else{	//if there is a valid result
        						break;		//break the for loop- stop looking at each neighbor
        					}
        				}
        			}
        			
        			if(resultToAdd != null){		//if there is a valid result
        				resultToAdd.addFirst(currentLoc);	//add the current location to that result
        				return resultToAdd;					//return the complete result
        			}
        			else{		//if there is not a valid result
        				return null;
        			}
        		}
        	}
        	else{		//if the word does not start with the current square's string
        		return null;		//there is not result here- return null
        	}
    	}
    }
    
    // This method resets the boardMarker variable to all UNUSED values
    private void resetBoardMarker(){
    	for(int i = 0; i < cols; i++){
			for(int j = 0; j < rows; j++){
				boardMarker[i][j] = UNUSED;		//iterates through each location, and sets each one to UNUSED
			}
		}
    }
    
    /**
     * This method creates a fixed board.
     * It is mainly useful in debugging such
     * that specific problems can be addressed easily (e.g. "Qu" boards).
     * @param none
     * @return a string array representing the board
     * @see BoggleGUI
     */
    public String[] getCustomBoard(){
	String[] customboard={"Qu","I","C","K","A","T","E", "E","K","S","N","L","E","N","D","P"};
	//should work: kelp, cent, cents, sent, quick, quake, quite, lent, lend
	//should not work: tent, sents
	return customboard;
    }
    
    
    /**
     * This method sorts a vector of strings using Quicksort.
     * @param data  the Vector to be sorted
     *        low   the leftmost boundary of the Vector to be sorted
     *        high  the rightmost boundary of the Vector to be sorted
     * @return void
     */
    public void quickSort(List<String> dat, int low, int high) {
        // Base case
        if (low >= high) return;
        // partition the Vector into two halves
        int pivot = partition(dat, low, high);
        // recursive calls to sort the two parts
        quickSort(dat, low, pivot - 1);
        quickSort(dat, pivot + 1, high);
    } 


    /**
     * Quicksort helper method that partitions a Vector into two
     *  halves based on a "pivot."  All the elements less than the
     *  pivot are placed in the left half, all the rest are in the
     *  right half.
     * @param data  The Vector being sorted
     *       left  The leftmost boundary
     *       right The rightmost boundary
     * @return the location of the pivot in the Vector
     */
    public int partition(List<String> data, int left, int right){
        // left and right represent the boundaries of elements we
        // haven't partitioned Our goal is to get to all of them
        // moving partitioned elements to either side as necessary.
        while (true) {
            // move right "pointer" toward left
            while (left < right && 
		   ((String)data.get(left)).compareTo(((String)data.get(right))) < 0) {
                right--;
            }
	    
            if (left < right) swap(data,left++,right);
            else return left;
            // move left pointer toward right
            while (left < right && ((String)data.get(left)).compareTo(((String)data.get(right))) < 0){
                left++;
            } 
            if (left < right) swap(data,left,right--);
            else return right;
        }
    }
    

    
    /**
     * This method swaps two elements in a Vector (regardless of their type).
     * @param data The vector
     *        i    The position of one element
     *        j    The position of the other element
     * @return void
     */
    public void swap(List<String> data, int i, int j) {
        String temp;
        temp = (String)data.get(i);
        data.set(i, data.get(j));
        data.set(j, temp);
    }
    

    
    /**
     * This method performs a recursive binary search on a Vector.  
     * It returns true if the search item is in the Vector and false otherwise.
     * @param s The Vector to be searched
     *        front The leftmost boundary of the Vector that we're still interested in
     *        back  The rightmost boundary
     *        item  The thing we're searching for
     * @return true when the item is in the vector, false otherwise
    */
    public boolean BinarySearch(List s, int front, int back, String item){
        // Check the middle spot
        int middle = (front + back) / 2;
        // base cases
        if(((String)s.get(middle)).compareTo(item)==0)
            return true;
        if(front >= back)
            return false;
        // More searching necessary
        if(((String)s.get(middle)).compareTo(item) > 0)
            return BinarySearch(s, front, middle-1, item);
        return BinarySearch(s, middle + 1, back, item);
    }
    
    
    /*BinarySearchForLexiconForPrefix searches the lexicon in a binary way for the words that start
     * start with a given prefix.   It returns the index of the first word that starts with the prefix, and the 
     * index of the last word that starts with the prefix.  It does this by going through the lexicon in a binary 
     * way, and when it finds a word that starts with the String prefix, it goes backwards until it reaches a word that no longer
     * starts with the given prefix and then stores that index, and then goes forward until it finds a word that no
     * longer starts with that prefix, and stores that last index again.  It then returns an array holding these two
     * locations.  In short, it returns the index of the first word in the lexicon 
     */
    private int[] BinarySearchLexiconForPrefix(int front, int back, String prefix){
    	// Check the middle spot
        int middle = (front + back) / 2;
        // base cases
        if((lexicon.get(middle)).startsWith(prefix)){
        	int[] startAndEnd = new int[2];
        	int i;
        	for(i = middle; ((String)(lexicon.elementAt(i))).startsWith(prefix) && i > 0; i--){
        		startAndEnd[0] = i;
        	}
        	for(i = middle; ((String)(lexicon.elementAt(i))).startsWith(prefix) && i < lexicon.size()-1; i++){
        		startAndEnd[1] = i;
        	}
        	return startAndEnd;
        }
        
        if(front >= back){
        	return null;
        }
        // More searching necessary
        if((lexicon.elementAt(middle)).compareTo(prefix) > 0)
            return BinarySearchLexiconForPrefix(front, middle-1, prefix);
        else
        	return BinarySearchLexiconForPrefix(middle + 1, back, prefix);
    }
}