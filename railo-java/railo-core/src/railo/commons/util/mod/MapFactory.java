package railo.commons.util.mod;

public class MapFactory {
	public static  MapPro getConcurrentMap(){
		return new HashMapPro();
		//return new ConcurrentHashMapPro();
	}
}
