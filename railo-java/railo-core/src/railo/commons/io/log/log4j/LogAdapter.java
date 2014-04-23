package railo.commons.io.log.log4j;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import railo.commons.io.log.Log;
import railo.commons.lang.StringUtil;

public class LogAdapter implements Log {
	
	private Logger logger;

	public LogAdapter(Logger logger){
		this.logger=logger;
	}

	@Override
	public void log(int level, String application, String message) {
		logger.log(Log4jUtil.toLevel(level), application+"->"+message);
		
	}

	@Override
	public void log(int level, String application, String message, Throwable t) {
		if(StringUtil.isEmpty(message))logger.log(Log4jUtil.toLevel(level), application,t);
		else logger.log(Log4jUtil.toLevel(level), application+"->"+message,t);
	}


	@Override
	public void log(int level, String application, Throwable t) {
		logger.log(Log4jUtil.toLevel(level), application,toThrowable(t));
	}

	@Override
	public void trace(String application, String message) {
		log(Log.LEVEL_TRACE,application,message);
	}

	@Override
	public void info(String application, String message) {
		log(Log.LEVEL_INFO,application,message);
	}

	@Override
	public void debug(String application, String message) {
		log(Log.LEVEL_DEBUG,application,message);
	}

	@Override
	public void warn(String application, String message) {
		log(Log.LEVEL_WARN,application,message);
	}

	@Override
	public void error(String application, String message) {
		log(Log.LEVEL_ERROR,application,message);
	}

	@Override
	public void fatal(String application, String message) {
		log(Log.LEVEL_FATAL,application,message);
	}

	@Override
	public int getLogLevel() {
		return Log4jUtil.toLevel(logger.getLevel());
	}

	@Override
	public void setLogLevel(int level) {
		logger.setLevel(Log4jUtil.toLevel(level));
	}

	public Logger getLogger() {
		return logger;
	}

	private Throwable toThrowable(Throwable t) {
		if(t instanceof InvocationTargetException) return ((InvocationTargetException)t).getTargetException();
		return t;
	}
}
