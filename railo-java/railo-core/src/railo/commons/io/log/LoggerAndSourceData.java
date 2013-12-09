package railo.commons.io.log;

import java.util.Map;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import railo.commons.io.log.log4j.Log4jUtil;
import railo.commons.io.log.log4j.LogAdapter;
import railo.runtime.config.Config;

/**
 * 
 */
public final class LoggerAndSourceData {
    
    private LogAdapter _log;
	private final String appender;
	private final Map<String, String> appenderArgs;
	private final String layout;
	private final Map<String, String> layoutArgs;
	private final Level level;
	private final String name;
	private final Config config;

 
    public LoggerAndSourceData(Config config,String name,String appender, Map<String, String> appenderArgs, String layout, Map<String, String> layoutArgs, Level level) {
    	//this.log=new LogAdapter(logger);
    	this.config=config;
    	this.name=name;
    	this.appender=appender;
    	this.appenderArgs=appenderArgs;
    	this.layout=layout;
    	this.layoutArgs=layoutArgs;
    	this.level=level;
    }

	public String getName() {
		return name;
	}
	
	public String getAppender() {
		return appender;
	}

	public Map<String, String> getAppenderArgs() {
		return appenderArgs;
	}

	public String getLayout() {
		return layout;
	}

	public Map<String, String> getLayoutArgs() {
		return layoutArgs;
	}

	public Level getLevel() {
		return level;
	}

    public Log getLog() {
    	if(_log==null) {
    		Layout l = Log4jUtil.getLayout(layout, layoutArgs);
    		Appender a = Log4jUtil.getAppender(config, l,name, appender, appenderArgs);
    		_log=new LogAdapter(Log4jUtil.getLogger(config, a, name, level));
    	}
        return _log;
    }
    
    public Logger getLogger() {
    	getLog();// make sure it exists
        return _log.getLogger();
    }

    
}
