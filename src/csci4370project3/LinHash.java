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
        
        for( int i = 0 ; i < hTable.size() ; i++ ){
            
            for( int j = 0 ; j < hTable.get(i).nKeys ; j++ ){
                mp.put(hTable.get(i).key[j], hTable.get(i).value[j]);
            }//for
            
        }//for
        
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
        int keyInt = h (key);
        
        String bin = Integer.toBinaryString(keyInt);
        
        int position = 0;
        int lastBucketPosition = hTable.size();
        int numOfBits = 2;
        
        while( position < lastBucketPosition ){
            
            while( position / Math.pow(2, numOfBits) < 1 ){

                if( bin.length() < numOfBits ){
                    String prepend = "";
                    for( int i = 0 ; i < numOfBits - bin.length() ; i++ ){
                        prepend += "0";
                    }
                    bin = prepend + bin;
                }
                
                String rowString = Integer.toBinaryString(position);
                
                if( rowString.length() < numOfBits ){
                    String prepend = "";
                    for( int i = 0 ; i < numOfBits - rowString.length() ; i++ ){
                        prepend += "0";
                    }
                    rowString = prepend + rowString;
                }
                           
                if( bin.substring(bin.length()-numOfBits).equals( rowString.substring(rowString.length()-numOfBits ) ) ){
                    for( int i = 0 ; i < hTable.get(position).nKeys ; i++ ){
                        if( hTable.get(position).key[i] == key ){
                            return hTable.get(position).value[i];
                        }//if
                    }//for
                }//if
            
                //Move through the hash table
                position++;
                
            }
            
            numOfBits++;
        }
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
        
        Set <Map.Entry <K, V>> linSet = this.entrySet();
        Iterator itr = linSet.iterator();
        
        for( int i = 0 ; i < hTable.size() ; i++ ){
            System.out.println("Bucket " + i + ":");
            for( int j = 0 ; j < hTable.get(i).nKeys ; j++ ){
                Map.Entry ent = (Map.Entry<K, V>) itr.next();
                System.out.println( "   Key: " + ent.getKey().hashCode() + " | Value: " + ent.getValue() );
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

