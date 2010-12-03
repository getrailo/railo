package railo.runtime.cache.eh.remote.rest.sax;

public class CacheConfiguration {



	private boolean clearOnFlush;
	private int diskExpiryThreadIntervalSeconds;
	private boolean diskPersistent;
	private long diskSpoolBufferSize;
	private boolean eternal;
	private int maxElementsInMemory;
	private int maxElementsOnDisk;
	private String name;
	private boolean overflowToDisk;
	private int timeToIdleSeconds;
	private int timeToLiveSeconds;
	
	/**
	 * @return the clearOnFlush
	 */
	public boolean getClearOnFlush() {
		return clearOnFlush;
	}

	/**
	 * @param clearOnFlush the clearOnFlush to set
	 */
	public void setClearOnFlush(boolean clearOnFlush) {
		this.clearOnFlush = clearOnFlush;
	}

	/**
	 * @return the diskExpiryThreadIntervalSeconds
	 */
	public int getDiskExpiryThreadIntervalSeconds() {
		return diskExpiryThreadIntervalSeconds;
	}

	/**
	 * @param diskExpiryThreadIntervalSeconds the diskExpiryThreadIntervalSeconds to set
	 */
	public void setDiskExpiryThreadIntervalSeconds(int diskExpiryThreadIntervalSeconds) {
		this.diskExpiryThreadIntervalSeconds = diskExpiryThreadIntervalSeconds;
	}

	/**
	 * @return the diskPersistent
	 */
	public boolean getDiskPersistent() {
		return diskPersistent;
	}

	/**
	 * @param diskPersistent the diskPersistent to set
	 */
	public void setDiskPersistent(boolean diskPersistent) {
		this.diskPersistent = diskPersistent;
	}
	
	/**
	 * @return the diskSpoolBufferSize
	 */
	public long getDiskSpoolBufferSize() {
		return diskSpoolBufferSize;
	}

	/**
	 * @param diskSpoolBufferSize the diskSpoolBufferSize to set
	 */
	public void setDiskSpoolBufferSize(long diskSpoolBufferSize) {
		this.diskSpoolBufferSize = diskSpoolBufferSize;
	}
	
	/**
	 * @return the eternal
	 */
	public boolean getEternal() {
		return eternal;
	}

	/**
	 * @param eternal the eternal to set
	 */
	public void setEternal(boolean eternal) {
		this.eternal = eternal;
	}
	
	/**
	 * @return the maxElementsInMemory
	 */
	public int getMaxElementsInMemory() {
		return maxElementsInMemory;
	}

	/**
	 * @param maxElementsInMemory the maxElementsInMemory to set
	 */
	public void setMaxElementsInMemory(int maxElementsInMemory) {
		this.maxElementsInMemory = maxElementsInMemory;
	}

	/**
	 * @return the maxElementsOnDisk
	 */
	public int getMaxElementsOnDisk() {
		return maxElementsOnDisk;
	}

	/**
	 * @param maxElementsOnDisk the maxElementsOnDisk to set
	 */
	public void setMaxElementsOnDisk(int maxElementsOnDisk) {
		this.maxElementsOnDisk = maxElementsOnDisk;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the overflowToDisk
	 */
	public boolean isOverflowToDisk() {
		return overflowToDisk;
	}

	/**
	 * @param overflowToDisk the overflowToDisk to set
	 */
	public void setOverflowToDisk(boolean overflowToDisk) {
		this.overflowToDisk = overflowToDisk;
	}


	/**
	 * @return the timeToIdleSeconds
	 */
	public int getTimeToIdleSeconds() {
		return timeToIdleSeconds;
	}

	/**
	 * @param timeToIdleSeconds the timeToIdleSeconds to set
	 */
	public void setTimeToIdleSeconds(int timeToIdleSeconds) {
		this.timeToIdleSeconds = timeToIdleSeconds;
	}

	/**
	 * @return the timeToLiveSeconds
	 */
	public int getTimeToLiveSeconds() {
		return timeToLiveSeconds;
	}

	/**
	 * @param timeToLiveSeconds the timeToLiveSeconds to set
	 */
	public void setTimeToLiveSeconds(int timeToLiveSeconds) {
		this.timeToLiveSeconds = timeToLiveSeconds;
	}
}
