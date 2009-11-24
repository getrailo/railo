package railo.runtime.schedule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import railo.commons.date.DateTimeUtil;
import railo.commons.io.log.LogAndSource;
import railo.commons.io.res.Resource;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.Date;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.dt.Time;

/**
 * scheduler class to execute the scheduled tasks
 */
public final class OldSchedulerImpl implements Scheduler {

    private ScheduleTaskImpl[] tasks;
    private static final long MINUTE=60000;
    private static final long DAY=24*3600000;
    
    private static final long TOLERANCE=10;
    private Calendar calendar=Calendar.getInstance();
    private long nextExecutionTime;
    private long future;
    
    //private File schedulerDir;
    private Resource schedulerFile;
    private Document doc;
    private LogAndSource log;
    private StorageUtil su=new StorageUtil();
	private String charset;
	private Config config;
	//private String md5;
	
    /**
     * constructor of the sheduler
     * @param config 
     * @param schedulerDir schedule file
     * @param log
     * @throws IOException
     * @throws SAXException
     * @throws PageException
     */
    public OldSchedulerImpl(Config config, Resource schedulerDir, LogAndSource log, String charset) throws SAXException, IOException, PageException {
    	this.charset=charset;
    	this.config=config;
    	
    	initFile(schedulerDir,log);
        doc=su.loadDocument(schedulerFile);
        tasks=readInAllTasks();
        
        
        init();
    }
    
	/*private String createMD5(ScheduleTaskImpl[] tasks) throws IOException {
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<tasks.length;i++){
			sb.append(tasks[i].md5());
		}
		return MD5.getDigestAsString(sb.toString());
	}*/

	/*public void reinit(Resource scheduleDirectory, LogAndSource logger) throws IOException, PageException{
		print.err("reinit");
		initFile(scheduleDirectory, logger);
		
		ScheduleTaskImpl[] tasks=readInAllTasks();
		String md5=createMD5(tasks);
		if(this.md5.equals(md5)) return;
		this.md5=md5;
		print.err("check single tasks");
		
		for(int i=0;i<tasks.length;i++){
			addTask(tasks[i]);
		}
	}*/

	private void addTask(ScheduleTaskImpl task) {
		
		for(int i=0;i<tasks.length;i++){
			if(!tasks[i].getTask().equals(task.getTask())) continue;
			if(!tasks[i].md5().equals(task.md5())) {
				tasks[i]=task;
				init(task);
				
			}
			return;
		}
		
		
		ScheduleTaskImpl[] tmp = new ScheduleTaskImpl[tasks.length+1];
		for(int i=0;i<tasks.length;i++){
			tmp[i]=tasks[i];
		}
		tmp[tasks.length]=task;
		tasks=tmp;
		init(task);
	}







	private void initFile(Resource schedulerDir, LogAndSource log) throws IOException {
		this.schedulerFile=schedulerDir.getRealResource("scheduler.xml");
		if(!schedulerFile.exists()) su.loadFile(schedulerFile,"/resource/schedule/default.xml");
		this.log=log;  
	}
	
    /**
     * initialize all tasks
     */

    private void init(ScheduleTask task) {
        calendar.set(Calendar.YEAR,3000);
        future=calendar.getTimeInMillis();
        nextExecutionTime=future;
        initTask(task);
        for(int i=0;i<tasks.length;i++) {
            if(tasks[i].isValid()) {
                long next = tasks[i].getNextExecution();
                if(nextExecutionTime>next)nextExecutionTime=next;
            }
        }
    	
    }
    
    private void init() {
        calendar.set(Calendar.YEAR,3000);
        future=calendar.getTimeInMillis();
        nextExecutionTime=future;
        
        for(int i=0;i<tasks.length;i++) {
            initTask(tasks[i]);
            
            if(tasks[i].isValid()) {
                long next = tasks[i].getNextExecution();
                if(nextExecutionTime>next)nextExecutionTime=next;
            }
        }
    }

	/**
	 * read in all schedule tasks
	 * @return all schedule tasks
	 * @throws PageException
     */
    private ScheduleTaskImpl[] readInAllTasks() throws PageException {
        Element root = doc.getDocumentElement();
        NodeList children = root.getChildNodes();
        ArrayList list=new ArrayList();
        
        int len=children.getLength();
        for(int i=0;i<len;i++) {
            Node n=children.item(i);
            if(n instanceof Element && n.getNodeName().equals("task")) {
                list.add(readInTask((Element)n));
            } 
        }
        return (ScheduleTaskImpl[]) list.toArray(new ScheduleTaskImpl[list.size()]);
    }

    /**
     * read in a single task element
     * @param el
     * @return matching task to Element
     * @throws PageException
     */
    private ScheduleTaskImpl readInTask(Element el) throws PageException {
        long timeout=su.toLong(el,"timeout");
        if(timeout>0 && timeout<1000)timeout*=1000;
        if(timeout<0)timeout=600000;
        try {
            ScheduleTaskImpl  st = new ScheduleTaskImpl(
                    su.toString(el,"name").trim(),
                    su.toResource(config,el,"file"),
                    su.toDate(config,el,"startDate"),
                    su.toTime(config,el,"startTime"),
                    su.toDate(config,el,"endDate"),
                    su.toTime(config,el,"endTime"),
                    su.toString(el,"url"),
                    su.toInt(el,"port",-1),
                    su.toString(el,"interval"),
                    timeout,
                    su.toCredentials(el,"username","password"),
                    su.toString(el,"proxyHost"),
                    su.toInt(el,"proxyPort",80),
                    su.toCredentials(el,"proxyUser","proxyPassword"),
                    su.toBoolean(el,"resolveUrl"),
                    su.toBoolean(el,"publish"),
                    su.toBoolean(el,"hidden",false),
                    su.toBoolean(el,"readonly",false),
                    su.toBoolean(el,"paused",false));
            return st;
        } catch (Exception e) {e.printStackTrace();
            throw Caster.toPageException(e);
        }
    }
    
    /**
     * sets all attributes in XML Element from Schedule Task
     * @param el
     * @param task
     */
    private void setAttributes(Element el,ScheduleTask task) {
        if(el==null) return;
        NamedNodeMap atts = el.getAttributes();
        
        for(int i=atts.getLength()-1;i>=0;i--) {
            Attr att=(Attr) atts.item(i);
            el.removeAttribute(att.getName());
        }
        
        su.setString(el,"name",task.getTask());
        su.setFile(el,"file",task.getResource());
        su.setDateTime(el,"startDate",task.getStartDate());
        su.setDateTime(el,"startTime",task.getStartTime());
        su.setDateTime(el,"endDate",task.getEndDate());
        su.setDateTime(el,"endTime",task.getEndTime());
        su.setString(el,"url",task.getUrl().toExternalForm());
        su.setInt(el,"port",task.getUrl().getPort());
        su.setString(el,"interval",task.getIntervalAsString());
        su.setInt(el,"timeout",(int)task.getTimeout());
        su.setCredentials(el,"username","password",task.getCredentials());
        su.setString(el,"proxyHost",task.getProxyHost());
        su.setInt(el,"proxyPort",task.getProxyPort());
        su.setCredentials(el,"proxyUser","proxyPassword",task.getProxyCredentials());
        su.setBoolean(el,"resolveUrl",task.isResolveURL());  
        su.setBoolean(el,"publish",task.isPublish());   
        su.setBoolean(el,"hidden",((ScheduleTaskImpl)task).isHidden());  
        su.setBoolean(el,"readonly",((ScheduleTaskImpl)task).isReadonly());  
    }
    
    /**
     * translate a schedule task object to a XML Element
     * @param task schedule task to translate
     * @return XML Element
     */
    private Element toElement(ScheduleTask task) {
        Element el = doc.createElement("task");
        setAttributes(el,task);   
        return el;
    }

	/**
     * @see railo.runtime.schedule.Scheduler#getScheduleTask(java.lang.String)
     */
	public ScheduleTask getScheduleTask(String name) throws ScheduleException {
	    for(int i=0;i<tasks.length;i++) {
	        if(tasks[i].getTask().equalsIgnoreCase(name)) return tasks[i];
	    }
	    throw new ScheduleException("schedule task with name "+name+" doesn't exist");
	}
	
	/**
     * @see railo.runtime.schedule.Scheduler#getScheduleTask(java.lang.String, railo.runtime.schedule.ScheduleTask)
     */
	public ScheduleTask getScheduleTask(String name, ScheduleTask defaultValue) {
	    for(int i=0;i<tasks.length;i++) {
	        if(tasks[i]!=null && tasks[i].getTask().equalsIgnoreCase(name)) return tasks[i];
	    }
	    return defaultValue;
	}

	/**
     * @see railo.runtime.schedule.Scheduler#getAllScheduleTasks()
     */
	public ScheduleTask[] getAllScheduleTasks() {
		ArrayList list=new ArrayList();
		for(int i=0;i<tasks.length;i++) {
	        if(!tasks[i].isHidden()) list.add(tasks[i]);
	    }
	    return (ScheduleTask[]) list.toArray(new ScheduleTask[list.size()]);
	}
	
	/**
     * @see railo.runtime.schedule.Scheduler#addScheduleTask(railo.runtime.schedule.ScheduleTask, boolean)
     */
	public void addScheduleTask(ScheduleTask task, boolean allowOverwrite) throws ScheduleException, IOException {
	    //ScheduleTask exTask = getScheduleTask(task.getTask(),null);
	    NodeList list = doc.getDocumentElement().getChildNodes();
	    Element el=su.getElement(list,"name", task.getTask());
	    
	    if(!allowOverwrite && el!=null)
		    throw new ScheduleException("there is already a schedule task with name "+task.getTask());
	    
	    addTask((ScheduleTaskImpl)task);
	    
	    // Element update
	    if(el!=null) {
		    setAttributes(el,task);
	    }
	    // Element insert
	    else {
		    doc.getDocumentElement().appendChild(toElement(task));
	    }
	    
	    su.store(doc,schedulerFile);
	}
	

    // FUTURE add to interface
	public void pauseScheduleTask(String name, boolean pause, boolean throwWhenNotExist) throws ScheduleException, IOException {

	    for(int i=0;i<tasks.length;i++) {
	        if(tasks[i].getTask().equalsIgnoreCase(name)) {
	        	tasks[i].setPaused(pause);
	            
	        }
	    }
	    
	    NodeList list = doc.getDocumentElement().getChildNodes();
	    Element el=su.getElement(list,"name", name);
	    if(el!=null) {
	    	el.setAttribute("paused", Caster.toString(pause));
	        //el.getParentNode().removeChild(el);
	    }
	    else if(throwWhenNotExist) throw new ScheduleException("can't "+(pause?"pause":"resume")+" schedule task ["+name+"], task doesn't exist");
	    
	    //init();
	    su.store(doc,schedulerFile);
	}

	/**
     * @see railo.runtime.schedule.Scheduler#removeScheduleTask(java.lang.String, boolean)
     */
	public synchronized void removeScheduleTask(String name, boolean throwWhenNotExist) throws IOException, ScheduleException {
	    
	    int pos=-1;
	    for(int i=0;i<tasks.length;i++) {
	        if(tasks[i].getTask().equalsIgnoreCase(name)) {
	        	tasks[i].setValid(false);
	            pos=i;
	        }
	    }
	    if(pos!=-1) {
		    ScheduleTaskImpl[] newTasks=new ScheduleTaskImpl[tasks.length-1];
		    int count=0;
		    for(int i=0;i<tasks.length;i++) {
		        if(i!=pos)newTasks[count++]=tasks[i];
		        
		    }
		    tasks=newTasks;
	    }
	    
	    
	    NodeList list = doc.getDocumentElement().getChildNodes();
	    Element el=su.getElement(list,"name", name);
	    if(el!=null) {
	        el.getParentNode().removeChild(el);
	    }
	    else if(throwWhenNotExist) throw new ScheduleException("can't delete schedule task ["+name+"], task doesn't exist");
	    
	    //init();
	    su.store(doc,schedulerFile);
	}

	/**
     * @see railo.runtime.schedule.Scheduler#runScheduleTask(java.lang.String, boolean)
     */
	public synchronized void runScheduleTask(String name, boolean throwWhenNotExist) throws IOException, ScheduleException {
	    ScheduleTask task = getScheduleTask(name);
	    
	    if(task!=null) {
	        ExecutionThread.execute(config, log, task, charset);
	        //execute(task);
	        init(task);
	    }
	    else if(throwWhenNotExist) throw new ScheduleException("can't run schedule task ["+name+"], task doesn't exist");
	    
	    
	    su.store(doc,schedulerFile);
	}
	
	
	
    
    
    
    
    
    /**
     * initialize a single task, define next execution time
     * @param task task to init next execution time
     */
    private void initTask(ScheduleTask task) {
        if(task==null) {
            return;
        }
        TimeZone tz = ThreadLocalPageContext.getTimeZone(config);
        
        // start date
        Date startDate = task.getStartDate();
        long startDateMillis=timeAtMidnight(tz,startDate);
        
        // start time
        Time startTime = task.getStartTime();
        long startTimeMillis=DateTimeUtil.getInstance().getMilliSecondsInDay(tz,startTime.getTime());
        // start
        long startMillis=startDateMillis+startTimeMillis;
        
        // now
        DateTime now = new DateTimeImpl(config);
        long nowMillis=now.getTime();
        long nowTimeMillis=DateTimeUtil.getInstance().getMilliSecondsInDay(tz,now.getTime());
        long nowDateMillis=nowMillis-nowTimeMillis;

        // end
        Date endDate = task.getEndDate();
        if(endDate!=null) {
	        long endDateMillis=timeAtMidnight(ThreadLocalPageContext.getTimeZone(config),endDate);
	        if(endDateMillis<=nowDateMillis) {
	            task.setValid(false);
	            return;
	        }
        }
        Time endTime = task.getEndTime();
        long endTimeMillis=(endTime==null)?DAY:DateTimeUtil.getInstance().getMilliSecondsInDay(ThreadLocalPageContext.getTimeZone(config),endTime.getTime());

        
        // direct excution
        if(startDateMillis>nowDateMillis) {
            task.setValid(setNext(task,(startDateMillis+startTimeMillis)));
            return ;
        }
        if(startDateMillis==nowDateMillis && startTimeMillis>nowTimeMillis) {
            task.setValid(setNext(task,(startDateMillis+startTimeMillis)));
            return ;
        }
        
        int interval = task.getInterval();
        
        
        // ONCE
        if(ScheduleTask.INTERVAL_ONCE == interval)	{
            if(startMillis+MINUTE+TOLERANCE>=nowMillis) {
                task.setValid(setNext(task,(nowMillis)));
                return ;
            }
            task.setValid(false);
            return;
        }
        //DAY
        else if(ScheduleTask.INTERVAL_DAY == interval) {
            task.setValid(setNext(task,
               (TOLERANCE+getNextDayExecution(startTimeMillis,nowDateMillis,nowTimeMillis))));
            return ;
        }
        // WEEK
        else if(ScheduleTask.INTERVAL_WEEK == interval) {
            long dayExecution = getNextDayExecution(startTimeMillis,nowDateMillis,nowTimeMillis);
            Calendar start = Caster.toCalendar(dayExecution+TOLERANCE,ThreadLocalPageContext.getTimeZone(config));
            int startWeekDay=Caster.toCalendar(startDateMillis+1,ThreadLocalPageContext.getTimeZone(config)).get(Calendar.DAY_OF_WEEK);
            
            while(startWeekDay!=start.get(Calendar.DAY_OF_WEEK)) {
                start.set(Calendar.DAY_OF_YEAR,start.get(Calendar.DAY_OF_YEAR)+1);
                //print.ln(start.getTime());
            }
            
            task.setValid(setNext(task,start.getTimeInMillis()));
            
            //while(start.getTimeInMillis()<nowMillis)start.set(Calendar.WEEK_OF_YEAR,start.get(Calendar.WEEK_OF_YEAR)+1);
            //task.setValid(setNext(task,start));
            return ;
        }
        // MONTH
        else if(ScheduleTask.INTERVAL_MONTH == interval) {
            long dayExecution = getNextDayExecution(startTimeMillis,nowDateMillis,nowTimeMillis);
            Calendar start=Caster.toCalendar(dayExecution+TOLERANCE,null);
            int startMonthDay=Caster.toCalendar(startDateMillis+1,null).get(Calendar.DAY_OF_MONTH);
            
            while(startMonthDay!=start.get(Calendar.DAY_OF_MONTH)) {
                start.set(Calendar.DAY_OF_YEAR,start.get(Calendar.DAY_OF_YEAR)+1);
            }
            task.setValid(setNext(task,start.getTimeInMillis()));
            return ;
        }
        // INTERVAL
        else if(interval>0) {
            if(startTimeMillis>=endTimeMillis) {
                task.setValid(false);
                return ;
            }
            long dayMillis=nowTimeMillis;
            if(startTimeMillis>dayMillis)dayMillis=startTimeMillis;
            if(endTimeMillis<dayMillis) {
                task.setValid(setNext(task,nowDateMillis+DAY+startTimeMillis));
                return ;
            }
            task.setValid(setNext(task,nowDateMillis+dayMillis));
            return ;
            
        }
        task.setValid(false);
        return;
    }
    
    private long getNextDayExecution(long startTimeMillis, long nowDateMillis, long nowTimeMillis) {
        if(startTimeMillis+MINUTE+TOLERANCE>=nowTimeMillis) {
            return nowDateMillis+startTimeMillis;
        }
        return nowDateMillis+startTimeMillis+DAY;
    }
    
    /**
     * @see railo.runtime.schedule.Scheduler#execute()
     */
    public void execute() {
    	
        long now=System.currentTimeMillis();
        if(nextExecutionTime>now) return;
        for(int i=0;i<tasks.length;i++) {
            ScheduleTaskImpl task = tasks[i];
            
            if(task==null || !task.isValid() || task.isPaused() || task.getNextExecution()>now) continue;
            new ExecutionThread(config,log,task,charset).start();
            
            Calendar next = Caster.toCalendar(task.getNextExecution(),ThreadLocalPageContext.getTimeZone(config));
            int interval = task.getInterval();
            // ONCE
            if(ScheduleTask.INTERVAL_ONCE == interval) {
                tasks[i].setValid(false);
            }
            // DAY
            else if(ScheduleTask.INTERVAL_DAY == interval) {
                next.set(Calendar.DAY_OF_MONTH,next.get(Calendar.DAY_OF_MONTH)+1);
                if(!setNext(task,next.getTimeInMillis()))tasks[i].setValid(false);
            }
            // WEEK
            else if(ScheduleTask.INTERVAL_WEEK == interval) {
                next.set(Calendar.WEEK_OF_YEAR,next.get(Calendar.WEEK_OF_YEAR)+1);
                if(!setNext(task,next.getTimeInMillis()))tasks[i].setValid(false);
            }
            // MONTH
            else if(ScheduleTask.INTERVAL_MONTH == interval) {
                Date startDate = task.getStartDate();
                long startDateMillis=timeAtMidnight(ThreadLocalPageContext.getTimeZone(),startDate);
                int startMonthDay=Caster.toCalendar(startDateMillis+1,null).get(Calendar.DAY_OF_MONTH);

                next.set(Calendar.DAY_OF_YEAR,next.get(Calendar.DAY_OF_YEAR)+1);
                while(startMonthDay!=next.get(Calendar.DAY_OF_MONTH)) {
                    next.set(Calendar.DAY_OF_YEAR,next.get(Calendar.DAY_OF_YEAR)+1);
                }
                //next.set(Calendar.MONTH,next.get(Calendar.MONTH)+1);
                if(!setNext(task,next.getTimeInMillis()))tasks[i].setValid(false);
            }
            // INTERVAL
            else if(interval>0) {
                //print.out(interval);
                next.set(Calendar.SECOND,next.get(Calendar.SECOND)+interval);
                if(!setNext(task,next.getTimeInMillis()))tasks[i].setValid(false);
                
            }
            
        }
        nextExecutionTime=future;
        for(int i=0;i<tasks.length;i++) {
            ScheduleTask task = tasks[i];
            if(task!=null && task.isValid()) {
                //print.ln(task.getTask());
                //Calendar ne = task.getNextExecution();
                long nextMillis = task.getNextExecution();
                if(nextExecutionTime>nextMillis)nextExecutionTime=nextMillis;
            }
        }
    } 
    
    private boolean setNext(ScheduleTask task, long next) {
        Date end = task.getEndDate();
        if(end!=null) {
            long endDateMillis=timeAtMidnight(ThreadLocalPageContext.getTimeZone(),task.getEndDate());
            if(next>endDateMillis) {
                return false;
            }
        }
        task.setNextExecution(next);
        return true;
    }

    /**
     * @see railo.runtime.schedule.Scheduler#getNextExecutionTime()
     */
    public long getNextExecutionTime() {
        return nextExecutionTime;
    }

    /**
     * @see railo.runtime.schedule.Scheduler#getLogger()
     */
    public LogAndSource getLogger() {
        return log;
    }
    

    private static long timeAtMidnight(TimeZone tz,DateTime date) {
    	return date.getTime()-DateTimeUtil.getInstance().getMilliSecondsInDay(tz,date.getTime());
    }

}