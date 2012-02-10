package railo.runtime.cache.legacy;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.oro.text.regex.MalformedPatternException;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.WildCardFilter;
import railo.runtime.converter.JavaConverter;

public class MetaData implements Serializable {
	
	private static Map<String,MetaData> instances=new HashMap<String,MetaData>();
	
	private HashMap<String,String> data=new HashMap<String,String>();
	private Resource file;
	
	private MetaData(Resource file) {
		this.file=file;
		data=new HashMap<String,String>();
	}
	
	public MetaData(Resource file,HashMap<String,String> data) {
		this.file=file;
		this.data=data;
	}

	public static MetaData getInstance(Resource directory) {
		MetaData instance=instances.get(directory.getAbsolutePath());
		
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
	
	public synchronized List<String> get(String wildcard) throws MalformedPatternException, IOException {
		synchronized (data) {
			List<String> list=new ArrayList<String>();
			Iterator<Entry<String, String>> it = data.entrySet().iterator();
			WildCardFilter filter=new WildCardFilter( wildcard);
			Entry<String, String> entry;
			String value;
			while(it.hasNext()) {
				entry = it.next();
				value= entry.getValue();
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
