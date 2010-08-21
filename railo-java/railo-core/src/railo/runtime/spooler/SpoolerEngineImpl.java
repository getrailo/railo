package railo.runtime.spooler;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import railo.aprint;
import railo.print;
import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.log.Log;
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.lang.StringUtil;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalConfig;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class SpoolerEngineImpl implements SpoolerEngine {
	
	private String label;
	

	private LinkedList openTasks=new LinkedList();
	private LinkedList closedTasks=new LinkedList();
	private SpoolerThread thread;
	//private ExecutionPlan[] plans;
	private Resource persisDirectory;
	private long count=0;
	private Log log;
	private Config config; 
	private int add=0;
	
	public SpoolerEngineImpl(Config config,Resource persisDirectory,String label, Log log) throws IOException {
		this.config=config;
		this.persisDirectory=persisDirectory;
		this.label=label;
		this.log=log;
		//print.ds(persisDirectory.getAbsolutePath());
		load();
		
	}

	/**
	 * @see railo.runtime.spooler.SpoolerEngine#add(railo.runtime.spooler.SpoolerTask)
	 */
	public void add(SpoolerTask task) {
		openTasks.add(task);
		add++;
		task.setNextExecution(System.currentTimeMillis());
		task.setId(StringUtil.addZeros(++count, 8));
		store(task);
		start();
	}

	private void start() {
		if(thread==null || !thread.isAlive()) {
			thread=new SpoolerThread(this);
			thread.start();
		}
		else if(thread.sleeping) {
			thread.interrupt();
		}
		//else print.out("- existing");
	}

	private void load() throws IOException {
		if(persisDirectory==null) return;
		
		Resource closed = persisDirectory.getRealResource("closed");
		Resource open = persisDirectory.getRealResource("open");

		load(open,openTasks);
		load(closed,closedTasks);
		if(openTasks.size()>0)start();
	}

	/**
	 * @see railo.runtime.spooler.SpoolerEngine#getLabel()
	 */
	public String getLabel() {
		return label;
	}
	
	private void load(Resource dir, LinkedList list) throws IOException {
		
		if(!dir.exists()){
			dir.createDirectory(true);
			return;
		}
		ObjectInputStream ois=null;
		InputStream is=null;
		SpoolerTask task=null;
        Resource[] resTasks = dir.listResources(new ExtensionResourceFilter("tsk"));
	
        for(int i=0;i<resTasks.length;i++) {
        	long name = Caster.toLongValue(StringUtil.replace(resTasks[i].getName(),".tsk","",true),-1);
        	if(count<name)count=name;
			try {
	        	is=resTasks[i].getInputStream();
		        ois = new ObjectInputStream(is);
		        task=(SpoolerTask) ois.readObject();
		        
		        //print.out(dir.getName()+":"+task.subject());
		        list.add(task);
	        } 
	        catch (Throwable t) {
	        	//print.printST(t);
	        	IOUtil.closeEL(is);
	        	IOUtil.closeEL(ois);
	        	resTasks[i].delete();
	        }
	        IOUtil.closeEL(is);
	        IOUtil.closeEL(ois);
		}
	}

	private void store(SpoolerTask task) {
		ObjectOutputStream oos=null;
		Resource persis = getFile(task);
		if(persis.exists()) persis.delete();
        try {
	        oos = new ObjectOutputStream(persis.getOutputStream());
	        oos.writeObject(task);
        } 
        catch (IOException e) {}
        finally {
        	IOUtil.closeEL(oos);
        }
	}
	

	private void unstore(SpoolerTask task) {
		Resource persis = getFile(task);
		if(persis.exists()) persis.delete();   
	}
	private Resource getFile(SpoolerTask task) {
		Resource dir = persisDirectory.getRealResource(task.closed()?"closed":"open");
		dir.mkdirs();
		return dir.getRealResource(task.getId()+".tsk");
	}

	/**
	 * @see railo.runtime.spooler.SpoolerEngine#calculateNextExecution(railo.runtime.spooler.SpoolerTask)
	 */
	public long calculateNextExecution(SpoolerTask task) {
		int _tries=0;
		ExecutionPlan plan=null;
		ExecutionPlan[] plans=task.getPlans();
		
		for(int i=0;i<plans.length;i++) {
			_tries+=plans[i].getTries();
			if(_tries>task.tries()) {
				plan=plans[i];
				break;
			}
		}
		if(plan==null)return -1;
		return task.lastExecution()+(plan.getIntervall()*1000);
	}

	/**
	 * @see railo.runtime.spooler.SpoolerEngine#getOpenTasks()
	 */
	public SpoolerTask[] getOpenTasks() {
		if(openTasks.size()==0) return new SpoolerTask[0];
		return (SpoolerTask[]) openTasks.toArray(new SpoolerTask[openTasks.size()]);
	}
	
	/**
	 * @see railo.runtime.spooler.SpoolerEngine#getClosedTasks()
	 */
	public SpoolerTask[] getClosedTasks() {
		if(closedTasks.size()==0) return new SpoolerTask[0];
		return (SpoolerTask[]) closedTasks.toArray(new SpoolerTask[closedTasks.size()]);
	}

	public static void list(SpoolerTask[] tasks) {
		for(int i=0;i<tasks.length;i++) {
			aprint.out(tasks[i].subject());
			aprint.out("- last exe:"+tasks[i].lastExecution());
			aprint.out("- tries:"+tasks[i].tries());
		}
	}
	
	class SpoolerThread extends Thread {

		private SpoolerEngineImpl engine;
		private boolean sleeping;
		private int maxThreads=10;

		public SpoolerThread(SpoolerEngineImpl engine) {
			this.engine=engine;
			try{
				this.setPriority(MIN_PRIORITY);
			}
			// can throw security exceptions
			catch(Throwable t){}
		}
		
		public void run() {
			SpoolerTask[] tasks;
			SpoolerTask task=null;
			long nextExection;
			ThreadLocalConfig.register(engine.config);
			//ThreadLocalPageContext.register(engine.);
			List<TaskThread> runningTasks=new ArrayList<TaskThread>();
			TaskThread tt;
			int adds;
			while(!engine.openTasks.isEmpty()) {
				adds=engine.adds();
				tasks=engine.getOpenTasks();
				nextExection=Long.MAX_VALUE;
				for(int i=0;i<tasks.length;i++) {
					task=tasks[i];
					if(task.nextExecution()<=System.currentTimeMillis()) {
						tt=new TaskThread(engine,task);
						tt.start();
						runningTasks.add(tt);
					}
					
					nextExection=joinTasks(runningTasks,maxThreads,nextExection);
					
					     
				}
				
				nextExection=joinTasks(runningTasks,0,nextExection);
				if(adds!=engine.adds()) continue;
				
				if(nextExection==Long.MAX_VALUE)break;
				long sleep = nextExection-System.currentTimeMillis();
				
				if(sleep>0)doWait(sleep);
				
				//if(sleep<0)break;
			}
		}

		private long joinTasks(List<TaskThread> runningTasks, int maxThreads,long nextExection) {
			if(runningTasks.size()>=maxThreads){
				Iterator<TaskThread> it = runningTasks.iterator();
				TaskThread tt;
				SpoolerTask task;
				while(it.hasNext()){
					tt = it.next();
					SystemUtil.join(tt);
					task = tt.getTask();

					if(task!=null && task.nextExecution()<nextExection && !task.closed()) {
						nextExection=task.nextExecution();
					}
				}
				runningTasks.clear();
			}
			return nextExection;
		}

		private void doWait(long sleep) {
			//long start=System.currentTimeMillis();
			try {
				sleeping=true;
				synchronized (this) {
					wait(sleep);
				}
				
			} catch (Throwable t) {
				//
			}
			finally {
				sleeping=false;
			}
			//print.out(sleep+":"+(System.currentTimeMillis()-start));
		}
		
	}
	
	
	class TaskThread extends Thread {
		
		private SpoolerEngineImpl engine;
		private SpoolerTask task;

		public TaskThread(SpoolerEngineImpl engine,SpoolerTask task) {
			this.engine=engine;
			this.task=task;
		}
		
		public SpoolerTask getTask() {
			return task;
		}

		public void run() {
			ThreadLocalConfig.register(engine.config);
			engine.execute(task);
			ThreadLocalConfig.release();
			
		}
	}
	

	/**
	 * remove that task from Spooler
	 * @param task
	 */
	public void remove(SpoolerTask task) {
		unstore(task);
		if(!openTasks.remove(task))closedTasks.remove(task);
	}
	

	/* *
	 * @see railo.runtime.spooler.SpoolerEngine#hasAdds()
	 */
	public int adds() {
		//return openTasks.size()>0;
		return add;
	}    
	
	/* *
	 * @see railo.runtime.spooler.SpoolerEngine#resetAdds()
	 * /
	public void resetAdds() {
		add=false;
	}*/

	/**
	 * @see railo.runtime.spooler.SpoolerEngine#remove(java.lang.String)
	 */
	public void remove(String id) {
		SpoolerTask task = getTaskById(getOpenTasks(),id);
		if(task==null)task=getTaskById(getClosedTasks(),id);
		if(task!=null)remove(task);
	}

	private SpoolerTask getTaskById(SpoolerTask[] tasks, String id) {
		for(int i=0;i<tasks.length;i++) {
			if(tasks[i].getId().equals(id)) {
				return tasks[i];
			}
		}
		return null;
	}

	/**
	 * execute task by id and return eror throwd by task
	 * @param id
	 * @throws SpoolerException
	 */
	public PageException execute(String id) {
		SpoolerTask task = getTaskById(getOpenTasks(),id);
		if(task==null)task=getTaskById(getClosedTasks(),id);
		if(task!=null){
			return execute(task);
		}
		return null;
	}
	
	public PageException execute(SpoolerTask task) {
		//task.closed();
		try {
			((SpoolerTaskSupport)task)._execute(config);
			if(task.closed())closedTasks.remove(task);
			else openTasks.remove(task);
			
			unstore(task);
			log.info("remote-client", task.subject());
			task.setLastExecution(System.currentTimeMillis());
			task.setNextExecution(-1);
			
			task.setClosed(true);
			task=null;
		} 
		catch(Throwable t) {
			task.setLastExecution(System.currentTimeMillis());
			task.setNextExecution(calculateNextExecution(task));
			log.error("remote-client", task.subject()+":"+t.getMessage());
			if(task.nextExecution()==-1) {
				openTasks.remove(task);
				if(!closedTasks.contains(task))closedTasks.add(task);
				unstore(task);
				task.setClosed(true);
				store(task);
				task=null;
			}
			else 
				store(task);
			
			return Caster.toPageException(t);
		}
		return null;
	}

	/**
	 * @see railo.runtime.spooler.SpoolerEngine#setLabel(java.lang.String)
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @see railo.runtime.spooler.SpoolerEngine#setPersisDirectory(railo.commons.io.res.Resource)
	 */
	public void setPersisDirectory(Resource persisDirectory) {
		this.persisDirectory = persisDirectory;
	}

	/**
	 * @see railo.runtime.spooler.SpoolerEngine#setLog(railo.commons.io.log.Log)
	 */
	public void setLog(Log log) {
		this.log = log;
	}

	/**
	 * @see railo.runtime.spooler.SpoolerEngine#setConfig(railo.runtime.config.Config)
	 */
	public void setConfig(Config config) {
		this.config = config;
	}
}
