package railo.runtime.debug;

import java.io.Serializable;

import railo.runtime.PageSource;

/**
 * a single debug entry
 */
public interface DebugEntry extends Serializable {

    /* *
     * start the watch
     */
    //public abstract void start();

    /* *
     * stops the watch
     * @return returns the current time or 0 if watch not was running
     */
    //public abstract int stop();

    /* *
     * @return returns the current time or 0 if watch is not running
     */
    //public abstract int time();

    /* *
     * resets the stopwatch
     */
    //public abstract void reset();

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
     * @return Returns the PageSource.
     * @deprecated no longer supported, use <code>getPath()</code> instead
     */
    public abstract PageSource getPageSource();
    
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