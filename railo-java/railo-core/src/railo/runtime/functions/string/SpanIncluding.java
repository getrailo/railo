/**
 * Implements the CFML Function spanincluding
 */
package railo.runtime.functions.string;

import java.util.StringTokenizer;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class SpanIncluding extends BIF {

	private static final long serialVersionUID = 3274154577208880461L;

	public static String call(PageContext pc, String str, String set) {
        StringTokenizer stringtokenizer = new StringTokenizer(str, set);
        if(stringtokenizer.hasMoreTokens()) {
            String rtn = stringtokenizer.nextToken();
            int i = str.indexOf(rtn);
            if(i == 0)
                return "";
            return str.substring(0, i);
        } 
        return str;
        
    }

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	
		throw new FunctionException(pc, "SpanIncluding", 2, 2, args.length);
	}
	
}