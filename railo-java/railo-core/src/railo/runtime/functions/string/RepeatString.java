/**
 * Implements the CFML Function repeatstring
 */
package railo.runtime.functions.string;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class RepeatString extends BIF {

	private static final long serialVersionUID = 6041471441971348584L;

	public static String call(PageContext pc , String str, double count) throws ExpressionException {
        if(count<0) throw new ExpressionException("Parameter 2 of function repeatString which is now ["+Caster.toString(count)+"] must be a non-negative integer");
        return StringUtil.repeatString(str,(int)count);
    }
    public static String _call(PageContext pc , String str, double count) throws ExpressionException {
        int len=(int) count;
        if(len<0) throw new ExpressionException("Parameter 2 of function repeatString which is now ["+len+"] must be a non-negative integer");
        char[] chars=str.toCharArray();
        StringBuilder cb=new StringBuilder(chars.length*len);
        for(int i=0;i<len;i++)cb.append(chars);
        return cb.toString();
    }
    public static StringBuilder call(StringBuilder sb,String str, double count) throws ExpressionException {
        int len=(int) count;
        if(len<0) throw new ExpressionException("Parameter 1 of function repeatString which is now ["+len+"] must be a non-negative integer");
        
        for(int i=0;i<len;i++)sb.append(str);
        return sb;
    }
    public static StringBuilder call(StringBuilder sb,char c, double count) throws ExpressionException {
        int len=(int) count;
        if(len<0) throw new ExpressionException("Parameter 1 of function repeatString which is now ["+len+"] must be a non-negative integer");
        
        for(int i=0;i<len;i++)sb.append(c);
        return sb;
    }

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]));
    	
		throw new FunctionException(pc, "RepeatString", 2, 2, args.length);
	}
}