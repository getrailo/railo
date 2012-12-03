package railo.runtime.debug;

import java.io.Serializable;

/**
 * a single debug entry
 */
public interface DebugEntry extends Serializable {

    /**
     * @return Returns the exeTime.
     */
    public abstract long getExeTime();

    /**
     * @param exeTime The exeTime to set.
     */
    public abstract void updateExeTime(long exeTime);

    /**
     * @return Returns the src.
     */
    public abstract String getSrc();

    /**
     * @return Returns the count.
     */
    public abstract int getCount();

    /**
     * @return Returns the max.
     */
    public abstract long getMax();

    /**
     * @return Returns the min.
     */
    public abstract long getMin();
    
    /**
     * @return the file path of this entry
     */
    public abstract String getPath();
    
    
    
    public abstract String getId();

}