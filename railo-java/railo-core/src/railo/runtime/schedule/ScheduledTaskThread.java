package railo.runtime.schedule;

import java.util.Calendar;
import java.util.TimeZone;

import railo.commons.date.DateTimeUtil;
import railo.commons.date.JREDateTimeUtil;
import railo.commons.io.log.Log;
import railo.commons.io.log.LogAndSource;
import railo.commons.io.log.LogUtil;
import railo.commons.lang.SystemOut;
import railo.runtime.config.Config;
import railo.runtime.engine.CFMLEngineImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.type.dt.DateTimeImpl;

public class ScheduledTaskThread extends Thread {


	private static final long DAY=24*3600000;
	//private Calendar calendar;
	
	private long startDate;
	private long startTime;
	private long endDate;
	private long endTime;
	private int intervall;
	private int amount;
	
	private DateTimeUtil util;

	private int cIntervall;
	
	private Config config;
	private LogAndSource log;
	private ScheduleTask task;
	private String charset;
	private final CFMLEngineImpl engine;
	private TimeZone timeZone;
	private SchedulerImpl scheduler;



	
	public ScheduledTaskThread(CFMLEngineImpl engine,SchedulerImpl scheduler, Config config, LogAndSource log, ScheduleTask task, String charset) {
		util = DateTimeUtil.getInstance();
		this.engine=engine;
		this.scheduler=scheduler;
		this.config=config;
		this.log=log;
		this.task=task;
		this.charset=charset;
		timeZone=ThreadLocalPageContext.getTimeZone(config);
		
		this.startDate=util.getMilliSecondsAdMidnight(timeZone,task.getStartDate().getTime());
		this.startTime=util.getMilliSecondsInDay(timeZone, task.getStartTime().getTime());
		this.endDate=task.getEndDate()==null?Long.MAX_VALUE:util.getMilliSecondsAdMidnight(timeZone,task.getEndDate().getTime());
		this.endTime=task.getEndTime()==null?DAY:util.getMilliSecondsInDay(timeZone, task.getEndTime().getTime());

		
		this.intervall=task.getInterval();
		if(intervall>=10){
			amount=intervall;
			intervall=ScheduleTaskImpl.INTERVAL_EVEREY;
		}
		else amount=1;

		cIntervall = toCalndarIntervall(intervall);
	}


	public void run(){
		try{
		_run();
		}
		finally{
			task.setValid(false);
			try {
				scheduler.removeIfNoLonerValid(task);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
	}
	public void _run(){
		
				
		// check values
		if(startDate>endDate) {
			log(Log.LEVEL_ERROR,"This task can not be executed because the task definition is invalid; enddate is before startdate");
			return;
		}
		if(intervall==ScheduleTaskImpl.INTERVAL_EVEREY && startTime>endTime) {
			log(Log.LEVEL_ERROR,"This task can not be executed because the task definition is invalid; endtime is before starttime");
			return;
		}
		
		
		long today = System.currentTimeMillis();
		long execution ;
		boolean isOnce=intervall==ScheduleTask.INTERVAL_ONCE;
		if(isOnce){
			if(startDate+startTime<today) return;
			execution=startDate+startTime;
		}
		else execution = calculateNextExecution(today,false);
		//long sleep=execution-today;
		
		log(Log.LEVEL_INFO,"first execution runs at "+new DateTimeImpl(execution,false).castToString(timeZone));
		
		
		while(true){
			sleepEL(execution,today);
			
			if(!engine.isRunning()) break;
			
			today=System.currentTimeMillis();
			long todayTime=util.getMilliSecondsInDay(null,today);
			long todayDate=today-todayTime;
			
			if(!task.isValid()) break;
			if(!task.isPaused()){
				if(endDate<todayDate && endTime<todayTime) {
					break;
				}
				execute();
			}
			if(isOnce)break;
			today=System.currentTimeMillis();
			execution=calculateNextExecution(today,true);
			log(Log.LEVEL_INFO,"next execution runs at "+new DateTimeImpl(execution,false).castToString(timeZone)+":"+today+":"+execution);
			//sleep=execution-today;
		}
	}
	
	
	
	
	private void log(int level, String msg) {
		String logName="schedule task:"+task.getTask();
		if(log!=null) log.log(level,logName, msg);
		else SystemOut.print(LogUtil.toStringType(level, "INFO").toUpperCase()+":"+msg);
		
	}


	private void sleepEL(long when, long now) {
		long millis = when-now;
		
		try {
			while(true){
				sleep(millis);
				millis=when-System.currentTimeMillis();
				if(millis<=0)break;
				millis=10;
			}
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void execute() {
		if(config!=null)new ExecutionThread(config,log,task,charset).start();
	}

	private long calculateNextExecution(long now, boolean notNow) {
		long nowTime=util.getMilliSecondsInDay(timeZone,now);
		long nowDate=now-nowTime;
		
		
		// when second or date intervall switch to current date
		if(startDate<nowDate && (cIntervall==Calendar.SECOND || cIntervall==Calendar.DATE))
			startDate=nowDate;
		
		// init calendar
		Calendar calendar = JREDateTimeUtil.getThreadCalendar(timeZone);
		calendar.setTimeInMillis(startDate+startTime);
		
		long time;
		while(true) {
			time=getMilliSecondsInDay(calendar);
			if(now<=calendar.getTimeInMillis() && time>=startTime) {
				// this is used because when cames back sometme to early
				if(notNow && (calendar.getTimeInMillis()-now)<1000);
				else if(intervall==ScheduleTaskImpl.INTERVAL_EVEREY && time>endTime)
					now=nowDate+DAY;
				else 
					break;
			}
			calendar.add(cIntervall, amount);
		}
		return calendar.getTimeInMillis();
	}

	private static int toCalndarIntervall(int intervall) {
		switch(intervall){
		case ScheduleTask.INTERVAL_DAY:return Calendar.DATE;
		case ScheduleTask.INTERVAL_MONTH:return Calendar.MONTH;
		case ScheduleTask.INTERVAL_WEEK:return Calendar.WEEK_OF_YEAR;
		case ScheduleTask.INTERVAL_ONCE:return -1;
		
		}
		return Calendar.SECOND;
	}
	
	public static long getMilliSecondsInDay(Calendar c) {
		return  (c.get(Calendar.HOUR_OF_DAY)*3600000)+
                    (c.get(Calendar.MINUTE)*60000)+
                    (c.get(Calendar.SECOND)*1000)+
                    (c.get(Calendar.MILLISECOND));
        
    }
	

	public Config getConfig() {
		return config;
	}


	public ScheduleTask getTask() {
		return task;
	}
}
