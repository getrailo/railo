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
 
    @Override
    public void log(int level, String application, String message) {
        log.log(level,application,message);
    }

    @Override
    public void info(String application, String message) {
        log.info(application,message);
    }

    @Override
    public void debug(String application, String message) {
        log.debug(application,message);
    }

    @Override
    public void warn(String application, String message) {
        log.warn(application,message);
    }

    @Override
    public void error(String application, String message) {
        log.error(application,message);
    }

    public void fatal(String application, String message) {
        log.fatal(application,message);
    }

    @Override
    public Log getLog() {
        return log;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public int getLogLevel() {
        return log.getLogLevel();
    }

    @Override
    public void setLogLevel(int level) {
        log.setLogLevel(level);    
    }
    
    @Override
    public String toString(){
    	return log+":"+source;
    }
}
