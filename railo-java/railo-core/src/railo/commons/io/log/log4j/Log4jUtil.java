package railo.commons.io.log.log4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import railo.print;
import railo.commons.io.log.Log;
import railo.commons.io.log.LogUtil;
import railo.commons.io.log.log4j.appender.ConsoleAppender;
import railo.commons.io.log.log4j.appender.RollingResourceAppender;
import railo.commons.io.log.log4j.layout.ClassicLayout;
import railo.commons.io.res.Resource;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWeb;

public class Log4jUtil {
	
	public static final long MAX_FILE_SIZE=1024*1024*10;
    public static final int MAX_FILES=10;
	

    public static Logger getConsole(Config config,String type, Level level) {
    	if(config instanceof ConfigWeb) {
    		ConfigWeb cw=(ConfigWeb) config;
    		type=cw.getLabel()+"."+type;
    	}
    	PatternLayout layout=new PatternLayout("%d{dd.MM.yyyy HH:mm:ss,SSS} %-5p [%c] %m%n");
    	
    	if(config==null || config.getOutWriter()==null)
    		return getLogger(type,new ConsoleAppender(System.out,layout));
    	
    	print.e(config.getOutWriter());
    	return getLogger(type,new ConsoleAppender(config.getOutWriter(),layout));
    }

	public static Logger getResource(Config config,Resource res, String type, Level level, Charset charset) throws SecurityException, IOException {
    	if(config instanceof ConfigWeb) {
    		ConfigWeb cw=(ConfigWeb) config;
    		type=cw.getLabel()+"."+type;
    	}
    	ClassicLayout layout = new ClassicLayout();
    	Logger logger = getLogger(type,new RollingResourceAppender(layout,res,charset,true,MAX_FILE_SIZE,MAX_FILES));
    	return logger;
    }

	private static Logger getLogger(String type, Appender appender) { 
		Logger l = LogManager.getLogger(type);
    	l.setAdditivity(false);
    	l.removeAllAppenders();
    	l.addAppender(appender);
    	l.setLevel(org.apache.log4j.Level.TRACE);
		return l;
	}

	public static Level toLevel(int level) {
		switch(level){
		case Log.LEVEL_FATAL: return Level.FATAL;
		case Log.LEVEL_ERROR: return Level.ERROR;
		case Log.LEVEL_WARN: return Level.WARN;
		case Log.LEVEL_DEBUG: return Level.DEBUG;
		case Log.LEVEL_INFO: return Level.INFO;
		case LogUtil.LEVEL_TRACE: return Level.TRACE;
		}
		return Level.INFO;
	}

	public static int toLevel(Level level) {
		if(Level.FATAL.equals(level)) return Log.LEVEL_FATAL;
		if(Level.ERROR.equals(level)) return Log.LEVEL_ERROR;
		if(Level.WARN.equals(level)) return Log.LEVEL_WARN;
		if(Level.DEBUG.equals(level)) return Log.LEVEL_DEBUG;
		if(Level.INFO.equals(level)) return Log.LEVEL_INFO;
		if(Level.TRACE.equals(level)) return LogUtil.LEVEL_TRACE;
		return Log.LEVEL_INFO;
	}

}
