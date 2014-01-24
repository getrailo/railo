package railo.runtime.cache.tag.query;

import java.io.Serializable;
import java.util.Date;

import railo.commons.digest.HashUtil;
import railo.runtime.PageContext;
import railo.runtime.cache.tag.CacheItem;
import railo.runtime.cache.tag.udf.UDFArgConverter;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.Dumpable;
import railo.runtime.type.Query;

public class QueryCacheItem implements CacheItem, Dumpable, Serializable {

	private static final long serialVersionUID = 7327671003736543783L;

	public final Query query;
	private final long creationDate;

	public QueryCacheItem(Query query){
		this.query=query;
		this.creationDate=System.currentTimeMillis();
	}

	@Override
	public String getHashFromValue() {
		return Long.toString(HashUtil.create64BitHash(UDFArgConverter.serialize(query)));
	}
	
	@Override
	public String getName() {
		return query.getName();
	}

	@Override
	public int getPayload() {
		return query.getRecordcount();
	}
	
	@Override
	public String getMeta() {
		return query.getSql().getSQLString();
	}
	
	@Override
	public long getExecutionTime() {
		return query.getExecutionTime();
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
