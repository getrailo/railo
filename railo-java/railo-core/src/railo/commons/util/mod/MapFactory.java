package railo.commons.util.mod;

import railo.print;

public class MapFactory {
	public static  MapPro getConcurrentMap(){
		//return new HashMapPro();
		return new ConcurrentHashMapPro();
	}
	
	public static void main(String[] args) {
		test(new HashMapPro());
		test(new ConcurrentHashMapPro());
		test(new SyncMap());
		print.e("++++");
		test(new HashMapPro());
		test(new ConcurrentHashMapPro());
		test(new SyncMap());
		print.e("++++");
		test(new HashMapPro());
		test(new ConcurrentHashMapPro());
		test(new SyncMap());
		print.e("++++");
		test(new HashMapPro());
		test(new ConcurrentHashMapPro());
		test(new SyncMap());
	}

	private static void test(MapPro map) {
		long start =micro();
		for(int i=0;i<10000000;i++){
			map.put("x", "X");
			map.get("x");
			map.get("z");
			map.containsKey("a");
		}
		print.e((micro()-start)+" -> "+map.getClass().getName());
		
	}

	private static long micro() {
		return System.nanoTime()/1000;
	}
}
