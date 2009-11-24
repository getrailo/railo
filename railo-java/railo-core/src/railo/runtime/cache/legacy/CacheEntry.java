package railo.runtime.cache.legacy;

import java.io.IOException;

import railo.runtime.type.dt.TimeSpan;
 
public interface CacheEntry {
	
	public String readEntry(TimeSpan timeSpan,String defaultValue) throws IOException;
	public void writeEntry(String entry,boolean append) throws IOException;
	
	//public String getName();
	//public String getRaw();	
}