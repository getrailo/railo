package railo.commons.collection;

import java.util.Map;

public class MapFactory {
	public static <K,V> MapPro<K,V> getConcurrentMap(){
		//return new HashMapPro();
		return new ConcurrentHashMapPro<K,V>();
	}
	
	public static <K,V> MapPro<K,V> getConcurrentMap(int initialCapacity){
		//return new HashMapPro();
		return new ConcurrentHashMapPro<K,V>(initialCapacity);
	}
	
	
	
	public static <K,V> MapPro<K,V> getConcurrentMap(Map<K,V> map){
		//return new HashMapPro(map);
		return new ConcurrentHashMapPro<K,V>(map);
	}
}
