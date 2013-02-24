package csci4370project3;

/*******************************************************************************
 * @file LinHash.java
 *
 * @author  John Miller
 */

import java.io.*;
import static java.lang.System.out;
import java.lang.reflect.Array;
import java.util.*;

/*******************************************************************************
 * This class provides hash maps that use the Linear Hashing algorithm.
 * A hash table is created that is an array of buckets.
 */
public class LinHash <K, V>
       extends AbstractMap <K, V>
       implements Serializable, Cloneable, Map <K, V>
{
    /** The number of slots (for key-value pairs) per bucket.
     */
    private static final int SLOTS = 4;

    /** The class for type K.
     */
    private final Class <K> classK;

    /** The class for type V.
     */
    private final Class <V> classV;

    /***************************************************************************
     * This inner class defines buckets that are stored in the hash table.
     */
    private class Bucket
    {
        int    nKeys;
        K []   key;
        V []   value;
        Bucket next;
        @SuppressWarnings("unchecked")
        Bucket ()
        {
            nKeys = 0;
            key   = (K []) Array.newInstance (classK, SLOTS);
            value = (V []) Array.newInstance (classV, SLOTS);
            next  = null;
        } // constructor
    } // Bucket inner class

    /** The list of buckets making up the hash table.
     */
    private final List <Bucket> hTable;

    /** The modulus for low resolution hashing
     */
    private int mod1;

    /** The modulus for high resolution hashing
     */
    private int mod2;

    /** Counter for the number buckets accessed (for performance testing).
     */
    private int count = 0;

    /** The index of the next bucket to split.
     */
    private int split = 0;
	
     /**
     * The number of items inserted into the hash table
     */
    private int insertCount = 0;

    /***************************************************************************
     * Construct a hash table that uses Linear Hashing.
     * @param classK    the class for keys (K)
     * @param classV    the class for keys (V)
     * @param initSize  the initial number of home buckets (a power of 2, e.g., 4)
     */
    public LinHash (Class <K> _classK, Class <V> _classV, int initSize)
    {
        classK = _classK;
        classV = _classV;
        hTable = new ArrayList <> ();
        mod1   = initSize;
        mod2   = 2 * mod1;
    } // LinHash

    /***************************************************************************
     * Return a set containing all the entries as pairs of keys and values.
     * @return  the set view of the map
     * @author Woong Kim
     */
    @Override
    public Set <Map.Entry <K, V>> entrySet ()
    {
        //Set <Map.Entry <K, V>> enSet = new HashSet <> ();
        Map<K, V> mp = new HashMap<>();
         
        //Go through the hTable and get all key/value pairs into a map
        for( int i = 0 ; i < hTable.size() ; i++ ){
            
            for( int j = 0 ; j < hTable.get(i).nKeys ; j++ ){
                mp.put(hTable.get(i).key[j], hTable.get(i).value[j]);
            }//for
            
        }//for

        //Put the map into a set for return value
        Set <Map.Entry <K, V>> enSet = mp.entrySet();
            
        return enSet;
    } // entrySet

    /***************************************************************************
     * Given the key, look up the value in the hash table.
     * @param key  the key used for look up
     * @return  the value associated with the key
     * @author Jeffrey Swindle
     */
    @Override
    public V get (Object key)
    {
        //Hash the key
        int keyInt = h (key);
        
        //Convert the hash to a string of bits
        String bin = Integer.toBinaryString(keyInt);
        
        //Value to maintain position in the hash table
        int position = 0;
        //Value to maintain the size of the hash table
        int lastBucketPosition = hTable.size();
        //Number of current bits to compare
        int numOfBits = 2;
        //A boolean value for moving the position counter to be equal to the 
        //binary string of the key hash
        boolean first = true;
        
        //While in the hash table
        while( position < lastBucketPosition ){       
            //While position is less than the number of positions for a bit length
            //For example if numOfBits is 2 position will iterate 4 times( 00,01,10,11 )
            //If numOfBits is 3 is will iterate 8 times ( 000,001,010,011,100,101,110,111 )
            while( ( position / Math.pow(2, numOfBits) < 1 ) && ( position < lastBucketPosition ) ){  
                
                //Convert the position to a binary string
                String rowString = Integer.toBinaryString(position); 
                
                //If the binary string to compare is longer than the 
                //position string just compare the bin.length() last characters
                //of the position string
                if( bin.length() < rowString.length() ){                    
                    //Only compare the position string - bin.length() to ignore
                    //leader characters in the position string and there is more 
                    //than one key value at the location
                    if( bin.equals( rowString.substring(rowString.length()-bin.length() ) ) && hTable.get(position).nKeys > 0 ){
                        count++;
                        //If the values match iterate through all elements in the 
                        //current bucket and see if they key passed matches
                        //the key in the bucket
                        for( int i = 0 ; i < hTable.get(position).nKeys ; i++ ){
                            //System.out.println("key = " + hTable.get(position).key[i]);
                            if( hTable.get(position).key[i].equals(key) ){ 
                                //If the keys match, return the value from the pair
                                return hTable.get(position).value[i];
                            }//if
                        }//for
                    }//if  
                }//if
                //Otherwise, if the binary string is longer than the position
                //string set the position and position string to be equal
                //to the binary string and do an initial comparions
                else if( bin.length() > rowString.length() ){
                    //If the binary key value hash is greater than the position
                    //set the position to the binary key value hash and update
                    //the binary posision string
                    //This matches up with what is done in the put method
                    if( first ){
                        int base = 2;
                        position = Integer.parseInt(bin, base);
                        rowString = Integer.toBinaryString(position);
                        first = false;
                    }//if
                    //If for some reason your posistion has exceeded end of the table
                    //return null
                    if( position >= hTable.size() ){
                        return null;
                    }//if
                    //Compare the binary and position strings directly and there 
                    //is more than one key value at the location
                    if( bin.equals( rowString ) && hTable.get(position).nKeys > 0 ){
                        count++;
                        //If the values match iterate through all elements in the 
                        //current bucket and see if they key passed matches
                        //the key in the bucket
                        for( int i = 0 ; i < hTable.get(position).nKeys ; i++ ){
                            if( hTable.get(position).key[i].equals(key)){
                                //If the keys match, return the value from the pair
                                return hTable.get(position).value[i];
                            }//if
                        }//for
                    }//if 
                }//if
                //If the binary string and position string are the same length
                //compare the two directly and there is more than one key value
                //at the location
                else{
                    if( bin.equals( rowString ) && hTable.get(position).nKeys > 0 ){
                        //If the values match iterate through all elements in the 
                        //current bucket and see if they key passed matches
                        //the key in the bucket
                        count++;
                        for( int i = 0 ; i < hTable.get(position).nKeys ; i++ ){
                            if( hTable.get(position).key[i].equals(key)){
                                //If the keys match, return the value from the pair
                                return hTable.get(position).value[i];
                            }//if
                        }//for
                    }//if       
                }//else
                //Move through the hash table
                position++;    
            }//inner while
            //Increase the number of bits to compare
            numOfBits++;
        }//outer while
        
        //If no match is found return null.
        return null;
        
    } // get

    /***************************************************************************
     * Put the key-value pair in the hash table.
     * @param key    the key to insert
     * @param value  the value to insert
     * @return  null (not the previous value)
     * @author Jeffrey Swindle
     */
    @Override
    public V put (K key, V value)
    {
        //If the given key or value is null return null
        if (key == null || value == null) {
            return null;
        }

        //If this is the first iteration of the linear hash create
        //the four base buckets
        if( this.hTable.isEmpty() ){
            this.hTable.add(new LinHash.Bucket());
            this.hTable.add(new LinHash.Bucket());
            this.hTable.add(new LinHash.Bucket());
            this.hTable.add(new LinHash.Bucket());
        }
        
        //Hash the key
        int keyInt = h (key);
        
        //Convert the hash to a string of bits
        String bin = Integer.toBinaryString(keyInt);
        
        //Value to maintain position in the hash table
        int position = 0;
        //Number of current bits to compare
        int numOfBits = 2;
        
        //While in the hash table
        while( position < hTable.size() ){
            //While position is less than the number of positions for a bit length
            //For example if numOfBits is 2 position will iterate 4 times( 00,01,10,11 )
            //If numOfBits is 3 is will iterate 8 times ( 000,001,010,011,100,101,110,111 )
            while( position / Math.pow(2, numOfBits) < 1 ){

                //If your bit string for the key to find is less than two
                //add a leading zero for comparisons since we are starting with
                //two bit comparions
                if( bin.length() < 2 ){
                    String prepend = "";
                    for( int i = 0 ; i < bin.length() ; i++ ){
                        prepend += "0";
                    }//for
                    bin = prepend + bin;
                }//if
                
                //If the position bit string is not at least the number
                //of bits, prepend 0's until you reach the numOfBits. This 
                //will allow for the substring to consistently pull the right
                //amount of characters out of the string
                String rowString = Integer.toBinaryString(position);
                if( rowString.length() < bin.length() ){
                    String prepend = "";
                    for( int i = 0 ; i < bin.length()-1 ; i++ ){
                        prepend += "0";
                    }//for
                    rowString = prepend + rowString;
                }//if
                
                //Generate substrings from the key and position based the key to
                //find and substrings of the row hashes in the db
                if( bin.equals( rowString.substring( rowString.length() - bin.length()) ) ){                 
                    //If you need to insert into a bucket that isn't yet in the table
                    //add new buckets 4 at a time until the desired the position is reached
                    while( position >= hTable.size() ){
                        this.addNewBuckets();
                    }//while                   
                    //If the bucket is full create a new set of buckets
                    if( hTable.get(position).nKeys == 4 ){
                       this.addNewBuckets();
                    }//if
                    //Otherwise if the bucket is not full, insert the key
                    //value pair into the bucket
                    else if( hTable.get(position).nKeys < 4 ){
                        hTable.get(position).key[hTable.get(position).nKeys] = key;
                        hTable.get(position).value[hTable.get(position).nKeys] = value;
                        insertCount++;
                        hTable.get(position).nKeys++;
                        return null;
                    }//else if 
                }//if 
                //Move through the hash table
                position++;   
            }//inner while
            //Increase the number of bits to compare
            numOfBits++;
            //If you need to insert into a bucket that isn't yet in the table
            //add new buckets 4 at a time until the desired the position is reached
            while( position >= hTable.size() ){
                this.addNewBuckets();
            }//while
        }//outer while

        return null;
    } // put
    
    /**
     * Add four new buckets for each time called
     * @author Jeffrey Swindle
     */
    private void addNewBuckets(){
        
        //Add four buckets
        this.hTable.add(new Bucket());
        this.hTable.add(new Bucket());
        this.hTable.add(new Bucket());
        this.hTable.add(new Bucket());
        
    }

    /***************************************************************************
     * Return the size (SLOTS * number of home buckets) of the hash table. 
     * @return  the size of the hash table
     */
    @Override
    public int size ()
    {
        return SLOTS * (mod1 + split);
    } // size

    /***************************************************************************
     * Print the hash table.
     * @author Jeffrey Swindle
     */
    public void print ()
    {
        out.println ("Hash Table (Linear Hashing)");
        out.println ("-------------------------------------------");
        
        //Go through the hTable and get all key/value pairs into a map
        for( int i = 0 ; i < hTable.size() ; i++ ){
           //Only print the buckets that have key/value pairs in them
           if( hTable.get(i).nKeys > 0 ){
                System.out.println("Bucket " + i + ":");
            }//if
            //for each bucket print all key/value pairs in that bucket
            for( int j = 0 ; j < hTable.get(i).nKeys ; j++ ){
                System.out.println( "   Key: " + hTable.get(i).key[j] +  " | Hash: " + h( hTable.get(i).key[j] )  + " | Value: " + hTable.get(i).value[j] );
            }//for
            
        }//for
        
        out.println ("-------------------------------------------");
    } // print

    /***************************************************************************
     * Hash the key using the low resolution hash function.
     * @param key  the key to hash
     * @return  the location of the bucket chain containing the key-value pair
     */
    private int h (Object key)
    {
        return key.hashCode () % mod1;
    } // h

    /***************************************************************************
     * Hash the key using the high resolution hash function.
     * @param key  the key to hash
     * @return  the location of the bucket chain containing the key-value pair
     */
    private int h2 (Object key)
    {
        return key.hashCode () % mod2;
    } // h2

    /***************************************************************************
     * The main method used for testing.
     * @param  the command-line arguments (args [0] gives number of keys to insert)
     */
    public static void main (String [] args)
    {
        LinHash <Integer, Integer> ht = new LinHash <> (Integer.class, Integer.class, 12);
        int nKeys = 1000;
        if (args.length == 1){
            nKeys = Integer.valueOf (args [0]);
        }
        for (int i = 1; i < nKeys; i += 2){
            ht.put (i, i * i);
        }
        ht.print ();
        for (int i = 1; i < nKeys ; i+=2) {
            out.println ("key = " + i + " value = " + ht.get (i));
        } // for
        ht.print();
        out.println ("-------------------------------------------");
        out.println ("Average number of buckets accessed = " + ht.count / (double) nKeys);
    } // main

} // LinHash class

