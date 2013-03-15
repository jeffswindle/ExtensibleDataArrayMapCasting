package CSCI4370Project4;

/*******************************************************************************
 * @file  TupleGenerator.java
 *
 * @author   Sadiq Charaniya, John Miller
 * Modified by: Jeffrey Swindle
 */

/*******************************************************************************
 * This interface can be used for generating tuples to populate a database.
 */
public interface TupleGenerator
{
    /***************************************************************************
     * Add the relational schema for a given table.  Do this for all tables and
     * then generate the tuples.
     * @param name        the table's name
     * @param attribute   the array holding the table's attributes
     * @param domain      the array holding the table's domains
     * @param primaryKey  the array of primary keys, format is {"id", "ssn"}
     */
    void addRelSchema (String name, String [] attribute, String [] domain,
                              String [] primaryKey); 

    /***************************************************************************
     * Add the relational schema for a given table.  Do this for all tables and
     * then generate the tuples.  This is convenience method.
     * @param name        the table's name
     * @param attribute   the string embedding the table's attributes
     * @param domain      the string embedding the table's domains
     * @param primaryKey  the string embedding the table's primary keys
     */
    void addRelSchema (String name, String attribute, String domain,
                              String primaryKey); 

    /***************************************************************************
     * Generate tuples for all of the tables.
     * @param   nTuples  the int array that contains the number of tuple for each table
     * @return  Comparable [i][j][k] 3D array, where 'i' is the table number,
     *          'j' is the tuple number and 'k' is the attribute number
     */
    Comparable [][][] generate (int [] nTuples);

} // TupleGenerator

