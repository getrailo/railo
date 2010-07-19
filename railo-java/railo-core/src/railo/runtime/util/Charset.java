package railo.runtime.util;


/**
 * charset Util Class
 */
public final class Charset {
    
    /**
     * @return returns default charset
     */
    public static String getDefault()	{
        return System.getProperty("file.encoding");
    }
}