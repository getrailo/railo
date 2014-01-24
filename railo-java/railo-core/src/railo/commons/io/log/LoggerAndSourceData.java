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
    private final String strAppender;
    private Appender _appender;
	private final Map<String, String> appenderArgs;
	private final String strLayout;
	private Layout layout;
	private final Map<String, String> layoutArgs;
	private final Level level;
	private final String name;
	private final Config config;
	private final boolean readOnly;

 
    public LoggerAndSourceData(Config config,String name,String appender, Map<String, String> appenderArgs, String layout, Map<String, String> layoutArgs, Level level, boolean readOnly) {
    	//this.log=new LogAdapter(logger);
    	this.config=config;
    	this.name=name;
    	this.strAppender=appender;
    	this.appenderArgs=appenderArgs;
    	this.strLayout=layout;
    	this.layoutArgs=layoutArgs;
    	this.level=level;
    	this.readOnly=readOnly;
    }

	public String getName() {
		return name;
	}
	
	public String getAppenderName() {
		return strAppender;
	}
	
	public Appender getAppender() {
		getLog();// initilaize if necessary
		return _appender;
	}
	
	public void close() {
		if(_log!=null) {
			Appender a = _appender;
    		_log=null;
			layout = null;
    		if(a!=null)a.close();
    		_appender=null;
    	}
	}


	public Map<String, String> getAppenderArgs() {
		getLog();// initilaize if necessary
		return appenderArgs;
	}

	public Layout getLayout() {
		getLog();// initilaize if necessary
		return layout;
	}
	public String getLayoutName() {
		return strLayout;
	}

	public Map<String, String> getLayoutArgs() {
		getLog();// initilaize if necessary
		return layoutArgs;
	}

	public Level getLevel() {
		return level;
	}

	public boolean getReadOnly() {
		return readOnly;
	}

    public Log getLog() {
    	if(_log==null) {
    		layout = Log4jUtil.getLayout(strLayout, layoutArgs);
    		_appender = Log4jUtil.getAppender(config, layout,name, strAppender, appenderArgs);
    		_log=new LogAdapter(Log4jUtil.getLogger(config, _appender, name, level));
    	}
    	return _log;
    }
    
    public Logger getLogger() {
    	getLog();// make sure it exists
        return _log.getLogger();
    }

    
}
