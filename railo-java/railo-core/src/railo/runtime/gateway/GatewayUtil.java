package railo.runtime.gateway;

import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import railo.commons.lang.SystemOut;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class GatewayUtil {

	
	public static String toRequestURI(String cfcPath) {
		String requestURI = cfcPath.replace('.','/');
		if(!requestURI.startsWith("/"))requestURI="/"+requestURI+".cfc";
		return requestURI;
	}

	public static Object toCFML(Object obj) {
		if(obj instanceof Map) return toCFML((Map)obj);
		if(obj instanceof List) return toCFML((List)obj);
		try{
			if(obj instanceof Message)return toCFML((Message)obj);
		}
		// catch when jms.jar does not exist
		catch(Throwable t){
			SystemOut.printDate(t.getMessage());
		}
		return obj;
	}
	


	public static Object toCFML(Message msg) throws JMSException {
		// Byte
		if(msg instanceof BytesMessage){
			BytesMessage bm = (BytesMessage) msg;
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			byte[] buffer = new byte[0xffff];
	        int len;
	        while((len = bm.readBytes(buffer)) !=-1) {
	          out.write(buffer, 0, len);
	        }
			return out.toByteArray();
		}
		// Stream
		if(msg instanceof StreamMessage){
			StreamMessage sm = (StreamMessage) msg;
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			byte[] buffer = new byte[0xffff];
	        int len;
	        while((len = sm.readBytes(buffer)) !=-1) {
	          out.write(buffer, 0, len);
	        }
			return out.toByteArray();
		}
		// Text
		if(msg instanceof TextMessage)
			return ((TextMessage)msg).getText();
		// Object
		if(msg instanceof ObjectMessage)
			return ((ObjectMessage)msg).getObject();
		// Map
		if(msg instanceof MapMessage){
			MapMessage mm = (MapMessage)msg;
			Struct sct=new StructImpl();
			Enumeration names = mm.getMapNames();
			String key;
			while(names.hasMoreElements()){
				key=Caster.toString(names.nextElement(),null);
				sct.setEL(key, mm.getObject(key));
			}
			return sct;
		}
		return msg;
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

}
