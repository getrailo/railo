package railo.runtime.op;

import java.util.HashMap;
import java.util.Map;


public class ThreadLocalDuplication {

	private static ThreadLocal<Map<Object,Object>> local=new ThreadLocal<Map<Object,Object>>();


	public static Map<Object, Object> getMap() {
		return touch();
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


