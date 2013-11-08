package railo.commons.io.logging;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

import railo.commons.io.log.Log;

public class LegacyProxy implements Log {
	
	private Logger logger;

	public LegacyProxy(Logger logger){
		this.logger=logger;
	}

	@Override
	public void log(int level, String application, String message) {
		logger.log(LoggerUtil.toLevel(level), message,application);
	}

	public void log(int level, String application, String message, Throwable t) {
		LogRecord lr = new LogRecord(LoggerUtil.toLevel(level), message);
		lr.setParameters(new Object[]{application});
		lr.setThrown(t);
		logger.log(lr);
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
		log(Log.LEVEL_WARN,application,message);
	}

	@Override
	public void fatal(String application, String message) {
		log(Log.LEVEL_FATAL,application,message);
	}

	@Override
	public int getLogLevel() {
		return LoggerUtil.toLevel(logger.getLevel());
	}

	@Override
	public void setLogLevel(int level) {
		logger.setLevel(LoggerUtil.toLevel(level));
	}

	public Logger getLogger() {
		return logger;
	}
}
