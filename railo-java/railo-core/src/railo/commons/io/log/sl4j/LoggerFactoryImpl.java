package railo.commons.io.log.sl4j;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import railo.commons.io.log.Log;
import railo.commons.io.log.LogAndSource;
import railo.commons.io.log.LogAndSourceImpl;
import railo.commons.io.log.LogConsole;
import railo.runtime.config.ConfigImpl;
import railo.runtime.engine.ThreadLocalPageContext;

/**
 * JDK14LoggerFactory is an implementation of {@link ILoggerFactory} returning
 * the appropriately named {@link LoggerAdapterImpl} instance.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class LoggerFactoryImpl implements ILoggerFactory {

  // key: name (String), value: a JDK14LoggerAdapter;
  Map<String,Logger> loggerMap;

  public LoggerFactoryImpl() {
    loggerMap = new HashMap<String,Logger>();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.slf4j.ILoggerFactory#getLogger(java.lang.String)
   */
  public synchronized Logger getLogger(String name) {
	  Logger logger = null;
	  // protect against concurrent access of loggerMap
	  synchronized (this) {
      
		  if(name.equalsIgnoreCase(Logger.ROOT_LOGGER_NAME)) name = "";
		
		  logger = loggerMap.get(name);
		  if(logger == null) {
			  ConfigImpl config = (ConfigImpl) ThreadLocalPageContext.getConfig();
			  if(config==null){
				  LogAndSource las= new LogAndSourceImpl(new LogConsole(Log.LEVEL_ERROR,new PrintWriter(System.out)), "");
				  logger = new LoggerAdapterImpl(las,name); 
			  }
			  else {
				  LogAndSource las;
				  if(name!=null &&name.startsWith("org.hibernate")) las = config.getORMLogger();
				  else las = config.getApplicationLogger();
				  logger = new LoggerAdapterImpl(las,name); 
			  }
			  loggerMap.put(name, logger);
		  }
	  }
	  return logger;
  }
}
