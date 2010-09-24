package railo.runtime.dump;

import java.util.HashMap;
import java.util.Map;


public class ThreadLocalDump {

	private static ThreadLocal<Map<Object,String>> local=new ThreadLocal<Map<Object,String>>();

	public static void set(Object o, String c) {
		
		touch().put(o,c);
	}

	public static Map<Object, String> getMap() {
		return touch();
	}
	
	public static void remove(Object o) {
		touch().remove(o);
	}
	
	public static String get(Object obj) {
		Map<Object,String> list = touch();
		return list.get(obj);
	}

	private static Map<Object,String> touch() {
		Map<Object,String> set = local.get();
		if(set==null) {
			set = new HashMap<Object,String>();
			local.set(set);
		}
		return set;
	}

}


