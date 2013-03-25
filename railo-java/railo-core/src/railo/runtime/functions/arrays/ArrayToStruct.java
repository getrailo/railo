/**
 * Implements the CFML Function arrayToStruct
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public final class ArrayToStruct extends BIF {

	private static final long serialVersionUID = 2050803318757965798L;

	public static Struct call(PageContext pc , Array arr) throws PageException {
		Struct sct=new StructImpl();
		int[] keys=arr.intKeys();
		for(int i=0;i<keys.length;i++) {
			int key=keys[i];
			sct.set(KeyImpl.toKey(key+""),arr.getE(key));
		}
		
		return sct;
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]));
	}
}