package railo.runtime.op;

import java.util.HashMap;
import java.util.Map;

import railo.print;
import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;


public class ThreadLocalDuplication {

	private static ThreadLocal<Map<Object,Object>> local=new ThreadLocal<Map<Object,Object>>();
	private static ThreadLocal<RefBoolean> inside=new ThreadLocal<RefBoolean>();

	public static void set(Object o, Object c) {
		
		touch().put(o,c);
	}

	/*public static Map<Object, Object> getMap() {
		return touch();
	}
	
	public static void removex(Object o) {
		touch().remove(o);
	}*/
	
	private static Object get(Object obj) {
		Map<Object,Object> list = touch();
		return list.get(obj);
	}
	


	public static Object get(Object object, RefBoolean before) {print.ds("inside:"+isInside());
		if(!isInside()){
			reset();
			setIsInside(true);
			before.setValue(false);
		}
		else
			before.setValue(true);
		
		Map<Object,Object> list = touch();
		return list.get(object);
		//return get(object);
	}
	

	private static Map<Object,Object> touch() {
		Map<Object,Object> set = local.get();
		if(set==null) {
			set = new HashMap<Object,Object>();
			local.set(set);
		}
		return set;
	}
	public static void reset() {
		Map<Object,Object> set = local.get();
		if(set!=null) set.clear();
		setIsInside(false);
	}

	private static boolean isInside() {
		RefBoolean b = inside.get();
		return b!=null && b.toBooleanValue();
	}
	
	private static void setIsInside(boolean isInside) {
		RefBoolean b = inside.get();
		if(b==null)inside.set(new RefBooleanImpl(isInside));
		else b.setValue(isInside);
	}

}


