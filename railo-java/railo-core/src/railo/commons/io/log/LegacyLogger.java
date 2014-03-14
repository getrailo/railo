package railo.commons.io.log;

import railo.runtime.exp.DeprecatedException;

public class LegacyLogger implements LogAndSource {

	private Log logger;

	public LegacyLogger(Log logger) {
		this.logger=logger;
	}

	@Override
	public void log(int level, String application, String message) {
		logger.log(level, application, message);
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
		return logger.getLogLevel();
	}

	@Override
	public void setLogLevel(int level) {
		logger.setLogLevel(level);
	}

	@Override
	public Log getLog() {
		return logger;
	}

	@Override
	public String getSource() {
		throw new RuntimeException(new DeprecatedException("this method is no longer supported"));
	}

}
