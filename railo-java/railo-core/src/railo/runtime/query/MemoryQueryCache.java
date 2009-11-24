package railo.runtime.query;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.map.ReferenceMap;

import railo.commons.lang.SizeOf;
import railo.runtime.config.ConfigWeb;
import railo.runtime.db.SQL;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;

/**
 * 
 */
 final class MemoryQueryCache extends QueryCacheSupport {
	
	private Map entries= new ReferenceMap();
	
	/**
     * @see railo.runtime.query.QueryCache#clearUnused()
     */
	public void clearUnused() {
		//print.out("clear unused:"+entries.size());
		if(entries.size()>100) {
			//print.out("clear");
			Object[] _entries = entries.entrySet().toArray();
			Map.Entry entry;
			synchronized(entries) {
				for(int i=0;i<_entries.length;i++) {
					entry =  ((Map.Entry)_entries[i]);
					if(entry!=null) {
						if(!((QueryCacheEntry)entry.getValue()).isInCacheRange(null)) {
							entries.remove(entry.getKey());
						}
					}
				}
			}
		}
	}

	/**
     * @see railo.runtime.query.QueryCache#getQuery(railo.runtime.db.SQL, java.lang.String, java.lang.String, java.lang.String, java.util.Date)
     */
	public Query getQuery(SQL sql, String datasource, String username, String password,Date cacheAfter) {
		return getQuery(key(sql,datasource,username,password),cacheAfter);
    }
	
	public Object get(SQL sql, String datasource, String username, String password,Date cacheAfter) {
		return getObject(key(sql,datasource,username,password),cacheAfter);
    }
	
    private String key(SQL sql, String datasource, String username,String password) {
    	return sql.toHashString()+datasource+username+password;
	}

	/**
	 * returns a Query from Query Cache or null if no match found
	 * @param key
     * @param cacheAfter
	 * @return Query
	 */
    private Query getQuery(String key, Date cacheAfter) {
    	Object obj=getObject(key, cacheAfter);
    	if(obj instanceof Query) return (Query) obj;
    	return null;
    }
    

    private Object getObject(String key, Date cacheAfter) {
    	synchronized(entries) {
            QueryCacheEntry entry = (QueryCacheEntry) entries.get(key);
			if(entry!=null) {
			    if(entry.isInCacheRange(cacheAfter)) {
                    //entry.getQuery().setCached(true);
			        return entry.getValue();
			    }
				entries.remove(key);
			}
			return null;
		}
    }
    

	/**
     * @see railo.runtime.query.QueryCache#set(railo.runtime.db.SQL, java.lang.String, java.lang.String, java.lang.String, railo.runtime.type.Query, java.util.Date)
     */
	public void set(SQL sql, String datasource, String username, String password, Object value,Date cacheBefore) {
        synchronized(entries) {
			entries.put(
					key(sql, datasource, username, password),
					new QueryCacheEntry(cacheBefore,value));
		}
	}
	
    // DIFF 23 zu interface dazu
    public void remove(SQL sql, String datasource, String username, String password) {
        synchronized(entries) {
        	entries.remove(key(sql, datasource, username, password));
		}
	}
	
	

    /**
     * @see railo.runtime.query.QueryCache#clear()
     */
    public void clear() {
        entries.clear();
    }
    public void clear(QueryCacheFilter filter) {
    	Iterator it = entries.entrySet().iterator();
    	String key;
    	//QueryImpl value;
    	Map.Entry entry;
    	while(it.hasNext()){
			entry=(Entry) it.next();
    		key=Caster.toString(entry.getKey(),"@@@@@");
    		//value=(QueryImpl) ((QueryCacheEntry)entry.getValue()).getValue();
    		if(filter.accept(key)){
				entry.setValue(null);
    			//entries.remove(key);
    		}
    	}
    }
    
    public int size() {
    	return entries.size();
    }
    
    

    /**
     * @see railo.runtime.type.Sizeable#sizeOf()
     */
    public long sizeOf() {
        return SizeOf.size(entries);
    }

	public void setConfigWeb(ConfigWeb config) {
		// not needed
	}
}