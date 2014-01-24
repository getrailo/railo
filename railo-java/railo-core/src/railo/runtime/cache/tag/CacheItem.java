package railo.runtime.cache.tag;

public interface CacheItem {

	public String getHashFromValue();

	public String getName();

	public int getPayload();

	public String getMeta();

	public long getExecutionTime();

	
}
