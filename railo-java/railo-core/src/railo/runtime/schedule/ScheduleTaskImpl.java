package railo.runtime.schedule;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import railo.commons.io.res.Resource;
import railo.commons.lang.Md5;
import railo.commons.net.HTTPUtil;
import railo.commons.security.Credentials;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.Date;
import railo.runtime.type.dt.Time;

/**
 * Define a single schedule Task
 */
public final class ScheduleTaskImpl implements ScheduleTask {
    
	public static int INTERVAL_EVEREY=-1;
    private String task;
    private short operation = OPERATION_HTTP_REQUEST;
    private Resource file;
    private Date startDate;
    private Time startTime;
    private URL url;
    private Date endDate;
    private Time endTime;
    private int interval;
    private long timeout;
    private Credentials credentials;
    private ProxyData proxy;
    private boolean resolveURL;

    private long nextExecution;

    private String strInterval;

    private boolean publish;
    private boolean valid=true;
	private boolean hidden;
	private boolean readonly;
	private boolean paused;
	private boolean autoDelete;
	private String md5;

    
    
    
    /**
     * constructor of the class
     * @param task Task name
     * @param file Output File
     * @param startDate Start Date
     * @param startTime Start  Time
     * @param endDate
     * @param endTime
     * @param url URL to invoke
     * @param port Port of the URL to invoke
     * @param interval interval of the job
     * @param timeout request timeout in miilisconds
     * @param credentials username and password for the request
     * @param proxyHost
     * @param proxyPort
     * @param proxyCredentials proxy username and password
     * @param resolveURL resolve links in the output page to absolute references or not
     * @param publish
     * @throws IOException
     * @throws ScheduleException
     */
    public ScheduleTaskImpl(String task, Resource file, Date startDate, Time startTime, 
            Date endDate, Time endTime, String url, int port, String interval,
            long timeout, Credentials credentials, ProxyData proxy, boolean resolveURL, boolean publish,boolean hidden, 
            boolean readonly,boolean paused, boolean autoDelete) throws IOException, ScheduleException {
    	
    	
    	String md5=task.toLowerCase()+file+startDate+startTime+endDate+endTime+url+port+interval+timeout+
    	credentials+proxy+resolveURL+publish+hidden+readonly+paused;
    	md5=Md5.getDigestAsString(md5);
    	this.md5=md5;
        
        if(file!=null && file.toString().trim().length()>0) {
        	Resource parent = file.getParentResource();
	        if(parent==null || !parent.exists())
	            throw new IOException("Directory for output file ["+file+"] doesn't exist");
	        if(file.exists() && !file.isFile())
	            throw new IOException("output file ["+file+"] is not a file");
        }
        if(timeout<1) {
            throw new ScheduleException("value timeout must be greater than 0");
        }
        if(startDate==null) throw new ScheduleException("start date is required");
        if(startTime==null)throw new ScheduleException("start time is required");
        //if(endTime==null)endTime=new Time(23,59,59,999);

        this.task=task.trim();
        this.file=file;
        this.startDate=startDate;
        this.startTime=startTime;
        this.endDate=endDate;
        this.endTime=endTime;
        this.url=toURL(url,port);
        this.interval=toInterval(interval);
        this.strInterval=interval;
        this.timeout=timeout;
        this.credentials=credentials;
        this.proxy=proxy;
        this.resolveURL=resolveURL;
        this.publish=publish;
        this.hidden=hidden;
        this.readonly=readonly;
        this.paused=paused;
        this.autoDelete=autoDelete;
    }



    /**
     * translate a String interval definition to a int definition
     * @param interval
     * @return interval
     * @throws ScheduleException
     */
    private static int toInterval(String interval) throws ScheduleException {
        interval=interval.trim().toLowerCase();
        int i=Caster.toIntValue(interval,0);
        if(i==0) {
            interval=interval.trim();
            if(interval.equals("once")) return INTERVAL_ONCE;
            else if(interval.equals("daily")) return INTERVAL_DAY;
            else if(interval.equals("day")) return INTERVAL_DAY;
            else if(interval.equals("monthly")) return INTERVAL_MONTH;
            else if(interval.equals("month")) return INTERVAL_MONTH;
            else if(interval.equals("weekly")) return INTERVAL_WEEK;
            else if(interval.equals("week")) return INTERVAL_WEEK;
            throw new ScheduleException("invalid interval definition ["+interval+"], valid values are [once,daily,monthly,weekly or number]");
        }
        if(i<10) {
            throw new ScheduleException("interval must be at least 10");
        }
        return i;
    }

    /**
     * translate a urlString and a port definition to a URL Object
     * @param url URL String 
     * @param port URL Port Definition
     * @return returns a URL Object
     * @throws MalformedURLException
     */
    private static URL toURL(String url, int port) throws MalformedURLException {
        URL u = HTTPUtil.toURL(url);
        if(port==-1) return u;
        return new URL(u.getProtocol(), u.getHost(), port, u.getFile());
    }    

    /**
     * @see railo.runtime.schedule.ScheduleTask#getCredentials()
     */
    public Credentials getCredentials() {	return credentials;	}

    /**
     * @see railo.runtime.schedule.ScheduleTask#hasCredentials()
     */
    public boolean hasCredentials() {	return credentials!=null;	}
    
    /**
     * @see railo.runtime.schedule.ScheduleTask#getResource()
     */
    public Resource getResource() {	
    	return file;
    }
    
    /**
     * @see railo.runtime.schedule.ScheduleTask#getInterval()
     */
    public int getInterval() {	return interval;	}
    
    /**
     * @see railo.runtime.schedule.ScheduleTask#getOperation()
     */
    public short getOperation() {	return operation;	}
    
    /**
     * @see railo.runtime.schedule.ScheduleTask#getProxyHost()
     */
    public ProxyData getProxyData() {	return proxy;	}
    
    /**
     * @see railo.runtime.schedule.ScheduleTask#isResolveURL()
     */
    public boolean isResolveURL() {	return resolveURL;	}
    
    /**
     * @see railo.runtime.schedule.ScheduleTask#getTask()
     */
    public String getTask() {	return task;	}
    
    /**
     * @see railo.runtime.schedule.ScheduleTask#getTimeout()
     */
    public long getTimeout() {	return timeout;	}
    
    /**
     * @see railo.runtime.schedule.ScheduleTask#getUrl()
     */
    public URL getUrl() {	
        return url;	
    }

    /**
     * @see railo.runtime.schedule.ScheduleTask#setNextExecution(java.util.Calendar)
     */
    public void setNextExecution(long nextExecution) {	this.nextExecution=nextExecution;	}
    
    /**
     * @see railo.runtime.schedule.ScheduleTask#getNextExecution()
     */
    public long getNextExecution() {	return nextExecution;	}
    
    /**
     * @see railo.runtime.schedule.ScheduleTask#getEndDate()
     */
    public Date getEndDate() {	return endDate;	}
    
    /**
     * @see railo.runtime.schedule.ScheduleTask#getStartDate()
     */
    public Date getStartDate() {	return startDate;	}
    
    /**
     * @see railo.runtime.schedule.ScheduleTask#getEndTime()
     */
    public Time getEndTime() {	return endTime;	}
    
    /**
     * @see railo.runtime.schedule.ScheduleTask#getStartTime()
     */
    public Time getStartTime() {	return startTime;	}

    /**
     * @see railo.runtime.schedule.ScheduleTask#getIntervalAsString()
     */
    public String getIntervalAsString() {	return strInterval;	}
    
    /**
     * @see railo.runtime.schedule.ScheduleTask#getStringInterval()
     */
    public String getStringInterval() {	return strInterval;	}
    /**
     * @see railo.runtime.schedule.ScheduleTask#isPublish()
     */
    public boolean isPublish() {
        return publish;
    }
    /**
     * @see railo.runtime.schedule.ScheduleTask#isValid()
     */
    public boolean isValid() {
        return valid;
    }
    /**
     * @see railo.runtime.schedule.ScheduleTask#setValid(boolean)
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }



	/**
	 * @return the hidden
	 */
	public boolean isHidden() {
		return hidden;
	}



	/** 
	 * @param hidden the hidden to set
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}



	/**
	 * @return the readonly
	 */
	public boolean isReadonly() {
		return readonly;
	}



	/**
	 * @param readonly the readonly to set
	 */
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}



	/**
	 * @see railo.runtime.schedule.ScheduleTask#isPaused()
	 */
	public boolean isPaused() {
		return paused;
	}



	public void setPaused(boolean paused) {
		this.paused=paused;
	}
	

	public boolean isAutoDelete() {
		return autoDelete;
	}



	public void setAutoDelete(boolean autoDelete) {
		this.autoDelete=autoDelete;
	}



	public String md5() {
		return md5;
	}
}