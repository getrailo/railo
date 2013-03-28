package railo.runtime.gateway;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

public class GatewayUtil {

	
	public static String toRequestURI(String cfcPath) {
		String requestURI = cfcPath.replace('.','/');
		if(!requestURI.startsWith("/"))requestURI="/"+requestURI+".cfc";
		return requestURI;
	}

	public static Object toCFML(Object obj) {
		if(obj instanceof Map) return toCFML((Map)obj);
		if(obj instanceof List) return toCFML((List)obj);
		return obj;
	}
	
	public static Map toCFML(Map map) {
		Iterator it = map.entrySet().iterator();
		Map.Entry entry;
		while(it.hasNext()){
			entry=(Entry) it.next();
			entry.setValue(toCFML(entry.getValue()));
		}
		return map;
	}

	public static Object toCFML(List list) {
		ListIterator it = list.listIterator();
		int index;
		while(it.hasNext()){
			index=it.nextIndex();
			list.set(index, toCFML(it.next()));
			
		}
		return list;
	}
	
	public static int getState(GatewayEntry ge){ // this method only exists to make sure the Gateway interface must not be used outsite the gateway package
		return ge.getGateway().getState();
	}

}
