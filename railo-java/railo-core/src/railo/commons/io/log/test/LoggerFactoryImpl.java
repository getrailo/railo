package railo.commons.io.log.test;

import org.slf4j.Logger;
import org.slf4j.ILoggerFactory;

import railo.print;
import railo.commons.io.log.LogAndSource;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalPageContext;

import java.util.HashMap;
import java.util.Map;

/**
 * JDK14LoggerFactory is an implementation of {@link ILoggerFactory} returning
 * the appropriately named {@link LoggerAdapterImpl} instance.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class LoggerFactoryImpl implements ILoggerFactory {

  // key: name (String), value: a JDK14LoggerAdapter;
  Map loggerMap;

  public LoggerFactoryImpl() {
    loggerMap = new HashMap();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.slf4j.ILoggerFactory#getLogger(java.lang.String)
   */
  public synchronized Logger getLogger(String name) {
	  //print.o("name:"+name);
	  Logger ulogger = null;
	  // protect against concurrent access of loggerMap
	  synchronized (this) {
      // the root logger is called "" in JUL
      if(name.equalsIgnoreCase(Logger.ROOT_LOGGER_NAME)) {
        name = "";
      }
      
      
      ulogger = (Logger) loggerMap.get(name);
      if (ulogger == null) {
    	  Config config = ThreadLocalPageContext.getConfig();
    	  LogAndSource logger = config.getApplicationLogger();
        ulogger = new LoggerAdapterImpl(logger,name);
        loggerMap.put(name, ulogger);
      }
    }
    return ulogger;
  }
}
