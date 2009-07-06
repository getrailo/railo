package railo.commons.lang;

import java.io.PrintWriter;
import java.util.Date;

public final class SystemOut {

    /**
     * logs a value 
     * @param value
     */
    public static void printDate(PrintWriter pw,String value) {
    	long millis=System.currentTimeMillis();
    	pw.write(new Date(millis)+"-"+(millis-(millis/1000*1000))+" "+value+"\n");
    	pw.flush();
    }
    /**
     * logs a value 
     * @param value
     */
    public static void print(PrintWriter pw,String value) {
    	pw.write(value+"\n");
    	pw.flush();
    }
    /**
     * logs a value 
     * @param value
     */
    public static void printDate(String value) {
    	
    	
       printDate(new PrintWriter(System.out),value);
    }
    /**
     * logs a value 
     * @param value
     */
    public static void print(String value) {
        print(new PrintWriter(System.out),value);
    }

}
