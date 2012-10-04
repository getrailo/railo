/**
 * Implements the CFML Function isdate
 */
package railo.runtime.functions.decision;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.java.JavaObject;
import railo.runtime.reflection.Reflector;
import railo.runtime.type.ObjectWrap;

public final class IsInstanceOf implements Function {
	public static boolean call(PageContext pc , Object obj,String typeName) throws PageException {
		if(obj instanceof Component)
			return ((Component)obj).instanceOf(typeName);
		if(obj instanceof JavaObject)
			return Reflector.isInstaneOf(((JavaObject)obj).getClazz(), typeName);
		if(obj instanceof ObjectWrap)
			return call(pc, ((ObjectWrap)obj).getEmbededObject(), typeName);
		
		
		return Reflector.isInstaneOf(obj.getClass(), typeName);
		
	}
}