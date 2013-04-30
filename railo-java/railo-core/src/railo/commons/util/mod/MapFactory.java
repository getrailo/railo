package railo.commons.util.mod;

import railo.print;

public class MapFactory {
	public static  MapPro getConcurrentMap(){
		//return new HashMapPro();
		return new ConcurrentHashMapPro();
	}
}
