/**
 * Implements the Cold Fusion Function getmetadata
 */
package railo.runtime.functions.other;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.img.Image;
import railo.runtime.java.JavaObject;
import railo.runtime.op.Caster;
import railo.runtime.type.ObjectWrap;
import railo.runtime.type.Query;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.scope.UndefinedImpl;

public final class GetMetaData implements Function {
	// TODO support enties more deeply
	public static Object call(PageContext pc ) throws PageException {
        Component ac = pc.getActiveComponent();
        if(ac!=null) {
	        return call(pc , ac);
	    }
	    
		return new StructImpl();
	}
	
	public static Object call(PageContext pc , Object object) throws PageException {
		return call(pc, object, false);
	}
	public static Object call(PageContext pc , Object object,boolean source) throws PageException {
		if(object instanceof JavaObject){
			return call(pc,((JavaObject)object).getClazz(),source);
		}
		else if(object instanceof ObjectWrap){
			return call(pc,((ObjectWrap)object).getEmbededObject(),source);
		}
		
		if(!source){
			// Component
			if(object instanceof Component) {
				return ((Component)object).getMetaData(pc);
			}
			// UDF
			if(object instanceof UDF) {
				return ((UDF)object).getMetaData(pc);
			}
			// Query
	        else if(object instanceof Query) {
	            return ((Query)object).getMetaDataSimple();
	        }
			// Image
	        else if(object instanceof Image) {
	            return ((Image)object).info();
	        }
			
			return object.getClass();
		}
		String str = Caster.toString(object,null);
		if(str==null)throw new FunctionException(pc,"GetMetaData",1,"object","must be a string when second argument is true");
        return ((UndefinedImpl)pc.undefinedScope()).getScope(str);
		
	}
}