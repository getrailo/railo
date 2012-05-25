/**
 * Implements the ColdFusion Function arrayToStruct
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public final class ArrayToStruct implements Function {
	public static Struct call(PageContext pc , Array arr) throws PageException {
		Struct sct=new StructImpl();
		int[] keys=arr.intKeys();
		for(int i=0;i<keys.length;i++) {
			int key=keys[i];
			sct.set(key+"",arr.getE(key));
		}
		
		return sct;
	}
}