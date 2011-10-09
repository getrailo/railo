package railo.runtime.type.dt;

import java.io.Serializable;
import java.util.Date;

import railo.runtime.dump.Dumpable;
import railo.runtime.op.Castable;

/**
 * 
 */
public abstract class DateTime extends Date implements Dumpable,Castable,Serializable {

    /**
     * constructor of the class
     * @param time
     */
    public DateTime(long time) {
        super(time);
    }

    /**
     * constructor of the class
     */
    public DateTime() {
        super();
    }
    
    /**
     * @return returns the CFML type double value represent a date
     */
    public abstract double toDoubleValue();
}