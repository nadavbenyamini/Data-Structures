

/**
 *
 * WAVLTree
 *
 * An implementation of a WAVL Tree.
 * (Haupler, Sen and Tarajan ‘15)
 *
 */

public class WAVLTree {
	private WAVLNode root;
	private int size;
	private int tempIndex;
	
	/**
	 * empty constructor  sets default values
	 */
  public WAVLTree() {
	  this.root = new WAVLNode(null);
	  this.size = 0;
	  this.tempIndex = 0;
  }
  /**
   * constructor
   * sets {@link #root} as new WAVLNode(key,value)
   * @param key  root's key
   * @param value  root's value
   */
  public WAVLTree(int key, String value) {
	  this.root = new WAVLNode(key,value);
	  this.size = 1;
	  this.tempIndex = 0;
  }
  
  /**
   * 
   * @return true iff size == 0
   */
  public boolean empty() {
    return size==0;
  }

 /**
   * public String search(int k)
   *
   * returns the info of an item with key k if it exists in the tree
   * otherwise, returns null
   */
  public String search(int k)
  {	  
	  return search(k,this.root, 0).getValue();
  }
  
  /**
   * searches for a node with key k
   * updates size of curr by toUpdate
   * @param k
   * @param curr
   * @param toUpdate -1 if delete, 1 if insert, 0 if only search without changing the tree
   * @return null if not found, node with key = k, if found
   */
  public WAVLNode search(int k,WAVLNode curr, int toUpdate) {
	  if(curr.getKey()==k) {
		  if(toUpdate<0) {
			  curr.updateSubTreeSizes(toUpdate);
		  }
		  return curr;		  
	  }
	  else if(curr.getKey() == -1) {
		  if (toUpdate>0) {
			  curr.updateSubTreeSizes(toUpdate);
		  }
		  return curr;
	  }
	  
	  else if(curr.getKey()<k) {	
		  
		  return search(k,curr.getRight(), toUpdate);
	  } 
	  else {
		  return search(k,curr.getLeft(), toUpdate);
	  }	  
  }
    
  /**
   * public int insert(int k, String i)
   *
   * inserts an item with key k and info i to the WAVL tree.
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
   * returns -1 if an item with key k already exists in the tree.
   */
   public int insert(int k, String i) {
	   	  WAVLNode location = search(k, this.root, 1);   
	   	  
	   	  // If item with k already exists:
	   	  if (location.getKey() != -1) {
	   		  return -1;
	   	  }
	   	  
	   	  location.setKey(k);
	   	  location.setValue(i); 	  
	   	  location.setRight(new WAVLNode(location));
	   	  location.setLeft(new WAVLNode(location));
	   	  location.setRank(0);
	   	  this.size ++;
	   	  int cnt = reBalance(location,false);
	   	  location.calculateSubTreeSize();
	   	  return cnt;
   }
  
   /**
   * public int delete(int k)
   *
   * deletes an item with key k from the binary tree, if it is there;
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
   * returns -1 if an item with key k was not found in the tree.
   */
   public int delete(int k)
   {
	   	  WAVLNode n = search(k, this.root, -1);
	   	  int counter = 0;
	   	  // If item with k doesn't exists:
	   	  if (n.getKey() == -1) {
	   		  return -1;
	   	  }
	   	  this.size --;
	   	  
	   	  if(n.isInnerLeaf()) {
	   		  if(this.root==n) {
	   			  this.root = new WAVLNode(null);
	   			  return 1;
	   		  }
	   		  if(n.getKey()<n.getParent().getKey()) {
	   			  n.getParent().setLeft(new WAVLNode(n.getParent()));
	   		  } else {
	   			  n.getParent().setRight(new WAVLNode(n.getParent()));
	   		  }
	   		  
	   		  counter = reBalance(new WAVLNode(n.getParent()),true);
	   		  n.calculatedSubTreeSizeUp();
	   		  return counter;
	   	  }
	   	  
	   	  // Finding the one to replace n:
	   	  WAVLNode swap = n.successor();
	   	  if(!swap.isInnerLeaf()) {
	   		  swap = n.predecessor();
	   	  }
	   	  
	   	  // Replacing n with swap:
	   	  WAVLNode nParent = n.getParent();
	   	  WAVLNode nRight = n.getRight();
	   	  WAVLNode nLeft = n.getLeft();
	   	  WAVLNode swapParent = swap.getParent();
	   	  WAVLNode swapLeft = swap.getLeft();
	   	  WAVLNode swapRight = swap.getRight();
	   	  swap.setRank(n.getRank());
	   	  
	   	  swap.setParent(nParent);
	   	  if(this.root == n) {
	   		  this.root = swap;
	   	  } else if (n.getKey()<nParent.getKey()) {
	   		  nParent.setLeft(swap);
	   	  } else {
	   		  nParent.setRight(swap);
	   	  }	   	  

	   	  if(swap == nLeft) {
	   		  swap.setLeft(nLeft.getLeft());
	   		  nLeft.getLeft().setParent(swap);
	   	  } else {
	   		  swap.setLeft(nLeft);
		   	  nLeft.setParent(swap);  
	   	  }   	  
	   	  

	   	  if(swap == nRight) {
	   		  swap.setRight(nRight.getRight());
	   		  nRight.getRight().setParent(swap);
	   	  } else {
	   		  swap.setRight(nRight);
		   	  nRight.setParent(swap);  
	   	  }
	   	  
	   	  if(swap.getKey()<swapParent.getKey()) {
	   		  swapParent.setLeft(swapRight);//new WAVLNode(swapParent));
	   		  swapRight.setParent(swapParent);
	   	  } else {
	   		  swapParent.setRight(swapLeft);
	   		  swapLeft.setParent(swapParent);
	   	  }
		  
	   	  // Rebalancing:
	   	  if(swapParent == n) {
	   		  if(swap.getKey()>n.getKey()) {
	   			counter = reBalance(swap.getRight(),true);
	   		  } else {
	   			  counter = reBalance(swap.getLeft(),true);
	   		  }
	   	  } else if(swap.getKey()<swapParent.getKey()) {
	   		  counter = reBalance(swapParent.getLeft(),true);
	   	  } else {
	   		  counter = reBalance(swapParent.getRight(),true);
	   	  }
	   	  swap.calculateSubTreeSize();
	   	  swapParent.calculatedSubTreeSizeUp();
	   	  return counter;
   }
      
   /**
    * public int reBalance()
    *
    * Rebalances the tree, returns number of operations
    * @return number of rebalance steps
    */  
   public int reBalance(WAVLNode n,boolean isDelete) {
	   int counter = 0;
	   WAVLNode curr = n;
	   
	   while (curr.getParent()!=null){	
		   curr = curr.getParent();
		   int Rank = 1 + Math.max(curr.getLeft().getRank(),curr.getRight().getRank());
		   int LeftRank = curr.getLeft().getRank();
		   int RightRank = curr.getRight().getRank();		   

		   // Case 1 - Problem with the left son:
		   if(Rank - LeftRank > 2){
			  if(curr.getRight().getLeft().getRank() > curr.getRight().getRight().getRank()) {
				  //System.out.println("Double Rotation Left Around: "+curr.getKey());
				  this.doubleRotationLeft(curr);
				  counter += 2;
			  } else {
				  //System.out.println("Single Rotation Left Around: "+curr.getKey());
				  this.rotateLeft(curr);		
				  counter += 1;
			  }
		   } 
		   
		   // Case 2 - Problem with the right son:	   
		   else if(Rank - RightRank > 2){
			   if(curr.getLeft().getRight().getRank() > curr.getLeft().getLeft().getRank()) {
				  //System.out.println("Double Rotation Right Around: "+curr.getKey());
				  this.doubleRotationRight(curr);
				  counter += 2;
			   } else {						  
				  //System.out.println("Single Rotation Right Around: "+curr.getKey());
				  this.rotateRight(curr);	
				  counter += 1;
			   }
		   }	   	   
		   
		   // Stopping conditions for rebalancing after deletes:
		   if(isDelete && curr.legitRank() &&
				       !curr.isInnerLeaf() 
				       && curr.getParent()!=null 
				       && curr.getParent().legitRank()) {
			   return counter;
		   } 
		   
		   if(isDelete && curr.getParent() != null && !curr.isInnerLeaf()) {
			   int diff = curr.getParent().getRank() - 3;
			   int maxChild = Math.max(curr.getLeft().getRank(),curr.getRight().getRank());
			   int minChild = Math.min(curr.getLeft().getRank(),curr.getRight().getRank());
			   if(curr.getParent().getRank()-minChild >= 5) {
				   curr.setRank(1+maxChild);						   
				   counter += 1;
			   } else {
				   curr.setRank(1+ Math.max(diff, maxChild));
				   counter += 1;
			   }
		   } else {
			    int prevRank = curr.getRank();
			    curr.updateRank();	
			    if(curr.getRank()!=prevRank) {
			    	counter++;
			    }
		   }
	   }	   
	   return counter;
   }
   
   /**
    * rotating right on node n
    * @param n
    */
   private void rotateRight(WAVLNode n) {
	   int nRank = n.getRank();
	   WAVLNode nParent = n.getParent();
	   WAVLNode nLeft = n.getLeft();
	   WAVLNode nLeftRight = nLeft.getRight();

	   n.setLeft(nLeftRight);
	   nLeftRight.setParent(n);
	   nLeft.setRight(n);
	   n.setParent(nLeft);
	   
	   nLeft.setParent(nParent);
	   n.setRank(nRank-1);
	   nLeft.setRank(nRank);
	   
	   //Placing nLeft where n used to be:
	   if (nParent == null) {
		   this.root = nLeft;
	   }
	   else {
		   nLeft.setParent(nParent);
		   if(nParent.getRight() == n) {
			   nParent.setRight(nLeft);		   
		   } 
		   else {
			   nParent.setLeft(nLeft);
			   }
	   		}
	   // Updating Sub Tree Sizes:
	   n.calculateSubTreeSize();
	   nLeft.calculateSubTreeSize();
   	}
   
   /**
    * rotating left on node n
    * @param n
    */
   private void rotateLeft(WAVLNode n) {
	   int nRank = n.getRank();
	   WAVLNode nParent = n.getParent();
	   WAVLNode nRight = n.getRight();
	   WAVLNode nRightLeft = nRight.getLeft();
	   
	   n.setRight(nRightLeft);
	   nRightLeft.setParent(n);
	   nRight.setLeft(n);
	   n.setParent(nRight);
	   
	   nRight.setParent(nParent);
   	   
	   n.setRank(nRank-1);
	   nRight.setRank(nRank);

	   //Placing nRight where n used to be:
	   if (nParent == null) {
		   this.root = nRight;
	   }
	   else {
		   nRight.setParent(nParent);
		   if(nParent.getRight() == n) {
			   nParent.setRight(nRight);		   
		   } 
		   else {
			   nParent.setLeft(nRight);
			   }
	   	}

	   // Updating Sub Tree Sizes:
	   n.calculateSubTreeSize();
	   nRight.calculateSubTreeSize();
   }
   
   /**
    * perform double rotation right on node n
    * @param n
    */
   public void doubleRotationRight(WAVLNode n) {
	   WAVLNode nParent = n.getParent();
	   WAVLNode nLeft = n.getLeft();
	   WAVLNode nLeftRight = n.getLeft().getRight(); // n's replacement
	   WAVLNode nLeftRightRight = n.getLeft().getRight().getRight();
	   WAVLNode nLeftRightLeft = n.getLeft().getRight().getLeft();
	   
	   // Updating and placing n:
	   n.setLeft(nLeftRightRight);
	   nLeftRightRight.setParent(n);
	   n.setParent(nLeftRight); 
	   
	   // Updating n's replacement:
	   nLeftRight.setLeft(nLeft);
	   nLeftRight.setRight(n);
	   nLeftRight.setParent(nParent);
	  
	   // Updating n's left son:
	   nLeft.setParent(nLeftRight);
	   nLeft.setRight(nLeftRightLeft);
	   nLeftRightLeft.setParent(nLeft);
	   
	   //Placing n's replacement:
	   if (nParent == null) {
		   this.root = nLeftRight;
	   }
	   else {
		   if(nParent.getRight() == n) {
			   nParent.setRight(nLeftRight);		   
		   } 
		   else {
			   nParent.setLeft(nLeftRight);
			   }
	   }

	   // Updating Ranks:
	   nLeft.updateRank();
	   n.updateRank();
	   nLeftRight.updateRank();
	   
	   // Updating Sub Tree Sizes:
	   n.calculateSubTreeSize();
	   nLeft.calculateSubTreeSize();
	   nLeftRight.calculateSubTreeSize();
	   
   }
   
   /**
    * perform double rotation left on node n
    * @param n
    */
   public void doubleRotationLeft(WAVLNode n) {
	   WAVLNode nParent = n.getParent();
	   WAVLNode nRight = n.getRight();
	   WAVLNode nRightLeft = n.getRight().getLeft(); // n's replacement
	   WAVLNode nRightLeftLeft = n.getRight().getLeft().getLeft();
	   WAVLNode nRightLeftRight = n.getRight().getLeft().getRight();
	   
	   // Updating and placing n:
	   n.setRight(nRightLeftLeft);
	   nRightLeftLeft.setParent(n);
	   n.setParent(nRightLeft); 
	   
	   // Updating n's replacement:
	   nRightLeft.setRight(nRight);
	   nRightLeft.setLeft(n);
	   nRightLeft.setParent(nParent);
	  
	   // Updating n's Right son:
	   nRight.setParent(nRightLeft);
	   nRight.setLeft(nRightLeftRight);
	   nRightLeftRight.setParent(nRight);
	   
	   //Placing n's replacement:
	   if (nParent == null) {
		   this.root = nRightLeft;
	   }
	   else {
		   if(nParent.getLeft() == n) {
			   nParent.setLeft(nRightLeft);		   
		   } 
		   else {
			   nParent.setRight(nRightLeft);
			   }
	   }

	   // Updating Ranks:
	   nRight.updateRank();
	   n.updateRank();
	   nRightLeft.updateRank();
	   
	   // Updating Sub Tree Sizes:
	   n.calculateSubTreeSize();
	   nRight.calculateSubTreeSize();
	   nRightLeft.calculateSubTreeSize();   
   }

   
   /**
    * public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty
    * @return value of node with smallest key
    */
   public String min()
   {
	   if (size == 0) {
		   return null;
	   }
	   	WAVLNode curr = this.root;
	   	while(curr.getLeft().isInnerNode()) {
	   		curr = curr.getLeft();
	   	}
           return curr.getValue(); 
   }
   
   /**
    *
    * @return minimal key in tree
    */
   public int minKey() {
	   	WAVLNode curr = this.root;
	   	while(curr.getLeft().isInnerNode()) {
	   		curr = curr.getLeft();
	   	}
          return curr.getKey(); 
   }

   /**
    * public String max()
    *
    * @return the info of the item with the largest key in the tree,
    * or null if the tree is empty
    */
   public String max()
   {
	   if (size == 0) {
		   return null;
	   }
	   	WAVLNode curr = this.root;
	   	while(curr.getRight().isInnerNode()) {
	   		curr = curr.getRight();
	   	}
          return curr.getValue();    }

   /**
   * public int[] keysToArray()
   *
   * @return a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   */
   public int[] keysToArray()
   {

   	int[] arr = new int[size];
   	
	    if(this.root == null) {
	    	return arr;
	    }
	    this.tempIndex = 0;	    
	    keysToArray(this.root,arr); 
	    
	    return arr;
   }
   
   /**
    * called by {@link #keysToArray()}
    * updating array with all keys found
    * perform inOrder traversal
    * @param curr  node to be traversed
    * @param arr  array to be updated
    */
   public void keysToArray(WAVLNode curr,int[] arr) {
	   if(!curr.isInnerNode()) {
		   return;
	   }	 
	   
	   keysToArray(curr.getLeft(),arr);	   
	   arr[this.tempIndex] = curr.getKey();
	   this.tempIndex ++;	   
	   keysToArray(curr.getRight(),arr);
   }
   /**
   * public String[] infoToArray()
   *
   * @return an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   */

   public String[] infoToArray()
   {

   	String[] arr = new String[size];
	    if(this.root == null) {
	    	return arr;
	    }
	    this.tempIndex = 0;
	    infoToArray(this.root,arr); 
	    return arr;
   }
   /**
    * called by {@link #infoToArray()}
    * updating array with all values found
    * perform inOrder traversal
    * @param curr  node to be traversed
    * @param arr  array to be updated
    */
   public void infoToArray(WAVLNode curr,String[] arr) {
	   if(!curr.isInnerNode()) {
		   return;
	   }	 
	   
	   infoToArray(curr.getLeft(),arr);	   
	   arr[this.tempIndex] = curr.getValue();
	   this.tempIndex ++;	   
	   infoToArray(curr.getRight(),arr);
   }

  
   /**
    * public int size()
    * Returns the number of nodes in the tree.
    */
   public int size()
   {
           return this.size; // to be replaced by student code
   }
   
     /**
    * public WAVLNode getRoot()
    *
    * @return the root WAVL node, or null if the tree is empty
    *
    */
   public WAVLNode getRoot()
   {
           return this.root;
   }
     /**
    * public int select(int i)
    *
    * @return the value of the i'th smallest key (return -1 if tree is empty)
    * Example 1: select(1) returns the value of the node with minimal key 
        * Example 2: select(size()) returns the value of the node with maximal key 
        * Example 3: select(2) returns the value 2nd smallest minimal node, i.e the value of the node minimal node's successor  
    *
    */   
   public String select(int i)
   {
	   	   if(this.size == 0 || i > this.size || i <= 0) {
	   		   return null;
	   	   }
           return this.infoToArray()[i-1]; 
   }


   /**
   * public class WAVLNode
   */
  public class WAVLNode{
	  private int key;
	  private String value;
	  private WAVLNode left;
	  private WAVLNode right;
	  private WAVLNode parent;
	  private int subTreeSize;
	  private int rank;

	  // Create root:
	  /**
	   * root constructor
	   * root's parent is null
	   */
	  public WAVLNode() {
		  this.key = -1;
		  this.value = null;
		  this.left = new WAVLNode(this);
		  this.right = new WAVLNode(this);
		  this.parent = null;
		  this.subTreeSize = 1;
		  this.rank = 0;
	  }
	  
	  /**
	   * external leaf constructor
	   * @param parent setting the node's parent
	   */
	  public WAVLNode(WAVLNode parent) {
		  this.key = -1;
		  this.value = null;
		  this.left = null;
		  this.right = null;
		  this.parent = parent;
		  this.subTreeSize = 0;
		  this.rank = -1;
	  }

	  /**
	   * node's constructor
	   * @param key root's key
	   * @param value root's value
	   * setting left and right as external leaves
	   * setting parent as null
	   * subTreeSize of a node is 1
	   */
	  public WAVLNode(int key,String value) {		  
		  this.key = key;
		  this.value = value;
		  this.left = new WAVLNode();
		  this.right = new WAVLNode();
		  this.parent = null;
		  this.subTreeSize = 1;
		  this.rank = 0;
	  }
	  
	  /**
	   * getter
	   * @return int this.rank
	   */
	  public int getRank() {
		  return this.rank;
	  }

	/**
	 * For testing only
	 * @param curr node to be calculated
	 * @return int new curr.subTreeSize 
	 
	  public int calculateSubTreeSize(WAVLNode curr) {
		if(!curr.isInnerNode()) {
			return 0;
		}
		return 1 + calculateSubTreeSize(curr.getRight()) + calculateSubTreeSize(curr.getLeft());
	}
	*/
	/**
	 * calculates and updates subTreeSize of this node and it parents up to the root
	 */
	  public void calculatedSubTreeSizeUp() {
	  WAVLNode curr = this;
 	  while(curr.getParent()!=null) {
   		  curr.calculateSubTreeSize();
   		  curr = curr.getParent();

   	  }
 	  curr.calculateSubTreeSize();
	}
	
	/**
	 * calculates and updates subTreeSize of this node only
	 */
	  public void calculateSubTreeSize() {
		if(!this.isInnerNode()) {
			this.subTreeSize = 0;
			return;
		}
		int LeftTree = 0;
		int RightTree = 0;
		if(this.left != null) {
			LeftTree = this.left.subTreeSize;
		}
		if(this.right != null) {
			RightTree = this.right.subTreeSize;
		}
		this.subTreeSize = 1 + LeftTree + RightTree;			
	}

	 /**
	 * @return true iff not external (rank != -1) and has no right and left
	 */
	  public boolean isInnerLeaf() {
		return this.rank != -1 && !this.left.isInnerNode() && !this.right.isInnerNode();
	}
    
	 /**
     * getter
     * @return {@link #parent}
     */
	  public WAVLNode getParent() {
		return parent;
	}
	
	  /**
	 * setter
	 * @param parent set {@link #parent} as parent
	 */
	  public void setParent(WAVLNode parent) {
		this.parent = parent;
	  }
	  
	  /**
	   * getter
	   * @return {@link #subTreeSize} get number of children+1
	   */
	  public int getSubTreeSize() {
		  return subTreeSize;
	  }
	  
	/**
	 * setter
	 * @param subTreeSize set {@link #subTreeSize} as subTreeSize
	 */
	  public void setSubTreeSize(int subTreeSize) {
		  this.subTreeSize = subTreeSize;
	  }

	/**
	 * setter
	 * @param key set {@link #key} as key
	 */
	  public void setKey(int key) {
		  this.key = key;
	  }

	/**
	 * setter
	 * @param value set {@link #value} as value
	 */
	  public void setValue(String value) {
		  this.value = value;
	  }

	  /**
	   * setter
	   * @param left set {@link #left} as left
	   */
	  public void setLeft(WAVLNode left) {
		  this.left = left;
	  }

	  /**
	   * setter
	   * @param right set {@link #right} as right
	   */
	  public void setRight(WAVLNode right) {
		  this.right = right;
	  }

	/**
	 * setter
	 * @param rank set {@link #rank} as rank
	 */
	  public void setRank(int rank) {
		  this.rank = rank;
	  }
      
    /**
     * getter
     * @return {@link #key}
     */
	  public int getKey()
	  {
		  return this.key; 
	  }
    
	  /**
	   * getter
	   * @return {@link #value}
	   */
	  public String getValue()
	  {
		  return this.value; 
	  }
    
	  /**
	   * getter
	   * @return {@link #left}
	   */
	  public WAVLNode getLeft()
	  {
		  return this.left;
	  }

	  /**
	   * getter
	   * @return {@link #right}
	   */
	  public WAVLNode getRight()
	  {
		  return this.right;
	  }
                
	  /**
	   *             
	   * @return {@link #parent}.{@link #left} or {@link #parent}.{@link #right} get node'sbrother
	   */
	  public WAVLNode getBrother() {
		  if(this.parent.right==this) {
			  return this.parent.left;
		  } else {
			  return this.parent.right;
		  }
	  }
                
	  /**
	   *             
	   * @return true iff {@link #left} != null and {@link #right}!= null not external node
	   */         
	  public boolean isInnerNode()
	  {
		  return(this.left != null || this.right != null);
	  }

	  /**
	   * getter
	   * @return {@link #subTreeSize}
	   */
	  public int getSubtreeSize()
	  {
		  return this.subTreeSize;
	  }
                               
     /**
      *            
      * @return node's successor WAVLNode in the tree s.t @ret key is higher than this.key and smallest among all higher nodes.
      */
	  public WAVLNode successor() {
		  WAVLNode curr = this;    	      	  
    	
		  if(this.getRight().isInnerNode())
		  {
			  if(!this.getRight().getLeft().isInnerNode() ) {
				  return this.getRight();
			  }
    		
			  curr = this.getRight();	 
			  while(curr.getLeft().isInnerNode()) {	    		 
				  curr = curr.getLeft();
			  }
			  return curr;
		  }
    	  
		  while(curr.parent != null && curr.parent.left.key == curr.key) {
			  curr = curr.parent;
		  }
		  return curr;  	
	  }
       
    /**
     *            
     * @return node's predecessor WAVLNode in the tree that s.t @ret key is lower than this.key and biggest among all higher nodes.
     */   
	  public WAVLNode predecessor() {
		  WAVLNode curr = this;    	      	  
    	
		  if(this.getLeft().isInnerNode())
		  {
			  if(!this.getLeft().getRight().isInnerNode() ) {
				  return this.getLeft();
			  }	
	    	  
			  curr = this.getLeft();	 
			  while(curr.getRight().isInnerNode()) {	    		 
				  curr = curr.getRight();
			  }
			  return curr;
		  }
    	  
		  if(curr.parent.right.key == curr.key) {
			  return curr.parent;
		  }
    	 
		  curr = curr.getParent();
		  while(curr.parent != null && curr.parent.right.key == curr.key) {
			  curr = curr.parent;
		  }
		  return curr;
   
    	 
	  }
      
    /**
     * updates ranks
     */
	  public void updateRank() {
		  if(this.left == null && this.right == null) {
			  this.setRank(-1);
			  return;
		  }
		  if(this.left == null) {
			  this.setRank(this.right.rank+1);
			  return;
		  }
		  if(this.right == null) {
			  this.setRank(this.left.rank+1);
			  return;
		  }
		  this.setRank(1+Math.max(this.left.rank,this.right.rank));
		  return;
	  }
     
    /**
     * @return true iff all rank diffs are legits
     */
	  public boolean legitRank(){
		  if(!this.isInnerNode()) {
			  return this.rank == -1;
		  }
		  if(this.isInnerLeaf()) {
			  return this.rank == 0;
		  }
		  if(this.parent != null && this.parent.getRank() - this.getRank() > 2) {
			  return false;
		  }
		  return this.rank - Math.min(this.left.rank,this.right.rank) <= 2;
	  }
    
    /**
     * @param updateBy updates subTreeSize of all the nodes for this up to the root by updateBy(1 for insert,-1 for delete)
     */
	  public void updateSubTreeSizes(int updateBy) {
		  WAVLNode curr = this;
		  while(curr!=null) { 
			  curr.subTreeSize += updateBy;
			  curr = curr.getParent();
		  }
	  }
      
  }


}