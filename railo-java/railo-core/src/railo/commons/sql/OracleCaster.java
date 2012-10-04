package railo.commons.sql;

import railo.commons.lang.ClassUtil;

public class OracleCaster {

	private static final Class OPAQUE=ClassUtil.loadClass("oracle.sql.OPAQUE", null);
	
	public static Object OPAQUE(Object o) {
		if(o==null) return null;
			
		try {
			byte[] bv = ((oracle.sql.OPAQUE)o).getBytes();
			
			//OPAQUE op = ((oracle.sql.OPAQUE)o);
			//OpaqueDescriptor desc = ((oracle.sql.OPAQUE)o).getDescriptor();
			
			
			//Method getBytesValue = o.getClass().getMethod("getBytesValue", new Class[0]);
			//byte[] bv = (byte[])getBytesValue.invoke(o, new Object[0]);
			return new String(bv,"UTF-8");
		}
		catch (Exception e) {
			//print.printST(e);
		}
		
		return o;
	}

	private static boolean equals(Class left, Class right) {
		if(left==right)return true;
		return left.equals(right.getName());
	}

}
