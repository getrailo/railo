package railo.commons.io.log.log4j.layout;

import java.util.Locale;
import java.util.TimeZone;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.format.DateFormat;
import railo.runtime.format.TimeFormat;
import railo.runtime.op.Caster;

public class ClassicLayout extends Layout {
	private static final String LINE_SEPARATOR=System.getProperty("line.separator");

	private static final  DateFormat dateFormat=new DateFormat(Locale.US);
	private static final  TimeFormat timeFormat=new TimeFormat(Locale.US);

	
	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return super.getContentType();
	}

	@Override
	public String getHeader() {
		return "\"Severity\",\"ThreadID\",\"Date\",\"Time\",\"Application\",\"Message\""+LINE_SEPARATOR;
	}

	@Override
	public void activateOptions() {
		// TODO Auto-generated method stub

	}

	@Override
	public String format(LoggingEvent event) {
		
		StringBuilder data=new StringBuilder();
        String application;
        
        String msg = Caster.toString(event.getMessage(),null);
        int index=msg.indexOf("->");
        if(index>-1) {
        	application=msg.substring(0,index);
        	msg=msg.substring(index+2);
        }
        else
        	application="";
        
        //if(!ArrayUtil.isEmpty(params)) 
        //	application=Caster.toString(params[0],"");
        // Severity
        data.append('"');
        data.append(event.getLevel().toString());
        data.append('"');
        
        data.append(',');
        
        data.append('"');
        data.append(event.getThreadName());
        data.append('"');
        
        data.append(',');
        
        // Date
        data.append('"');
        
        data.append(dateFormat.format(event.timeStamp,"mm/dd/yyyy",TimeZone.getDefault()));
        data.append('"');
        
        data.append(',');
        
        // Time
        data.append('"');
        data.append(timeFormat.format(event.timeStamp,"HH:mm:ss",TimeZone.getDefault()));
        data.append('"');
        
        data.append(',');
        
        // Application
        data.append('"');
        data.append(StringUtil.replace(application,"\"","\"\"",false));
        data.append('"');
        
        data.append(',');
        
        // Message
        data.append('"');
        if(msg==null && event.getMessage()!=null) msg=event.getMessage().toString();
        data.append(StringUtil.replace(msg,"\"","\"\"",false));
        ThrowableInformation ti = event.getThrowableInformation();
        if(ti!=null) {
        	Throwable t = ti.getThrowable();
        	data.append(';');
            String em = ExceptionUtil.getMessage(t);
            data.append(StringUtil.replace(em,"\"","\"\"",false));
			data.append(';');
            String est = ExceptionUtil.getStacktrace(t, false);
            data.append(StringUtil.replace(est,"\"","\"\"",false));
        }
        
        data.append('"');
        
        return data.append(LINE_SEPARATOR).toString();
        
    }
	

	@Override
	public boolean ignoresThrowable() {
		return false;
	}

}
