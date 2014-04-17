package railo.commons.io.log;

/**
 * Log Interface
 */
public interface Log {

    /**
     * Field <code>LEVEL_INFO</code>
     */
    public static final int LEVEL_INFO=0;
    /**
     * Field <code>LEVEL_DEBUG</code>
     */
    public static final int LEVEL_DEBUG=1;
    /**
     * Field <code>LEVEL_WARN</code>
     */
    public static final int LEVEL_WARN=2;
    /**
     * Field <code>LEVEL_ERROR</code>
     */
    public static final int LEVEL_ERROR=3;
    /**
     * Field <code>LEVEL_FATAL</code>
     */
    public static final int LEVEL_FATAL=4;

    /**
     * log one line
     * @param level level to log (Log.LEVEL_DEBUG, Log.LEVEL_WARN, Log.LEVEL_ERROR)
     * @param application application name
     * @param message message to log
     */
    public void log(int level, String application, String message);

    // FUTURE public void log(int level, String application, String message,Throwable t);
    // FUTURE public void log(int level, String application,Throwable t);
    
    /**
     * log level info
     * @param application application name
     * @param message message to log
     */
    public void info(String application, String message);

    /**
     * log level debug
     * @param application application name
     * @param message message to log
     */
    public void debug(String application, String message);
    
    /**
     * log level warn
     * @param application application name
     * @param message message to log
     */
    public void warn(String application, String message);
    
    /**
     * log level error
     * @param application application name
     * @param message message to log
     */
    public void error(String application, String message);
    
    /**
     * log level fatal
     * @param application application name
     * @param message message to log
     */
    public void fatal(String application, String message);

    /**
     * @return returns the log level of the log
     */
    public int getLogLevel();
    
    /**
     * @param level sets the log level of the log
     */
    public void setLogLevel(int level);
}
