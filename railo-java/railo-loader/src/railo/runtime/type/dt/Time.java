package railo.runtime.type.dt;

/**
 * time interface
 */
public abstract class Time extends DateTime {
    /**
     * constructor of the class
     * @param time
     */
    public Time(long time) {
        super(time);
    }

    /**
     * constructor of the class
     */
    public Time() {
        super();
    }
}