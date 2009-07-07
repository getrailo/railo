package railo.commons.io.log;

/**
 * Contains a Log and the source from where is loaded
 */
public interface LogAndSource extends Log {

    /**
     * @return Returns the log.
     */
    public abstract Log getLog();

    /**
     * @return Returns the source.
     */
    public abstract String getSource();

}