

package railo.commons.io.log;

import railo.runtime.config.Config;
import railo.runtime.exp.PageException;

/**
 * Datasource output logger
 * TODO impl
 */
public final class LogDataSource implements Log {

    private Config config;
    private String datasource;
    private String username;
    private String password;
    private String table;
    private int logLevel;

    /**
     * Constructor of the class
     * @param config
     * @param datasource 
     * @param username 
     * @param password 
     * @param table 
     * @throws PageException 
     */
    public LogDataSource(int logLevel,Config config, String datasource, String username, String password, String table) {
        this.logLevel=logLevel;
        this.config=config;
        this.datasource=datasource;
        this.username=username;
        this.password=password;
        this.table=table;
        
        
    }


    /**
     * @see railo.commons.io.log.Log#log(int, java.lang.String, java.lang.String)
     */
    public void log(int level, String application, String message) {
        // TODO impl
    }

    /**
     * @see railo.commons.io.log.LogWithLevel#getLogLevel()
     */
    public int getLogLevel() {
        return logLevel;
    }

    /**
     * @see railo.commons.io.log.LogWithLevel#setLogLevel(int)
     */
    public void setLogLevel(int level) {
        this.logLevel=level;
    }


    /**
     * @see railo.commons.io.log.Log#debug(java.lang.String, java.lang.String)
     */
    public void debug(String application, String message) {
    	log(LEVEL_DEBUG, application, message);
    }

    /**
     * @see railo.commons.io.log.Log#error(java.lang.String, java.lang.String)
     */
    public void error(String application, String message) {
    	log(LEVEL_ERROR, application, message);
    }

    /**
     * @see railo.commons.io.log.Log#fatal(java.lang.String, java.lang.String)
     */
    public void fatal(String application, String message) {
    	log(LEVEL_FATAL, application, message);
    }

    /**
     * @see railo.commons.io.log.Log#info(java.lang.String, java.lang.String)
     */
    public void info(String application, String message) {
    	log(LEVEL_INFO, application, message);
    }

    /**
     * @see railo.commons.io.log.Log#warn(java.lang.String, java.lang.String)
     */
    public void warn(String application, String message) {
    	log(LEVEL_WARN, application, message);
    }
}
