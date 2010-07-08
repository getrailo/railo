package railo.runtime.type;

import java.util.Map;

// FUTURE add inteface ,Sizeable
/**
 * 
 */
public interface Struct extends Collection,Map {

	public static final int TYPE_WEAKED=0;
	public static final int TYPE_LINKED=1;
	public static final int TYPE_SYNC=2;
	public static final int TYPE_REGULAR=3;
	//FUTURE public static final int TYPE_SOFT=4;
	
}