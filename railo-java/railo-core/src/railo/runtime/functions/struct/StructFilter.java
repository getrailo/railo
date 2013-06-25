/**
 * Implements the CFML Function arrayavg
 */
package railo.runtime.functions.struct;

import java.util.Iterator;
import java.util.Map.Entry;

import railo.commons.lang.CFTypes;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;


public final class StructFilter extends BIF {

	private static final long serialVersionUID = -91410716194244194L;

	public static Struct call(PageContext pc , Struct sct, UDF filter) throws PageException {
		

		// check UDF return type
		int type = filter.getReturnType();
		if(type!=CFTypes.TYPE_BOOLEAN && type!=CFTypes.TYPE_ANY)
			throw new ExpressionException("invalid return type ["+filter.getReturnTypeAsString()+"] for UDF Filter, valid return types are [boolean,any]");
		
		// check UDF arguments
		FunctionArgument[] args = filter.getFunctionArguments();
		if(args.length>2)
			throw new ExpressionException("UDF filter has to many arguments ["+args.length+"], should have at maximum 2 arguments");
		
		
		Struct rtn=new StructImpl();
		//Key[] keys = sct.keys();
		Iterator<Entry<Key, Object>> it = sct.entryIterator();
		Object value;
		while(it.hasNext()){
			Entry<Key, Object> e = it.next();
			value=e.getValue();
			if(Caster.toBooleanValue(filter.call(pc, new Object[]{e.getKey().getString(),value}, true)))
				rtn.set(e.getKey(), value);
		}
		return rtn;
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toStruct(args[0]),Caster.toFunction(args[1]));
	}
}