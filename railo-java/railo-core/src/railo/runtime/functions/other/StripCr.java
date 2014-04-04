/**
 * Implements the CFML Function stripcr
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class StripCr extends BIF {

	private static final long serialVersionUID = 1101162964675776635L;

	public static String call(PageContext pc , String string) {
		StringBuilder sb=new StringBuilder(string.length());
		int start=0;
		int pos=0;
		
		while((pos=string.indexOf('\r',start))!=-1) {
			sb.append(string.substring(start,pos));
			start=pos+1;
		}
		if(start<string.length())sb.append(string.substring(start,string.length()));
		
		return sb.toString();
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==1)
			return call(pc, Caster.toString(args[0]));
    	
		throw new FunctionException(pc, "StripCr", 1, 1, args.length);
	}	
}