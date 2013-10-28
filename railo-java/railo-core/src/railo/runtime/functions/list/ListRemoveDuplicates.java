/**
 * Implements the CFML Function listrest
 */
package railo.runtime.functions.list;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.util.ListUtil;

public final class ListRemoveDuplicates implements Function {
	
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
}