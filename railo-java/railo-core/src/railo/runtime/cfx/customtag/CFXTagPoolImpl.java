package railo.runtime.cfx.customtag;

import java.util.Map;
import java.util.Set;

import railo.commons.collection.MapFactory;
import railo.runtime.cfx.CFXTagException;
import railo.runtime.cfx.CFXTagPool;
import railo.runtime.config.Config;
import railo.runtime.type.util.ListUtil;

import com.allaire.cfx.CustomTag;


/**
 * 
 */
public final class CFXTagPoolImpl implements CFXTagPool {
	
	Config config;
	Map<String,CFXTagClass> classes;
	Map<String,CFXTagClass> objects=MapFactory.<String,CFXTagClass>getConcurrentMap();
	
	/**
	 * constructor of the class
	 * @param classes
	 */
	public CFXTagPoolImpl(Map<String,CFXTagClass> classes) {
		this.classes=classes;
	}

    @Override
    public Map<String,CFXTagClass> getClasses() {// FUTURE add generic type to interface
        return classes;
    }
    
	@Override
	public synchronized CustomTag getCustomTag(String name) throws CFXTagException {
		name=name.toLowerCase();
		
		Object o=classes.get(name);
		if(o==null) {
			Set<String> set = classes.keySet();
			String names = ListUtil.arrayToList(set.toArray(new String[set.size()]),",");
			
			throw new CFXTagException("there is no Custom Tag (CFX) with name ["+name+"], available Custom Tags are ["+names+"]");
		}
		CFXTagClass ctc=(CFXTagClass) o;
		CustomTag ct = ctc.newInstance();
		//if(!(o instanceof CustomTag))throw new CFXTagException("["+name+"] is not of type ["+CustomTag.class.getName()+"]");
		return ct;
	}
	
	@Override
	public synchronized CFXTagClass getCFXTagClass(String name) throws CFXTagException {
		name=name.toLowerCase();
		CFXTagClass ctc = classes.get(name);
		if(ctc==null) throw new CFXTagException("there is not Custom Tag (CFX) with name ["+name+"]");
		return ctc;
	}

	@Override
	public synchronized void releaseCustomTag(CustomTag ct) {
		//table.put(ct.getClass().toString(),ct);
	}
	public synchronized void releaseTag(Object tag) {
		//table.put(ct.getClass().toString(),ct);
	}
}