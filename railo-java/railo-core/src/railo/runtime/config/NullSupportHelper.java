package railo.runtime.config;

import railo.runtime.type.Null;

public class NullSupportHelper {
	private static final Null NULL=Null.NULL;
	
	protected static boolean fullNullSupport=false;
	
	public static boolean full() {
		return fullNullSupport;
	}
	
	public static Object NULL() {
		return fullNullSupport?NULL:null;
	}
	
	public static Object empty() {
		return fullNullSupport?null:"";
	}
}
