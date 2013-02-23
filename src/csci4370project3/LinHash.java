package csci4370project3;

/*******************************************************************************
 * @file LinHash.java
 *
 * @author  John Miller
 */

import java.io.*;
import java.lang.reflect.Array;
import static java.lang.System.out;
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
        Bucket (Bucket n)
        {
            nKeys = 0;
            key   = (K []) Array.newInstance (classK, SLOTS);
            value = (V []) Array.newInstance (classV, SLOTS);
            next  = n;
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
     */
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
     */
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
        
        //While in the hash table
        while( position < lastBucketPosition ){
            
            //While position is less than the number of positions for a bit length
            //For example if numOfBits is 2 position will iterate 4 times( 00,01,10,11 )
            //If numOfBits is 3 is will iterate 8 times ( 000,001,010,011,100,101,110,111 )
            while( position / Math.pow(2, numOfBits) < 1 ){

                //If your bit string for the key to find is not at least the number
                //of bits, prepend 0's until you reach the numOfBits. This 
                //will allow for the substring to consistently pull the right
                //amount of characters out of the string
                if( bin.length() < numOfBits ){
                    String prepend = "";
                    for( int i = 0 ; i < numOfBits - bin.length() ; i++ ){
                        prepend += "0";
                    }
                    bin = prepend + bin;
                }
                
                //If the position bit string is not at least the number
                //of bits, prepend 0's until you reach the numOfBits. This 
                //will allow for the substring to consistently pull the right
                //amount of characters out of the string
                String rowString = Integer.toBinaryString(position);
                if( rowString.length() < numOfBits ){
                    String prepend = "";
                    for( int i = 0 ; i < numOfBits - rowString.length() ; i++ ){
                        prepend += "0";
                    }
                    rowString = prepend + rowString;
                }
                           
                //Generate substrings from the key and position based on the numOfBits
                //value and compare them
                if( bin.substring(bin.length()-numOfBits).equals( rowString.substring(rowString.length()-numOfBits ) ) ){
                    //If the values match iterate through all elements in the 
                    //current bucket and see if they key passed matches
                    //the key in the bucket
                    for( int i = 0 ; i < hTable.get(position).nKeys ; i++ ){
                        if( hTable.get(position).key[i] == key ){
                            //If the keys match, return the value from the pair
                            return hTable.get(position).value[i];
                        }//if
                    }//for
                }//if
            
                //Move through the hash table
                position++;
                
            }
            
            //Increase the number of bits to compare
            numOfBits++;
        }
        
        //If no match is found return null.
        return null;
    } // get

    /***************************************************************************
     * Put the key-value pair in the hash table.
     * @param key    the key to insert
     * @param value  the value to insert
     * @return  null (not the previous value)
     */
    public V put (K key, V value)
    {
        if (key == null || value == null) {
            return null;
        }

        if( this.hTable.isEmpty() ){
            this.hTable.add(new LinHash.Bucket());
            this.hTable.add(new LinHash.Bucket());
            this.hTable.add(new LinHash.Bucket());
            this.hTable.add(new LinHash.Bucket());
        }
        
        int keyInt = h (key);
        int compareSize = 1;
        int counter = 0;
        while( compareSize > 0 ){
            compareSize = hTable.size() / (int) Math.pow( 2, counter);
            System.out.println("compareSize: " + compareSize);
            counter++;
            System.out.println("counter: " + counter);
        }
        int compared = hTable.size();
        
        //Convert key hash to bit string
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(keyInt);
        String bin = Integer.toBinaryString(keyInt);

        System.out.println(bin);
        
        while( counter > 0 ){
            
            if( bin.length() > Integer.toBinaryString(keyInt).length() ){
                if( bin.substring(bin.length()-counter).toString().equals(Integer.toBinaryString(keyInt)) ){
                   if( hTable.get(compared-1).nKeys < 4){
                        hTable.get(compared-1).key[hTable.get(compared-1).nKeys] = key;
                        hTable.get(compared-1).value[hTable.get(compared-1).nKeys] = value;
                        counter = 0;
                        insertCount++;
                        hTable.get(compared-1).nKeys++;
                        return null;
                    }
                }
            }
            else if( bin.length() == Integer.toBinaryString(keyInt).length() ){  
                if( bin.equals(Integer.toBinaryString(keyInt)) ){
                   if( hTable.get(compared-1).nKeys < 4){
                        hTable.get(compared-1).key[hTable.get(compared-1).nKeys] = key;
                        hTable.get(compared-1).value[hTable.get(compared-1).nKeys] = value;
                        counter = 0;
                        insertCount++;
                        hTable.get(compared-1).nKeys++;
                        return null;
                    }
                }
            }
            else{
                String prepend = "";
                for( int i = 0 ; i < compared - bin.length() ; i++ ){
                    prepend += "0";
                }
                
                bin = prepend + bin;
                
                if( bin.equals(Integer.toBinaryString(compared)) ){
                   if( hTable.get(compared-1).nKeys < 4){
                        hTable.get(compared-1).key[hTable.get(compared-1).nKeys] = key;
                        hTable.get(compared-1).value[hTable.get(compared-1).nKeys] = value;
                        counter = 0;
                        insertCount++;
                        hTable.get(compared-1).nKeys++;
                        return null;
                    }
                }
                
                
                
            }
            
            compared--;
            counter--;
        }      
                
        return null;
    } // put

    /***************************************************************************
     * Return the size (SLOTS * number of home buckets) of the hash table. 
     * @return  the size of the hash table
     */
    public int size ()
    {
        return SLOTS * (mod1 + split);
    } // size

    /***************************************************************************
     * Print the hash table.
     */
    private void print ()
    {
        out.println ("Hash Table (Linear Hashing)");
        out.println ("-------------------------------------------");
        
        //Use the entry set method and generate a set of all key/value pairs
        //in the hash table
        Set <Map.Entry <K, V>> linSet = this.entrySet();
        Iterator itr = linSet.iterator();
        
        //Use an iterator over the set to get the key/hash/value pairs for each bucket
        //Print them out per bucket
        for( int i = 0 ; i < hTable.size() ; i++ ){
            System.out.println("Bucket " + i + ":");
            for( int j = 0 ; j < hTable.get(i).nKeys ; j++ ){
                Map.Entry ent = (Map.Entry<K, V>) itr.next();
                System.out.println( "   Key: " + ent.getKey() +  " | Hash: " + h( ent.getKey() )  + " | Value: " + ent.getValue() );
            }
        }

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
        LinHash <Integer, Integer> ht = new LinHash <> (Integer.class, Integer.class, 11);
        int nKeys = 30;
        if (args.length == 1) nKeys = Integer.valueOf (args [0]);
        for (int i = 1; i < nKeys; i += 2) ht.put (i, i * i);
        ht.print ();
        for (int i = 0; i < nKeys; i++) {
            out.println ("key = " + i + " value = " + ht.get (i));
        } // for
        out.println ("-------------------------------------------");
        out.println ("Average number of buckets accessed = " + ht.count / (double) nKeys);
    } // main

} // LinHash class

