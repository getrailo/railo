/**
 * Implements the Cold Fusion Function listdeleteat
 */
package railo.runtime.functions.list;

import java.util.StringTokenizer;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class ListDeleteAt implements Function {
	public static String call(PageContext pc , String list, double pos) throws ExpressionException {
		return call(pc,list,pos,",");
	}
    public static String call(PageContext pc, String list, double posNumber, String delimeter) throws ExpressionException {
        
    	int pos=(int)posNumber;
    	StringTokenizer stringtokenizer = new StringTokenizer(list, delimeter, true);
        StringTokenizer stringtokenizer1 = new StringTokenizer(list, delimeter);
        String rtn = "";
        int j = 0;
        int k = stringtokenizer1.countTokens();
        boolean flag = false;
        boolean flag1 = pos == k;
        if(pos > stringtokenizer1.countTokens() || pos < 1)
            throw new ExpressionException("invalid string list index ["+pos+"]");
        
        while(stringtokenizer.hasMoreTokens()) 
        {
            String s3 = stringtokenizer.nextToken();
            if(s3.length() != 1 || delimeter.indexOf(s3) <= -1)
            {
                if(++j != pos)
                {
                    rtn = rtn + s3;
                    flag = false;
                } else
                {
                    flag = true;
                }
            } else
            if(!flag && (!flag1 || j < pos - 1 && flag1))
                rtn = rtn + s3;
        }
        return rtn;
    }
	
	
	
}