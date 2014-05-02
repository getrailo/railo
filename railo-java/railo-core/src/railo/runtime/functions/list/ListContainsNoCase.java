/**
 * Implements the CFML Function listcontainsnocase
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ListUtil;

public final class ListContainsNoCase extends BIF {
	
	private static final long serialVersionUID = 4955787566835292639L;
	
	public static double call(PageContext pc , String list, String value) {
		return call(pc, list, value, ",", false, false);
	}
	public static double call(PageContext pc , String list, String value, String delimter) {
		return call(pc, list, value, delimter, false, false);
	}
	public static double call(PageContext pc , String list, String value, String delimter, boolean includeEmptyFields) {
		return call(pc, list, value, delimter, includeEmptyFields,false);
	}
	public static double call(PageContext pc , String list, String value, String delimter, boolean includeEmptyFields, boolean multiCharacterDelimiter) {
		return ListUtil.listContainsNoCase(list,value,delimter,includeEmptyFields,multiCharacterDelimiter)+1;
	}

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toString(args[2]));
    	if(args.length==4)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toString(args[2]), Caster.toBooleanValue(args[3]));
    	if(args.length==5)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toString(args[2]), Caster.toBooleanValue(args[3]), Caster.toBooleanValue(args[4]));
    	
		throw new FunctionException(pc, "ListContainsNoCase", 2, 5, args.length);
	}
}