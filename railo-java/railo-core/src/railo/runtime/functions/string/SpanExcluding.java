/**
 * Implements the CFML Function spanexcluding
 */
package railo.runtime.functions.string;

import java.util.StringTokenizer;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class SpanExcluding implements Function {
	public static String call(PageContext pc , String str, String set) {
        StringTokenizer stringtokenizer = new StringTokenizer(str, set);
        
        if(stringtokenizer.hasMoreTokens()){
            String rtn = stringtokenizer.nextToken();
            int i = str.indexOf(rtn);
            if(i != 0)
                return "";
            return rtn;
        } 
        return "";
    }

	
	
}