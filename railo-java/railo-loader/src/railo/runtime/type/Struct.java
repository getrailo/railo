package railo.runtime.type;

import java.util.Map;

/**
 * 
 */
public interface Struct extends Collection,Map,Objects {

	// FUTURE public static final int TYPE_UNDEFINED=-1;
	public static final int TYPE_WEAKED=0;
	public static final int TYPE_LINKED=1;
	public static final int TYPE_SYNC=2;
	public static final int TYPE_REGULAR=3;
	public static final int TYPE_SOFT=4;
	
}