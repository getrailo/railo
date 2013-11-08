package railo.commons.io.log;

import java.util.Locale;
import java.util.TimeZone;

import railo.commons.io.logging.LegacyProxy;
import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.format.DateFormat;
import railo.runtime.format.TimeFormat;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

/**
 * Helper class for the logs
 */
public final class LogUtil {
	
	public static final int LEVEL_TRACE=5; // FUTURE add to Log interface
    
    private static final  DateFormat dateFormat=new DateFormat(Locale.US);
    private static final  TimeFormat timeFormat=new TimeFormat(Locale.US);

    private static final String LINE_SEPARATOR=System.getProperty("line.separator");

    /**
     * return log header line
     * @return header
     */
    public static String getHeader() {
        return "\"Severity\",\"ThreadID\",\"Date\",\"Time\",\"Application\",\"Message\""+LINE_SEPARATOR;
    }
        
    /**
     * return log line from given data
     * @param type
     * @param application
     * @param message
     * @return line
     */
    public static String getLine(int type,String application, String message) {
    	StringBuilder data=new StringBuilder();
        if(application==null)application="";
        if(message==null)message="";
        
        // Severity
        data.append('"');
        data.append(toStringType(type,"INFO"));
        data.append('"');
        
        data.append(',');
        data.append("\"web-0\"");
        
        data.append(',');
        
        DateTime date = new DateTimeImpl();
        // Date
        data.append('"');
        data.append(dateFormat.format(date,"mm/dd/yyyy",TimeZone.getDefault()));
        data.append('"');
        
        data.append(',');
        
        // Time
        data.append('"');
        data.append(timeFormat.format(date,"HH:mm:ss",TimeZone.getDefault()));
        data.append('"');
        
        data.append(',');
        
        // Application
        data.append('"');
        data.append(StringUtil.replace(application,"\"","\"\"",false));
        data.append('"');
        
        data.append(',');
        
        // Message
        data.append('"');
        data.append(StringUtil.replace(message,"\"","\"\"",false));
        data.append('"');
        
        return data.append(LINE_SEPARATOR).toString();
        
    }
    
    /**
     * translate int type to String type
     * @param type
     * @param defaultValue 
     * @return string type
     */
    public static String toStringType(int type, String defaultValue) {
        switch(type) {
        case Log.LEVEL_INFO:    return "INFO"; 
        case Log.LEVEL_DEBUG:   return "DEBUG"; 
        case Log.LEVEL_WARN:    return "WARN"; 
        case Log.LEVEL_ERROR:   return "ERROR"; 
        case Log.LEVEL_FATAL:   return "FATAL"; 
        case LogUtil.LEVEL_TRACE:   return "TRACE"; 
        default:                return defaultValue;
        }
    }
    

    /**
     * transalte a string log type to a int represenatation
     * @param attribute
     * @param defaultValue
     * @return int lelog lev
     */
    public static int toIntType(String attribute, int defaultValue) {
        if(attribute==null) return defaultValue;
        attribute=attribute.toLowerCase().trim();
        if(attribute.startsWith("info")) return Log.LEVEL_INFO;
        if(attribute.startsWith("debug")) return Log.LEVEL_DEBUG;
        if(attribute.startsWith("warn")) return Log.LEVEL_WARN;
        if(attribute.startsWith("error")) return Log.LEVEL_ERROR;
        if(attribute.startsWith("fatal")) return Log.LEVEL_FATAL;
        if(attribute.startsWith("trace")) return LogUtil.LEVEL_TRACE;
        
        return defaultValue;
    }    
    
	public static String toMessage(Exception e) {
		if(e==null) return "";
		String msg = e.getMessage();
		String clazz=Caster.toClassName(e);
		
		if(StringUtil.isEmpty(msg)) return clazz;
		
		return clazz+":"+msg;
	}

	public static void log(Log log, int level, String logName, Throwable t) { 
		log(log,level,logName,"",t);
	}   

	public static void log(Log log, int level, String logName,String msg, Throwable t) { 
		if(log instanceof LogAndSource) log=((LogAndSource)log).getLog();
		if(log instanceof LegacyProxy) {
			((LegacyProxy)log).log(level, logName, msg,t);
		}
		else {
			String em = ExceptionUtil.getMessage(t);
			String est = ExceptionUtil.getStacktrace(t, false);
			log.log(level, logName, msg+";"+em+";"+est);
		}
	}

	public static void log(Log log, int level, String logName, String msg, StackTraceElement[] stackTrace) {
		Throwable t = new Throwable();
		t.setStackTrace(stackTrace);
		log(log,level,logName,msg,t);
	}    
	
	
    
/*
    public static File getLogFileX(Log log) {
        if(log instanceof LogFile) return ((LogFile)log).getFile();
        return null;
    }
    public static Object getLogTemplate(Log log) {
        if(log instanceof LogFile) return ((LogFile)log).getTemplate();
        return "";
    }
*/
}
