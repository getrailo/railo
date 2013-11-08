package railo.commons.io.logging;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import railo.commons.io.log.Log;
import railo.commons.io.logging.format.ClassicFormatter;
import railo.commons.io.logging.handler.ConsoleHandler;
import railo.commons.io.logging.handler.ResourceHandler;
import railo.commons.io.res.Resource;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWeb;
import railo.runtime.type.util.ArrayUtil;

 class LoggerUtil {

	public static final long MAX_FILE_SIZE=1024*1024*10;
    public static final int MAX_FILES=10;
    
    
    public static Logger getConsole(Config config,String type, Level level) {
    	if(config instanceof ConfigWeb) {
    		ConfigWeb cw=(ConfigWeb) config;
    		type=cw.getLabel()+"."+type;
    	}
    	
    	if(config==null || config.getOutWriter()==null)
    		return getLogger(type,new ConsoleHandler(new PrintWriter(System.out)));
    	return getLogger(type,new ConsoleHandler(config.getOutWriter()));
    }

	public static Logger getResource(Config config,Resource res, String type, Level level, Charset charset) throws SecurityException, IOException {
    	if(config instanceof ConfigWeb) {
    		ConfigWeb cw=(ConfigWeb) config;
    		type=cw.getLabel()+"."+type;
    	}
    	
    	Logger logger = getLogger(type,new ResourceHandler(res,charset,MAX_FILE_SIZE,MAX_FILES,true));
    	logger.setUseParentHandlers(false);
    	return logger;
    }
    
    private static Logger getLogger(String type, Handler handler) {
    	Logger logger = Logger.getLogger(type);
    	handler.setFormatter(new ClassicFormatter());
    	//handler.setFormatter(new SimpleFormatter());
    	//handler.setFormatter(new XMLFormatter());
    	//handler.setErrorManager(new ExceptionErrorManager());
    	setHandler(logger, handler);
    	return logger;
	}
    

    public static void setHandler(Logger logger, Handler handler) {
    	// remove all existing handlers 
    	Handler[] handlers = logger.getHandlers();
    	if(ArrayUtil.isEmpty(handlers)) for(int i=0;i<handlers.length;i++) {
    		logger.removeHandler(handlers[i]);
    	}
    	// add handler
    	logger.addHandler(handler);
	}
    

	public static Level toLevel(int level) {
		switch(level){
		case Log.LEVEL_FATAL: return Level.SEVERE;
		case Log.LEVEL_ERROR: return Level.SEVERE;
		case Log.LEVEL_WARN: return Level.WARNING;
		case Log.LEVEL_DEBUG: return Level.FINE;
		case Log.LEVEL_INFO: return Level.INFO;
		}
		return Level.FINE;
	}

	public static int toLevel(Level level) {
		if(Level.SEVERE.equals(level)) return Log.LEVEL_ERROR;
		if(Level.WARNING.equals(level)) return Log.LEVEL_WARN;
		if(Level.INFO.equals(level)) return Log.LEVEL_INFO;
		if(Level.CONFIG.equals(level)) return Log.LEVEL_DEBUG;
		if(Level.FINE.equals(level)) return Log.LEVEL_DEBUG;
		if(Level.FINER.equals(level)) return Log.LEVEL_DEBUG;
		if(Level.FINEST.equals(level)) return Log.LEVEL_DEBUG;
		return Log.LEVEL_INFO;
	}
}
