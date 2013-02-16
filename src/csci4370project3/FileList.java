package csci4370project3;


/*******************************************************************************
 * @file  FileList.java
 *
 * @author   John Miller
 */

import java.io.*;
import static java.lang.System.out;
import java.util.*;

/*******************************************************************************
 * This class allows data tuples/tuples (e.g., those making up a relational table)
 * to be stored in a random access file.  This implementation requires that each
 * tuple be packed into a fixed length byte array.
 */
public class FileList
       extends AbstractList <Comparable []>
       implements List <Comparable []>, RandomAccess
{
    /** File extension for data files.
     */
    private static final String EXT = ".dat";

    /** The random access file that holds the tuples.
     */
    private RandomAccessFile file;

    /** The table it is used to store.
     */
    private final Table table;

    /** The number bytes required to store a "packed tuple"/record.
     */
    private final int recordSize;

    /** Counter for the number of tuples in this list.
     */
    private int nRecords = 0;

    /***************************************************************************
     * Construct a FileList.
     * @param _table       the name of list
     * @param _recordSize  the size of tuple in bytes.
     */
    public FileList (Table _table, int _recordSize)
    {
        table      = _table;
        recordSize = _recordSize;

        try {
            file = new RandomAccessFile (table.getName () + EXT, "rw");
        } catch (FileNotFoundException ex) {
            file = null;
            out.println ("FileList.constructor: unable to open - " + ex);
        } // try
    } // constructor

    /***************************************************************************
     * Add a new tuple into the file list by packing it into a record and writing
     * this record to the random access file.  Write the record either at the
     * end-of-file or into a empty slot.
     * @param tuple  the tuple to add
     * @return  whether the addition succeeded
     * @author Nick Burlingame
     */
    public boolean add (Comparable [] tuple)
    {
        byte [] record = table.pack(tuple);

        if (record.length != recordSize) {
            out.println ("FileList.add: wrong record size " + record.length);
            return false;
        } // if
        try{
            file.write(record);
            nRecords++;
        }
        catch(Exception e){
            System.out.println("FileList.add: unable to write record");
        }

        return true;
    } // add

    /***************************************************************************
     * Get the ith tuple by seeking to the correct file position and reading the
     * record.
     * @param i  the index of the tuple to get
     * @return  the ith tuple
     * @author Nick Burlingame
     */
    public Comparable [] get (int i)
    {
        byte [] record = new byte [recordSize];
        try{
            file.seek(i*recordSize);
            file.read(record);
            Comparable [] comparableReturn = table.unpack(record);
            return comparableReturn;
            
        }
        catch(Exception e){
            System.out.println("FileList.get: Unable to seek record");
        }

        return null;
    } // get

    /***************************************************************************
     * Return the size of the file list in terms of the number of tuples/records.
     * @return  the number of tuples
     */
    public int size ()
    {
        return nRecords;
    } // size

    /***************************************************************************
     * Close the file.
     */
    public void close ()
    {
        try {
            file.close ();
        } catch (IOException ex) {
            out.println ("FileList.close: unable to close - " + ex);
        } // try
    } // close

} // FileList class

