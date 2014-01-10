package railo.runtime.tag;

import java.io.IOException;

import railo.commons.io.log.LogConsole;
import railo.commons.io.log.LogResource;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.config.Config;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;
import railo.runtime.tag.util.DeprecatedUtil;

/**
* Writes a message to a log file.
*
*
*
**/
public final class Log extends TagImpl {

    private final static short LOG_APPLICATION=10;
    private final static short LOG_SCHEDULER=11;
    private final static short LOG_CONSOLE=12;
    
    

	/** If you omit the file attribute, specifies the standard log file in which to write the message. 
	** 		Ignored if you specify a file attribute */
	private short log=LOG_APPLICATION;

	/** The message text to log. */
	private String text;

	/** The type or severity of the message. */
	private short type=railo.commons.io.log.Log.LEVEL_INFO;
	/**  */
	private String file;

	/** Specifies whether to log the application name if one has been specified in a application tag. */
	private boolean application;
	private String charset=null;
	
	@Override
	public void release()	{
		super.release();
		log=LOG_APPLICATION;
		type=railo.commons.io.log.Log.LEVEL_INFO;
		file=null;
		application=false;
		charset=null;
	}

	/** set the value log
	*  If you omit the file attribute, specifies the standard log file in which to write the message. 
	* 		Ignored if you specify a file attribute
	* @param log value to set
	 * @throws ApplicationException
	**/
	public void setLog(String log) throws ApplicationException	{
		if(StringUtil.isEmpty(log,true)) return;
	    log=log.toLowerCase().trim();
	    if(log.equals("application")) this.log=LOG_APPLICATION;
	    else if(log.equals("scheduler")) this.log=LOG_SCHEDULER;
	    else if(log.equals("console")) this.log=LOG_CONSOLE;
		else 
		    throw new ApplicationException("invalid value for attribute log ["+log+"]","valid values are [application, scheduler,console]");
	}

	/** set the value text
	*  The message text to log.
	* @param text value to set
	**/
	public void setText(String text)	{
		this.text=text;
	}

	/** set the value type
	*  The type or severity of the message.
	* @param type value to set
	 * @throws ApplicationException
	**/
	public void setType(String type) throws ApplicationException	{
	    type=type.toLowerCase().trim(); 
	    if(type.equals("information")) this.type=railo.commons.io.log.Log.LEVEL_INFO;
	    else if(type.equals("info")) this.type=railo.commons.io.log.Log.LEVEL_INFO;
	    else if(type.equals("warning")) this.type=railo.commons.io.log.Log.LEVEL_WARN;
	    else if(type.equals("warn")) this.type=railo.commons.io.log.Log.LEVEL_WARN;
	    else if(type.equals("error")) this.type=railo.commons.io.log.Log.LEVEL_ERROR;
        else if(type.startsWith("fatal")) this.type=railo.commons.io.log.Log.LEVEL_FATAL;
        else if(type.startsWith("debug")) this.type=railo.commons.io.log.Log.LEVEL_DEBUG;
		else
		    throw new ApplicationException("invalid value for attribute type ["+type+"]",
		      "valid values are [information,warning,error,fatal,debug]");

	}

	/** set the value time
	*  Specifies whether to log the system time.
	* @param time value to set
	 * @throws ApplicationException
	**/
	public void setTime(boolean useTime) throws ApplicationException	{
		if(useTime) return;
		DeprecatedUtil.tagAttribute(pageContext,"Log", "time");
	    throw new ApplicationException("attribute [time] for tag [log] is deprecated, only the value true is allowed");
	}

	/** set the value file
	*  
	* @param file value to set
	 * @throws ApplicationException
	**/
	public void setFile(String file) throws ApplicationException	{
		if(StringUtil.isEmpty(file))return;
		
	    if(file.indexOf('/')!=-1 || file.indexOf('\\')!=-1)
	        throw new ApplicationException("value ["+file+"] from attribute [file] at tag [log] can only contain a filename, file separators like [\\/] are not allowed");
		if(!file.endsWith(".log"))file+=".log";
		this.file=file;
	}

	/** set the value date
	*  Specifies whether to log the system date.
	* @param date value to set
	 * @throws ApplicationException
	**/
	public void setDate(boolean useDate) throws ApplicationException	{
		if(useDate) return;
		DeprecatedUtil.tagAttribute(pageContext,"Log", "date");
	    throw new ApplicationException("attribute [date] for tag [log] is deprecated, only the value true is allowed");
	}

	/** set the value thread
	*  Specifies whether to log the thread ID. The thread ID identifies which internal service thread logged a 
	* 		message. Since a service thread normally services a CFML page request to completion, then moves on to 
	* 		the next queued request, the thread ID serves as a rough indication of which request logged a message. 
	* 		Leaving thread IDs turned on can help diagnose patterns of server activity.
	* @param thread value to set
	 * @throws ApplicationException
	**/
	public void setThread(boolean thread) throws ApplicationException	{
		if(thread) return;
		DeprecatedUtil.tagAttribute(pageContext,"Log", "thread");
	    throw new ApplicationException("attribute [thread] for tag [log] is deprecated, only the value true is allowed");
	}

	/** set the value application
	*  Specifies whether to log the application name if one has been specified in a application tag.
	* @param application value to set
	**/
	public void setApplication(boolean application)	{
		this.application=application;
	}


	@Override
	public int doStartTag() throws PageException	{
	    railo.commons.io.log.Log logger;
	    Config config =pageContext.getConfig();
	    if(file==null) {
	    	if(log==LOG_SCHEDULER)logger=config.getScheduleLogger();
	    	else if(log==LOG_CONSOLE)logger=LogConsole.getInstance(config, railo.commons.io.log.Log.LEVEL_INFO);
	        else logger=config.getApplicationLogger();
	        
	    }
	    else {
	    	if(charset==null) charset=pageContext.getConfig().getResourceCharset();
	    	Resource logDir=config.getConfigDir().getRealResource("logs");
	        if(!logDir.exists())logDir.mkdirs();
	        try {
	        	Resource f = logDir.getRealResource(file);
                logger=new LogResource(f,railo.commons.io.log.Log.LEVEL_INFO,charset);
            } catch (IOException e) {
                throw Caster.toPageException(e);
            }
	    }
	    
	    
	    String contextName = pageContext.getApplicationContext().getName();
	    if(contextName==null || !application)contextName="";
	    logger.log(type,contextName,text);
        //logger.write(toStringType(type),contextName,text);
		return SKIP_BODY;
	}

	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		if(StringUtil.isEmpty(log,true)) return;
	    this.charset = charset;
	}
}