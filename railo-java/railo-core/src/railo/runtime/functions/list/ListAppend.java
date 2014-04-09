/**
 * Implements the CFML Function listappend
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class ListAppend extends BIF {

	private static final long serialVersionUID = -4893447489733907241L;

	public static String call(PageContext pc , String list, String value) {
		if(list.length()==0) return value;
		return new StringBuilder(list).append(',').append(value).toString();
	}
	public static String call(PageContext pc , String list, String value, String delimiter) {
		if(list.length()==0) return value;
        switch(delimiter.length()) {
        case 0:return list;
        case 1:return new StringBuilder(list).append(delimiter).append(value).toString();
        }
        return new StringBuilder(list).append(delimiter.charAt(0)).append(value).toString();
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toString(args[2]));

		throw new FunctionException(pc, "ListAppend", 2, 3, args.length);
	}
	
}