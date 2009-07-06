package railo.commons.io.log;

/**
 * 
 */
public final class LogAndSourceImpl implements LogAndSource {
    
    private final String source;
    private final Log log;

    /**
     * @param log
     * @param source
     */
    public LogAndSourceImpl(Log log, String source) {
        this.log=log;
        this.source=source;
    }
    
    /**
     * @see railo.commons.io.log.Log#log(int, java.lang.String, java.lang.String)
     */
    public void log(int level, String application, String message) {
        log.log(level,application,message);
    }

    /**
     * @see railo.commons.io.log.Log#info(java.lang.String, java.lang.String)
     */
    public void info(String application, String message) {
        log.info(application,message);
    }

    /**
     * @see railo.commons.io.log.Log#debug(java.lang.String, java.lang.String)
     */
    public void debug(String application, String message) {
        log.debug(application,message);
    }

    /**
     * @see railo.commons.io.log.Log#warn(java.lang.String, java.lang.String)
     */
    public void warn(String application, String message) {
        log.warn(application,message);
    }

    /**
     * @see railo.commons.io.log.Log#error(java.lang.String, java.lang.String)
     */
    public void error(String application, String message) {
        log.error(application,message);
    }

    public void fatal(String application, String message) {
        log.fatal(application,message);
    }

    /**
     * @see railo.commons.io.log.LogAndSource#getLog()
     */
    public Log getLog() {
        return log;
    }

    /**
     * @see railo.commons.io.log.LogAndSource#getSource()
     */
    public String getSource() {
        return source;
    }

    /**
     * @see railo.commons.io.log.Log#getLogLevel()
     */
    public int getLogLevel() {
        return log.getLogLevel();
    }

    /**
     * @see railo.commons.io.log.Log#setLogLevel(int)
     */
    public void setLogLevel(int level) {
        log.setLogLevel(level);    
    }
    
}
