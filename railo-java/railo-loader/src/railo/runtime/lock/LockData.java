package railo.runtime.lock;

/**
 * lock data
 */
public interface LockData {
    /**
     * is type of token read only
     * @return is read only
     */
    boolean isReadOnly();

    /**
     * @return Returns the id.
     */
    int getId();

    /**
     * @return Returns the name.
     */
    String getName();

}