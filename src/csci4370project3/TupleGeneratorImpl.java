package CSCI4370Project4;

/*******************************************************************************
 * @file  TupleGeneratorImpl
 *
 * @author   Sadiq Charaniya, John Miller
 * Modified by: Jeffrey Swindle
 */

import static java.lang.System.out;
import java.util.*;

/*******************************************************************************
 * This class is used to populate a database (collection of tables) with randomly
 * generated values that satisfy the following integrity constraints:  domain,
 * primary keys and foreign key constraints.
 */
public class TupleGeneratorImpl
       implements TupleGenerator
{
    /** Counter for table numbers
     */
    private int counter = 0;

    /** Initializations 
     */
    private HashMap <String, Comparable [][]> result = new HashMap <> ();

    private HashMap <Integer, String> tableIndex = new HashMap <> ();

    private HashMap <String, String []> tableAttr = new HashMap <> ();

    private HashMap <String, String []> tableDomain = new HashMap <> ();

    private HashMap <String, String []> tablepks = new HashMap <> ();
    
    /***************************************************************************
     * Adding relation to Schema.
     * @param name        the name of the table
     * @param attribute   the array of attributes
     * @param domain      the array of domains
     * @param primaryKey  the array of primary keys
     */
    public void addRelSchema (String name, String [] attribute, String [] domain,
                              String [] primaryKey)
    {
        tableIndex.put (counter, name);
        tableAttr.put (name, attribute);
        tableDomain.put (name, domain);
        tablepks.put (name, primaryKey);
        counter++;
    } // addRelSchema
    
    /***************************************************************************
     * Adding relation to Schema.  Convenience method.
     * @param name        the name of the table
     * @param attribute   the string embedding the table's attributes
     * @param domain      the string embedding the table's domains
     * @param primaryKey  the string embedding the table's primary keys
     */
    public void addRelSchema (String name, String attribute, String domain,
                              String primaryKey)
    {
        addRelSchema (name, attribute.split (" "), domain.split (" "),
                      primaryKey.split (" "));
    } // addRelSchema

    /***************************************************************************
     * Generates random tuples that satisfy all the integrity constraints.
     * @param tuples  the number of tuples for each table
     * @return  tempResult contains tuples for all the tables in the order they were added
     */
    public Comparable [][][]  generate (int [] tuples)
    {
        Random        rand      = new Random ();
        String        tableName = "";
        String []     attribute;
        String []     domain;
        String []     pks;
        Set <String>  pKeys      = new HashSet <String> ();
        Set <Comparable <?>>  pKeyValues = new HashSet <Comparable <?>> ();
        int           iVal;
        String        sVal;
        double        dVal;

        for (int i = 0; i < tuples.length; i++) {
            tableName = tableIndex.get (i);
            attribute = tableAttr.get (tableName);
            domain    = tableDomain.get (tableName);
            pks       = tablepks.get (tableName);
            Comparable [][] subResult = new Comparable [tuples [i]][attribute.length];

            for (int n = 0; n < pks.length; n++) pKeys.add (pks [n]);

                for (int j = 0; j < tuples[i]; j++) {
                    for (int k = 0; k < attribute.length; k++) {
                        if (pKeys.contains (attribute[k])) {  // key requires uniqueness
                            switch (domain[k]) {
                            case "Integer":
                                for (iVal = rand.nextInt (1000000); pKeyValues.contains (iVal);
                                    iVal = rand.nextInt (1000000));
                                subResult[j][k] = iVal;
                                pKeyValues.add (iVal);
                                break;
                            case "String":
                                for (sVal = attribute[k] + rand.nextInt (1000000); pKeyValues.contains(sVal);
                                    sVal = attribute[k] + rand.nextInt (1000000));
                                subResult[j][k] = sVal;
                                pKeyValues.add (sVal);
                                break;
                            case "Double":
                                for (dVal = rand.nextInt (1000000) * rand.nextDouble (); pKeyValues.contains( dVal);
                                    dVal = rand.nextInt (1000000) * rand.nextDouble ());
                                subResult[j][k] = dVal;
                                pKeyValues.add (dVal);
                                break;
                            default:
                                throw new IllegalArgumentException("Invalid type in switch: " + domain[k]);
                            } // switch

                        } else {  // non-key does not require uniqueness

                            switch (domain[k]) {
                            case "Integer":
                                subResult[j][k] = rand.nextInt(1000000); break;
                            case "String":
                                subResult[j][k] = attribute [k] + rand.nextInt (1000000); break;
                            case "Double":
                                subResult[j][k] = rand.nextInt (100000) * rand.nextDouble (); break;
                            default:
                                throw new IllegalArgumentException("Invalid type in switch: " + domain[k]);
                            } // switch

                        } // if
                    } // for
                } // for
                
            pKeys.clear ();
            result.put (tableName, subResult);
        } // for

        Comparable[][][] tempResult = new Comparable [result.size()][][];

        for (int i = 0; i < result.size (); i++) {
            tableName = tableIndex.get (i);
            Comparable [][] subTable = result.get (tableName);
            tempResult [i] = subTable;
        } // for

        return tempResult;
    } // generate

} // TestGeneratorImpl class

