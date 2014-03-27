/**
 * Implements the CFML Function listrest
 */
package railo.runtime.functions.list;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.util.ListUtil;

public final class ListRemoveDuplicates extends BIF {
	
	private static final long serialVersionUID = -6596215135126751629L;
	
	public static String call(PageContext pc , String list) throws PageException {

		return call(pc, list, ",", false);
	}

	public static String call(PageContext pc, String list, String delimiter) throws PageException {

		return call(pc, list, delimiter, false);
	}

	public static String call(PageContext pc , String list, String delimiter, boolean ignoreCase) throws PageException {
		if(delimiter==null) delimiter=",";
		Array array = ListUtil.listToArrayRemoveEmpty(list, delimiter);

		Set<String> existing;
		if (ignoreCase)
			existing = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		else
			existing = new HashSet<String>();

		StringBuilder sb=new StringBuilder();
		//Key[] keys = array.keys();
		Iterator<Object> it = array.valueIterator();
		String value;

		while(it.hasNext()){

			value=Caster.toString(it.next());

			if(!existing.contains(value)) {

				sb.append(value);
				existing.add(value);

				if(it.hasNext())
					sb.append(delimiter);
			}
		}

		return sb.toString();
	}
	
    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==1)
			return call(pc, Caster.toString(args[0]));
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toBooleanValue(args[2]));
    	
		throw new FunctionException(pc, "ListRemoveDuplicates", 2, 5, args.length);
	}
}