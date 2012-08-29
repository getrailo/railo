package railo.runtime.op;

import java.util.HashMap;
import java.util.Map;


public class ThreadLocalDuplication {

	private static ThreadLocal<Map<Object,Object>> local=new ThreadLocal<Map<Object,Object>>();

	public static void set(Object o, Object c) {
		
		touch().put(o,c);
	}

	public static Map<Object, Object> getMap() {
		return touch();
	}
	
	public static void removex(Object o) {
		touch().remove(o);
	}
	
	public static Object get(Object obj) {
		Map<Object,Object> list = touch();
		return list.get(obj);
	}

	private static Map<Object,Object> touch() {
		Map<Object,Object> set = local.get();
		if(set==null) {
			set = new HashMap<Object,Object>();
			local.set(set);
		}
		return set;
	}

}


