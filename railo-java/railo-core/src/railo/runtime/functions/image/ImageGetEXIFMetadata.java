package railo.runtime.functions.image;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class ImageGetEXIFMetadata {

	public static Struct call(PageContext pc, Object name) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		return getData(img);
	}

	public static Struct getData(Image img) throws PageException {
		Struct sct = img.info(),data=new StructImpl();
		Iterator it = sct.entrySet().iterator();
		Map.Entry entry;
		while(it.hasNext()){
			entry=(Entry) it.next();
			if(entry.getValue() instanceof Map) 
				fill(data,(Map)entry.getValue());
			else if(entry.getValue() instanceof List) 
				fill(data,entry.getKey(),(List)entry.getValue());
			else
				data.put(entry.getKey(),entry.getValue());
		}
		
		return data;
	}

	private static void fill(Struct data, Map map) throws PageException {
		Iterator it = map.entrySet().iterator();
		Map.Entry entry;
		while(it.hasNext()){
			entry=(Entry) it.next();
			if(entry.getValue() instanceof Map) 
				fill(data,(Map)entry.getValue());
			else if(entry.getValue() instanceof List) 
				fill(data,entry.getKey(),(List)entry.getValue());
			else
				data.put(entry.getKey(),entry.getValue());
		}
	}

	private static void fill(Struct data, Object key, List list) throws PageException {
		data.put(
				key,
				railo.runtime.type.util.ListUtil.listToList(list, ","));
	}
}
