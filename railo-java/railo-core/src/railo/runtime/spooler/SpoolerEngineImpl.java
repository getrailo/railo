package railo.runtime.spooler;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.log.Log;
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.ResourceNameFilter;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalConfig;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.KeyConstants;

public class SpoolerEngineImpl implements SpoolerEngine {
	
	private static final TaskFileFilter FILTER=new TaskFileFilter();

	private static final Collection.Key LAST_EXECUTION = KeyImpl.intern("lastExecution");
	private static final Collection.Key NEXT_EXECUTION = KeyImpl.intern("nextExecution");
	
	private static final Collection.Key CLOSED = KeyImpl.intern("closed");
	private static final Collection.Key TRIES = KeyImpl.intern("tries");
	private static final Collection.Key TRIES_MAX = KeyImpl.intern("triesmax");

	
	private String label;
	

	//private LinkedList<SpoolerTask> openTaskss=new LinkedList<SpoolerTask>();
	//private LinkedList<SpoolerTask> closedTasks=new LinkedList<SpoolerTask>();
	private SpoolerThread thread;
	//private ExecutionPlan[] plans;
	private Resource persisDirectory;
	private long count=0;
	private Log log;
	private Config config; 
	private int add=0;


	private Resource closedDirectory;
	private Resource openDirectory;

	private int maxThreads;
	
	public SpoolerEngineImpl(Config config,Resource persisDirectory,String label, Log log, int maxThreads) {
		this.config=config;
		this.persisDirectory=persisDirectory;

		closedDirectory = persisDirectory.getRealResource("closed");
		openDirectory = persisDirectory.getRealResource("open");
		//calculateSize();
		

		this.maxThreads=maxThreads;
		this.label=label;
		this.log=log;
		//print.ds(persisDirectory.getAbsolutePath());
		//load();
		if(getOpenTaskCount()>0)start();
	}

	/*private void calculateSize() {
		closedCount=calculateSize(closedDirectory);
		openCount=calculateSize(openDirectory);
	}*/

	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	/**
	 * @return the maxThreads
	 */
	public int getMaxThreads() {
		return maxThreads;
	}

	private int calculateSize(Resource res) {
		return ResourceUtil.directrySize(res,FILTER);
	}

	@Override
	public synchronized void add(SpoolerTask task) {
		//openTasks.add(task);
		add++;
		task.setNextExecution(System.currentTimeMillis());
		task.setId(createId(task));
		store(task);
		start();
	}


	private void start() {
		if(thread==null || !thread.isAlive()) {
			thread=new SpoolerThread(this);
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		}
		else if(thread.sleeping) {
			thread.interrupt();
		}
		//else print.out("- existing");
	}

	@Override
	public String getLabel() {
		return label;
	}
	
	

	private SpoolerTask getTaskById(Resource dir,String id) {
		return getTask(dir.getRealResource(id+".tsk"),null);
	}
	
	private SpoolerTask getTaskByName(Resource dir,String name) {
		return getTask(dir.getRealResource(name),null);
	}

	private SpoolerTask getTask(Resource res, SpoolerTask defaultValue) {
		InputStream is = null;
        ObjectInputStream ois = null;
        
        SpoolerTask task=defaultValue;
		try {
			is = res.getInputStream();
	        ois = new ObjectInputStream(is);
	        task = (SpoolerTask) ois.readObject();
        } 
        catch (Throwable t) {//t.printStackTrace();
        	IOUtil.closeEL(is);
        	IOUtil.closeEL(ois);
        	res.delete();
        }
        IOUtil.closeEL(is);
        IOUtil.closeEL(ois);
		return task;
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
		boolean exists=persis.exists();
		if(exists) persis.delete(); 
	}
	private Resource getFile(SpoolerTask task) {
		Resource dir = persisDirectory.getRealResource(task.closed()?"closed":"open");
		dir.mkdirs();
		return dir.getRealResource(task.getId()+".tsk");
	}
	
	private String createId(SpoolerTask task) {
		Resource dir = persisDirectory.getRealResource(task.closed()?"closed":"open");
		dir.mkdirs();
		
		String id=null;
		do{
			id=StringUtil.addZeros(++count, 8);
		}while(dir.getRealResource(id+".tsk").exists());
		return id;
	}

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
	
	public Query getOpenTasksAsQuery(int startrow, int maxrow) throws PageException {
		return getTasksAsQuery(createQuery(),openDirectory,startrow, maxrow);
	}

	public Query getClosedTasksAsQuery(int startrow, int maxrow) throws PageException {
		return getTasksAsQuery(createQuery(),closedDirectory,startrow, maxrow);
	}

	public Query getAllTasksAsQuery(int startrow, int maxrow) throws PageException {
		Query query = createQuery();
		//print.o(startrow+":"+maxrow);
		getTasksAsQuery(query,openDirectory,startrow, maxrow);
		int records = query.getRecordcount();
		if(maxrow<0) maxrow=Integer.MAX_VALUE;
		// no open tasks
		if(records==0) {
			startrow-=getOpenTaskCount();
			if(startrow<1) startrow=1;
		}
		else {
			startrow=1;
			maxrow-=records;
		}
		if(maxrow>0)getTasksAsQuery(query,closedDirectory,startrow, maxrow);
		return query;
	}
	
	public int getOpenTaskCount() {
		return calculateSize(openDirectory);
	}
	
	public int getClosedTaskCount() {
		return calculateSize(closedDirectory);
	}
	
	
	private Query getTasksAsQuery(Query qry,Resource dir, int startrow, int maxrow) {
		String[] children = dir.list(FILTER);
		if(ArrayUtil.isEmpty(children)) return qry;
		if(children.length<maxrow)maxrow=children.length;
		SpoolerTask task;
		
		int to=startrow+maxrow;
		if(to>children.length)to=children.length;
		if(startrow<1)startrow=1;
		
		for(int i=startrow-1;i<to;i++){
			task = getTaskByName(dir, children[i]);
			if(task!=null)addQueryRow(qry, task);
		}
		
		return qry;
	}
	
	private Query createQuery() throws DatabaseException {
		String v="VARCHAR";
		String d="DATE";
		railo.runtime.type.Query qry=new QueryImpl(
				new String[]{"type","name","detail","id","lastExecution","nextExecution","closed","tries","exceptions","triesmax"},
				new String[]{v,v,"object",v,d,d,"boolean","int","object","int"},
				0,"query");
		return qry;
	}
	
	private void addQueryRow(railo.runtime.type.Query qry, SpoolerTask task) {
    	int row = qry.addRow();
		try{
			qry.setAt(KeyConstants._type, row, task.getType());
			qry.setAt(KeyConstants._name, row, task.subject());
			qry.setAt(KeyConstants._detail, row, task.detail());
			qry.setAt(KeyConstants._id, row, task.getId());

			
			qry.setAt(LAST_EXECUTION, row,new DateTimeImpl(task.lastExecution(),true));
			qry.setAt(NEXT_EXECUTION, row,new DateTimeImpl(task.nextExecution(),true));
			qry.setAt(CLOSED, row,Caster.toBoolean(task.closed()));
			qry.setAt(TRIES, row,Caster.toDouble(task.tries()));
			qry.setAt(TRIES_MAX, row,Caster.toDouble(task.tries()));
			qry.setAt(KeyConstants._exceptions, row,translateTime(task.getExceptions()));
			
			int triesMax=0;
			ExecutionPlan[] plans = task.getPlans();
			for(int y=0;y<plans.length;y++) {
				triesMax+=plans[y].getTries();
			}
			qry.setAt(TRIES_MAX, row,Caster.toDouble(triesMax));
		}
		catch(Throwable t){}
	}
	
	private Array translateTime(Array exp) {
		exp=(Array) Duplicator.duplicate(exp,true);
		Iterator<Object> it = exp.valueIterator();
		Struct sct;
		while(it.hasNext()) {
			sct=(Struct) it.next();
			sct.setEL(KeyConstants._time,new DateTimeImpl(Caster.toLongValue(sct.get(KeyConstants._time,null),0),true));
		}
		return exp;
	}

	class SpoolerThread extends Thread {

		private SpoolerEngineImpl engine;
		private boolean sleeping;
		private final int maxThreads;

		public SpoolerThread(SpoolerEngineImpl engine) {
			this.maxThreads=engine.getMaxThreads();
			this.engine=engine;
			try{
				this.setPriority(MIN_PRIORITY);
			}
			// can throw security exceptions
			catch(Throwable t){}
		}
		
		public void run() {
			String[] taskNames;
			//SpoolerTask[] tasks;
			SpoolerTask task=null;
			long nextExection;
			ThreadLocalConfig.register(engine.config);
			//ThreadLocalPageContext.register(engine.);
			List<TaskThread> runningTasks=new ArrayList<TaskThread>();
			TaskThread tt;
			int adds;
			
			while(getOpenTaskCount()>0) {
				adds=engine.adds();
				taskNames = openDirectory.list(FILTER);
				//tasks=engine.getOpenTasks();
				nextExection=Long.MAX_VALUE;
				for(int i=0;i<taskNames.length;i++) {
					task=getTaskByName(openDirectory, taskNames[i]);
					if(task==null) continue;
					
					if(task.nextExecution()<=System.currentTimeMillis()) {
						//print.o("- execute");
						tt=new TaskThread(engine,task);
						tt.start();
						runningTasks.add(tt);
					}
					else if(task.nextExecution()<nextExection && 
							nextExection!=-1 && 
							!task.closed()) 
						nextExection=task.nextExecution();
					nextExection=joinTasks(runningTasks,maxThreads,nextExection);
				}
				
				nextExection=joinTasks(runningTasks,0,nextExection);
				if(adds!=engine.adds()) continue;
				
				if(nextExection==Long.MAX_VALUE)break;
				long sleep = nextExection-System.currentTimeMillis();
				
				//print.o("sleep:"+sleep+">"+(sleep/1000));
				if(sleep>0)doWait(sleep);
				
				//if(sleep<0)break;
			}
			//print.o("end:"+getOpenTaskCount());
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

					if(task!=null && task.nextExecution()!=-1 && task.nextExecution()<nextExection && !task.closed()) {
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
		//if(!openTasks.remove(task))closedTasks.remove(task);
	}
	
	public void removeAll() {
		ResourceUtil.removeChildrenEL(openDirectory);
		ResourceUtil.removeChildrenEL(closedDirectory);
		SystemUtil.sleep(100);
		ResourceUtil.removeChildrenEL(openDirectory);
		ResourceUtil.removeChildrenEL(closedDirectory);
	}
	
	public int adds() {
		//return openTasks.size()>0;
		return add;
	}    

	@Override
	public void remove(String id) {
		SpoolerTask task = getTaskById(openDirectory,id);
		if(task==null)task=getTaskById(closedDirectory,id);
		if(task!=null)remove(task);
	}

	/*private SpoolerTask getTaskById(SpoolerTask[] tasks, String id) {
		for(int i=0;i<tasks.length;i++) {
			if(tasks[i].getId().equals(id)) {
				return tasks[i];
			}
		}
		return null;
	}*/

	/**
	 * execute task by id and return eror throwd by task
	 * @param id
	 * @throws SpoolerException
	 */
	public PageException execute(String id) {
		SpoolerTask task = getTaskById(openDirectory,id);
		if(task==null)task=getTaskById(closedDirectory,id);
		if(task!=null){
			return execute(task);
		}
		return null;
	}
	
	public PageException execute(SpoolerTask task) {
		//task.closed();
		try {
			if(task instanceof SpoolerTaskSupport)  // FUTURE this is bullshit, call the execute method directly, but you have to rewrite them for that
				((SpoolerTaskSupport)task)._execute(config);
			else 
				task.execute(config);
			
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
				//openTasks.remove(task);
				//if(!closedTasks.contains(task))closedTasks.add(task);
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

	public void setLabel(String label) {
		this.label = label;
	}

	public void setPersisDirectory(Resource persisDirectory) {
		this.persisDirectory = persisDirectory;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
	
}
	
class TaskFileFilter implements ResourceNameFilter {

	public boolean accept(Resource parent, String name) {
		return name!=null && name.endsWith(".tsk");
	}
	
}
