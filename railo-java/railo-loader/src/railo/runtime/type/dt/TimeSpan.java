package railo.runtime.type.dt;

import java.io.Serializable;

import railo.runtime.dump.Dumpable;
import railo.runtime.op.Castable;

/**
 * defines a time span 
 */
public interface TimeSpan extends Castable,Dumpable,Serializable  {

    /**
     * @return returns the timespan in milliseconds
     */
    public abstract long getMillis();

    /**
     * @return returns the timespan in seconds
     */
    public abstract long getSeconds();

    /**
     * @return Returns the day value.
     */
    public abstract int getDay();

    /**
     * @return Returns the hour value.
     */
    public abstract int getHour();

    /**
     * @return Returns the minute value.
     */
    public abstract int getMinute();

    /**
     * @return Returns the second value.
     */
    public abstract int getSecond();

}