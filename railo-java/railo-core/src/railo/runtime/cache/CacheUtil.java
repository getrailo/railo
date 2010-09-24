package railo.runtime.cache;

import railo.commons.io.cache.Cache;
import railo.commons.io.cache.CacheEntry;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.dt.TimeSpanImpl;

public class CacheUtil {

	public static Struct getInfo(CacheEntry ce) {
		Struct info=new StructImpl();
		info.setEL("key", ce.getKey());
		info.setEL("created", ce.created());
		info.setEL("last_hit", ce.lastHit());
		info.setEL("last_modified", ce.lastModified());

		info.setEL("hit_count", new Double(ce.hitCount()));
		info.setEL("size", new Double(ce.size()));
		
		
		info.setEL("idle_time_span", toTimespan(ce.idleTimeSpan()));		
		info.setEL("live_time_span", toTimespan(ce.liveTimeSpan()));
		
		
		return info;
	}


	public static Struct getInfo(Cache c) {
		Struct info=new StructImpl();

		long value = c.hitCount();
		if(value>=0)info.setEL("hit_count", new Double(value));
		value = c.missCount();
		if(value>=0)info.setEL("miss_count", new Double(value));
		
		return info;
	}

	
	public static Object toTimespan(long timespan) {
		if(timespan==0)return "";
		
		TimeSpan ts = TimeSpanImpl.fromMillis(timespan);
		if(ts==null)return "";
		return ts;
	}


	public static String toString(CacheEntry ce) {

		return "created:	"+ce.created()
		+"\nlast-hit:	"+ce.lastHit()
		+"\nlast-modified:	"+ce.lastModified()
		
		+"\nidle-time:	"+ce.idleTimeSpan()
		+"\nlive-time	:"+ce.liveTimeSpan()
		
		+"\nhit-count:	"+ce.hitCount()
		+"\nsize:		"+ce.size();
	}


	
	
	
	
	
}
