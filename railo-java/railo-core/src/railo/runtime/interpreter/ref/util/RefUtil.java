

package railo.runtime.interpreter.ref.util;

import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;

public final class RefUtil {

	/**
	 * transalte a Ref array to a Object array
	 * @param refs
	 * @return objects
	 * @throws PageException 
	 */
	public static Object[] getValue(Ref[] refs) throws PageException {
		Object[] objs=new Object[refs.length];
		for(int i=0;i<refs.length;i++) {
			objs[i]=refs[i].getValue();
		}
		return objs;
	}

	public static boolean eeq(Ref left,Ref right) throws PageException {
		// TODO Auto-generated method stub
		return left.getValue()==right.getValue();
	}

	
}
