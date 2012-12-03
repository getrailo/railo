/**
 * Implements the CFML Function repeatstring
 */
package railo.runtime.functions.string;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class RepeatString implements Function {
    public static String call(PageContext pc , String str, double count) throws ExpressionException {
        if(count<0) throw new ExpressionException("Parameter 2 of function repeatString which is now ["+Caster.toString(count)+"] must be a non-negative integer");
        return StringUtil.repeatString(str,(int)count);
    }
    public static String _call(PageContext pc , String str, double count) throws ExpressionException {
        int len=(int) count;
        if(len<0) throw new ExpressionException("Parameter 2 of function repeatString which is now ["+len+"] must be a non-negative integer");
        char[] chars=str.toCharArray();
        StringBuffer cb=new StringBuffer(chars.length*len);
        for(int i=0;i<len;i++)cb.append(chars);
        return cb.toString();
    }
    public static StringBuffer call(StringBuffer sb,String str, double count) throws ExpressionException {
        int len=(int) count;
        if(len<0) throw new ExpressionException("Parameter 1 of function repeatString which is now ["+len+"] must be a non-negative integer");
        
        for(int i=0;i<len;i++)sb.append(str);
        return sb;
    }
    public static StringBuffer call(StringBuffer sb,char c, double count) throws ExpressionException {
        int len=(int) count;
        if(len<0) throw new ExpressionException("Parameter 1 of function repeatString which is now ["+len+"] must be a non-negative integer");
        
        for(int i=0;i<len;i++)sb.append(c);
        return sb;
    }
}