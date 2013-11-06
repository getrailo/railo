package railo.runtime.cache.tag.timespan;

import java.io.IOException;

import railo.commons.lang.KeyGenerator;
import railo.runtime.cache.tag.CacheIdentifier;
import railo.runtime.db.SQL;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.functions.cache.Util;
import railo.runtime.op.Caster;

public class TimespanCacheIdentifier implements CacheIdentifier {
	
	private final String id;
	private final long timespan;

	public TimespanCacheIdentifier(String id, long timespan) {
		this.id=id;
		this.timespan=timespan;
	}

	@Override
	public String id() {
		return id;
	}

	public long getTimeSpan() {
		return timespan;
	}
	
	// id=Util.key(KeyGenerator.createKey(sql.toHashString()+datasource+username+password));
}
