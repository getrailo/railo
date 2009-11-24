package railo.runtime.cache.legacy;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.oro.text.regex.MalformedPatternException;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.WildCardFilter;
import railo.runtime.converter.JavaConverter;

public class MetaData implements Serializable {
	
	private static Map instances=new HashMap();
	
	private HashMap data=new HashMap();
	private Resource file;
	
	private MetaData(Resource file) {
		this.file=file;
		data=new HashMap();
	}
	
	public MetaData(Resource file,HashMap data) {
		this.file=file;
		this.data=data;
	}

	public static MetaData getInstance(Resource directory) {
		MetaData instance=(MetaData) instances.get(directory.getAbsolutePath());
		
		if(instance==null) {
			Resource file = directory.getRealResource("meta");
			if(file.exists()){
				try {
					instance= new MetaData(file,(HashMap)JavaConverter.deserialize(file));
				}
				catch (Throwable t) {}
			}
			if(instance==null) instance=new MetaData(file);
			instances.put(directory.getAbsolutePath(), instance);
		}
		return instance;
	}
	
	public synchronized void add(String name, String raw) throws IOException {
		synchronized (data) {
			data.put(name, raw);
			JavaConverter.serialize(data, file);
		}
	}
	
	public synchronized List get(String wildcard) throws MalformedPatternException, IOException {
		synchronized (data) {
			List list=new ArrayList();
			Iterator it = data.entrySet().iterator();
			WildCardFilter filter=new WildCardFilter( wildcard);
			Map.Entry entry;
			String value;
			while(it.hasNext()) {
				entry=(Map.Entry)it.next();
				value=(String) entry.getValue();
				if(filter.accept(value)){
					list.add(entry.getKey());
					it.remove();
				}
			}
			if(list.size()>0)JavaConverter.serialize(data, file);
			return list;
		}
	}
	
}
