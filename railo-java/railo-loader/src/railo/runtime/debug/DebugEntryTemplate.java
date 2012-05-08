package railo.runtime.debug;

/**
 * a single debug entry
 */
public interface DebugEntryTemplate extends DebugEntry {

    /**
     * @return Returns the fileLoadTime.
     */
    public abstract long getFileLoadTime();

    /**
     * @param fileLoadTime The fileLoadTime to set.
     */
    public abstract void updateFileLoadTime(long fileLoadTime);

    /**
     * @return Returns the queryTime.
     */
    public abstract long getQueryTime();

    /**
     * @param queryTime update queryTime
     */
    public abstract void updateQueryTime(long queryTime);

    /**
     * resets the query time to zero
     */
    public abstract void resetQueryTime();

}