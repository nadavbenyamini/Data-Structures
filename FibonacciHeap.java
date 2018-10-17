
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over non-negative integers.
 */
public class FibonacciHeap
{
	private HeapNode min;
	private int size;
	private static int totalLinks = 0;
	private static int totalCuts = 0;
	
	/**
	 * constructor
	 */
	public FibonacciHeap() {
		this.min = null;
		this.size = 0;
	}
	
	   /**
	    * public boolean empty()
	    *
	    * precondition: none
	    * 
	    * The method returns true if and only if the heap
	    * is empty.
	    * O(1)
	    *   
	    */
    public boolean empty()
    { 
    	return this.min == null;
    }
	
    /**
    * public HeapNode insert(int key)
    * key = added key
    * O(1)
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
    */
    public HeapNode insert(int key)
    {    
    	HeapNode node = new HeapNode(key);
    	
    	if (this.min == null) {
    		this.min = node;
    		this.size++;
    		return node;
    	}
		node.setLeft(min); 
		node.setRight(min.getRight());
        min.getRight().setLeft(node);    	
    	min.setRight(node);
    	size++;    	
    	 
    	if(key <= min.getKey()) { //because of example 1, (FUCKING < INSTEAD OF <=) !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11
    		this.min = node;
    	}    		
    	return node;    	
    }
    
   /**
    * public void consolidate()
    * start is the min node
    * Consolidate the heap into standard binomial heap
    *  O(logn) amortize
    */
    public void consolidate(HeapNode start) {//,boolean debug) {
    	HeapNode[] A = new HeapNode[45];
    	HeapNode curr = start;
    	HeapNode startLeft = start.getLeft();
    	HeapNode currRight = start.getRight();
    	boolean stop = false;
    	int minKey = start.key;
    	int newRank = 0;
    	int currRank = 0;
    	
    	while(true) {
    		currRank = curr.getRank();
	    	currRight = curr.getRight();
	    	
    		// Updating the heap's new min:
    		if(curr.getKey() <= minKey) {
    			minKey = curr.getKey();
    			this.min = curr;
    		} 		    		
    		
	    	int rank = curr.getRank();    	
	    	if(A[rank] == null) {
	    		A[rank] = curr;	    		
	    	} else {
	    		HeapNode temp = null;
	    		while(A[rank] != null) {
		    		if(curr.getKey() <= A[rank].getKey()) { // We want to link the bigger node to the smaller node
		    			temp = curr;
			    		curr = A[rank];
			    		A[rank] = temp;
			    		// When swapping curr and A[rank], we should swap the min if min==curr
		    			if(this.min == curr && curr.getKey() == A[rank].getKey()) {
			    			this.min = A[rank];
			    		}
		    		}
		    		temp = curr;
		    		curr.link(A[rank]);//debug);
		    		curr = A[rank];
		    		newRank = A[rank].getRank();
		    		rank = newRank;		    		
		    		if(curr == startLeft || temp == startLeft) {
		    			stop = true;
			    	}
	    		}
	    		A[rank] = A[rank-1];
	    		for(int i=currRank;i<rank;i++) {
	    			A[i] = null;
	    		}    		
	    	}
	    	if(curr == startLeft || stop) {
	    		break;
	    	}	    	
	    	curr = currRight;	    	
    	}
    }

    /* 
     * Finding the new min by looping over the roots
     * Called only after delete min where min had one child
     * O(log n)
     */
    public void FindNewMin() {
		if(min==null) {
			return;
		}
		FibonacciHeap.HeapNode curr = min;
		FibonacciHeap.HeapNode start = min;
		do {
			if(curr.getKey()<=min.getKey()) {
				min = curr;
			}
			curr = curr.getRight();
		} while(curr!=start);
    }
    
   /**
    * public void deleteMin()
    * O(logn) amortize
    * Delete the node containing the minimum key.
    *
    */
    public void deleteMin()    
    {    	
    	if(this.min==null) {
    		return;
    	}
        this.size--;
    	HeapNode minLeft = min.getLeft();
    	HeapNode minRight = min.getRight();
    	
    	// There is one tree in the heap::
    	if(minLeft == min) {
    		if(min.getChild()==null) {
    			min=null;
    			return;
    		} else {
    			HeapNode curr = min.getChild();
    			HeapNode start = min.getChild();
    			int minKey = start.getKey();
    			do {
    				curr.setParent(null);
    				if(curr.getKey() <= minKey) {
    					this.min = curr;
    					minKey = curr.getKey();
    					curr = curr.getRight();
    				}
    			} while(curr.getParent()!=null);
    			this.FindNewMin();
    		}
    	
    	// There are two trees in the heap:
    	} else if(minLeft == minRight && min.getChild() == null) {
			minLeft.setRight(minLeft);
			minLeft.setLeft(minLeft);
			this.min = minLeft;
			return;
		// There are three or more trees in the heap:	
		} 
    else {    	
    	if(min.getChild() == null) {
	    	minLeft.setRight(minRight);
		    minRight.setLeft(minLeft);		    
        	this.consolidate(minRight);
    	} else if (min.getChild().getLeft() == min.getChild()) { 
    		min.getChild().setParent(null);
    		min.getChild().setLeft(minLeft);
        	minLeft.setRight(min.getChild()); 
    		min.getChild().setRight(minRight);
        	minRight.setLeft(min.getChild()); 
        	this.consolidate(minRight);
    	} else {
    		HeapNode curr = min.getChild();
    		while(curr.getParent() != null) {
    			curr.setParent(null);
    			curr = curr.getRight();
    		}
	    	min.getChild().getLeft().setRight(minRight);
	    	minRight.setLeft(min.getChild().getLeft());
	    	min.getChild().setLeft(minLeft);
	    	minLeft.setRight(min.getChild());
	    	this.consolidate(minRight);
    	}
		//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 		
    }
   }
    
   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin()
    {
    	return this.min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    * O(1) w.c and amortize
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	HeapNode node = heap2.min;
    	
    	if (this.min == null) {
    		this.min = node;
    		this.size+=heap2.size();
    		return;
    	} else if(heap2.empty()) {
    		return;
    	} else if(heap2.size == 1) {
    		insert(node.getKey());
    	} else {
    		HeapNode nodeLeft = node.getLeft();
    		node.setLeft(min);
    		nodeLeft.setRight(min.getRight());
    		min.getRight().setLeft(nodeLeft);
    		min.setRight(node);
    		size += heap2.size();
    		
    		if(node.getKey() <= min.getKey()) {
    			this.min = node;
    		}
    		
    		return;
    		
    	}
    	
 		
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    */
    public int size()
    {
    	return this.size; // should be replaced by student code
    }
    	
    /**
    * public int[] countersRep()
    * O(logn)
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep()
    {
    	if(this.empty()) {
    		return new int[0];
    	}
    	
    	int maxRank = 0;
    	HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
    	HeapNode curr = min;
    	boolean firstRun = true;
    	while(firstRun || (curr != min && curr != null)) {
    		firstRun = false;
    		maxRank = Math.max(maxRank,curr.getRank());
    		if(!map.containsKey(curr.getRank())) {
    			map.put(curr.getRank(),1);
    		} else {
    			map.put(curr.getRank(),map.get(curr.getRank())+1);
    		}
    		curr = curr.getRight();
    	}  	
    	
    	int[] arr = new int[maxRank+1];
    	for (int rank : map.keySet()) {
    		arr[rank] = map.get(rank);
    	}
        return arr;
    }
	
   /**
    * public void delete(HeapNode x)
    * O(logn)
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) 
    {    
    	this.decreaseKey(x,x.getKey());
    	this.deleteMin();
    }

    /**
     * cut(HeapNode x, HeapNode y)
     * @param x the node being cut
     * @param y = x.parent
     * O(1)
     * cutting x from y
     */
    public void cut(HeapNode x, HeapNode y) {
    	x.setParent(null);
    	x.setMark(0);
    	y.setRank(y.getRank()-1);
    	if(x.getRight() == x) {
    		y.setChild(null);
    	}
    	else {
    		y.setChild(x.getRight());
    		x.getLeft().setRight(x.getRight());
    		x.getRight().setLeft(x.getLeft());
    	}
    	// Placing x as a new root:
    	x.setLeft(this.min);
    	x.setRight(this.min.getRight());
    	this.min.getRight().setLeft(x);
    	this.min.setRight(x);
    	if(x.getKey()<=this.min.getKey()) {
    		this.min = x;
    	}    	
    	totalCuts++;    	
    }
  
    /**
     * cascadingCut(HeapNode x, HeapNode y)
     * @param x the node being cut
     * @param y = x.parent
     * O(logm) w.c, O(1) amortized
     * if not marked, mark y and cut, else cascading cut y
     */
    public void cascadingCut(HeapNode x, HeapNode y){
    	cut(x,y);
    	if(y.getParent()!=null) {
    		if(y.getMark()==0) {
    			y.setMark(1);
    		}
    		else { // going up:
    			cascadingCut(y,y.getParent());
    		}
    	}
    }
   
   /**
    * public void decreaseKey(HeapNode x, int delta)
    * O(1) amortize, O(log n) w.c
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	x.setKey(x.getKey()-delta);   
    	if(x.getParent()!= null && x.getKey() <= x.getParent().getKey()) {
    		cascadingCut(x,x.getParent());    		
    	} else if(x.getParent()==null && x.getKey()<=this.min.getKey()) {    	 	
        	this.min = x;
    	}
    }

   /**
    * public int potential() 
    * O(n)
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
    	if(this.empty()) {
    		return 0;
    	}
    	int potential = 0;
    	HeapNode root = min;
    	boolean firstRun = true;
    	while(firstRun || root != min) { // iterate trees
    		firstRun = false;
    		potential+= 1 + 2*root.getMark();
    		HeapNode curr = root.child;
    		while (curr!= null) { // iterate childs (root to leaf)
    			potential += 2*curr.getMark();
    			HeapNode sibling = curr.getRight();
    			while (sibling != curr) { // iterating siblings
    				potential += 2*sibling.getMark();
    				sibling = sibling.getRight();
    			}
    			curr = curr.getChild();
    		}
    		root = root.getRight();
    	} 
    	return potential; 
    }

   /**
    * public static int totalLinks() 
    *O(1)
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks()
    {    
    	return totalLinks; // should be replaced by student code
    }

   /**
    * public static int totalCuts() 
    * O(1)
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return totalCuts; // should be replaced by student code
    }
    
    // FOR QA ONLY:
	public void printTreeRoots() {
		if(this.min==null) {
			System.out.println("[]");
			return;
		}
		List<String> lst = new ArrayList<String>();
		FibonacciHeap.HeapNode curr = this.findMin();
		FibonacciHeap.HeapNode start = this.findMin();
		do {
			lst.add(curr.getKey()+"("+curr.getRank()+")");
			if(curr.getRight()==null) {
				break;
			}
			curr = curr.getRight();
		} while(curr!=start);
		System.out.println(lst);
	}
     
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{

	public int key;
	private HeapNode right, left, parent, child;
	private int rank;
	private int mark;

	/*
	 * Constructor
	 */
  	public HeapNode(int key) {
	    this.key = key;
	    this.right = this;
	    this.left = this;
	    this.parent = null;
	    this.child = null;
	    this.rank = 0;
	    this.mark = 0;
      }

  	/**
  	 * link(HeapNode other)
  	 * O(1)
  	 * linking this with other as its parent
  	 * @pre: other.key lower than this.key
  	 * @param other
  	 */    
    private void link(HeapNode other) {//,boolean debug) {
    	totalLinks++;
    	/*if(debug) {
    		System.out.println("Linking: "+this+","+other);
    	}*/
    	
    	// other should always become parent of this, and get its siblings
    	this.getLeft().setRight(this.getRight());
    	this.getRight().setLeft(this.getLeft());   	
    	this.setParent(other);
    	
    	// if this and others are sibling, set others' brother to be this's brother:
    	// left:
    	if(other.getLeft() == this) {
    		other.setLeft(this.getLeft());
    	}
    	// right:
    	if(other.getRight()  == this) {
    		other.setRight(this.getRight());
    	}   	
    	
    	
    	HeapNode otherChild = other.getChild();
    	
    	// Placing this as child of other:
    	if(otherChild == null) {
    		other.setChild(this);
    		//****** added: updating siblings of lonely child
    		this.setRight(this);
    		this.setLeft(this);    
    	} 
    	else {
    		// Also setting this's brothers to other's children:
	    	HeapNode otherChildLeft = other.getChild().getLeft();
	    	other.setChild(this);
	    	otherChild.setLeft(this);
	    	this.setRight(otherChild);
	    	this.setLeft(otherChildLeft);
	    	otherChildLeft.setRight(this);	    
    	}
		
    	// Updating mark and rank:
    	this.setMark(0);
    	other.rank ++;
    }

	/**
	 * for QA
	 */
	public String toString() {
		return Integer.toString(key);
	}
  	
	/**
	 * getRight()
	 * @return this.right
	 */
  	public HeapNode getRight() {
		return right;
	}
  	
  	/**
  	 * setRight(HeapNode right)
  	 * set right as this.right
  	 * @param right
  	 */
	public void setRight(HeapNode right) {
		this.right = right;
	}
	
	/**
	 * getLeft()
	 * @return this.left
	 */
	public HeapNode getLeft() {
		return left;
	}
	
  	/**
  	 * setLeft(HeapNode left)
  	 * set left as this.left
  	 * @param left
  	 */
	public void setLeft(HeapNode left) {
		this.left = left;
	}
	
	/**
	 * getParent()
	 * getter
	 * @return this.parent
	 */
	public HeapNode getParent() {
		return parent;
	}
	
	/**
	 * setParent(HeapNode parent)
	 * set this.parent as parent
	 * @param parent
	 */
	public void setParent(HeapNode parent) {
		this.parent = parent;
	}
	
	/**
	 * getChild()
	 * getter
	 * @return this.child
	 */
	public HeapNode getChild() {
		return child;
	}
	
	/**
	 * setChild(HeapNode child)
	 * set this.child as child
	 * @param child
	 */
	public void setChild(HeapNode child) {
		this.child = child;
	}
	
	/**
	 * getRank()
	 * getter
	 * @return this.rank
	 */
	public int getRank() {
		return rank;
	}
	
	/**
	 * setRank(int rank)
	 * set this.rank as rank
	 * @param rank
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	/**
	 * getMark()
	 * @return this.mark
	 */
	public int getMark() {
		return mark;
	}
	
	/**
	 * setMark(int mark)
	 * set this.mark as mark
	 * @param mark
	 */
	public void setMark(int mark) {
		this.mark = mark;
	}
	
	/**
	 * setKey(int key)
	 * set this.key as key
	 * @param key
	 */
	public void setKey(int key) {
		this.key = key;
	}
	
	/**
	 * getKey()
	 * getter
	 * @return this.key
	 */
	public int getKey() {
	    return this.key;
      }

    }
}
