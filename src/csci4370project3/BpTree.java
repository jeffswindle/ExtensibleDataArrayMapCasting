package csci4370project3;

/*******************************************************************************
 * @file BpTree.java
 *
 * @author  John Miller
 */

import java.io.*;
import java.lang.reflect.Array;
import static java.lang.System.out;
import java.util.*;

/*******************************************************************************
 * This class provides B+Tree maps.  B+Trees are used as multi-level index structures
 * that provide efficient access for both point queries and range queries.
 */
public class BpTree <K extends Comparable <K>, V>
       extends AbstractMap <K, V>
       implements Serializable, Cloneable, SortedMap <K, V>
{
    /** The maximum fanout for a B+Tree node.
     */
    private static final int ORDER = 5;

    /** The class for type K.
     */
    private final Class <K> classK;

    /** The class for type V.
     */
    private final Class <V> classV;

    /***************************************************************************
     * This inner class defines nodes that are stored in the B+tree map.
     */
    private class Node
    {
        boolean   isLeaf;
        int       nKeys;
        K []      key;
        Object [] ref;
        @SuppressWarnings("unchecked")
        Node (boolean _isLeaf)
        {
            isLeaf = _isLeaf;
            nKeys  = 0;
            key    = (K []) Array.newInstance (classK, ORDER - 1);
            if (isLeaf) {
                //ref = (V []) Array.newInstance (classV, ORDER);
                ref = new Object [ORDER];
            } else {
                ref = (Node []) Array.newInstance (Node.class, ORDER);
            } // if
        } // constructor
    } // Node inner class

    /** The root of the B+Tree
     */
    private Node root;

    /** The counter for the number nodes accessed (for performance testing).
     */
    private int count = 0;

/**
 *  total number of keys updated on insert method;
 */
    private int numKeys= 0;


    /***************************************************************************
     * Construct an empty B+Tree map.
     * @param _classK  the class for keys (K)
     * @param _classV  the class for values (V)
     */
    public BpTree (Class <K> _classK, Class <V> _classV)
    {
        classK = _classK;
        classV = _classV;
        root   = new Node (true);
    } // BpTree

    /***************************************************************************
     * Return null to use the natural order based on the key type.  This requires
     * the key type to implement Comparable.
     */
    public Comparator <? super K> comparator () 
    {
        return null;
    } // comparator

    /***************************************************************************
     * Return a set containing all the entries as pairs of keys and values.
     * @return  the set view of the map
     */
    public Set <Map.Entry <K, V>> entrySet ()
    {
        Set <Map.Entry <K, V>> result = new HashSet <> ();
       
    	//if the root is a leaf node
    	if(this.root.isLeaf)
    	{
//out.println("DEBUG:: entrySet: the root is a leaf");
    		int i = 0;
    		//go through all the keys of the root
    		while(i<root.nKeys)
    		{
    			Map.Entry<K,V> newEntry = new AbstractMap.SimpleEntry<K,V>(root.key[i], (V) root.ref[i]);
    			//add the pair to the result set
    			result.add(newEntry);
    			//increment
    			i++;
    		}
    	}else
    	{
    		//if the root is not a leaf node
    		//find the depth of the tree
    		int depth = 0;
    		Node currentNode = root;
    		while(!currentNode.isLeaf)
    		{
    			depth++;
    			currentNode = (Node) currentNode.ref[0];
    		}
    		//begin at the root
    		currentNode = root;
    		//start at the root level
    		int currentLevel = 0;
    		//an array to store the current index of each level (pseudo-recursion here)
    		int[] holder = new int[depth+1];
//out.println("DEBUG:: entrySet: holder size = " + depth );
    		Stack<Node> nodeHolder = new Stack<Node>();
    		//initialize the array to an array of 0's
    		for(int i=0;i<depth;i++){holder[i] = 0;}
    		//starting key is root's leftmost key
    		K currentK = root.key[0];
    		//continue through the tree in order, until every key is found
    		while(true)
    		{
    			//if we are looking at a leaf node
    			if(currentLevel==depth)
    			{
    				Map.Entry<K,V> newEntry = new AbstractMap.SimpleEntry<K,V>(currentNode.key[holder[currentLevel]], (V) currentNode.ref[holder[currentLevel]]);
    				//add the pair to the result set
    				result.add(newEntry);
    				//move to the next key in the node
    				holder[currentLevel]++;
    				//if we are done with the node, we must move up one and increment
    				if(holder[currentLevel]>=currentNode.nKeys)
    				{
    					//reset the holder in case we visit this level again 
    					holder[currentLevel] = 0;
    					//move up
    					currentLevel--;
    					//get the parent Node
    					currentNode = (Node) nodeHolder.pop();
    					//move to the next key in the node
    					holder[currentLevel]++;
    					//find out what the next pointer to get will be
    					int toGet = holder[currentLevel];
    					//if the pointer is out of bounds of the node, don't assign K and let the loop handle it during the next iteration 
    					if(!(holder[currentLevel]>=currentNode.nKeys))
    					{
    						//otherwise assign the next K
    						currentK = currentNode.key[toGet];
    					}
    				}
    			//if we are looking at an internal node
    			}else
    			{
    				//if there are no more pointers
    				if(holder[currentLevel]>currentNode.nKeys)
    				{
    					//if we are in the root, the tree is done
    					if(currentLevel==0)
    					{
    						//exit and return
    						break;
    					//if it is a non-root internal node, move up one node and continue
    					}else
    					{
    						//set the current level's index back to 0 in case we visit it again
    						holder[currentLevel] = 0;
    						//move up
    						currentLevel--;
    						//get the parent Node
    						currentNode = (Node) nodeHolder.pop();
    						//increment the location in the node (move to the next key)
    						holder[currentLevel]++;
    						//find out what the next pointer to get will be
    						int toGet = holder[currentLevel];
    						//if the pointer is out of bounds of the node, don't assign K and let the loop handle it during the next iteration 
    						if(!(holder[currentLevel]>currentNode.nKeys))
    						{
    							//otherwise assign the next K
    							currentK = currentNode.key[toGet];
    						}
    					}
    				//if we are not done with the node yet
    				}else
    				{
    					//push the current Node onto the stack to establish it as the parent
    					nodeHolder.push(currentNode);
    					//set the current node with the appropriate pointer
    					currentNode = (Node) currentNode.ref[holder[currentLevel]];
    					//move down
    					currentLevel++;
    					//set the current K to the appropriate key
//out.println("DEBUG:: entrySet: depth is " + depth + " and currentLevel is " + currentLevel);
    					currentK = currentNode.key[holder[currentLevel]];
    				}
    			}
    		}
    	}
        return result;
    } // entrySet

    /***************************************************************************
     * Given the key, look up the value in the B+Tree map.
     * @param key  the key used for look up
     * @return  the value associated with the key
     */
    @SuppressWarnings("unchecked")
    public V get (Object key)
    {
        return find ((K) key, root);
    } // get

    /***************************************************************************
     * Put the key-value pair in the B+Tree map.
     * @param key    the key to insert
     * @param value  the value to insert
     * @return  null (not the previous value)
     */
    public V put (K key, V value)
    {
        insert (key, value, root, null, 0);
        return null;
    } // put

    /***************************************************************************
     * Return the first (smallest) key in the B+Tree map.
     * @return  the first key in the B+Tree map.
     * @author Stephen Lago
     */
    public K firstKey () 
    {
           //check to see if the root is a leaf node
    	if(root.isLeaf)
    	{
    		//if it is, simply return the first key in the root
    		return(root.key[0]);
    	//if the root is not a leaf
    	}else
    	{
    		//start at the root
    		Node current = root;
    		//while the current node is not a leaf
    		while(!(current.isLeaf))
    		{
    			//move down to the leftmost child of the current node
    			current = (Node) current.ref[0];
    		}
    		//return the first key in the (now leaf) node
    		return(current.key[0]);
    	}
    } // firstKey

    /***************************************************************************
     * Return the last (largest) key in the B+Tree map.
     * @return  the last key in the B+Tree map.
     */
    public K lastKey () 
    {
    	//check to see if the root is a leaf node
    	if(root.isLeaf)
    	{
    		//if it is, simply return the first key in the root
    		return(root.key[root.nKeys-1]);
    		//if the root is not a leaf
    	}else
    	{
    		//start at the root
    		Node current = root;
    		//while the current node is not a leaf
    		while(!(current.isLeaf))
    		{
    			//move down to the leftmost child of the current node
    			current = (Node) current.ref[current.nKeys];
    		}
    		//return the first key in the (now leaf) node
    		return(current.key[current.nKeys-1]);
    	}
    } // lastKey

    /***************************************************************************
     * Return the portion of the B+Tree map where key < toKey.
     * @return  the submap with keys in the range [firstKey, toKey)
     * @author Stephen Lago
     */
   public SortedMap <K,V> headMap (K toKey)
    {
    	BpTree<K, V> result = new BpTree<K, V>(classK,classV);
    	//if tokey is less than or equal to the firstKey 
    	if((toKey.compareTo(this.firstKey())<0) || (toKey.compareTo(this.firstKey())==0))
    	{
    		//return null, there are no values to return
    		return(result);
    	}
    	//if the root is a leaf node
    	if(this.root.isLeaf)
    	{
    		int i = 0;
    		//go through all the keys of the root
    		while(i<root.nKeys)
    		{
    			//if the key is less than toKey
    			if(root.key[i].compareTo(toKey)<0)
    			{
    				//add the pair to the result tree
    				result.put(root.key[i],(V) root.ref[i]);
    			//if the key is greater than or equal to toKey, there are no more keys to add
    			}else
    			{
    				//exit the loop
    				break;
    			}
    			//increment
    			i++;
    		}
    	}else
    	{
    		//if the root is not a leaf node
    		//find the depth of the tree
    		int depth = 0;
    		Node currentNode = root;
    		while(!currentNode.isLeaf)
    		{
    			depth++;
    			currentNode = (Node) currentNode.ref[0];
    		}
//out.println("DEBUG:: headMap: depth is " + depth);
    		//begin at the root
    		currentNode = root;
    		//start at the root level
    		int currentLevel = 0;
    		//an array to store the current index of each level (avoiding recursion here)
    		int[] holder = new int[depth+1];
    		Stack<Node> nodeHolder = new Stack<Node>();
    		//initialize the array to an array of 0's
    		for(int i=0;i<depth;i++){holder[i] = 0;}
    		//starting key is root's leftmost key
    		K currentK = root.key[0];
    		//continue through the tree in order, until a larger key is found
    		while(currentK.compareTo(toKey)<0)
    		{
    			//if we are looking at a leaf node
    			if(currentLevel==depth)
    			{
    				//put the current node into the result tree 
    				result.put(currentNode.key[holder[currentLevel]],(V) currentNode.ref[holder[currentLevel]]);
    				//move to the next key in the node
    				holder[currentLevel]++;
    				//if we are done with the node, we must move up one and increment
    				if(holder[currentLevel]>=currentNode.nKeys)
    				{
    					//reset the holder in case we visit this level again 
    					holder[currentLevel] = 0;
    					//move up
    					currentLevel--;
    					//get the parent Node
    					currentNode = (Node) nodeHolder.pop();
    					//move to the next key in the node
    					holder[currentLevel]++;
    					//find out what the next pointer to get will be
    					int toGet = holder[currentLevel];
    					//if the pointer is out of bounds of the node, don't assign K and let the loop handle it during the next iteration 
    					if(!(holder[currentLevel]>=currentNode.nKeys))
    					{
    						//otherwise assign the next K
    						currentK = currentNode.key[toGet];
    					}
    				}
    				//if we are looking at an internal node
    			}else
    			{
    				//if there are no more pointers
    				if(holder[currentLevel]>currentNode.nKeys)
    				{
    					//if we are in the root, the tree is done
    					if(currentLevel==0)
    					{
    						//exit and return
    						break;
    						//if it is a non-root internal node, move up one node and continue
    					}else
    					{
    						//set the current level's index back to 0 in case we visit it again
    						holder[currentLevel] = 0;
    						//move up
    						currentLevel--;
    						//get the parent Node
    						currentNode = (Node) nodeHolder.pop();
    						//increment the location in the node (move to the next key)
    						holder[currentLevel]++;
    						//find out what the next pointer to get will be
    						int toGet = holder[currentLevel];
    						//if the pointer is out of bounds of the node, don't assign K and let the loop handle it during the next iteration 
    						if(!(holder[currentLevel]>currentNode.nKeys))
    						{
    							//otherwise assign the next K
    							currentK = currentNode.key[toGet];
    						}
    					}
    					//if we are not done with the node yet
    				}else
    				{
    					//push the current Node onto the stack to establish it as the parent
    					nodeHolder.push(currentNode);
    					//set the current node with the appropriate pointer
    					currentNode = (Node) currentNode.ref[holder[currentLevel]];
    					//move down
    					currentLevel++;
    					//set the current K to the appropriate key
    					currentK = currentNode.key[holder[currentLevel]];
    				}
    			}
    		}
    	}
    	return result;
    } // headMap

    /***************************************************************************
     * Return the portion of the B+Tree map where fromKey <= key.
     * @return  the submap with keys in the range [fromKey, lastKey]
     * @author Stephen Lago
     */
    public SortedMap <K,V> tailMap (K fromKey)
    {
    	BpTree<K, V> result = new BpTree<K, V>(classK,classV);
    	//if fromkey is greater than or equal to the lastKey 
    	if((fromKey.compareTo(lastKey())>0) || (fromKey.compareTo(lastKey())==0))
    	{
    		//return null, there are no values to return
    		return(result);
    	}
    	//if the root is a leaf node
    	if(this.root.isLeaf)
    	{
    		int i = (root.nKeys-1);
    		//go through all the keys of the root
    		while(i>0)
    		{
    			//if the key is greater than fromKey
    			if(root.key[i].compareTo(fromKey)>0)
    			{
    				//add the pair to the result tree
//out.print("DEBUG:: headmap: attempt to put(" + root.key[i] + "," + (V) root.ref[i] + ")   ");
    				result.put(root.key[i],(V) root.ref[i]);
//out.println("Made it!");
    			//if the key is lesser than or equal to fromKey, there are no more keys to add
    			}else
    			{
    				//exit the loop
    				break;
    			}
    			//increment
    			i--;
    		}
    	}else
    	{
    		//if the root is not a leaf node
    		//find the depth of the tree
    		int depth = 0;
    		Node currentNode = root;
    		while(!currentNode.isLeaf)
    		{
    			depth++;
    			currentNode = (Node) currentNode.ref[0];
    		}
//out.println("DEBUG:: headMap: depth is " + depth);
    		//begin at the root
    		currentNode = root;
    		//start at the root level
    		int currentLevel = 0;
    		//an array to store the current index of each level (avoiding recursion here)
    		int[] holder = new int[depth+1];
    		Stack<Node> nodeHolder = new Stack<Node>();
    		//initialize the array to an array of nKeys's
    		for(int i=0;i<depth;i++)
    		{
    			holder[i] = (currentNode.nKeys-1);
    			currentNode = (Node) currentNode.ref[holder[i]];
    		}
    		//begin at the root
    		currentNode = root;
    		//starting key is root's rightmost key
    		K currentK = root.key[root.nKeys-1];
    		//continue through the tree in reverse order, until a smaller key is found
    		while(currentK.compareTo(fromKey)>0)
    		{
    			//if we are looking at a leaf node
    			if(currentLevel==depth)
    			{
    				//put the current node into the result tree 
    				result.put(currentNode.key[holder[currentLevel]],(V) currentNode.ref[holder[currentLevel]]);
    				//move to the previous key in the node
    				holder[currentLevel]--;
    				//if we are done with the node, we must move up one and increment
    				if(holder[currentLevel]<0)
    				{
    					//move up
    					currentLevel--;
    					//get the parent Node
    					currentNode = (Node) nodeHolder.pop();
    					//move to the next key in the node
    					holder[currentLevel]--;
    					//find out what the next pointer to get will be
    					int toGet = holder[currentLevel];
    					//if the pointer is out of bounds of the node, don't assign K and let the loop handle it during the next iteration 
    					if(!(toGet<0))
    					{
    						//otherwise assign the next K
    						currentK = currentNode.key[toGet];
    						//reset the holder in case we visit the next level again 
    						holder[currentLevel+1] = (((Node) (currentNode.ref[toGet])).nKeys)-1;
    					}
    				}
    				//if we are looking at an internal node
    			}else
    			{
    				//if there are no more pointers
    				if(holder[currentLevel]<0)
    				{
    					//if we are in the root, the tree is done
    					if(currentLevel==0)
    					{
    						//exit and return
    						break;
    						//if it is a non-root internal node, move up one node and continue
    					}else
    					{
    						//move up
    						currentLevel--;
    						//get the parent Node
    						currentNode = (Node) nodeHolder.pop();
    						//increment the location in the node (move to the next key)
    						holder[currentLevel]--;
    						//find out what the next pointer to get will be
    						int toGet = holder[currentLevel];
    						//if the pointer is out of bounds of the node, don't assign K and let the loop handle it during the next iteration 
    						if(!(toGet<0))
    						{
    							//reset the lower level's index in case we visit it again
    							holder[currentLevel+1] = (((Node) (currentNode.ref[toGet])).nKeys)-1;
    							//otherwise assign the next K
    							currentK = currentNode.key[toGet];
    						}
    					}
    					//if we are not done with the node yet
    				}else
    				{
    					//push the current Node onto the stack to establish it as the parent
    					nodeHolder.push(currentNode);
    					//set the current node with the appropriate pointer
    					currentNode = (Node) currentNode.ref[holder[currentLevel]];
    					//move down
    					currentLevel++;
    					//set the current K to the appropriate key
    					currentK = currentNode.key[holder[currentLevel]];
    				}
    			}
    		}
    	}
    	return result;
    } // tailMap

    /***************************************************************************
     * Return the portion of the B+Tree map whose keys are between fromKey and toKey,
     * i.e., fromKey <= key < toKey.
     * @return  the submap with keys in the range [fromKey, toKey)
     * @author Stephen Lago
     */
    public SortedMap <K,V> subMap (K fromKey, K toKey)
    {
    	BpTree<K, V> result = new BpTree<K, V>(classK,classV);

    	result = (BpTree<K, V>) this.headMap(toKey);
    	result = (BpTree<K, V>) result.tailMap(fromKey);
    	
        return result;
    } // subMap

    /***************************************************************************
     * Return the size (number of keys) in the B+Tree.
     * @return  the size of the B+Tree
     */
    public int size ()
    {
        int sum = 0;
	//sum is numKeys
             sum=numKeys;

        return  sum;
    
    } // size

    /***************************************************************************
     * Print the B+Tree using a pre-order traveral and indenting each level.
     * @param n      the current node to print
     * @param level  the current level of the B+Tree
     */
    @SuppressWarnings("unchecked")
    private void print (Node n, int level)
    {
        out.println ("BpTree");
        out.println ("-------------------------------------------");

        for (int j = 0; j < level; j++) out.print ("\t");
        out.print ("[ . ");
        for (int i = 0; i < n.nKeys; i++) out.print (n.key [i] + " . ");
        out.println ("]");
        if ( ! n.isLeaf) {
            for (int i = 0; i <= n.nKeys; i++) print ((Node) n.ref [i], level + 1);
        } // if

        out.println ("-------------------------------------------");
    } // print

    /***************************************************************************
     * Recursive helper function for finding a key in B+trees.
     * @param key  the key to find
     * @param ney  the current node
     */
    @SuppressWarnings("unchecked")
    private V find (K key, Node n)
    {
        count++;
        for (int i = 0; i < n.nKeys; i++) {
            K k_i = n.key [i];
            if (key.compareTo (k_i) <= 0) {
                if (n.isLeaf) {
                    return (key.equals (k_i)) ? (V) n.ref [i] : null;
                } else {
                    return find (key, (Node) n.ref [i]);
                } // if
            } // if
        } // for
        return (n.isLeaf) ? null : find (key, (Node) n.ref [n.nKeys]);
    } // find

    /***************************************************************************
     * Recursive helper function for inserting a key in B+trees.
     * @param key  the key to insert
     * @param ref  the value/node to insert
     * @param n    the current node
     * @param p    the parent node
     */
    private void insert (K key, V ref, Node n, Node p, int level)
    {
    	//if we are in a leaf node
        if(n.isLeaf)
        {
        	//if there is room in the node
        	if(n.nKeys < (ORDER-1))
        	{
        		boolean haveWedged = false;
        		for (int i = 0; (i < n.nKeys && haveWedged==false); i++) {
                    K k_i = n.key [i];
//out.println("DEBUG:: insert: key = " + key + ", k_i = " + k_i + ", nKeys = " + n.nKeys);
                    if (key.compareTo (k_i) < 0) {
//out.println("DEBUG:: INSERT:less than");
                        wedge (key, ref, n, i);
                        haveWedged = true;
                    } else if (key.equals (k_i)) {
                        out.println ("BpTree:insert: attempt to insert duplicate key = " + key);
                        haveWedged = true;
                    } // if
                }// for
        		if(!haveWedged)
        		{
        			wedge(key,ref,n,n.nKeys);
        		}// if
        		return;
        	//if there is no more room in the leaf node
        	}else
        	{
        		//split the node and get a reference to the new one
        		Node newNode = split(key,ref,n,level);
        		//if we are at the root level
        		if(level==0)
        		{
        			//simply assign the resulting node as the root and walk away 
        			root = newNode;
        			root.isLeaf = false;
        			return;
        		//if we are at a non-root leaf node and there is no more room
        		}else
        		{
        			//if the new node comes before any of the keys in the old node
        			if(newNode.key[0].compareTo(p.key[0])<0)
        			{
//out.println("DEBUG:: insert: node must be fused and new Node comes before all others");
        				//iterate over the keys in the old node
        				for(int i=p.nKeys;i>0;i--)
        				{      					
        					//shift them right
        					p.key[i] = p.key[i-1];
        					p.ref[i+1] = p.ref[i];
//out.println("DEBUG:: insert: turn:"+i+" ref[i]="+((Node)p.ref[i]).key[0] + "; p.key[0]=" + p.key[0]);          					
        				}
        				//assign the first keys and refs accordingly
        				p.key[0] = newNode.key[0];
        				p.ref[0] = newNode.ref[0];
        				p.ref[1] = newNode.ref[1];
        				p.nKeys++;
        				return;
        			//if the newNode does not come before all the keys of the old node
        			}else
        			{
//out.println("DEBUG:: insert: inserting " + key);
        				//iterate over the keys until you find the right place to insert
        				int i = 0;
        				for(i=0;i<p.nKeys;i++)
        				{
        					//if you have found the place to insert
        					if(newNode.key[0].compareTo(p.key[i])<0)
        					{
//out.println("DEBUG:: insert: " + newNode.key[0] + " was less than " + p.key[i]);
        						//shift the ones to the right of it one slot to the right
        						for(int j=n.nKeys;j>i;j--)
                				{
                					//shift them right
                					n.key[j] = n.key[j-1];
                					n.ref[j] = n.ref[j-1];
                				}// for
        						n.key[i] = newNode.key[0];
        						n.ref[i] = newNode.ref[0];
        						n.ref[i+1] = newNode.ref[1];
        						n.nKeys++;
        						return;
        					}// if
        				}// for
    					//otherwise, the key belongs at the end of the node
    					p.key[p.nKeys] = newNode.key[0];
    					p.ref[p.nKeys] = newNode.ref[0];
    					p.ref[p.nKeys+1] = newNode.ref[1];
    					p.nKeys++;
    					return;
        			}// if
        		}// if
        	}// if
        //if we are in a non-leaf node
        }else
        {
        	//go through all the keys in the node
        	for(int i=0;i<n.nKeys;i++)
        	{
        		//if the key to insert is less than the current key
        		if(key.compareTo(n.key[i])<0)
        		{
        			//insert (recursively) the new/value pair into the appropriate Node
        			insert(key,ref,(Node)n.ref[i],n,level+1);
        			return;
        		}
        	}
        	//otherwise, it belongs in the last Node, so insert it accordingly
        	insert(key,ref,(Node)n.ref[n.nKeys],n,level+1);
        	return;
        }
    } // insert

    /***************************************************************************
     * Wedge the key-ref pair into node n.
     * @param key  the key to insert
     * @param ref  the value/node to insert
     * @param n    the current node
     * @param i    the insertion position within node n
     */
    private void wedge (K key, V ref, Node n, int i)
    {
 //out.print("DEBUG:: wedge: wedging; ");
        for (int j = n.nKeys; j > i; j--) {
            n.key [j] = n.key [j - 1];
            n.ref [j] = n.ref [j - 1];
//out.println("looking at position " + j);
        } // for
        n.key [i] = key;
        n.ref [i] = ref;
        n.nKeys++;
//out.println("DEBUG:: wedge: wedged key " + key + " into position " + i);
    } // wedge

    /***************************************************************************
     * Split node n and return the newly created node.
     * @param key  the key to insert
     * @param ref  the value/node to insert
     * @param n    the current node
     */
    private Node split (K key, V ref, Node n, int level)
    {
    	//if we're splitting a leaf node
    	if(n.isLeaf)
    	{
    		//the new node can never be a leaf (by nature of split)
    		Node result = new Node(false);
    		//make an array to hold all 5 keys
    		ArrayList<K> karray = new ArrayList<K>();
    		//and another for the value
    		ArrayList<V> varray = new ArrayList<V>();
    		boolean addedNew = false;
    		//cycle through the node and add the keys in order
    		for (int i = 0; i < n.nKeys; i++) {
//out.println("DEBUG:: split: key[i] = " + n.key[i]);
    			//if the new key is less than the next key
    			if(key.compareTo(n.key[i])<0 && (!addedNew))
    			{
//out.println("DEBUG:: split: splitting with new value being smallest");
    				karray.add(key);
    				varray.add(ref);
    				karray.add(n.key[i]);
    				varray.add((V) n.ref[i]);
    				addedNew = true;
    			}else
    			{
    				karray.add(n.key[i]);
    				varray.add((V) n.ref[i]);
    			}// if
    		}// for
    		//if the new key is larger than all the keys already in the node
    		if(key.compareTo(n.key[n.nKeys-1])>0)
    		{
    			karray.add(key);
    			varray.add(ref);
    		}// if
    		//the keys should all be in order now in karray
    		//make a new Node to be the leftchild with the same leafality as n
    		Node leftChild = new Node(n.isLeaf);
    		//make a new Node to be the rightchild with the same leafality as n
    		Node rightChild = new Node(n.isLeaf);
    		//put the appropriate values in the appropriate nodes
    		insert((K)karray.get(0),(V)varray.get(0),leftChild,result,level);
    		insert((K)karray.get(1),(V)varray.get(1),leftChild,result,level);
    		insert((K)karray.get(2),(V)varray.get(2),leftChild,result,level);
    		insert((K)karray.get(3),(V)varray.get(3),rightChild,result,level);
    		insert((K)karray.get(4),(V)varray.get(4),rightChild,result,level);
    		result.key[0]=(K)karray.get(2);
    		result.nKeys++;
    		result.ref[0] = leftChild;
    		result.ref[1] = rightChild;
            return(result);
    	//if we're not splitting a leaf node
    	}else
    	{
            return(null);
    	}// if
    } // split

    /***************************************************************************
     * The main method used for testing.
     * @param  the command-line arguments (args [0] gives number of keys to insert)
     */
    public static void main (String [] args)
    {
        BpTree <Integer, Integer> bpt = new BpTree <> (Integer.class, Integer.class);
        int totKeys = 31;
        //if (args.length == 1) totKeys = Integer.valueOf (args [0]);
/*        for (int i = totKeys; i > 0; i -= 2)
        {
out.println("inserting " + i);
        	bpt.put (i, i * i);
        }
//*/        for (int i = 1; i < totKeys; i += 2) bpt.put (i, i * i);
        bpt.print (bpt.root, 0);
        for (int i = 0; i < totKeys; i++) {
            out.println ("key = " + i + " value = " + bpt.get (i));
        } // for
/*        out.println ("-------------------------------------------");
        out.println ("First key is " + bpt.firstKey());
        out.println ("Last key is " + bpt.lastKey());
        out.println ("-------------------------------------------");
        out.println ("Average number of nodes accessed = " + bpt.count / (double) totKeys);
        out.println("--------------------------------------------");
        out.println("Testing for entrySet: ");
        Set <Map.Entry <Integer, Integer>> mySet = new HashSet <> ();
        mySet = bpt.entrySet();
        out.println("Entry Set created for tree");
        out.println("Iterating over the created set: ");
        Iterator itr = mySet.iterator();
        while(itr.hasNext())
        {
        	Map.Entry ent = (Map.Entry<Integer, Integer>) itr.next();
        	out.println("key = " + ent.getKey() + "; value = " + ent.getValue());
        }
        out.println("--------------------------------------------");
        out.println("Testing for Submap methods: ");
        out.println("Testing headMap(): ");
        out.println("When index is below min (empty tree): ");
        BpTree <Integer, Integer> bpth1 = (BpTree<Integer, Integer>) bpt.headMap(0);
        bpth1.print (bpth1.root, 0);
        out.println("\nWhen index is above max key (original tree): ");
        BpTree <Integer, Integer> bpth2 = (BpTree<Integer, Integer>) bpt.headMap(100);
        bpth2.print (bpth2.root, 0);
        out.println("\nWhen index is between min and max keys (sub tree): ");
        BpTree <Integer, Integer> bpth3 = (BpTree<Integer, Integer>) bpt.headMap(16);
        bpth3.print (bpth3.root, 0);
        out.println("\n\nTesting tailMap(): ");
        out.println("\nWhen index is above max (empty tree): ");
        BpTree <Integer, Integer> bptt2 = (BpTree<Integer, Integer>) bpt.tailMap(100);
        bptt2.print (bptt2.root, 0);
        out.println("When index is below min (original tree): ");
        BpTree <Integer, Integer> bptt1 = (BpTree<Integer, Integer>) bpt.tailMap(0);
        bptt1.print (bptt1.root, 0);
        out.println("\nWhen index is between min and max (sub tree): ");
        BpTree <Integer, Integer> bptt3 = (BpTree<Integer, Integer>) bpt.tailMap(16);
        bptt3.print (bptt3.root, 0);
        out.println("\n\nTesting subMap(): ");
        out.println("When index1 is below min (essentially head map): ");
        BpTree <Integer, Integer> bpts1 = (BpTree<Integer, Integer>) bpt.subMap(0,16);
        bpts1.print (bpts1.root, 0);
        out.println("\nWhen index2 is above max (essentially tail map): ");
        BpTree <Integer, Integer> bpts2 = (BpTree<Integer, Integer>) bpt.subMap(16,100);
        bpts2.print (bpts2.root, 0);
        out.println("\nWhen index1 is above min index2 is below max (true sub tree): ");
        BpTree <Integer, Integer> bpts3 = (BpTree<Integer, Integer>) bpt.subMap(4,24);
        bpts3.print (bpts3.root, 0);*/
    } // main

} // BpTree class
