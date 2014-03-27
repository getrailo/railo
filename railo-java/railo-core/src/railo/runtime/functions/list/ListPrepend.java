/**
 * Implements the CFML Function listprepend
 */
package railo.runtime.functions.list;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class ListPrepend extends BIF {

	private static final long serialVersionUID = -4252541560957800011L;
	
	public static String call(PageContext pc , String list, String value) {
		if(list.length()==0) return value;
		return new StringBuilder(value).append(',').append(list).toString();
	}
	public static String call(PageContext pc , String list, String value, String delimiter) {
		if(list.length()==0) return value;
		if(StringUtil.isEmpty(delimiter)) {
		    return call(pc,list,value);
        }
        return new StringBuilder(value).append(delimiter.charAt(0)).append(list).toString();
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
		if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toString(args[2]));

		throw new FunctionException(pc, "ListPrepend", 1, 3, args.length);
	}
}