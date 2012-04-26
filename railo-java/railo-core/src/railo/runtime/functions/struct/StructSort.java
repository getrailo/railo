/**
 * Implements the Cold Fusion Function structsort
 */
package railo.runtime.functions.struct;

import java.util.Arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.interpreter.VariableInterpreter;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.comparator.ExceptionComparator;
import railo.runtime.type.comparator.NumberSortRegisterComparator;
import railo.runtime.type.comparator.SortRegister;
import railo.runtime.type.comparator.SortRegisterComparator;

public final class StructSort implements Function {
	public static Array call(PageContext pc , Struct base) throws PageException {
		return call(pc,base,"text","asc",null);
	}
	public static Array call(PageContext pc , Struct base, String sortType) throws PageException {
		return call(pc,base,sortType,"asc",null);
	}
	public static Array call(PageContext pc , Struct base, String sortType, String sortOrder) throws PageException {
		return call(pc,base,sortType,sortOrder,null);
	}
	public static Array call(PageContext pc , Struct base, String sortType, String sortOrder, String pathToSubElement) throws PageException {

		boolean isAsc=true;
		PageException ee=null;
		if(sortOrder.equalsIgnoreCase("asc"))isAsc=true;
		else if(sortOrder.equalsIgnoreCase("desc"))isAsc=false;
		else throw new ExpressionException("invalid sort order type ["+sortOrder+"], sort order types are [asc and desc]");
		
		Collection.Key[] keys = base.keys();
		SortRegister[] arr=new SortRegister[keys.length];
		boolean hasSubDef=pathToSubElement!=null;
		
		for(int i=0;i<keys.length;i++) {
		    Object value=base.get(keys[i],null);
		    
		    if(hasSubDef) {
		        value=VariableInterpreter.getVariable(pc,Caster.toCollection(value),pathToSubElement);
		    }		    
		    arr[i]=new SortRegister(i,value);
		}
		
		ExceptionComparator comp=null;
		// text
		if(sortType.equalsIgnoreCase("text")) comp=new SortRegisterComparator(isAsc,false);
		
		// text no case
		else if(sortType.equalsIgnoreCase("textnocase")) comp=new SortRegisterComparator(isAsc,true);			
		
		// numeric
		else if(sortType.equalsIgnoreCase("numeric")) comp=new NumberSortRegisterComparator(isAsc);
			
		else {
			throw new ExpressionException("invalid sort type ["+sortType+"], sort types are [text, textNoCase, numeric]");
		}
		
		Arrays.sort(arr,0,arr.length,comp);
		ee=comp.getPageException();
		
		if(ee!=null) {
			throw ee;
		}
		
		Array rtn=new ArrayImpl();
		for(int i=0;i<arr.length;i++) {
		    rtn.append(keys[arr[i].getOldPosition()].getString());
		}
		return rtn;
			
	}
	
	
	
	
}