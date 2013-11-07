package railo.runtime.cache.tag.request;

import java.io.Serializable;
import java.util.Date;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.Dumpable;
import railo.runtime.type.Query;

public class CacheEntry implements Dumpable, Serializable {
	
	public final Query query;
	private final long creationDate;

	public CacheEntry(Query query){
		this.query=query;
		this.creationDate=System.currentTimeMillis();
	}
	
	public boolean isCachedAfter(Date cacheAfter) {
    	if(cacheAfter==null) return true;
    	if(creationDate>=cacheAfter.getTime()){
        	return true;
        }
        return false;
    }

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties properties) {
		return query.toDumpData(pageContext, maxlevel, properties);
	}
}
