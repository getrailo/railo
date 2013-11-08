package railo.commons.io.logging.format;

import java.io.PrintStream;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.format.DateFormat;
import railo.runtime.format.TimeFormat;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ArrayUtil;

public class ClassicFormatter extends Formatter {
	
	private static final String LINE_SEPARATOR=System.getProperty("line.separator");

	private static final  DateFormat dateFormat=new DateFormat(Locale.US);
	private static final  TimeFormat timeFormat=new TimeFormat(Locale.US);

	@Override
	public String format(LogRecord record) {
		StringBuilder data=new StringBuilder();
        Object[] params = record.getParameters();
        String application="";
        if(!ArrayUtil.isEmpty(params)) 
        	application=Caster.toString(params[0],"");
        // Severity
        data.append('"');
        data.append(toString(record.getLevel()));
        data.append('"');
        
        data.append(',');
        
        data.append('"');
        data.append(record.getThreadID());
        data.append('"');
        
        data.append(',');
        
        // Date
        data.append('"');
        data.append(dateFormat.format(record.getMillis(),"mm/dd/yyyy",TimeZone.getDefault()));
        data.append('"');
        
        data.append(',');
        
        // Time
        data.append('"');
        data.append(timeFormat.format(record.getMillis(),"HH:mm:ss",TimeZone.getDefault()));
        data.append('"');
        
        data.append(',');
        
        // Application
        data.append('"');
        data.append(StringUtil.replace(application,"\"","\"\"",false));
        data.append('"');
        
        data.append(',');
        
        // Message
        data.append('"');
        data.append(StringUtil.replace(record.getMessage(),"\"","\"\"",false));
        Throwable t = record.getThrown();
        if(t!=null) {
        	String em = ExceptionUtil.getMessage(t);
            data.append(';');
            data.append(StringUtil.replace(em,"\"","\"\"",false));
			String est = ExceptionUtil.getStacktrace(t, false);
            data.append(';');
            data.append(StringUtil.replace(est,"\"","\"\"",false));
			
        }
        
        data.append('"');
        
        return data.append(LINE_SEPARATOR).toString();
        
    }

	private String toString(Level level) {
		if(Level.SEVERE.equals(level)) return "FATAL";
		if(Level.WARNING.equals(level)) return "ERROR";
		if(Level.INFO.equals(level)) return "WARN";
		if(Level.CONFIG.equals(level)) return "DEBUG";
		if(Level.FINE.equals(level)) return "INFO";
		return "INFO";
	}

	@Override
	public String getHead(Handler h) {
		return "\"Severity\",\"ThreadID\",\"Date\",\"Time\",\"Application\",\"Message\""+LINE_SEPARATOR;
	}
}
