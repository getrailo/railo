package railo.runtime.debug;

import java.io.Serializable;

/**
 * a single debug entry
 */
public interface DebugEntry extends Serializable {

    /**
     * @return Returns the exeTime.
     */
    public abstract int getExeTime();

    /**
     * @param exeTime The exeTime to set.
     */
    public abstract void updateExeTime(int exeTime);

    /**
     * @return Returns the fileLoadTime.
     */
    public abstract int getFileLoadTime();

    /**
     * @param fileLoadTime The fileLoadTime to set.
     */
    public abstract void updateFileLoadTime(int fileLoadTime);

    /**
     * @param queryTime update queryTime
     */
    public abstract void updateQueryTime(int queryTime);

    /**
     * @return Returns the src.
     */
    public abstract String getSrc();

    /**
     * @return Returns the count.
     */
    public abstract int getCount();

    /**
     * @return Returns the queryTime.
     */
    public abstract int getQueryTime();

    /**
     * @return Returns the max.
     */
    public abstract int getMax();

    /**
     * @return Returns the min.
     */
    public abstract int getMin();
    
    /**
     * @return the file path of this entry
     */
    public abstract String getPath();
    
    
    
    public abstract String getId();

    /**
     * resets the query time to zero
     */
    public abstract void resetQueryTime();

}