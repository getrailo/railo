/**
 * Implements the CFML Function asc
 */
package railo.runtime.functions.string;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class UCFirst extends BIF {

	private static final long serialVersionUID = 6476775359884698477L;

	public static String call(PageContext pc , String string) {
        return call(pc, string,false);
    }

    public static String call( PageContext pc, String string, boolean doAll ) {
        if ( !doAll ) return StringUtil.ucFirst(string);
        return StringUtil.capitalize(string, null);
    }

    public static String call( PageContext pc, String string, boolean doAll, boolean doLowerIfAllUppercase ) {
	     if (doLowerIfAllUppercase && StringUtil.isAllUpperCase(string))
		    string = string.toLowerCase();

        if (!doAll) return StringUtil.ucFirst(string);

        return StringUtil.capitalize( string, null );
    }

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==1)
			return call(pc, Caster.toString(args[0]));
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toBooleanValue(args[1]));
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toBooleanValue(args[1]), Caster.toBooleanValue(args[2]));

		throw new FunctionException(pc, "UCFirst", 1, 3, args.length);
	}
}