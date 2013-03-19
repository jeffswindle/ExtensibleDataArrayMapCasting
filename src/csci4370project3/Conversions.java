package csci4370project4;


/*******************************************************************************
 * @file  Conversions.java
 *
 * @author   John Miller
 *
 * @see http://snippets.dzone.com/posts/show/93
 */

/*******************************************************************************
 * This class provides methods for converting Java's primitive data types into
 * byte arrays.
 */
public class Conversions
{
    /***************************************************************************
     * Convert short into a byte array.
     * @param value  the short value to convert
     * @return  a corresponding byte array
     */
    public static byte [] short2ByteArray (short value)
    {
        return new byte [] { (byte) (value >>> 8),
                             (byte) value };
    } // short2ByteArray

    /***************************************************************************
     * Convert int into a byte array.
     * @param value  the int value to convert
     * @return  a corresponding byte array
     */
    public static byte [] int2ByteArray (int value)
    {
        return new byte [] { (byte) (value >>> 24),
                             (byte) (value >>> 16),
                             (byte) (value >>> 8),
                             (byte) value };
    } // int2ByteArray

    /***************************************************************************
     * Convert long into a byte array.
     * @param value  the long value to convert
     * @return  a corresponding byte array
     */
    public static byte [] long2ByteArray (long value)
    {
        return new byte [] { (byte) (value >>> 56),
                             (byte) (value >>> 48),
                             (byte) (value >>> 40),
                             (byte) (value >>> 32),
                             (byte) (value >>> 24),
                             (byte) (value >>> 16),
                             (byte) (value >>> 8),
                             (byte) value };
    } // long2ByteArray

    /***************************************************************************
     * Convert float into a byte array.
     * @param value  the float value to convert
     * @return  a corresponding byte array
     * @author Jeffrey Swindle
     */
    public static byte [] float2ByteArray (float value)
    {

        return new byte [] { (byte) (Float.floatToRawIntBits(value) >>> 24),
                             (byte) (Float.floatToRawIntBits(value) >>> 16),
                             (byte) (Float.floatToRawIntBits(value) >>> 8),
                             (byte) Float.floatToRawIntBits(value) };
    } // float2ByteArray

    /***************************************************************************
     * Convert double into a byte array.
     * @param value  the double value to convert
     * @return  a corresponding byte array
     * @author Jeffrey Swindle
     */
    public static byte [] double2ByteArray (double value)
    {
        return new byte [] { (byte) (Double.doubleToRawLongBits(value) >>> 56),
                             (byte) (Double.doubleToRawLongBits(value) >>> 48),
                             (byte) (Double.doubleToRawLongBits(value) >>> 40),
                             (byte) (Double.doubleToRawLongBits(value) >>> 32),
                             (byte) (Double.doubleToRawLongBits(value) >>> 24),
                             (byte) (Double.doubleToRawLongBits(value) >>> 16),
                             (byte) (Double.doubleToRawLongBits(value) >>> 8),
                             (byte) Double.doubleToRawLongBits(value) };
    } // double2ByteArray

} // Conversions

