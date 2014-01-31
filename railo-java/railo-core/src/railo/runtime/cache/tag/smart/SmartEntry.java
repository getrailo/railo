package railo.runtime.cache.tag.smart;

public interface SmartEntry {
	public String getId();

	public String getType();

	public String getEntryHash();

	public String getResultHash();

	public long getCreateTime();
	public long getExecutionTime();

	public String getApplicationName();

	public String getCfid();
	

	public String getName();

	public long getPayLoad();

	public String getMeta();

	public String getTemplate();
	
	public int getLine();
}
