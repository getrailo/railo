package railo.commons.io.log;

import java.util.logging.Logger;

import railo.commons.io.logging.LegacyProxy;

/**
 * 
 */
public final class LogAndSourceImpl implements LogAndSource {
    
    private final String source;
    private final LegacyProxy log;

    /**
     * @param log
     * @param source
     */
    public LogAndSourceImpl(Logger logger, String source) {
        this.log=new LegacyProxy(logger);
        this.source=source;
    }
    /*public LogAndSourceImpl(Log log, String source) {
        this.log=log;
        this.source=source;
    }*/
 
    @Override
    public void log(int level, String application, String message) {
        log.log(level,application,message);
    }
    /*public void log(int level, String application, String message, Throwable t) {
        log.log(level,application,message,t);
    } 
    public void log(int level, String application, Throwable t) {
        log.log(level,application,t);
    }*/

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
    
    public Logger getLogger() {
        return log.getLogger();
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
