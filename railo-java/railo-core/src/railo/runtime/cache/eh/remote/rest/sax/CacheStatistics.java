package railo.runtime.cache.eh.remote.rest.sax;

public class CacheStatistics {


	private double averageGetTime;
	private int cacheHits;
	private int diskStoreSize;
	private int evictionCount;
	private int inMemoryHits;
	private int memoryStoreSize;
	private int misses;
	private int onDiskHits;
	private int size;
	private String statisticsAccuracy;
	
	/**
	 * @return the averageGetTime
	 */
	public double getAverageGetTime() {
		return averageGetTime;
	}
	/**
	 * @param averageGetTime the averageGetTime to set
	 */
	public void setAverageGetTime(double averageGetTime) {
		this.averageGetTime = averageGetTime;
	}
	/**
	 * @return the cacheHits
	 */
	public int getCacheHits() {
		return cacheHits;
	}
	/**
	 * @param cacheHits the cacheHits to set
	 */
	public void setCacheHits(int cacheHits) {
		this.cacheHits = cacheHits;
	}
	/**
	 * @return the diskStoreSize
	 */
	public int getDiskStoreSize() {
		return diskStoreSize;
	}
	/**
	 * @param diskStoreSize the diskStoreSize to set
	 */
	public void setDiskStoreSize(int diskStoreSize) {
		this.diskStoreSize = diskStoreSize;
	}
	/**
	 * @return the evictionCount
	 */
	public int getEvictionCount() {
		return evictionCount;
	}
	/**
	 * @param evictionCount the evictionCount to set
	 */
	public void setEvictionCount(int evictionCount) {
		this.evictionCount = evictionCount;
	}
	/**
	 * @return the inMemoryHits
	 */
	public int getInMemoryHits() {
		return inMemoryHits;
	}
	/**
	 * @param inMemoryHits the inMemoryHits to set
	 */
	public void setInMemoryHits(int inMemoryHits) {
		this.inMemoryHits = inMemoryHits;
	}
	/**
	 * @return the memoryStoreSize
	 */
	public int getMemoryStoreSize() {
		return memoryStoreSize;
	}
	/**
	 * @param memoryStoreSize the memoryStoreSize to set
	 */
	public void setMemoryStoreSize(int memoryStoreSize) {
		this.memoryStoreSize = memoryStoreSize;
	}
	/**
	 * @return the misses
	 */
	public int getMisses() {
		return misses;
	}
	/**
	 * @param misses the misses to set
	 */
	public void setMisses(int misses) {
		this.misses = misses;
	}
	/**
	 * @return the onDiskHits
	 */
	public int getOnDiskHits() {
		return onDiskHits;
	}
	/**
	 * @param onDiskHits the onDiskHits to set
	 */
	public void setOnDiskHits(int onDiskHits) {
		this.onDiskHits = onDiskHits;
	}
	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}
	/**
	 * @return the statisticsAccuracy
	 */
	public String getStatisticsAccuracy() {
		return statisticsAccuracy;
	}
	/**
	 * @param statisticsAccuracy the statisticsAccuracy to set
	 */
	public void setStatisticsAccuracy(String statisticsAccuracy) {
		this.statisticsAccuracy = statisticsAccuracy;
	}
	
}
