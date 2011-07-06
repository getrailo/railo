package railo.runtime.debug;

import railo.runtime.PageSource;
import railo.runtime.exp.DeprecatedException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;


/**
 * a single debug entry
 */
public final class DebugEntryImpl implements DebugEntry {
	
	private static final long serialVersionUID = 809949164432900481L;
	
	private String path;
	//private long start;
	private int fileLoadTime;
	private int exeTime;
	
	//boolean isRunning;
    private String key;
    private int count=1;
    private int queryTime;
    private static int _id=1;
    private String id;
    
    private int max;
    private int min=0;

	/**
	 * constructor of the class
	 * @param source 
	 * @param key 
	 */
    protected DebugEntryImpl(PageSource source, String key) {
		this.path=source==null?"":source.getDisplayPath();
		this.key=key;
		id=Caster.toString(++_id);
	}
	
	
	/* *
     * @see railo.runtime.debug.DebugEntry#start()
     * /
	public void start() {
		isRunning=true;
		start=System.currentTimeMillis();
	}*/
	
	/* *
     * @see railo.runtime.debug.DebugEntry#stop()
     * /
	public int stop() {
		if(isRunning) {
			int time=(int)(System.currentTimeMillis()-start);
			isRunning=false;
			return time;
			
		}
		return 0;
	}*/
	
	/* *
     * @see railo.runtime.debug.DebugEntry#time()
     * /
	public int time() {
		if(isRunning)return (int)(System.currentTimeMillis()-start);
		return 0;
	}*/
	
	/* *
     * @see railo.runtime.debug.DebugEntry#reset()
     * /
	public void reset() {
		start=0;
		isRunning=false;
	}*/
	
	/**
     * @see railo.runtime.debug.DebugEntry#getExeTime()
     */
	public int getExeTime() {
		return positiv(exeTime);
	}
	/**
     * @see railo.runtime.debug.DebugEntry#updateExeTime(int)
     */
	public void updateExeTime(int exeTime) {
		if(exeTime>=0) {
            if(count==1 || min>exeTime)min=exeTime;
            if(max<exeTime)max=exeTime;
            
            this.exeTime += exeTime;
        }
	}
	/**
     * @see railo.runtime.debug.DebugEntry#getFileLoadTime()
     */
	public int getFileLoadTime() {
        return positiv(fileLoadTime);
	}
	
    private int positiv(int time) {
        if(time<0)return 0;
        return time;
    }


    /**
     * @see railo.runtime.debug.DebugEntry#updateFileLoadTime(int)
     */
	public void updateFileLoadTime(int fileLoadTime) {
		if(fileLoadTime>0)this.fileLoadTime+= fileLoadTime;
	}
    /**
     * @see railo.runtime.debug.DebugEntry#updateQueryTime(int)
     */
    public void updateQueryTime(int queryTime) {
        if(queryTime>0)this.queryTime+=queryTime;
    }
    /**
     * @see railo.runtime.debug.DebugEntry#getSrc()
     */
    public String getSrc() {
        return getSrc(path,key);//source.getDisplayPath()+(key==null?"":"$"+key);
    }
    

    /**
     * @see railo.runtime.debug.DebugEntry#getPath()
     */
    public String getPath() {
        return path;
    }
    
    /**
     * @param source 
     * @param key 
     * @return Returns the src.
     */
    public static String getSrc(String path, String key) {
        return 
        	path
            +
            (key==null?"":"$"+key);
    }
    
    /**
     * increment the inner counter
     */
    protected void countPP() {
        count++;
        
    }
    /**
     * @see railo.runtime.debug.DebugEntry#getCount()
     */
    public int getCount() {
        return count;
    }
    /**
     * @see railo.runtime.debug.DebugEntry#getQueryTime()
     */
    public int getQueryTime() {
        return positiv(queryTime);
    }
    /**
     * @see railo.runtime.debug.DebugEntry#getMax()
     */
    public int getMax() {
        return positiv(max);
    }
    /**
     * @see railo.runtime.debug.DebugEntry#getMin()
     */
    public int getMin() {
        return positiv(min);
    }
    /**
     * @see railo.runtime.debug.DebugEntry#resetQueryTime()
     */
    public void resetQueryTime() {
        this.queryTime=0;
    }


    public PageSource getPageSource() {
        throw new PageRuntimeException(new DeprecatedException("no longer supported"));
    	//return source;
    }


	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
}