package railo.commons.io.log.test;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

import railo.print;
import railo.commons.io.log.Log;
import railo.commons.io.log.LogAndSource;

/**
 * A wrapper over {@link java.util.logging.Logger java.util.logging.Logger} in
 * conformity with the {@link Logger} interface. Note that the logging levels
 * mentioned in this class refer to those defined in the java.util.logging
 * package.
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author Peter Royal
 */
public final class LoggerAdapterImpl extends MarkerIgnoringBase implements LocationAwareLogger {

	private LogAndSource logger;
	private String _name;

	public LoggerAdapterImpl(LogAndSource logger, String name){
		//print.o("logger:"+logger.getSource());
		this.logger=logger;
		this._name=name;
	}
	
	public void debug(String msg) {log(Log.LEVEL_DEBUG,msg);}
	public void error(String msg) {log(Log.LEVEL_ERROR,msg);}
	public void info(String msg) {log(Log.LEVEL_INFO,msg);}
	public void trace(String msg) {}
	public void warn(String msg) {log(Log.LEVEL_WARN,msg);}

	public void debug(String format, Object arg) {log(Log.LEVEL_DEBUG,format,arg);}
	public void error(String format, Object arg) {log(Log.LEVEL_ERROR,format,arg);}
	public void info(String format, Object arg) {log(Log.LEVEL_INFO,format,arg);}
	public void trace(String format, Object arg) {}
	public void warn(String format, Object arg) {log(Log.LEVEL_WARN,format,arg);}

	public void debug(String format, Object arg1, Object arg2) {log(Log.LEVEL_DEBUG,format,arg1,arg2);}
	public void error(String format, Object arg1, Object arg2) {log(Log.LEVEL_DEBUG,format,arg1,arg2);}
	public void info(String format, Object arg1, Object arg2) {log(Log.LEVEL_DEBUG,format,arg1,arg2);}
	public void trace(String format, Object arg1, Object arg2) {}
	public void warn(String format, Object arg1, Object arg2) {log(Log.LEVEL_DEBUG,format,arg1,arg2);}

	public void debug(String format, Object[] args) {log(Log.LEVEL_DEBUG,format,args);}
	public void error(String format, Object[] args) {log(Log.LEVEL_DEBUG,format,args);}
	public void info(String format, Object[] args) {log(Log.LEVEL_DEBUG,format,args);}
	public void trace(String format, Object[] args) {}
	public void warn(String format, Object[] args) {log(Log.LEVEL_DEBUG,format,args);}
	
	public void debug(String msg, Throwable t) {log(Log.LEVEL_DEBUG,msg,t);}
	public void error(String msg, Throwable t) {log(Log.LEVEL_DEBUG,msg,t);}
	public void info(String msg, Throwable t) {log(Log.LEVEL_DEBUG,msg,t);}
	public void trace(String msg, Throwable t) {}
	public void warn(String msg, Throwable t) {log(Log.LEVEL_DEBUG,msg,t);}
	
	private void log(int level, String msg) {
		logger.log(level, _name, msg);
		
	}
	private void log(int level, String msg, Throwable t) {
		
	}
	
	private void log(int level, String format, Object arg) {
		
	}
	private void log(int level, String format, Object arg1, Object arg2) {
		
	}
	private void log(int level, String format, Object[] args) {
		
	}

	














	public boolean isDebugEnabled() {
		return logger.getLogLevel()<=Log.LEVEL_DEBUG;
	}

	public boolean isErrorEnabled() {
		return logger.getLogLevel()<=Log.LEVEL_ERROR;
	}

	public boolean isInfoEnabled() {
		return logger.getLogLevel()<=Log.LEVEL_INFO;
	}

	public boolean isTraceEnabled() {
		return false;
	}

	public boolean isWarnEnabled() {
		return logger.getLogLevel()<=Log.LEVEL_WARN;
	}











	public void log(Marker arg0, String arg1, int arg2, String arg3,
			Throwable arg4) {
		// TODO Auto-generated method stub
		
	}

  
}
