package railo.runtime.cfx.customtag;

import java.util.Map;
import java.util.Set;

import railo.commons.collections.HashTable;
import railo.runtime.cfx.CFXTagException;
import railo.runtime.cfx.CFXTagPool;
import railo.runtime.config.Config;
import railo.runtime.type.List;

import com.allaire.cfx.CustomTag;


/**
 * 
 */
public final class CFXTagPoolImpl implements CFXTagPool {
	
	Config config;
	Map classes;
	Map objects=new HashTable();
	
	/**
	 * constructor of the class
	 * @param classes
	 */
	public CFXTagPoolImpl(Map classes) {
		this.classes=classes;
	}

    /**
     * @see railo.runtime.cfx.CFXTagPool#getClasses()
     */
    public Map getClasses() {
        return classes;
    }
	/**
     * @see railo.runtime.cfx.CFXTagPool#getCustomTag(java.lang.String)
     */
	public synchronized CustomTag getCustomTag(String name) throws CFXTagException {
		name=name.toLowerCase();
		
		Object o=classes.get(name);
		if(o==null) {
			Set<String> set = classes.keySet();
			String names = List.arrayToList(set.toArray(new String[set.size()]),",");
			
			throw new CFXTagException("there is no Custom Tag (CFX) with name ["+name+"], available Custom Tags are ["+names+"]");
		}
		CFXTagClass ctc=(CFXTagClass) o;
		CustomTag ct = ctc.newInstance();
		//if(!(o instanceof CustomTag))throw new CFXTagException("["+name+"] is not of type ["+CustomTag.class.getName()+"]");
		return ct;
	}
	
	// FUTURE add to interface
	public synchronized CFXTagClass getCFXTagClass(String name) throws CFXTagException {
		name=name.toLowerCase();
		CFXTagClass ctc=(CFXTagClass) classes.get(name);
		if(ctc==null) throw new CFXTagException("there is not Custom Tag (CFX) with name ["+name+"]");
		return ctc;
	}

	/**
     * @see railo.runtime.cfx.CFXTagPool#releaseCustomTag(com.allaire.cfx.CustomTag)
     */
	public synchronized void releaseCustomTag(CustomTag ct) {
		//table.put(ct.getClass().toString(),ct);
	}
	public synchronized void releaseTag(Object tag) {
		//table.put(ct.getClass().toString(),ct);
	}
}