package railo.runtime.cache.tag.timespan;

import java.io.Serializable;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.Dumpable;

public class TimeSpanCacheToken implements Serializable,Dumpable {

	
	public final Object value;

	public TimeSpanCacheToken(Object value) {
		this.value=value;
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties properties) {
		return DumpUtil.toDumpData(value, pageContext, maxlevel, properties);
	}

}
