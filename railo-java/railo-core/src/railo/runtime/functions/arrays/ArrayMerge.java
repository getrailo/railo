/**
 * Implements the CFML Function arrayMerge
 * Merge 2 arrays
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;


public final class ArrayMerge extends BIF {

	private static final long serialVersionUID = -391473381762154998L;

	public static Array call(PageContext pc , Array arr1, Array arr2) throws PageException {
		return call(pc,arr1,arr2,false);
	}
	public static Array call(PageContext pc , Array arr1, Array arr2, boolean leaveIndex) throws PageException {
		if(leaveIndex) {
			Array arr = new ArrayImpl();
			set(arr,arr2);
			set(arr,arr1);
			return arr;			
		}
		
			Array arr = new ArrayImpl();
			append(arr,arr1);
			append(arr,arr2);
			return arr;
		
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)return call(pc,Caster.toArray(args[0]),Caster.toArray(args[1]));
		return call(pc,Caster.toArray(args[0]),Caster.toArray(args[1]), Caster.toBooleanValue(args[2]));
	}
	
	

	public static void set(Array target,Array source) throws PageException {
		int[] srcKeys=source.intKeys();
		for(int i=0;i<srcKeys.length;i++) {
			target.setE(srcKeys[i],source.getE(srcKeys[i]));
		}
	}
	
	public static void append(Array target,Array source) throws PageException {
		int[] srcKeys=source.intKeys();
		for(int i=0;i<srcKeys.length;i++) {
			target.append(source.getE(srcKeys[i]));
		}
	}
	
}