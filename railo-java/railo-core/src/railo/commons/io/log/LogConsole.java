package railo.commons.io.log;

import java.io.PrintWriter;

import railo.commons.lang.SystemOut;
import railo.runtime.config.Config;


/**
 * log for Console
 */
public final class LogConsole implements Log {
    
    private static LogConsole[] singeltons=new LogConsole[LogUtil.LEVEL_TRACE+1];
    /*{
        new LogConsole(Log.LEVEL_INFO),
        new LogConsole(Log.LEVEL_DEBUG),
        new LogConsole(Log.LEVEL_WARN),
        new LogConsole(Log.LEVEL_ERROR),
        new LogConsole(Log.LEVEL_FATAL)
    };*/

    private int logLevel;

	private PrintWriter writer;
    
    public LogConsole(int logLevel, PrintWriter writer) {
        this.logLevel=logLevel;
        this.writer=writer;
    }
    
    public static LogConsole getInstance(Config config,int logLevel) {
    	if(singeltons[logLevel]==null) {
    		if(config==null || config.getOutWriter()==null)
        		return new LogConsole(logLevel,new PrintWriter(System.out));
        	
    		singeltons[logLevel]=new LogConsole(logLevel,config.getOutWriter());
    	}
    	return singeltons[logLevel];
    }
    
    @Override
    public void log(int level, String application, String message) {
    	if(level>=logLevel)SystemOut.print(writer, LogUtil.getLine(level,application,message));
    }

    @Override
    public void info(String application, String message) {
        log(LEVEL_INFO,application,message);
    }
    @Override
    public void debug(String application, String message) {
        log(LEVEL_DEBUG,application,message);    
    }
    @Override
    public void warn(String application, String message) {
        log(LEVEL_WARN,application,message);
    }
    @Override
    public void error(String application, String message) {
        log(LEVEL_ERROR,application,message);
    }
    @Override
    public void fatal(String application, String message) {
        log(LEVEL_FATAL,application,message);
    }

    @Override
    public int getLogLevel() {
        return logLevel;
    }

    @Override
    public void setLogLevel(int level) {
        this.logLevel=level;
    }

}
