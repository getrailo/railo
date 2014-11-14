/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
/**
 * Implements the CFML Function getmetadata
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
import railo.runtime.type.KeyImpl;
import railo.runtime.type.ObjectWrap;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;

public final class GetMetaData implements Function {

	private static final long serialVersionUID = -3787469574373656167L;

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
				return getMetaData((Component)object,pc);
				//return ((Component)object).getMetaData(pc);
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
			if(object==null) throw new FunctionException(pc,"GetMetaData",1,"object","value is null");
			return object.getClass();
		}
		
		String str = Caster.toString(object,null);
		if(str==null)throw new FunctionException(pc,"GetMetaData",1,"object","must be a string when second argument is true");
        return pc.undefinedScope().getScope(KeyImpl.init(str));
		
	}

	public static Struct getMetaData(Component cfc, PageContext pc) throws PageException {
		return cfc.getMetaData(pc);
	}
}